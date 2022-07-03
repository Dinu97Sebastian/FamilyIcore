package com.familheey.app.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.familheey.app.Activities.CalendarActivity;
import com.familheey.app.Activities.CreateEventActivity;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Adapters.EventTabAdapter;
import com.familheey.app.CustomViews.FSpinner;
import com.familheey.app.Fragments.Events.ExploreFragment;
import com.familheey.app.Fragments.Events.InvitationFragment;
import com.familheey.app.Fragments.Events.MyEventsFragment;
import com.familheey.app.Interfaces.HomeInteractor;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Utilities.hideCircularReveal;
import static com.familheey.app.Utilities.Utilities.showCircularReveal;

public class EventFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private static final SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    private EventTabAdapter eventTabAdapter;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.bellNotificationCount)
    NotificationBadge bellNotificationCount;
    @BindView(R.id.bellIcon)
    ImageView bellIcon;
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.search_post)
    EditText searchFamily;
    @BindView(R.id.createEvent)
    CardView createEvent;
    @BindView(R.id.stype)
    FSpinner types;
    @BindView(R.id.custome_view)
    LinearLayout custome_view;
    @BindView(R.id.etxt_from_date)
    EditText etxtFromDate;
    @BindView(R.id.etxt_to_date)
    EditText etxtToDate;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.eventPager)
    ViewPager viewPager;
    @BindView(R.id.feedback)
    ImageView feedback;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    @BindView(R.id.searchLabelIndicator)
    TextView searchLabelIndicator;

    @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;


    @BindView(R.id.imgSearch)
    ImageView imgSearch;


    @BindView(R.id.imageBack)
    ImageView imageBack;

    private HomeInteractor mListener;
    private final String query = "";
    private JsonObject jsonObject = new JsonObject();
    private String customFromDate, customToDate;
    Calendar calendar;
    private Integer fromMonthOfYear, fromYear, fromDayOfMonth;
    private Integer toYear, toMonthOfYear, toDayOfMonth;

    public EventFragment() {
        // Required empty public constructor
    }

    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeToolbar();
        initializeFilter();
        initializeViewPager();
        initListener();
        initializeSearchClearCallback();
        Utilities.setEditTextHorizontallyScrollable(getContext(), etxtFromDate, etxtToDate);
    }


    private void initListener() {
        imgSearch.setOnClickListener(view -> {
            showCircularReveal(constraintSearch);
            profileImage.setEnabled(false);
            showKeyboard();

        });

        imageBack.setOnClickListener(view -> {
            hideCircularReveal(constraintSearch);
            profileImage.setEnabled(true);
            searchFamily.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);

        });
    }


    private void showKeyboard() {
        if (searchFamily.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchFamily, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    private void initializeSearchClearCallback() {
        searchFamily.addTextChangedListener(new TextWatcher() {

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
            searchLabelIndicator.setVisibility(View.GONE);
            searchFamily.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void initializeViewPager() {
        eventTabAdapter = new EventTabAdapter(getChildFragmentManager());
        eventTabAdapter.addFragment(new InvitationFragment(), "Invitations");
        eventTabAdapter.addFragment(new MyEventsFragment(), "My Events");
        eventTabAdapter.addFragment(new ExploreFragment(), "Explore");
        viewPager.setAdapter(eventTabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        viewPager.setOffscreenPageLimit(3);

    }

    private void initializeFilter() {
        String[] colors = {"All Events", "Today", "Tomorrow", "Custom"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, colors);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        types.setAdapter(spinnerArrayAdapter);
        types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0: {
                        custome_view.setVisibility(View.GONE);
                        jsonObject = new JsonObject();
                        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                        jsonObject.addProperty("query", query);
                        updateEvents(jsonObject);
                        break;
                    }
                    /*case 1:{
                        custome_view.setVisibility(View.GONE);
                        getOnlineEvents();
                        break;
                    }*/
                    case 1: {
                        custome_view.setVisibility(View.GONE);
                        getToadayevent();
                        break;
                    }
                    case 2: {
                        custome_view.setVisibility(View.GONE);
                        getTommorowEvent();
                        break;
                    }
                    case 3: {
                        custome_view.setVisibility(View.VISIBLE);
                        break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        etxtFromDate.setOnClickListener(v -> {
            datePicker(etxtFromDate, "FROM");
        });
        etxtToDate.setOnClickListener(v -> {
            datePicker(etxtToDate, "TO");
        });
    }

    private void getOnlineEvents() {
        jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("from_date", "");
        jsonObject.addProperty("to_date", "");
        jsonObject.addProperty("filter", "online");
        jsonObject.addProperty("query", query);
        updateEvents(jsonObject);
    }

    private void getToadayevent() {
        jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("from_date", "");
        jsonObject.addProperty("to_date", "");
        jsonObject.addProperty("filter", "today");
        jsonObject.addProperty("query", query);
        updateEvents(jsonObject);
    }

    private void getTommorowEvent() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("from_date", "");
        jsonObject.addProperty("to_date", "");
        jsonObject.addProperty("filter", "tomorrow");
        jsonObject.addProperty("query", query);
        updateEvents(jsonObject);
    }

    private void datePicker(EditText editText, String type) {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        if (type.equals("FROM") && fromDayOfMonth != null) {
            mYear = fromYear;
            mMonth = fromMonthOfYear;
            mDay = fromDayOfMonth;
        } else if (toDayOfMonth != null) {
            mYear = toYear;
            mMonth = toMonthOfYear;
            mDay = toDayOfMonth;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    if (type.equals("FROM")) {
                        fromYear = year;
                        fromMonthOfYear = monthOfYear;
                        fromDayOfMonth = dayOfMonth;
                        customFromDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    } else {
                        toYear = year;
                        toMonthOfYear = monthOfYear;
                        toDayOfMonth = dayOfMonth;
                        customToDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                    tiemPicker(editText, dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, type);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker(EditText editText, String date, String type) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    displayFormat.format(calendar.getTime());
                    editText.setText(displayFormat.format(calendar.getTime()));
                    if (type.equals("FROM")) {
                        customFromDate += " " + hourOfDay + ":" + minute;
                    } else {
                        customToDate += " " + hourOfDay + ":" + minute;
                        if (dateConversion(customFromDate) > dateConversion(customToDate)) {
                            editText.setText("");
                            Toast.makeText(getActivity(), "Wrong Date Selection", Toast.LENGTH_LONG).show();
                        }
                    }

                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private long dateConversion(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
            Date gmt = formatter.parse(date);
            return TimeUnit.MILLISECONDS.toSeconds(gmt.getTime());
        } catch (Exception e) {
            return 0;
        }
    }

    private void updateEvents(JsonObject jsonObject) {
        ExploreFragment exploreFragment = (ExploreFragment) eventTabAdapter.getRegisteredFragment(2);
        if (exploreFragment != null)
            exploreFragment.updateEvent(jsonObject);
        InvitationFragment invitationFragment = (InvitationFragment) eventTabAdapter.getRegisteredFragment(0);
        if (invitationFragment != null)
            invitationFragment.updateEvent(jsonObject);
        MyEventsFragment myEventsFragment = (MyEventsFragment) eventTabAdapter.getRegisteredFragment(1);
        if (myEventsFragment != null)
            myEventsFragment.updateEvent(jsonObject);
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchFamily.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Events");
        setNotificationCount(mListener.getNotificationsCount());
        profileImage.setOnClickListener(v -> {
            FamilyMember familyMember = new FamilyMember();
            familyMember.setId(Integer.parseInt(SharedPref.getUserRegistration().getId()));
            familyMember.setUserId(familyMember.getId());
            familyMember.setProPic(SharedPref.getUserRegistration().getPropic());
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra(Constants.Bundle.DATA, familyMember);
            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), profileImage, "profile");
            startActivity(intent, options.toBundle());
        });

        Utilities.loadProfilePicture(profileImage);
    }

    public void setNotificationCount(int count) {
        if (count > 99) {
            bellNotificationCount.setText("99+", true);
        }
        else if (count >0){
            bellNotificationCount.setText(String.valueOf(count), true);
        } else
            bellNotificationCount.clear(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeInteractor) {
            mListener = (HomeInteractor) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeInteractor");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.imgMessage, R.id.bellIcon, R.id.bellNotificationCount, R.id.feedback, R.id.createEvent, R.id.chipAll, R.id.chipToday, R.id.chipTomorrow, R.id.btn_custom_search, R.id.btn_calender})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgMessage:
                mListener.navigateMessageScreen();
                break;
            case R.id.bellIcon:
            case R.id.bellNotificationCount:
                mListener.loadNotifications();
                break;
            case R.id.feedback:
                Utilities.showMainMenuPopup(getContext(), feedback);
                break;
            case R.id.createEvent:
                startActivity(new Intent(getContext(), CreateEventActivity.class));
                break;
            case R.id.chipAll:
                jsonObject = new JsonObject();
                jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                jsonObject.addProperty("query", query);
                updateEvents(jsonObject);
                break;
            case R.id.chipToday:
                getToadayevent();
                break;
            case R.id.chipTomorrow:
                getTommorowEvent();
                break;
            case R.id.btn_custom_search:
                if (!etxtFromDate.getText().toString().trim().isEmpty() && !etxtToDate.getText().toString().trim().isEmpty()) {
                    jsonObject = new JsonObject();
                    jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                    jsonObject.addProperty("from_date", dateConversion(customFromDate));
                    jsonObject.addProperty("to_date", dateConversion(customToDate));
                    jsonObject.addProperty("query", query);
                    updateEvents(jsonObject);
                }
                break;
            case R.id.btn_calender:
                startActivity(new Intent(getActivity(), CalendarActivity.class));
                break;
        }
    }

    @OnEditorAction(R.id.search_post)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            jsonObject.remove("query");
            jsonObject.addProperty("query", searchFamily.getText().toString());
            updateEvents(jsonObject);
            if (!searchFamily.getText().toString().trim().isEmpty()) {
                searchLabelIndicator.setVisibility(View.VISIBLE);
                searchLabelIndicator.setText("Showing results for \"" + searchFamily.getText().toString() + "\"");
            } else {
                searchLabelIndicator.setVisibility(View.GONE);

            }
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.loadProfilePicture(profileImage);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        appBar.setExpanded(true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
