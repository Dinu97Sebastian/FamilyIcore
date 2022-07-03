package com.familheey.app.Fragments.Events;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class CreateEventFragment extends Fragment implements RetrofitListener {

    @BindView(R.id.recurrence_count)
    TextView recurrence_count;
    @BindView(R.id.spinnerRecurrence)
    Spinner recurrence_type;
    @BindView(R.id.recurringEvents)
    Switch recurringEvents;
    @BindView(R.id.linearLayout)
    LinearLayout hiding_layout;


    @BindView(R.id.event_name)
    EditText eventName;
    @BindView(R.id.spinnerEvent)
    Spinner event_type;
    @BindView(R.id.event_category)
    Spinner eventCategory;
    @BindView(R.id.allowGuestToInviteOthers)
    Switch allowGuestToInviteOthers;
    @BindView(R.id.buttonContinue)
    MaterialButton buttonContinue;
    @BindView(R.id.makeYourEventPrivate)
    Switch makeYourEventPrivate;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.allowGuestToBringPeople)
    Switch allowGuestToBringPeople;
    @BindView(R.id.textView8)
    TextView labelAllowGuestToInviteOther;
    @BindView(R.id.textView33)
    TextView labelAllowGuestToBringPeople;

    @BindView(R.id.mainview)
    ConstraintLayout constraintLayout;
    boolean updating = false;

    @BindView((R.id.goBack))
    ImageView goBack;


    private EventDetail eventDetail;
    private final ArrayList<String> eventTypeArray = new ArrayList<>();
    private final ArrayList<String> eventCategoryArray = new ArrayList<>();
    private CreateEventInterface createEventInterface;
    private ArrayAdapter categoryTypeAdapter;
    String id = "";

    private static final SimpleDateFormat userDisplay = new SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.getDefault());
    private final ArrayList<String> recurrenceTypeArray = new ArrayList<>();
    private Calendar calendar;

    public static CreateEventFragment newInstance(EventDetail eventDetail, String id) {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        if (eventDetail != null) {
            args.putParcelable(Constants.Bundle.DATA, eventDetail);
            fragment.setArguments(args);
        }

        if (id != null) {
            args.putString(Constants.Bundle.ID, id);
            fragment.setArguments(args);

        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventDetail = getArguments().getParcelable(Constants.Bundle.DATA);
            id = getArguments().getString(Constants.Bundle.ID);
            updating = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Add Event");
        if (eventDetail != null)
            toolBarTitle.setText(R.string.update);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListeners();
        eventTypeArray.add("Sign Up");
        eventTypeArray.add("Regular");
        eventTypeArray.add("Online");

        ArrayAdapter eventTypeAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, eventTypeArray);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        event_type.setAdapter(eventTypeAdapter);
        event_type.setSelection(2);

        categoryTypeAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, eventCategoryArray);
        categoryTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventCategory.setAdapter(categoryTypeAdapter);

        recurrenceTypeArray.add("Daily");
        recurrenceTypeArray.add("Weekly");
        recurrenceTypeArray.add("Monthly");

       ArrayAdapter recurrenceTypeAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, recurrenceTypeArray);
        recurrenceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurrence_type.setAdapter(recurrenceTypeAdapter);
        recurrence_type.setSelection(0);
        //Fetching eventDetail catagories


        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();

        apiServiceProvider.fetchEventcategory(jsonObject, null, this);
