package com.familheey.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.Adapters.GuestViewPagerAdapter;
import com.familheey.app.Fragments.FragmentAttending;
import com.familheey.app.Fragments.FragmentMayAttend;
import com.familheey.app.Fragments.FragmentNotAttend;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.GUEST_ATTENDING;
import static com.familheey.app.Utilities.Constants.Bundle.GUEST_MAY_ATTEND;
import static com.familheey.app.Utilities.Constants.Bundle.GUEST_NOT_ATTENDING;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.Bundle.PUSH;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
public class GuestActivity extends AppCompatActivity {


    String eventID = "";
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.searchText)
    EditText searchText;
    @BindView(R.id.guest_tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.guest_eventPager)
    ViewPager viewPager;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    @BindView(R.id.txtShowingResult)
    TextView txtShowingResult;
    private String notificationId = "";
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        ButterKnife.bind(this);
        toolBarTitle.setText("Guests");
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.Bundle.DATA)) {
                eventID = extras.getString(Constants.Bundle.DATA, "");
            }
        }
        GuestViewPagerAdapter adapter = new GuestViewPagerAdapter(getSupportFragmentManager());
        fragmentArrayList.add(FragmentAttending.newInstance(eventID));
        fragmentArrayList.add(FragmentMayAttend.newInstance(eventID));
        fragmentArrayList.add(FragmentNotAttend.newInstance(eventID));
        adapter.AddFragments(fragmentArrayList.get(0), "ATTENDING");
        adapter.AddFragments(fragmentArrayList.get(1), "MAY ATTEND");
        adapter.AddFragments(fragmentArrayList.get(2), "NOT ATTENDING");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
        setDesiredTab(Objects.requireNonNull(getIntent().getStringExtra(POSITION)));
        initializeSearchClearCallback();
        // Modified By: Dinu(22/02/2021) For update visible_status="read" in firebase
        if (getIntent().hasExtra(NOTIFICATION_ID)) {
            notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
            database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(notificationId).child("visible_status").setValue("read");
        }
    }


    private void initializeSearchClearCallback() {
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    clearSearch.setVisibility(View.INVISIBLE);
                else clearSearch.setVisibility(View.VISIBLE);
            }
        });
        clearSearch.setOnClickListener(v -> {
            txtShowingResult.setVisibility(View.GONE);
            searchText.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void setDesiredTab(String tab) {
        switch (tab) {
            case GUEST_MAY_ATTEND:
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
                break;
            case GUEST_ATTENDING:
                Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                break;
            case GUEST_NOT_ATTENDING:
                Objects.requireNonNull(tabLayout.getTabAt(2)).select();
                break;
        }
    }

    @OnEditorAction(R.id.searchText)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            updateFragment(searchText.getText().toString());
            return true;
        }
        return false;
    }

    private void updateFragment(String text) {
        if (text.length() == 0) {
            txtShowingResult.setVisibility(View.GONE);
        } else {
            txtShowingResult.setVisibility(View.VISIBLE);
        }

        txtShowingResult.setText("Showing results for \"" + text + "\"");
        ((FragmentAttending) fragmentArrayList.get(0)).attending(text);
        ((FragmentMayAttend) fragmentArrayList.get(1)).mayattend(text);
        ((FragmentNotAttend) fragmentArrayList.get(2)).notattend(text);
    }

    @OnClick(R.id.goBack)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().hasExtra(TYPE)) {
            Intent intent = new Intent(this, CreatedEventDetailActivity.class).putExtra(TYPE, PUSH).putExtra(ID, eventID);
            startActivity(intent);
        }
        finish();
    }
}
