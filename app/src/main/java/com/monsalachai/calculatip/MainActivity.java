package com.monsalachai.calculatip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
