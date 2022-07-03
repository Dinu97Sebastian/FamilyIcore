package com.familheey.app.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.CountryCodeAdapter;
import com.familheey.app.Interfaces.CountrySelectedListener;
import com.familheey.app.Models.Country;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class CountryCodeDialogFragment extends DialogFragment implements CountryCodeAdapter.CountryMediatorListener {


    @BindView(R.id.searchCountryCode)
    EditText searchCountryCode;
    @BindView(R.id.countryList)
    RecyclerView countryList;

    private List<Country> countries = new ArrayList<>();
    CountryCodeAdapter countryAdapter;

    private CountrySelectedListener mListener;

    public CountryCodeDialogFragment() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
    }

    public static CountryCodeDialogFragment newInstance(ArrayList<Country> countries) {
        CountryCodeDialogFragment fragment = new CountryCodeDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.Bundle.DATA, countries);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            countries = getArguments().getParcelableArrayList(Constants.Bundle.DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_country_code_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeCountryCodeAdapter();
    }

    @OnTextChanged(R.id.searchCountryCode)
    protected void onTextChanged(CharSequence text) {
        countryAdapter.getFilter().filter(text.toString());
    }

    private void initializeCountryCodeAdapter() {
        countryAdapter = new CountryCodeAdapter(this::OnCountrySelected, countries);
        countryList.setLayoutManager(new LinearLayoutManager(getContext()));
        countryList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        countryList.setAdapter(countryAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, CountrySelectedListener.class);
        if (mListener == null) {
            throw new RuntimeException(context.toString()
                    + " must implement CountrySelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnCountrySelected(Country selectedCountry) {
        mListener.OnCountrySelected(selectedCountry);
        dismissAllowingStateLoss();
    }
}
