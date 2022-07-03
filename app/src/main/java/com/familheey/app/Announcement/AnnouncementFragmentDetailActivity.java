package com.familheey.app.Announcement;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
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

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class AnnouncementFragmentDetailActivity extends AppCompatActivity {

    public CompositeDisposable subscriptions;
    private ArrayList<PostData> postDatas;
    @BindView(R.id.pager)
    ViewPager pager;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_detail);
        subscriptions = new CompositeDisposable();
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (Objects.equals(bundle.getString(TYPE), "PUSH")) {
                getPost(bundle.getString("id"));

            } else {
                Type listType = new TypeToken<List<PostData>>() {
                }.getType();
                postDatas = new Gson().fromJson(getIntent().getExtras().getString(DATA), listType);
                pos = getIntent().getExtras().getInt("pos");
                pager.setAdapter(new AnnouncementPagerAdapter(this, postDatas));
                pager.setCurrentItem(pos);
            }

            bundle.clear();
        }

    }


    private void getPost(String id) {
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
                    if(response.isSuccessful()){
                        assert response.body() != null;
                        if (response.body().getData().size() > 0) {
                        postDatas = response.body().getData();
                        pager.setAdapter(new AnnouncementPagerAdapter(this, postDatas));
                    }}else{

                        SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                        contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> finish());
                        contentNotFoundDialog.setCanceledOnTouchOutside(false);
                        contentNotFoundDialog.setCancelable(false);
                        contentNotFoundDialog.show();
                        Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                    }

                }, throwable -> {
                }));
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().hasExtra(TYPE) && "PUSH".equals(Objects.requireNonNull(getIntent().getExtras()).getString(TYPE))) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra(POSITION, 0);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
