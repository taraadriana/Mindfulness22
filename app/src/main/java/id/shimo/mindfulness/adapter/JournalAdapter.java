package id.shimo.mindfulness.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

import id.shimo.mindfulness.AddJurnalActivity;
import id.shimo.mindfulness.DetailActivity;
import id.shimo.mindfulness.R;
import id.shimo.mindfulness.model.Journal;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.viewholder> {

    ArrayList<Journal> dataholder = new ArrayList<>();
    Context context;
    SQLiteDatabase sqLiteDatabase;

    public JournalAdapter(ArrayList<Journal> dataholder, Context context, SQLiteDatabase sqLiteDatabase) {
        this.dataholder = dataholder;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journal, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.bestThing.setText(dataholder.get(position).getBestThing());
        holder.rate.setText(dataholder.get(position).getRate());

        String[] separated = dataholder.get(position).getDatetime().split(" ");

        holder.date.setText(separated[2]);
        holder.month.setText(separated[1]);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("BestThing", dataholder.get(position).getBestThing());
                intent.putExtra("WorstThing", dataholder.get(position).getWorstThing());
                intent.putExtra("Rate", dataholder.get(position).getRate());
                intent.putExtra("RadioButton", dataholder.get(position).getWellness());
                intent.putExtra("Check", dataholder.get(position).getDid());
                intent.putExtra("DateTime", dataholder.get(position).getDatetime());
                intent.putExtra("Id", dataholder.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataholder == null ? 0 : dataholder.size();
    }

    class viewholder extends RecyclerView.ViewHolder{
        TextView date, month, bestThing, rate;
        ImageView card;
        public viewholder(@NonNull View itemView){
            super(itemView);
            card = (ImageView) itemView.findViewById(R.id.imageView);
            date = (TextView) itemView.findViewById(R.id.dateCard);
            month = (TextView) itemView.findViewById(R.id.monthCard);
            bestThing = (TextView) itemView.findViewById(R.id.cardContent);
            rate = (TextView) itemView.findViewById(R.id.cardRate);
        }
    }
}
