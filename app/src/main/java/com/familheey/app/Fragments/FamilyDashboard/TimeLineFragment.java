package com.familheey.app.Fragments.FamilyDashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;

import butterknife.ButterKnife;

public class TimeLineFragment extends Fragment {


    private FamilyDashboardInteractor mListener;

    public TimeLineFragment() {
        // Required empty public constructor
    }

    public static TimeLineFragment newInstance(Family family) {
        TimeLineFragment fragment = new TimeLineFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, family);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_line, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, FamilyDashboardInteractor.class);
        if (mListener == null)
            throw new RuntimeException(context.toString() + " must implement FamilyDashboardInteractor");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
