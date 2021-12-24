package id.shimo.mindfulness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import id.shimo.mindfulness.helper.DBHelper;
import id.shimo.mindfulness.helper.DialogAPI;
import id.shimo.mindfulness.model.Journal;
import id.shimo.mindfulness.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateActivity extends AppCompatActivity {

    private TextInputLayout etBestThing, etWorstThing;
    private SeekBar seekRate;
    private ArrayList<String> didChecks;
    private CheckBox smileCheck, gratefulCheck, careCheck;
    private Button btnSubmit;
    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;
    private StringBuilder stringChecks;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView tvRate, tvBestThing, tvWorstThing, tvSeekRate, tvRadioResult, tvCheckResult, tvDatetime;
    private DBHelper db;
    String bestThing, worstThing, rate, radioButtonIntent, check, datetime;
    Integer id;
    private RadioButton yesRadio, noRadio;
    SharedPreferences mPrefs;
    private User loggedUser;
    private DialogAPI dialogAPI;
    private Call<ResponseBody> call;
    Gson gson=new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_jurnal);

        Intent intent = getIntent();
        bestThing = intent.getStringExtra("BestThing");
        worstThing = intent.getStringExtra("WorstThing");
        rate = intent.getStringExtra("Rate");
        radioButtonIntent = intent.getStringExtra("RadioButton");
        check = intent.getStringExtra("Check");
        datetime = intent.getStringExtra("DateTime");
        id = intent.getIntExtra("Id", 0);

        etBestThing = findViewById(R.id.username);
        etWorstThing = findViewById(R.id.password);
        seekRate = findViewById(R.id.rateSeek);
        tvRate = findViewById(R.id.ratetextvalue);

        etBestThing.getEditText().setText(bestThing);
        etWorstThing.getEditText().setText(worstThing);
        tvRate.setText(rate);
        seekRate.setProgress(Integer.valueOf(rate));

        radioGroup = findViewById(R.id.radioGroup);
        yesRadio = findViewById(R.id.yesCheck);
        noRadio = findViewById(R.id.noCheck);
        if(radioButtonIntent.equals(yesRadio.getText().toString())){
            radioGroup.check(R.id.yesCheck);
        }else if(radioButtonIntent.equals(noRadio.getText().toString())){
            radioGroup.check(R.id.noCheck);
        }

        smileCheck = (CheckBox)findViewById(R.id.smileCheck);
        gratefulCheck = (CheckBox)findViewById(R.id.gratefulCheck);
        careCheck = (CheckBox)findViewById(R.id.careCheck);
        didChecks = new ArrayList<>();

        String[] did = check.split("- ");
        if(did.length > 1){
            smileCheck.setChecked(true);
            didChecks.add(smileCheck.getText().toString());
        }
        if(did.length > 2){
            gratefulCheck.setChecked(true);
            didChecks.add(gratefulCheck.getText().toString());
        }
        if(did.length > 3){
            careCheck.setChecked(true);
            didChecks.add(careCheck.getText().toString());
        }

        checkOnClick();

        btnSubmit = (Button) findViewById(R.id.login);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdate(didChecks);
            }
        });

        mPrefs= this.getSharedPreferences("pref",0);
        String json = mPrefs.getString("loggedUser", "");
        loggedUser = gson.fromJson(json, User.class);
        Retrofit retrofit= new Retrofit.Builder().baseUrl(dialogAPI.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        dialogAPI=retrofit.create(DialogAPI.class);
    }

    private void checkOnClick() {
        smileCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smileCheck.isChecked())
                    didChecks.add(smileCheck.getText().toString());
                else
                    didChecks.remove(smileCheck.getText().toString());
            }
        });

        gratefulCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gratefulCheck.isChecked())
                    didChecks.add(gratefulCheck.getText().toString());
                else
                    didChecks.remove(gratefulCheck.getText().toString());
            }
        });

        careCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (careCheck.isChecked())
                    didChecks.add(careCheck.getText().toString());
                else
                    didChecks.remove(careCheck.getText().toString());
            }
        });
    }

    private void btnUpdate(ArrayList<String> benefits){
        //benefits array to string
        stringChecks = new StringBuilder();
        for (String s : didChecks)
            stringChecks.append(" - "+s).append("\n");

        radioGroup = findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        db = new DBHelper(this);
        db.updateJournal(id,
                etBestThing.getEditText().getText().toString(),
                etWorstThing.getEditText().getText().toString(),
                tvRate.getText().toString(),
                radioButton.getText().toString(),
                stringChecks.toString(),
                datetime);

        Journal journal=new Journal(id,etBestThing.getEditText().getText().toString(),etWorstThing.getEditText().getText().toString(),tvRate.getText().toString(),radioButton.getText().toString(),stringChecks.toString(), Calendar.getInstance().getTime().toString(),loggedUser.getId_user());
        String jsonJournal=gson.toJson(journal);
        call=dialogAPI.updateJournal(jsonJournal);
        Log.d("inputJ",jsonJournal);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    Log.d("errcode",String.valueOf(response.code()));
                    Toast.makeText(getApplicationContext(), "Code : " + Integer.toString(response.code()), Toast.LENGTH_LONG).show();
                    return;
                }
                String respon=response.body().toString();
                Log.d("inputJ",jsonJournal);
                Log.d("outputJ",respon);
                Toast.makeText(getApplicationContext(), "Respon :" +respon,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Gagal !",Toast.LENGTH_LONG).show();
            }
        });

    }
}
