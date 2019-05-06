package com.monsalachai.calculatip.ui;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.monsalachai.calculatip.R;

public class InvestedPartyView extends ConstraintLayout {
    
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

    private void init() {
        // Inflate the base view.
        inflate(getContext(), R.layout.view_invested_party, this);

        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                EditText editText = (EditText) findViewById(R.id.percent_other_entry);
                if (checkedId == R.id.percent_other) {
                    // show the edit text.
                    editText.setVisibility(View.VISIBLE);

                    // Clear the report fields.
                    // Wait for a state change in editText to show update report fields.
                }
                else {
                    // hide the edit text.
                    editText.setVisibility(View.GONE);

                    // Calculate the report fields and update them.
                }
            }
        });
        // Set the default clicked button
        ((RadioButton) radioGroup.findViewById(getDefaultRadioSelection())).setChecked(true);

    }

    protected int getDefaultRadioSelection() {
        return R.id.percent_fifteen;
    }
}
