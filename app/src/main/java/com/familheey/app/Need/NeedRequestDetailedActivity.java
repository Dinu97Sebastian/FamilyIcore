package com.familheey.app.Need;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Stripe.StripeActivity;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DETAIL;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;
import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
public class NeedRequestDetailedActivity extends AppCompatActivity implements NeedDetailAdapter.OnUserSupportListener {

    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.userProfileImage)
    ImageView userProfileImage;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.postedIn)
    TextView postedIn;
    @BindView(R.id.postedTime)
    TextView postedTime;
    @BindView(R.id.requestedTimeIcon)
    ImageView requestedTimeIcon;
    @BindView(R.id.requestedTime)
    TextView requestedTime;
    @BindView(R.id.requestedLocationIcon)
    ImageView requestedLocationIcon;
    @BindView(R.id.requestedLocation)
    TextView requestedLocation;
    @BindView(R.id.needsList)
    RecyclerView needsList;
    @BindView(R.id.shimmerLoader)
    ShimmerFrameLayout shimmerLoader;
    @BindView(R.id.contributionSheet)
    FrameLayout contributionSheet;
    @BindView(R.id.contributedSheet)
    FrameLayout contributedSheet;
    @BindView(R.id.thanks_post)
    FrameLayout thanks_post;
    @BindView(R.id.moreOptions)
    ImageView moreOptions;
    @BindView(R.id.bg)
    View bg;
    private Need needRequest = new Need();
    private NeedDetailAdapter needDetailAdapter;
    private String id = "";
    private String from = "";
    private CompositeDisposable subscriptions;
    private boolean hasUpdatedAny = false;
    private boolean isAdmin = false;
    private BottomSheetBehavior contributionSheetBehaviour, contributedSheetBehaviour, thanksPostSheetBehaviour;
    // private ArrayList<HistoryImages> thankYouImages;
    private String notificationId = "";
    private DatabaseReference database;
    private static int CONTRIBUTOR_ACTIVITY_RESULT = 1000;
    private static int STRIPE_ACTIVITY_RESULT = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_request_detailed);
        ButterKnife.bind(this);
        initializeToolbar();
        id = getIntent().getStringExtra(DATA);
        if (getIntent().hasExtra("FROM")) {
            from = getIntent().getStringExtra("FROM");
        }
        if (getIntent().hasExtra(NOTIFICATION_ID)) {
            notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
            database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(notificationId).child("visible_status").setValue("read");
        }
        subscriptions = new CompositeDisposable();
        getNeedRequestDetails(false);
        initializeBottomSheets();
        initializeListeners();
    }

    private void initializeListeners() {
        postedIn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ViewFamiliesActivity.class);
            intent.putParcelableArrayListExtra(DATA, needRequest.getTo_groups());
            startActivity(intent);
        });
    }


    private void initializeToolbar() {
        toolBarTitle.setText("Request Details");
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void initializeAdapter() {
        needDetailAdapter = new NeedDetailAdapter(this, needRequest);
        needsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        needsList.setAdapter(needDetailAdapter);
        needsList.addItemDecoration(new BottomAdditionalMarginDecorator());
        if (from.equals("NOTIFICATION") && needRequest.getItems().size() == 1) {
            showContributionSheet(needRequest.getItems().get(0));
        }
    }

    private void initializeBottomSheets() {
        contributedSheetBehaviour = BottomSheetBehavior.from(contributedSheet);
        contributionSheetBehaviour = BottomSheetBehavior.from(contributionSheet);
        thanksPostSheetBehaviour = BottomSheetBehavior.from(thanks_post);
        contributedSheetBehaviour.setPeekHeight(0);
        contributionSheetBehaviour.setPeekHeight(0);
        thanksPostSheetBehaviour.setPeekHeight(0);
        // getThankYouPostImages();

        thanksPostSheetBehaviour.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (BottomSheetBehavior.STATE_COLLAPSED == newState) bg.setVisibility(View.GONE);
                else bg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        contributedSheetBehaviour.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (BottomSheetBehavior.STATE_COLLAPSED == newState) bg.setVisibility(View.GONE);
                else bg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        contributionSheetBehaviour.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (BottomSheetBehavior.STATE_COLLAPSED == newState) bg.setVisibility(View.GONE);
                else bg.setVisibility(View.VISIBLE);

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    private void showContributionSheet(Item contributionItem) {
        bg.setVisibility(View.VISIBLE);
        contributionSheet.setVisibility(View.VISIBLE);
        ((TextView) contributionSheet.findViewById(R.id.itemName)).setText(contributionItem.getRequest_item_title());
        EditText contributionQuantity = contributionSheet.findViewById(R.id.contributionQuantity);
        MaterialButton pledgeNow = contributionSheet.findViewById(R.id.pledgeNow);
        LinearLayout funding = contributionSheet.findViewById(R.id.funding);

        Switch anonymous = contributionSheet.findViewById(R.id.anonymous);
        MaterialButton paylater = contributionSheet.findViewById(R.id.paylater);
        MaterialButton paynow = contributionSheet.findViewById(R.id.paynow);

        contributionQuantity.setFilters(new InputFilter[]{new InputFilterMinMax("0", "999999")});
        //contributionItem.getMyContribution() + ""
        contributionQuantity.setText("");
        long balance = Math.round(contributionItem.getItem_quantity() - contributionItem.getReceived_amount());
        if (balance < 0) {
            balance = 0;
        }
        if ("fund".equals(needRequest.getRequest_type())) {
            pledgeNow.setVisibility(View.GONE);
            funding.setVisibility(View.VISIBLE);
            ((TextView) contributionSheet.findViewById(R.id.itemsHave)).setText("$" + balance);
            ((TextView) contributionSheet.findViewById(R.id.totalItems)).setText("$" + contributionItem.getItem_quantity() + "");

            if (contributionItem.getSum() != null)
                ((TextView) contributionSheet.findViewById(R.id.txt_my_contribution)).setText("(My contributions - $" + contributionItem.getSum() + ")");
            else
                ((TextView) contributionSheet.findViewById(R.id.txt_my_contribution)).setText("(My contributions - $0 )");

        } else {
            ((TextView) contributionSheet.findViewById(R.id.itemsHave)).setText(balance + "");
            ((TextView) contributionSheet.findViewById(R.id.totalItems)).setText(contributionItem.getItem_quantity() + "");
            if (contributionItem.getSum() != null)
                ((TextView) contributionSheet.findViewById(R.id.txt_my_contribution)).setText("(My contributions - " + contributionItem.getSum() + ")");
            else
                ((TextView) contributionSheet.findViewById(R.id.txt_my_contribution)).setText("(My contributions - 0 )");
        }

        pledgeNow.setText("Contribute");


        paylater.setOnClickListener(v -> {
            if (contributionQuantity.getText().toString().length() == 0) {
                Toast.makeText(this, "Please enter contribution", Toast.LENGTH_SHORT).show();
                return;
            }
            contribute(contributionItem, contributionQuantity.getText().toString(), "FUND", anonymous.isChecked());
        });

        pledgeNow.setOnClickListener(v -> {
            if (contributionQuantity.getText().toString().length() == 0) {
                Toast.makeText(this, "Please enter contribution", Toast.LENGTH_SHORT).show();
                return;
            }
            contribute(contributionItem, contributionQuantity.getText().toString(), "", false);
        });

        paynow.setOnClickListener(view -> {
                    if (contributionQuantity.getText().toString().length() == 0) {
                        Toast.makeText(this, "Please enter contribution", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivityForResult(new Intent(getApplicationContext(), StripeActivity.class)
                            .putExtra("TYPE", "request")
                            .putExtra("ANONYMOUS", anonymous.isChecked())
                            .putExtra("ITEMID", contributionItem.getItemId().toString())
                            .putExtra("RID", "" + needRequest.getPost_request_id())
                            .putExtra("ID", "" + needRequest.getTo_groups().get(0).getId())
                            .putExtra("AMT", contributionQuantity.getText().toString()), STRIPE_ACTIVITY_RESULT);
                    bg.setVisibility(View.GONE);
                    contributionSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
        );

        if (contributionSheetBehaviour.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            contributionSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bg.setVisibility(View.GONE);
            contributionSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void showContributedSheet() {
        bg.setVisibility(View.VISIBLE);
        ImageView successTick = contributedSheet.findViewById(R.id.successTick);
        successTick.setOnClickListener(view -> {

            bg.setVisibility(View.GONE);
            contributedSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            ;
        });
        MaterialButton callNow = contributedSheet.findViewById(R.id.callNow);
        callNow.setOnClickListener(v -> {
            bg.setVisibility(View.GONE);
            contributedSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (needRequest.getPhone() == null) {
                Toast.makeText(this, "Phone number not registered", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", needRequest.getPhone(), null));
            startActivity(intent);
        });
        if (contributedSheetBehaviour.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            contributedSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bg.setVisibility(View.GONE);
            contributedSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        contributionSheet.setVisibility(View.GONE);
        contributionSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        contributionSheetBehaviour.setPeekHeight(0);
    }


    private void contribute(Item contributionItem, String qty, String type, Boolean ischecked) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        if (type.equals("FUND")) {
            jsonObject.addProperty("is_anonymous", ischecked);
            jsonObject.addProperty("group_id", needRequest.getTo_groups().get(0).getId().toString());
        }
        jsonObject.addProperty("contribute_user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_request_item_id", contributionItem.getItemId() + "");
        jsonObject.addProperty("contribute_item_quantity", qty);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        subscriptions.add((authService.addContribution(requestBody))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    JsonObject responseJson = response.body();
                    progressDialog.dismissWithAnimation();
                    assert responseJson != null;
                    contributionItem.setMyContribution(responseJson.getAsJsonObject("data").get("contribute_item_quantity").getAsInt());
                    contributionItem.setTotalContribution(responseJson.getAsJsonObject("data").get("total_contribution").getAsInt());
                    contributionItem.setContributionId(responseJson.getAsJsonObject("data").get("id").getAsInt());
                    int t = 0;
                    if (contributionItem.getSum() != null) {
                        t = Integer.parseInt(contributionItem.getSum());
                    }
                    contributionItem.setSum(t + Integer.parseInt(qty) + "");
                    bg.setVisibility(View.GONE);
                    contributionSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    needDetailAdapter.notifyDataSetChanged();
                    showContributedSheet();
                    hasUpdatedAny = true;
                }, throwable -> {
                    throwable.printStackTrace();
                    Utilities.getErrorDialog(progressDialog, Constants.SOMETHING_WRONG);
                }));
    }


    private void getNeedRequestDetails(Boolean isShow) {
        showShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_request_id", id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        subscriptions.add(authService.getNeedsRequestDetail(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    assert response.body() != null;
                    isAdmin = response.body().getIs_admin();
                    needRequest = response.body().getNeedRequest();
                    initializeAdapter();
                    fillDetails();
                    needDetailAdapter.notifyDataSetChanged();
                    hideShimmer();
                    //initializeRestrictions();
                    if (isShow)
                        showContributedSheet();
                }, throwable -> {
                    hideShimmer();
                    if (throwable instanceof IOException) {
                        networkError();
                    } else {
                        SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this, "Sorry, details of this request are marked as private and cannot be displayed");
                        contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> finish());
                        contentNotFoundDialog.setCanceledOnTouchOutside(false);
                        contentNotFoundDialog.setCancelable(false);
                        contentNotFoundDialog.show();
                        Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                    }
                }));
    }

    void showShimmer() {
        if (shimmerLoader != null) {
            shimmerLoader.setVisibility(View.VISIBLE);
            shimmerLoader.startShimmer();
        }
    }

    void hideShimmer() {
        if (shimmerLoader != null) {
            shimmerLoader.stopShimmer();
            shimmerLoader.setVisibility(View.GONE);
        }
    }

    private void networkError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> getNeedRequestDetails(false)).setNegativeButton("Cancel", (dialog, which) -> {
            onBackPressed();
            dialog.dismiss();
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        AlertDialog alert = builder.create();
        alert.setTitle("Connection Unavailable");
        alert.show();
        params.setMargins(0, 0, 20, 0);
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(params);
    }

    private void fillDetails() {
        Glide.with(getApplicationContext())
                .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + needRequest.getPropic())
                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .apply(Utilities.getCurvedRequestOptions())
                .placeholder(R.drawable.avatar_male)
                .into(userProfileImage);
        name.setText(needRequest.getFullName());
        if (needRequest.getRequest_location() != null) {
            requestedLocation.setText(needRequest.getRequest_location());
            requestedLocation.setVisibility(View.VISIBLE);
            requestedLocationIcon.setVisibility(View.VISIBLE);
        } else {
            requestedLocation.setVisibility(View.INVISIBLE);
            requestedLocationIcon.setVisibility(View.INVISIBLE);
        }
        if (needRequest.getFormattedCreatedAt() != null) {
            postedTime.setText(needRequest.getFormattedCreatedAt());
            postedTime.setVisibility(View.VISIBLE);
        } else {
            postedTime.setVisibility(View.INVISIBLE);
        }
        postedIn.setText(needRequest.getFormattedPostedIn());
        if (needRequest.getFormattedNeedStartDate() != null) {
            String time = needRequest.getFormattedNeedStartDate();
            requestedTime.setVisibility(View.VISIBLE);
            requestedTimeIcon.setVisibility(View.VISIBLE);
            if (needRequest.getFormattedNeedEndDate() != null) {
                time += " - " + needRequest.getFormattedNeedEndDate();
            }
            requestedTime.setText(time);


        } else {
            requestedTime.setVisibility(View.INVISIBLE);
            requestedTimeIcon.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onUserContributionPrompted(Item selectedContribution) {
        showContributionSheet(selectedContribution);
    }

    @Override
    public void onUserContributorsRequested(Item selectedContribution) {
        Intent intent = new Intent(getApplicationContext(), ContributorsListingActivity.class);
        intent.putExtra(ID, selectedContribution.getItemId() + "");
        intent.putExtra(DATA, selectedContribution);
        intent.putExtra(DETAIL, needRequest);
        intent.putExtra(IS_ADMIN, isAdmin);
        startActivityForResult(intent, CONTRIBUTOR_ACTIVITY_RESULT);
    }

    private void showNeedRequestMoreOptions() {
        PopupMenu popup = new PopupMenu(moreOptions.getContext(), moreOptions);
        popup.getMenuInflater().inflate(R.menu.popup_more_options, popup.getMenu());
        //Menu m = popup.getMenu();
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.editNeed:
                case R.id.deleteNeed:
                    break;
            }
            return true;
        });
        popup.show();
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().hasExtra("FROM")) {
            finish();
            overridePendingTransition(R.anim.left,
                    R.anim.right);
        } else if (getIntent() != null && getIntent().hasExtra(TYPE)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            overridePendingTransition(R.anim.left,
                    R.anim.right);
        }
        bg.setVisibility(View.GONE);
        if (contributedSheetBehaviour != null && contributedSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED)
            contributedSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else if (contributionSheetBehaviour != null && contributionSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED)
            contributionSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else if (thanksPostSheetBehaviour != null && thanksPostSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED)
            thanksPostSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else if (hasUpdatedAny) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            overridePendingTransition(R.anim.left,
                    R.anim.right);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (contributedSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                contributedSheet.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    contributedSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bg.setVisibility(View.GONE);
                }
            }
            if (contributionSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                contributionSheet.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    contributionSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bg.setVisibility(View.GONE);
                }
            }
            if (thanksPostSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                thanks_post.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    thanksPostSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bg.setVisibility(View.GONE);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                hasUpdatedAny = true;
            }
        } else if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            getNeedRequestDetails(true);
        }
    }

    @OnClick({R.id.userProfileImage, R.id.name, R.id.moreOptions})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userProfileImage:
            case R.id.name:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                FamilyMember familyMember = new FamilyMember();
                familyMember.setUserId(Integer.parseInt(needRequest.getUser_id()));
                familyMember.setProPic(needRequest.getPropic());
                intent.putExtra(DATA, familyMember);
                intent.putExtra(Constants.Bundle.FOR_EDITING, true);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(NeedRequestDetailedActivity.this, userProfileImage, "profile");
                startActivity(intent, options.toBundle());
                break;
            case R.id.moreOptions:
                showNeedRequestMoreOptions();
                break;
        }
    }

