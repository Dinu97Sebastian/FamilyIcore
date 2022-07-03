package com.familheey.app.Fragments.Events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.CreateAlbumBasicActivity;
import com.familheey.app.Activities.CreateAlbumDetailedActivity;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Adapters.LisAlbumsforEventsAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Fragments.FamilyDashboard.BackableFragment;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.ListEventAlbumsResponse;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.Bundle.CAN_CREATE;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_UPDATE;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ALBUM;
import static com.familheey.app.Utilities.Constants.Bundle.IS_UPDATE_MODE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeAlbumFragment;
import static com.familheey.app.Utilities.Constants.FileUpload.TYPE_EVENTS;
import static com.familheey.app.Utilities.Constants.FileUpload.TYPE_FAMILY;
import static com.familheey.app.Utilities.Constants.FileUpload.TYPE_USER;

public class AlbumFragment extends BackableFragment implements RetrofitListener, LisAlbumsforEventsAdapter.FolderAdapterInterface, ProgressListener {

    @BindView(R.id.create_event)
    MaterialButton addEvent;
    @BindView(R.id.constraintEmpty)
    ConstraintLayout constraintEmpty;
    @BindView(R.id.recyclerListEvents)
    RecyclerView recyclerListEvents;
    @BindView(R.id.addAlbum)
    CardView fabCreateEvent;
    @BindView(R.id.progressMyEvents)
    ProgressBar progressMyEvents;
    @BindView(R.id.deleteAlbumElements)
    MaterialButton deleteAlbumElements;
    @BindView(R.id.clearSearchEvent)
    ImageView clearSearch;
    @BindView(R.id.familyToolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;

    @BindView(R.id.searchAlbumQuery)
    EditText searchAlbumQuery;
    @BindView(R.id.newtest)
    TextView newtest;
    //search icon
   /* @BindView(R.id.constraintSearch)
    ConstraintLayout constraintSearch;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.searchMyFamilies)
    EditText searchAlbumQuery;
    @BindView(R.id.imageBack)
    ImageView imageBack;*/
    @BindView(R.id.toolbar_gallery)
    Toolbar toolbar;

    private Integer id = 0;
    private LisAlbumsforEventsAdapter lisAlbumsforEventsAdapter;
    private ListEventAlbumsResponse listGroupFoldersResponse = new ListEventAlbumsResponse();
    private final List<Long> selectedElementIds = new ArrayList<>();
    private SweetAlertDialog progressDialog;
    private int type;
    private boolean isAdmin;
    private FamilyDashboardInteractor familyDashboardInteractor;
    private String parentId;
    private boolean canUpdate = true;
    private boolean canCreate = true;
    //today
    String query = "";
    String parent ="";

    public static AlbumFragment newInstance(int type, String id, boolean isAdmin) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Bundle.DATA, id);
        args.putInt(TYPE, type);
        args.putBoolean(IS_ADMIN, isAdmin);
        args.putString("parentPage", "");
        fragment.setArguments(args);
        return fragment;
    }
    public static AlbumFragment newInstance(int type, String id, boolean isAdmin,String parentPage,boolean canCreatePost) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Bundle.DATA, id);
        args.putInt(TYPE, type);
        args.putBoolean(IS_ADMIN, isAdmin);
        args.putString("parentPage",parentPage);
        args.putBoolean(CAN_CREATE, canCreatePost);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_album_fragment, container, false);
        ButterKnife.bind(this, view);
