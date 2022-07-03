package com.familheey.app.Adapters;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilySearchModal;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Activities.OtherUsersFamilyActivity.CONNECTIONS;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class OtherUserFamilyJoiningAdapter extends RecyclerView.Adapter<OtherUserFamilyJoiningAdapter.ViewHolder> implements RetrofitListener {


    private final List<Family> families;
    private final String joinerUserId;
    private final Context context;
    private final String joineeUserId;
    private final int type;

    public OtherUserFamilyJoiningAdapter(Context context, List<Family> families, String joinerUserId, String joineeUserId, int type) {
        this.families = families;
        this.joinerUserId = joinerUserId;
        this.joineeUserId = joineeUserId;
        this.context = context;
        this.type = type;
        initializeFamiliesAsNotAdded();
    }

    private void initializeFamiliesAsNotAdded() {
        for (Family family : families) {
            family.setAddedTogroup(false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_add_to_other_user_family, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Family family = families.get(position);
        holder.join.setOnClickListener(view -> {
            switch (family.getUserJoinedCalculated()) {
                case FamilySearchModal.JOIN:
                    break;
                case FamilySearchModal.REJECTED:
                    Toast.makeText(holder.join.getContext(), "Admin has rejected your family joining request", Toast.LENGTH_SHORT).show();
                    return;
                case FamilySearchModal.JOINED:
                    Toast.makeText(holder.join.getContext(), "Already joined!", Toast.LENGTH_SHORT).show();
                    return;
                case FamilySearchModal.PENDING:
                    Toast.makeText(holder.join.getContext(), "Already requested for joining!", Toast.LENGTH_SHORT).show();
                    return;
                case FamilySearchModal.PRIVATE:
                    Toast.makeText(holder.join.getContext(), "Only admin of this group can send you a joining request!", Toast.LENGTH_SHORT).show();
                    return;
                case FamilySearchModal.ACCEPT_INVITATION:
                    families.get(position).setAddedTogroup(true);
                    notifyItemChanged(position);
                    onInvitationAccept(position);
                    return;

            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", joinerUserId);
            jsonObject.addProperty("group_id", families.get(position).getId().toString());
            families.get(position).setAddedTogroup(true);
            notifyItemChanged(position);
            addToFamily(jsonObject, position);
        });
        holder.familyName.setText(families.get(position).getGroupName());
        if (families.get(position).getLogo() != null) {
            Glide.with(holder.familyLogo.getContext())
                    .load(IMAGE_BASE_URL + LOGO + families.get(position).getLogo())
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
        if (families.get(position).getMembersCount() != null) {
            holder.membersCount.setText(families.get(position).getMembersCount());
            if (families.get(position).getMembersCount().equalsIgnoreCase("0")) {
                holder.membersCount.setVisibility(View.INVISIBLE);
                holder.labelMembersCount.setVisibility(View.INVISIBLE);
            } else {
                holder.membersCount.setVisibility(View.VISIBLE);
                holder.labelMembersCount.setVisibility(View.VISIBLE);
            }
        } else {
            holder.membersCount.setVisibility(View.INVISIBLE);
            holder.labelMembersCount.setVisibility(View.INVISIBLE);
            holder.membersCount.setText("-");
        }

        if (families.get(position).getKnownCount() != null) {
            holder.eventsCount.setText(families.get(position).getKnownCount());
            if (families.get(position).getKnownCount().equalsIgnoreCase("0")) {
                holder.eventsCount.setVisibility(View.INVISIBLE);
                holder.labelEventsCount.setVisibility(View.INVISIBLE);
            } else {
                holder.eventsCount.setVisibility(View.VISIBLE);
                holder.labelEventsCount.setVisibility(View.VISIBLE);
            }
        } else {
            holder.eventsCount.setVisibility(View.INVISIBLE);
            holder.labelEventsCount.setVisibility(View.INVISIBLE);
            holder.eventsCount.setText("-");
        }

        if (families.get(position).getGroupCategory() != null)
            holder.familyCategory.setText(families.get(position).getGroupCategory());
        else holder.familyCategory.setText("");
        if (families.get(position).getBaseRegion() != null)
            holder.familyLocation.setText(families.get(position).getBaseRegion());
        else holder.familyLocation.setText("");

        if (family.isAddedTogroup()) {
            holder.join.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.join.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.INVISIBLE);
        }

        if (family.getCreatedByUserUsingType(type) != null) {
            holder.familyCreatedBy.setText("By " + family.getCreatedByUserUsingType(type));
            holder.familyCreatedBy.setVisibility(View.VISIBLE);
        } else {
            holder.familyCreatedBy.setVisibility(View.INVISIBLE);
        }

        switch (family.getUserJoinedCalculated()) {
            case FamilySearchModal.JOIN:
                holder.join.setText("Join");
                break;
            case FamilySearchModal.REJECTED:
                holder.join.setText("Rejected");
                break;
            case FamilySearchModal.JOINED:
                holder.join.setText("Member");
                break;
            case FamilySearchModal.PENDING:
                holder.join.setText("Pending");
                break;
            case FamilySearchModal.PRIVATE:
                holder.join.setText("Private");
                break;
            case FamilySearchModal.ACCEPT_INVITATION:
                holder.join.setText("Accept");
                break;
        }

        if (type != CONNECTIONS) {
            holder.join.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (family.getUserJoinedCalculated() == FamilySearchModal.PRIVATE) {
                Toast.makeText(context, "Private Families cannot be accessed !!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, FamilyDashboardActivity.class);
            intent.putExtra(DATA, family);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    public void onInvitationAccept(int position) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", families.get(position).getReqId());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("group_id", families.get(position).getId().toString());
        jsonObject.addProperty("from_id", families.get(position).getFromId());
        jsonObject.addProperty("status", "accepted");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(context);
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamily(families.get(position));
        apiCallbackParams.setPosition(position);
        apiServiceProvider.respondToFamilyInvitation(jsonObject, null, new RetrofitListener() {

            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                try {
                    Family family = new Family();
                    String status = new JSONObject(responseBodyString).getJSONObject("data").getString("status");
                    if (status.equalsIgnoreCase("accepted")) {
                        family.setHasUserJoinedAdminsFamily(true);
                        family.setGroupName(families.get(position).getGroupName());
                        family.setLogo(families.get(position).getLogo());
                        family.setMembersCount(families.get(position).getMembersCount());
                        family.setKnownCount(families.get(position).getKnownCount());
                        family.setGroupCategory(families.get(position).getGroupCategory());
                        family.setBaseRegion(families.get(position).getBaseRegion());
                        family.setCreatedByUserUsingType(families.get(position).getCreatedByUserUsingType(type));
                        family.setId(families.get(position).getId());
                        family.setStatus("Member");
                        family.setAddedTogroup(false);
                        families.set(position, family);
                        notifyItemChanged(position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                Toast.makeText(context, Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return families.size();
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        Family family = apiCallbackParams.getFamily();
        try {
            String status = new JSONObject(responseBodyString).getJSONObject("data").getString("status");
            if (status.equalsIgnoreCase("Pending")) {
                family.setHasUserJoinedAdminsFamily(false);
                family.setMemberJoining(null);
                family.setStatus("pending");
            } else {
                family.setHasUserJoinedAdminsFamily(true);
                family.setStatus("Member");
            }
            family.setAddedTogroup(false);
            families.set(apiCallbackParams.getPosition(), family);
            notifyItemChanged(apiCallbackParams.getPosition());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        Toast.makeText(context, Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
        for (int i = 0; i < families.size(); i++) {
            if (families.get(i).getId().equals(apiCallbackParams.getFamily().getId())) {
                families.get(i).setAddedTogroup(false);
            }
        }
        notifyItemChanged(apiCallbackParams.getPosition());
    }

    public void addToFamily(JsonObject jsonObject, int position) {
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(context);
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setFamily(families.get(position));
        apiCallbackParams.setPosition(position);
        apiServiceProvider.joinFamily(jsonObject, apiCallbackParams, this);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.familyLogo)
        ImageView familyLogo;
        @BindView(R.id.familyName)
        TextView familyName;
        @BindView(R.id.familyCategory)
        TextView familyCategory;
        @BindView(R.id.familyLocation)
        TextView familyLocation;
        @BindView(R.id.join)
        MaterialButton join;
        @BindView(R.id.membersCount)
        TextView membersCount;
        @BindView(R.id.labelMembersCount)
        TextView labelMembersCount;
        @BindView(R.id.eventsCount)
        TextView eventsCount;
        @BindView(R.id.labeleventsCount)
        TextView labelEventsCount;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.familyCreatedBy)
        TextView familyCreatedBy;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
