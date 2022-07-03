package com.familheey.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.ShareUser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class EventShareAdapter extends RecyclerView.Adapter<EventShareAdapter.MyViewHolder> {
    private List<ShareUser> shareUser;

    public EventShareAdapter(Context context, List<ShareUser> shareUser) {
        this.shareUser = shareUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_people_share, parent, false);

        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.select.setVisibility(View.INVISIBLE);
        holder.txtLocation.setText(shareUser.get(position).getLocation());
        String tittle = shareUser.get(position).getFull_name();
        if (shareUser.get(position).getTo_group_name() != null) {
            tittle += " Shared to " + shareUser.get(position).getTo_group_name();
        } else if (shareUser.get(position).getTo_user_name() != null) {
            tittle += " Shared to " + shareUser.get(position).getTo_user_name();
        } else {
            if (shareUser.get(position).getFull_name() == null) {
                tittle = "Unknown";
            }
        }
        if (shareUser.get(position).getCreated_date() != null) {
            holder.txt_date.setText(dateFormat(shareUser.get(position).getCreated_date()));
        }
        holder.txtName.setText(tittle);
        Glide.with(holder.itemView.getContext())
                .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + shareUser.get(position).getPropic())
                .apply(Utilities.getCurvedRequestOptions())
                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .placeholder(R.drawable.avatar_male)
                .into(holder.profileImage);
        holder.itemView.setOnClickListener(v -> {
            if (shareUser != null && shareUser.get(position) != null && shareUser.get(position).getUserId() != null) {
                Intent userIntent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
                FamilyMember familyMember = new FamilyMember();
                familyMember.setUserId(Integer.valueOf(shareUser.get(position).getUserId()));
                userIntent.putExtra(DATA, familyMember);
                holder.itemView.getContext().startActivity(userIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shareUser.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        MaterialButton select;
        TextView txtName, txtLocation, txt_date;
        ImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.userName);
            txt_date = itemView.findViewById(R.id.txt_date);
            txtLocation = itemView.findViewById(R.id.userLocation);
            profileImage = itemView.findViewById(R.id.userProfileImage);
            select = itemView.findViewById(R.id.select);
        }
    }

    private String dateFormat(String time) {
        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time);
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
        return dtfOut.print(dateTime);
    }
}
