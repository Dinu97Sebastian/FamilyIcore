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
import com.familheey.app.Fragments.SearchPeopleFragment;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.PeopleSearchModal;
import com.familheey.app.R;
import com.familheey.app.Topic.CreateTopic;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class PeopleSearchAdapter extends RecyclerView.Adapter<PeopleSearchAdapter.ViewHolder> {

    ViewHolder viewHolder;
    private final ArrayList<PeopleSearchModal> peopleSearchArrayList;
    private final OnPeopleJoinInteraction mListener;
    private boolean hideAddToTopics = false;

    public static String validateString(String text) {
        if (text == null || text.isEmpty() || text.contains("null")) {
            return "";

        }
        return text;
    }

    @Override
    public int getItemCount() {
        return peopleSearchArrayList.size();
    }

    public PeopleSearchAdapter(OnPeopleJoinInteraction mListener, ArrayList<PeopleSearchModal> peopleSearchArrayList, SearchPeopleFragment searchPeopleFragment) {
        this.peopleSearchArrayList = peopleSearchArrayList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_people_search, parent, false);
        viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PeopleSearchModal peopleSearchModal = peopleSearchArrayList.get(position);
        holder.userName.setText(peopleSearchModal.getFullName());
        String location = validateString(peopleSearchModal.getLocation());
        holder.userLocation.setText(location);
        /*
        * megha(29/11/2021), removed the "unknown location" from profile ->myConnections*/
        if (peopleSearchModal.getLocation()==null){
            holder.userLocation.setVisibility(View.GONE);
            holder.locationIcon.setVisibility(View.GONE);
        }
        if (hideAddToTopics)
            holder.addToTopic.setVisibility(View.INVISIBLE);
        else holder.addToTopic.setVisibility(View.VISIBLE);
        if (peopleSearchModal.getMutualfamilycount() == null) {
            holder.eventsCount.setVisibility(View.INVISIBLE);
            holder.labeleventsCount.setVisibility(View.INVISIBLE);
        } else {
            holder.eventsCount.setText(peopleSearchModal.getMutualfamilycount());
            if (Integer.parseInt(peopleSearchModal.getMutualfamilycount()) == 0) {
                holder.eventsCount.setVisibility(View.INVISIBLE);
                holder.labeleventsCount.setVisibility(View.INVISIBLE);
            } else {
                holder.eventsCount.setVisibility(View.VISIBLE);
                holder.labeleventsCount.setVisibility(View.VISIBLE);
            }
        }

        if (holder.eventsCount.getVisibility() == View.VISIBLE || peopleSearchModal.getFamilycount() == null) {
            holder.membersCount.setVisibility(View.GONE);
            holder.labelMembersCount.setVisibility(View.GONE);
        } else {
            holder.membersCount.setText(peopleSearchModal.getFamilycount());
            if (Integer.parseInt(peopleSearchModal.getFamilycount()) == 0) {
                holder.membersCount.setVisibility(View.GONE);
                holder.labelMembersCount.setVisibility(View.GONE);
            } else {
                holder.membersCount.setVisibility(View.VISIBLE);
                holder.labelMembersCount.setVisibility(View.VISIBLE);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            FamilyMember familyMember = new FamilyMember();
            familyMember.setUserId(peopleSearchArrayList.get(position).getId());
            Intent profileIntent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
            profileIntent.putExtra(DATA, familyMember);
            profileIntent.putExtra(Constants.Bundle.FOR_EDITING, false);
            holder.itemView.getContext().startActivity(profileIntent);
        });
        holder.addToFamily.setOnClickListener(view -> mListener.onUserSelected(peopleSearchArrayList.get(position)));
        if (peopleSearchModal.getPropic() != null) {
            Glide.with(holder.userProfileImage.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + peopleSearchModal.getPropic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.userProfileImage);
        } else {
            Glide.with(holder.userProfileImage.getContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.userProfileImage);
        }

        if (peopleSearchModal.getLoggedUserFamilyCount() != null && (Integer.parseInt(peopleSearchModal.getLoggedUserFamilyCount()) > 0)) {
            holder.addToFamily.setVisibility(View.VISIBLE);
        } else {
            holder.addToFamily.setVisibility(View.INVISIBLE);
        }

        holder.addToTopic.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), CreateTopic.class);
            intent.putExtra(DATA, peopleSearchModal.getId() + "");
            holder.itemView.getContext().startActivity(intent);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userProfileImage)
        ImageView userProfileImage;
        @BindView(R.id.locationIcon)
        ImageView locationIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.userLocation)
        TextView userLocation;
        @BindView(R.id.membersCount)
        TextView membersCount;
        @BindView(R.id.eventsCount)
        TextView eventsCount;
        @BindView(R.id.addToFamily)
        MaterialButton addToFamily;
        @BindView(R.id.labelMembersCount)
        TextView labelMembersCount;
        @BindView(R.id.labeleventsCount)
        TextView labeleventsCount;
        @BindView(R.id.addToTopic)
        ImageView addToTopic;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnPeopleJoinInteraction {
        void onUserSelected(PeopleSearchModal user);
    }

    public void hideAddToTopics(boolean visibility) {
        hideAddToTopics = visibility;
    }
}
