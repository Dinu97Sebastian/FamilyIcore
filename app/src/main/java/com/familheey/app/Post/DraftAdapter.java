package com.familheey.app.Post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.R;

import java.util.ArrayList;

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.MyViewHolder> {


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_drafted_items, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
        holder.txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

    }



    @Override
    public int getItemCount() {
        return 10;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtFamilyname,txtAttachmentCount,txtRetry,txtCancel;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtFamilyname = (TextView) itemView.findViewById(R.id.txtFamilyName);
            txtAttachmentCount = (TextView) itemView.findViewById(R.id.txtAttachmentCount);
            txtRetry = (TextView) itemView.findViewById(R.id.txtRetry);
            txtCancel = (TextView) itemView.findViewById(R.id.txtCancel);
        }
    }
}
