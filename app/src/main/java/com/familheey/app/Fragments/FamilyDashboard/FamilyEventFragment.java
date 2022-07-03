package com.familheey.app.Fragments.FamilyDashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.CalendarActivity;
import com.familheey.app.Activities.CreateEventActivity;
import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Adapters.ExploreAdapter;
import com.familheey.app.Adapters.FamilyEventAdapter;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Event;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.EventsParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.FAMILY;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFamilyEventFragment;

public class FamilyEventFragment extends Fragment implements RetrofitListener, FamilyEventAdapter.OnFamilyEventSelectedListener {

    /*@BindView(R.id.searchMyFamilies)
    EditText searchMyFamilies;*/
    @BindView(R.id.eventList)
    RecyclerView eventList;
    @BindView(R.id.progressListMember)
    ProgressBar progressListMember;
    @BindView(R.id.emptyIndicatorContainer)
    View emptyIndicatorContainer;
    @BindView(R.id.createEvent)
    CardView createEvent;
    private Family family;
    /*@BindView(R.id.clearSearch)
    ImageView clearSearch;*/
   /* @BindView(R.id.goToCalendar)
    ImageView goToCalendar;*/
String value = "";
    private FamilyDashboardInteractor familyDashboardInteractor;

    private final List<Event> events = new ArrayList<>();
    private ExploreAdapter familyEventAdapter;

    public FamilyEventFragment() {
        // Required empty public constructor
    }

    public static FamilyEventFragment newInstance(Family family) {
        FamilyEventFragment fragment = new FamilyEventFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(DATA);
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_event, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeAdapter();
        //initializeSearchClearCallback();
    }

   /* private void initializeSearchClearCallback() {
        searchMyFamilies.addTextChangedListener(new TextWatcher() {

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
            searchMyFamilies.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }*/

    private void fetchFamilyEvents() {
        progressListMember.setVisibility(View.VISIBLE);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("query", this.value.trim());
        apiServiceProvider.fetchFamilyGroupEvents(jsonObject, null, this);
    }

    private void initializeAdapter() {
        familyEventAdapter = new ExploreAdapter(getContext(), events);
        eventList.setAdapter(familyEventAdapter);
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList.addItemDecoration(new BottomAdditionalMarginDecorator());
        eventList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        familyDashboardInteractor = Utilities.getListener(this, FamilyDashboardInteractor.class);
        if (familyDashboardInteractor == null)
            throw new RuntimeException(context.toString() + " must implement FamilyDashboardInteractor");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        familyDashboardInteractor = null;
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        events.clear();
        events.addAll(EventsParser.parseFamilyEvents(responseBodyString));
        addTemporaryFix();
        familyEventAdapter.notifyDataSetChanged();
        if (progressListMember != null)
            progressListMember.setVisibility(View.INVISIBLE);
        if (events.size() > 0) {
            emptyIndicatorContainer.setVisibility(View.GONE);
            if (familyDashboardInteractor != null)
                familyDashboardInteractor.onFamilyAddComponentVisible(TypeFamilyEventFragment);
            //createEvent.setVisibility(View.VISIBLE);
            //searchMyFamilies.setVisibility(View.VISIBLE);
        } else {
            if (familyDashboardInteractor != null)
                familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilyEventFragment);
            //createEvent.setVisibility(View.INVISIBLE);
           // searchMyFamilies.setVisibility(View.INVISIBLE);
            emptyIndicatorContainer.setVisibility(View.VISIBLE);
        }
       /* if (events.size() == 0 && searchMyFamilies.getText().toString().trim().length() == 0) {
            searchMyFamilies.setVisibility(View.INVISIBLE);
        } else searchMyFamilies.setVisibility(View.VISIBLE);*/
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        if (progressListMember != null)
            progressListMember.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFamilyEventSelected(Event event) {
        Intent intent = new Intent(getContext(), CreatedEventDetailActivity.class);
        intent.putExtra(ID, String.valueOf(event.getEventId()));
        startActivity(intent);
    }

    @OnEditorAction(R.id.searchMyFamilies)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            fetchFamilyEvents();
            return true;
        }
        return false;
    }

    @OnClick({R.id.createEvent, R.id.goToCalendar, R.id.create_event})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.createEvent:
            case R.id.create_event:
                createNewEvent();
                break;
            case R.id.goToCalendar:
                Intent calendarIntent = new Intent(getContext(), CalendarActivity.class);
                calendarIntent.putExtra(IDENTIFIER, FAMILY);
                calendarIntent.putExtra(DATA, family);
                startActivity(calendarIntent);
                break;
        }
    }

    public void createNewEvent() {
        if (family.canCreatePost())
            startActivity(new Intent(getContext(), CreateEventActivity.class).putExtra(ID, family.getId()));
        else
            Toast.makeText(getContext(), "Only admins can create Events !!", Toast.LENGTH_SHORT).show();
    }
/** goToCalendar commented as the searchview is common for all tabs in family dashboard **/
    public void updateFamily(Family family) {
        this.family = family;
        if (family.getUserStatus().equalsIgnoreCase("admin")) {
           // goToCalendar.setVisibility(View.VISIBLE);
        } else if (family.getUserStatus().equalsIgnoreCase("not-member")) {
            //goToCalendar.setVisibility(View.GONE);
        } else {
           // goToCalendar.setVisibility(View.VISIBLE);
        }
    }

    public void addTemporaryFix() {
        for (int i = 0; i < events.size(); i++) {
            events.get(i).setFullName(events.get(i).getCreatedByName());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchFamilyEvents();
        if (events != null && events.size() > 0) {
            if (familyDashboardInteractor != null)
                familyDashboardInteractor.onFamilyAddComponentVisible(TypeFamilyEventFragment);
        } else {
            if (familyDashboardInteractor != null)
                familyDashboardInteractor.onFamilyAddComponentHidden(TypeFamilyEventFragment);
        }
    }

    public void startSearch(String value) {
        this.value = value;
        fetchFamilyEvents();
    }
}
