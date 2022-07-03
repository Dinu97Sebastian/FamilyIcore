package com.familheey.app.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.MemberAdded;
import com.familheey.app.Models.Response.User;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class ListMemberAdapter extends RecyclerView.Adapter<ListMemberAdapter.ViewHolder> {

    private final List<User> users;
    private final Family family;
    private final Context context;

    public ListMemberAdapter(List<User> users, Family family, Context context) {
        this.users = users;
        this.family = family;
        this.context = context;
    }

    @NotNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.listmember_itemcard, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.userName.setText(user.getFullName());
        holder.familyLocation.setText(user.getLocation());
        holder.addToFamily.setOnClickListener(v -> {
            if (user.getEXISTS() != null && user.getEXISTS()) {
                Toast.makeText(context, "Already added", Toast.LENGTH_SHORT).show();
                return;
            } else if (user.getINVITATIONSTATUS() != null && user.getINVITATIONSTATUS()) {
                Toast.makeText(context, "Pending", Toast.LENGTH_SHORT).show();
                return;
            }
            ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
            apiCallbackParams.setUser(user);
            apiCallbackParams.setPosition(position);
            callApi(apiCallbackParams, holder.progressbar, user.getId());
            users.get(position).setUpdating(true);
            notifyDataSetChanged();
        });
        if(user.getPropic()!=null){
            Glide.with(holder.familyLogo.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + user.getPropic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.familyLogo);
        }else {
            Glide.with(holder.familyLogo.getContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.familyLogo);
        }

        holder.familyLogo.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setId(Integer.parseInt(SharedPref.getUserRegistration().getId()));
            familyMember.setUserId(user.getId());
            familyMember.setProPic(user.getPropic());
            intent.putExtra(DATA, familyMember);
            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, holder.familyLogo, "profile");
            context.startActivity(intent, options.toBundle());
        });

        // ADDED NULL CHECK. CHECK WHETHER THIS CONDITIONS ARE CORRECT WITH ANIL!! HEHE
        if (user.isUpdating()) {
            holder.addToFamily.setVisibility(View.INVISIBLE);
            holder.progressbar.setVisibility(View.VISIBLE);
            return;
        } else {
            holder.addToFamily.setVisibility(View.VISIBLE);
            holder.progressbar.setVisibility(View.INVISIBLE);
        }

        if (user.getEXISTS() != null && user.getEXISTS())
            holder.addToFamily.setText("Member");
        else if (user.getINVITATIONSTATUS() != null && user.getINVITATIONSTATUS())
            holder.addToFamily.setText("Pending");
        else holder.addToFamily.setText("Add");
    }

    private void callApi(ApiCallbackParams apiCallbackParams, ProgressBar progressbar, int id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", String.valueOf(id));
        jsonObject.addProperty("from_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("groupId", family.getId().toString());

        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(context);
        apiServiceProvider.addToFamily(jsonObject, apiCallbackParams, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                MemberAdded memberAdded = GsonUtils.getInstance().getGson().fromJson(responseBodyString, MemberAdded.class);
                if (memberAdded.getStatus() != null && memberAdded.getStatus().equalsIgnoreCase("pending")) {
                    users.get(apiCallbackParams.getPosition()).setEXISTS(false);
                    users.get(apiCallbackParams.getPosition()).setINVITATIONSTATUS(true);
                } else {
                    users.get(apiCallbackParams.getPosition()).setEXISTS(true);
                }
                users.get(apiCallbackParams.getPosition()).setUpdating(false);
                notifyDataSetChanged();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                Toast.makeText(context, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                users.get(apiCallbackParams.getPosition()).setUpdating(false);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    static
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.familyLogo)
        ImageView familyLogo;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.familyLocation)
        TextView familyLocation;
        @BindView(R.id.familiesCount)
        TextView familiesCount;
        @BindView(R.id.membersCount)
        TextView membersCount;
        @BindView(R.id.addToFamily)
        MaterialButton addToFamily;
        @BindView(R.id.progressBar4)
        ProgressBar progressbar;
        @BindView(R.id.locationIcon)
        ImageView locationIcon;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
