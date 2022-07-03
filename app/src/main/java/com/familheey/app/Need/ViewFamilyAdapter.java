package com.familheey.app.Need;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class ViewFamilyAdapter extends RecyclerView.Adapter<ViewFamilyAdapter.ViewHolder> {

    private List<Group> families;

    ViewFamilyAdapter(List<Group> families) {
        this.families = families;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_view_family, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group family = families.get(position);
        if (family.getBase_region() != null) {
            holder.familyLocation.setText(family.getBase_region());
            holder.familyLocation.setVisibility(View.VISIBLE);
        } else holder.familyLocation.setVisibility(View.INVISIBLE);
        holder.familyName.setText(family.getGroup_name());
        if (family.getLogo() != null) {
            Glide.with(holder.familyLogo.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + family.getLogo())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.family_logo)
                    .into(holder.familyLogo);
        } else {
            Glide.with(holder.familyLogo.getContext())
                    .load(R.drawable.family_logo)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.familyLogo);
        }
        holder.itemView.setOnClickListener(v -> {
            if (family.getId() == null)
                return;
            Intent intent = new Intent(holder.itemView.getContext(), FamilyDashboardActivity.class);
            Family goToFamily = new Family();
            goToFamily.setId(family.getId());
            intent.putExtra(DATA, goToFamily);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (families != null)
            return families.size();
        else return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.familyLogo)
        ImageView familyLogo;
        @BindView(R.id.familyName)
        TextView familyName;
        @BindView(R.id.familyLocation)
        TextView familyLocation;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
