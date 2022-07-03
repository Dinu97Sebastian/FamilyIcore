package com.familheey.app.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.familheey.app.Adapters.ContactsAdapter;
import com.familheey.app.Adapters.CreatedEventTabAdapter;
import com.familheey.app.Adapters.SignupAdapter;
import com.familheey.app.Dialogs.EventActionFragment;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Events.AgendaFragment;
import com.familheey.app.Fragments.Events.AlbumFragment;
import com.familheey.app.Fragments.Events.EventDetailTabFragment;
import com.familheey.app.Fragments.Events.GuestsFragment;
import com.familheey.app.Fragments.Events.SignupFragment;
import com.familheey.app.Fragments.FoldersFragment;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.CreateEventRequest;
import com.familheey.app.Models.Response.Event;
import com.familheey.app.Models.Response.EventContacts;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Models.Response.GetEventByIdResponse;
import com.familheey.app.Models.Response.RecurrenceDate;
import com.familheey.app.Models.Response.Reminder;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Topic.MainActivity;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiFlags.GET_EVENT_BY_ID;
import static com.familheey.app.Utilities.Constants.ApiFlags.RESPOND_TO_RSVP;
import static com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_COVER;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_CREATE;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_UPDATE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Constants.Bundle.IS_INVITATION;
import static com.familheey.app.Utilities.Constants.Bundle.NOTIFICATION_ID;
import static com.familheey.app.Utilities.Constants.Bundle.PUSH;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.ImageUpdate.EVENT_LOGO;
import static com.familheey.app.Utilities.Constants.Paths.EVENT_IMAGE;
public class CreatedEventDetailActivity extends AppCompatActivity implements RetrofitListener, ProgressListener, EventActionFragment.OnEventActionListener, ContactsAdapter.ContactsAdapterInterface, FoldersFragment.OnFolderSelectedListener, SignupAdapter.SignupInterface, TabLayout.OnTabSelectedListener {
    private static int EVENT_DETAILS_REQUEST_CODE=9847;
    @BindView(R.id.btnGoing5)
    MaterialButton btnGoing;
    @BindView(R.id.btnInterested5)
    MaterialButton btnInterested;
    @BindView(R.id.btnNotInterested5)
    MaterialButton btnNotInterested;
    @BindView(R.id.imageViewCal)
    ImageView calenderIcon;
    @BindView(R.id.eventActions)
    ImageView eventActions;
    public CompositeDisposable subscriptions;
    @BindView(R.id.img_banner)
    ImageView img_banner;
    @BindView(R.id.btn_img_change)
    ImageView btn_img_change;
    @BindView(R.id.txtEventTime)
    TextView txtEventTime;
    @BindView(R.id.inviteToEvent)
    TextView inviteToEvent;
    @BindView(R.id.type)
    TextView tvType;
    @BindView(R.id.visibility)
    TextView visibility;
    @BindView(R.id.goBack)
    ImageView back;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.hostedBy)
    TextView hostedBy;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.meetingLink)
    AutoLinkTextView meetingLink;
    @BindView(R.id.meetingIcon)
    ImageView meetingIcon;
    @BindView(R.id.progressDetail)
    ProgressBar progressDetail;
    @BindView(R.id.joinOnlineEvent)
    MaterialButton joinOnlineEvent;
    Event event;
    private List<RecurrenceDate> RecurrenceDateList;

    TextView txtTitleMain, txtLocation, txtCreatedBy, txtCategory;
    JsonObject jsonObject = new JsonObject();
    SweetAlertDialog progressDialog;
    EventDetailTabFragment eventDetailTabFragment;
    @BindView(R.id.img_location)
    ImageView img_location;
    String eventID = "0";
    Boolean DoneClick = true;
    private GetEventByIdResponse getEventByIdResponse;
    //Button invite;
    @BindView(R.id.share_icon)
    ImageView share_icon;
    String countString = "";
    Fragment agendaFragment;
    private ViewPager eventCreatedPager;
    private CreatedEventTabAdapter createdEventTabAdapter;
    ImageView imgCalendar;
    LinearLayout rsvp;
    //Toolbar toolbarCreatedEvent;
    private TabLayout tabLayoutEventCreatedView;
    ContactsAdapter contactsAdapter;
    Button imageViewAdd;
    TextView txtNoContacts;
    BottomSheetDialog mBottomSheetDialog;
    View contactSheetView;
    private final List<EventContacts> contactList = new ArrayList<>();
    // TextView txtContactFor;
    private boolean isAdmin = false;
    private EventDetail eventDetail = new EventDetail();
    private EventDetail eventDetailForRecurring = new EventDetail();
    JSONObject jsonObject1 = null;
    Long recurFromDate;
    Long recurrenceEndDate;
    Long recrRangeEnd;
    Long recurToDate;
    Long recWeekNextFromDate;
    Long recWeekNextToDate;
    Long dailyNextFromDate;
    Long dailyNextToDate;
    int isRecur;
    private String groupId;
    private String button_selection = "";

    private String nextDailyFromDate="";
    private String nextDaillyToDate="";
    private String recurFromTime="";
    private String recEndDate1="";
    private String recrRangeEndDate="";
    private String recurToTime="";
    private String recurDate="";
    private String recrToDate="";
    private String recurrenceType="";
    private String currentDate="";
    private String formattedCurrentDate="";

    private String recNextWeekFromDate="";
    private String recNextWeekToDate="";
    private String recrRangeEndTodate="";
    // private int type;
    private String currentTimezoneOffset;
    private String timezoneName;
    SignupFragment signupFragment;
    GuestsFragment guestsFragment;
    private String notificationId = "";
    private DatabaseReference database;

    @BindView(R.id.occurrenceView)
    LinearLayout occurrenceView;
    @BindView(R.id.occurrence)
    TextView occurrence;
    @BindView(R.id.recurringEvent)
    TextView recurringEvent;

    @Override
    protected void onResume() {
        super.onResume();
        if (!eventID.equals("0"))
            fetchEventDetailApi();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_eventdetail);
        ButterKnife.bind(this);
        txtTitleMain = findViewById(R.id.txtTitleMain);
        imgCalendar = findViewById(R.id.imageViewCal);
        txtLocation = findViewById(R.id.txtLocation);
        txtCreatedBy = findViewById(R.id.txtEventCreator);
        txtCategory = findViewById(R.id.txtCategory);
        rsvp = findViewById(R.id.rsvp);
        //invite = findViewById(R.id.invite);
        subscriptions = new CompositeDisposable();
        signupFragment = SignupFragment.newInstance(eventDetail);
        guestsFragment = GuestsFragment.newInstance(eventDetail);
        initBottomSheet();
        getIntentDatas();
        initListeners();
        setViewPager();
        initContactsRecycler();
        // Modified By: Dinu(22/02/2021) For update visible_status="read" in firebase
        if (getIntent().hasExtra(NOTIFICATION_ID)) {
            notificationId=getIntent().getStringExtra(NOTIFICATION_ID);
            database= FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().getId() + "_notification");
            database.child(notificationId).child("visible_status").setValue("read");
        }
        currentTimezoneOffset = getCurrentTimezoneOffset();
        timezoneName = getTimeZoneName();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.cancel();
        }

    }


    private void initBottomSheet() {
        mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        contactSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_event_contacts, null);
        imageViewAdd = contactSheetView.findViewById(R.id.imageViewAdd);
        txtNoContacts = contactSheetView.findViewById(R.id.txtNoContacts);
        mBottomSheetDialog.setContentView(contactSheetView);
    }

    private void initContactsRecycler() {
        RecyclerView recyclerView = contactSheetView.findViewById(R.id.recyclerContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsAdapter = new ContactsAdapter(contactList, CreatedEventDetailActivity.this, isAdmin);
        recyclerView.setAdapter(contactsAdapter);
    }


    private void callDeleteEventContactApi(String id) {
        showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", id);

        apiServiceProvider.deleteEventContact(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgressDialog();
                if (!eventID.equals("0"))
                    fetchEventDetailApi();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgressDialog();
            }
        });
    }


    private void getIntentDatas() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(DATA)) {
                String data = extras.getString(DATA, "");
                JsonParser jsonParser = new JsonParser();
                jsonObject = (JsonObject) jsonParser.parse(data);
            }

            if (extras.containsKey("OBJECT")) {
                event = extras.getParcelable("OBJECT");
                if (event != null)
                    setDataToScreenFromObject(event);
            }

            if (extras.containsKey(ID)) {
                eventID = extras.getString(ID);
            }
        }
    }
    private void initListeners() {
        meetingLink.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (autoLinkMode == AutoLinkMode.MODE_URL) {
                try {
                    String url = matchedText.trim();
                    if (!url.contains("http")) {
                        url = url.replaceAll("www.", "http://www.");
                    }
                    if (url.contains("blog.familheey.com")) {
                        // Dinu(09/11/2021) for open another app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                    else if (url.contains("familheey")) {
                        openAppGetParams(url);
                    } else {
                        // Dinu(15/07/2021) for open another app
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        //  startActivity(new Intent(this, BrowserActivity.class).putExtra("URL", url));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            }
        });
        joinOnlineEvent.setOnClickListener(v -> {
            try {
                if (eventDetail.getMeetingLink() == null) {
                    Toast.makeText(CreatedEventDetailActivity.this, "There is some issue with the Joining URL", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = eventDetail.getFormattedMeetingLink();
                if (!url.contains("http")) {
                    url = url.replaceAll("www.", "http://www.");
                }
                if (url.contains("blog.familheey.com")) {
                    // Dinu(09/11/2021) for open another app
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                else if (url.contains("familheey")) {
                    openAppGetParams(url);
                } else {
                    // Dinu(15/07/2021) for open another app
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    // startActivity(new Intent(this, BrowserActivity.class).putExtra("URL", url));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        imgCalendar.setOnClickListener(view -> {
                    if (calenderIcon.getTag() != null)
                        if ((Integer) calenderIcon.getTag() == R.drawable.ic_more) {
                            showRsvpSuccessDialog("OTHER");
                        } else {
                            startActivity(new Intent(CreatedEventDetailActivity.this, CalendarActivity.class));
                        }
                }
        );
        share_icon.setOnClickListener(view -> {
            if (eventDetail.getEventType() == null)
                return;
            Intent inviteEvent = new Intent(this, ShareEventActivity.class);
            inviteEvent.putExtra(DATA, eventID);
            inviteEvent.putExtra(IS_INVITATION, eventDetail.getIsPublic() == null || !eventDetail.getIsPublic());
            inviteEvent.putExtra(Constants.Bundle.TYPE, "EVENT");
            startActivity(inviteEvent);
        });

        btn_img_change.setOnClickListener(v -> {
            if (eventDetail.getEventType() == null)
                return;
            Intent familyLogoEditIntent = new Intent(this, ImageChangerActivity.class);
            familyLogoEditIntent.putExtra(DATA, eventDetail);
            familyLogoEditIntent.putExtra(TYPE, EVENT_LOGO);
            familyLogoEditIntent.putExtra(IDENTIFIER, isAdmin);
            startActivityForResult(familyLogoEditIntent, ImageChangerActivity.REQUEST_CODE);
        });
    }

    private void fetchEventDetailApi() {
        if (progressDetail != null)
            progressDetail.setVisibility(View.VISIBLE);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event_id", eventID);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.getEventById(jsonObject, null, this);
    }

    void updateRSVP(String RSVP, boolean isGuestCountUpdateMode, String log) {
        if (getEventByIdResponse != null && !eventID.equals("0")) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("event_id", "" + eventID);
            jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
            jsonObject.addProperty("resp", RSVP);
            if (!countString.isEmpty()) {
                jsonObject.addProperty("others_count", countString);
            }
            if (isGuestCountUpdateMode)
                jsonObject.addProperty("guest_count_update", true);
            jsonObject.addProperty("created_by", "" + getEventByIdResponse.getData().getEvent().get(0).getCreatedBy());

            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());

            ApiCallbackParams apiCallbackParams = new ApiCallbackParams();

            String param = getEventByIdResponse.getData().getEvent().get(0).getFromDate().toString();

            apiCallbackParams.setDeveloperString(param);
            apiCallbackParams.setStatus(!RSVP.equalsIgnoreCase("not-going"));
            apiServiceProvider.respondToRSVP(jsonObject, apiCallbackParams, this);
        }
    }

    private void setViewPager() {
        tabLayoutEventCreatedView = findViewById(R.id.scrollTab);
        eventDetail.setEventId(eventID);
        eventDetailTabFragment = new EventDetailTabFragment();
        eventCreatedPager = findViewById(R.id.eventCreatedPager);
        createdEventTabAdapter = new CreatedEventTabAdapter(getSupportFragmentManager());
        createdEventTabAdapter.addFragment(eventDetailTabFragment, "Details");
        agendaFragment = AgendaFragment.newInstance(eventDetail);
        createdEventTabAdapter.addFragment(agendaFragment, "Agenda");
        createdEventTabAdapter.addFragment(guestsFragment, "Guests");
        createdEventTabAdapter.addFragment(signupFragment, "Sign up");

        createdEventTabAdapter.addFragment(AlbumFragment.newInstance(Constants.FileUpload.TYPE_EVENTS, eventID, eventDetail.isAdminViewing()), "Albums");
/**added isAdmin for getting the "admin only" condition in family settings**/
        createdEventTabAdapter.addFragment(FoldersFragment.newInstance(eventID, Constants.FileUpload.TYPE_EVENTS,"fromEventDetails",isAdmin), "Documents");

        eventCreatedPager.setAdapter(createdEventTabAdapter);
        tabLayoutEventCreatedView.setupWithViewPager(eventCreatedPager);
        eventCreatedPager.setOffscreenPageLimit(5);
        new Handler().postDelayed(() -> tabLayoutEventCreatedView.addOnTabSelectedListener(CreatedEventDetailActivity.this), 500);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().hasExtra(TYPE) && getIntent().getStringExtra(TYPE).equals(PUSH)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
    }

    void selectButton(MaterialButton button) {
        button.setBackgroundColor(getResources().getColor(R.color.buttoncolor));
        button.setTextColor(getResources().getColor(R.color.white));
        button.setStrokeColorResource(R.color.purpleText);
    }

    void unSelectButton(MaterialButton button) {
        button.setBackgroundColor(getResources().getColor(R.color.white));
        button.setTextColor(getResources().getColor(R.color.quantum_grey500));
        button.setStrokeColorResource(R.color.quantum_grey500);
    }

    void initializeRSVPButton() {
        btnGoing.setStrokeColorResource(R.color.purpleText);
        btnInterested.setStrokeColorResource(R.color.purpleText);
        btnNotInterested.setStrokeColorResource(R.color.purpleText);
        btnGoing.setTextColor(getResources().getColor(R.color.purpleText));
        btnInterested.setTextColor(getResources().getColor(R.color.purpleText));
        btnNotInterested.setTextColor(getResources().getColor(R.color.purpleText));
    }


    private void setDataToScreenFromObject(Event event) {
        RecurrenceDateList = event.getRecurrenceDate();
        txtTitleMain.setText(event.getEventName());
        txtLocation.setText(event.getLocation());
        txtCategory.setText(event.getCategory());
        //Dinu(27-07-2021) for handle recurring event
        if(event.getIsRecurrence()==1){
            occurrence.setText(String.valueOf(event.getRecurrenceCount()));
            occurrenceView.setVisibility(View.VISIBLE);
            recurringEvent.setVisibility(View.VISIBLE);
            recurringEvent.setText("Recurring Event - "+event.getRecurrenceType());
            if (isSameDate(Long.parseLong(event.getFromDate() + ""), Long.parseLong(event.getToDate() + ""))) {
                // RecurrenceDateList = event.getRecurrenceDate();
                for (int j = 0; j < RecurrenceDateList.size(); j++) {
                    int compareValue1= compareDate(RecurrenceDateList.get(j).getRecurrence_from_date());
                    int compareValue2= compareDate(RecurrenceDateList.get(j).getRecurrence_to_date());
                        /* 0 comes when two date are same,
                        1 comes when date1 is higher then date2
                        -1 comes when date1 is lower then date2*/
                    if((compareValue1==1 && compareValue2== -1) ||compareValue1== -1 || compareValue1==0){

                        recurFromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "hh:mm aa");
                        recurToTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "hh:mm aa");
                        recurDate = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE, MMM dd yyyy");
                        txtEventTime.setText(String.format(recurDate + "\n" + "%s - %s", recurFromTime, recurToTime));
                        break;
                    }else if ((j==RecurrenceDateList.size()-1) && (compareValue1==1 && compareValue2==1)){
                        recurFromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "hh:mm aa");
                        recurToTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "hh:mm aa");
                        recurDate = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE, MMM dd yyyy");
                        txtEventTime.setText(String.format(recurDate + "\n" + "%s - %s", recurFromTime, recurToTime));
                        break;
                    }
                }

            } else {
                // RecurrenceDateList = event.getRecurrenceDate();
                for (int j = 0; j < RecurrenceDateList.size(); j++) {
                    int compareValue1= compareDate(RecurrenceDateList.get(j).getRecurrence_from_date());
                    int compareValue2= compareDate(RecurrenceDateList.get(j).getRecurrence_to_date());
                        /* 0 comes when two date are same,
                        1 comes when date1 is higher then date2
                        -1 comes when date1 is lower then date2*/
                    if((compareValue1==1 && compareValue2== -1) ||compareValue1== -1 || compareValue1==0){
                        recurFromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "hh:mm aa");
                        recurToTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "hh:mm aa");
                        String fromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE MMM dd");
                        String toTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "EEE MMM dd, yyyy");
                        txtEventTime.setText(String.format("%s - %s" + " \n" + "%s - %s", fromTime, toTime, recurFromTime, recurToTime));
                        break;
                    } else if ((j==RecurrenceDateList.size()-1) && (compareValue1==1 && compareValue2==1)){
                        recurFromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "hh:mm aa");
                        recurToTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "hh:mm aa");
                        String fromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE MMM dd");
                        String toTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "EEE MMM dd, yyyy");
                        txtEventTime.setText(String.format("%s - %s" + " \n" + "%s - %s", fromTime, toTime, recurFromTime, recurToTime));
                        break;
                    }
                }
            }
        }
    }

    private void setDataToScreen(GetEventByIdResponse getEventByIdResponse) {
        if (getEventByIdResponse.getData().getEvent().size() != 0) {
            if(getEventByIdResponse.getData().getEvent().get(0).getIsRecurrence()==1){
                int arr=getEventByIdResponse.getData().getEvent().size()-1;
              //  eventDetail = getEventByIdResponse.getData().getEvent().get(arr);

                for (int j = 0; j < getEventByIdResponse.getData().getEvent().size(); j++) {
                    int compareValue1= compareDate(getEventByIdResponse.getData().getEvent().get(j).getRecurrence_from_date());
                    int compareValue2= compareDate(getEventByIdResponse.getData().getEvent().get(j).getRecurrence_to_date());
                    if((compareValue1==1 && compareValue2== -1) ||compareValue1== -1 || compareValue1==0){
                        eventDetail = getEventByIdResponse.getData().getEvent().get(j);
                        break;
                    }
                    else if ((j==getEventByIdResponse.getData().getEvent().size()-1) && (compareValue1==1 && compareValue2==1)){
                        eventDetail = getEventByIdResponse.getData().getEvent().get(j);
                        break;
                    }
                }
            }else {
                eventDetail = getEventByIdResponse.getData().getEvent().get(0);

            }


            isAdmin = eventDetail.getCreatedBy().toString().equals(SharedPref.getUserRegistration().getId());
            if (contactsAdapter != null)
                contactsAdapter.setAdminStatus(isAdmin);
            //Contacts Code
            collapsingToolBarBehaviour();
            contactList.clear();
            contactList.addAll(getEventByIdResponse.getData().getContacts());
            contactsAdapter.notifyDataSetChanged();
            if(eventDetail.getIsRecurrence()==0){
                if (isSameDate(Long.parseLong(eventDetail.getFromDate() + ""), Long.parseLong(eventDetail.getToDate() + ""))) {
                    String fromTime1 = dateFormat(Long.parseLong(eventDetail.getFromDate() + ""), "hh:mm aa");
                    String toTime1 = dateFormat(Long.parseLong(eventDetail.getToDate() + ""), "hh:mm aa");

                    String fromTime = dateFormat(Long.parseLong(eventDetail.getFromDate() + ""), "EEE, MMM dd yyyy");
                    txtEventTime.setText(String.format(fromTime + "\n" + "%s - %s", fromTime1, toTime1));
                } else {

                    String fromTime = dateFormat(Long.parseLong(eventDetail.getFromDate() + ""), "EEE MMM dd");
                    String toTime = dateFormat(Long.parseLong(eventDetail.getToDate() + ""), "EEE MMM dd, yyyy");

                    String fromTime1 = dateFormat(Long.parseLong(eventDetail.getFromDate() + ""), "hh:mm aa");
                    String toTime1 = dateFormat(Long.parseLong(eventDetail.getToDate() + ""), "hh:mm aa");

                    txtEventTime.setText(String.format("%s - %s" + " \n" + "%s - %s", fromTime, toTime, fromTime1, toTime1));
                }
            }
            else if(eventDetail.getIsRecurrence()==1 && (RecurrenceDateList!=null && RecurrenceDateList.size()>0)){
                if (isSameDate(Long.parseLong(eventDetail.getRecurrence_from_date() + ""), Long.parseLong(eventDetail.getRecurrence_to_date() + ""))) {
                    for (int j = 0; j < RecurrenceDateList.size(); j++) {
                        int compareValue1= compareDate(RecurrenceDateList.get(j).getRecurrence_from_date());
                        int compareValue2= compareDate(RecurrenceDateList.get(j).getRecurrence_to_date());
                        /* 0 comes when two date are same,
                        1 comes when date1 is higher then date2
                        -1 comes when date1 is lower then date2*/
                        if((compareValue1==1 && compareValue2== -1) ||compareValue1== -1 || compareValue1==0){

                            recurFromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "hh:mm aa");
                            recurToTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "hh:mm aa");
                            recurDate = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE, MMM dd yyyy");
                            txtEventTime.setText(String.format(recurDate + "\n" + "%s - %s", recurFromTime, recurToTime));
                            break;
                        }else if ((j==RecurrenceDateList.size()-1) && (compareValue1==1 && compareValue2==1)){
                            recurFromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "hh:mm aa");
                            recurToTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "hh:mm aa");
                            recurDate = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE, MMM dd yyyy");
                            txtEventTime.setText(String.format(recurDate + "\n" + "%s - %s", recurFromTime, recurToTime));
                            break;
                        }
                    }
                } else {

                    for (int j = 0; j < RecurrenceDateList.size(); j++) {
                        int compareValue1= compareDate(RecurrenceDateList.get(j).getRecurrence_from_date());
                        int compareValue2= compareDate(RecurrenceDateList.get(j).getRecurrence_to_date());
                        /* 0 comes when two date are same,
                        1 comes when date1 is higher then date2
                        -1 comes when date1 is lower then date2*/
                        if((compareValue1==1 && compareValue2== -1) ||compareValue1== -1 || compareValue1==0){
                            recurFromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "hh:mm aa");
                            recurToTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "hh:mm aa");
                            String fromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE MMM dd");
                            String toTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "EEE MMM dd, yyyy");
                            txtEventTime.setText(String.format("%s - %s" + " \n" + "%s - %s", fromTime, toTime, recurFromTime, recurToTime));
                            break;
                        } else if ((j==RecurrenceDateList.size()-1) && (compareValue1==1 && compareValue2==1)){
                            recurFromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "hh:mm aa");
                            recurToTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "hh:mm aa");
                            String fromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE MMM dd");
                            String toTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "EEE MMM dd, yyyy");
                            txtEventTime.setText(String.format("%s - %s" + " \n" + "%s - %s", fromTime, toTime, recurFromTime, recurToTime));
                            break;
                        }
                    }
                }
            }
            else if(RecurrenceDateList==null && eventDetail.getIsRecurrence()==1){
                if (isSameDate(Long.parseLong(eventDetail.getRecurrence_from_date() + ""), Long.parseLong(eventDetail.getRecurrence_to_date() + ""))) {
                    String fromTime1 = dateFormat(Long.parseLong(eventDetail.getRecurrence_from_date() + ""), "hh:mm aa");
                    String toTime1 = dateFormat(Long.parseLong(eventDetail.getRecurrence_to_date() + ""), "hh:mm aa");

                    String fromTime = dateFormat(Long.parseLong(eventDetail.getRecurrence_from_date() + ""), "EEE, MMM dd yyyy");
                    txtEventTime.setText(String.format(fromTime + "\n" + "%s - %s", fromTime1, toTime1));
                } else {

                    String fromTime = dateFormat(Long.parseLong(eventDetail.getRecurrence_from_date() + ""), "EEE MMM dd");
                    String toTime = dateFormat(Long.parseLong(eventDetail.getRecurrence_to_date() + ""), "EEE MMM dd, yyyy");

                    String fromTime1 = dateFormat(Long.parseLong(eventDetail.getRecurrence_from_date() + ""), "hh:mm aa");
                    String toTime1 = dateFormat(Long.parseLong(eventDetail.getRecurrence_to_date() + ""), "hh:mm aa");

                    txtEventTime.setText(String.format("%s - %s" + " \n" + "%s - %s", fromTime, toTime, fromTime1, toTime1));
                }
            }
            txtTitleMain.setText(eventDetail.getEventName());
            txtLocation.setText(eventDetail.getLocation());
            //Dinu(27-07-2021) for handle recurring event
            if (eventDetail.getIsRecurrence() == 1) {
                occurrence.setText(String.valueOf(eventDetail.getRecurrenceCount()));
                occurrenceView.setVisibility(View.VISIBLE);
                recurringEvent.setVisibility(View.VISIBLE);
                recurringEvent.setText("Recurring Event - " + eventDetail.getRecurrenceType());

            } else {
                occurrenceView.setVisibility(View.GONE);
                recurringEvent.setVisibility(View.GONE);
            }
            // txtCreatedBy.setText("Created by " +eventDetail.getCreatedByName());
            if (getEventByIdResponse.getData().getEvent().get(0).getEventHost() != null) {
                if (eventDetail.getCreatedByName().equals(getEventByIdResponse.getData().getEvent().get(0).getEventHost())) {
                    txtCreatedBy.setText("Hosted by " + eventDetail.getCreatedByName());
                } else {
                    txtCreatedBy.setText("Created by " + eventDetail.getCreatedByName() + "\n" +
                            "Hosted by " + getEventByIdResponse.getData().getEvent().get(0).getEventHost());
                }
            } else {
                txtCreatedBy.setText("Created by " + eventDetail.getCreatedByName());
            }
            //   hostedBy.setText("Hosted by " + getEventByIdResponse.getData().getEvent().get(0).getEventHost());
            // else hostedBy.setVisibility(View.INVISIBLE);


            txtCategory.setText(eventDetail.getCategory());
            if (eventDetail.getIsPublic() != null && eventDetail.getIsPublic())
                visibility.setText("Public : ");
            else visibility.setText("Private : ");
            tvType.setText(eventDetail.getEventType() + " event");

            if (eventDetail.getEventImage() != null) {
                Glide.with(getApplicationContext())
                        .load(S3_DEV_IMAGE_URL_COVER + IMAGE_BASE_URL + EVENT_IMAGE + eventDetail.getEventImage())
                        .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.default_event_image)
                        .into(img_banner);
            } else {
                Glide.with(getApplicationContext())
                        .load(R.drawable.default_event_image)
                        .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(img_banner);
            }

            if (eventDetail.isRegularEvent())
                createdEventTabAdapter.removeSignUpEvent();
            else
                createdEventTabAdapter.addSignUpEvent(SignupFragment.newInstance(eventDetail), "Sign up");
            eventDetailTabFragment = createdEventTabAdapter.getInstance(EventDetailTabFragment.class);
            if (eventDetailTabFragment != null)
                eventDetailTabFragment.updateEventDetails(getEventByIdResponse.getData());
            AgendaFragment agendaFragment = createdEventTabAdapter.getInstance(AgendaFragment.class);
            if (agendaFragment != null) agendaFragment.updateEvent(getEventByIdResponse.getData());
