package com.familheey.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class SelectMemberAdapter extends RecyclerView.Adapter<SelectMemberAdapter.ViewHolder> {


    private final List<FamilyMember> familyMembers;
    private final Context context;

    public SelectMemberAdapter(Context context, List<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_family_member_selection, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FamilyMember familyMember = familyMembers.get(position);
        // Update family member json
        if(familyMember.getProPic()!=null){
            Glide.with(context)
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + familyMember.getProPic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.memberImage);
        }else {
            Glide.with(context)
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.memberImage);
        }
        holder.memberName.setText(familyMember.getFullName());
        holder.memberType.setText(familyMember.getUserType());
        holder.memberLocation.setText(getMemberSinceAsFormatted(familyMember));
        if (familyMember.getDevEnabled())
            holder.select.setText("Selected");
        else holder.select.setText("Select");
        if (familyMember.getRelationShip() != null) {
            holder.memberRole.setVisibility(View.VISIBLE);
            holder.memberRole.setText(familyMember.getRelationShip());
        } else {
            holder.memberRole.setVisibility(View.INVISIBLE);
        }
        holder.location.setVisibility(View.INVISIBLE);
        holder.locationIcon.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return familyMembers.size();
    }

    private String getMemberSinceAsFormatted(FamilyMember familyMember) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = simpleDateFormat.parse(familyMember.getMemberSince());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            return "Member since " + sdf.format(date);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
            if (familyMember.getMemberSince() == null)
                return "Member since -";
            else return familyMember.getMemberSince();
        }
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
        @BindView(R.id.location)
        TextView location;
        @BindView(R.id.memberTypeBackground)
        ImageView memberTypeBackground;
        @BindView(R.id.memberType)
        TextView memberType;
        @BindView(R.id.select)
        Button select;
        @BindView(R.id.locationIcon)
        ImageView locationIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            select.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                default:
                    familyMembers.get(getAdapterPosition()).setDevEnabled(!familyMembers.get(getAdapterPosition()).getDevEnabled());
                    notifyItemChanged(getAdapterPosition());
            }
        }
    }
}