package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Interfaces.FamilyDashboardListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.MemberRequests;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class UserPendingRequestAdapter extends RecyclerView.Adapter<UserPendingRequestAdapter.ViewHolder> {

    private ArrayList<MemberRequests> memberRequestsArrayList;
    private FamilyDashboardListener mListener;

    public UserPendingRequestAdapter(FamilyDashboardListener mListener, ArrayList<MemberRequests> memberRequestsArrayList) {
        this.memberRequestsArrayList = memberRequestsArrayList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_user_pending_request, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemberRequests memberRequests = memberRequestsArrayList.get(position);
        holder.userName.setText(memberRequests.getFullName());
        if (memberRequests.getBaseRegion() != null) {
            holder.locationIcon.setVisibility(View.VISIBLE);
            holder.userLocation.setVisibility(View.VISIBLE);
            holder.userLocation.setText(memberRequests.getLocation());
        } else {
            holder.locationIcon.setVisibility(View.INVISIBLE);
            holder.userLocation.setVisibility(View.INVISIBLE);
        }
        Glide.with(holder.itemView.getContext())
                .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + memberRequests.getProPic())
                .apply(Utilities.getCurvedRequestOptions())
                .placeholder(R.drawable.avatar_male)
                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .into(holder.userProfileImage);
        holder.cancel.setOnClickListener(view -> {
            if (mListener != null)
                mListener.showProgressDialog();
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(holder.itemView.getContext());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", memberRequests.getId().toString());
            ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
            apiCallbackParams.setRequest(memberRequests);
            apiServiceProvider.deletePendingRequest(jsonObject, apiCallbackParams, new RetrofitListener() {
                @Override
                public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                    memberRequestsArrayList.remove(memberRequests);
                    notifyDataSetChanged();
                    if (mListener != null)
                        mListener.hideProgressDialog();
                }

                @Override
                public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                    if (mListener != null)
                        mListener.showErrorDialog(Constants.SOMETHING_WRONG);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return memberRequestsArrayList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userProfileImage)
        ImageView userProfileImage;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.locationIcon)
        ImageView locationIcon;
        @BindView(R.id.userLocation)
        TextView userLocation;
        @BindView(R.id.cancel)
        MaterialButton cancel;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
