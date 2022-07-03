package com.familheey.app.Fragments.Events;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.familheey.app.Activities.EventInvitationsListingActivity;
import com.familheey.app.Activities.GuestActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.GUEST_ATTENDING;
import static com.familheey.app.Utilities.Constants.Bundle.GUEST_MAY_ATTEND;
import static com.familheey.app.Utilities.Constants.Bundle.GUEST_NOT_ATTENDING;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuestsFragment extends Fragment {
    String[] myDataset = new String[5];
    @BindView(R.id.may_attend_count)
    TextView mayAttendCount;
    @BindView(R.id.may_attend)
    CardView mayAttend;
    @BindView(R.id.attending_count)
    TextView attendingCount;
    @BindView(R.id.attending)
    CardView attending;
    @BindView(R.id.not_attending_count)
    TextView notAttendingCount;
    @BindView(R.id.not_attend)
    CardView notAttend;
    @BindView(R.id.invitation_sent_count)
    TextView invitationSentCount;
    @BindView(R.id.invitation_sent)
    CardView invitationSent;

    private EventDetail eventDetail;

    public GuestsFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.progressBar)
    ProgressBar progressContainer;

    public static GuestsFragment newInstance(EventDetail eventDetail) {
        GuestsFragment fragment = new GuestsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, eventDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guests, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void getGuestCount() {
        if (progressContainer!=null)
        progressContainer.setVisibility(View.VISIBLE);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(FamilheeyApplication.getInstance());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event_id", eventDetail.getEventId());
        apiServiceProvider.getGuestCount(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                progressContainer.setVisibility(View.GONE);
                JsonParser jsonParser = new JsonParser();
                JsonObject responseJson = (JsonObject) jsonParser.parse(responseBodyString);
                if (responseJson.has("data")) {
                    JsonObject data = responseJson.getAsJsonObject("data");
                    String invitation = getString("invitationsSend", data);
                    String mayattend = getString("interested", data);
                    String notGoing = getString("notGoing", data);
                    String attending = getString("allCount", data);
                    invitationSentCount.setText(invitation);
                    mayAttendCount.setText(mayattend);
                    notAttendingCount.setText(notGoing);
                    attendingCount.setText(attending);
                }
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                if (progressContainer!=null)
                    progressContainer.setVisibility(View.GONE);
                attendingCount.setText("N/A");
                mayAttendCount.setText("N/A");
                notAttendingCount.setText("N/A");
                invitationSentCount.setText("N/A");
            }
        });
    }

    private String getString(String name, JsonObject jsonObject) {
        if (jsonObject.has(name))
            return jsonObject.get(name).toString();

        return "N/A";
    }


    @OnClick({R.id.may_attend, R.id.attending, R.id.not_attend, R.id.invitation_sent})
    public void onViewClicked(View view) {
        if (eventDetail.isPastEvent()) {
           // Toast.makeText(getContext(), "This is a past event", Toast.LENGTH_SHORT).show();
            //return;
        }else {
            //Toast.makeText(getContext(), "Meeting is on going", Toast.LENGTH_SHORT).show();
        }
        if (!eventDetail.isAdminViewing()) {
            Toast.makeText(getContext(), "You must be a admin to view guests list", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.may_attend:
                startActivity(new Intent(getActivity(), GuestActivity.class).putExtra(Constants.Bundle.DATA, eventDetail.getEventId()).putExtra(Constants.Bundle.POSITION, GUEST_MAY_ATTEND));
                break;
            case R.id.attending:
                startActivity(new Intent(getActivity(), GuestActivity.class).putExtra(Constants.Bundle.DATA, eventDetail.getEventId()).putExtra(Constants.Bundle.POSITION, GUEST_ATTENDING));
                break;
            case R.id.not_attend:
                startActivity(new Intent(getActivity(), GuestActivity.class).putExtra(Constants.Bundle.DATA, eventDetail.getEventId()).putExtra(Constants.Bundle.POSITION, GUEST_NOT_ATTENDING));
                break;
            case R.id.invitation_sent:
                Log.i("event_id",eventDetail.getEventId());
                startActivity(new Intent(getActivity(), EventInvitationsListingActivity.class).putExtra(Constants.Bundle.DATA, eventDetail.getEventId()));
                break;
        }
    }

    public void updateEvent(EventDetail eventDetail) {
        this.eventDetail = eventDetail;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (eventDetail.getEventId()!=null)
        getGuestCount();
    }
}
