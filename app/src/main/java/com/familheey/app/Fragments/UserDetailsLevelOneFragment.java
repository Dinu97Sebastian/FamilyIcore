package com.familheey.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.familheey.app.Interfaces.RegistrationInteractor;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Utilities.hideSoftKeyboard;
import static com.familheey.app.Utilities.Utilities.isValidEmail;

public class UserDetailsLevelOneFragment extends Fragment {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.genderMale)
    ImageView genderMale;
    @BindView(R.id.genderFemale)
    ImageView genderFemale;
    @BindView(R.id.genderMaleSelected)
    ImageView genderMaleSelected;
    @BindView(R.id.genderFemaleSelected)
    ImageView genderFemaleSelected;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.next)
    MaterialButton next;
    @BindView(R.id.genderRatherNotSay)
    ImageView genderRatherNotSay;
    @BindView(R.id.genderRatherNotSaySelected)
    ImageView genderRatherNotSaySelected;
    @BindView(R.id.labelGenderRatherNotSay)
    TextView labelGenderRatherNotSay;
    @BindView(R.id.parentScroller)
    NestedScrollView parentScroller;


    private UserRegistrationResponse userRegistration;
    private RegistrationInteractor mListener;

    public UserDetailsLevelOneFragment() {
        // Required empty public constructor
    }

    public static UserDetailsLevelOneFragment newInstance(UserRegistrationResponse userRegistration) {
        UserDetailsLevelOneFragment fragment = new UserDetailsLevelOneFragment();
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
            if (userRegistration == null)
                userRegistration = new UserRegistrationResponse();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        email.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (isValidEmail(email.getText().toString())) {
                    hideSoftKeyboard(requireActivity());
                }
                return true;
            }
            return false;
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    String s = charSequence.toString();
                    if (s.contains(" ") || !isValidName(s)) {
                        name.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    String s = charSequence.toString();
                    if (s.contains(" ") || !isValidName(s)) {
                        email.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details_level_one, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefillBasicDetails();
        Utilities.attachEmptyListener(name, email);
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

    @OnClick({R.id.genderMale, R.id.genderFemale, R.id.genderRatherNotSay, R.id.next, R.id.email})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.genderMale:
                genderFemaleSelected.setVisibility(View.INVISIBLE);
                genderMaleSelected.setVisibility(View.VISIBLE);
                genderRatherNotSaySelected.setVisibility(View.INVISIBLE);
                break;
            case R.id.genderFemale:
                genderFemaleSelected.setVisibility(View.VISIBLE);
                genderMaleSelected.setVisibility(View.INVISIBLE);
                genderRatherNotSaySelected.setVisibility(View.INVISIBLE);
                break;
            case R.id.genderRatherNotSay:
                genderFemaleSelected.setVisibility(View.INVISIBLE);
                genderMaleSelected.setVisibility(View.INVISIBLE);
                genderRatherNotSaySelected.setVisibility(View.VISIBLE);
                break;
            case R.id.next:
                if (!isValid())
                    return;
                userRegistration.setFullName(name.getText().toString());
                if (genderMaleSelected.getVisibility() == View.VISIBLE)
                    userRegistration.setGender("male");
                else if (genderFemaleSelected.getVisibility() == View.VISIBLE)
                    userRegistration.setGender("female");
                else userRegistration.setGender("");
                userRegistration.setEmail(email.getText().toString());
                mListener.loadUserRegistrationLevelTwo(userRegistration);
                break;

        }
    }


    public void prefillBasicDetails() {
        if (userRegistration.getFullName() != null)
            name.setText(userRegistration.getFullName());
        if (userRegistration.getEmail() != null)
            email.setText(userRegistration.getEmail());
    }

    private boolean isValid() {
        boolean isValid = true;
        if (name.getText().toString().trim().length() <= 2) {
            isValid = false;
            name.setError("Name should be minimum of three characters");
        }

        if (!isValidName(name.getText().toString().trim().replace(" ", ""))) {
            isValid = false;
            name.setError("No special characters allowed");
        }


        if (email.getText().toString().trim().length() == 0) {
            isValid = false;
            email.setError("Required Field");
        } else {
            if (!isValidEmail(email.getText().toString().trim())) {
                isValid = false;
                email.setError("Invalid email");
                Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_SHORT).show();
            }
        }
        if (genderMaleSelected.getVisibility() != View.VISIBLE && genderFemaleSelected.getVisibility() != View.VISIBLE && genderRatherNotSaySelected.getVisibility() != View.VISIBLE) {
            isValid = false;
            Toast.makeText(getContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    public boolean isValidName(String name) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        boolean b = m.find();
        return !b;
    }
}
