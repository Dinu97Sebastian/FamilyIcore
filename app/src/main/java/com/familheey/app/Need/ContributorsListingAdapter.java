package com.familheey.app.Need;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.skydoves.androidribbon.RibbonView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class ContributorsListingAdapter extends RecyclerView.Adapter<ContributorsListingAdapter.ViewHolder> {


    private List<Contributor> contributors;
    private Context context;
    private boolean isAdmin;
    private String type;
    private OnUserSupportListener mListener;

    ContributorsListingAdapter(Context context, List<Contributor> contributors, boolean isAdmin, String type, OnUserSupportListener mListener) {
        this.contributors = contributors;
        this.context = context;
        this.isAdmin = isAdmin;
        this.mListener = mListener;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_needs_contributor, parent, false);
        return new ViewHolder(listItem);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contributor contributor = contributors.get(position);

        if (contributor.getContributeUserId() == 2) {
            holder.txt_known_member.setVisibility(View.VISIBLE);
            holder.txt_known_member.setText("Non App Member");
        } else {
            holder.txt_known_member.setVisibility(View.GONE);
        }
        if (contributor.getContribution_count() == 1 && !contributor.isIs_acknowledge() && "fund".equals(type) && contributor.getContributeUserId().toString().equals(SharedPref.getUserRegistration().getId())) {
            holder.paynow.setVisibility(View.VISIBLE);
        } else {
            holder.paynow.setVisibility(View.GONE);
        }
        if (contributor.isIs_anonymous()) {
            if (isAdmin || contributor.getContributeUserId().toString().equals(SharedPref.getUserRegistration().getId())) {
                holder.txt_anonymous.setVisibility(View.VISIBLE);
                if (contributor.getContributeUserId() == 2) {
                    holder.name.setText(contributor.getPaid_user_name());
                } else {
                    holder.name.setText(contributor.getFullName());
                }
                holder.postedIn.setText(contributor.getLocation());
                if (contributor.getLocation() == null || contributor.getLocation().isEmpty())
                    holder.postedIn.setText("UnKnown");
            } else {
                holder.txt_anonymous.setVisibility(View.GONE);
                holder.name.setText("Anonymous");
                holder.postedIn.setText("-------------------");
            }
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.userProfileImage);
        } else {
            holder.postedIn.setText(contributor.getLocation());
            holder.txt_anonymous.setVisibility(View.GONE);

            if (contributor.getLocation() == null || contributor.getLocation().isEmpty())
                holder.postedIn.setText("UnKnown");
            if (contributor.getContributeUserId() == 2) {
                holder.name.setText(contributor.getPaid_user_name());
            } else {
                holder.name.setText(contributor.getFullName());
            }
            if (contributor.getPropic() != null) {
                Glide.with(holder.itemView.getContext())
                        .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + contributor.getPropic())
                        .apply(Utilities.getCurvedRequestOptions())
                        .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .placeholder(R.drawable.avatar_male)
                        .into(holder.userProfileImage);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.avatar_male)
                        .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(holder.userProfileImage);
            }
        }

        holder.itemsContributed.setText(contributor.getTotal_contribution().toString());
        if (contributor.getFormattedCreatedAt() != null && contributor.getFormattedCreatedAt().length() > 0) {
            holder.contributedOn.setText(contributor.getFormattedCreatedAt());
            holder.contributedOn.setVisibility(View.VISIBLE);
        } else {
            holder.contributedOn.setVisibility(View.GONE);
        }
        holder.call.setOnClickListener(v -> {
            if (contributor.getPhone() == null) {
                Toast.makeText(holder.itemView.getContext(), "User has not registered a phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contributor.getPhone(), null));
            holder.itemView.getContext().startActivity(intent);
        });
        holder.name.setOnClickListener(view -> {
            if (contributor.isIs_anonymous() && isAdmin)
                navigateProfile(position, holder);
            else if (!contributor.isIs_anonymous())
                navigateProfile(position, holder);
        });

        holder.userProfileImage.setOnClickListener(v -> {
            if (contributor.isIs_anonymous() && isAdmin)
                navigateProfile(position, holder);
            else if (!contributor.isIs_anonymous())
                navigateProfile(position, holder);
        });
        holder.itemView.setOnClickListener(view -> {
            if (contributor.getContribution_count() > 1) {
                mListener.onItemClick(contributor);
            }
        });
        if (contributor.getContribution_count() > 1) {
            holder.multiple.setVisibility(View.VISIBLE);
        } else {
            holder.multiple.setVisibility(View.GONE);
        }
        holder.thankyou.setOnClickListener(view -> mListener.onthankyouPost(contributors.get(position)));
        if (isAdmin && contributor.getContributeUserId() != 2) {
            holder.call.setVisibility(View.VISIBLE);
            if (!contributor.isIs_pending_thank_post()) {
                holder.thankyou.setVisibility(View.GONE);
                holder.cb_on.setVisibility(View.VISIBLE);
            } else {
                holder.cb_on.setVisibility(View.GONE);
                if (contributor.isIs_anonymous()) {
                    holder.thankyou.setVisibility(View.GONE);
                    if (!contributor.isIs_acknowledge()) {
                        holder.acknowledge.setVisibility(View.VISIBLE);
                    } else {
                        holder.acknowledge.setVisibility(View.GONE);
                    }
                } else {
                    holder.thankyou.setVisibility(View.VISIBLE);
                    holder.acknowledge.setVisibility(View.GONE);
                }
            }

        } else {
            holder.whole_view.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.cb_on.setVisibility(View.GONE);

            if (isAdmin && contributor.getContributeUserId() == 2) {
                holder.call.setVisibility(View.VISIBLE);
            } else {
                holder.call.setVisibility(View.GONE);
            }
            holder.thankyou.setVisibility(View.GONE);
            holder.acknowledge.setVisibility(View.GONE);
        }

        if (contributor.getContribution_count() == 1) {

            if (contributor.isIs_acknowledge()) {
                holder.ribbonLayout01.setRibbonHeader(new RibbonView.Builder(context)
                        .setText("Android-Ribbon")
                        .setRibbonBackgroundColor(Color.parseColor("#62FA2E"))
                        .setTextColor(Color.parseColor("#62FA2E"))
                        .setTextSize(10f)
                        .setRibbonRotation(45)
                        .build());
            } else {
                holder.ribbonLayout01.setRibbonHeader(new RibbonView.Builder(context)
                        .setText("Android-Ribbon")
                        .setRibbonBackgroundColor(Color.parseColor("#F8ED11"))
                        .setTextColor(Color.parseColor("#F8ED11"))
                        .setTextSize(10f)
                        .setRibbonRotation(45)
                        .build());
            }
        } else {
            holder.ribbonLayout01.setRibbonHeader(new RibbonView.Builder(context)
                    .setText("")
                    .setRibbonBackgroundColor(Color.parseColor("#66ffffff"))
                    .setTextSize(10f)
                    .setRibbonRotation(45)
                    .build());


        }
        holder.paynow.setOnClickListener(view ->
                mListener.onPaynow(contributor)
        );

        holder.acknowledge.setOnClickListener(view ->
                mListener.onAcknowledge(contributor)
        );

    }

    private void navigateProfile(int position, ViewHolder holder) {

        FamilyMember familyMember = new FamilyMember();
        familyMember.setId(contributors.get(position).getContributeUserId());
        familyMember.setUserId(contributors.get(position).getContributeUserId());
        familyMember.setProPic(contributors.get(position).getPropic());
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(Constants.Bundle.DATA, familyMember);
        intent.putExtra(Constants.Bundle.FOR_EDITING, true);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, holder.userProfileImage, "profile");
        context.startActivity(intent, options.toBundle());
    }

    @Override
    public int getItemCount() {
        return contributors.size();
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public interface OnUserSupportListener {
        void onthankyouPost(Contributor selectedContribution);

        void onPaynow(Contributor selectedContribution);

        void onItemClick(Contributor selectedContribution);

        void onAcknowledge(Contributor selectedContribution);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.whole_view)
        LinearLayout whole_view;
        @BindView(R.id.multiple)
        ImageView multiple;
        @BindView(R.id.userProfileImage)
        ImageView userProfileImage;
        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.txt_known_member)
        TextView txt_known_member;
        @BindView(R.id.postedIn)
        TextView postedIn;

        @BindView(R.id.txt_anonymous)
        TextView txt_anonymous;
        @BindView(R.id.contributedOn)
        TextView contributedOn;
        @BindView(R.id.itemsContributed)
        TextView itemsContributed;
        @BindView(R.id.itemStatus)
        TextView itemStatus;
        @BindView(R.id.call)
        MaterialButton call;
        @BindView(R.id.thankyou)
        MaterialButton thankyou;
        @BindView(R.id.cb_on)
        ImageView cb_on;
        @BindView(R.id.paynow)
        MaterialButton paynow;

        @BindView(R.id.acknowledge)
        MaterialButton acknowledge;

        @BindView(R.id.ribbonLayout01)
        com.skydoves.androidribbon.RibbonLayout ribbonLayout01;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}