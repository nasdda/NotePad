package com.example.javaprac.Recycler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaprac.Activities.NoteActivity;
import com.example.javaprac.Database.NoteDatabase;
import com.example.javaprac.R;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mTitles;
    private ArrayList<Long> primaryId;
    private Context mContext;
    Executor dbExecutor = Executors.newSingleThreadExecutor();

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mTitles, ArrayList<Long> primaryId) {
        this.mTitles = mTitles;
        this.mContext = mContext;
        this.primaryId = primaryId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleText.setText(mTitles.get(position));
        holder.icon.setImageResource(R.mipmap.ic_launcher);
        holder.contentLayout.setOnClickListener(view -> dbExecutor.execute(()->{
            Intent intent = new Intent(mContext,NoteActivity.class);
            Bundle extra = new Bundle();
            extra.putLong("ID", primaryId.get(position));
            extra.putString("title", mTitles.get(position));
            intent.putExtras(extra);
            mContext.startActivity(intent);
        }));
        holder.deleteIcon.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.click_anim));
            AlertDialog.Builder builder = confirmDeletion();
            builder.setPositiveButton("Delete", (dialogInterface, i) -> {
                dbExecutor.execute(()->{
                    NoteDatabase.getNoteDatabase(mContext).noteDatabaseDao().delete(primaryId.get(position));
                    primaryId.remove(position);
                });
                mTitles.remove(position);
                notifyDataSetChanged();
            });
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView titleText;
        ConstraintLayout parentLayout;
        LinearLayout contentLayout;
        ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.list_icon);
            titleText = itemView.findViewById(R.id.item_title);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            contentLayout = itemView.findViewById(R.id.content_box);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
        }
    }


    public AlertDialog.Builder confirmDeletion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Delete Note?");
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            //do nothing
        });
        return builder;
    }
}
