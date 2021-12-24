package id.shimo.mindfulness;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import id.shimo.mindfulness.helper.DBHelper;
import id.shimo.mindfulness.helper.DialogAPI;
import id.shimo.mindfulness.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    public EditText regName, regEmail, regUsername, regPassword, regUsia;
    public TextView alertName, alertEmail, alertUsername, alertPassword,
            alertUsia, alertGender;
    public Button btnRegister;
    public RadioGroup gender;
    public RadioButton r1, r2;
    DBHelper dbHelper;
    private DialogAPI dialogAPI;
    private Call<ResponseBody> call;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regName = findViewById(R.id.regName);
        regEmail = findViewById(R.id.regEmail);
        regUsername = findViewById(R.id.regUsername);
        regPassword = findViewById(R.id.regPassword);
        regUsia = findViewById(R.id.regUsia);
        btnRegister = findViewById(R.id.btn_register);
        gender = (RadioGroup) findViewById(R.id.gender);
        r1 = (RadioButton) findViewById(R.id.male);
        r2 = (RadioButton) findViewById(R.id.female);
        dbHelper = new DBHelper(this);

        Retrofit retrofit= new Retrofit.Builder().baseUrl(dialogAPI.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        dialogAPI=retrofit.create(DialogAPI.class);
        btnRegister.setOnClickListener(v -> {

            String ambilName = regName.getText().toString();
            String ambilEmail = regEmail.getText().toString();
            String ambilUsername = regUsername.getText().toString();
            String ambilPassword = regPassword.getText().toString();
            String ambilUsia = regUsia.getText().toString();
            int ambilGender = gender.getCheckedRadioButtonId();

            View view = getLayoutInflater().inflate(R.layout.alert_dialog, null);

            boolean check = validasiForm(ambilName, ambilEmail, ambilUsername, ambilPassword,
                    ambilUsia, ambilGender);
            if (check == true) {
                alertName = (TextView) view.findViewById(R.id.ad_name);
                alertName.setText(ambilName);

                alertEmail = (TextView) view.findViewById(R.id.ad_email);
                alertEmail.setText(ambilEmail);

                alertUsername = (TextView) view.findViewById(R.id.ad_username);
                alertUsername.setText(ambilUsername);

                alertPassword = (TextView) view.findViewById(R.id.ad_password);
                alertPassword.setText(ambilPassword);

                alertUsia = (TextView) view.findViewById(R.id.ad_age);
                alertUsia.setText(ambilUsia);

                alertGender = (TextView) view.findViewById(R.id.ad_gender);
                if (ambilGender == r1.getId()) {
                    alertGender.setText("male");
                } else if (ambilGender == r2.getId()) {
                    alertGender.setText("female");
                }
                String ambilGenderString = alertGender.getText().toString();

                AlertDialog.Builder dialog1 = new AlertDialog.Builder(RegisterActivity.this);
                dialog1.setPositiveButton("ok", (dialogInterface, which) -> {

                    ContentValues values = new ContentValues();

                    values.put(dbHelper.row_name, ambilName);
                    values.put(dbHelper.row_email, ambilEmail);
                    values.put(dbHelper.row_username, ambilUsername);
                    values.put(dbHelper.row_password, ambilPassword);
                    values.put(dbHelper.row_age, ambilUsia);
                    values.put(dbHelper.row_gender, ambilGenderString);
                    dbHelper.insertDataUser(values);

                    call=dialogAPI.createUser(ambilName,ambilUsername,ambilGenderString,ambilEmail,ambilPassword,Integer.parseInt(ambilUsia));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(!response.isSuccessful()){
                                Log.d("errcode",String.valueOf(response.code()));
                                Toast.makeText(getApplicationContext(), "Code : " + Integer.toString(response.code()), Toast.LENGTH_LONG).show();
                                return;
                            }
                            Toast.makeText(getApplicationContext(), "Pendaftaran akun berhasil!" + Integer.toString(response.code()), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });


                    Intent intent = new Intent(RegisterActivity.this,
                            LoginActivity.class);
                    intent.putExtra("name",ambilName);
                    intent.putExtra("email",ambilEmail);
                    intent.putExtra("username",ambilUsername);
                    intent.putExtra("password",ambilPassword);
                    intent.putExtra("age",ambilUsia);
                    intent.putExtra("gender",ambilGenderString);
                    startActivity(intent);
                });

                dialog1.setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.cancel());
                dialog1.setView(view);
                AlertDialog dialog2 = dialog1.create();
                dialog2.show();

            }else {
                //Toast.makeText(getApplicationContext(),"Data tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean validasiForm(String ambilName, String ambilEmail, String ambilUsername, String ambilPassword,
                                String ambilUsia, Integer ambilGender) {
        if (ambilName.length() == 0) {
            regName.requestFocus();
            regName.setError("Nama tidak boleh kosong!");
            return false;

        } else if (ambilEmail.length() == 0) {
            regEmail.requestFocus();
            regEmail.setError("Email tidak boleh kosong!");
            return false;
        } else if (!ambilEmail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            regEmail.requestFocus();
            regEmail.setError("Email tidak valid!");
            return false;

        } else if (ambilUsername.length() == 0) {
            regUsername.requestFocus();
            regUsername.setError("Username tidak boleh kosong!");
            return false;

        } else if (ambilPassword.length() <= 5){
            regPassword.requestFocus();
            regPassword.setError("Minimal 6 karakter!");
            return false;

        } else if (ambilUsia.length() == 0) {
            regUsia.requestFocus();
            regUsia.setError("Usia tidak boleh kosong!");
            return false;

        } else if(ambilGender == -1){
            Toast.makeText(getApplicationContext(),"Jenis kelamin harus diisi!", Toast.LENGTH_SHORT).show();
            return false;

        }else{
            return true;
        }
    }

    public void onToLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}


