package com.familheey.app.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familheey.app.Activities.AddressFetchingActivity;
import com.familheey.app.Interfaces.RegistrationInteractor;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.ADDRESS;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;

public class UserDetailsLevelTwoFragment extends Fragment implements DatePickerDialog.OnDateSetListener {


    public static final int LIVING_IN = 9856;
    public static final int ORIGIN = 4512;

    @BindView(R.id.dateOfBirth)
    EditText dateOfBirth;
    @BindView(R.id.livingIn)
    EditText livingIn;
    @BindView(R.id.origin)
    EditText etorigin;
    @BindView(R.id.next)
    MaterialButton next;
    @BindView(R.id.back)
    MaterialButton back;
    @BindView(R.id.headerText)
    TextView headerText;
    String living = "", orgin = "";
    private Calendar calendar;
    private UserRegistrationResponse userRegistration;
    private RegistrationInteractor mListener;

    public UserDetailsLevelTwoFragment() {
        // Required empty public constructor
    }

    public static UserDetailsLevelTwoFragment newInstance(UserRegistrationResponse userRegistration) {
        UserDetailsLevelTwoFragment fragment = new UserDetailsLevelTwoFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, userRegistration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userRegistration = getArguments().getParcelable(Constants.Bundle.DATA);
        }
        calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details_level_two, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dateOfBirth.setFocusable(false);
        dateOfBirth.setClickable(true);
        dateOfBirth.setLongClickable(false);
        livingIn.setFocusable(false);
        livingIn.setClickable(true);
        livingIn.setLongClickable(false);
        etorigin.setFocusable(false);
        etorigin.setClickable(true);
        etorigin.setLongClickable(false);
        headerText.setText("Hi, " + userRegistration.getFullName());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegistrationInteractor) {
            mListener = (RegistrationInteractor) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RegistrationInteractor");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.dateOfBirth, R.id.livingIn, R.id.origin, R.id.next, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:
                if (!isValid())
                    return;
                try {
                    if (dateOfBirth.getText().toString().trim().length() > 0) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
                        Date date = sdf.parse(dateOfBirth.getText().toString());
                        SimpleDateFormat ourSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String parsedDate = ourSdf.format(date);
                        userRegistration.setDob(parsedDate);
                    } else userRegistration.setDob(null);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                userRegistration.setLocation(livingIn.getText().toString());
                userRegistration.setOrigin(etorigin.getText().toString());
                userRegistration.setPropic("");
                mListener.loadUserRegistrationLevelThree(userRegistration);
                break;
            case R.id.back:
                requireActivity().onBackPressed();
                break;
            case R.id.dateOfBirth:
                captureDateOfBirth();
                break;
            case R.id.livingIn:
                Intent livingInAddressintent = new Intent(getContext(), AddressFetchingActivity.class);
                livingInAddressintent.putExtra(IDENTIFIER, LIVING_IN);
                startActivityForResult(livingInAddressintent, AddressFetchingActivity.RequestCode);
                break;
            case R.id.origin:
                Intent originAddressIntent = new Intent(getContext(), AddressFetchingActivity.class);
                originAddressIntent.putExtra(IDENTIFIER, ORIGIN);
                startActivityForResult(originAddressIntent, AddressFetchingActivity.RequestCode);
                break;
        }
    }

    public void captureDateOfBirth() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), AlertDialog.THEME_HOLO_LIGHT, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
        try {
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.parseColor("#7e57c2"));
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#7e57c2"));
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar dateOfBirthCalendar = Calendar.getInstance();
        dateOfBirthCalendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd yyyy");
        dateOfBirth.setText(dateFormatter.format(dateOfBirthCalendar.getTime()));
    }

    private boolean isValid() {
        boolean isValid = true;
        /*if (dateOfBirth.getText().toString().length() == 0) {
            isValid = false;
            dateOfBirth.setError("Required Field");
            Toast.makeText(getActivity(), "Please enter your birthday", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (livingIn.getText().toString().length() == 0) {
            isValid = false;
            Toast.makeText(getActivity(), " Please enter yor city of residence", Toast.LENGTH_SHORT).show();
            return false;

        }
        if (etorigin.getText().toString().length() == 0) {
            isValid = false;
            Toast.makeText(getActivity(), "Please enter your city of origin", Toast.LENGTH_SHORT).show();
            return false;


        }
        return isValid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddressFetchingActivity.RequestCode && resultCode == Activity.RESULT_OK) {
            if (data.getIntExtra(IDENTIFIER, ORIGIN) == ORIGIN)
                etorigin.setText(data.getStringExtra(ADDRESS));
            else {
                livingIn.setText(data.getStringExtra(ADDRESS));
                if (etorigin.getText().toString().trim().length() == 0)
                    etorigin.setText(data.getStringExtra(ADDRESS));
            }
        }
    }
}
