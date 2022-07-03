package com.familheey.app.Fragments.Events;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.familheey.app.R;

/**
 * A simple {@link Fragment} subclass.getId()
 */
public class ItemsFragment extends Fragment {
    String[] myDataset =new String[5];
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public ItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_items, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myDataset[0]="";
        myDataset[1]="";
        myDataset[2]="";
        myDataset[3]="";
        myDataset[4]="";
        intRecyclerView();
    }

    private void intRecyclerView() {
        recyclerView = (getActivity().findViewById(R.id.recyclerItem));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ItemsAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);
    }
}
