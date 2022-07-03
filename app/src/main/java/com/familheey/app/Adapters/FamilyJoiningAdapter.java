package com.familheey.app.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Interfaces.FamilyJoiningListener;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Models.Response.Family.JOIN;
import static com.familheey.app.Models.Response.Family.JOINED;
import static com.familheey.app.Models.Response.Family.PENDING;
import static com.familheey.app.Models.Response.Family.PRIVATE;
import static com.familheey.app.Models.Response.Family.REJECTED;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class FamilyJoiningAdapter extends RecyclerView.Adapter<FamilyJoiningAdapter.ViewHolder> {


    private final ArrayList<Family> families;
    private final FamilyJoiningListener mListener;

    public FamilyJoiningAdapter(FamilyJoiningListener mListener, ArrayList<Family> families) {
        this.families = families;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_family_search, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Family family = families.get(position);
        holder.familyName.setText(family.getGroupName());
        holder.createdBy.setText("" + family.getSimilarFamilyCreatedBy());
        holder.familyType.setText("" + family.getGroupType());
        holder.familyLocation.setText(family.getBaseRegion());

        if (family.getMembersCount() == null) {
            holder.labelMembersCount.setVisibility(View.GONE);
            holder.membersCount.setVisibility(View.GONE);
        } else {
            holder.membersCount.setText(family.getMembersCount());
            if (Integer.parseInt(family.getMembersCount()) == 0) {
                holder.labelMembersCount.setVisibility(View.GONE);
                holder.membersCount.setVisibility(View.GONE);
            } else {
                holder.labelMembersCount.setVisibility(View.VISIBLE);
                holder.membersCount.setVisibility(View.VISIBLE);
            }
        }

        if (family.getKnownCount() == null) {
            holder.labeleventsCount.setVisibility(View.GONE);
            holder.eventsCount.setVisibility(View.GONE);
        } else {
            holder.eventsCount.setText(family.getKnownCount());
            if (Integer.parseInt(family.getKnownCount()) == 0) {
                holder.labeleventsCount.setVisibility(View.GONE);
                holder.eventsCount.setVisibility(View.GONE);
            } else {
                holder.labeleventsCount.setVisibility(View.VISIBLE);
                holder.eventsCount.setVisibility(View.VISIBLE);
            }
        }

        switch (family.getSimilarFamilyJoiningStatus()) {
            case JOINED:
                holder.joinFamily.setText("Member");
                break;
            case PRIVATE:
                holder.joinFamily.setText("Private");
                break;
            case PENDING:
                holder.joinFamily.setText("Pending");
                break;
            case REJECTED:
            case JOIN:
                holder.joinFamily.setText("Join");
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), FamilyDashboardActivity.class);
            intent.putExtra(DATA, family);
            holder.itemView.getContext().startActivity(intent);
        });

        if(family.getLogo()!=null){
            Glide.with(holder.familyLogo.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + family.getLogo())
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.family_logo)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.familyLogo);
        }else {
            Glide.with(holder.familyLogo.getContext())
                    .load(R.drawable.family_logo)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.familyLogo);
        }
    }

    @Override
    public int getItemCount() {
        return families.size();
    }

    public ArrayList<Family> getJoinedFamilies() {
        ArrayList<Family> selectedFamilies = new ArrayList<>();
        for (int i = 0; i < families.size(); i++) {
            if (families.get(i).isDevSelected())
                selectedFamilies.add(families.get(i));
        }
        return selectedFamilies;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.familyLogo)
        ImageView familyLogo;
        @BindView(R.id.familyName)
        TextView familyName;
        @BindView(R.id.createdBy)
        TextView createdBy;
        @BindView(R.id.familyType)
        TextView familyType;
        @BindView(R.id.familyLocation)
        TextView familyLocation;
        @BindView(R.id.labelMembersCount)
        TextView labelMembersCount;
        @BindView(R.id.membersCount)
        TextView membersCount;
        @BindView(R.id.labeleventsCount)
        TextView labeleventsCount;
        @BindView(R.id.eventsCount)
        TextView eventsCount;
        @BindView(R.id.join)
        MaterialButton joinFamily;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.join)
        public void onViewClicked(View v) {
            Family family = families.get(getAdapterPosition());
            switch (family.getSimilarFamilyJoiningStatus()) {
                case JOINED:
                case PRIVATE:
                case PENDING:
                    break;
                case REJECTED:
                case JOIN:
                    mListener.requestFamilyJoining(families.get(getAdapterPosition()));
                    break;
            }
        }
    }
}
