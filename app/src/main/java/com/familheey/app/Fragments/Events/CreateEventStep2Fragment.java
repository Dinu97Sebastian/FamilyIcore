package com.familheey.app.Fragments.Events;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import com.familheey.app.Activities.AddressFetchingActivity;
import com.familheey.app.Activities.CreateEventActivity;
import com.familheey.app.Fragments.CalendarPickerFragment;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.ApiFlags.CHECK_LINK;
import static com.familheey.app.Utilities.Constants.ApiFlags.CREATE_EVENTS;
import static com.familheey.app.Utilities.Constants.Bundle.ADDRESS;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.PLACE;

public class CreateEventStep2Fragment extends Fragment implements RetrofitListener {

    private static final SimpleDateFormat userDisplay = new SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.getDefault());
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.hosted_by)
    EditText hostedBy;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.rsvp_yes)
    RadioButton rsvpYes;
    @BindView(R.id.rsvp_no)
    RadioButton rsvpNo;

    @BindView(R.id.url)
    EditText url;

    @BindView(R.id.edtxDateTimeStart)
    EditText edtxDateTimeStart;
    @BindView(R.id.edtxDateTimeEnd)
    EditText edtxDateTimeEnd;
    @BindView(R.id.materialButtonDone)
    Button materialButtonDone;
    boolean rsvpRequired = true;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.venue)
    EditText venue;
    @BindView(R.id.textViewUrlCheck)
    TextView textViewUrlCheck;

    @BindView(R.id.offlineGroup)
    Group offlineGroup;
    @BindView(R.id.onlineGroup)
    Group onlineGroup;
    @BindView(R.id.webinar)
    EditText webinar;
    @BindView(R.id.dialInNumber)
    EditText dialInNumber;
    @BindView(R.id.participantPin)
    EditText participantPin;

    SweetAlertDialog progressDialog;
    String startTime;
    String endTime;
    boolean isUpdate = false;
    private boolean isUrlVerified;
    private Calendar calendar;
    private CreateEventStep2Frag createEventStep2Frag;
    private JsonObject jsonObject;
    private EventDetail eventDetail;
    private Place eventPlace;
    private String oldUrl = "";
    private Boolean isOnlineEvent = false;
    //
    private String maximumWeeklyEndTime;
    private Integer startMonth;
    private Integer endMonth;
    private Integer startYear;
    private Integer endYear;


    private String dailyStartDate;
    private String dailyEndDate;
    private String currentTimezoneOffset;
    private String timezoneName;
