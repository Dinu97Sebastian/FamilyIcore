package com.familheey.app.Announcement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
public class AnnouncementDetailActivity extends AppCompatActivity {

    public CompositeDisposable subscriptions;
    private ArrayList<PostData> postDatas;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.pbar)
    ProgressBar bar;
    int pos = 0;
    private String notificationId = "";
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_detail);
        subscriptions = new CompositeDisposable();
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (Objects.equals(bundle.getString(TYPE), "PUSH") || Objects.equals(bundle.getString(TYPE), "NOTIFICATION")) {
                getPost(bundle.getString("id"));
            } else if (Objects.equals(bundle.getString(TYPE), "UNREAD")) {
                getAnnouncement();
            } else {
                Type listType = new TypeToken<List<PostData>>() {
                }.getType();
                postDatas = new Gson().fromJson(FamilheeyApplication.announcementData, listType);
                pos = getIntent().getExtras().getInt("pos");
                pager.setAdapter(new AnnouncementPagerAdapter(this, postDatas));
                pager.setCurrentItem(pos);

                try {
                    if (postDatas != null && postDatas.size() >= pos && postDatas.get(pos) != null) {
                        if (!postDatas.get(pos).isRead_status())
                            addViewCount(postDatas.get(pos).getPost_id() + "");
                    }
                } catch (Exception e) {
                }
            }

            bundle.clear();
            if (getIntent().hasExtra(NOTIFICATION_ID)) {
                notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
                database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
                database.child(notificationId).child("visible_status").setValue("read");
            }
        }

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (postDatas.get(position) != null && !postDatas.get(position).isRead_status()) {
                    addViewCount(postDatas.get(position).getPost_id() + "");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void getPost(String id) {
        bar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", id);
        jsonObject.addProperty("type", "announcement");
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.getMyPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {

                    bar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().getData().size() > 0) {
                            postDatas = response.body().getData();
                            addViewCount(postDatas.get(pos).getPost_id() + "");
                            pager.setAdapter(new AnnouncementPagerAdapter(this, postDatas));
                        } else {

                            SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                            contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                                contentNotFoundDialog.cancel();
                                Intent intent = new Intent( this, MainActivity.class );
                                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity( intent );
                            });
                            contentNotFoundDialog.setCanceledOnTouchOutside(false);
                            contentNotFoundDialog.setCancelable(false);
                            contentNotFoundDialog.show();
                            Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                        }
                    } else {

                        SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                        contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                            contentNotFoundDialog.cancel();
                            Intent intent = new Intent( this, MainActivity.class );
                            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            startActivity( intent );
                        });
                        contentNotFoundDialog.setCanceledOnTouchOutside(false);
                        contentNotFoundDialog.setCancelable(false);
                        contentNotFoundDialog.show();
                        Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                    }

                }, throwable -> bar.setVisibility(View.GONE)));
    }

    @Override
    protected void onDestroy() {
        FamilheeyApplication.announcementData = "";
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().hasExtra(TYPE) && "PUSH".equals(Objects.requireNonNull(getIntent().getExtras()).getString(TYPE))) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.left,
                    R.anim.right);
        } else if (getIntent() != null && getIntent().hasExtra(TYPE) && "UNREAD".equals(Objects.requireNonNull(getIntent().getExtras()).getString(TYPE))) {
            Intent announcementIntent = new Intent(getApplicationContext(), AnnouncementListing.class);
            startActivity(announcementIntent);
            finish();
            overridePendingTransition(R.anim.left,
                    R.anim.right);
        } else {
            Intent intent = new Intent();
            intent.putExtra(POSITION, 0);
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.left,
                    R.anim.right);
        }
    }


    private void getAnnouncement() {
        bar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type", "announcement");
        jsonObject.addProperty("offset", "0");
        jsonObject.addProperty("limit", "1000");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.getAnnouncement(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    bar.setVisibility(View.GONE);
                    if (response.body() != null && response.body().getUnread_announcement() != null) {
                        postDatas = response.body().getUnread_announcement();
                        pager.setAdapter(new AnnouncementPagerAdapter(this, postDatas));
                        addUpdate_Announcement_Seen();
                        if (!postDatas.get(0).isRead_status()) {
                            addViewCount(postDatas.get(0).getPost_id() + "");
                        }
                    }
                }, throwable -> {


                }));


    }

    private void addViewCount(String post_id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.addViewCount(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }

    private void addUpdate_Announcement_Seen() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.addUpdate_Announcement_Seen(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {

                }, throwable -> {
                }));
    }
}
