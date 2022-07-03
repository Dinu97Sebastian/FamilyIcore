package com.familheey.app.Fragments;


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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.CreateAlbumBasicActivity;
import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.SubFolderActivity;
import com.familheey.app.Adapters.FoldersAdapter;
import com.familheey.app.CustomViews.TextViews.SemiBoldTextView;
import com.familheey.app.Fragments.FamilyDashboard.BackableFragment;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.ListGroupFoldersResponse;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.ApiFlags.LIST_EVENT_ALBUMS;
import static com.familheey.app.Utilities.Constants.ApiFlags.LIST_GROUP_FOLDERS;
import static com.familheey.app.Utilities.Constants.ApiFlags.LIST_USER_FOLDERS;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_CREATE;
import static com.familheey.app.Utilities.Constants.Bundle.CAN_UPDATE;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ADMIN;
import static com.familheey.app.Utilities.Constants.Bundle.IS_ALBUM;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeFoldersFragment;
import static com.familheey.app.Utilities.Constants.FileUpload.TYPE_FAMILY;

public class FoldersFragment extends BackableFragment implements FoldersAdapter.FolderAdapterInterface, RetrofitListener, ProgressListener {

    @BindView(R.id.recycler_folder)
    RecyclerView recycler_folder;

    @BindView(R.id.floatingAddFolder)
    CardView floatingAddFolder;
    @BindView(R.id.emptyIndicator)
    View emptyIndicator;
    @BindView(R.id.createFolder)
    MaterialButton createFolder;
    @BindView(R.id.deleteAlbumElements)
    MaterialButton deleteAlbumElements;
    @BindView(R.id.clearSearchEvent)
    ImageView clearSearch;
    @BindView(R.id.searchFolderQuery)
    EditText searchFolderQuery;
    @BindView(R.id.emptyResultText)
    SemiBoldTextView emptyResultText;
    private String id = "";
    private int type;
    @BindView(R.id.progressFolders)
    ProgressBar progressBar;
    @BindView(R.id.familyToolBarTitlenew)
    TextView title;
    private SweetAlertDialog progressDialog;
    private final List<ListGroupFoldersResponse.Row> folders = new ArrayList<>();

