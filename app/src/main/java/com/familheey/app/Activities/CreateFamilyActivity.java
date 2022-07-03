package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.familheey.app.Fragments.FamilyCreationLevelAdvancedFragment;
import com.familheey.app.Fragments.FamilyCreationLevelFourFragment;
import com.familheey.app.Fragments.FamilyCreationLevelOneFragment;
import com.familheey.app.Fragments.FamilyCreationLevelThreeFragment;
import com.familheey.app.Fragments.FamilyCreationLevelTwoFragment;
import com.familheey.app.Fragments.SimilarFamiliesFragment;
import com.familheey.app.Interfaces.FamilyCreationListener;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.FamilyCreation;
import com.familheey.app.Models.Request.SimilarFamilyRequest;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IS_CREATED_NOW;
import static com.familheey.app.Utilities.Constants.Bundle.TO_CREATE_FAMILY;

public class CreateFamilyActivity extends AppCompatActivity implements RetrofitListener, FamilyCreationListener, ProgressListener, FamilyCreationLevelAdvancedFragment.OnFamilyCreationListener {


    @BindView(R.id.createFamilyStepsLoader)
    FrameLayout createFamilyStepsLoader;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.goBack)
    ImageView goBack;

    @BindView(R.id.round1)
    CardView round1;
    @BindView(R.id.line1)
    View line1;

    @BindView(R.id.round2)
    CardView round2;
    @BindView(R.id.line2)
    View line2;

    @BindView(R.id.round3)
    CardView round3;
    @BindView(R.id.line3)
    View line3;

    @BindView(R.id.round4)
    CardView round4;


    private SweetAlertDialog progressDialog;
    private FragmentManager fragmentManager;
    private Family family = new Family();
    private final FamilyCreation familyCreation = new FamilyCreation();
    private String linkFamilyId = "";
    private boolean hasLinked = false;
    private boolean toCreateFamily=false;
    private boolean isExistingUser = false;
    private int flag=1;
    private int barStatus=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        family = new Family();
        toCreateFamily=getIntent().getBooleanExtra( TO_CREATE_FAMILY, false);
        isExistingUser=getIntent().getBooleanExtra( Constants.Bundle.IS_EXISTING_USER, false);
        getIntentDatas();
        loadFamilyCreationLevelOne(family);
        initializeToolbar();
    }

    private void getIntentDatas() {
        if (getIntent().hasExtra(DATA))
            linkFamilyId = getIntent().getStringExtra(DATA);
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Create Family");
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void showHideStatus(int i) {
        if (i == 1) {
            if(!isExistingUser && toCreateFamily){
                goBack.setVisibility( View.INVISIBLE );
            }
            barStatus=1;
            round1.setCardBackgroundColor(getResources().getColor(R.color.greenTextColor));
            line1.setBackgroundColor(getResources().getColor(R.color.greenTextColor));
        }
        if (i == 2) {
            goBack.setVisibility( View.VISIBLE );
            barStatus=2;
            round2.setCardBackgroundColor(getResources().getColor(R.color.greenTextColor));
            line2.setBackgroundColor(getResources().getColor(R.color.greenTextColor));

        }

        if (i == 3) {
            goBack.setVisibility( View.VISIBLE );
            barStatus=3;
            line3.setBackgroundColor(getResources().getColor(R.color.greenTextColor));
            round3.setCardBackgroundColor(getResources().getColor(R.color.greenTextColor));


        }
        if (i == 4) {
            goBack.setVisibility( View.VISIBLE );
            barStatus=4;
            round4.setCardBackgroundColor(getResources().getColor(R.color.greenTextColor));
        }

    }

    @Override
    public void loadFamilyCreationLevelOne(Family family) {
        this.family = family;
        fragmentManager.beginTransaction()
                .add(createFamilyStepsLoader.getId(), FamilyCreationLevelOneFragment.newInstance(family,isExistingUser))
                .commit();
        showHideStatus(1);
    }


    @Override
    public void loadFamilyCreationLevelTwo(Family family) {
        this.family = family;
        flag=1;
        fragmentManager.beginTransaction()
                .add(createFamilyStepsLoader.getId(), FamilyCreationLevelTwoFragment.newInstance(family))
                .addToBackStack("FamilyCreationLevelTwoFragment")
                .commit();
        showHideStatus(2);

    }

    @Override
    public void loadFamilyCreationLevelThree(Family family) {
        this.family = family;
        fragmentManager.beginTransaction()
                .add(createFamilyStepsLoader.getId(), FamilyCreationLevelThreeFragment.newInstance(family))
                .addToBackStack("FamilyCreationLevelThreeFragment")
                .commit();
        showHideStatus(3);

    }

    @Override
    public void loadFamilyCreationLevelFour(Family family) {
        this.family = family;
        fragmentManager.beginTransaction()
                .add(createFamilyStepsLoader.getId(), FamilyCreationLevelFourFragment.newInstance(family,getIntent().getBooleanExtra( TO_CREATE_FAMILY, false)))
                .addToBackStack("FamilyCreationLevelFourFragment")
                .commit();
        showHideStatus(4);

    }

    @Override
    public void loadFamilyCreationLevelAdvanced(Family family) {
        this.family = family;
        fragmentManager.beginTransaction()
                .add(createFamilyStepsLoader.getId(), FamilyCreationLevelAdvancedFragment.newInstance(family, false))
                .addToBackStack("FamilyCreationLevelAdvancedFragment")
                .commit();
        showHideStatus(5);

    }

    @Override
    public void loadSimilarFamilies(SimilarFamilyRequest similarFamilyRequest, ArrayList<Family> similarFamilies) {
        fragmentManager.beginTransaction()
                .add(createFamilyStepsLoader.getId(), SimilarFamiliesFragment.newInstance(similarFamilyRequest, similarFamilies))
                .addToBackStack("SimilarFamiliesFragment")
                .commit();
    }

    @Override
    public void showProgressDialog() {
        if(progressDialog!=null && progressDialog.isShowing()){
            //Nothing is needed since the progressbar is already showing
            return;
        }
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        Utilities.getErrorDialog(progressDialog, errorMessage);
    }

    @Override
    public FamilyCreation getFamilyCreation() {
        return familyCreation;
    }

    @Override
    public String getFamilyIdToLink() {
        return linkFamilyId;
    }

    @Override
    public boolean hasLinkedFamily() {
        return hasLinked;
    }

    @Override
    public void setLinked(boolean isLinked) {
        hasLinked = isLinked;
    }

    @Override
    public boolean familyNeedsLinking() {
        return (linkFamilyId != null && linkFamilyId.length() > 0);
    }

    @Override
    public void onFamilyCreated(Family family) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        SharedPref.setUserHasFamily(true);
        Intent familyIntent = new Intent(getApplicationContext(), FamilyDashboardActivity.class);
        familyIntent.putExtra(DATA, family);
        familyIntent.putExtra(IS_CREATED_NOW, true);
        familyIntent.putExtra(TO_CREATE_FAMILY, toCreateFamily);
        startActivity(familyIntent);
        finish();
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        SharedPref.setUserHasFamily(true);
        Intent intent = new Intent();
        intent.putExtra(DATA, family);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        Utilities.getErrorDialog(this, Constants.SOMETHING_WRONG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.createFamilyStepsLoader);
        assert fragment != null;
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        barStatus=barStatus-1;
        Fragment currentVisibleFragment = getSupportFragmentManager().findFragmentById(R.id.createFamilyStepsLoader);
        if (currentVisibleFragment instanceof FamilyCreationLevelOneFragment) {
            if(barStatus==1 && !isExistingUser && toCreateFamily){
                goBack.setVisibility( View.INVISIBLE );
            }
            if(barStatus==0 && toCreateFamily)
            {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
            }
            round2.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey500));
            line2.setBackgroundColor(getResources().getColor(R.color.quantum_grey500));
            round3.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey500));
            line3.setBackgroundColor(getResources().getColor(R.color.quantum_grey500));
        } else if (currentVisibleFragment instanceof FamilyCreationLevelTwoFragment) {
            round3.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey500));
            line3.setBackgroundColor(getResources().getColor(R.color.quantum_grey500));
        }
        overridePendingTransition(R.anim.left,
                R.anim.right);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (v instanceof EditText) {
                    Rect outRect = new Rect();
                    v.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        v.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.dispatchTouchEvent(event);
    }
}
