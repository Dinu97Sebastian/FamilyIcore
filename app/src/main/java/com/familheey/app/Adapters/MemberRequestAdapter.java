package com.familheey.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Interfaces.FamilyRequestInterface;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.MemberRequests;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class MemberRequestAdapter extends RecyclerView.Adapter<MemberRequestAdapter.ViewHolder> {

    private ArrayList<MemberRequests> memberRequestsArrayList;
    private Context activity;
    private boolean isAdmin = true;
    private FamilyRequestInterface familyRequestInterface;
public MemberRequestStatus memberRequestStatus;
    String reqStatus = "";
    JSONObject jsonObject = null;
    RetrofitListener retrofitListener = new RetrofitListener() {
        @Override
        public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
            if (apiFlag == Constants.ApiFlags.MEMBER_REQUEST_ACTION) {
                MemberRequests memberRequests = apiCallbackParams.getMemberRequests();
                memberRequestsArrayList.remove(memberRequests);
                try {
                    jsonObject = new JSONObject(responseBodyString);
                    JSONArray arr = jsonObject.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++)
                    {
                         reqStatus = arr.getJSONObject(i).getString("status");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                notifyDataSetChanged();
                familyRequestInterface.onClickedAccept(reqStatus);

            }
        }
        @Override
        public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {

        }
    };


    public MemberRequestAdapter(ArrayList<MemberRequests> memberRequestsArrayList, Context activity, FamilyRequestInterface familyRequestInterface) {
        this.memberRequestsArrayList = memberRequestsArrayList;
        this.activity = activity;
        this.familyRequestInterface = familyRequestInterface;
    }

    @NonNull
    @Override
    public MemberRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.member_request, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemberRequests memberRequests = memberRequestsArrayList.get(position);
        holder.requestMessage.setText("Requested : " + memberRequests.getRequestedBy());
        //The other type is "fetch_link"
        if (memberRequests.getType().equalsIgnoreCase("request")) {
            holder.requestMessage.setText(memberRequests.getFullName() + " has requested to join this family ! \nAccept request ?");
        } else {
            holder.requestMessage.setText(memberRequests.getFullName() + " has requested to link \n" + memberRequests.getGroupName() + " with this family!");
        }
        if(memberRequests.getProPic()!=null){
            Glide.with(holder.itemView.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + memberRequests.getProPic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.requesterImage);
        }else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.requesterImage);
        }
        holder.accept.setOnClickListener(view -> {
            if (!isAdmin) {
                Snackbar.make(holder.itemView, "You must have sufficient permission to perform this action", Snackbar.LENGTH_SHORT).show();
                return;
            }
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(activity);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", memberRequests.getId().toString());
            jsonObject.addProperty("type", memberRequests.getType());
            jsonObject.addProperty("status", "accepted");
            jsonObject.addProperty("responded_by", SharedPref.getUserRegistration().getId());
            ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
            apiCallbackParams.setRequest(memberRequests);
            apiServiceProvider.memerRequestAction(jsonObject, apiCallbackParams, retrofitListener);

        });

        holder.reject.setOnClickListener(view -> {
            if (!isAdmin) {
                Snackbar.make(holder.itemView, "You must have sufficient permission to perform this action", Snackbar.LENGTH_SHORT).show();
                return;
            }
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(activity);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", memberRequests.getId().toString());
            jsonObject.addProperty("type", memberRequests.getType());
            jsonObject.addProperty("status", "rejected");
            jsonObject.addProperty("responded_by", SharedPref.getUserRegistration().getId());
            ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
            apiCallbackParams.setRequest(memberRequests);
            apiServiceProvider.memerRequestAction(jsonObject, apiCallbackParams, retrofitListener);


        });

        holder.itemView.setOnClickListener(v -> {
            if (memberRequests.getUserId() == null)
                return;
            Intent userIntent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setUserId(Integer.parseInt(memberRequests.getUserId()));
            familyMember.setId(Integer.parseInt(memberRequests.getUserId()));
            if (memberRequests.getProPic() != null) {
                familyMember.setProPic(memberRequests.getProPic());
                userIntent.putExtra(DATA, familyMember);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) activity, holder.requesterImage, "profile");
                activity.startActivity(userIntent, options.toBundle());
            } else {
                userIntent.putExtra(DATA, familyMember);
                holder.itemView.getContext().startActivity(userIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberRequestsArrayList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.requestMessage)
        TextView requestMessage;
        @BindView(R.id.reject)
        Button reject;
        @BindView(R.id.accept)
        Button accept;
        @BindView(R.id.requesterImage)
        ImageView requesterImage;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setAdminStatus(boolean isAdmin) {
        this.isAdmin = isAdmin;
        notifyDataSetChanged();
    }
    public interface MemberRequestStatus{
        void requestStatus(String status);
    }
}
