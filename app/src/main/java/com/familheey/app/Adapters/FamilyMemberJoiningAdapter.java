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
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class FamilyMemberJoiningAdapter extends RecyclerView.Adapter<FamilyMemberJoiningAdapter.ViewHolder> implements RetrofitListener {

    private final List<Family> families;
    private final String joinerUserId;
    private final Context context;
    private final String joineeUserId;

    public FamilyMemberJoiningAdapter(Context context, List<Family> families, String joinerUserId, String joineeUserId) {
        this.families = families;
        this.joinerUserId = joinerUserId;
        this.joineeUserId = joineeUserId;
        this.context = context;
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
        View listItem = layoutInflater.inflate(R.layout.item_add_tofamily, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Family family = families.get(position);
        holder.join.setOnClickListener(view -> {
             // Dinu(03-03-2021)-> for check admin or member
            if (family.getLogined_user() != null && family.getLogined_user().equalsIgnoreCase("admin")) {
                switch (family.getUserJoinedCalculated()) {
                    case FamilySearchModal.PRIVATE:
                        //Since current user adds user to his private family, No need to show Private, Only show Join
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
                /*case FamilySearchModal.PRIVATE:
                    Toast.makeText(holder.join.getContext(), "Only admin of this group can send you a joining request!", Toast.LENGTH_SHORT).show();
                    return;*/
                }
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userId", joinerUserId);
                jsonObject.addProperty("from_id", joineeUserId);
                jsonObject.addProperty("groupId", families.get(position).getId().toString());
                families.get(position).setAddedTogroup(true);
                notifyItemChanged(position);
                addToFamily(jsonObject, position);
            }
            else{
                Toast.makeText(holder.join.getContext(), "Only admin can add new members", Toast.LENGTH_SHORT).show();
            }

        });
        holder.familyName.setText(families.get(position).getGroupName());
        if (families.get(position).getLogo() != null) {
            Glide.with(holder.familyLogo.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + families.get(position).getLogo())
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
            if (families.get(position).getEventsCount().equalsIgnoreCase("0")) {
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

        if (families.get(position).getEventsCount() != null) {
            holder.eventsCount.setText(families.get(position).getEventsCount());
            if (families.get(position).getEventsCount().equalsIgnoreCase("0")) {
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

        switch (family.getUserJoinedCalculated()) {
            case FamilySearchModal.PRIVATE:
                //Since current user adds user to his private family, No need to show Private, Only show Join
            case FamilySearchModal.JOIN:
                holder.join.setText("Invite");
                holder.join.setBackgroundColor(context.getResources().getColor(R.color.greenTextColor));
                break;
            case FamilySearchModal.REJECTED:
                holder.join.setText("Rejected");
                break;
            case FamilySearchModal.JOINED:
                holder.join.setText("Member");
                holder.join.setBackgroundColor(context.getResources().getColor(R.color.searchBox));
                break;
            case FamilySearchModal.PENDING:
                holder.join.setText("Pending");
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FamilyDashboardActivity.class);
            intent.putExtra(DATA, family);
            holder.itemView.getContext().startActivity(intent);
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
            String status = new JSONObject(responseBodyString).getString("status");
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
        apiServiceProvider.addToFamily(jsonObject, apiCallbackParams, this);
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
