package com.monsalachai.calculatip;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.monsalachai.calculatip.model.Database;
import com.monsalachai.calculatip.model.entities.Participant;
import com.monsalachai.calculatip.ui.ParticipantAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Database database;

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

        // create database handle.
        database = Room.databaseBuilder(this, Database.class, "main_db")
                .allowMainThreadQueries()   // Not likely to make any queries that would lag UI thread.
                .build();

        // load data from persistence (e.g. we're resuming).
        List<Participant> participants = database.getStoredParticipants();

        // create participant adapter.
        final ParticipantAdapter adapter = new ParticipantAdapter(participants);
        adapter.setEventListener(new ParticipantAdapter.OnDataStateChangeListener() {
            @Override
            public void onParticipantUpdate(Participant participant) {
                database.participantDao().update(participant);
            }
        });

        // locate the RecyclerView and assign its adapter.
        ((RecyclerView)findViewById(R.id.recycler)).setAdapter(adapter);

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

                                adapter.addParticipant(participant);
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
}
