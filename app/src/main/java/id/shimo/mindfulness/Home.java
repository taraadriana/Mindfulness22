package id.shimo.mindfulness;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;

import id.shimo.mindfulness.helper.DBHelper;
import id.shimo.mindfulness.model.Journal;

import id.shimo.mindfulness.adapter.JournalAdapter;
import id.shimo.mindfulness.model.User;

public class Home extends AppCompatActivity {
    String mainUser;
    public TextView homeUsername;
    DBHelper dbHelper;
    protected Cursor cursor1;
    private FloatingActionButton add;
    private RecyclerView recyclerView;
    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<Journal> journalHolder = new ArrayList<>();
    private ImageButton btnLogout;
    SharedPreferences mPrefs;
    private User loggedUser;
    Gson gson=new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_home);

        dbHelper = new DBHelper(this);
        homeUsername = (TextView) findViewById(R.id.homeUsername);
        btnLogout=findViewById(R.id.btn_logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(view.getContext());
                Intent intent=new Intent(Home.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        add = (FloatingActionButton) findViewById(R.id.addJurnal);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, AddJurnalActivity.class);
                startActivity(intent);
            }
        });
        mPrefs= this.getSharedPreferences("pref",0);
        String json = mPrefs.getString("loggedUser", "");
        loggedUser = gson.fromJson(json, User.class);
        Bundle login = getIntent().getExtras();
        mainUser=loggedUser.getName();
//        mainUser = login.getString("UserNameLogin");
//        mainUser="dewa";
        DBHelper db = new DBHelper(this);
        SQLiteDatabase dB = dbHelper.getReadableDatabase();
        homeUsername.setText(mainUser);
//        cursor1 = dB.rawQuery("SELECT name FROM tb_user WHERE username = '" +
//                mainUser + "'", null);
//        cursor1.moveToFirst();
//        if (cursor1.getCount()>0){
//            cursor1.moveToPosition(0);
//
//        }
//        cursor1.close();

        recyclerView = (RecyclerView) findViewById(R.id.rvJournals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = new DBHelper(this).readJournals();
        while(cursor.moveToNext()){
            Journal obj = new Journal(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7));
            journalHolder.add(obj);
        }

        JournalAdapter adapter = new  JournalAdapter(journalHolder, Home.this, sqLiteDatabase);
        recyclerView.setAdapter((RecyclerView.Adapter) adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.account) {
            Toast.makeText(Home.this,"Account",Toast.LENGTH_SHORT);
            Intent intent = new Intent(Home.this,AccountActivity.class);
            intent.putExtra("MainName", mainUser);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.logout) {
            Toast.makeText(Home.this,"Logout",Toast.LENGTH_LONG);
            Intent intent = new Intent(Home.this,LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
    public void logout(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
    }
}