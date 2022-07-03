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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragment extends Fragment {
    //  Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //  Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FamilyDashboardInteractor mListener;

    public AlbumFragment() {
        // Required empty public constructor
    }

    private Family family;

    public static AlbumFragment newInstance(Family family) {
        AlbumFragment fragment = new AlbumFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
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

    public void startSearch(String toString) {
    }
}
