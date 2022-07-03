package com.familheey.app.Need;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.familheey.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

public class ProfileMyContributionsListingActivity extends AppCompatActivity {

    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imageBack)
    ImageView imageBack;
    @BindView(R.id.searchQuery)
    EditText searchQuery;
    @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;

    @BindView(R.id.fragmentContainer)
    FrameLayout fragmentContainer;
    String profileId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_my_contributions_listing);
        ButterKnife.bind(this);
        profileId = getIntent().getStringExtra(DATA);
        initializeToolbar();
        addFragment();
    }

    private void addFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, MyContributionsListingFragment.newInstance(profileId), "MyContributionsListingFragment")
                .disallowAddToBackStack()
                .commit();
    }

    private void initializeToolbar() {
        toolBarTitle.setText("My Contributions");
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void showKeyboard() {
        if (searchQuery.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchQuery, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    @OnClick({R.id.goBack, R.id.imgSearch, R.id.imageBack, R.id.clearSearch})
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
            MyContributionsListingFragment myContributionsListingFragment = ((MyContributionsListingFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer));
            assert myContributionsListingFragment != null;
            myContributionsListingFragment.performSearch(searchQuery.getText().toString().trim());
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

}
