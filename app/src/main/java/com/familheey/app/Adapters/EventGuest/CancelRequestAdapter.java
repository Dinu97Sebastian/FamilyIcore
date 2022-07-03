package com.familheey.app.Adapters.EventGuest;

import android.content.Context;
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
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class CancelRequestAdapter extends RecyclerView.Adapter<CancelRequestAdapter.ViewHolder> {

    private ArrayList<Family> familySearchArrayList;
    private Family family = new Family();

    private Context context;

    public CancelRequestAdapter(Context context, ArrayList<Family> familySearchArrayList) {
        this.familySearchArrayList = familySearchArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_family_cancel_request, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Family familySearchModal = familySearchArrayList.get(position);
        holder.createdBy.setText(familySearchModal.getGroupName());
        holder.familyLocation.setText(familySearchModal.getBaseRegion());
        holder.familyType.setText(familySearchModal.getGroupCategory());
        holder.familyName.setText(familySearchModal.getCreatedBy());
        if (familySearchModal.getLogo() != null) {
            Glide.with(holder.familyLogo.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + familySearchModal.getLogo())
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
        holder.join.setOnClickListener(view -> {
            showConfirmDialog(context, familySearchModal, holder, position);
        });
        holder.itemView.setOnClickListener(view -> {
            family.setId(familySearchArrayList.get(position).getGroupId());
            Intent intent = new Intent(context, FamilyDashboardActivity.class);
            intent.putExtra(DATA, family);
            context.startActivity(intent);
        });
    }

    private void showConfirmDialog(Context context, Family familySearchModal, ViewHolder holder, int position) {

        SweetAlertDialog cancelRequestDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Cancel?")
                .setContentText("Do you really want to cancel your request?")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();

                })
                .setConfirmClickListener(sDialog -> {
                    ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(holder.itemView.getContext());
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", familySearchModal.getId().toString());
                    apiServiceProvider.deletePendingRequest(jsonObject, null, new RetrofitListener() {
                        @Override
                        public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                            familySearchArrayList.remove(position);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {

                        }
                    });
                    sDialog.dismissWithAnimation();

                });
        cancelRequestDialog.show();
        Utilities.addPositiveButtonMargin(cancelRequestDialog);
        Utilities.addNegativeButtonMargin(cancelRequestDialog);


    }

    @Override
    public int getItemCount() {
        return familySearchArrayList.size();
    }


    static
    class ViewHolder extends RecyclerView.ViewHolder {
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
        MaterialButton join;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
