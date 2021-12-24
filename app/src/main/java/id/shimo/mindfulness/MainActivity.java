package id.shimo.mindfulness;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import id.shimo.mindfulness.helper.DBHelper;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    public TextView textRegister;
    public EditText logUsername, logPassword;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();
        setContentView(R.layout.activity_main);
//
//        btn = (Button) findViewById(R.id.btnLogin);
//        textRegister = (TextView) findViewById(R.id.textRegister);
//        //logUsername = findViewById(R.id.username1);
//        //logPassword = findViewById(R.id.password1);
//        dbHelper = new DBHelper(this);
    }

//    public void onLoginClick (View v){
//
//        //logUsername = findViewById(R.id.username1);
//        //logPassword = findViewById(R.id.password1);
//
//        if(v.getId() == R.id.btnLogin) {
//            String ambilUsername = logUsername.getText().toString().trim();
//            String ambilPassword = logPassword.getText().toString().trim();
//
//            Boolean res = dbHelper.checkUser(ambilUsername, ambilPassword);
//            if (res){
//                Toast.makeText(MainActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this,Home.class);
//                intent.putExtra("UserNameLogin", ambilUsername);
//                startActivity(intent);
//            }else{
//                Toast.makeText(MainActivity.this, "Login failed!",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    public void onRegisterClick (View v){
//        Intent intent = new Intent(this, RegisterActivity.class);
//        startActivity(intent);
//    }
}