//        initializeToolbar();
        // initListener();
        return view;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            assert getArguments() != null;
            id = Integer.valueOf((Objects.requireNonNull(requireArguments().getString(Constants.Bundle.DATA))));
            type = getArguments().getInt(TYPE);
            isAdmin = getArguments().getBoolean(IS_ADMIN);
            canCreate = getArguments().getBoolean(CAN_CREATE);
            parent=getArguments().getString("parentPage");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
        * megha(02/11/2021)
        * visibility for albums in my profile albums, event's album & event's documents */
        if (type==2 ||type== 0){
            searchAlbumQuery.setVisibility(View.VISIBLE);
        }else if(type==1){
            searchAlbumQuery.setVisibility(View.GONE);

        }
        fabCreateEvent.setOnClickListener(v -> {
            if (isAdmin || canCreate) {
                createNewAlbum();
            } else {
                Toast.makeText(getActivity(), "You don't have sufficient privileges to do this  ", Toast.LENGTH_SHORT).show();
            }
        });
        addEvent.setOnClickListener(v -> {
            if (isAdmin || canCreate) {
                createNewAlbum();
            } else {
                Toast.makeText(getActivity(), "You don't have sufficient privileges to do this  ", Toast.LENGTH_SHORT).show();
            }
        });
        lisAlbumsforEventsAdapter = new LisAlbumsforEventsAdapter(type);
        recyclerListEvents.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerListEvents.setAdapter(lisAlbumsforEventsAdapter);
        deleteAlbumElements.setOnClickListener(v -> deleteAlbumElements());
        lisAlbumsforEventsAdapter.setAdminStatus(isAdmin);
        initializeCallbacks();
    }
    /** defined by Devika on 24/08/2021 **/
    @Override
    public void onBackButtonPressed() {
        if(parent.equals("fromDashboard"))
            ((FamilyDashboardActivity)requireActivity()).backAction();
        else
            requireActivity().onBackPressed();
    }

    public void createNewAlbum() {
        /** modified the if condition for getting the "admin only condition in family settings."**/
        if (isAdmin || canCreate) {
            Intent intent = new Intent(getActivity(), CreateAlbumBasicActivity.class);
            intent.putExtra(Constants.Bundle.DATA, String.valueOf(id));
            intent.putExtra(TYPE, type);
            intent.putExtra(IS_ADMIN, isAdmin);
            intent.putExtra(IS_ALBUM, true);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Only admin can create Albums", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void initializeCallbacks() {
        searchAlbumQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    clearSearch.setVisibility(View.INVISIBLE);
                else clearSearch.setVisibility(View.VISIBLE);
            }
        });
        clearSearch.setOnClickListener(v -> {
            searchAlbumQuery.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }


    private void getAllAlbums() {
        progressMyEvents.setVisibility(View.VISIBLE);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("folder_type", "albums");
        /*
        * megha(02/11/2021)
        * for albums in myprofile, events and document in events*/
        if (type==2 || type==0){
            jsonObject.addProperty("txt",searchAlbumQuery.getText().toString().trim());

        }else{
            jsonObject.addProperty("txt", this.query);
        }
        switch (type) {
            case TYPE_EVENTS:
                jsonObject.addProperty("event_id", id.toString());
                jsonObject.addProperty("folder_for", "events");
                jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                apiServiceProvider.listEventFolders(jsonObject, null, this);
                break;
            case TYPE_FAMILY:
                jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                jsonObject.addProperty("group_id", id.toString());
                jsonObject.addProperty("folder_for", "groups");
                apiServiceProvider.listGroupFolders(jsonObject, null, this);
                break;
            default:
                jsonObject.addProperty("user_id", id.toString());
                jsonObject.addProperty("profile_id", id.toString());
                jsonObject.addProperty("from_user", SharedPref.getUserRegistration().getId());
                jsonObject.addProperty("folder_for", "users");
                apiServiceProvider.listUserFolders(jsonObject, null, this);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllAlbums();
        if (type == TYPE_FAMILY) {
            if (listGroupFoldersResponse != null && listGroupFoldersResponse.getData() != null && listGroupFoldersResponse.getData().size() > 0) {
                if (familyDashboardInteractor != null)
                    familyDashboardInteractor.onFamilyAddComponentVisible(TypeAlbumFragment);
                /**05-09-21**/
                requireActivity().findViewById(R.id.addComponent).setVisibility(View.VISIBLE);
            } else {
                if (familyDashboardInteractor != null)
                    familyDashboardInteractor.onFamilyAddComponentHidden(TypeAlbumFragment);
                /**05-09-21**/
                requireActivity().findViewById(R.id.addComponent).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        progressMyEvents.setVisibility(View.GONE);
        listGroupFoldersResponse = new Gson().fromJson(responseBodyString, ListEventAlbumsResponse.class);
        lisAlbumsforEventsAdapter.setData(this, listGroupFoldersResponse.getData());
        /*
        * megha(02/11/2021)
        * for my profile and events albums  && documents in events*/
        if ((type ==2 || type == 0)) {
            if (listGroupFoldersResponse.getData().size() == 0) {
                if (searchAlbumQuery.getText().toString().trim().length() > 0) {
                    constraintEmpty.setVisibility(View.INVISIBLE);
                    emptyResultText.setVisibility(View.VISIBLE);
                    searchAlbumQuery.setVisibility(View.VISIBLE);
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    if (type == TYPE_USER && !isAdmin) {
                        constraintEmpty.setVisibility(View.INVISIBLE);
                        emptyResultText.setVisibility(View.VISIBLE);
                        searchAlbumQuery.setVisibility(View.INVISIBLE);
                        clearSearch.setVisibility(View.INVISIBLE);
                    } else {
                        searchAlbumQuery.setVisibility(View.INVISIBLE);
                        clearSearch.setVisibility(View.INVISIBLE);
                        emptyResultText.setVisibility(View.INVISIBLE);
                        constraintEmpty.setVisibility(View.VISIBLE);
                    }
                }
                if (type == TYPE_FAMILY) {
                    if (familyDashboardInteractor != null)
                        familyDashboardInteractor.onFamilyAddComponentHidden(TypeAlbumFragment);
                }
                fabCreateEvent.setVisibility(View.INVISIBLE);
            } else {
                emptyResultText.setVisibility(View.INVISIBLE);
                constraintEmpty.setVisibility(View.INVISIBLE);
                searchAlbumQuery.setVisibility(View.VISIBLE);
                if (searchAlbumQuery.getText().toString().trim().length() > 0)
                    clearSearch.setVisibility(View.VISIBLE);
                else clearSearch.setVisibility(View.INVISIBLE);
                if (type == TYPE_FAMILY) {
                    if (familyDashboardInteractor != null)
                        familyDashboardInteractor.onFamilyAddComponentVisible(TypeAlbumFragment);
                } else fabCreateEvent.setVisibility(View.VISIBLE);
            }
            /*
             * megha(02/11/2021)
             * for my profile and events albums  && documents in events*/
        } else if ( type==1) {
            if (listGroupFoldersResponse.getData().size() == 0) {
                if ((this.query.length() > 0)) {
                    constraintEmpty.setVisibility(View.INVISIBLE);
                    emptyResultText.setVisibility(View.VISIBLE);
//                  searchAlbumQuery.setVisibility(View.VISIBLE);
//                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    if (type == TYPE_USER && !isAdmin) {
                        constraintEmpty.setVisibility(View.INVISIBLE);
                        emptyResultText.setVisibility(View.VISIBLE);
                        searchAlbumQuery.setVisibility(View.INVISIBLE);
                        clearSearch.setVisibility(View.INVISIBLE);
                    } else {
                        searchAlbumQuery.setVisibility(View.GONE);
                        clearSearch.setVisibility(View.INVISIBLE);
                        emptyResultText.setVisibility(View.INVISIBLE);
                        constraintEmpty.setVisibility(View.VISIBLE);
                    }
                }
                if (type == TYPE_FAMILY) {
                    if (familyDashboardInteractor != null)
                        familyDashboardInteractor.onFamilyAddComponentHidden(TypeAlbumFragment);
                    /** 05/09/2021 **/
                    if (parent.equals("fromDashboard"))
                        requireActivity().findViewById(R.id.addComponent).setVisibility(View.INVISIBLE);
                }
                /** 06-09-2021**/
                // fabCreateEvent.setVisibility(View.INVISIBLE);
                if (parent.equals("fromDashboard"))
                    requireActivity().findViewById(R.id.addComponent).setVisibility(View.INVISIBLE);
                else
                    fabCreateEvent.setVisibility(View.INVISIBLE);
            } else {
//                initializeToolbar();
                newtest.setVisibility(View.VISIBLE);
                newtest.setText("Gallery");
                emptyResultText.setVisibility(View.INVISIBLE);
                constraintEmpty.setVisibility(View.INVISIBLE);
//            searchAlbumQuery.setVisibility(View.VISIBLE);
//                if (searchAlbumQuery.getText().toString().trim().length() > 0)
//                    clearSearch.setVisibility(View.VISIBLE);
//                else clearSearch.setVisibility(View.INVISIBLE);
                if (type == TYPE_FAMILY) {
                    if (familyDashboardInteractor != null)
                        familyDashboardInteractor.onFamilyAddComponentVisible(TypeAlbumFragment);
                    /** 05/09/2021 **/
                    if (parent.equals("fromDashboard"))
                        requireActivity().findViewById(R.id.addComponent).setVisibility(View.VISIBLE);
                } else
                /** 06-09-2021**/
                    // fabCreateEvent.setVisibility(View.VISIBLE);
                    if (parent.equals("fromDashboard"))
                        requireActivity().findViewById(R.id.addComponent).setVisibility(View.VISIBLE);
                    else
                        fabCreateEvent.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        progressMyEvents.setVisibility(View.GONE);
        constraintEmpty.setVisibility(View.VISIBLE);
    }

    public void updateAdminStatus(boolean isAdmin, boolean canCreate, boolean canUpdate, String parentId) {
        this.isAdmin = isAdmin;
        this.canCreate = canCreate;
        this.canUpdate = canUpdate;
        this.parentId = parentId;
        lisAlbumsforEventsAdapter.updateAdminStatus(isAdmin, canCreate, canUpdate, parentId);
    }

    @Override
    public void onFolderSelected(ListEventAlbumsResponse.Datum folder, int position) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, folder);
        args.putBoolean(IS_UPDATE_MODE, true);
        args.putBoolean(IS_ADMIN, isAdmin);
        args.putBoolean(CAN_CREATE, canCreate);
        args.putBoolean(CAN_UPDATE, folder.canUpdate(SharedPref.getUserRegistration().getId()));
        args.putInt(TYPE, type);
        args.putString("FROM", "NORMAL");
        Intent intent = new Intent(getContext(), CreateAlbumDetailedActivity.class);
        intent.putExtras(args);
        requireContext().startActivity(intent);
    }

    @Override
    public void onFolderSelectedForDeletion(List<Long> selectedElementIds) {
        this.selectedElementIds.clear();
        this.selectedElementIds.addAll(selectedElementIds);
        deleteAlbumElements.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFolderDeselected() {
        deleteAlbumElements.setVisibility(View.GONE);
        selectedElementIds.clear();
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    private void deleteAlbumElements() {
        showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (Long documentId : selectedElementIds) {
            jsonArray.add(documentId);
        }
        jsonObject.add("folder_id", jsonArray);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.removeFolder(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgressDialog();
                for (Iterator<ListEventAlbumsResponse.Datum> it = listGroupFoldersResponse.getData().iterator(); it.hasNext(); ) {
                    if (selectedElementIds.contains(it.next().getId())) {
                        it.remove();
                    }
                }
                lisAlbumsforEventsAdapter.notifyDataSetChanged();
                deleteAlbumElements.setVisibility(View.GONE);
                selectedElementIds.clear();
                getAllAlbums();
            }

            @Override
            public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
                showErrorDialog("Please try again later");
            }
        });
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(getActivity());
        progressDialog.show();

    }

    @Override
    public void showErrorDialog(String errorMessage) {
        if (progressDialog == null) {
            progressDialog = Utilities.getErrorDialog(getActivity(), errorMessage);
            progressDialog.show();
            return;
        }
        Utilities.getErrorDialog(progressDialog, errorMessage);
    }

    @OnEditorAction(R.id.searchAlbumQuery)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            getAllAlbums();
            try {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchAlbumQuery.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        familyDashboardInteractor = Utilities.getListener(this, FamilyDashboardInteractor.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        familyDashboardInteractor = null;
        parent="";
    }

    /**method for inflating the searchview on click of search icon in toolbar*/
  /*  private void initListener() {
        imgSearch.setOnClickListener(view -> {
            showCircularReveal(constraintSearch);
            //profileImage.setEnabled(false);
            showKeyboard();

        });

        imageBack.setOnClickListener(view -> {
            hideCircularReveal(constraintSearch);
            searchAlbumQuery.setText("");
            //profileImage.setEnabled(true);
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);


        });
    }*/
   /* private void showKeyboard() {
        if (searchAlbumQuery.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchAlbumQuery, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }*/
    /**setting toolbar title*/
    private void initializeToolbar(){
        toolBarTitle.setVisibility(View.VISIBLE);
        toolBarTitle.setText("Gallery");
       /* FamilyDashboardActivity activity = ((FamilyDashboardActivity) getActivity());
        activity.setupActionbar(getResources().getString(R.string.fragment_dashboard_menu_gallery),toolbar);*/
    }

    public void startSearch(String query){
        this.query = query;
        getAllAlbums();
    }

}