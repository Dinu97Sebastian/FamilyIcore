package com.familheey.app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Models.Response.FamilySearchModal;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class FamilySearchAdapter extends RecyclerView.Adapter<FamilySearchAdapter.ViewHolder> {

    private ArrayList<FamilySearchModal> familySearchArrayList;
    private OnFamilyJoiningStatusChanged mListener;
    public CompositeDisposable subscriptions;
    private Context context;

    public FamilySearchAdapter(Context context, OnFamilyJoiningStatusChanged mListener, ArrayList<FamilySearchModal> familySearchArrayList) {
        this.familySearchArrayList = familySearchArrayList;
        this.mListener = mListener;
        this.context = context;
        subscriptions = new CompositeDisposable();
    }

    public static SpannableString formatString(String membersCount) {
        if (membersCount == null || membersCount.isEmpty()) {
            membersCount = "N/A";
        }
        SpannableString ss1 = new SpannableString(membersCount);
        ss1.setSpan(new RelativeSizeSpan(1.3f), 0, membersCount.length(), 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#58B97E")), 0, membersCount.length(), 0);// set color
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        ss1.setSpan(boldSpan, 0, membersCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_family_search, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FamilySearchModal familySearchModal = familySearchArrayList.get(position);
        holder.familyName.setText(familySearchModal.getGroupName());
        holder.familyLocation.setText(familySearchModal.getBaseRegion());
        holder.familyType.setText(familySearchModal.getGroupCategory());
        holder.createdBy.setText(familySearchModal.getCreatedByName());
        if (familySearchModal.getMembercount() == null) {
            holder.labelMembersCount.setVisibility(View.GONE);
            holder.membersCount.setVisibility(View.GONE);
        } else {
            holder.membersCount.setText(familySearchModal.getMembercount());
            if (Integer.parseInt(familySearchModal.getMembercount()) == 0) {
                holder.labelMembersCount.setVisibility(View.GONE);
                holder.membersCount.setVisibility(View.GONE);
            } else {
                holder.labelMembersCount.setVisibility(View.VISIBLE);
                holder.membersCount.setVisibility(View.VISIBLE);
            }
        }

        if (familySearchModal.getKnowncount() == null) {
            holder.labeleventsCount.setVisibility(View.GONE);
            holder.eventsCount.setVisibility(View.GONE);
        } else {
            holder.eventsCount.setText(familySearchModal.getKnowncount());
            if (Integer.parseInt(familySearchModal.getKnowncount()) == 0) {
                holder.labeleventsCount.setVisibility(View.GONE);
                holder.eventsCount.setVisibility(View.GONE);
            } else {
                holder.labeleventsCount.setVisibility(View.VISIBLE);
                holder.eventsCount.setVisibility(View.VISIBLE);
            }
        }

        Glide.with(holder.familyLogo.getContext())
                .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + familySearchModal.getLogo())
                .apply(Utilities.getCurvedRequestOptions())
                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .placeholder(R.drawable.family_logo)
                .into(holder.familyLogo);
        switch (familySearchModal.getUserJoinedCalculated()) {

            case FamilySearchModal.REJECTED:
                // holder.join.setText("Rejected");
                //  break;
            case FamilySearchModal.JOIN:
                holder.join.setText("Join");
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

        holder.itemView.setOnClickListener(v -> {
            mListener.onFamilySelected(familySearchArrayList.get(position));
        });

        holder.join.setOnClickListener(view -> {
            switch (familySearchModal.getUserJoinedCalculated()) {

                case FamilySearchModal.REJECTED:
                    //Toast.makeText(holder.join.getContext(), "Admin has rejected your family joining request", Toast.LENGTH_SHORT).show();
                    //return;
                case FamilySearchModal.JOIN:
                    mListener.onFamilyJoinRequested(familySearchModal, position);
                    break;
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
                    SweetAlertDialog progressDialog = Utilities.getProgressDialog(context);
                    progressDialog.show();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", familySearchModal.getRequestId());
                    jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                    jsonObject.addProperty("group_id", familySearchModal.getGroupId().toString());
                    jsonObject.addProperty("from_id", familySearchModal.getFromId());
                    jsonObject.addProperty("status", "accepted");
                    FamilheeyApplication application = FamilheeyApplication.get(context);
                    RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
                    ApiServices apiServices = RetrofitBase.createRxResource(holder.itemView.getContext(), ApiServices.class);
                    subscriptions.add(apiServices.respondToFamilyInvitationRx(requestBody)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(application.defaultSubscribeScheduler())
                            .subscribe(response -> {
                                if (response.isSuccessful()) {
                                    familySearchArrayList.remove(familySearchModal);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(application, Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }, throwable -> {
                                if (!(throwable instanceof IOException)) {
                                    Toast.makeText(application, "Please check your internet connectivity", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(application, Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }));
            }
        });
    }

    @Override
    public int getItemCount() {
        return familySearchArrayList.size();
    }

    public interface OnFamilyJoiningStatusChanged {
        void onFamilySelected(FamilySearchModal family);

        void onFamilyJoinRequested(FamilySearchModal family, int position);
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
        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
