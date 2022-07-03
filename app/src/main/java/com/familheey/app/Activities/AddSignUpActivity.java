package com.familheey.app.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Models.Response.EventSignUp;
import com.familheey.app.Need.InputFilterMinMax;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.Bundle.ADDITIONAL_DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.EVENT_ID;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IS_EDIT;

public class AddSignUpActivity extends AppCompatActivity {

    boolean isTimePickerEnabled = true;
    boolean isTimeSelected = false;
    private final SimpleDateFormat userDisplay = new SimpleDateFormat("MMM dd yyyy hh:mm aa", Locale.getDefault());
    private final SimpleDateFormat timeDisplay = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
    private final SimpleDateFormat userDateOnlyDisplay = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
    private final SimpleDateFormat apiDisplay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.slot_tittle)
    EditText slotTittle;
    @BindView(R.id.slot_count)
    EditText slotCount;
    @BindView(R.id.start_time)
    EditText startTime;
    @BindView(R.id.end_time)
    EditText endTime;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.btnAdd)
    MaterialButton btnAdd;

    Calendar myCalendar = Calendar.getInstance();
    SweetAlertDialog progressDialog;
    int id = 0;
    String eventId;
    String selectedStartTime = "";
    Boolean isEdit = false;
    boolean isStart = true;
    String selectedEndTime = "";
    private EventDetail eventDetail;
    int filledQuantity = 0;
    private final SimpleDateFormat fetchDisplay = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());


    private EventSignUp eventSignUp;
    private long startDate;
    private long endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sign_up);
        ButterKnife.bind(this);
        setStatusBarColor();
        try {
            prefillDatas();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something unexpected happened !! Please try again", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void prefillDatas() throws ParseException {
        isEdit = Objects.requireNonNull(getIntent().getExtras()).getBoolean(IS_EDIT);
        eventId = getIntent().getExtras().getString(EVENT_ID);
        id = getIntent().getExtras().getInt(ID);
        startDate=getIntent().getExtras().getLong("START_DATE");
        endDate=getIntent().getExtras().getLong("END_DATE");
        eventDetail = getIntent().getExtras().getParcelable(ADDITIONAL_DATA);
        if (isEdit) {
            toolBarTitle.setText("Edit Sign Ups");
            eventSignUp = getIntent().getParcelableExtra(DATA);
            filledQuantity = eventSignUp.getItemQuantity() - eventSignUp.getNeeded();
        } else {
            toolBarTitle.setText("Add Sign Ups");
        }
        if (!isEdit)
            return;
        slotTittle.setText(eventSignUp.getSlotTitle());
        if (eventSignUp.getSlotDescription() != null)
            description.setText(eventSignUp.getSlotDescription());
        else description.setText("");
        slotCount.setText(String.valueOf(eventSignUp.getItemQuantity()));

        isStart = true;
        Date parsedStartDate = fetchDisplay.parse(eventSignUp.getStartDate());
        Date parsedEndDate = fetchDisplay.parse(eventSignUp.getEndDate());
        isTimePickerEnabled = calculateDateOnly(parsedStartDate, parsedEndDate);
        assert parsedStartDate != null;
        myCalendar.setTime(parsedStartDate);
        calculateTimeAndUpdateFields();

        isStart = false;
        assert parsedEndDate != null;
        myCalendar.setTime(parsedEndDate);
        calculateTimeAndUpdateFields();

        isTimePickerEnabled = true;
    }

    private void setStatusBarColor() {
        slotCount.setFilters(new InputFilter[]{new InputFilterMinMax("1", "999999")});

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    @OnClick({R.id.goBack, R.id.btnAdd, R.id.start_time, R.id.end_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goBack:
                super.onBackPressed();
                break;
            case R.id.btnAdd:
                if (validated())
                    if (!isEdit) {
                        addSignUp();
                    } else
                        callUpdateEventSignupApi();
                break;
            case R.id.start_time:
                isStart = true;
                showDatetimePicker();
                break;
            case R.id.end_time:
                isStart = false;
                showDatetimePicker();
                break;
        }
    }

    private void showDatetimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        if (eventDetail != null)
            currentDate.setTime(new DateTime(TimeUnit.SECONDS.toMillis(eventDetail.getFromDate())).toDate());
        myCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(year, monthOfYear, dayOfMonth);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                isTimeSelected = true;
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                String userFormatDateTime = convertToUserFormat(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                String apiFormatDateTime = "";
                try {
                    apiFormatDateTime = convertUserFormatToApiFormat(userFormatDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date dateStart = new DateTime(TimeUnit.SECONDS.toMillis(startDate)).toDate();
                Date dateEnd = new DateTime(TimeUnit.SECONDS.toMillis(endDate)).toDate();
                if (isStart) {
                    isTimePickerEnabled = true;
                    selectedStartTime = apiFormatDateTime;
                    Date dateSelected=new Date(myCalendar.getTimeInMillis());
                    if (isWithinRange(dateSelected,dateStart,dateEnd)) {
                        startTime.setText(userFormatDateTime);
                    }else {
                        startTime.setText("");
                        Toast.makeText(this, "Selected date not within range", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    selectedEndTime = apiFormatDateTime;
                    endTime.setText(userFormatDateTime);
                    if (compareDate()) {
                        endTime.setText("");
                        Toast.makeText(this, "Please select end date higher than start date", Toast.LENGTH_SHORT).show();
                    }else {
                        Date dateSelected=new Date(myCalendar.getTimeInMillis());

                        if (isWithinRange(dateSelected,dateStart,dateEnd)) {
                            endTime.setText(userFormatDateTime);
                        }else {
                            endTime.setText("");

                            Toast.makeText(this, "Selected date not within range", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
            if (isTimePickerEnabled) {
                timePickerDialog.show();
                timePickerDialog.setOnDismissListener(dialog -> {
                    if (isTimeSelected)
                        return;
                    isTimePickerEnabled = false;
                    handleTimePickerDialog(year, monthOfYear, dayOfMonth);
                });
            } else {
                handleTimePickerDialog(year, monthOfYear, dayOfMonth);
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.getDatePicker().setMinDate(new DateTime(TimeUnit.SECONDS.toMillis(eventDetail.getFromDate())).toDate().getTime());
        datePickerDialog.getDatePicker().setMaxDate(new DateTime(TimeUnit.SECONDS.toMillis(eventDetail.getToDate())).toDate().getTime());
        datePickerDialog.show();
    }
    boolean isWithinRange(Date testDate,Date startDate,Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }


    private void handleTimePickerDialog(int year, int monthOfYear, int dayOfMonth) {
        if (isStart) {
            myCalendar.set(Calendar.HOUR_OF_DAY, 0);
            myCalendar.set(Calendar.MINUTE, 0);
            myCalendar.set(Calendar.SECOND, 0);
        } else {
            myCalendar.set(Calendar.HOUR_OF_DAY, 23);
            myCalendar.set(Calendar.MINUTE, 59);
            myCalendar.set(Calendar.SECOND, 0);
        }
        String userFormatDateTime;
        String apiFormatDateTime;
        if (isStart) {
            userFormatDateTime = convertToUserFormatWithoutTime(year, monthOfYear, dayOfMonth, 0, 0);
            apiFormatDateTime = convertUserFormatToApiFormat(year, monthOfYear, dayOfMonth, 0, 0);
        } else {
            userFormatDateTime = convertToUserFormatWithoutTime(year, monthOfYear, dayOfMonth, 23, 59);
            apiFormatDateTime = convertUserFormatToApiFormat(year, monthOfYear, dayOfMonth, 23, 59);
        }
        if (isStart) {
            selectedStartTime = apiFormatDateTime;
            startTime.setText(userFormatDateTime);
        } else {
            selectedEndTime = apiFormatDateTime;
            endTime.setText(userFormatDateTime);
            if (compareDate()) {
                endTime.setText("");
                Toast.makeText(this, "Please select end date higher than start date", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addSignUp() {
        showProgressDialog();
        JsonObject lisFamily = new JsonObject();
        lisFamily.addProperty("event_id", eventId);
        lisFamily.addProperty("user_id", SharedPref.getUserRegistration().getId());
        lisFamily.addProperty("slot_title", slotTittle.getText().toString());
        lisFamily.addProperty("item_quantity", slotCount.getText().toString());
        lisFamily.addProperty("start_date", selectedStartTime);
        lisFamily.addProperty("end_date", selectedEndTime);
        lisFamily.addProperty("slot_description", description.getText().toString());

        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.addEventSignup(lisFamily, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                Toast.makeText(AddSignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();


                hideProgressDialog();
                finish();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {

                hideProgressDialog();
            }
        });
    }

    private void calculateTimeAndUpdateFields() {
        if (isStart) {
            if (isTimePickerEnabled) {
                startTime.setText(userDisplay.format(myCalendar.getTime()));
            } else {
                startTime.setText(userDateOnlyDisplay.format(myCalendar.getTime()));
            }
            selectedStartTime = apiDisplay.format(myCalendar.getTime());
        } else {
            if (isTimePickerEnabled) {
                endTime.setText(userDisplay.format(myCalendar.getTime()));
            } else {
                endTime.setText(userDateOnlyDisplay.format(myCalendar.getTime()));
            }
            selectedEndTime = apiDisplay.format(myCalendar.getTime());
        }
    }

    private void callUpdateEventSignupApi() {
        showProgressDialog();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event_id", String.valueOf(eventSignUp.getEventId()));
        jsonObject.addProperty("id", String.valueOf(eventSignUp.getId()));
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("slot_title", slotTittle.getText().toString());
        jsonObject.addProperty("item_quantity", slotCount.getText().toString());
        jsonObject.addProperty("start_date", selectedStartTime);
        jsonObject.addProperty("end_date", selectedEndTime);
        jsonObject.addProperty("slot_description", description.getText().toString());

        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        apiServiceProvider.callUpdateEventSignupApi(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgressDialog();
                finish();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgressDialog();
            }
        });

    }

    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private boolean validated() {


        if (selectedStartTime.trim().isEmpty()) {
            Toast.makeText(this, "Enter start time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedEndTime.trim().isEmpty()) {
            Toast.makeText(this, "Enter end time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (slotTittle.getText().toString().length() == 0) {
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (slotCount.getText().toString().length() == 0) {
            Toast.makeText(this, "Enter count", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isEdit) {

            int enteredCount = Integer.parseInt(slotCount.getText().toString());
            if (enteredCount < filledQuantity) {
                Toast.makeText(this, "Count cannot be less than " + filledQuantity, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private String convertToUserFormat(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        Date date = calendar.getTime();
        return userDisplay.format(date);
    }

    private String convertUserFormatToApiFormat(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        Date date = calendar.getTime();
        return userDisplay.format(date);
    }

    private String convertToUserFormatWithoutTime(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        Date date = calendar.getTime();
        return userDateOnlyDisplay.format(date);
    }



    private long convertUserFormatForChecking(String date) throws ParseException {
        Date parsedDate;
        if (isTimePickerEnabled) {
            parsedDate = userDisplay.parse(date);
        } else {
            parsedDate = userDateOnlyDisplay.parse(date);
        }
        assert parsedDate != null;
        return TimeUnit.MILLISECONDS.toSeconds(parsedDate.getTime());
    }

    private String convertUserFormatToApiFormat(String date) throws ParseException {
        Date parsedDate = userDisplay.parse(date);
        assert parsedDate != null;
        return apiDisplay.format(parsedDate);
    }

    private boolean compareDate() {
        try {
            Long date1 = convertUserFormatForChecking(startTime.getText().toString());
            Long date2 = convertUserFormatForChecking(endTime.getText().toString());
            if (date1.compareTo(date2) > 0) {
                return true;
            } else if (date1.compareTo(date2) < 0) {
                return false;
            } else if (date1.compareTo(date2) == 0) {
                return isTimePickerEnabled;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean calculateDateOnly(Date fromDate, Date toDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        String fromHourRef = timeDisplay.format(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        String toHourRef = timeDisplay.format(calendar.getTime());
        String fromTime = timeDisplay.format(fromDate);
        String toTime = timeDisplay.format(toDate);
        return !fromTime.equalsIgnoreCase(fromHourRef) || !toTime.equalsIgnoreCase(toHourRef);
    }
}
