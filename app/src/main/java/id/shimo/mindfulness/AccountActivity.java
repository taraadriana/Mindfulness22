package id.shimo.mindfulness;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import id.shimo.mindfulness.helper.DBHelper;

public class AccountActivity extends AppCompatActivity {

    public TextView accProfile, accName, accEmail, accUsername, accPassword,
            accAge, accGender;
    DBHelper dbHelper;
    protected Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        dbHelper = new DBHelper(this);

        accProfile = (TextView) findViewById(R.id.profile_name);
        accName = (TextView) findViewById(R.id.acc_name);
        accEmail = (TextView) findViewById(R.id.acc_email);
        accUsername = (TextView) findViewById(R.id.acc_username);
        accPassword = (TextView) findViewById(R.id.acc_password);
        accAge = (TextView) findViewById(R.id.acc_age);
        accGender = (TextView) findViewById(R.id.acc_gender);

        DBHelper dbHelper = new DBHelper(AccountActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM tb_user WHERE username = '" +
                getIntent().getStringExtra("MainName") + "'", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            cursor.moveToPosition(0);
            accProfile.setText(cursor.getString(3).toString());
            accName.setText(cursor.getString(1).toString());
            accEmail.setText(cursor.getString(2).toString());
            accUsername.setText(cursor.getString(3).toString());
            accPassword.setText(cursor.getString(4).toString());
            accAge.setText(cursor.getString(5).toString());
            accGender.setText(cursor.getString(6).toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}