//

    EditText.OnEditorActionListener onUrlAction = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                case EditorInfo.IME_ACTION_NEXT:
                case EditorInfo.IME_ACTION_PREVIOUS:
                case KeyEvent.KEYCODE_BACK:
                    validateUrl(url.getText().toString().trim());
                    return true;
                default:
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    }
            }
            return false;
        }
    };

    public CreateEventStep2Fragment() {
        // Required empty public constructor
    }

    public static CreateEventStep2Fragment newInstance(JsonObject jsonObject, EventDetail eventDetail) {
        CreateEventStep2Fragment fragment = new CreateEventStep2Fragment();
        Bundle args = new Bundle();
        args.putString(DATA, jsonObject.toString());
        args.putParcelable(Constants.Bundle.TITLE, eventDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event_step2, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(getActivity());
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            JsonParser jsonParser = new JsonParser();
            jsonObject = (JsonObject) jsonParser.parse(requireArguments().getString(DATA));
            eventDetail = getArguments().getParcelable(Constants.Bundle.TITLE);
            isUpdate = eventDetail != null;
            isOnlineEvent = "Online".equalsIgnoreCase(jsonObject.get("event_type").getAsString());

            currentTimezoneOffset = getCurrentTimezoneOffset();
            timezoneName = getTimeZoneName();

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        createEventStep2Frag = (CreateEventActivity) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolBarTitle.setText("Add Events");
        if (isOnlineEvent) {
            onlineGroup.setVisibility(View.VISIBLE);
            offlineGroup.setVisibility(View.GONE);
        } else {
            onlineGroup.setVisibility(View.GONE);
            offlineGroup.setVisibility(View.VISIBLE);
        }
        edtxDateTimeStart.setOnClickListener(view -> showDateTimePicker(0));
        textViewUrlCheck.setOnClickListener(view -> validateUrl(url.getText().toString().trim()));
        edtxDateTimeEnd.setOnClickListener(view -> {
            if (edtxDateTimeStart.getText().toString().trim().length() > 0)
                showDateTimePicker(1);
            else
                Snackbar.make(scrollView, "     Please enter start date", BaseTransientBottomBar.LENGTH_LONG).show();
        });
        if (isUpdate) {
            isUrlVerified = true;
            materialButtonDone.setText("Update Event");
            if (eventDetail.getEventHost() != null)
                hostedBy.setText(eventDetail.getEventHost());
            else hostedBy.setText("");
            description.setText(eventDetail.getDescription());
            String StartTime=convertToUserFormat(eventDetail.getFromDate());
            Long ss=eventDetail.getFromDate();


            Calendar fromCalender = Calendar.getInstance();
            Date d = new Date(eventDetail.getFromDate()*1000);
            fromCalender.setTime(d);
            int year = fromCalender.get(Calendar.YEAR);
            int month = fromCalender.get(Calendar.MONTH)+1;
            int date = fromCalender.get(Calendar.DATE);

            maximumWeeklyEndTime = convertToUserFormat(fromCalender.get(Calendar.YEAR),fromCalender.get(Calendar.MONTH)+1,fromCalender.get(Calendar.DATE)+6,23,59);
            dailyStartDate=String.valueOf(fromCalender.get(Calendar.YEAR))+"-"+String.valueOf(fromCalender.get(Calendar.MONTH)+1)+"-"+String.valueOf(fromCalender.get(Calendar.DATE));
            startMonth=fromCalender.get(Calendar.MONTH)+1;
            startYear=fromCalender.get(Calendar.YEAR);
            Calendar ToCalender = Calendar.getInstance();
            Date endDate = new Date(eventDetail.getToDate()*1000);
            ToCalender.setTime(endDate);
            dailyEndDate=String.valueOf(ToCalender.get(Calendar.YEAR))+"-"+String.valueOf(ToCalender.get(Calendar.MONTH)+1)+"-"+String.valueOf(ToCalender.get(Calendar.DATE));
            endMonth=ToCalender.get(Calendar.MONTH)+1;
            endYear=ToCalender.get(Calendar.YEAR);


            edtxDateTimeStart.setText(convertToUserFormat(eventDetail.getFromDate()));
            edtxDateTimeEnd.setText(convertToUserFormat(eventDetail.getToDate()));
            url.setText(eventDetail.getEventPage());


            if (eventDetail.getEventPage() != null) {
                oldUrl = eventDetail.getEventPage();
            }
            if (eventDetail.getRsvp())
                rsvpYes.setChecked(true);
            else
                rsvpNo.setChecked(true);

            if (isOnlineEvent) {
                webinar.setText(eventDetail.getMeetingLink() == null ? "" : eventDetail.getMeetingLink());
                dialInNumber.setText(eventDetail.getMeetingDialNumber() == null ? "" : eventDetail.getMeetingDialNumber());
                participantPin.setText(eventDetail.getMeetingPin() == null ? "" : eventDetail.getMeetingPin());
            } else {
                venue.setText(eventDetail.getLocation());
            }
        } else {
            hostedBy.setText(SharedPref.getUserRegistration().getFullName());
            materialButtonDone.setText("Done");
        }
    }

    private boolean validate() {
        if (oldUrl.equals(url.getText().toString()))
            isUrlVerified = true;
        rsvpRequired = rsvpYes.isChecked();
        if (hostedBy.getText().toString().trim().isEmpty()) {
            Snackbar.make(scrollView, "     Name is required", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }
        if (edtxDateTimeStart.getText().toString().trim().isEmpty()) {
            Snackbar.make(scrollView, "     Start date is required", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }
        if (edtxDateTimeEnd.getText().toString().trim().isEmpty()) {
            Snackbar.make(scrollView, "     End date is required", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }
        /**
         * for showing correct date while updating normal event to recurring events.
         */
        /*
        * megha, validation for normal events to recurring*/
        if (isUpdate && "1".equalsIgnoreCase(jsonObject.get("isRecurrence").getAsString())) {
            if (!edtxDateTimeEnd.getText().toString().trim().isEmpty() && !dailyStartDate.equals(dailyEndDate) && "daily".equalsIgnoreCase(jsonObject.get("recurrence_type").getAsString())) {
                Snackbar.make(scrollView, "  Please select start date same as end date", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;

            }else if(!edtxDateTimeEnd.getText().toString().trim().isEmpty() && edtxDateTimeEnd.getText().toString().trim().compareTo(maximumWeeklyEndTime)>0 && "weekly".equalsIgnoreCase(jsonObject.get("recurrence_type").getAsString())) {
                Snackbar.make(scrollView, "  Please select end date within same week", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;

            }else if (!edtxDateTimeEnd.getText().toString().trim().isEmpty() && (startMonth.compareTo(endMonth)!=0 || startYear.compareTo(endYear)!=0) && "monthly".equalsIgnoreCase(jsonObject.get("recurrence_type").getAsString())){
                Snackbar.make(scrollView, "  Please select end date and start date from same month", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;

            }else if(!edtxDateTimeEnd.getText().toString().trim().isEmpty() && dailyEndDate.compareTo(dailyStartDate)==0 && "weekly".equalsIgnoreCase(jsonObject.get("recurrence_type").getAsString())){
                Snackbar.make(scrollView, "  Please select end date higher than start date", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;

            }else if(!edtxDateTimeEnd.getText().toString().trim().isEmpty() && dailyEndDate.compareTo(dailyStartDate)==0 && "monthly".equalsIgnoreCase(jsonObject.get("recurrence_type").getAsString())){
                Snackbar.make(scrollView, "  Please select end date higher than start date", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;
            }

            }else if (isUpdate && "0".equalsIgnoreCase(jsonObject.get("isRecurrence").getAsString())){
            if (dailyStartDate.equals(dailyEndDate)){
                Snackbar.make(scrollView, "  Please select end date higher than start date", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;
            }
        }

        if (isOnlineEvent) {
            webinar.setText(webinar.getText().toString().trim().replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                    .replaceAll("Https:", "https:")
                    .replaceAll("HTTPS:", "https:"));
            webinar.setText(webinar.getText().toString().trim().replaceAll("WWW.", "www.").replaceAll("Www.", "www."));
            if(!webinar.getText().toString().trim().contains("http")){
                webinar.setText(webinar.getText().toString().trim().replaceAll("www.","http://www."));
            }
            if (webinar.getText().toString().trim().isEmpty()) {
                Snackbar.make(scrollView, "     Webinar link is required", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;
            }
            if (!Utilities.isValidURL(webinar.getText().toString())) {
                Snackbar.make(scrollView, "     Malformed webinar link", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;
            }
        } else {
            if (venue.getText().toString().length() < 2) {
                Snackbar.make(scrollView, "     Venue is required", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;
            }
        }
        if (!url.getText().toString().trim().isEmpty() && !isUrlVerified) {
            Snackbar.make(scrollView, "     Please check the availability of event url", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private String convertToUserFormat(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute);
        Date date = calendar.getTime();
        return userDisplay.format(date);
    }

    private String convertToUserFormat(Long timeInMillis) {
        return userDisplay.format(new Date(TimeUnit.SECONDS.toMillis(timeInMillis)));
    }

    private long convertUserFormatToApiFormat(String date) throws ParseException {
        Date parsedDate = userDisplay.parse(date);
        long millisecondsSinceEpoch0 = TimeUnit.MILLISECONDS.toSeconds(parsedDate.getTime());
        return millisecondsSinceEpoch0;
    }

    private void showDateTimePicker(int i) {
        final Calendar currentDate = Calendar.getInstance();
        calendar = Calendar.getInstance();
//datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        CalendarPickerFragment calendarPickerFragment = CalendarPickerFragment.newInstance(true,eventDetail,i);
        calendarPickerFragment.myListener(selectedDate -> {
            TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String dateTime = convertToUserFormat(selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDay(), hourOfDay, minute);
                int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
                int currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);


                if (i == 0) {
                    startMonth = (selectedDate.getMonth());
                    startYear = selectedDate.getYear();
                    int selectedDay=selectedDate.getDay();
                    maximumWeeklyEndTime = convertToUserFormat(selectedDate.getYear(),selectedDate.getMonth(),selectedDate.getDay()+6,23,59);
                    dailyStartDate=String.valueOf(selectedDate.getYear())+"-"+String.valueOf(selectedDate.getMonth())+"-"+String.valueOf(selectedDate.getDay());
                    startTime = dateTime;
                    edtxDateTimeStart.setText(dateTime);
                    boolean d=currentHour>hourOfDay && currentDay==selectedDay;
                    boolean f=currentHour==hourOfDay && currentMinute>minute && currentDay==selectedDay;
                    if(startMonth==currentMonth && startYear==currentYear){
                        if ((currentHour>hourOfDay && currentDay==selectedDay) ||(currentHour==hourOfDay && currentMinute>minute && currentDay==selectedDay)) {
                            edtxDateTimeStart.setText("");
                            Snackbar.make(scrollView, "     Please select start time higher than current time", BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    }

                    endTime = "";
                    edtxDateTimeEnd.setText("");
                } else if (i == 1) {
                    dailyEndDate=String.valueOf(selectedDate.getYear())+"-"+String.valueOf(selectedDate.getMonth())+"-"+String.valueOf(selectedDate.getDay());
                    endTime = startTime;
                    edtxDateTimeEnd.setText(dateTime);
                    endMonth = (selectedDate.getMonth());
                    endYear = selectedDate.getYear();

                    if (!compareDate()) {
                        edtxDateTimeEnd.setText("");
                        // Snackbar.make(scrollView, "     Please select end date and time higher than start date and time", BaseTransientBottomBar.LENGTH_LONG).show();
                    }

                }
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);

            datePickerDialog.show();
        });
        calendarPickerFragment.show(requireActivity().getSupportFragmentManager(), "CalendarPickerFragment");

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.removeEditableButClickable(venue);
        url.setOnEditorActionListener(onUrlAction);
        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    url.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                isUrlVerified = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private boolean compareDate() {
        try {
            Long date1 = convertUserFormatToApiFormat(edtxDateTimeStart.getText().toString());
            Long date2 = convertUserFormatToApiFormat(edtxDateTimeEnd.getText().toString());
            Long date3 = convertUserFormatToApiFormat(maximumWeeklyEndTime);

            if ("daily".equalsIgnoreCase(jsonObject.get("recurrence_type").getAsString()) && "1".equalsIgnoreCase(jsonObject.get("isRecurrence").getAsString())) {
                if (!dailyStartDate.equals(dailyEndDate)) {
                    Snackbar.make(scrollView, "     Please select end date as same as start date", BaseTransientBottomBar.LENGTH_LONG).show();
                    return false;
                }

            } else if ("weekly".equalsIgnoreCase(jsonObject.get("recurrence_type").getAsString())  && "1".equalsIgnoreCase(jsonObject.get("isRecurrence").getAsString())) {
                if (date2.compareTo(date3) > 0) {
                    Snackbar.make(scrollView, "  Please select end date within the same week", BaseTransientBottomBar.LENGTH_LONG).show();
                    return false;
                }
            }else if ("monthly".equalsIgnoreCase(jsonObject.get("recurrence_type").getAsString()) && "1".equalsIgnoreCase(jsonObject.get("isRecurrence").getAsString())) {
                if (startMonth.compareTo(endMonth)!=0 || startYear.compareTo(endYear)!=0){
                    Snackbar.make(scrollView, "     Please select end date and start date in the same month", BaseTransientBottomBar.LENGTH_LONG).show();
                    return false;
                }
            }
            if (date1.compareTo(date2) > 0) {
                Snackbar.make(scrollView, "     Please select end date and time higher than start date and time", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;
            } else if (date1.compareTo(date2) < 0) {
                return true;
            }

            else if (date1.compareTo(date2) == 0) {
                Snackbar.make(scrollView, "     Please select end date and time higher than start date and time", BaseTransientBottomBar.LENGTH_LONG).show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddressFetchingActivity.RequestCode && resultCode == Activity.RESULT_OK) {
            venue.setText(data.getStringExtra(ADDRESS));
            eventPlace = data.getParcelableExtra(PLACE);
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        hideProgressDialog();
        switch (apiFlag) {
            case CREATE_EVENTS:
                JsonParser jsonParser = new JsonParser();
                jsonObject = (JsonObject) jsonParser.parse(responseBodyString);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseBodyString);
                    JSONObject data = jsonObject.getJSONObject("data");
                    int eventId = data.getInt("id");
                    createEventStep2Frag.onEventCreateSuccessResponse(eventId, isUpdate);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case CHECK_LINK:
                isUrlVerified = true;
                url.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_success, 0);
                break;
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        hideProgressDialog();
        switch (apiFlag) {
            case CREATE_EVENTS:
                break;
            case CHECK_LINK:
                url.setText("");
                isUrlVerified = false;
                url.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                break;
        }
        Toast.makeText(getActivity(), "Event creation failed!\n" + errorData.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.goBack, R.id.venue, R.id.locationSelector, R.id.materialButtonDone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.goBack:
                requireActivity().onBackPressed();
                break;
            case R.id.locationSelector:
            case R.id.venue:
                Intent livingInAddressintent = new Intent(getContext(), AddressFetchingActivity.class);
                startActivityForResult(livingInAddressintent, AddressFetchingActivity.RequestCode);
                break;
            case R.id.materialButtonDone:
                try {
                    if (validate()) {
                        showProgressDialog();
                        jsonObject.addProperty("event_host", hostedBy.getText().toString());
                        if (isOnlineEvent) {
                            jsonObject.addProperty("meeting_link", webinar.getText().toString().trim());
                            jsonObject.addProperty("meeting_dial_number", dialInNumber.getText().toString().trim());
                            jsonObject.addProperty("meeting_pin", participantPin.getText().toString().trim());
                        } else {
                            jsonObject.addProperty("location", venue.getText().toString().trim());
                        }
                        jsonObject.addProperty("from_date", convertUserFormatToApiFormat(edtxDateTimeStart.getText().toString()));
                        jsonObject.addProperty("to_date", convertUserFormatToApiFormat(edtxDateTimeEnd.getText().toString()));
                        jsonObject.addProperty("timezone_offset",timezoneName);
                        jsonObject.addProperty("description", description.getText().toString());
                        jsonObject.addProperty("rsvp", rsvpRequired);
                        if (isUrlVerified)
                            jsonObject.addProperty("event_page", url.getText().toString());
                        if (eventPlace != null && eventPlace.getLatLng() != null) {
                            jsonObject.addProperty("lat", String.valueOf(eventPlace.getLatLng().latitude));
                            jsonObject.addProperty("long", String.valueOf(eventPlace.getLatLng().longitude));
                        }
                        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
                        if (isUpdate) {
                            jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                            //jsonObject.addProperty("timezone_offset",timezoneName);
                            // Please verify this stuff
                            jsonObject.addProperty("id", eventDetail.getEventId());
                            apiServiceProvider.updateEvents(jsonObject, null, this);

                        } else
                            apiServiceProvider.createEvents(jsonObject, null, this);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    hideProgressDialog();
                }
                break;
        }
    }


    void validateUrl(String urlToCheck) {
        if (oldUrl.equals(url.getText().toString())) {
            return;
        }

        if (urlToCheck.length() == 0)
            return;
        if (containsWhiteSpace(urlToCheck)) {
            Snackbar.make(scrollView, "Event url contains spaces. Since spaces are not allowed, it has been replaced with underscore", BaseTransientBottomBar.LENGTH_LONG).show();
            url.setText(urlToCheck.replaceAll(" ", "_"));
            urlToCheck = url.getText().toString();
        } else
            url.setText(urlToCheck);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        showProgressDialog();
        apiServiceProvider.checkLink(urlToCheck, null, this);
    }

    public interface CreateEventStep2Frag {
        void onEventCreateSuccessResponse(int eventId, boolean isUpdate);
    }

    public static boolean containsWhiteSpace(final String testCode) {
        if (testCode != null) {
            for (int i = 0; i < testCode.length(); i++) {
                if (Character.isWhitespace(testCode.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }


    public static String getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format("%02d.%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;

        return offset;
    }
    private String getTimeZoneName() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getID().toString();
        //System.out.println("TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezone id :: " +tz.getID());
    }
}
