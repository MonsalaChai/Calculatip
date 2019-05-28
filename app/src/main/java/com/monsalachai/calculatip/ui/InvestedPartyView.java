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

import com.monsalachai.calculatip.R;

public class InvestedPartyView extends ConstraintLayout {

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

        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final EditText editText = (EditText) findViewById(R.id.percent_other_entry);
                if (checkedId == R.id.percent_other) {
                    // show the edit text.
                    editText.setVisibility(View.VISIBLE);

                }
                else {
                    // hide the edit text.
                    editText.setVisibility(View.GONE);
                    // call listener callback.
                    if (listener != null)
                        listener.onTipChanged((checkedId == R.id.percent_fifteen) ? 0.15f : (checkedId == R.id.percent_eighteen) ? 0.18f : 0.2f);
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
                        if (listener != null) {
                            try {
                                listener.onTipChanged(Float.parseFloat(s.toString()) / 100);
                            } catch (NumberFormatException e) {
                                listener.onTipChanged(0f);
                            }
                        }
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

    protected int getDefaultRadioSelection() {
        return R.id.percent_fifteen;
    }
}
