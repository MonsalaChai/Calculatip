package com.monsalachai.calculatip.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.monsalachai.calculatip.R;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class InvestedPartyView extends ConstraintLayout {
    private float    tipValue;
    private EditText nameEntry;
    private EditText portionEntry;
    private TextView tipView;
    private TextView totalView;
    private TextView roundedTipView;
    private TextView roundedTotalView;
    private TextView roundedPercentageView;

    public interface OnInvestedPartyStateChangeListener {
        void onTipChanged(float tip);
        void onNameChanged(String name);
        void onPortionChanged(float portion);
    }

    private OnInvestedPartyStateChangeListener listener;
    
    public InvestedPartyView(Context context) {
        super(context);
        init();
    }

    public InvestedPartyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InvestedPartyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setEventListener(OnInvestedPartyStateChangeListener listener) {
        this.listener = listener;
    }

    private void init() {
        listener = null;
        // Inflate the base view.
        inflate(getContext(), R.layout.view_invested_party, this);

        // assign view members.
        nameEntry = findViewById(R.id.name_entry);
        portionEntry = findViewById(R.id.portion);
        tipView = findViewById(R.id.tip_report_layout).findViewById(R.id.value);
        totalView = findViewById(R.id.total_report_layout).findViewById(R.id.value);
        roundedTipView = findViewById(R.id.rounded_tip_report_layout).findViewById(R.id.value);
        roundedTotalView = findViewById(R.id.rounded_total_report_layout).findViewById(R.id.value);
        roundedPercentageView = findViewById(R.id.rounded_percent_report_layout).findViewById(R.id.value);

        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final EditText editText = (EditText) findViewById(R.id.percent_other_entry);
                if (checkedId == R.id.percent_other) {
                    // show the edit text.
                    editText.setVisibility(View.VISIBLE);
                    // set tip value to the current value in the editText.
                    try {
                        tipValue = Float.parseFloat(editText.getText().toString());
                    }
                    catch (NumberFormatException e) {
                        tipValue = 0;
                    }
                    updateFields();

                }
                else {
                    // hide the edit text.
                    editText.setVisibility(View.GONE);
                    // get the new tip value.
                    tipValue = (checkedId == R.id.percent_fifteen) ? 0.15f : (checkedId == R.id.percent_eighteen) ? 0.18f : 0.2f;
                    // call listener callback.
                    if (listener != null)
                        listener.onTipChanged(tipValue);
                    updateFields();
                }

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("mesalu", "other text edit value changed.");
                        try {
                            tipValue = Float.parseFloat(s.toString()) / 100;

                        } catch (NumberFormatException e) {
                            tipValue = 0;
                        }

                        if (listener != null)
                            listener.onTipChanged(tipValue);

                        updateFields();
                    }
                });
            }
        });

        EditText portionEntry = findViewById(R.id.portion);
        portionEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null)
                    listener.onPortionChanged(Float.parseFloat(s.toString()));
                else Log.d("mesalu", "No listener bound for that action");
                updateFields();
            }
        });

        EditText nameEntry = findViewById(R.id.name_entry);
        nameEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null)
                    listener.onNameChanged(s.toString());
            }
        });

        // Set the default clicked button
        ((RadioButton) radioGroup.findViewById(getDefaultRadioSelection())).setChecked(true);

    }

    protected void updateFields() {
        // get field contents
        float portion;
        float tip = tipValue;
        try {
            portion = Float.parseFloat(portionEntry.getText().toString());
        }
        catch (NumberFormatException e) {
            portion = 0;
        }
        // pass on to other override.
        updateFields(portion, tip);
    }

    protected void updateFields(float portion, float tip_percent) {
        float tip = portion * tip_percent;
        float total = portion + tip;
        float rounded_total = (float)Math.ceil(total);
        float rounded_tip = rounded_total - portion;


        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMaximumFractionDigits(2);

        // apply to the report views.
        tipView.setText(formatter.format(tip));
        totalView.setText(formatter.format(total));
        roundedTotalView.setText(formatter.format(rounded_total));
        roundedTipView.setText(formatter.format(rounded_tip));

        if (portion != 0 && rounded_tip != 0) {
            float rounded_percentage = rounded_tip / portion;
            roundedPercentageView.setText(String.format(Locale.getDefault(), "%.2f%%", rounded_percentage * 100));
        }
        else
            roundedPercentageView.setText("0.0%");
    }

    protected int getDefaultRadioSelection() {
        return R.id.percent_fifteen;
    }
}
