package com.familheey.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.CustomViews.FSpinner;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.CreateAlbumEventResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.Bundle.CAN_CREATE;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_UPDATE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.DESCRIPTION;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ALBUM;
import static com.familheey.app.Utilities.Constants.Bundle.IS_FAMILY_SETTINGS_NEEDED;
import static com.familheey.app.Utilities.Constants.Bundle.IS_UPDATE_MODE;
import static com.familheey.app.Utilities.Constants.Bundle.MEMBER;
import static com.familheey.app.Utilities.Constants.Bundle.PERMISSION;
import static com.familheey.app.Utilities.Constants.Bundle.PERMISSION_GRANTED_USERS;
import static com.familheey.app.Utilities.Constants.Bundle.TITLE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.FileUpload.TYPE_FAMILY;
import static com.familheey.app.Utilities.Constants.FileUpload.TYPE_USER;

public class CreateAlbumBasicActivity extends AppCompatActivity implements ProgressListener, RetrofitListener {


    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.album_name)
    EditText albumName;
    @BindView(R.id.album_description)
    EditText albumDescription;
    @BindView(R.id.add_album)
    MaterialButton addAlbum;
    @BindView(R.id.progressBar5)
    ProgressBar progressBar;
    @BindView(R.id.whoCanViewAlbums)
    FSpinner whoCanViewAlbums;

    String eventId = "";
    int type;
    boolean isEdit = false;
    boolean isAlbum = true;
    boolean isListenerEnabled = true;
    @BindView(R.id.labelWhoCanViewAlbums)
    TextView labelWhoCanViewAlbums;
    @BindView(R.id.labelName)
    TextView labelName;
    private SweetAlertDialog progressDialog;
    private String folderId = "";
    private ArrayList<String> visibilityModes = new ArrayList<>();
    private ArrayList<Integer> familyMembers = new ArrayList<>();
    private int selectedVisibilityMode = 0;
    private String permission = "";
    private boolean isFamilySettingsNeeded = false;
    AdapterView.OnItemSelectedListener whoCanViewAlbumsSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (isListenerEnabled) {
                selectedVisibilityMode = position;
                switch (position) {
                    case 0:
                    case 2:
                        familyMembers.clear();
                        break;
                    case 1:
                        if (type == TYPE_USER)
                            return;
                        Intent intent = new Intent(getApplicationContext(), SelectMemberActivity.class);
                        intent.putExtra(DATA, eventId);
                        intent.putIntegerArrayListExtra(MEMBER, familyMembers);
                        startActivityForResult(intent, SelectMemberActivity.REQUEST_CODE);
                        break;
                }
            } else
                isListenerEnabled = true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void initializeToolbar() {
        if (isAlbum) {
            labelName.setText("Album name");
            labelWhoCanViewAlbums.setText("Who can view album");
            if (isEdit) {
                toolBarTitle.setText("Update Album");
                addAlbum.setText("Update");
            } else {
                toolBarTitle.setText("Create Album");
                addAlbum.setText("Add Album");
            }
        } else {
            labelName.setText("Folder name");
            labelWhoCanViewAlbums.setText("Who can view folder");
            if (isEdit) {
                toolBarTitle.setText("Update Folder");
                addAlbum.setText("Update");
            } else {
                toolBarTitle.setText("Create Folder");
                addAlbum.setText("Add Folder");
            }
        }
        goBack.setOnClickListener(v -> finish());
    }

    private void initializeRestrictions() {
        if (type == TYPE_FAMILY || type == TYPE_USER) {
            if (type == TYPE_FAMILY && !isFamilySettingsNeeded) {
                whoCanViewAlbums.setVisibility(View.VISIBLE);
                labelWhoCanViewAlbums.setVisibility(View.VISIBLE);
            } else {
                whoCanViewAlbums.setVisibility(View.VISIBLE);
                labelWhoCanViewAlbums.setVisibility(View.VISIBLE);
            }
        } else {
            whoCanViewAlbums.setVisibility(View.INVISIBLE);
            labelWhoCanViewAlbums.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album_basic);
        ButterKnife.bind(this);
       /* if (getCallingActivity().getPackageName().equals("CreateAlbumBasicActivity")) {}*/
            Intent intent=getIntent();
            eventId = intent.getStringExtra(DATA);
            type = intent.getIntExtra(TYPE, 0);
            isAlbum = intent.getBooleanExtra(IS_ALBUM, true);
            isFamilySettingsNeeded = intent.getBooleanExtra(IS_FAMILY_SETTINGS_NEEDED, true);
            initializeSpinner();
            if (intent.hasExtra(TITLE)) {
                folderId = intent.getStringExtra(ID);
                isEdit = true;
                setData(intent.getStringExtra(TITLE), intent.getStringExtra(DESCRIPTION), intent.getStringExtra(PERMISSION),intent.getIntegerArrayListExtra(PERMISSION_GRANTED_USERS));
            }
            //end - upto this part inside commented if//

        initializeRestrictions();
        initializeToolbar();
    }

    private void initializeSpinner() {
        visibilityModes.clear();
        if (type == TYPE_USER) {
            visibilityModes.add("Anyone (Public)");
            visibilityModes.add("My Connections");
            visibilityModes.add("Only Me (Private)");
        } else {
            visibilityModes.add("All Members");
            visibilityModes.add("Selected Members");
            visibilityModes.add("Only Me");
        }
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, visibilityModes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whoCanViewAlbums.setAdapter(spinnerAdapter);
        whoCanViewAlbums.setOnItemSelectedListener(whoCanViewAlbumsSelectedListener);
    }

    @OnClick(R.id.goBack)
    public void onViewClicked() {
        onBackPressed();
    }

    private void setData(String stringTitle, String stringDescription, String permission, List<Integer> permissionGrantedUsers) {
        this.permission = permission;
        albumDescription.setText(stringDescription);
        albumName.setText(stringTitle);
        if (type == TYPE_FAMILY || type == TYPE_USER) {
            if (permission.equalsIgnoreCase("all")) {
                whoCanViewAlbums.setSelection(0, false);
            } else if (permission.equalsIgnoreCase("only-me")) {
                whoCanViewAlbums.setSelection(2, false);
            } else {
                isListenerEnabled = false;
                familyMembers.addAll(permissionGrantedUsers);
                whoCanViewAlbums.post(() -> whoCanViewAlbums.setSelection(1, false));
            }
        }
    }

    @Override
    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        if (progressDialog == null) {
            progressDialog = Utilities.getErrorDialog(this, errorMessage);
            progressDialog.show();
            return;
        }
        Utilities.getErrorDialog(progressDialog, errorMessage);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        if (!isEdit) {
            if (isAlbum) {
                CreateAlbumEventResponse getEventByIdResponse = new Gson().fromJson(responseBodyString, CreateAlbumEventResponse.class);
                Intent intent = new Intent(getApplicationContext(), CreateAlbumDetailedActivity.class);
                intent.putParcelableArrayListExtra(DATA, getEventByIdResponse.getRows());
                intent.putExtra(IS_UPDATE_MODE, false);
                intent.putExtra(IS_ADMIN, true);
                intent.putExtra(CAN_UPDATE, true);
                intent.putExtra(CAN_CREATE, true);
                intent.putExtra(TYPE, type);
                intent.putExtra("FROM", "NORMAL");
                startActivity(intent);
            }
            finish();
        } else {
            //edit
            Intent intent = getIntent();
            intent.putExtra(TITLE, albumName.getText().toString());
            intent.putExtra(DESCRIPTION, albumDescription.getText().toString());
            if (type == TYPE_FAMILY || type == TYPE_USER) {
                intent.putExtra(PERMISSION, permission);
                intent.putExtra(PERMISSION_GRANTED_USERS, familyMembers);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
//            if (getCallingActivity().getPackageName().equals("CreateAlbumBasicActivity")) {
//                Intent intent = getIntent();
//                intent.putExtra(TITLE, albumName.getText().toString());
//                intent.putExtra(DESCRIPTION, albumDescription.getText().toString());
//                if (type == TYPE_FAMILY || type == TYPE_USER) {
//                    intent.putExtra(PERMISSION, permission);
//                    intent.putExtra(PERMISSION_GRANTED_USERS, familyMembers);
//                }
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        addAlbum.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectMemberActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                familyMembers.clear();
                assert data != null;
                familyMembers.addAll(Objects.requireNonNull(data.getParcelableArrayListExtra(DATA)));
            }
        }
    }

    @OnClick(R.id.add_album)
    public void onAddAlbumClicked() {
        if (albumName.getText().toString().trim().length() == 0) {
            if (labelName.getText().toString().contains("Album")) {
                Toast.makeText(this, "Album name required !", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Folder name required !", Toast.LENGTH_SHORT).show();

            }

            return;
        }
        if ((type == TYPE_FAMILY) && selectedVisibilityMode == 1 && familyMembers != null && familyMembers.size() == 0) {
            Toast.makeText(this, "Please select family members !", Toast.LENGTH_SHORT).show();
            return;
        }
        addAlbum.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("folder_name", albumName.getText().toString());
        if (isAlbum)
            jsonObject.addProperty("folder_type", "albums");
        else jsonObject.addProperty("folder_type", "documents");
        jsonObject.addProperty("description", albumDescription.getText().toString());
        if (isEdit) {
            jsonObject.addProperty("id", folderId);
        } else
            jsonObject.addProperty("created_by", SharedPref.getUserRegistration().getId());
        switch (type) {
            case Constants.FileUpload.TYPE_EVENTS:
                if (!isEdit)
                    jsonObject.addProperty("event_id", eventId);
                jsonObject.addProperty("folder_for", "events");
                jsonObject.addProperty("permissions", "all");
                permission = "all";
                break;
            case TYPE_FAMILY:
                if (!isEdit)
                    jsonObject.addProperty("group_id", eventId);
                jsonObject.addProperty("folder_for", "groups");
                switch (selectedVisibilityMode) {
                    case 0:
                        if (!isFamilySettingsNeeded)
                            break;
                        jsonObject.addProperty("permissions", "all");
                        permission = "all";
                        break;
                    case 1:
                        if (!isFamilySettingsNeeded)
                            break;
                        jsonObject.addProperty("permissions", "selected");
                        permission = "selected";
                        JsonArray selectedMembers = new JsonArray();
                        for (Integer selectedMember : familyMembers) {
                            selectedMembers.add(selectedMember);
                        }
                        jsonObject.add("users_id", selectedMembers);
                        break;
                    case 2:
                        if (!isFamilySettingsNeeded)
                            break;
                        jsonObject.addProperty("permissions", "only-me");
                        permission = "only-me";
                        break;
                }
                break;
            case Constants.FileUpload.TYPE_USER:
                if (!isEdit)
                    jsonObject.addProperty("user_id", eventId);
                jsonObject.addProperty("folder_for", "users");
                jsonObject.addProperty("permissions", "all");
                switch (selectedVisibilityMode) {
                    case 0:
                        jsonObject.addProperty("permissions", "all");
                        permission = "all";
                        break;
                    case 1:
                        jsonObject.addProperty("permissions", "private");
                        permission = "private";
                        break;
                    case 2:
                        jsonObject.addProperty("permissions", "only-me");
                        permission = "only-me";
                        break;
                }
                break;
        }

        if (!isEdit) {
            apiServiceProvider.createFolder(jsonObject, null, this);
        } else {
            apiServiceProvider.editFolder(jsonObject, null, this);

        }
    }
}
