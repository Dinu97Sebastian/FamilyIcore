package com.familheey.app.Adapters.EventGuest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.InvitedOther;
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

public class OtherGuestListingAdapter extends RecyclerView.Adapter<OtherGuestListingAdapter.ViewHolder> {

    private ArrayList<InvitedOther> invitedOthers;

    public OtherGuestListingAdapter(ArrayList<InvitedOther> invitedOthers) {
        this.invitedOthers = invitedOthers;
    }

    @NonNull
    @Override
    public OtherGuestListingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_event_guest_invitation, parent, false);
        return new OtherGuestListingAdapter.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull OtherGuestListingAdapter.ViewHolder holder, int position) {
        InvitedOther invitedOther = invitedOthers.get(position);
        holder.name.setText(invitedOther.getFullName());
        holder.location.setVisibility(View.INVISIBLE);
        holder.locationIcon.setVisibility(View.INVISIBLE);
        Glide.with(holder.itemView.getContext())
                .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + invitedOther.getFullName())
                .apply(Utilities.getCurvedRequestOptions())
                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .placeholder(R.drawable.avatar_male)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return invitedOthers.size();
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