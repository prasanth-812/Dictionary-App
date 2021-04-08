package com.example.dictionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity {

    SearchView search;
    private Toolbar toolbar;
    static DataBaseHelper myDbHelper;
    static boolean databaseOpened=false;
    SimpleCursorAdapter suggestionAdapter;
    ArrayList<History> historyList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter historyAdapter;
    RelativeLayout emptyHistory;
    Cursor cursorHistory;
    Boolean doubleBackToExitPressedOnce = false;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = (SearchView) findViewById(R.id.search_view);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search.setIconified(false);
            }
        });

        myDbHelper = new DataBaseHelper(this);
        if (myDbHelper.checkDataBase()){
            openDatabase();
        }
        else {
            LoadDatabaseAsync task = new LoadDatabaseAsync(HomeActivity.this);
            task.execute();
        }

        final String[] from = new String[] {"en_word"};
        final int[] to = new int[] {R.id.suggestion_text};

        suggestionAdapter = new SimpleCursorAdapter(HomeActivity.this,R.layout.suggestion_row, null, from, to, 0){
            @Override
            public void changeCursor(Cursor cursor) {
                super.changeCursor(cursor);
            }
        };
        search.setSuggestionsAdapter(suggestionAdapter);

        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter ca = search.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                String clicked_word = cursor.getString(cursor.getColumnIndex("en_word"));
                search.setQuery(clicked_word,false);
                search.clearFocus();
                search.setFocusable(false);
                Intent intent = new Intent(HomeActivity.this, WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("en_word",clicked_word);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text = search.getQuery().toString();
                Pattern p = Pattern.compile("[A-Za-z \\-.]{1,25}");
                Matcher m =p.matcher(text);
                if (m.matches()) {
                    Cursor c = myDbHelper.getMeaning(text);

                    if (c.getCount() == 0) {
                        showAlertDialog();
                    }
                    else {
                        search.clearFocus();
                        search.setFocusable(false);
                        Intent intent = new Intent(HomeActivity.this, WordMeaningActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("en_word", text);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                }
                else {
                    showAlertDialog();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search.setIconifiedByDefault(false);
                Pattern p = Pattern.compile("[A-Za-z \\-.]{1,25}");
                Matcher m =p.matcher(newText);
                if (m.matches()){
                    Cursor cursorSuggestion = myDbHelper.getSuggestions(newText);
                    suggestionAdapter.changeCursor(cursorSuggestion);
                }
                return false;
            }
        });

        emptyHistory = (RelativeLayout) findViewById(R.id.empty_history);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_history);
        layoutManager = new LinearLayoutManager(HomeActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        fetch_history();

    }

    protected static void openDatabase() {
        try {
            myDbHelper.openDataBase();
            databaseOpened=true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private  void fetch_history(){
        historyList = new ArrayList<>();
        historyAdapter = new RecyclerViewAdapterHistory(this, historyList);
        recyclerView.setAdapter(historyAdapter);

        History h;
        if (databaseOpened){
            cursorHistory = myDbHelper.getHistory();
            if (cursorHistory.moveToFirst()){
                do {
                    h = new History(cursorHistory.getString(cursorHistory.getColumnIndex("word")),cursorHistory.getString(cursorHistory.getColumnIndex("en_definition")));
                    historyList.add(h);
                }while (cursorHistory.moveToNext());
            }

            historyAdapter.notifyDataSetChanged();

            if(historyAdapter.getItemCount() == 0){
                emptyHistory.setVisibility(View.VISIBLE);
            }
            else {
                emptyHistory.setVisibility(View.GONE);
            }
        }
    }

    private  void showAlertDialog(){
        search.setQuery("",false);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,R.style.MyDialogTheme);
        builder.setTitle("Word Not Found");
        builder.setMessage("Please Search Again");
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                search.clearFocus();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent intent = new Intent(HomeActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;

        }

        if (id == R.id.action_logout){
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_exit){
            System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetch_history();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce){
            super.onBackPressed();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this,"Press Back Again To Exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        },5000);
    }
}
