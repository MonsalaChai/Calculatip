package com.monsalachai.calculatip;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.monsalachai.calculatip.model.Database;
import com.monsalachai.calculatip.model.entities.BasicInfo;
import com.monsalachai.calculatip.model.entities.Participant;
import com.monsalachai.calculatip.ui.InvestedPartyView;
import com.monsalachai.calculatip.ui.ParticipantAdapter;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Database database;
    ParticipantAdapter participantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // The total view at the top of the main view uses view_invested_party to
        // keep things consistent, but the 'name' aspect of that view isn't relevant to
        // the total report, so hide it.
        findViewById(R.id.total_view).findViewById(R.id.name_entry).setVisibility(View.GONE);

        // because the name entry is gone, we'll want to change the start constraint of that
        // portion entry to parent, rather than name_entry.
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone( (ConstraintLayout) findViewById(R.id.total_view).findViewById(R.id.ipv));
        constraintSet.connect(R.id.portion, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.applyTo((ConstraintLayout) findViewById(R.id.total_view).findViewById(R.id.ipv));

        // create database handle.
        database = Room.databaseBuilder(this, Database.class, "main_db")
                .allowMainThreadQueries()   // Not likely to make any queries that would lag UI thread.
                .build();

        // Load basic info back from persistence.
        final BasicInfo basicInfo = database.getStoredData();

        // Set up callbacks (notably in total_view) to keep basicInfo updated.
        ((InvestedPartyView) findViewById(R.id.total_view).findViewById(R.id.ipv)).setEventListener(new InvestedPartyView.OnInvestedPartyStateChangeListener() {
            @Override
            public void onTipChanged(float tip) {
                basicInfo.setStoredTip(tip);
                database.basicInfoDao().update(basicInfo);
            }

            @Override
            public void onNameChanged(String name) {
            // don't care.
            }

            @Override
            public void onPortionChanged(float portion) {
                basicInfo.setStoredPortion(portion);
                database.basicInfoDao().update(basicInfo);
            }
        });

        ((EditText) findViewById(R.id.reduced_portion_entry)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float value = 0;
                try {
                    value = Float.parseFloat(s.toString());
                }
                catch (NumberFormatException e) {}

                basicInfo.setStoredPortion(value);
                database.basicInfoDao().update(basicInfo);
            }
        });

        // now that callbacks are set up, restore values to
        // the controls (if necessary)
        if (basicInfo.getStoredPortion() != 0) {
            ((EditText) findViewById(R.id.reduced_portion_entry))
                    .setText(String.format(Locale.getDefault(), "%.2f", basicInfo.getStoredPortion()));
            ((EditText) findViewById(R.id.total_view).findViewById(R.id.ipv).findViewById(R.id.portion))
                    .setText(String.format(Locale.getDefault(), "%.2f", basicInfo.getStoredPortion()));
        }

        // now restore tip percentage in total_view's IPV.
        int radioId = (basicInfo.getStoredTip() == 0.15f) ? R.id.percent_fifteen :
                (basicInfo.getStoredTip() == 0.18f) ? R.id.percent_eighteen :
                        (basicInfo.getStoredTip() == 0.2f) ? R.id.percent_twenty : R.id.percent_other;

        ((RadioButton) findViewById(R.id.total_view).findViewById(radioId)).setChecked(true);

        if (radioId == R.id.percent_other) {
            // copy value as string into the percent_other_entry view.
            ((EditText) findViewById(R.id.total_view).findViewById(R.id.percent_other_entry))
                    .setText(String.format(Locale.getDefault(), "%.2f", basicInfo.getStoredTip()));
        }

        // Load particpants from persistence.
        List<Participant> participants = database.getStoredParticipants();

        // create participant adapter.
        participantAdapter = new ParticipantAdapter(participants);
        participantAdapter.setEventListener(new ParticipantAdapter.OnDataStateChangeListener() {
            @Override
            public void onParticipantUpdate(Participant participant) {
                database.participantDao().update(participant);
                float totalParty = participantAdapter.getTotalContributions();
                TextView totalRemainingView = findViewById(R.id.total_view).findViewById(R.id.remaining_value);
                float totalRemaining = basicInfo.getStoredPortion() - totalParty;

                NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
                formatter.setRoundingMode(RoundingMode.HALF_UP);
                formatter.setMaximumFractionDigits(2);

                totalRemainingView.setText(formatter.format(totalRemaining));
            }
        });

        // Determine which mode total_view should be in.
        handleTotalViewVisibility();

        // locate the RecyclerView and assign its adapter.
        ((RecyclerView)findViewById(R.id.recycler)).setAdapter(participantAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch a dialog that prompts the user to enter the new participants
                // name and portion.
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final View dialogView = view.inflate(MainActivity.this, R.layout.dialog_new_participant, null);

                builder.setMessage("New Participant")
                        .setView(dialogView)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Create the new Participant object, send it to Adapter's internal
                                // list, and put it in persistence.
                                Participant participant = new Participant();

                                participant.setName(((EditText)dialogView.findViewById(R.id.name_entry)).getText().toString());

                                try {
                                    participant.setPortion(Float.parseFloat(((EditText) dialogView.findViewById(R.id.portion_entry)).getText().toString()));
                                }
                                catch (NumberFormatException e) {
                                    participant.setPortion(0);
                                }

                                participantAdapter.addParticipant(participant);
                                participant.setUid(database.participantDao().insert(participant));
                            }
                        })
                        .setNegativeButton("Cancel", null);

                builder.create().show();

                if (findViewById(R.id.total_view).findViewById(R.id.ipv).getVisibility() == View.VISIBLE) {
                    // just added something, so hide the IPV and swap to the reduced view.
                    findViewById(R.id.total_view).findViewById(R.id.ipv).setVisibility(View.GONE);
                    findViewById(R.id.total_view).findViewById(R.id.reduced_layout).setVisibility(View.VISIBLE);

                    // transfer the primary portion value.
                    String value = ((EditText) findViewById(R.id.total_view).findViewById(R.id.ipv).findViewById(R.id.portion)).getText().toString();
                    ((EditText) findViewById(R.id.total_view).findViewById(R.id.reduced_portion_entry)).setText(value);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_clear_all:
                if (database != null) {
                    for (Participant p : participantAdapter.getDataSet())
                        database.participantDao().remove(p);
                }

                else {
                    Log.e("MainAcitivity", "No reference to database object!?");
                }

                participantAdapter.clear();
                return true;

            case R.id.action_settings_menu:
                // Open settings menu, where people can:
                // 1. Set app color
                // 2. Set default % to start at
                // 3. Other ideas?
                showSettingsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    void showSettingsMenu()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View DialogView = getLayoutInflater().inflate(R.layout.dialog_menu_settings, null);
        builder.setView(DialogView).setTitle("Settings Menu")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // save any settings set
                        Log.d("Settings Menu", "Need to save settings");
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.create().show();

    }

    private void handleTotalViewVisibility() {
        if (participantAdapter == null) return; // can't yet determine visibility.

        InvestedPartyView ipv = (InvestedPartyView) findViewById(R.id.total_view).findViewById(R.id.ipv);
        ConstraintLayout reducedView = findViewById(R.id.total_view).findViewById(R.id.reduced_layout);

        if (participantAdapter.getItemCount() > 0) {
            // ensure reduced view is visible, and ipv is not.
            if (reducedView.getVisibility() != View.VISIBLE)
                reducedView.setVisibility(View.VISIBLE);
            if (ipv.getVisibility() != View.GONE)
                ipv.setVisibility(View.GONE);
        }

        else {
            // ensure ipv is visible, and reduced view is not.
            if (reducedView.getVisibility() != View.GONE)
                reducedView.setVisibility(View.GONE);
            if (ipv.getVisibility() != View.VISIBLE)
                ipv.setVisibility(View.VISIBLE);
        }
    }
}
