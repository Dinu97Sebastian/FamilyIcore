package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class LinkFamilyAdapter extends RecyclerView.Adapter<LinkFamilyAdapter.ViewHolder> {

    private ArrayList<Family> families;
    private List<Family> tempFamilies = new ArrayList<>();
    public LinkFamilyAdapter(ArrayList<Family> families) {
        this.families = families;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_link_family, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Family familyToBeLinked = families.get(position);
        holder.createdBy.setText(familyToBeLinked.getCreatedBy() + "");
        holder.familyName.setText(familyToBeLinked.getGroupName());
        if (familyToBeLinked.getBaseRegion() == null)
            holder.familyLocation.setText("Unknown Location");
        else holder.familyLocation.setText(familyToBeLinked.getBaseRegion());
        holder.linkingActionNotNeeded.setText(getFirstLetterCapitalized(familyToBeLinked.getIsLinked()));
        if (familyToBeLinked.getIsLinked() != null && familyToBeLinked.getIsLinked().equalsIgnoreCase("accepted")) {
            holder.linkFamily.setVisibility(View.GONE);//Unlink
            holder.familyLinked.setVisibility(View.GONE);
            holder.linkingActionNotNeeded.setText("Linked");
            holder.linkingActionNotNeeded.setVisibility(View.VISIBLE);
        } else if (familyToBeLinked.getIsLinked() != null && familyToBeLinked.getIsLinked().equalsIgnoreCase("pending")) {
            holder.linkFamily.setVisibility(View.GONE);
            holder.familyLinked.setVisibility(View.GONE);
            holder.linkingActionNotNeeded.setText("Invited");
            holder.linkingActionNotNeeded.setVisibility(View.VISIBLE);//Invited
        } else if (familyToBeLinked.getIsLinked() != null && familyToBeLinked.getIsLinked().equalsIgnoreCase("rejected")) {
            if (familyToBeLinked.isDevSelected()) {
                holder.linkFamily.setVisibility(View.VISIBLE);
                holder.familyLinked.setVisibility(View.GONE);
            } else {
                holder.familyLinked.setVisibility(View.VISIBLE);
                holder.linkFamily.setVisibility(View.GONE);
            }
            holder.linkingActionNotNeeded.setVisibility(View.GONE);
        } else {
            //Link
            if (familyToBeLinked.isDevSelected()) {
                holder.linkFamily.setVisibility(View.VISIBLE);
                holder.familyLinked.setVisibility(View.GONE);
            } else {
                holder.familyLinked.setVisibility(View.VISIBLE);
                holder.linkFamily.setVisibility(View.GONE);
            }
            holder.linkingActionNotNeeded.setVisibility(View.GONE);
        }
        holder.familyLinked.setOnClickListener(v -> holder.linkFamily.performClick());
        holder.linkFamily.setOnClickListener(v -> {
            if (familyToBeLinked.isDevSelected())
                familyToBeLinked.setDevSelected(false);
            else familyToBeLinked.setDevSelected(true);
            notifyItemChanged(position);
        });
        Glide.with(holder.familyLogo.getContext())
                .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + familyToBeLinked.getLogo())
                .apply(Utilities.getCurvedRequestOptions())
                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .placeholder(R.drawable.avatar_male)
                .into(holder.familyLogo);
    }

    @Override
    public int getItemCount() {
        return families.size();
    }

    public JsonArray getSelectedFamiliesForLinking() {
        JsonArray idsForLinking = new JsonArray();
        tempFamilies.clear();
        for (Family familyForLinking : families) {
            if (familyForLinking.isDevSelected()) {
                idsForLinking.add(familyForLinking.getId());
                tempFamilies.add(familyForLinking);
            }
        }
        return idsForLinking;
    }
    public List<Family> getSelectedTempFamiliesForLinking() {
        return tempFamilies;
    }
    private String getFirstLetterCapitalized(String string) {
        if (string.length() == 0)
            return "";
        StringBuilder sb = new StringBuilder(string);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.familyLogo)
        ImageView familyLogo;
        @BindView(R.id.familyName)
        TextView familyName;
        @BindView(R.id.createdBy)
        TextView createdBy;
        @BindView(R.id.locationIcon)
        ImageView locationIcon;
        @BindView(R.id.familyLocation)
        TextView familyLocation;
        @BindView(R.id.linkFamily)
        MaterialButton linkFamily;
        @BindView(R.id.familyLinked)
        MaterialButton familyLinked;
        @BindView(R.id.linkingActionNotNeeded)
        MaterialButton linkingActionNotNeeded;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