//            GuestsFragment guestsFragment = createdEventTabAdapter.getInstance(GuestsFragment.class);
            if (guestsFragment != null) guestsFragment.updateEvent(eventDetail);
            SignupFragment signupFragment = createdEventTabAdapter.getInstance(SignupFragment.class);
            if (signupFragment != null) signupFragment.updateEvent(eventDetail);
            AlbumFragment albumFragment = createdEventTabAdapter.getInstance(AlbumFragment.class);
            if (albumFragment != null) {
                albumFragment.updateAdminStatus(eventDetail.isAdminViewing(), eventDetail.isAdminViewing(), eventDetail.isAdminViewing(), SharedPref.getUserRegistration().getId());
            }
            FoldersFragment foldersFragment = createdEventTabAdapter.getInstance(FoldersFragment.class);
            if (foldersFragment != null) {
                foldersFragment.updateAdminStatus(eventDetail.isAdminViewing(), eventDetail.isAdminViewing(), eventDetail.isAdminViewing(), SharedPref.getUserRegistration().getId());
            }
            // If eventDetail is sharable, eventDetail icon is displayed
            if (eventDetail.getIsSharable() != null && eventDetail.getIsSharable()) {
                share_icon.setVisibility(View.VISIBLE);
            } else share_icon.setVisibility(View.INVISIBLE);

            //If admin is viewing, Edit EventDetail Cover image is displayed
            if (eventDetail.isAdminViewing()) {
                btn_img_change.setVisibility(View.VISIBLE);
                inviteToEvent.setVisibility(View.VISIBLE);
                rsvp.setVisibility(View.GONE);
            } else {
                btn_img_change.setVisibility(View.INVISIBLE);
                //inviteToEvent.setVisibility(View.VISIBLE);
                if (eventDetail.getIsPublic() != null && eventDetail.getIsPublic())
                    inviteToEvent.setVisibility(View.VISIBLE);
                else if (eventDetail.getIsPublic() != null && !eventDetail.getIsPublic()) {
                    if (eventDetail.getIsSharable() != null && eventDetail.getIsSharable()) {
                        inviteToEvent.setVisibility(View.VISIBLE);
                    } else inviteToEvent.setVisibility(View.INVISIBLE);
                } else {
                    inviteToEvent.setVisibility(View.INVISIBLE);
                }
                if (tabLayoutEventCreatedView.getSelectedTabPosition() == 0) {
                    if (eventDetail.isPastEvent())
                        rsvp.setVisibility(View.GONE);
                    else
                        rsvp.setVisibility(View.VISIBLE);
                } else {
                    rsvp.setVisibility(View.GONE);
                }
            }

            if (eventDetail.isOnlineEvent()) {
                img_location.setVisibility(View.GONE);
                txtLocation.setVisibility(View.GONE);
                if (eventDetail.canShowJoinOnlineEventButton())
                    joinOnlineEvent.setVisibility(View.VISIBLE);
                else joinOnlineEvent.setVisibility(View.GONE);

                //joinOnlineEvent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cursor_icon, 0, 0, 0);
                if (eventDetail.getMeetingLink() != null) {
                    meetingLink.addAutoLinkMode(
                            AutoLinkMode.MODE_URL);
                    meetingLink.setUrlModeColor(ContextCompat.getColor(getApplicationContext(), R.color.buttoncolor));
                    meetingLink.setVisibility(View.VISIBLE);
                    meetingIcon.setVisibility(View.VISIBLE);
                    meetingLink.setAutoLinkText(eventDetail.getFormattedMeetingLink());
                    if (eventDetail.getMeetingLogo() != null)
                        Glide.with(getApplicationContext())
                                .load(IMAGE_BASE_URL + eventDetail.getMeetingLogo())
                                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.cursor_icon)
                                .into(meetingIcon);
                    else meetingIcon.setImageResource(R.drawable.cursor_icon);
                } else {
                    meetingLink.setVisibility(View.GONE);
                    meetingIcon.setVisibility(View.GONE);
                }
            } else {
                img_location.setVisibility(View.VISIBLE);
                txtLocation.setVisibility(View.VISIBLE);
                meetingLink.setVisibility(View.GONE);
                meetingIcon.setVisibility(View.GONE);
                joinOnlineEvent.setVisibility(View.GONE);
            }
        }

        if (eventDetail.isPastEvent()) {
            inviteToEvent.setVisibility(View.INVISIBLE);
            btn_img_change.setVisibility(View.INVISIBLE);
            share_icon.setVisibility(View.INVISIBLE);
            eventActions.setVisibility(View.INVISIBLE);
            img_location.setVisibility(View.INVISIBLE);
            joinOnlineEvent.setVisibility(View.GONE);

        }

        if (getEventByIdResponse.getData().getRsvp().size() != 0) {
            switch (getEventByIdResponse.getData().getRsvp().get(0).getAttendeeType()) {
                case "going":
                    button_selection = "going";
                    selectButton(btnGoing);
                    unSelectButton(btnInterested);
                    unSelectButton(btnNotInterested);
                    btnGoing.setEnabled(false);
                    break;

                case "interested":
                    button_selection = "interested";
                    btnInterested.setEnabled(false);
                    selectButton(btnInterested);
                    unSelectButton(btnGoing);
                    unSelectButton(btnNotInterested);
                    break;

                case "not-going":
                    button_selection = "not-going";
                    btnNotInterested.setEnabled(false);
                    selectButton(btnNotInterested);
                    unSelectButton(btnGoing);
                    unSelectButton(btnInterested);
                    break;
            }
        } else {
            initializeRSVPButton();
        }

        if (getEventByIdResponse.getData().getContacts().size() == 0) {
            txtNoContacts.setVisibility(View.VISIBLE);
        }

    }

    @OnClick({R.id.btnGoing5, R.id.btnInterested5, R.id.btnNotInterested5, R.id.img_location, R.id.invite, R.id.inviteToEvent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnGoing5:
                if (!button_selection.equals("going")) {
                    DoneClick = true;
                    button_selection = "going";
                    selectButton(btnGoing);
                    unSelectButton(btnNotInterested);
                    unSelectButton(btnInterested);
                    updateRSVP("going", false, "1");
                    showProgressDialog();
                    showRsvpSuccessDialog("OTHER");
                    btnGoing.setEnabled(false);
                    btnNotInterested.setEnabled(true);
                    btnInterested.setEnabled(true);
                }
                break;
            case R.id.btnInterested5:
                if (!button_selection.equals("interested")) {
                    DoneClick = true;
                    button_selection = "interested";
                    selectButton(btnInterested);
                    unSelectButton(btnGoing);
                    unSelectButton(btnNotInterested);
                    updateRSVP("interested", false, "2");
                    showProgressDialog();
                    showRsvpSuccessDialog("OTHER");
                    btnInterested.setEnabled(false);
                    btnGoing.setEnabled(true);
                    btnNotInterested.setEnabled(true);

                }
                break;
            case R.id.btnNotInterested5:

                if (!button_selection.equals("not-going")) {
                    DoneClick = true;
                    button_selection = "not-going";
                    selectButton(btnNotInterested);
                    unSelectButton(btnGoing);
                    unSelectButton(btnInterested);
                    updateRSVP("not-going", false, "3");
                    showProgressDialog();
                    btnNotInterested.setEnabled(false);
                    btnInterested.setEnabled(true);
                    btnGoing.setEnabled(true);
                }

                break;
            case R.id.img_location:
                if (eventDetail.getLat() == null)
                    return;
                if (getEventByIdResponse.getData().getEvent().get(0).getLat() == null && getEventByIdResponse.getData().getEvent().get(0).getLong() == null) {
                    Toast.makeText(this, "Due to unavailability of Location, the Maps cannot be shown!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + getEventByIdResponse.getData().getEvent().get(0).getLat() + "," + getEventByIdResponse.getData().getEvent().get(0).getLong() + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(this, "Something went wrong !! Please try again later", Toast.LENGTH_SHORT).show();
                }
                /*Intent intent = new Intent(this, LocateActivity.class);
                intent.putExtra(DATA, new LatLng(getEventByIdResponse.getData().getEvent().get(0).getLat(), getEventByIdResponse.getData().getEvent().get(0).getLong()));
                startActivity(intent);*/
                break;
            case R.id.invite:
            case R.id.inviteToEvent:
                Intent inviteEvent = new Intent(this, ShareEventActivity.class);
                inviteEvent.putExtra(DATA, eventID);
                inviteEvent.putExtra(IS_INVITATION, true);
                inviteEvent.putExtra(Constants.Bundle.TYPE, "EVENT");
                startActivity(inviteEvent);
                break;
        }
    }


    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        hideProgressDialog();
        switch (apiFlag) {
            case GET_EVENT_BY_ID:
                getEventByIdResponse = new Gson().fromJson(responseBodyString, GetEventByIdResponse.class);
                if (getEventByIdResponse.getData().getEvent().size() > 0) {
                    if (getEventByIdResponse.getData().getRsvp() != null && getEventByIdResponse.getData().getRsvp().size() > 0 && getEventByIdResponse.getData().getRsvp().get(0) != null) {
                        countString = getEventByIdResponse.getData().getRsvp().get(0).getOthersCount() + "";
                    }
                    if (getEventByIdResponse.getData().getEvent().size() == 0) {
                        SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                        contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                            contentNotFoundDialog.cancel();
                            Intent intent = new Intent( this, MainActivity.class );
                            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            startActivity( intent );
                        });
                        contentNotFoundDialog.setCanceledOnTouchOutside(false);
                        contentNotFoundDialog.setCancelable(false);
                        contentNotFoundDialog.show();
                        Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                        return;
                    }
                    setDataToScreen(getEventByIdResponse);
                    checkRsvp(getEventByIdResponse.getData().getRsvp());
                    if (progressDetail != null)
                        progressDetail.setVisibility(View.GONE);
                } else {

                    SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                    contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                        contentNotFoundDialog.cancel();
                        Intent intent = new Intent( this, MainActivity.class );
                        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity( intent );
                    });
                    contentNotFoundDialog.setCanceledOnTouchOutside(false);
                    contentNotFoundDialog.setCancelable(false);
                    contentNotFoundDialog.show();
                    Utilities.addPositiveButtonMargin(contentNotFoundDialog);
                }
                break;
            case RESPOND_TO_RSVP:
                if (apiCallbackParams.getDeveloperString() != null) {
                    if (apiCallbackParams.getStatus()) {
                        if (DoneClick) {
                        }
                        //  showRsvpSuccessDialog();
                    } else Toast.makeText(this, "Not interested", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "RSVP success", Toast.LENGTH_SHORT).show();
                }
                if (!eventID.equals("0"))
                    fetchEventDetailApi();
                break;
        }
    }

    private String formatDate(long milliseconds) /* This is your topStory.getTime()*1000 */ {
        DateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);
        return sdf.format(calendar.getTime());
    }
    private String formatRecuEndDate(Long endDate){
        Date date = new Date(endDate * 1000L);
// format of the date
        SimpleDateFormat jdf = new SimpleDateFormat("EEE, MMM dd yyyy");
        String java_date = jdf.format(date);
        return java_date;
    }
    public boolean isPastTime(Long recurToDateInfo) {
        if (recurToDateInfo== null)
            return true;
        Long toTime = TimeUnit.SECONDS.toMillis(recurToDateInfo);
        DateTime dateTime = new DateTime(toTime);
        DateTime currentTime = DateTime.now();
        return dateTime.isBefore(currentTime);
    }
    private String addOneDayToCurrentDate(String sourceDate) throws ParseException {
        SimpleDateFormat formattedDate = new SimpleDateFormat("EEE, MMM dd yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(formattedDate.parse(sourceDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, MMM dd yyyy");
        String output = sdf1.format(c.getTime());
        return output;
    }
    private String addSevenDayToCurrentDate(String sourceDate) throws ParseException {
        SimpleDateFormat formattedDate = new SimpleDateFormat("EEE, MMM dd yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(formattedDate.parse(sourceDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 7);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, MMM dd yyyy");
        String output = sdf1.format(c.getTime());
        return output;
    }
    public static boolean CheckDates(String startDate, String endDate) {

        SimpleDateFormat dfDate = new SimpleDateFormat("EEE, MMM dd yyyy");

        boolean b = false;

        try {
            if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                b = true;  // If start date is before end date.
            } else if (dfDate.parse(startDate).equals(dfDate.parse(endDate))) {
                b = true;  // If two dates are equal.
            } else {
                b = false; // If start date is after the end date.
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return b;
    }




    private void checkRsvp(List<GetEventByIdResponse.Rsvp> rsvp) {
        if (rsvp.size() == 0) {
            calenderIcon.setTag(R.drawable.icon_calendar);
        } else {
            if (rsvp.get(0).getAttendeeType().equals("going")) {
                calenderIcon.setImageResource(R.drawable.ic_more);
                calenderIcon.setTag(R.drawable.ic_more);
            }
            if (rsvp.get(0).getAttendeeType().equals("interested")) {
                calenderIcon.setImageResource(R.drawable.ic_more);
                calenderIcon.setTag(R.drawable.ic_more);
            }
            if (rsvp.get(0).getAttendeeType().equals("not-going")) {
                calenderIcon.setImageResource(R.drawable.icon_calendar);
                calenderIcon.setTag(R.drawable.icon_calendar);
            }
        }
    }

    private void showRsvpSuccessDialog(String type) {
        try {
            View dialogView = getLayoutInflater().inflate(R.layout.rsvp_added_successful_dialogue, null);

            BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTransparent);

            TextView date = dialogView.findViewById(R.id.textView36);
            androidx.constraintlayout.widget.ConstraintLayout view = dialogView.findViewById(R.id.allowothersview);

            if (getEventByIdResponse.getData().getEvent().get(0).getAllowOthers()) {

                if ("going".equals(button_selection)) {
                    view.setVisibility(View.VISIBLE);
                }
            }
            TextView txt_change = dialogView.findViewById(R.id.txt_change);
            if ("MORE".equals(type)) {
                String message = "You will receive a reminder on \n ";
                if (getEventByIdResponse.getData().getEvent().get(0).getRemindOn() != null) {
                    long value = TimeUnit.SECONDS.toMillis(Long.parseLong(getEventByIdResponse.getData().getEvent().get(0).getRemindOn()));
                    DateTime utc = new DateTime(value);
                    //    org.joda.time.format.DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa").withZone(DateTimeZone.getDefault());
                    org.joda.time.format.DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("hh:mm aa").withZone(DateTimeZone.getDefault());

                    message = message + dateTimeFormat.print(utc);
                }
                date.setText(message);
            } else {
                reminderTimeConversion(10, date);
            }

            txt_change.setOnClickListener(v -> showReminderPickerDialog(date));

            Button button = dialogView.findViewById(R.id.done);
            EditText editText = dialogView.findViewById(R.id.count);
            editText.setText(countString);
            button.setOnClickListener(view1 -> {
                DoneClick = false;
                countString = editText.getText().toString();
                if (type.equals("MORE")) {
                    updateRSVP(button_selection, true, "4");
                } else {
                    updateRSVP(button_selection, true, "4");

                }
                showProgressDialog();
                dialog.dismiss();
            });

            dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            dialog.setContentView(dialogView);
            dialog.show();


        } catch (Exception e) {
          /*
          need to handle
           */
        }
    }


    private void showReminderPickerDialog(TextView date) {

        try {
            View dialogView = getLayoutInflater().inflate(R.layout.reminder_picker_dialogue, null);
            BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTransparent);
            TextView txt_one = dialogView.findViewById(R.id.btn_one_hour);
            TextView txt_two = dialogView.findViewById(R.id.btn_two_hour);
            TextView txt_five = dialogView.findViewById(R.id.btn_five_hour);
            TextView txt_one_day = dialogView.findViewById(R.id.btn_one_day);
            dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            txt_one.setOnClickListener(v -> {
                dialog.dismiss();
                reminderTimeConversion(60, date);
            });
            txt_two.setOnClickListener(v -> {
                dialog.dismiss();
                reminderTimeConversion(120, date);
            });
            txt_five.setOnClickListener(v -> {
                dialog.dismiss();

                reminderTimeConversion(300, date);
            });
            txt_one_day.setOnClickListener(v -> {
                dialog.dismiss();
                reminderTimeConversion(1440, date);

            });
            dialog.setContentView(dialogView);
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reminderTimeConversion(int mintues, TextView date) {
        createEventReminder(mintues);
        String message = "You will receive a reminder on \n ";
        long value = TimeUnit.SECONDS.toMillis(getEventByIdResponse.getData().getEvent().get(0).getFromDate());
        DateTime utc = new DateTime(value);
        org.joda.time.format.DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("hh:mm aa").withZone(DateTimeZone.getDefault());
        DateTime almutc = utc.minusMinutes(mintues);
        message = message + dateTimeFormat.print(almutc);
        date.setText(message);
    }


    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        hideProgressDialog();
        if (apiFlag == GET_EVENT_BY_ID) {
            try {
                if (errorData.getCode() == 500) {
                    SweetAlertDialog contentNotFoundDialog = Utilities.getContentNotFoundDialog(this);
                    contentNotFoundDialog.setConfirmClickListener(sweetAlertDialog -> {
                        contentNotFoundDialog.cancel();
                        Intent intent = new Intent( this, MainActivity.class );
                        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity( intent );
                    });
                    contentNotFoundDialog.setCanceledOnTouchOutside(false);
                    contentNotFoundDialog.setCancelable(false);
                    contentNotFoundDialog.show();
                    Utilities.addPositiveButtonMargin(contentNotFoundDialog);

                } else {
                    networkError();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showProgressDialog() {
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
        if (progressDialog == null) {
            progressDialog = Utilities.getErrorDialog(this, errorMessage);
            progressDialog.show();
            return;
        }
        Utilities.getErrorDialog(progressDialog, errorMessage);
    }

    private String dateFormat(Long value, String format) {
        if (value != null) {
            value = TimeUnit.SECONDS.toMillis(value);
            DateTime dateTime = new DateTime(value);

            DateTimeFormatter dtfOut = DateTimeFormat.forPattern(format);
            return dtfOut.print(dateTime);
        }
        return "";
    }
    /** 29/09/21 **/
    private String incrementDateForRecurring(Long value,String format){
        if(value!=null){
            value = TimeUnit.SECONDS.toMillis(value);
            DateTime dateTime = new DateTime(value);
            dateTime.plusDays(1);

            DateTimeFormatter dtfOut = DateTimeFormat.forPattern(format);
            return dtfOut.print(dateTime);

        }
        return "";
    }

    private Boolean isSameDate(Long from, Long to) {

        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyyMMdd");
        from = TimeUnit.SECONDS.toMillis(from);
        to = TimeUnit.SECONDS.toMillis(to);
        DateTime date1 = new DateTime(from);
        DateTime date2 = new DateTime(to);
        return dtfOut.print(date1).equals(dtfOut.print(date2));
    }


    public void createEventReminder(int reminder) {


        CreateEventRequest obj = new CreateEventRequest();
        obj.setUser_id(SharedPref.getUserRegistration().getId());
        obj.setEvent_id(String.valueOf(eventID));
        obj.setRemind_on(reminder);
        obj.setEvent_date(getEventByIdResponse.getData().getEvent().get(0).getFromDate() + "");
        obj.setTimezone_offset(timezoneName);

        if (getEventByIdResponse.getData().getEvent().get(0).getReminder_id() != null && getEventByIdResponse.getData().getEvent().get(0).getIsRecurrence()!=0) {
            obj.setRemind_id(getEventByIdResponse.getData().getEvent().get(0).getReminder_id());
            obj.setRemind_date(getEventByIdResponse.getData().getEvent().get(0).getRemindOn());
           // obj.setTimezone_offset(timezoneName);
            obj.setIs_recurrence("0");
        }
        if(getEventByIdResponse.getData().getEvent().get(0).getIsRecurrence()==1){
          //  obj.setTimezone_offset(timezoneName);
            obj.setIs_recurrence("1");
        }

        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.createReminder(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    assert response.body() != null;
                    JsonObject eventsJson = response.body().getAsJsonObject("data");
                    Reminder datum = new Gson().fromJson(eventsJson.toString(), Reminder.class);
                    getEventByIdResponse.getData().getEvent().get(0).setReminder_id(datum.getId());
                    getEventByIdResponse.getData().getEvent().get(0).setRemindOn(datum.getRemind_on());
                }, throwable -> {
                }));
    }


    @Override
    public void onEventEditRequested() {
        SweetAlertDialog confirmCancel = Utilities.confirmCancel(this);
        confirmCancel.show();
        Utilities.addNegativeButtonMargin(confirmCancel);
        Utilities.addPositiveButtonMargin(confirmCancel);
        confirmCancel.setConfirmClickListener(sDialog -> {
            Bundle bundle = new Bundle();
            getEventByIdResponse.getData().getEvent().get(0).setEventId(eventID);
            bundle.putParcelable(DATA, getEventByIdResponse.getData().getEvent().get(0));
            startActivityForResult(new Intent(CreatedEventDetailActivity.this, CreateEventActivity.class).putExtras(bundle), EVENT_DETAILS_REQUEST_CODE);
            sDialog.dismissWithAnimation();
        });
        confirmCancel.setCancelClickListener(SweetAlertDialog::cancel);
    }

    @Override
    public void onEventCancelRequested() {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event_id", eventID);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("status", "cancel");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.delterOrCancelEvent(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgressDialog();
                onBackPressed();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgressDialog();
                showErrorDialog("Something went wrong ! Please try again later");
            }
        });
    }

    @Override
    public void onEventDeleteRequested() {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event_id", eventID);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("status", "delete");
        jsonObject.addProperty("action", "delete_family");
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.delterOrCancelEvent(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgressDialog();
                onBackPressed();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgressDialog();
                showErrorDialog("Something went wrong ! Please try again later");
            }
        });
    }

    @OnClick({R.id.goBack, R.id.eventActions, R.id.img_banner})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goBack:
                finish();
                break;
            case R.id.eventActions:
                showEventActionsPopUp();
                break;
            case R.id.img_banner:
                Intent imageChangingIntent = new Intent(getApplicationContext(), ImageChangerActivity.class);
                imageChangingIntent.putExtra(DATA, eventDetail);
                imageChangingIntent.putExtra(TYPE, EVENT_LOGO);
                imageChangingIntent.putExtra(IDENTIFIER, isAdmin);
                startActivityForResult(imageChangingIntent, ImageChangerActivity.REQUEST_CODE);
                break;
        }
    }

    private void showEventActionsPopUp() {
        //Blocking code before initialization
        if (eventDetail.getCreatedBy() == null)
            return;
        PopupMenu popup = new PopupMenu(getApplicationContext(), eventActions);
        popup.getMenuInflater().inflate(R.menu.popup_menu_event_action, popup.getMenu());
        if (!eventDetail.getCreatedBy().toString().equals(SharedPref.getUserRegistration().getId())) {
            popup.getMenu().getItem(0).setVisible(false);
            popup.getMenu().getItem(1).setVisible(false);
            popup.getMenu().getItem(2).setVisible(false);
        }
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.editEvent:
                    onEventEditRequested();
                    break;
                case R.id.cancelEvent:
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Cancel Event");
                    alert.setMessage("Are you sure you want to cancel ?");
                    alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        onEventCancelRequested();
                        dialog.dismiss();
                    });
                    alert.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel());
                    AlertDialog alertDialog = alert.show();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 20, 0);
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(params);
                    break;
                case R.id.deleteEvent:
                    AlertDialog.Builder deleteAlertDialogBuilder = new AlertDialog.Builder(this);
                    deleteAlertDialogBuilder.setTitle("Delete Event");
                    deleteAlertDialogBuilder.setMessage("Are you sure you want to delete ?");
                    deleteAlertDialogBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        onEventDeleteRequested();
                        dialog.dismiss();
                    });
                    deleteAlertDialogBuilder.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel());
                    AlertDialog deleteAlertDialog = deleteAlertDialogBuilder.show();
                    LinearLayout.LayoutParams deleteLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    deleteLayoutParams.setMargins(0, 0, 20, 0);
                    deleteAlertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(deleteLayoutParams);
                    break;
                case R.id.copyEventUrl:
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, eventDetail.getEventName());
                    i.putExtra(Intent.EXTRA_TEXT, eventDetail.getEventUrl());
                    startActivity(Intent.createChooser(i, "Share " + eventDetail.getEventName()));
                    break;
            }
            return true;
        });
        popup.show();
    }

    @Override
    public void onDeleteContact(String id) {
        showDeletePopup(id);
        mBottomSheetDialog.cancel();

    }

    private void showDeletePopup(String id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete entry");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callDeleteEventContactApi(id);
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.show();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 20, 0);
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(params);
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public void loadSubFolder(String id, String folderId, int type, String title, String description, boolean isAdmin, boolean canCreate, boolean canUpdate) {
        Intent intent = new Intent(this, SubFolderActivity.class);
        intent.putExtra(Constants.Bundle.ID, groupId).putExtra(Constants.Bundle.FOLDER_ID, folderId);
        intent.putExtra(Constants.Bundle.TYPE, type);
        intent.putExtra(Constants.Bundle.TITLE, title);
        intent.putExtra(Constants.Bundle.DESCRIPTION, description);
        intent.putExtra(IS_ADMIN, this.isAdmin);
        intent.putExtra(IS_ADMIN, isAdmin);
        intent.putExtra(CAN_CREATE, canCreate);
        intent.putExtra(CAN_UPDATE, canUpdate);
        startActivity(intent);
    }


    @Override
    public void onDeleteSignupClicked(String id) {
        callDeleteSignupApi(id);
    }

    private void callDeleteSignupApi(String id) {
        showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("event_id", eventID);
        apiServiceProvider.deleteSignup(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgressDialog();
                signupFragment.onRefreshItems();

            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                hideProgressDialog();
            }
        });
    }

    private void collapsingToolBarBehaviour() {
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (eventDetail != null && eventDetail.getEventName() != null)
                        toolBarTitle.setText(eventDetail.getEventName());
                    isShow = true;
                } else if (isShow) {
                    toolBarTitle.setText("");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (eventDetail.isAdminViewing()) {
            rsvp.setVisibility(View.GONE);
        } else if (tab.getPosition() != 0) {
            rsvp.setVisibility(View.GONE);
        } else {
            if (eventDetail.isPastEvent())
                rsvp.setVisibility(View.GONE);
            else
                rsvp.setVisibility(View.VISIBLE);
        }
        appBar.setExpanded(false);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void networkError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> {
                    if (!eventID.equals("0"))
                        fetchEventDetailApi();
                }).setNegativeButton("Cancel", (dialog, which) -> {
            finish();
            dialog.dismiss();
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        AlertDialog alert = builder.create();
        alert.setTitle("Connection Unavailable");
        alert.show();
        params.setMargins(0, 0, 20, 0);
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVENT_DETAILS_REQUEST_CODE) {
            if (!eventID.equals("0"))
                fetchEventDetailApi();
        } else if (requestCode == ImageChangerActivity.REQUEST_CODE) {
            if (!eventID.equals("0"))
                fetchEventDetailApi();
        }
    }


    private void openAppGetParams(String url) {
        // UserNotification
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", url);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.openAppGetParams(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    progressDialog.dismiss();
                    if(response.body()!=null) {
                        if (response.body().getData().getSub_type().equals("family_link")) {
                            response.body().getData().setSub_type("");
                        }
                        response.body().getData().goToCorrespondingDashboard(this);
                    }else{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                }, throwable -> progressDialog.dismiss()));
    }
    public int compareDate(Long formatDate){
        Calendar toDayCalendar = Calendar.getInstance();
        Date date1 = toDayCalendar.getTime();
        long timestampLong = Long.parseLong(formatDate.toString())*1000;
        Date date2 = new Date(timestampLong);

// date1 is a present date and date2 is tomorrow date
        int value =date1.compareTo(date2);
        return value;

//            //  0 comes when two date are same,
//            //  1 comes when date1 is higher then date2
//            // -1 comes when date1 is lower then date2




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
