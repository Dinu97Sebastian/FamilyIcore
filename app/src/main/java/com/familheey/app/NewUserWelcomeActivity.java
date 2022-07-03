package com.familheey.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.Activities.DiscoverFamilyActivity;
import com.familheey.app.Activities.LoginActivity;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.TO_CREATE_FAMILY;

public class NewUserWelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnDiscover, btnCreate;
    private TextView slider1Discover;
    private ImageView btnBack;
    private LinearLayout isExistingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_new_user_welcome );
        ButterKnife.bind(this);
        SharedPref.write(SharedPref.IS_REGISTERED, false);
        SharedPref.write(SharedPref.USER, "");
        SharedPref.setUserRegistration(null);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnDiscover = (Button) findViewById(R.id.btn_discover);
        btnCreate = (Button) findViewById(R.id.btn_create);
        isExistingUser = (LinearLayout) findViewById(R.id.isExistingUser);
        btnBack = (ImageView) findViewById(R.id.back);
        slider1Discover=(TextView) findViewById( R.id.slider1Discover );
        layouts = new int[]{
                R.layout.welcome_slider1,
                R.layout.welcome_slider2,
                R.layout.welcome_slider3};

        // adding bottom dots
        addBottomDots(0);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    @OnClick({R.id.btn_create, R.id.btn_discover,R.id.back,R.id.btn_isExistingUser})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra(TO_CREATE_FAMILY, true);
                startActivity(intent);

                break;
            case R.id.btn_discover:
                startActivity(new Intent(getApplicationContext(), DiscoverFamilyActivity.class));
                break;
            case R.id.back:
                addBottomDots(0);
                btnCreate.setText("Create");
                btnDiscover.setText("Join");
                btnCreate.setVisibility(View.VISIBLE);
                btnDiscover.setVisibility(View.VISIBLE);
                slider1Discover.setVisibility( View.VISIBLE );
                btnBack.setVisibility( View.GONE );
                myViewPagerAdapter = new MyViewPagerAdapter();
                viewPager.setAdapter(myViewPagerAdapter);
                viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
                break;
            case R.id.btn_isExistingUser:
                Intent intents = new Intent(getApplicationContext(), LoginActivity.class);
                intents.putExtra( Constants.Bundle.IS_EXISTING_USER, true);
                startActivity(intents);
                break;
        }
    }


    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            addBottomDots(position);
            if (position == 1) {
                btnCreate.setText("Create Your Family");
                btnCreate.setVisibility(View.VISIBLE);
                btnDiscover.setVisibility(View.GONE);
                slider1Discover.setVisibility( View.GONE );
                btnBack.setVisibility( View.VISIBLE );
                isExistingUser.setVisibility(View.GONE);


            } else if(position==2)
            {
                btnDiscover.setText("Join Other Families");
                btnCreate.setVisibility(View.GONE);
                btnDiscover.setVisibility(View.VISIBLE);
                slider1Discover.setVisibility( View.GONE );
                btnBack.setVisibility( View.VISIBLE );
                isExistingUser.setVisibility(View.GONE);
            }
            else{
                btnCreate.setText("Create");
                btnDiscover.setText("Join");
                btnCreate.setVisibility(View.VISIBLE);
                btnDiscover.setVisibility(View.VISIBLE);
                slider1Discover.setVisibility( View.VISIBLE );
                btnBack.setVisibility( View.GONE );
                isExistingUser.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };



    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor( Color.TRANSPARENT);
        }
    }
    private void addBottomDots(int currentPage) {

        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText( Html.fromHtml("&#8226;"));
            dots[i].setTextSize(50);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}