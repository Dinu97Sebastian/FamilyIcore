package com.familheey.app.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DateSelectorDialog extends DialogFragment implements DatePicker.OnDateChangedListener {

    @BindView(R.id.datePicker)
    DatePicker datePicker;
    @BindView(R.id.pickDate)
    MaterialButton pickDate;
    @BindView(R.id.cancel)
    MaterialButton cancel;

    private OnDateSelectedListener mListener;

    public DateSelectorDialog() {
        // Required empty public constructor
    }

    public static DateSelectorDialog newInstance() {
        DateSelectorDialog fragment = new DateSelectorDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_selector_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        datePicker.init(year, month, day, this);
        datePicker.setMaxDate(System.currentTimeMillis());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, OnDateSelectedListener.class);
        if (context instanceof OnDateSelectedListener) {
            mListener = (OnDateSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDateSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.pickDate, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pickDate:
                mListener.onDateSelected(datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + String.format(Locale.getDefault(), "%02d", datePicker.getDayOfMonth()));
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + (monthOfYear + 1) + "-" + String.format(Locale.getDefault(), "%02d", dayOfMonth);
    }

    public interface OnDateSelectedListener {
        void onDateSelected(String date);
    }
}
