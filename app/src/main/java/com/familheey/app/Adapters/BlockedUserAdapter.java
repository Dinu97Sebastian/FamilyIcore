package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class BlockedUserAdapter extends RecyclerView.Adapter<BlockedUserAdapter.ViewHolder> implements RetrofitListener {


    private final List<FamilyMember> familyMembers;
    private final OnUserUnblockedListener mListener;

    public BlockedUserAdapter(OnUserUnblockedListener mListener, List<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_blocked_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FamilyMember familyMember = familyMembers.get(position);
        // Update family member json
        if (familyMember.getProPic() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + familyMember.getProPic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.avatar_male)
                    .into(holder.memberImage);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.memberImage);
        }
        holder.memberName.setText(familyMember.getFullName());
        holder.memberRole.setText(familyMember.getRelationShip());
        if (familyMember.getRelationShip() != null)
            holder.memberRole.setText(familyMember.getRelationShip());
        else holder.memberRole.setText("");
        holder.memberType.setText(familyMember.getUserType());
        if (familyMember.getDevEnabled())
            holder.unblock.setText(holder.itemView.getContext().getString(R.string.tick_mark));
        else holder.unblock.setText("Unblock");
    }

    @Override
    public int getItemCount() {
        return familyMembers.size();
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        mListener.onUserUnblocked(apiCallbackParams.getFamilyMember());
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        for (int i = 0; i < familyMembers.size(); i++) {
            if (familyMembers.get(i).getUserId().equals(apiCallbackParams.getFamilyMember().getUserId())) {
                familyMembers.get(i).setDevEnabled(false);
            }
        }
        notifyDataSetChanged();
    }

    private void unBlockUser(FamilyMember familyMember) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", familyMember.getGroupMapId());
        jsonObject.addProperty("user_id", familyMember.getUserId());
        jsonObject.addProperty("group_id", familyMember.getGroupId());
        jsonObject.addProperty("is_blocked", "false");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(FamilheeyApplication.getInstance());
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamilyMember(familyMember);
        apiServiceProvider.updateUserRestrictionStatus(jsonObject, apiCallbackParams, this);
    }

    public interface OnUserUnblockedListener {
        void onUserUnblocked(FamilyMember user);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.memberImage)
        ImageView memberImage;
        @BindView(R.id.memberName)
        TextView memberName;
        @BindView(R.id.memberRole)
        TextView memberRole;
        @BindView(R.id.memberLocation)
        TextView memberLocation;
        @BindView(R.id.memberOptions)
        ImageView memberOptions;
        @BindView(R.id.memberTypeBackground)
        ImageView memberTypeBackground;
        @BindView(R.id.memberType)
        TextView memberType;
        @BindView(R.id.unblock)
        MaterialButton unblock;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this::onClick);
            unblock.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.unblock:
                    if (familyMembers.get(getAdapterPosition()).getDevEnabled())
                        return;
                    familyMembers.get(getAdapterPosition()).setDevEnabled(true);
                    notifyItemChanged(getAdapterPosition());
                    unBlockUser(familyMembers.get(getAdapterPosition()));
            }
        }
    }
}