    String parent ="";
    /*
        private OnFolderSelectedListener mListener;
        private ProgressListener progressListener;*/
    private FamilyDashboardInteractor familyDashboardInteractor;
    private boolean isAdmin=false;
    private final List<Integer> selectedElementIds = new ArrayList<>();
    private FoldersAdapter adapter;
    private String parentId;
    private boolean canUpdate = true;
    private boolean canCreate = true;
    // search string
    String query = "";
    public FoldersFragment() {
        // Required empty public constructor
    }
    /* added isAdmin for checking the "admin only" condition in family settings*/
    public static FoldersFragment newInstance(String id, int type,boolean isAdmin,boolean canCreatePost) {
        FoldersFragment fragment = new FoldersFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Bundle.ID, id);
        args.putInt(Constants.Bundle.TYPE, type);
        args.putBoolean("ADMIN", isAdmin);
        args.putString("parentPage", "");
        args.putBoolean("canCreate", canCreatePost);
        fragment.setArguments(args);
        return fragment;
    }

    /* added isAdmin for checking the "admin only" condition in family settings*/
    public static FoldersFragment newInstance(String id, int type,String parent,boolean isAdmin) {
        FoldersFragment fragment = new FoldersFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Bundle.ID, id);
        args.putInt(Constants.Bundle.TYPE, type);
        args.putString("parentPage",parent);
        args.putBoolean("ADMIN", isAdmin);
        fragment.setArguments(args);
        return fragment;
    }

    public static FoldersFragment newInstance(String id, int type, String title, String description) {
        FoldersFragment fragment = new FoldersFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Bundle.ID, id);
        args.putInt(Constants.Bundle.TYPE, type);
        args.putString(Constants.Bundle.TITLE, title);
        args.putString(Constants.Bundle.DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        ButterKnife.bind(this, view);
        //initializeToolbar();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getString(Constants.Bundle.ID) != null) {
                id = getArguments().getString(Constants.Bundle.ID);
                type = getArguments().getInt(Constants.Bundle.TYPE);
                parent=getArguments().getString("parentPage");
                isAdmin=getArguments().getBoolean("ADMIN");
                canCreate=getArguments().getBoolean("canCreate");
            }
            getArguments().clear();
        }
        initListener();
        initRecyclerView();
    }

    private void initListener() {
        /*
        29/08
        floatingAddFolder.setOnClickListener(v -> createNewFolder());

       */
        floatingAddFolder.setOnClickListener(v -> createNewFolder());
        createFolder.setOnClickListener(v -> createNewFolder());
        deleteAlbumElements.setOnClickListener(v -> deleteAlbumElements());
        searchFolderQuery.addTextChangedListener(new TextWatcher() {

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
            searchFolderQuery.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });
    }
    public void createNewFolder() {
        /* modified the if condition to isAdmin*/
        if (isAdmin || canCreate) {
            Intent intent = new Intent(getActivity(), CreateAlbumBasicActivity.class);
            intent.putExtra(Constants.Bundle.DATA, String.valueOf(id));
            intent.putExtra(TYPE, type);
            intent.putExtra(IS_ADMIN, isAdmin);
            intent.putExtra(IS_ALBUM, false);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "You don't have privilege to create Folder", Toast.LENGTH_SHORT).show();
        }
    }
    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void fetchFolderApi() {
        showProgressBar();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();
        if (type == 0){/*for documents in events (megha/02/11/2021)*/
            jsonObject.addProperty("txt", searchFolderQuery.getText().toString().trim());

        }else if (type ==1) {/* for documents in grid (megha/02/11/2021)*/
            jsonObject.addProperty("txt", this.query.trim());
        }
        switch (type) {
            case Constants.FileUpload.TYPE_EVENTS:
                jsonObject.addProperty("event_id", id);
                jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                jsonObject.addProperty("folder_type", "documents");
                jsonObject.addProperty("folder_for", "events");
                apiServiceProvider.listEventFolders(jsonObject, null, this);
                break;
            case TYPE_FAMILY:
                jsonObject.addProperty("group_id", id);
                jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                jsonObject.addProperty("folder_type", "documents");
                jsonObject.addProperty("folder_for", "groups");
                apiServiceProvider.listGroupFolders(jsonObject, null, this);
                break;
            case Constants.FileUpload.TYPE_USER:
                jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                jsonObject.addProperty("folder_type", "documents");
                jsonObject.addProperty("folder_for", "users");
                apiServiceProvider.listUserFolders(jsonObject, null, this);
                break;
        }
    }

    private void initRecyclerView() {
        adapter = new FoldersAdapter(folders, FoldersFragment.this, id);
        recycler_folder.setHasFixedSize(true);
        recycler_folder.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_folder.setAdapter(adapter);
    }

    @Override
    public void onFolderClick(String id, String folderId, String title, String description, boolean isAdmin, boolean canCreate, boolean canUpdate) {
        Intent intent = new Intent(getContext(), SubFolderActivity.class);
        intent.putExtra(Constants.Bundle.ID, id);
        intent.putExtra(Constants.Bundle.FOLDER_ID, folderId);
        intent.putExtra(Constants.Bundle.TYPE, type);
        intent.putExtra(Constants.Bundle.TITLE, title);
        intent.putExtra(Constants.Bundle.DESCRIPTION, description);
        intent.putExtra(IS_ADMIN, isAdmin);
        intent.putExtra(CAN_CREATE, canCreate);
        intent.putExtra(CAN_UPDATE, canUpdate);
        startActivity(intent);
    }

    @Override
    public void onFolderSelectedForDeletion(List<Integer> selectedElementIds) {
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

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        switch (apiFlag) {
            case LIST_USER_FOLDERS:
            case LIST_GROUP_FOLDERS:
            case LIST_EVENT_ALBUMS:
                try {
                    ListGroupFoldersResponse listGroupFoldersResponse = new Gson().fromJson(responseBodyString, ListGroupFoldersResponse.class);
                    folders.clear();
                    folders.addAll(listGroupFoldersResponse.getRows());
                    adapter.notifyDataSetChanged();
                    adapter.setAdminStatus(isAdmin, canCreate, canUpdate, parentId);
                    /*megha(02/11/2021)
                     * for documents in events tab
                     * */
                    if (type==0){
                        if (listGroupFoldersResponse.getRows() != null && listGroupFoldersResponse.getRows().size() > 0) {
                            searchFolderQuery.setVisibility(View.VISIBLE);
                            clearSearch.setVisibility(View.VISIBLE);
                            emptyIndicator.setVisibility(View.INVISIBLE);
                            emptyResultText.setVisibility(View.INVISIBLE);
                            recycler_folder.setVisibility(View.VISIBLE);
                            if (type == TYPE_FAMILY) {
                                if (familyDashboardInteractor != null)
                                    familyDashboardInteractor.onFamilyAddComponentVisible(TypeFoldersFragment);
                            } else
                                floatingAddFolder.setVisibility(View.VISIBLE);
                        } else {
                            if (searchFolderQuery.getText().toString().trim().length() > 0) {
                                searchFolderQuery.setVisibility(View.VISIBLE);
                                clearSearch.setVisibility(View.VISIBLE);
                                emptyIndicator.setVisibility(View.INVISIBLE);
                                emptyResultText.setVisibility(View.VISIBLE);
                            }else {
                                searchFolderQuery.setVisibility(View.INVISIBLE);
                                clearSearch.setVisibility(View.INVISIBLE);
                                emptyIndicator.setVisibility(View.VISIBLE);
                                emptyResultText.setVisibility(View.INVISIBLE);
                            }
                            recycler_folder.setVisibility(View.INVISIBLE);
                            if (type == TYPE_FAMILY) {
                                if (familyDashboardInteractor != null)
                                    familyDashboardInteractor.onFamilyAddComponentHidden(TypeFoldersFragment);
                            } else
                                floatingAddFolder.setVisibility(View.INVISIBLE);
                        }
                        /*
                         * megha(02/11/2021)
                         * for docuemnts in grid*/
                    }else if (type==1){
                        if (listGroupFoldersResponse.getRows() != null && listGroupFoldersResponse.getRows().size() > 0) {
//                            initializeToolbar();
                            title.setVisibility(View.VISIBLE);
                            title.setText("Documents");
                            //searchFolderQuery.setVisibility(View.VISIBLE);
//                            clearSearch.setVisibility(View.VISIBLE);
                            emptyIndicator.setVisibility(View.INVISIBLE);
                            emptyResultText.setVisibility(View.INVISIBLE);
                            recycler_folder.setVisibility(View.VISIBLE);
                            if (type == TYPE_FAMILY) {
                            /*if (familyDashboardInteractor != null)
                                familyDashboardInteractor.onFamilyAddComponentVisible(TypeFoldersFragment);*/
                                /** 05/09/2021 **/
                                requireActivity().findViewById(R.id.addComponent).setVisibility(View.VISIBLE);
                            } else
                                floatingAddFolder.setVisibility(View.VISIBLE);
                        } else {
                            if (this.query.trim().length() > 0) {
                                //searchFolderQuery.setVisibility(View.VISIBLE);
//                                clearSearch.setVisibility(View.VISIBLE);
                                emptyIndicator.setVisibility(View.INVISIBLE);
                                emptyResultText.setVisibility(View.VISIBLE);
                            }else {
                                //searchFolderQuery.setVisibility(View.INVISIBLE);
                                clearSearch.setVisibility(View.INVISIBLE);
                                emptyIndicator.setVisibility(View.VISIBLE);
                                emptyResultText.setVisibility(View.INVISIBLE);
                            }
                            recycler_folder.setVisibility(View.INVISIBLE);
                            if (type == TYPE_FAMILY) {
                            /*if (familyDashboardInteractor != null)
                                familyDashboardInteractor.onFamilyAddComponentHidden(TypeFoldersFragment);*/
                                /** 05/09/2021 **/
                                requireActivity().findViewById(R.id.addComponent).setVisibility(View.GONE);
                            } else
                                floatingAddFolder.setVisibility(View.INVISIBLE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), Constants.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
                }


                break;
        }
        hideProgressBar();
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        hideProgressBar();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getActivity() instanceof CreatedEventDetailActivity) {
            //mListener = (CreatedEventDetailActivity) context;
        } else {/*
            mListener = Utilities.getListener(this, OnFolderSelectedListener.class);
            progressListener = Utilities.getListener(this, ProgressListener.class);*/
            familyDashboardInteractor = Utilities.getListener(this, FamilyDashboardInteractor.class);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();/*
        mListener = null;
        progressListener = null;*/
        familyDashboardInteractor = null;
    }

    public void updateAdminStatus(boolean isAdmin, boolean canCreate, boolean canUpdate, String parentId) {
        this.isAdmin = isAdmin;
        this.canCreate = canCreate;
        this.canUpdate = canUpdate;
        this.parentId = parentId;
        if (adapter != null) adapter.setAdminStatus(isAdmin, canCreate, canUpdate, parentId);

    }
    /** Defined by Devika on 24/08/2021 **/
    @Override
    public void onBackButtonPressed() {
        if(parent.equals("fromEventDetails")){
            requireActivity().onBackPressed();
        }else{
            ((FamilyDashboardActivity)requireActivity()).backAction();
        }
        //((FamilyDashboardActivity)requireActivity()).gotoDashboard();
        //((FamilyDashboardActivity)requireActivity()).backAction();
    }


    public interface OnFolderSelectedListener {
        void loadSubFolder(String id, String folderId, int type, String title, String description, boolean isAdmin, boolean canCreate, boolean canUpdate);
    }
    @Override
    public void onResume() {
        super.onResume();
        fetchFolderApi();
        if (type == TYPE_FAMILY) {
            if (folders != null && folders.size() > 0) {
               /* if (familyDashboardInteractor != null)
                    familyDashboardInteractor.onFamilyAddComponentVisible(TypeFoldersFragment);*/
                /** 05/09/2021 **/
                requireActivity().findViewById(R.id.addComponent).setVisibility(View.VISIBLE);
            } else {
                /*if (familyDashboardInteractor != null)
                    familyDashboardInteractor.onFamilyAddComponentHidden(TypeFoldersFragment);*/
                /** 05/09/2021 **/
                requireActivity().findViewById(R.id.addComponent).setVisibility(View.GONE);
            }
        }
    }
    private void deleteAlbumElements() {
        showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (Integer documentId : selectedElementIds) {
            jsonArray.add(documentId);
        }
        jsonObject.add("folder_id", jsonArray);
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.removeFolder(jsonObject, null, new RetrofitListener() {
            @Override
            public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
                hideProgressDialog();
                for (Iterator<ListGroupFoldersResponse.Row> it = folders.iterator(); it.hasNext(); ) {
                    if (selectedElementIds.contains(it.next().getId())) {
                        it.remove();
                    }
                }
                adapter.notifyDataSetChanged();
                deleteAlbumElements.setVisibility(View.GONE);
                selectedElementIds.clear();
                fetchFolderApi();
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

    @OnEditorAction(R.id.searchFolderQuery)
    protected boolean onSearchQueryListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            fetchFolderApi();
            try {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(searchFolderQuery.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**setting toolbar title*/
    private void initializeToolbar(){
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.fragment_dashboard_menu_documents));
    }
    /** This method is invokes from FamilyDashboardActivity for searching folders based on the input
     * entered by the user in the searchview
     * @param query : user input passed from method call
     */
    public void startSearch(String query) {
        this.query = query;
        fetchFolderApi();
    }
}
