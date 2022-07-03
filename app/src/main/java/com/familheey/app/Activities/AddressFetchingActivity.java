package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Adapters.PlaceArrayAdapter;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textview.MaterialAutoCompleteTextView;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Fragments.UserDetailsLevelTwoFragment.LIVING_IN;
import static com.familheey.app.Fragments.UserDetailsLevelTwoFragment.ORIGIN;
import static com.familheey.app.Utilities.Constants.Bundle.ADDITIONAL_DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ADDRESS;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.PLACE;

public class AddressFetchingActivity extends AppCompatActivity {

    public static final int RequestCode = 198;

    @BindView(R.id.labelSearch)
    TextView labelSearch;
    @BindView(R.id.searcAddress)
    MaterialAutoCompleteTextView searcAddress;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar;
    @BindView(R.id.done)
    TextView done;
    String selectedLocationText = "";

    private PlaceArrayAdapter mPlaceArrayAdapter;
    private PlacesClient placesClient;
    private Place selectedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_fetching);
        ButterKnife.bind(this);
        initPlaces();
        initAdapter();
        initListeners();
    }

    private void initPlaces() {
        String apiKey = SharedPref.read(SharedPref.GOOGLE_API, "");
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);
    }

    private void initListeners() {
        searcAddress.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedPlace = null;
            hideKeyboard();
            try {
                progressBar.setVisibility(View.VISIBLE);
                final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(i);
                String placeID = null;
                if (item != null) {
                    placeID = item.placeId.toString();
                    selectedLocationText = item.description.toString();
                }

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

                FetchPlaceRequest request = null;
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields)
                            .build();
                }

                if (request != null) {
                    placesClient.fetchPlace(request).addOnSuccessListener(task -> {
                        {
                            selectedPlace = task.getPlace();
                            progressBar.setVisibility(View.GONE);
                            searcAddress.setAdapter(null);
                            fetchAddress();
                            //searcAddress.setAdapter(mPlaceArrayAdapter);
                        }
                    }).addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        selectedPlace = null;
                        e.printStackTrace();
                        Toast.makeText(this, "Unable to fetch Latitude and Longitude", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Unable to fetch Latitude and Longitude", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void initAdapter() {
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, R.layout.item_autocomplete_address);
        searcAddress.setThreshold(3);
        searcAddress.setAdapter(mPlaceArrayAdapter);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick(R.id.done)
    public void onViewClicked() {
            Intent data = new Intent();
            data.putExtra(ADDRESS, searcAddress.getText().toString());
            if (getIntent().hasExtra(IDENTIFIER))
                data.putExtra(IDENTIFIER, getIntent().getIntExtra(IDENTIFIER, 0));
            if (getIntent().hasExtra(ADDITIONAL_DATA))
                data.putExtra(ADDITIONAL_DATA, getIntent().getStringExtra(ADDITIONAL_DATA));
            data.putExtra(PLACE, selectedPlace);
            setResult(RESULT_OK, data);
            finish();
    }

    private void fetchAddress() {
        progressBar.setVisibility(View.VISIBLE);
        if (getIntent().hasExtra(IDENTIFIER)) {
            if (getIntent().getIntExtra(IDENTIFIER, 2) == LIVING_IN || getIntent().getIntExtra(IDENTIFIER, 2) == ORIGIN) {

            } else {
                progressBar.setVisibility(View.GONE);
                searcAddress.setAdapter(mPlaceArrayAdapter);
                return;
            }
        } else {
            progressBar.setVisibility(View.GONE);
            searcAddress.setAdapter(mPlaceArrayAdapter);
            return;
        }
        try {
            LatLng coordinates = selectedPlace.getLatLng(); // Get the coordinates from your place
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            assert coordinates != null;
            List<Address> addresses = geocoder.getFromLocation(
                    coordinates.latitude,
                    coordinates.longitude,
                    1);
            Address address = addresses.get(0);
            if (address != null) {
                if (searcAddress != null && address.getAdminArea() != null && address.getCountryCode() != null) {
                    if(address.getLocality()!=null&&address.getAdminArea()!=null&&address.getLocality().equals(address.getAdminArea())){
                        searcAddress.setText((address.getLocality() == null ? "" : address.getLocality() + ", ") +  address.getCountryName());
                    }
                    else
                    searcAddress.setText((address.getLocality() == null ? "" : address.getLocality() + ", ") + address.getAdminArea() + ", " + address.getCountryName());
                }
            }
            progressBar.setVisibility(View.GONE);
            if (address != null && address.getCountryName().equalsIgnoreCase(selectedLocationText)) {
                searcAddress.setText(selectedLocationText);
            }
            searcAddress.setAdapter(mPlaceArrayAdapter);
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            searcAddress.setAdapter(mPlaceArrayAdapter);
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
