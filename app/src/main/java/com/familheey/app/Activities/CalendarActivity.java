package com.familheey.app.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.CalendarAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Decorators.EventDecorator;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FetchCalendarResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.EventsParser;
import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import org.joda.time.DateTime;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.ApiFlags.FETCH_CALENDAR;
import static com.familheey.app.Utilities.Constants.ApiFlags.FETCH_GROUP_EVENTS;
import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.FAMILY;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
public class CalendarActivity extends AppCompatActivity implements OnDateSelectedListener, RetrofitListener {

    private static final String PRIVACY_ALL = "-1";
    private static final String PRIVACY_PRIVATE = "false";
    private static final String PRIVACY_PUBLIC = "true";
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.searchQuery)
    EditText searchQuery;
    @BindView(R.id.isPublic)
    MaterialCheckBox isPublic;
    @BindView(R.id.isPrivate)
    MaterialCheckBox isPrivate;
    @BindView(R.id.displayFamilyFilter)
    TextView displayFamilyFilter;
    @BindView(R.id.clearSelectedFamily)
    ImageView clearSelectedFamily;
    @BindView(R.id.clearSearch)
    ImageView clearSearch;
    @BindView(R.id.yearIndicator)
    ImageView yearIndicator;
    @BindView(R.id.emptyIndicator)
    SemiBoldTextView emptyIndicator;
    @BindView(R.id.scrollBar)
    NestedScrollView scrollBar;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    private Family family = null;

    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private MaterialCalendarView calendarView;
    private String privacyType = PRIVACY_ALL;

    private final List<FetchCalendarResponse.Datum> listData = new ArrayList<>();
    private final List<FetchCalendarResponse.Datum> listDataFiltered = new ArrayList<>();
    private ProgressBar progressCalendar;
    private String selectedDate = getCurrentDate();
    private String notificationId = "";
    private DatabaseReference database;
    public static String getDate(long calendarTime, boolean isTimeInMilliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        if (isTimeInMilliSeconds)
            calendar.setTimeInMillis(calendarTime);
        else
            calendar.setTimeInMillis(calendarTime * 1000);
        return formatter.format(calendar.getTime());
    }

    void showProgress() {
        if (progressCalendar != null) {
            progressCalendar.setVisibility(View.VISIBLE);
        }
    }

    void hideProgress() {
        if (progressCalendar != null) {
            progressCalendar.setVisibility(View.GONE);
        }
    }

    public static String getDate(CalendarDay calendarDay) {
        return calendarDay.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }


    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return getDate(calendar.getTimeInMillis(), true);
    }

    public static CalendarDay getCurrentCalendarDate() {
        Calendar calendar = Calendar.getInstance();
        LocalDate localDate = LocalDateTime.ofInstant(DateTimeUtils.toInstant(calendar), DateTimeUtils.toZoneId(calendar.getTimeZone())).toLocalDate();
        return CalendarDay.from(localDate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        recyclerView = findViewById(R.id.recyclerView);
        progressCalendar = findViewById(R.id.progressCalendar);
        initAdapter();
        initializeToolbar();
        initializeRestrictions();
        fetchCalendarApi();
        initializeSearchClearCallback();
        onDateSelected(calendarView, getCurrentCalendarDate(), true);
        // Modified By: Dinu(22/02/2021) For update visible_status="read" in firebase
        if (getIntent().hasExtra(NOTIFICATION_ID)) {
            notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
            database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(notificationId).child("visible_status").setValue("read");
        }
    }

    private void initializeRestrictions() {
        if (getIntent() == null)
            return;
        if (getIntent().hasExtra(IDENTIFIER) && Objects.equals(getIntent().getStringExtra(IDENTIFIER), FAMILY)) {
            family = getIntent().getParcelableExtra(DATA);
        }
    }

    private void initializeSearchClearCallback() {
        searchQuery.addTextChangedListener(new TextWatcher() {

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
            searchQuery.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Calendar");
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void initAdapter() {
        calendarAdapter = new CalendarAdapter(this, listDataFiltered);
        calendarView = findViewById(R.id.eventCalendar);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(calendarAdapter);
    }

    private void initCalendar() {
        calendarView.setWeekDayTextAppearance(R.style.CustomTextAppearance);
        calendarView.setHeaderTextAppearance(R.style.CustomTextAppearance);
        calendarView.setDateTextAppearance(R.style.CustomTextAppearance);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.customMonths)));
        calendarView.setOnDateChangedListener(this);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        calendarView.setTopbarVisible(true);
        calendarView.allowClickDaysOutsideCurrentMonth();
        calendarView.setOnTitleClickListener(v -> openDatePicker());
    }

    private void fetchCalendarApi() {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("query", searchQuery.getText().toString());
        jsonObject.addProperty("type", privacyType);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(FamilheeyApplication.getInstance());
        if (family != null) {
            jsonObject.addProperty("group_id", family.getId().toString());
            apiServiceProvider.fetchGroupEvents(jsonObject, null, this);
        } else
            apiServiceProvider.fetchCalendar(jsonObject, null, this);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        selectedDate = getDate(date);
        calendarView.setSelectedDate(date);
        filterResult();
    }

    private void filterResult() {
        listDataFiltered.clear();
        for (FetchCalendarResponse.Datum event : listData) {
            event.getEventFormattedDates();
            if (event.getEventFormattedDates().contains(selectedDate)) {
                if (!listDataFiltered.contains(event))
                    listDataFiltered.add(event);
            }
        }
        if (listDataFiltered != null && listDataFiltered.size() > 0){
            emptyIndicator.setVisibility(View.INVISIBLE); startScroll();}
        else{ emptyIndicator.setVisibility(View.VISIBLE); stopScroll();}
        calendarAdapter.notifyDataSetChanged();

    }

    @OnEditorAction(R.id.searchQuery)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            fetchCalendarApi();
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(searchQuery.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void openDatePicker() {
        CalendarDay calendarDay = calendarView.getCurrentDate();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendarDay.getYear(), calendarDay.getMonth() - 1, calendarDay.getDay());
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, year, month, dayOfMonth) -> {
            CalendarDay today = CalendarDay.from(year, month + 1, dayOfMonth);
            calendarView.setCurrentDate(today);
            //calendarView.setSelectedDate(today);
            calendarView.state().edit();
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            datePickerDialog.getDatePicker().getTouchables().get(1).performClick();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
        }

    }

    @OnCheckedChanged({R.id.isPrivate, R.id.isPublic})
    void onPrivatePublicChanged(CompoundButton checkBox, boolean checked) {
        if (isPrivate.isChecked() && isPublic.isChecked()) {
            privacyType = PRIVACY_ALL;
        } else if (isPrivate.isChecked()) {
            privacyType = PRIVACY_PRIVATE;
        } else if (isPublic.isChecked()) {
            privacyType = PRIVACY_PUBLIC;
        } else {
            checkBox.setChecked(true);
            if (checkBox.getId() == R.id.isPrivate)
                privacyType = PRIVACY_PRIVATE;
            else privacyType = PRIVACY_PUBLIC;
        }
        if (isPublic.isChecked() || isPrivate.isChecked()) {
            fetchCalendarApi();
        } else {
            listData.clear();
            listDataFiltered.clear();
            calendarAdapter.notifyDataSetChanged();
            calendarView.removeDecorators();
        }
    }

    private void clearFamilyFilter() {
        family = null;
        clearSelectedFamily.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectFamilyActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                family = data.getParcelableExtra(DATA);
                assert family != null;
                displayFamilyFilter.setText(family.getGroupName());
                clearSelectedFamily.setVisibility(View.VISIBLE);
                fetchCalendarApi();
            }
        }
    }

    @OnClick({R.id.clearSelectedFamily, R.id.displayFamilyFilter, R.id.yearIndicator})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearSelectedFamily:
                clearFamilyFilter();
                displayFamilyFilter.setText("Select Family");
                fetchCalendarApi();
                break;
            case R.id.displayFamilyFilter:
                Intent intent = new Intent(getApplicationContext(), SelectFamilyActivity.class);
                startActivityForResult(intent, SelectFamilyActivity.REQUEST_CODE);
                break;
            case R.id.yearIndicator:
                openDatePicker();
                break;
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        switch (apiFlag) {
            case FETCH_GROUP_EVENTS:
                listData.clear();
                listData.addAll(EventsParser.parseFamilyCalendarEvents(responseBodyString));
                break;
            case FETCH_CALENDAR:
                FetchCalendarResponse fetchCalendarResponse = new Gson().fromJson(responseBodyString, FetchCalendarResponse.class);
                listData.clear();
                listData.addAll(fetchCalendarResponse.getData());
                break;
        }
        listDataFiltered.clear();
        listDataFiltered.addAll(listData);
        calendarAdapter.notifyDataSetChanged();
        hideProgress();
        List<CalendarDay> eventDates = new ArrayList<>();
        initCalendar();
        for (int i = 0; i < listData.size(); i++) {
            for (int j = 0; j < listData.get(i).getEventDates().size(); j++) {
                try {
                    DateTime dateTime = new DateTime(listData.get(i).getEventDates().get(j));
                    LocalDate date = Instant.ofEpochMilli(dateTime.getMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
                    eventDates.add(CalendarDay.from(date));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        calendarView.removeDecorators();
        calendarView.addDecorator(new EventDecorator(getApplicationContext(), R.color.greenTextColor, eventDates));
        calendarView.setSelectedDate(getCurrentCalendarDate());
        onDateSelected(calendarView, CalendarDay.from(LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))), true);
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        hideProgress();
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().hasExtra(TYPE)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
        overridePendingTransition(R.anim.left,
                R.anim.right);
    }

    public void stopScroll() {
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        collapsingToolbar.setLayoutParams(toolbarLayoutParams);
    }

    public void startScroll() {
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        collapsingToolbar.setLayoutParams(toolbarLayoutParams);
    }
}