//////
        buttonContinue.setOnClickListener(view -> {
            if (validate()) {
                JsonObject object = new JsonObject();
                object.addProperty("is_public", !makeYourEventPrivate.isChecked());
                object.addProperty("is_sharable", allowGuestToInviteOthers.isChecked());
                object.addProperty("allow_others", allowGuestToBringPeople.isChecked());
                object.addProperty("created_by", SharedPref.getUserRegistration().getId());
                object.addProperty("event_name", eventName.getText().toString().trim());
                object.addProperty("event_type", event_type.getSelectedItem().toString());
                object.addProperty("category", eventCategory.getSelectedItem().toString());
                object.addProperty("group_id", id);
                if (recurringEvents.isChecked()) {
                    object.addProperty("isRecurrence","1");
                    object.addProperty("recurrence_type", recurrence_type.getSelectedItem().toString().toLowerCase());
                    object.addProperty("recurrence_count", recurrence_count.getText().toString());
                }else{
                    object.addProperty("isRecurrence","0");
                    object.addProperty("recurrence_type", "daily");
                    object.addProperty("recurrence_count", "0");
                }


                if (updating)
                    createEventInterface.onUpdateEventContinueClicked(object);

                else
                    createEventInterface.onCreateEventContinueClicked(object);
            }

        });

        initializeToolbar();

        if (eventDetail != null) {
            setPreFill();
            if (eventDetail.getEventType().contains("Sign Up"))
                event_type.setSelection(0);
            else if (eventDetail.getEventType().contains("Regular"))
                event_type.setSelection(1);
            else event_type.setSelection(2);

            if (eventDetail.getIsPublic()) {
                makeYourEventPrivate.setChecked(false);
                labelAllowGuestToInviteOther.setEnabled(false);
                labelAllowGuestToBringPeople.setEnabled(false);
                allowGuestToInviteOthers.setChecked(true);
                allowGuestToBringPeople.setChecked(true);
                allowGuestToBringPeople.setEnabled(false);
                allowGuestToInviteOthers.setEnabled(false);
            } else {
                makeYourEventPrivate.setChecked(true);
            }

            allowGuestToBringPeople.setChecked(eventDetail.getAllowOthers());

            allowGuestToInviteOthers.setChecked(eventDetail.getIsSharable());
        } else {
            makeYourEventPrivate.setChecked(true);
        }
    }

    private void initListeners() {
        goBack.setOnClickListener(view -> getActivity().finish());
        makeYourEventPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (event_type.getSelectedItemPosition() == 2) {
                        allowGuestToBringPeople.setChecked(false);
                        allowGuestToBringPeople.setEnabled(false);
                    } else {
                        allowGuestToBringPeople.setEnabled(true);
                    }
                    allowGuestToInviteOthers.setEnabled(true);
                    labelAllowGuestToBringPeople.setEnabled(true);
                    labelAllowGuestToInviteOther.setEnabled(true);
                } else {
                    if (event_type.getSelectedItemPosition() == 2) {
                        allowGuestToBringPeople.setChecked(false);
                        allowGuestToBringPeople.setEnabled(false);
                    } else {
                        labelAllowGuestToInviteOther.setEnabled(false);
                        labelAllowGuestToBringPeople.setEnabled(false);
                        allowGuestToInviteOthers.setChecked(true);
                        allowGuestToBringPeople.setChecked(true);
                        allowGuestToBringPeople.setEnabled(false);
                        allowGuestToInviteOthers.setEnabled(false);
                    }
                }
            }
        });
        recurringEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recurringEvents.setChecked(true);
                    recurringEvents.setEnabled(true);
                    hiding_layout.setVisibility(View.VISIBLE);
                }else{
                    hiding_layout.setVisibility(View.GONE);
                }
            }
        });

        event_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    allowGuestToBringPeople.setChecked(false);
                    allowGuestToBringPeople.setEnabled(false);
                } else allowGuestToBringPeople.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setPreFill() {
        eventName.setText(eventDetail.getEventName());
if(eventDetail.getIsRecurrence()==1){
    recurringEvents.setChecked(true);
    recurringEvents.setEnabled(true);
    recurrence_count.setText(String.valueOf(eventDetail.getRecurrenceCount()));
    if (eventDetail.getRecurrenceType().contains("daily"))
        recurrence_type.setSelection(0);
               else if (eventDetail.getRecurrenceType().contains("weekly"))
               recurrence_type.setSelection(1);
           else recurrence_type.setSelection(2);

}
    }

    private boolean validate() {
        if (eventName.getText().toString().trim().isEmpty()) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "     Event name is required", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }
        if (recurrence_count.getText().toString().trim().isEmpty() && recurringEvents.isChecked()) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "     Recurrence count is required", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        return eventCategoryArray.size() != 0;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        createEventInterface = (CreateEventActivity) context;
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        switch (apiFlag) {
            case Constants.ApiFlags.EVENT_CATEGORY:
                try {
                    JSONObject jsonObject = new JSONObject(responseBodyString);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if (jsonObject1.has("name")) {
                            String name = jsonObject1.getString("name");
                            if (!name.isEmpty()) {
                                name = Utilities.capitalizeFirstLetter(name);
                                progressBar.setVisibility(View.INVISIBLE);
                                eventCategoryArray.add(name);
                                categoryTypeAdapter.notifyDataSetChanged();
                                if (eventDetail != null)
                                    if (updating && name.equalsIgnoreCase(eventDetail.getCategory()))
                                        eventCategory.setSelection(i);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {

    }

    public interface CreateEventInterface {
        void onCreateEventContinueClicked(JsonObject jsonObject);

        void onUpdateEventContinueClicked(JsonObject jsonObject);
    }
}
