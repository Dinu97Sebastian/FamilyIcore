package com.familheey.app.Adapters.EventGuest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Response.InvitedUser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class UserGuestListingAdapter extends RecyclerView.Adapter<UserGuestListingAdapter.ViewHolder> {

    private ArrayList<InvitedUser> invitedUsers;

    public UserGuestListingAdapter(ArrayList<InvitedUser> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    @NonNull
    @Override
    public UserGuestListingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_event_guest_invitation, parent, false);
        return new UserGuestListingAdapter.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGuestListingAdapter.ViewHolder holder, int position) {
        InvitedUser invitedUser = invitedUsers.get(position);
        holder.name.setText(invitedUser.getFullName());
        if(invitedUser.getLocation()!=null){
            holder.location.setText(invitedUser.getLocation());
            holder.locationIcon.setVisibility(View.VISIBLE);
            holder.location.setVisibility(View.VISIBLE);
        }else {
            holder.locationIcon.setVisibility(View.INVISIBLE);
            holder.location.setVisibility(View.INVISIBLE);
        }
        if(invitedUser.getPropic()!=null){
            Glide.with(holder.itemView.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + invitedUser.getPropic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.avatar_male)
                    .into(holder.image);
        }else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return invitedUsers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.locationIcon)
        ImageView locationIcon;
        @BindView(R.id.location)
        TextView location;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}