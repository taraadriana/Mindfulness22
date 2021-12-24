package id.shimo.mindfulness;

import android.content.DialogInterface;
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

public class AddJurnalActivity extends AppCompatActivity {

    private TextInputLayout etBestThing;
    private TextInputLayout etWorstThing;
    private SeekBar seekRate;
    private ArrayList<String> didChecks;
    private CheckBox smileCheck, gratefulCheck, careCheck;
    private Button btnSubmit;
    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;
    public String strBestThing, strWorstThing;
    private StringBuilder stringChecks;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView tvRate, tvBestThing, tvWorstThing, tvSeekRate, tvRadioResult, tvCheckResult, tvDatetime;
    private DBHelper db;
    private DialogAPI dialogAPI;
    private Call<ResponseBody> call;
    SharedPreferences mPrefs;
    private User loggedUser;
    Gson gson=new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setTitle("Today's Journey");
        getSupportActionBar().setTitle("Today's Journey");
        setContentView(R.layout.add_jurnal);

        etBestThing = findViewById(R.id.username);
        etWorstThing = findViewById(R.id.password);
        seekRate = findViewById(R.id.rateSeek);
        tvRate = findViewById(R.id.ratetextvalue);

        seekRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRate.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        smileCheck = (CheckBox)findViewById(R.id.smileCheck);
        gratefulCheck = (CheckBox)findViewById(R.id.gratefulCheck);
        careCheck = (CheckBox)findViewById(R.id.careCheck);
        didChecks = new ArrayList<>();
        checkOnClick();
        btnSubmit = (Button) findViewById(R.id.login);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(didChecks);
            }
        });
        Retrofit retrofit= new Retrofit.Builder().baseUrl(dialogAPI.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        dialogAPI=retrofit.create(DialogAPI.class);

        mPrefs= this.getSharedPreferences("pref",0);
        String json = mPrefs.getString("loggedUser", "");
        loggedUser = gson.fromJson(json, User.class);
    }

    private void showDialog(ArrayList<String> didChecks){

        dialog = new AlertDialog.Builder(AddJurnalActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.alert_journal, null);
        dialog.setView(dialogView);
        //dialog.setTitle("Journal Preview");

        //checkbox array to string
        stringChecks = new StringBuilder();
        for (String s : didChecks)
            stringChecks.append(" - "+s).append("\n");

        //radio
        radioGroup = findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        tvBestThing = (TextView) dialogView.findViewById(R.id.username);
        tvWorstThing = (TextView) dialogView.findViewById(R.id.password);
        tvSeekRate = (TextView) dialogView.findViewById(R.id.seekRate);
        tvRadioResult = (TextView) dialogView.findViewById(R.id.radioResult);
        tvCheckResult = (TextView) dialogView.findViewById(R.id.checkResult);
        tvDatetime = (TextView) dialogView.findViewById(R.id.datetime);

        strBestThing = etBestThing.getEditText().getText().toString();
        strWorstThing = etWorstThing.getEditText().getText().toString();

        tvBestThing.setText(strBestThing);
        tvWorstThing.setText(strWorstThing);
        tvSeekRate.setText(tvRate.getText().toString());
        tvRadioResult.setText(radioButton.getText());
        tvCheckResult.setText(stringChecks);
        tvDatetime.setText(Calendar.getInstance().getTime().toString());

        boolean check = validasiForm(strBestThing, strWorstThing, selectedId);

        if (check == true) {

            tvBestThing.setText(etBestThing.getEditText().getText().toString());
            tvWorstThing.setText(etWorstThing.getEditText().getText().toString());
            tvSeekRate.setText(tvRate.getText().toString());
            tvRadioResult.setText(radioButton.getText());
            tvCheckResult.setText(stringChecks);
            tvDatetime.setText(Calendar.getInstance().getTime().toString());

            dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveDataToDB();

                    Intent intent = new Intent(AddJurnalActivity.this, Home.class);
                    intent.putExtra("BestThing", etBestThing.getEditText().getText().toString());
                    intent.putExtra("WorstThing", etWorstThing.getEditText().getText().toString());
                    intent.putExtra("Rate", tvRate.getText().toString());
                    intent.putExtra("RadioButton", radioButton.getText());
                    intent.putExtra("Check", stringChecks.toString());
                    intent.putExtra("DateTime", Calendar.getInstance().getTime().toString());

                    Journal journal=new Journal(0,etBestThing.getEditText().getText().toString(),etWorstThing.getEditText().getText().toString(),tvRate.getText().toString(),radioButton.getText().toString(),stringChecks.toString(),Calendar.getInstance().getTime().toString(),loggedUser.getId_user());
                    String jsonJournal=gson.toJson(journal);
                    Log.d("jsonJournal",jsonJournal);
                    call=dialogAPI.createJournal(jsonJournal);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(!response.isSuccessful()){
                                Log.d("dewa27",String.valueOf(response.code()));
                                Toast.makeText(getApplicationContext(), "Code : " + Integer.toString(response.code()), Toast.LENGTH_LONG).show();
                                return;
                            }
                            String respon=response.body().toString();
                            Toast.makeText(getApplicationContext(), "Respon :" +respon,Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Gagal !",Toast.LENGTH_LONG).show();
                        }
                    });

                    startActivity(intent);
                    finish();
                }
            });

            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
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

    private void saveDataToDB(){
        db = new DBHelper(this);
        db.insertJournal(etBestThing.getEditText().getText().toString(),
                etWorstThing.getEditText().getText().toString(),
                tvRate.getText().toString(),
                radioButton.getText().toString(),
                stringChecks.toString(),
                Calendar.getInstance().getTime().toString());
    }

    public boolean validasiForm(String bestThing, String worstThing, Integer rbDayWent) {
        if (bestThing.length() == 0) {
            etBestThing.requestFocus();
            etBestThing.setError("Inputan tidak boleh kosong!");
            return false;

        } else if (worstThing.length() == 0) {
            etWorstThing.requestFocus();
            etWorstThing.setError("Inputan tidak boleh kosong!");
            return false;

        } else if(rbDayWent == -1){
            Toast.makeText(getApplicationContext(),"Inputan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return false;

        } else if (!smileCheck.isChecked() && !gratefulCheck.isChecked() && !careCheck.isChecked()){
            Toast.makeText(getApplicationContext(),"Inputan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return false;

        }else{
            return true;
        }
    }
}
