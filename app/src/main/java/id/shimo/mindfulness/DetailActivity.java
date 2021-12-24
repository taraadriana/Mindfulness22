package id.shimo.mindfulness;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import id.shimo.mindfulness.helper.DBHelper;
import id.shimo.mindfulness.helper.DialogAPI;
import id.shimo.mindfulness.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {
    private TextView tvRate, tvDatetime, tvBestThing, tvWorstThing, tvSeekRate, tvRadioResult, tvCheckResult;
    Integer id;
    String bestThing, worstThing, rate, radioButton, check, datetime;
    private DBHelper db;
    private DialogAPI dialogAPI;
    private Call<ResponseBody> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        bestThing = intent.getStringExtra("BestThing");
        worstThing = intent.getStringExtra("WorstThing");
        rate = intent.getStringExtra("Rate");
        radioButton = intent.getStringExtra("RadioButton");
        check = intent.getStringExtra("Check");
        datetime = intent.getStringExtra("DateTime");
        id = intent.getIntExtra("Id", 0);

        tvBestThing = (TextView) findViewById(R.id.username);
        tvWorstThing = (TextView) findViewById(R.id.password);
        tvSeekRate = (TextView) findViewById(R.id.seekRate);
        tvRadioResult = (TextView) findViewById(R.id.radioResult);
        tvCheckResult = (TextView) findViewById(R.id.checkResult);
        tvDatetime = (TextView) findViewById(R.id.datetime);

        tvBestThing.setText(bestThing);
        tvWorstThing.setText(worstThing);
        tvSeekRate.setText(rate);
        tvRadioResult.setText(radioButton);
        tvCheckResult.setText(check);
        tvDatetime.setText(datetime);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(DetailActivity.this, "onRestart Running", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(DetailActivity.this, "onResume Running", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(DetailActivity.this, "onStop Running", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
        Toast.makeText(DetailActivity.this, "onDestroy Running", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class);
                intent.putExtra("BestThing", bestThing);
                intent.putExtra("WorstThing", worstThing);
                intent.putExtra("Rate", rate);
                intent.putExtra("RadioButton", radioButton);
                intent.putExtra("Check", check);
                intent.putExtra("DateTime", datetime);
                intent.putExtra("Id", id);
                startActivity(intent);
                return true;
            case R.id.delete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailActivity.this);
                alertDialog.setTitle("Delete Confirmation");
                alertDialog.setMessage("Are you sure to delete this journal?");
                alertDialog.setCancelable(false);
                alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Log.v("tess", id.toString());
                        db = new DBHelper(DetailActivity.this);
                        db.deleteJournal(id);
                        Retrofit retrofit= new Retrofit.Builder().baseUrl(dialogAPI.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
                        dialogAPI=retrofit.create(DialogAPI.class);
                        call=dialogAPI.deleteJournal(id);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(!response.isSuccessful()){
                                    Log.d("errcode",String.valueOf(response.code()));
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
                        startActivity(new Intent(DetailActivity.this, LoginActivity.class));
                        finish();
                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();
                return true;
        }

        return false;
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(DetailActivity.this, Home.class));
//    }
}