/*
    private void getThankYouPostImages() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_type", "request");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        subscriptions.add((authService.get_post_default_image(requestBody))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> thankYouImages = response.body(), throwable -> throwable.printStackTrace()));

    }


    public void createPostRequest(int imgPos, String des) {
        PostRequest request = new PostRequest();
        request.setCategory_id(3 + "");
        request.setCreated_by(SharedPref.getUserRegistration().getId());
        request.setPublish_type("request");
        request.setPublish_id(needRequest.getPost_request_id());
        request.setPublish_mention_users(new ArrayList<>());
        ArrayList<SelectFamilys> family = new ArrayList<>();
        *//*
	"publish_mention_users":[1,4,3,5
         *//*
        for (Group obj : needRequest.getTo_groups()) {
            SelectFamilys fam = new SelectFamilys();
            fam.setId(obj.getId() + "");
            family.add(fam);
        }


        request.setSelected_groups(family);
        request.setSnap_description(des);
        ArrayList<HistoryImages> img = new ArrayList<>();
        img.add(thankYouImages.get(imgPos));
        request.setPost_attachment(img);
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.createPost(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    // hideProgressDialog();
                    if (response.code() == 200) {
                        Toast.makeText(NeedRequestDetailedActivity.this,
                                "Post successfully created", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, throwable -> {
                    //hideProgressDialog()
                }));
    }*/

}
