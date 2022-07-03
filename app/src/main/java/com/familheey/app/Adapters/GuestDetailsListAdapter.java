package com.familheey.app.Adapters;

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
import com.familheey.app.Models.Response.GuestRSVPResponse;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class GuestDetailsListAdapter extends RecyclerView.Adapter<GuestDetailsListAdapter.ViewHolder> {
    private ArrayList<GuestRSVPResponse> guests;

    public GuestDetailsListAdapter(ArrayList<GuestRSVPResponse> guests) {
        this.guests = guests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.guest_details_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GuestRSVPResponse modal = guests.get(position);
        if (modal.getOthersCount() == 0)
            holder.userName.setText(modal.getFullName());
        else
            holder.userName.setText(modal.getFullName() + " with " + modal.getOthersCount() + " others");
        holder.userLocation.setText(modal.getLocation());
        holder.mutualCount.setText(modal.getMutualContact());
        holder.familyCount.setText(modal.getTotalFamily());
        if(modal.getPropic()!=null){
            Glide.with(holder.userProfileImage.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + modal.getPropic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.avatar_male)
                    .into(holder.userProfileImage);
        }else {
            Glide.with(holder.userProfileImage.getContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.userProfileImage);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent userIntent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setUserId(modal.getId());
            userIntent.putExtra(DATA, familyMember);
            holder.itemView.getContext().startActivity(userIntent);
        });
    }

    @Override
    public int getItemCount() {
        return guests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userProfileImage)
        ImageView userProfileImage;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.userLocation)
        TextView userLocation;
        @BindView(R.id.membersCount)
        TextView familyCount;
        @BindView(R.id.eventsCount)
        TextView mutualCount;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
