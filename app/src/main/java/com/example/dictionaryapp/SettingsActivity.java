package com.example.dictionaryapp;

import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity{
    DataBaseHelper myDbHelper;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Settings");

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

            TextView clearHistory = (TextView) findViewById(R.id.clear_history);
            clearHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDbHelper = new DataBaseHelper(SettingsActivity.this);
                    try {
                        myDbHelper.openDataBase();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                    showAlertDialog();
                }
            });
        }

        private void showAlertDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this,R.style.MyDialogTheme);
            builder.setTitle("Are you sure?");
            builder.setMessage("ALL THE HISTORY WILL BE DELETED");

            String positiveText = "Yes";
            builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    myDbHelper.deleteHistory();
                }
            });

            String negativeText = "No";
            builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == android.R.id.home){
                onBackPressed();
            }
            return super.onOptionsItemSelected(item);
        }

    }
