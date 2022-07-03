package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class FamilyShareAdapter extends RecyclerView.Adapter<FamilyShareAdapter.ViewHolder> {

    private ArrayList<Family> families = new ArrayList<>();
    private final ArrayList<Family> selectedFamilies = new ArrayList<>();
    private String type = "";


    public FamilyShareAdapter(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_share_event_to_family, parent, false);
        return new ViewHolder(listItem);

    }

    public void setData(ArrayList<Family> families) {
        this.families = families;
        notifyDataSetChanged();
    }

    public ArrayList<Family> getSelectedList() {
        return selectedFamilies;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Family familyToBeSelect = families.get(position);
        holder.createdBy.setText(familyToBeSelect.getRequestedByName());
        holder.familyName.setText(familyToBeSelect.getGroupName());
        holder.familyType.setText(familyToBeSelect.getGroupCategory());
        //holder.eventsCount.setText(familyToBeSelect.getMembersCount());
        holder.createdBy.setText(familyToBeSelect.getCreatedBy());

        if (familyToBeSelect.getMembersCount() != null && Integer.parseInt(familyToBeSelect.getMembersCount()) > 0) {
            holder.labelMembersCount.setText("Members");
            holder.membersCount.setText(familyToBeSelect.getMembersCount());
        }
        holder.familyLocation.setText(familyToBeSelect.getBaseRegion());
        if (familyToBeSelect.getLogo() != null) {
            Glide.with(holder.familyLogo.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + familyToBeSelect.getLogo())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.family_logo)
                    .into(holder.familyLogo);
        } else {
            Glide.with(holder.familyLogo.getContext())
                    .load(R.drawable.family_logo)
                    .into(holder.familyLogo);
        }

        if (familyToBeSelect.getInvitationStatus() != null && familyToBeSelect.getInvitationStatus())
            holder.linkFamily.setText("Invited");
        else {
            if (familyToBeSelect.isDevSelected()) {
                holder.linkFamily.setText("Selected");
            } else {
                holder.linkFamily.setText("Select");
            }
        }
        holder.linkFamily.setOnClickListener(v -> {
            if (type.equals("POST") || type.equals("Announcement")) {

                if (familyToBeSelect.isDevSelected()) {
                    familyToBeSelect.setDevSelected(false);
                    removeSelectedFamilies(familyToBeSelect);
                } else {
                    familyToBeSelect.setDevSelected(true);
                    addSelectedFamilies(familyToBeSelect);
                }

            } else {
                if (familyToBeSelect.getInvitationStatus() != null && familyToBeSelect.getInvitationStatus()) {
                    Toast.makeText(holder.itemView.getContext(), "Already invited", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (familyToBeSelect.isDevSelected()) {
                    familyToBeSelect.setDevSelected(false);
                    removeSelectedFamilies(familyToBeSelect);
                } else {
                    familyToBeSelect.setDevSelected(true);
                    addSelectedFamilies(familyToBeSelect);
                }
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return families.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.familyLogo)
        ImageView familyLogo;
        @BindView(R.id.familyName)
        TextView familyName;
        @BindView(R.id.createdBy)
        TextView createdBy;
        @BindView(R.id.familyType)
        TextView familyType;
        @BindView(R.id.locationIcon)
        ImageView locationIcon;
        @BindView(R.id.familyLocation)
        TextView familyLocation;
        @BindView(R.id.membersCount)
        TextView membersCount;
        @BindView(R.id.labelMembersCount)
        TextView labelMembersCount;
        @BindView(R.id.eventsCount)
        TextView eventsCount;
        @BindView(R.id.linkFamily)
        MaterialButton linkFamily;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ArrayList<Family> getSelectedFamilies() {
        return selectedFamilies;
    }

    public void removeSelectedFamilies(Family familyToBeRemoved) {
        for (int i = 0; i < selectedFamilies.size(); i++) {
            if (selectedFamilies.get(i).getId().equals(familyToBeRemoved.getId())) {
                selectedFamilies.remove(i);
                return;
            }
        }
    }

    public void addSelectedFamilies(Family familyToBeAdded) {
        boolean exists = false;
        for (int i = 0; i < selectedFamilies.size(); i++) {
            if (selectedFamilies.get(i).getId().equals(familyToBeAdded.getId())) {
                exists = true;
                break;
            }
        }
        if (!exists)
            selectedFamilies.add(familyToBeAdded);
    }

}