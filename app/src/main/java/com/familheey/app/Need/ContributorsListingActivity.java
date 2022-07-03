package com.familheey.app.Need;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Barrier;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Models.Request.PostInfo;
import com.familheey.app.Models.Request.PostRequest;
import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Stripe.StripeActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.DETAIL;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

public class ContributorsListingActivity extends AppCompatActivity implements ContributorsListingAdapter.OnUserSupportListener {
    public static final int STRIPE_REQUEST_CODE = 1001;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.itemName)
    TextView itemName;
    @BindView(R.id.txt_complete)
    TextView txtComplete;
    @BindView(R.id.labelContributorsCount)
    TextView labelContributorsCount;
    @BindView(R.id.contributorsCount)
    TextView contributorsCount;
    @BindView(R.id.barrier)
    Barrier barrier;
    @BindView(R.id.totalItems)
    TextView totalItems;
    @BindView(R.id.labelOf)
    TextView labelOf;
    @BindView(R.id.itemsHave)
    TextView itemsHave;
    @BindView(R.id.itemStatus)
    TextView itemStatus;
    @BindView(R.id.contributorsList)
    RecyclerView contributorsList;
    @BindView(R.id.shimmerLoader)
    ShimmerFrameLayout shimmerLoader;

    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imageBack)
    ImageView imageBack;
    @BindView(R.id.searchQuery)
    EditText searchQuery;
    @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;
    @BindView(R.id.shimmerSupporter)
    Group shimmerSupporter;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
    @BindView(R.id.bg)
    View bg;
    @BindView(R.id.contributionSheet)
    FrameLayout contributionSheet;
    @BindView(R.id.contributedSheet)
    FrameLayout contributedSheet;
    @BindView(R.id.thanks_post)
    FrameLayout thanks_post;
    @BindView(R.id.txt_receive_text)
    TextView txt_receive_text;
    @BindView(R.id.txt_rceived_amt)
    TextView txt_rceived_amt;
    @BindView(R.id.txt_pledge_text)
    TextView txt_pledge_text;
    @BindView(R.id.txt_pledge_amt)
    TextView txt_pledge_amt;
    private ContributorsListingAdapter contributorsListingAdapter;
    private List<Contributor> contributors = new ArrayList<>();
    private ContributorsWrapper contributorsWrapper;
    private String id = "";
    private CompositeDisposable subscriptions;
    private Item need;
    private Need needRequest = new Need();
    private boolean isAdmin = false;
    private boolean hasUpdatedAny = false;
    private BottomSheetBehavior contributionSheetBehaviour, contributedSheetBehaviour, thanksPostSheetBehaviour;
    private ArrayList<HistoryImages> thankYouImages;
    private SweetAlertDialog progressDialog;
    private String des = "";
    private String fname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributors_listing);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra(Constants.Bundle.ID);
        need = getIntent().getParcelableExtra(Constants.Bundle.DATA);
        needRequest = getIntent().getParcelableExtra(Constants.Bundle.DETAIL);
        isAdmin = getIntent().getBooleanExtra(IS_ADMIN, false);
        subscriptions = new CompositeDisposable();
        initializeToolbar();
        initializeRecyclerview();
        initializeBottomSheets();
        getContributors(false);
        getThankYouPostImages();
        if (needRequest.getThank_post_id() != null && !needRequest.getThank_post_id().isEmpty()) {
            getPost(needRequest.getThank_post_id());
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (contributors.size() > 0)
            getAmounts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STRIPE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            contributors.clear();
            contributorsListingAdapter.notifyDataSetChanged();
            getContributors(true);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
    public void onthankyouPost(Contributor selectedContribution) {
        thanksPostSheet(selectedContribution);
    }

    @Override
    public void onPaynow(Contributor selectedContribution) {
        startActivityForResult(new Intent(getApplicationContext(), StripeActivity.class)
                .putExtra("CID", selectedContribution.getId() + "")
                .putExtra("ITEMID", need.getItemId().toString())
                .putExtra("TYPE", "request")
                .putExtra("RID", "" + needRequest.getPost_request_id())
                .putExtra("ID", "" + needRequest.getTo_groups().get(0).getId())
                .putExtra("AMT", selectedContribution.getTotal_contribution().toString()), STRIPE_REQUEST_CODE);
    }


    @Override
    public void onAcknowledge(Contributor selectedContribution) {
        acknowledgeContribute(selectedContribution);
    }

    @Override
    public void onItemClick(Contributor contributor) {
        Intent intent = new Intent(this, SingleContrionsListing.class);
        intent.putExtra("NEED", need);
        intent.putExtra(DETAIL, needRequest);
        intent.putExtra(Constants.Bundle.DATA, contributor.getContributeUserId().toString());
        intent.putExtra(Constants.Bundle.ID, contributor.getPostRequestItemId().toString());
        intent.putExtra(Constants.Bundle.IS_ANONYMOUS, contributor.isIs_anonymous());
        intent.putExtra(Constants.Bundle.IS_ADMIN, isAdmin);
        intent.putExtra("RID", needRequest.getPost_request_id());
        if (contributor.getContributeUserId() == 2) {
            intent.putExtra("NAME", contributor.getPaid_user_name());
        } else {
            intent.putExtra("NAME", contributor.getFullName());
        }
        intent.putExtra(Constants.Bundle.FAMILY_ID, needRequest.getTo_groups().get(0).getId().toString());
        intent.putExtra(Constants.Bundle.IDENTIFIER, needRequest.getRequest_type());
        startActivity(intent);
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Contributors");
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void initializeRecyclerview() {
        contributorsListingAdapter = new ContributorsListingAdapter(this, contributors, isAdmin, needRequest.getRequest_type(), this);
        contributorsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        contributorsList.setAdapter(contributorsListingAdapter);
        contributorsList.addItemDecoration(new BottomAdditionalMarginDecorator());
    }


    void showShimmer() {
        shimmerSupporter.setVisibility(View.GONE);
        emptyResultText.setVisibility(View.GONE);
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
        shimmerSupporter.setVisibility(View.VISIBLE);
    }

    private void networkError(Context context) {
        if (context == null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> getContributors(false)).setNegativeButton("Cancel", (dialog, which) -> {
            finish();
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

    private void showKeyboard() {
        if (searchQuery.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchQuery, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    @OnClick({R.id.bg, R.id.goBack, R.id.imgSearch, R.id.imageBack, R.id.clearSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.goBack:
                onBackPressed();
                break;
            case R.id.imgSearch:
                showCircularReveal(constraintSearch);
                goBack.setEnabled(false);
                showKeyboard();
                break;
            case R.id.imageBack:
                hideCircularReveal(constraintSearch);
                searchQuery.setText("");
                goBack.setEnabled(true);
                onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
                break;
            case R.id.clearSearch:
                break;
        }
    }

    @OnEditorAction(R.id.searchQuery)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            getContributors(false);
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(searchQuery.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (hasUpdatedAny) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

            overridePendingTransition(R.anim.left,
                    R.anim.right);
        } else
            super.onBackPressed();


        bg.setVisibility(View.GONE);
        if (thanksPostSheetBehaviour != null && thanksPostSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED)
            thanksPostSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    private void initializeBottomSheets() {

        contributedSheetBehaviour = BottomSheetBehavior.from(contributedSheet);
        contributionSheetBehaviour = BottomSheetBehavior.from(contributionSheet);
        thanksPostSheetBehaviour = BottomSheetBehavior.from(thanks_post);
        contributedSheetBehaviour.setPeekHeight(0);
        contributionSheetBehaviour.setPeekHeight(0);
        thanksPostSheetBehaviour.setPeekHeight(0);
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

    private void thanksPostSheet(Contributor selectedContribution) {
        thanks_post.setVisibility(View.VISIBLE);
        ViewPager2 pager2 = thanks_post.findViewById(R.id.preview_img);
        ImageView left = thanks_post.findViewById(R.id.btn_left);
        ImageView right = thanks_post.findViewById(R.id.btn_right);
        left.setOnClickListener(view -> pager2.setCurrentItem((pager2.getCurrentItem() - 1), true));
        right.setOnClickListener(view -> pager2.setCurrentItem((pager2.getCurrentItem() + 1), true));
        EditText text = thanks_post.findViewById(R.id.contributionQuantity);
        if (!des.isEmpty()) {
            text.setText(des);
        }
        pager2.setAdapter(new ThankYouImageAdapter(this, thankYouImages));

        pager2.setCurrentItem(findpicture(fname), true);
        bg.setVisibility(View.VISIBLE);
        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    left.setVisibility(View.GONE);
                } else {
                    left.setVisibility(View.VISIBLE);
                }
                if (position == (thankYouImages.size() - 1)) {

                    right.setVisibility(View.GONE);
                } else {
                    right.setVisibility(View.VISIBLE);
                }
            }
        });
        thanks_post.findViewById(R.id.btn_skip).setOnClickListener(view -> thankYouPostSkip(selectedContribution));

        thanks_post.findViewById(R.id.btn_thankpost).setOnClickListener(view -> createPostRequest(pager2.getCurrentItem(), text.getText().toString(), selectedContribution));

        if (thanksPostSheetBehaviour.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            thanksPostSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            thanksPostSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bg.setVisibility(View.GONE);
        }

    }


    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private int findpicture(String fname) {
        for (int i = 0; i < thankYouImages.size(); i++) {
            if (thankYouImages.get(i).getFilename().equals(fname)) {
                return i;
            }
        }
        return 0;
    }

    private void getThankYouPostImages() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_type", "request");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        subscriptions.add((authService.get_post_default_image(requestBody))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> thankYouImages = response.body(), Throwable::printStackTrace));

    }


    private void getContributors(boolean refresh) {
        showShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_request_item_id", id);
        jsonObject.addProperty("query", searchQuery.getText().toString().trim());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        subscriptions.add(authService.getNeedsContributors(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    contributorsWrapper = response.body();
                    itemName.setText(need.getRequest_item_title());
                    contributors.clear();
                    contributors.addAll(contributorsWrapper.getContributors());
                    if (contributors.size() > 0) {
                        getAmounts();
                        if (SharedPref.getUserRegistration().getId().equalsIgnoreCase(contributors.get(0).getCreatedBy() + "")) {
                            isAdmin = true;
                        }
                        itemName.setText(contributors.get(0).getRequestItemTitle());
                        totalItems.setText(contributors.get(0).getTotalNeeded() + "");
                        labelOf.setText(" of ");
                        labelContributorsCount.setText(contributors.size() + (contributors.size() > 1 ? " Contributors" : " Contributor"));
                        emptyResultText.setVisibility(View.INVISIBLE);
                    } else emptyResultText.setVisibility(View.VISIBLE);
                    contributorsListingAdapter.setAdmin(isAdmin);
                    contributorsListingAdapter.notifyDataSetChanged();
                    hideShimmer();
                    hasUpdatedAny = true;
                    if (refresh)
                        showContributedSheet();
                }, throwable -> {
                    hideShimmer();
                    networkError(this);
                }));
    }


    public void createPostRequest(int imgPos, String des, Contributor selectedContribution) {
        showProgressDialog();
        PostRequest request = new PostRequest();
        request.setCategory_id(3 + "");
        request.setCreated_by(SharedPref.getUserRegistration().getId());
        request.setPublish_type("request");
        request.setPost_info(new PostInfo());
        request.setPublish_id(needRequest.getPost_request_id());
        ArrayList<Item_need> item_needs = new ArrayList<>();
        Item_need item_need = new Item_need();
        item_need.setItem_id(need.getItemId());
        item_need.setItem_name(need.getRequest_item_title());
        item_needs.add(item_need);
        request.setPublish_mention_items(item_needs);
        ArrayList<User> users = new ArrayList<>();
        User user = new User();
        user.setUser_id(selectedContribution.getContributeUserId());
        user.setUser_name(selectedContribution.getFullName());
        users.add(user);
        request.setPublish_mention_users(users);
        ArrayList<SelectFamilys> family = new ArrayList<>();
        for (com.familheey.app.Need.Group obj : needRequest.getTo_groups()) {
            SelectFamilys fam = new SelectFamilys();
            fam.setId(obj.getId() + "");
            fam.setPost_create("6");
            family.add(fam);
        }
        request.setType("post");
        request.setPrivacy_type("public");
        request.setPost_type("only_groups");

        request.setConversation_enabled(true);
        request.setIs_shareable(false);
        request.setSelected_groups(family);
        request.setSnap_description(des);
        ArrayList<HistoryImages> img = new ArrayList<>();
        img.add(thankYouImages.get(imgPos));
        request.setPost_attachment(img);


        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.createThankYouPost(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    hideProgressDialog();
                    if (response.code() == 200) {
                        selectedContribution.setIs_thank_post(true);
                        selectedContribution.setIs_acknowledge(true);
                        selectedContribution.setIs_pending_thank_post(false);
                        contributorsListingAdapter.notifyDataSetChanged();
                        fname = thankYouImages.get(imgPos).getFilename();
                        this.des = des;
                        bg.setVisibility(View.GONE);
                        if (thanksPostSheetBehaviour != null && thanksPostSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED)
                            thanksPostSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        Toast.makeText(ContributorsListingActivity.this,
                                "Thank you post successfully created", Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> hideProgressDialog()));
    }

    private void getPost(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", id);
        jsonObject.addProperty("type", "post");
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.getMyPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    assert response.body() != null;
                    des = response.body().getData().get(0).getSnap_description();
                    fname = response.body().getData().get(0).getPost_attachment().get(0).getFilename();
                }, throwable -> {
                }));
    }

    private void thankYouPostSkip(Contributor selectedContribution) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("contribute_user_id", selectedContribution.getContributeUserId() + "");
        jsonObject.addProperty("request_item_id", selectedContribution.getPostRequestItemId() + "");
        jsonObject.addProperty("skip_thank_post", true);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.contributionStatusUpdation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    selectedContribution.setSkip_thank_post(true);
                    selectedContribution.setIs_acknowledge(true);
                    selectedContribution.setIs_thank_post(false);
                    selectedContribution.setIs_pending_thank_post(false);
                    contributorsListingAdapter.notifyDataSetChanged();
                    bg.setVisibility(View.GONE);
                    if (thanksPostSheetBehaviour != null && thanksPostSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED)
                        thanksPostSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    hideProgressDialog();
                }, throwable -> hideProgressDialog()));
    }

    private void acknowledgeContribute(Contributor selectedContribution) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("is_anonymous", true);
        jsonObject.addProperty("contribute_user_id", selectedContribution.getContributeUserId() + "");
        jsonObject.addProperty("request_item_id", selectedContribution.getPostRequestItemId() + "");
        jsonObject.addProperty("skip_thank_post", true);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.contributionStatusUpdation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    selectedContribution.setSkip_thank_post(true);
                    selectedContribution.setIs_acknowledge(true);
                    selectedContribution.setIs_thank_post(true);
                    selectedContribution.setIs_pending_thank_post(true);
                    contributorsListingAdapter.notifyDataSetChanged();
                    bg.setVisibility(View.GONE);
                    if (thanksPostSheetBehaviour != null && thanksPostSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED)
                        thanksPostSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    hideProgressDialog();
                }, throwable -> hideProgressDialog()));
    }


    private void showContributedSheet() {
        bg.setVisibility(View.VISIBLE);
        ImageView successTick = contributedSheet.findViewById(R.id.successTick);
        successTick.setOnClickListener(view -> {
                    contributedSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bg.setVisibility(View.GONE);
                }
        );
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
    }


    private void getAmounts() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("item_id", id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.get_contributeItemAmountSplit(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    String balance;
                    float b;
                    if (response.body() != null && response.body().size() > 0) {
                        txt_receive_text.setText("Received : ");
                        txt_rceived_amt.setText("0");
                        if (response.body().get(0).getReceived_amount() != null) {
                            b = Math.round(contributors.get(0).getTotalNeeded() - (response.body().get(0).getReceived_amount()));
                            txt_rceived_amt.setText(Math.round(response.body().get(0).getReceived_amount()) + "");
                        } else {
                            b = contributors.get(0).getTotalNeeded();
                        }
                        if ("fund".equals(needRequest.getRequest_type())) {
                            balance = "$" + Math.round(b);
                        } else {
                            balance = "" + Math.round(b);
                        }
                        if (Math.round(b) > 0) {
                            itemsHave.setText(balance);
                        } else {
                            txtComplete.setVisibility(View.VISIBLE);
                            itemsHave.setVisibility(View.GONE);
                            itemStatus.setVisibility(View.GONE);
                            itemStatus.setText("");
                            totalItems.setText("");
                            labelOf.setText("");
                            totalItems.setVisibility(View.GONE);
                            labelOf.setVisibility(View.GONE);
                        }

                        txt_pledge_text.setText("Pledged : ");
                        txt_pledge_amt.setText("0");
                        if (response.body().get(0).getPledged_amount() != null)
                            txt_pledge_amt.setText((Math.round(response.body().get(0).getPledged_amount())) + "");
                    }
                }, throwable -> {
                }));
    }

}
