package com.familheey.app.Need;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.familheey.app.pagination.PaginationAdapterCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

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
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class NeedsRequestAdapter extends RecyclerView.Adapter<NeedsRequestAdapter.ViewHolder> {


    public static final int REQUEST_NEED_REQUEST_CODE = 1000;
    public static final int REQUEST_EDIT_REQUEST_CODE = 1001;
    private CompositeDisposable subscriptions;
    private List<Need> needRequests;
    private Fragment fragment;
    private PaginationAdapterCallback mCallback;

    public NeedsRequestAdapter(Fragment context, List<Need> needRequests, PaginationAdapterCallback mCallback) {
        this.needRequests = needRequests;
        subscriptions = new CompositeDisposable();
        this.fragment = context;
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_needs_request, parent, false);
        return new ViewHolder(listItem);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Need needRequest = needRequests.get(position);
        Glide.with(holder.itemView.getContext())
                .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + needRequest.getPropic())
                .apply(Utilities.getCurvedRequestOptions())
                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .placeholder(R.drawable.avatar_male)
                .into(holder.userProfileImage);
        holder.userProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(fragment.getActivity(), ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setUserId(Integer.parseInt(needRequest.getUser_id()));
            familyMember.setProPic(needRequest.getPropic());
            intent.putExtra(DATA, familyMember);
            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(fragment.requireActivity(), holder.userProfileImage, "profile");
            fragment.startActivity(intent, options.toBundle());
        });
        holder.supportersCount.setText(needRequest.getSupporters());
        holder.needsAdapter = new NeedsAdapter(fragment, needRequest);
        holder.needsList.setLayoutManager(new LinearLayoutManager(fragment.getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        holder.needsList.setAdapter(holder.needsAdapter);
        holder.itemView.setOnClickListener(v -> {
            Intent requestDetailedIntent = new Intent(fragment.getContext(), NeedRequestDetailedActivity.class);
            requestDetailedIntent.putExtra(DATA, String.valueOf(needRequest.getPost_request_id()));
            fragment.startActivityForResult(requestDetailedIntent, REQUEST_NEED_REQUEST_CODE);
        });
        holder.name.setText(needRequest.getFullName());

        holder.postedIn.setText(needRequest.getFormattedPostedIn());

        if (needRequest.getFormattedCreatedAt() != null) {
            holder.postedTime.setText(needRequest.getFormattedCreatedAt());
        }
        if (needRequest.getFormattedNeedStartDate() != null) {
            holder.requestedTime.setText(needRequest.getFormattedNeedStartDate());
            holder.requestedTime.setVisibility(View.VISIBLE);
            holder.requestedTimeIcon.setVisibility(View.VISIBLE);
        } else {
            holder.requestedTime.setVisibility(View.INVISIBLE);
            holder.requestedTimeIcon.setVisibility(View.INVISIBLE);
        }
        holder.requestedLocation.setText(needRequest.getRequest_location());

        if (needRequest.getUser_id().equals(SharedPref.getUserRegistration().getId())) {
            holder.moreOptions.setVisibility(View.VISIBLE);
        } else {
            holder.moreOptions.setVisibility(View.GONE);
        }

        holder.moreOptions.setOnClickListener(v -> showMenus(v, position));
    }

    @Override
    public int getItemCount() {
        return needRequests.size();
    }


    private void showMenus(View v, int position) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_request_owner, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit:
                    fragment.startActivityForResult(new Intent(fragment.getContext(), EditRequestActivity.class).putExtra(DATA, new Gson().toJson(needRequests.get(position))), REQUEST_EDIT_REQUEST_CODE);
                    break;
                case R.id.delete:
                    confirmation(position);
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void confirmation(int position) {

        SweetAlertDialog pDialog = new SweetAlertDialog(fragment.requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setContentText("Do you really want to delete this Request?")
                .setConfirmText("Yes")
                .setCancelText("No");

        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
            deleteNeed(needRequests.get(position).getPost_request_id() + "");
            needRequests.remove(position);
            notifyDataSetChanged();
            pDialog.cancel();
        });
        pDialog.setCancelClickListener(SweetAlertDialog::cancel);
    }

    private void deleteNeed(String item_id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_request_id", item_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(fragment.getContext());
        ApiServices apiServices = RetrofitBase.createRxResource(fragment.getContext(), ApiServices.class);
        assert application != null;
        subscriptions.add(apiServices.deleteNeed(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {

                }, throwable -> {
                    /*

                     */
                }));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userProfileImage)
        ImageView userProfileImage;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.postedIn)
        TextView postedIn;
        @BindView(R.id.postedTime)
        TextView postedTime;
        @BindView(R.id.needsList)
        RecyclerView needsList;
        @BindView(R.id.divider)
        ImageView divider;
        @BindView(R.id.requestedTimeIcon)
        ImageView requestedTimeIcon;
        @BindView(R.id.requestedTime)
        TextView requestedTime;
        @BindView(R.id.requestedLocationIcon)
        ImageView requestedLocationIcon;
        @BindView(R.id.requestedLocation)
        TextView requestedLocation;
        @BindView(R.id.supportersCount)
        TextView supportersCount;
        @BindView(R.id.labelSupporters)
        TextView labelSupporters;
        @BindView(R.id.moreOptions)
        ImageView moreOptions;
        NeedsAdapter needsAdapter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void showRetry(boolean show, @Nullable String errorMsg) {
        notifyItemChanged(needRequests.size() - 1);
    }

    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            ImageButton mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }
}