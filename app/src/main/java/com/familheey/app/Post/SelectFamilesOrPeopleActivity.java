package com.familheey.app.Post;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.Adapters.ShareViewPagerAdapter;
import com.familheey.app.Fragments.FragmentFamilyShare;
import com.familheey.app.Fragments.FragmentPeopleShare;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.PeopleShareResponse;
import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.IS_INVITATION;

public class SelectFamilesOrPeopleActivity extends AppCompatActivity {
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;

    @BindView(R.id.txt_done)
    TextView txt_done;
    @BindView(R.id.search_share)
    EditText search_share;

    @BindView(R.id.share_tabLayout)
    TabLayout tabLayout;
    private String eventId = "";

    private ViewPager viewPager;

    private ShareViewPagerAdapter adapter;
    private boolean isInvitation = false;
    private String type = "";
    private String refid = "";
    private String from = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_event);
        ButterKnife.bind(this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            type = b.getString("type");
        }

        assert b != null;
        if(b.getString("refid")!=null){
            refid=b.getString("refid");
        }

        if(b.getString("from")!=null){
            from=b.getString("from");
        }

        eventId = getIntent().getStringExtra(Constants.Bundle.DATA);
        isInvitation = getIntent().getBooleanExtra(IS_INVITATION, false);
        tabLayout.setVisibility(View.GONE);
        viewPager = findViewById(R.id.share_eventPager);


        initializeToolbar();
        initializetabs();
    }

    private void initializetabs() {
        adapter = new ShareViewPagerAdapter(getSupportFragmentManager());

        if (type.equals("FAMILY")) {
            adapter.AddFragments(FragmentFamilyShare.newInstance(eventId, isInvitation,true,refid,from), "FAMILY");
        } else {

            adapter.AddFragments(FragmentPeopleShare.newInstance(eventId, isInvitation,true), "PEOPLE");
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
    }

    private void initializeToolbar() {

        if (type.equals("FAMILY"))
            toolBarTitle.setText("Select Family");
        else
            toolBarTitle.setText("Select People");
        goBack.setOnClickListener(v -> onBackPressed());

        txt_done.setVisibility(View.VISIBLE);

        txt_done.setOnClickListener(v -> onBack());
    }

    @Override
    public void onBackPressed() {
        onBack();
    }


    private void onBack(){
        if (type.equals("FAMILY")) {
            FragmentFamilyShare fragmentFamilyShare = (FragmentFamilyShare) adapter.getRegisteredFragment(0);
            if (fragmentFamilyShare != null){
                ArrayList<SelectFamilys> family=new ArrayList<>();
                ArrayList<Family> families = fragmentFamilyShare.linkFamilyAdapter.getSelectedList();
                for (int i = 0; i < families.size(); i++) {
                    if (families.get(i).isDevSelected()) {
                        SelectFamilys obj=new SelectFamilys();
                        obj.setId(families.get(i).getId()+"");
                        obj.setPost_create(families.get(i).getPostCreate());
                        family.add(obj);
                    }
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("DATA",new Gson().toJson(family));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        } else {
            FragmentPeopleShare fragmentPeopleShare = (FragmentPeopleShare) adapter.getRegisteredFragment(0);
            if (fragmentPeopleShare != null){
                ArrayList<String> people=new ArrayList<>();
                ArrayList<PeopleShareResponse.Datum> data = fragmentPeopleShare.peopleShareAdapter.getSelectedList();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).isDevSet())
                        people.add(data.get(i).getUserId()+"");
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("DATA",new Gson().toJson(people));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }


    @OnEditorAction(R.id.search_share)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            if (type.equals("FAMILY")) {
                FragmentFamilyShare fragmentFamilyShare = (FragmentFamilyShare) adapter.getRegisteredFragment(0);
                if (fragmentFamilyShare != null)
                    fragmentFamilyShare.updateQuery(search_share.getText().toString().trim());
            } else {

                FragmentPeopleShare fragmentPeopleShare = (FragmentPeopleShare) adapter.getRegisteredFragment(0);
                if (fragmentPeopleShare != null)
                    fragmentPeopleShare.updateQuery(search_share.getText().toString().trim());
            }
            return true;
        }
        return false;
    }
}