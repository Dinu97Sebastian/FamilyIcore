package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Response.PeopleShareResponse;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class PeopleShareAdapter extends RecyclerView.Adapter<PeopleShareAdapter.ViewHolder> {
    ArrayList<PeopleShareResponse.Datum> data = new ArrayList<>();
    private final ArrayList<PeopleShareResponse.Datum> selectedUsers = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_people_share, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PeopleShareResponse.Datum datum = data.get(position);
        if(datum.getPropic()!=null){
            Glide.with(holder.userProfileImage.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + datum.getPropic())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.avatar_male)
                    .into(holder.userProfileImage);
        }else {
            Glide.with(holder.userProfileImage.getContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.userProfileImage);
        }
        holder.userName.setText(datum.getFullName());
        holder.userLocation.setText(datum.getLocation());
        holder.select.setOnClickListener(view -> {
            if (datum.getInvitationStatus()) {
                Toast.makeText(holder.itemView.getContext(), "Already invited", Toast.LENGTH_SHORT).show();
                return;
            }
            if (datum.isDevSet()) {
                datum.setDevSet(false);
                removeSelectedUsers(datum);
            } else {
                datum.setDevSet(true);
                selectedUsers.add(datum);
            }
            notifyDataSetChanged();
        });
        if (datum.getInvitationStatus() != null && datum.getInvitationStatus()) {
            holder.select.setText("Invited");
        } else {
            if (datum.isDevSet()) {
                holder.select.setText("Selected");
            } else {
                holder.select.setText("Select");
            }
        }
    }

    public ArrayList<PeopleShareResponse.Datum> getSelectedList() {
        return selectedUsers;
    }

    public void removeSelectedUsers(PeopleShareResponse.Datum userToBeRemoved) {
        for (int i = 0; i < selectedUsers.size(); i++) {
            if (selectedUsers.get(i).getUserId().equals(userToBeRemoved.getUserId())) {
                selectedUsers.remove(i);
                return;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<PeopleShareResponse.Datum> data) {
        this.data = data;
        notifyDataSetChanged();
    }


    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userProfileImage)
        ImageView userProfileImage;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.locationIcon)
        ImageView locationIcon;
        @BindView(R.id.userLocation)
        TextView userLocation;
        @BindView(R.id.select)
        Button select;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
