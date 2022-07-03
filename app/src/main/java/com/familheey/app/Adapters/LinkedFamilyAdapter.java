package com.familheey.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class LinkedFamilyAdapter extends RecyclerView.Adapter<LinkedFamilyAdapter.ViewHolder> implements RetrofitListener {

    private ArrayList<Family> families;
    private OnLinkedFamilySelectedListener mListener;
    private ProgressListener progressListener;
    private SweetAlertDialog confirmUnlinkDialog;
    private boolean isAdmin = true;

    public LinkedFamilyAdapter(OnLinkedFamilySelectedListener mListener, ProgressListener progressListener, ArrayList<Family> families) {
        this.families = families;
        this.mListener = mListener;
        this.progressListener = progressListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_linked_family, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Family familyToBeLinked = families.get(position);
        holder.eventsCount.setVisibility(View.INVISIBLE);
        holder.labelEventsCount.setVisibility(View.INVISIBLE);
        holder.createdBy.setText(familyToBeLinked.getRequestedByName());
        holder.familyName.setText(familyToBeLinked.getGroupName());
        holder.familyType.setText(familyToBeLinked.getGroupCategory());
        holder.eventsCount.setText(familyToBeLinked.getMembersCount());
        holder.membersCount.setText(familyToBeLinked.getMembersCount());
        holder.familyLocation.setText(familyToBeLinked.getBaseRegion());

        if (familyToBeLinked.getStatus().equalsIgnoreCase("accepted")) {
            holder.unLinkFamily.setText("Unlink");
        } else if (familyToBeLinked.getStatus().equalsIgnoreCase("unlinked"))
            holder.unLinkFamily.setText("Link");
        else
            holder.unLinkFamily.setText("Pending");

        holder.itemView.setOnClickListener(v -> {
            mListener.onLinkedFamilySelected(familyToBeLinked);
        });

        holder.unLinkFamily.setOnClickListener(v -> {
            if (!isAdmin) {
                Toast.makeText(holder.itemView.getContext(), "You must be an admin to perform this action", Toast.LENGTH_SHORT).show();
                return;
            }
            if (familyToBeLinked.getStatus().equalsIgnoreCase("accepted"))
                unLinkFamily(familyToBeLinked, holder.itemView.getContext());
            else if (familyToBeLinked.getStatus().equalsIgnoreCase("unlinked"))
                linkFamily(familyToBeLinked);
        });

        Glide.with(holder.familyLogo.getContext())
                .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + familyToBeLinked.getLogo())
                .apply(Utilities.getCurvedRequestOptions())
                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .placeholder(R.drawable.family_logo)
                .into(holder.familyLogo);
    }

    @Override
    public int getItemCount() {
        return families.size();
    }

    private String getFirstLetterCapitalized(String string) {
        if (string.length() == 0)
            return "";
        StringBuilder sb = new StringBuilder(string);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        progressListener.hideProgressDialog();
        try {
            JSONObject jsonObject = new JSONObject(responseBodyString);
            apiCallbackParams.getFamily().setStatus(jsonObject.getJSONObject("data").getString("status"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mListener.onFamilyLinkingStatusChanged(apiCallbackParams.getFamily());
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        progressListener.hideProgressDialog();
    }

    private void unLinkFamily(Family family, Context context) {
        confirmUnlinkDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Do you want to unlink " + family.getGroupName() + " ?")
                .setConfirmText("Yes")
                .setCancelText("No");
        confirmUnlinkDialog.show();
        Utilities.addNegativeButtonMargin(confirmUnlinkDialog);
        Utilities.addPositiveButtonMargin(confirmUnlinkDialog);
        confirmUnlinkDialog.setConfirmClickListener(sDialog -> {
            confirmUnlinkDialog.dismiss();
            progressListener.showProgressDialog();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", family.getId().toString());
            jsonObject.addProperty("status", "unlinked");//Status either "unlinked" or "accepted"
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(FamilheeyApplication.getInstance());
            ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
            apiCallbackParams.setFamily(family);
            apiServiceProvider.requestUnlinkFamily(jsonObject, apiCallbackParams, LinkedFamilyAdapter.this);
        });
        confirmUnlinkDialog.setCancelClickListener(SweetAlertDialog::cancel);
    }

    private void linkFamily(Family family) {
        progressListener.showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", family.getId().toString());
        jsonObject.addProperty("status", "accepted");//Status either "unlinked" or "accepted"
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(FamilheeyApplication.getInstance());
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamily(family);
        apiServiceProvider.requestUnlinkFamily(jsonObject, apiCallbackParams, this);
    }

    public interface OnLinkedFamilySelectedListener {
        void onLinkedFamilySelected(Family family);

        void onFamilyLinkingStatusChanged(Family family);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.familyLogo)
        ImageView familyLogo;
        @BindView(R.id.familyName)
        TextView familyName;
        @BindView(R.id.createdBy)
        TextView createdBy;
        @BindView(R.id.familyType)
        TextView familyType;
        @BindView(R.id.locationIcon)
        ImageView locationIcon;
        @BindView(R.id.familyLocation)
        TextView familyLocation;
        @BindView(R.id.membersCount)
        TextView membersCount;
        @BindView(R.id.eventsCount)
        TextView eventsCount;
        @BindView(R.id.unLinkFamily)
        MaterialButton unLinkFamily;
        @BindView(R.id.labeleventsCount)
        TextView labelEventsCount;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setAdminStatus(boolean isAdmin) {
        this.isAdmin = isAdmin;
        notifyDataSetChanged();
    }
}