package com.familheey.app.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Models.FamilyJoiningRequest;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class FamilyInvitationAdapter extends RecyclerView.Adapter<FamilyInvitationAdapter.ViewHolder> {


    private final List<FamilyJoiningRequest> familyMembers;
    private final OnInvitationSelectedListener mListener;

    public FamilyInvitationAdapter(OnInvitationSelectedListener mListener, List<FamilyJoiningRequest> familyMembers) {
        this.familyMembers = familyMembers;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_join_family_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FamilyJoiningRequest familyJoiningRequest = familyMembers.get(position);
        if(familyJoiningRequest.getPropic()!=null){
            Glide.with(holder.itemView.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + familyJoiningRequest.getPropic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.avatar_male)
                    .into(holder.profileImage);
        }else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.profileImage);
        }
        holder.requestedFamily.setText(familyJoiningRequest.getFullName());
        holder.requestedBy.setText("invited you to " + familyJoiningRequest.getGroupName());
        holder.accept.setOnClickListener(v -> mListener.onInvitationAccepted(familyJoiningRequest));
        holder.reject.setOnClickListener(v -> mListener.onInvitationRejected(familyJoiningRequest));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (familyJoiningRequest.getFromId() == null)
                    return;
                Intent intent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
                FamilyMember familyMember = new FamilyMember();
                familyMember.setUserId(Integer.parseInt(familyJoiningRequest.getFromId()));
                familyMember.setProPic(familyJoiningRequest.getPropic());
                intent.putExtra(Constants.Bundle.DATA, familyMember);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return familyMembers.size();
    }

    public interface OnInvitationSelectedListener {

        void onInvitationAccepted(FamilyJoiningRequest familyJoiningRequest);

        void onInvitationRejected(FamilyJoiningRequest familyJoiningRequest);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.profileImage)
        ImageView profileImage;
        @BindView(R.id.requestedFamily)
        TextView requestedFamily;
        @BindView(R.id.accept)
        ImageView accept;
        @BindView(R.id.reject)
        ImageView reject;
        @BindView(R.id.requestedBy)
        TextView requestedBy;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.reject:
                    mListener.onInvitationRejected(familyMembers.get(getAdapterPosition()));
                    break;
                default:
                    mListener.onInvitationAccepted(familyMembers.get(getAdapterPosition()));
            }
        }
    }
}