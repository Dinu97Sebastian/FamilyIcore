package com.familheey.app.Fragments.FamilyViewMembers;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.FamilyMembersAdapter;
import com.familheey.app.Adapters.MemberRequestAdapter;
import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.Fragments.FamilyDashboardFragment;
import com.familheey.app.Interfaces.FamilyRequestInterface;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.MemberRequests;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FamilyRequestsFragment extends Fragment implements RetrofitListener , FamilyRequestInterface {
    public static final int MEMBER_REQUEST = 0;
    public static final int LINK_REQUEST = 1;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.request_count)
    TextView request_count;
    @BindView(R.id.no_data)
    TextView no_data;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar;
    private Family family;
    private int type;
    private MemberRequestAdapter memberRequestAdapter;
    private final ArrayList<MemberRequests> memberRequestsArrayList = new ArrayList<>();
    public MemberRequestInterface requestInterface;

    public String memberCount = "";
    public String requestCount = "";

    public int memCount=0;
    public int reqCount=0;
    public FamilyRequestsFragment() {
        // Required empty public constructor
    }
    /**@author Devika on 23-09-21
     * Interface used for displaying updated members and requests count(inside FamilySubscriptionUpdatedFragment)
     * based on accepting or rejecting requests.Implemented inside FamilyDashboardActivity.
     ***/
    public interface MemberRequestInterface{
        void sendMemberCount(int member);
        void sendRequestCount(int request);
        void refreshDashboard();
    }

    public static FamilyRequestsFragment newInstance(Family family, int type) {
        FamilyRequestsFragment fragment = new FamilyRequestsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        args.putInt(Constants.Bundle.TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }
    public static FamilyRequestsFragment newInstance(Family family) {
        FamilyRequestsFragment fragment = new FamilyRequestsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(Constants.Bundle.DATA);
            type = getArguments().getInt(Constants.Bundle.TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_people_bottomsheet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        requestInterface = (MemberRequestInterface) requireActivity();
        /*if (context instanceof FamilyDashboardListener) {
            mListener = (FamilyDashboardListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FamilyDashboardListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        getMemReqCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAllRequests();
        //getMemReqCount();
    }
    private void initAdapter() {
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        memberRequestAdapter = new MemberRequestAdapter(memberRequestsArrayList, getContext(),this);
        recyclerview.setAdapter(memberRequestAdapter);
        recyclerview.addItemDecoration(new BottomAdditionalMarginDecorator());
    }
    private void fetchAllRequests() {
        if (progressBar!=null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", family.getId().toString());
        apiServiceProvider.viewMemberRequest(jsonObject, null, this);

    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        progressBar.setVisibility(View.GONE);
        memberRequestsArrayList.clear();
        switch (type) {
            case LINK_REQUEST:
                memberRequestsArrayList.addAll(validatePendingRequest(FamilyParser.parseFamilyLinkingRequests(responseBodyString)));
                break;
            case MEMBER_REQUEST:
                memberRequestsArrayList.addAll(validatePendingRequest(FamilyParser.parseMemberRequests(responseBodyString)));
               reqCount=memberRequestsArrayList.size();
                requestInterface.sendRequestCount(memberRequestsArrayList.size());
                //requestInterface.refreshDashboard();
                break;
        }
        memberRequestAdapter.notifyDataSetChanged();
    }

    private Collection<? extends MemberRequests> validatePendingRequest(ArrayList<MemberRequests> parseMemberRequests) {
        ArrayList<MemberRequests> parseMemberRequestsFilter = new ArrayList<>();
        for (MemberRequests m :
                parseMemberRequests) {
            if (m.getStatus().equalsIgnoreCase("pending")) {
                parseMemberRequestsFilter.add(m);
            }
        }
        Integer arrayLength = parseMemberRequestsFilter.size();
        SpannableString ss1 = new SpannableString(arrayLength.toString());
        ss1.setSpan(new RelativeSizeSpan(1.3f), 0, arrayLength.toString().length(), 0); // set size
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        ss1.setSpan(boldSpan, 0, arrayLength.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        request_count.setText(ss1);
        request_count.append("Requests");
        if (parseMemberRequestsFilter.size() == 0 || parseMemberRequests.size() == 0) {
            no_data.setVisibility(View.VISIBLE);
        }
        return parseMemberRequestsFilter;
    }
    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        progressBar.setVisibility(View.GONE);
    }

    public void updateFamily(Family family) {
        this.family = family;
        if(memberRequestAdapter!=null){
            memberRequestAdapter.setAdminStatus(family.whoCanApproveMemberRequest());
        }

    }
    /**@author Devika on 23-09-21
     * modified to update the requests and members count once the user clicks on accept/reject button
     ***/
    @Override
    public void onClickedAccept(String s) {
        fetchAllRequests();
        if(s.contains("rejected")){
            if(reqCount!=0) {
                reqCount = reqCount - 1;
                requestInterface.sendRequestCount(reqCount);
            }
            //NotificationManagerCompat.from(getContext()).cancelAll();
        }else{
            memCount =  memCount+1;
            requestInterface.sendMemberCount(memCount);
            if(reqCount!=0) {
                reqCount = reqCount - 1;
                requestInterface.sendRequestCount(reqCount);
            }
            requestInterface.refreshDashboard();
        }
    }

    /**@author Devika on 23-09-21
     * method to get the current members and request count to be used for
     * displaying updated request and members on clicking accept or reject button
     * **/
    public void getMemReqCount(){
        memberCount = family.getMembersCount();
        requestCount = family.getRequestCount();

        memCount = Integer.parseInt(memberCount);
        reqCount = Integer.parseInt(requestCount);
        requestInterface.sendRequestCount(reqCount);
    }

}
