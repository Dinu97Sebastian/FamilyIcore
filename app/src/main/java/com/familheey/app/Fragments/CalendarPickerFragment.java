package com.familheey.app.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.familheey.app.Decorators.EventDecorator;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Models.Response.FetchCalendarResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_MULTIPLE;


public class CalendarPickerFragment extends DialogFragment implements OnDateSelectedListener {

    private static final String EVENT_DETAIL = "eventDetail";
    private static final String START_END = "startEnd";
    @BindView(R.id.buttonCancel)
    Button buttonCancel;
    @BindView(R.id.buttonOk)
    Button buttonOk;
    @BindView(R.id.progressCalendar)
    ProgressBar progressCalendar;

    private MaterialCalendarView calendarView;
    private FetchCalendarResponse fetchCalendarResponse;
    private CalendarDay selectedDate;
    private boolean setMinimumDateAsCurrent = false;
    private PickerListener pickerListener;

    private final List<FetchCalendarResponse.Datum> listData = new ArrayList<>();
    View view;
    private EventDetail eventDetail;
    private int startEnd;

    public CalendarPickerFragment() {
        // Required empty public constructor
    }

    public static CalendarPickerFragment newInstance(boolean setMinimumDateAsCurrent, EventDetail eventDetail, int i) {
        CalendarPickerFragment fragment = new CalendarPickerFragment();
        Bundle args = new Bundle();
        args.putBoolean(IDENTIFIER, setMinimumDateAsCurrent);
        args.putParcelable(EVENT_DETAIL, eventDetail);
        args.putInt(START_END, i);
        fragment.setArguments(args);
        return fragment;
    }

    public void myListener(CalendarPickerFragment.PickerListener pickerListener) {
        this.pickerListener = pickerListener;
    }

    private void initCalendar() {
        calendarView = view.findViewById(R.id.eventCalendar);
        calendarView.setWeekDayTextAppearance(R.style.CustomTextAppearance);
        calendarView.setHeaderTextAppearance(R.style.CustomTextAppearance);
        calendarView.setDateTextAppearance(R.style.CustomTextAppearance);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.customMonths)));
        calendarView.setOnDateChangedListener(this);
        calendarView.setSelectionMode(SELECTION_MODE_MULTIPLE);
        calendarView.setTopbarVisible(true);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        calendarView.allowClickDaysOutsideCurrentMonth();
        if (setMinimumDateAsCurrent)
            calendarView.state().edit().setMinimumDate(LocalDate.now()).commit();
        if (eventDetail != null) {
            try {
                /*if (eventDetail.getFromDate() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(eventDetail.getFromDate() * 1000);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    calendarView.state().edit().setMinimumDate(CalendarDay.from(year, month, day)).commit();
                }
                if (eventDetail.getToDate() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(eventDetail.getToDate() * 1000);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    calendarView.state().edit().setMaximumDate(CalendarDay.from(year, month, day)).commit();
                }*/
                long highLightedDate = 0;
                if (startEnd == 0) {
                    highLightedDate = eventDetail.getFromDate() * 1000;
                } else {
                    highLightedDate = eventDetail.getToDate() * 1000;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(highLightedDate);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                calendarView.setSelectedDate(CalendarDay.from(year, month, day));
            } catch (Exception e) {
                e.printStackTrace();
                calendarView.setSelectedDate(CalendarDay.today());
            }
        } else {
            calendarView.setSelectedDate(CalendarDay.today());
        }
        selectedDate = calendarView.getSelectedDate();
        calendarView.state().edit().commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
        setMinimumDateAsCurrent = getArguments().getBoolean(IDENTIFIER);
        eventDetail = getArguments().getParcelable(EVENT_DETAIL);
        startEnd = getArguments().getInt(START_END);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar_picker, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initListeners();
        fetchCalendarApi();
    }

    private void initListeners() {
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getDialog()).cancel();
            }
        });


        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDate != null) {
                    pickerListener.getSelectedDate(selectedDate);
                    Objects.requireNonNull(getDialog()).dismiss();
                } else {
                    Toast.makeText(getContext(), "Please select date !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchCalendarApi() {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(FamilheeyApplication.getInstance());
        apiServiceProvider.fetchCalendar(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgress();
                if (calendarView != null) {
                    calendarView.setVisibility(View.VISIBLE);
                }

                fetchCalendarResponse = new Gson().fromJson(responseBodyString, FetchCalendarResponse.class);
                listData.clear();
                if (fetchCalendarResponse != null && fetchCalendarResponse.getData() != null && fetchCalendarResponse.getData().size() > 0)
                    listData.addAll(fetchCalendarResponse.getData());
                List<CalendarDay> eventDates = new ArrayList<>();
                initCalendar();
                Calendar[] calendars = new Calendar[listData.size()];
                for (int i = 0; i < listData.size(); i++) {
                    try {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(listData.get(i).getFromDate() * 1000);
                        LocalDate date = Instant.ofEpochMilli(cal.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
                        eventDates.add(CalendarDay.from(date));
                        calendars[i] = cal;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    calendarView.removeDecorators();
                    calendarView.addDecorator(new EventDecorator(getContext(), R.color.greenTextColor, eventDates));
                    calendarView.state().edit().commit();
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgress();
            }
        });
    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        selectedDate = date;
    }

    public interface PickerListener {
        void getSelectedDate(CalendarDay selectedDates);
    }
}