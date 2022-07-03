package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.familheey.app.Fragments.Events.CreateEventFragment;
import com.familheey.app.Fragments.Events.CreateEventStep2Fragment;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.Bundle.EVENT_ID;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.JOIN_FAMILY_ID;

public class CreateEventActivity extends AppCompatActivity implements CreateEventFragment.CreateEventInterface, CreateEventStep2Fragment.CreateEventStep2Frag {
    private EventDetail eventDetail;
    private String id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Intent intent = getIntent();


        if (intent.hasExtra(Constants.Bundle.DATA)) {
            Bundle b = intent.getExtras();
            if (b != null) {
                eventDetail = b.getParcelable(Constants.Bundle.DATA);


                if (eventDetail != null) {

                    loadFragment(CreateEventFragment.newInstance(eventDetail, id));
                    return;
                }
            }
        }

        if (intent.hasExtra(ID)) {
            id = String.valueOf(getIntent().getIntExtra(ID, 0));
        }
        loadFragment(CreateEventFragment.newInstance(null, id));

    }


    public void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    public void loadFragmentReplace(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .addToBackStack("CreateEventStep2Fragment")
                    .commit();
        }
    }

    @Override
    public void onCreateEventContinueClicked(JsonObject jsonObject) {
        loadFragmentReplace(CreateEventStep2Fragment.newInstance(jsonObject, null));
    }

    @Override
    public void onUpdateEventContinueClicked(JsonObject jsonObject) {
        if (eventDetail != null)
            loadFragmentReplace(CreateEventStep2Fragment.newInstance(jsonObject, eventDetail));
        else
            loadFragmentReplace(CreateEventStep2Fragment.newInstance(jsonObject, null));
    }

    @Override
    public void onEventCreateSuccessResponse(int eventId, boolean isUpdate) {
        SweetAlertDialog dialog;
        if (isUpdate)
            dialog = Utilities.succesDialog(this, "Event updated successfully");
        else
            dialog = Utilities.succesDialog(this, "Event created successfully");

        dialog.show();
        Utilities.addPositiveButtonMargin(dialog);
        dialog.setConfirmClickListener(sDialog -> {
            if (!isUpdate) {
                sDialog.dismissWithAnimation();
                Intent intent = new Intent(this, CreatedEventDetailActivity.class);
                intent.putExtra(ID, eventId + "");
                startActivity(intent);

            } else {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
            }
            finish();
        });
    }
}