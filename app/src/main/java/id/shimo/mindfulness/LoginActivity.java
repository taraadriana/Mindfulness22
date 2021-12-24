package id.shimo.mindfulness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import id.shimo.mindfulness.helper.DBHelper;
import id.shimo.mindfulness.model.User;

public class LoginActivity extends AppCompatActivity {

    public EditText logUsername, logPassword;
    public TextView textRegister;
    DBHelper dbHelper;
    private User loggedUser;
    SharedPreferences mPrefs;
    Gson gson=new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logUsername = findViewById(R.id.username);
        logPassword = findViewById(R.id.password);
        textRegister = findViewById(R.id.textRegister);
        dbHelper = new DBHelper(this);
        //ngambil shared preferences
        mPrefs= this.getSharedPreferences("pref",0);
        String json = mPrefs.getString("loggedUser", "");
        loggedUser = gson.fromJson(json, User.class);

        if(loggedUser!=null){
            Intent intent  = new Intent(LoginActivity.this, Home.class);
            intent.putExtra("UserNameLogin", loggedUser.getUsername());
            startActivity(intent);
            finish();
        }
    }

    public void onLoginClick (View view){
        if(view.getId() == R.id.btnLogin) {
            String ambilUsername = logUsername.getText().toString().trim();
            String ambilPassword = logPassword.getText().toString().trim();

            loggedUser = dbHelper.checkUser(ambilUsername, ambilPassword);
            if (loggedUser.getId_user()>0){
                Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                String json = gson.toJson(loggedUser);
                prefsEditor.putString("loggedUser", json);
                prefsEditor.commit();
                Intent intent = new Intent(LoginActivity.this,Home.class);
                intent.putExtra("UserNameLogin", ambilUsername);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, "Login failed!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRegisterClick (View view){
        startActivity(new Intent(this, RegisterActivity.class));
    }
}