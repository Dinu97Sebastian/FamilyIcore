package com.familheey.app.Fragments.Events;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.AddContactActivity;
import com.familheey.app.Adapters.ContactsAdapter;
import com.familheey.app.Models.Response.EventContacts;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Models.Response.GetEventByIdResponse;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailTabFragment extends Fragment {
    @BindView(R.id.recyclerContacts)
    RecyclerView recyclerContacts;

    @BindView(R.id.showContactButton)
    Button showContactButton;

    @BindView(R.id.labelContactForQueries)
    TextView labelContactForQueries;

    TextView txtNoContacts;

    @BindView(R.id.txtEventDescription)
    AutoLinkTextView txtEventDescription;
    @BindView(R.id.labelDialInNumber)
    TextView labelDialInNumber;
    @BindView(R.id.dialInNumber)
    AutoLinkTextView dialInNumber;
    @BindView(R.id.labelParticipantPin)
    TextView labelParticipantPin;
    @BindView(R.id.participantPin)
    TextView participantPin;
    @BindView(R.id.labelDescription)
    TextView labelDescription;

    private ContactsAdapter contactsAdapter;

    private GetEventByIdResponse.Data eventData = null;
    private final List<EventContacts> contactList = new ArrayList<>();
    private boolean isAdmin = false;

    public EventDetailTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initContactsRecycler();
        initListeners();
    }

    private void initListeners() {
        txtEventDescription.addAutoLinkMode(
                AutoLinkMode.MODE_URL);
        dialInNumber.addAutoLinkMode(
                AutoLinkMode.MODE_PHONE);
        dialInNumber.setPhoneModeColor(ContextCompat.getColor(getContext(), R.color.buttoncolor));
        txtEventDescription.setUrlModeColor(ContextCompat.getColor(getContext(), R.color.buttoncolor));
        txtEventDescription.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (autoLinkMode == AutoLinkMode.MODE_URL) {
                try {
                    String url = matchedText.trim();
                    if (!url.contains("http")) {
                        url = url.replaceAll("www.", "http://www.");
                    }
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Utilities.setClipboard(getContext(), eventData.getEvent().get(0).getMeetingPin());
            }
        });
        participantPin.setOnLongClickListener(v -> {
            Utilities.setClipboard(getContext(), eventData.getEvent().get(0).getMeetingPin());
            return false;
        });
        dialInNumber.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (autoLinkMode == AutoLinkMode.MODE_PHONE) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", matchedText, null));
                startActivity(intent);
            }
        });
    }

    private void initContactsRecycler() {
        contactsAdapter = new ContactsAdapter(contactList, getContext(), isAdmin);
        recyclerContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerContacts.setAdapter(contactsAdapter);
    }

    public void updateEventDetails(GetEventByIdResponse.Data eventData) {
        this.eventData = eventData;
        EventDetail eventDetail = eventData.getEvent().get(0);
        contactList.clear();
        contactList.addAll(eventData.getContacts());
        contactsAdapter.notifyDataSetChanged();
        if (eventDetail.isPastEvent()) {
            isAdmin = false;
        } else {
            isAdmin = eventDetail.isAdminViewing();
        }
        if (isAdmin) {
            showContactButton.setVisibility(View.VISIBLE);
        } else {
            showContactButton.setVisibility(View.INVISIBLE);
        }
        if (!isAdmin) {
            if (contactList.size() == 0)
                labelContactForQueries.setVisibility(View.INVISIBLE);
            else labelContactForQueries.setVisibility(View.VISIBLE);
        }
        contactsAdapter.setAdminStatus(isAdmin);
        if (eventDetail.getRsvp()) {
            txtEventDescription.setAutoLinkText(eventDetail.getDescription().trim() + "\n\nRSVP Required");
        } else {
            txtEventDescription.setAutoLinkText(eventDetail.getDescription().trim());
        }
        if (eventDetail.isOnlineEvent()) {
            dialInNumber.setAutoLinkText(eventDetail.getMeetingDialNumber() + "");
            participantPin.setText(eventDetail.getMeetingPin() + "");

            if (txtEventDescription.getText().toString().trim().length() > 0) {
                labelDescription.setVisibility(View.VISIBLE);
                txtEventDescription.setVisibility(View.VISIBLE);
            } else {
                labelDescription.setVisibility(View.GONE);
                txtEventDescription.setVisibility(View.GONE);
            }

            if (eventDetail.getMeetingDialNumber() != null && eventDetail.getMeetingDialNumber().trim().length() > 0) {
                labelDialInNumber.setVisibility(View.VISIBLE);
                dialInNumber.setVisibility(View.VISIBLE);
            } else {
                labelDialInNumber.setVisibility(View.GONE);
                dialInNumber.setVisibility(View.GONE);
            }
            if (eventDetail.getMeetingPin() != null && eventDetail.getMeetingPin().trim().length() > 0) {
                participantPin.setVisibility(View.VISIBLE);
                labelParticipantPin.setVisibility(View.VISIBLE);
            } else {
                participantPin.setVisibility(View.GONE);
                labelParticipantPin.setVisibility(View.GONE);
            }
        } else {
            labelDescription.setVisibility(View.GONE);
            labelParticipantPin.setVisibility(View.GONE);
            labelDialInNumber.setVisibility(View.GONE);
            dialInNumber.setVisibility(View.GONE);
            participantPin.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.showContactButton)
    public void onClick() {
        if (eventData != null && eventData.getEvent().get(0) != null && eventData.getEvent().get(0).getEventId() != null) {
            Intent eventIntent = new Intent(getContext(), AddContactActivity.class);
            eventIntent.putExtra(ID, eventData.getEvent().get(0).getEventId() + "");
            eventIntent.putExtra(IS_ADMIN, isAdmin);
            startActivity(eventIntent);
        }
    }
}
