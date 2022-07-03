package com.familheey.app.Fragments.Events;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.familheey.app.Adapters.ChooseDateAdapter;
import com.familheey.app.Adapters.agenda.AgendaParentAdapter;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Models.Response.GetEventByIdResponse;
import com.familheey.app.Models.Response.ListAgendaResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.PathUtil;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiFlags.CREATE_AGENDA;
import static com.familheey.app.Utilities.Constants.ApiFlags.DELETE_AGENDA;
import static com.familheey.app.Utilities.Constants.ApiFlags.LIST_AGENDA;
import static com.familheey.app.Utilities.Constants.ApiFlags.UPDATE_AGENDA;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.SOMETHING_WRONG;

public class AgendaFragment extends Fragment implements RetrofitListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.add)
    CardView add;
    EditText startTime, slotTittle, EndTime, Description;
    ChooseDateAdapter chooseDateAdapter;
    ImageView imageViewClose;
    DateModal selectedDate;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar;
    @BindView(R.id.empty_container)
    ConstraintLayout emptyContainer;
    @BindView(R.id.add_agenda)
    MaterialButton addAgenda;
    GetEventByIdResponse.Data data;
    JsonObject jsonObject;
    private final ArrayList<ListAgendaResponse.Datum> datumArrayList = new ArrayList<>();
    private ListAgendaResponse listAgendaResponse;
    int PICK_IMAGE = 10;
    boolean isPhotoAttached = false;
    boolean isPhotoRemovedWhileUpdate = false;

    private EventDetail eventDetail;
    private File fileAttached;
    private ImageView preview;
    private ProgressListener mListener;
    private BottomSheetDialog agendaDialog;
    private AgendaParentAdapter mAdapter;

    DateClicked dateClicked = new DateClicked() {
        @Override
        public void itemClicked(DateModal dateModal, int position) {
            selectedDate = dateModal;
        }
    };

    AgendaEdit agendaEdit = new AgendaEdit() {
        @Override
        public void agendaEditclicked(ListAgendaResponse.Datum datum, boolean b) {
            if (b) {
                openBottomSheet(datum);
            } else {
                SweetAlertDialog sweetAlertDialog = Utilities.deleteConfirmCancel(getActivity());
                sweetAlertDialog.show();
                sweetAlertDialog.setConfirmClickListener(sDialog -> {
                    sDialog.dismiss();
                    mListener.showProgressDialog();
                    ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("agenda_id", datum.getId().toString());
                    jsonObject.addProperty("event_id", datum.getEventId().toString());
                    jsonObject.addProperty("is_active", false);
                    ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
                    //apiCallbackParams.setDialog(sDialog);
                    apiCallbackParams.setAgenda(datum);//To identify Deletion is done in callback!!
                    apiServiceProvider.deleteAgenda(jsonObject, apiCallbackParams, AgendaFragment.this);
                });
                sweetAlertDialog.setCancelClickListener(SweetAlertDialog::cancel);
            }
        }
    };

    public AgendaFragment() {
        // Required empty public constructor
    }

    public static AgendaFragment newInstance(EventDetail eventDetail) {
        AgendaFragment fragment = new AgendaFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.Bundle.DATA, eventDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecycler();
        initListeners();
        getAllAgendas();
    }

    private void initListeners() {
        /*addAgenda.setOnClickListener(view -> {
            if (eventDetail.isAdminViewing())
                openBottomSheet(null);
            else
                Toast.makeText(getContext(), "Only admins of this group can create agendas", Toast.LENGTH_SHORT).show();
        });*/


    }

    private void getAllAgendas() {
        progressBar.setVisibility(View.VISIBLE);
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        JsonObject listQuery = new JsonObject();
        listQuery.addProperty("event_id", eventDetail.getEventId());
        apiServiceProvider.listAgenda(listQuery, null, this);
    }

    private void initRecycler() {
        mAdapter = new AgendaParentAdapter(getActivity(), datumArrayList, agendaEdit);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    @OnClick({R.id.add, R.id.add_agenda})
    public void onViewClicked() {
        if (eventDetail.isPastEvent()) {
            Toast.makeText(getContext(), "This is a past event ! You cannot add agenda", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eventDetail.isAdminViewing())
            openBottomSheet(null);
        else
            Toast.makeText(getContext(), "Only admins of this group can create agendas", Toast.LENGTH_SHORT).show();
    }

    public void openBottomSheet(ListAgendaResponse.Datum datum) {
        isPhotoRemovedWhileUpdate = false;
        isPhotoAttached = false;
        fileAttached = null;
        View dialogView = getLayoutInflater().inflate(R.layout.activity_create_agenda, null);
        agendaDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
        agendaDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        agendaDialog.setContentView(dialogView);
        agendaDialog.show();
        ArrayList<DateModal> dateModals = printDates(data.getEvent().get(0).getFromDate().toString(), data.getEvent().get(0).getToDate().toString());
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        startTime = dialogView.findViewById(R.id.start_time);
        EndTime = dialogView.findViewById(R.id.end_time);
        imageViewClose = dialogView.findViewById(R.id.imageViewClose);
        slotTittle = dialogView.findViewById(R.id.slot_tittle);
        Description = dialogView.findViewById(R.id.description);
        Button done = dialogView.findViewById(R.id.done);
        Button upload = dialogView.findViewById(R.id.image_upload);
        preview = dialogView.findViewById(R.id.preview);

        startTime.setOnClickListener(view -> showTimePicker(0));
        EndTime.setOnClickListener(view -> showTimePicker(1));
        imageViewClose.setOnClickListener(view -> {
            isPhotoAttached = false;
            preview.setImageDrawable(null);
            imageViewClose.setVisibility(View.INVISIBLE);
            isPhotoRemovedWhileUpdate = datum != null;
        });

        if (datum != null) {

            String eventDate = datum.getEventDate();
            String starttIme = formatDate(datum.getEventStartTime());
            String endTime = formatDate(datum.getEventEndTime());
            String tittle = datum.getTitle();
            String description = datum.getDescription();
            starttIme = starttIme.split(":")[0] + ":" + starttIme.split(":")[1];
            endTime = endTime.split(":")[0] + ":" + endTime.split(":")[1];
            Calendar cal = Calendar.getInstance();
            cal.clear();
            String[] dateArray = eventDate.split("-");
            cal.set(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]) - 1, Integer.parseInt(dateArray[2]));
            String MON = new SimpleDateFormat("MMM", Locale.getDefault()).format(cal.getTime());
            dateModals.clear();
            DateModal dateModal = new DateModal(cal.get(Calendar.DAY_OF_MONTH) + "", getWeek(cal.get(Calendar.DAY_OF_WEEK)), MON, cal.get(Calendar.YEAR) + "");
            dateModal.setSelected(true);
            dateModals.add(dateModal);

            selectedDate = dateModal;
            startTime.setText(starttIme);
            EndTime.setText(endTime);
            slotTittle.setText(tittle);
            Description.setText(description);
            if (datum.getAgendaPic() != null && datum.getAgendaPic().length() > 0) {
                preview.setVisibility(View.VISIBLE);
                imageViewClose.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(IMAGE_BASE_URL + Constants.Paths.AGENDA_PIC + datum.getAgendaPic())
                        .placeholder(R.drawable.default_event_image)
                        .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(preview);
            } else {
                preview.setVisibility(View.INVISIBLE);
                imageViewClose.setVisibility(View.INVISIBLE);
            }
            done.setText(getActivity().getString(R.string.update));
        }
        chooseDateAdapter = new ChooseDateAdapter(dateModals, dateClicked);

        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);

        recyclerView.setAdapter(chooseDateAdapter);

        if (dateModals != null && dateModals.size() == 1)
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(0)).itemView.performClick();
                }
            });
        done.setOnClickListener(view -> {

            if (validate()) {
                if (isPhotoAttached) {
                    uploadFileFromRetrofit(fileAttached, "image/jpeg", "agenda_pic", datum);
                    return;
                }

                if (checktimings(startTime.getText().toString(), EndTime.getText().toString())) {

                    String startDate = selectedDate.getYear() + "-" + selectedDate.getMonth() + "-" + selectedDate.getDay() + " " + startTime.getText().toString().trim() + ":00";
                    String endDate = selectedDate.getYear() + "-" + selectedDate.getMonth() + "-" + selectedDate.getDay() + " " + EndTime.getText().toString().trim() + ":00";

                    JsonObject jsonObject = new JsonObject();
                    if (isPhotoRemovedWhileUpdate)
                        jsonObject.addProperty("agenda_pic", "");
                    jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
                    if (jsonObject.has("id"))
                        jsonObject.addProperty("event_id", String.valueOf(jsonObject.get("id")));
                    else
                        jsonObject.addProperty("event_id", eventDetail.getEventId());
                    jsonObject.addProperty("title", slotTittle.getText().toString());
                    jsonObject.addProperty("start_date", startDate);
                    jsonObject.addProperty("end_date", endDate);
                    jsonObject.addProperty("description", Description.getText().toString());

                    mListener.showProgressDialog();
                    ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());

                    if (datum != null) {
                        jsonObject.addProperty("agenda_id", datum.getId() + "");
                        apiServiceProvider.updateAgenda(jsonObject, null, this);
                    } else
                        apiServiceProvider.createAgenda(jsonObject, null, this);

                    datumArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(getActivity(), "Start time must be less than end time", Toast.LENGTH_SHORT).show();
            }
        });

        upload.setOnClickListener(view -> {
            if (isReadPermissionGranted()) goToImageGalleryintent();
            else showPermission();
        });

    }

    private String formatDate(String time) {
        DateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat outputFormat = new SimpleDateFormat("KK:mm a");
        try {

            return outputFormat.format(inputFormat.parse(time));


        } catch (ParseException e) {
            String[] abc = time.split(":");
            if (abc.length == 3) {

                return abc[0] + ":" + abc[1];
            }

            return time;
        }
    }

    @SuppressLint("IntentReset")
    private void goToImageGalleryintent() {
        @SuppressLint("IntentReset")
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {

                try {
                    Uri selectedImage = data.getData();
                    String filePath = PathUtil.getPath(getActivity(), selectedImage);
                    RequestOptions ro = new RequestOptions()
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(256, 140)
                            .fitCenter();
                    assert filePath != null;
                    fileAttached = new File(filePath);
                    Glide.with(getActivity())
                            .load(fileAttached)
                            .apply(ro)// Uri of the picture
                            .into(preview);
                    isPhotoAttached = true;
                    isPhotoRemovedWhileUpdate = false;
                    preview.setVisibility(View.VISIBLE);
                    imageViewClose.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadFileFromRetrofit(File file, String type, String format, ListAgendaResponse.Datum datum) {
        mListener.showProgressDialog();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
        RequestBody requestFile = RequestBody.create(MediaType.parse(type), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData(format, file.getName(), requestFile);
        RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), SharedPref.getUserRegistration().getId());
        RequestBody event_id = RequestBody.create(MediaType.parse("multipart/form-data"), eventDetail.getEventId());
        RequestBody title = RequestBody.create(MediaType.parse("multipart/form-data"), slotTittle.getText().toString());
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), Description.getText().toString());
        String startDate = selectedDate.getYear() + "-" + selectedDate.getMonth() + "-" + selectedDate.getDay() + " " +
                startTime.getText().toString().trim() + ":00";
        String endDate = selectedDate.getYear() + "-" + selectedDate.getMonth() + "-" + selectedDate.getDay() + " " +
                EndTime.getText().toString().trim() + ":00";

        RequestBody start_date = RequestBody.create(MediaType.parse("multipart/form-data"), startDate);
        RequestBody end_date = RequestBody.create(MediaType.parse("multipart/form-data"), endDate);
        ApiCallbackParams apiCallbackParams = new ApiCallbackParams();
        apiCallbackParams.setAgenda(datum);
        if (datum != null) {
            RequestBody agenda_id = RequestBody.create(MediaType.parse("multipart/form-data"), datum.getId() + "");
            apiServiceProvider.updateAgendaMultipart(body, user_id, event_id, title, description, start_date, end_date, agenda_id, apiCallbackParams, this);
        } else {
            apiServiceProvider.createAgendaMultipart(body, user_id, event_id, title, description, start_date, end_date, apiCallbackParams, this);
        }
    }

    private boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else {
            return true;
        }
    }


    private boolean isReadPermissionGranted() {
        return TedPermission.isGranted(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private boolean checktimings(String time, String endtime) {

        String pattern = "hh:mm aa";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            assert date1 != null;
            return date1.before(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean validate() {
        if (selectedDate == null) {
            Toast.makeText(getActivity(), "Date empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (startTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Start time empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (EndTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "End time empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (slotTittle.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Title empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showTimePicker(int position) {
        final Calendar currentDate = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        boolean isPM = (hourOfDay >= 12);

                        String time = String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM");


                        if (position == 0) {
                            startTime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                        } else {
                            if (!startTime.getText().toString().trim().isEmpty()) {
                                if (checktimings(startTime.getText().toString(), time))
                                    EndTime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                                else
                                    Toast.makeText(getActivity(), "Start time must be less than end time", Toast.LENGTH_SHORT).show();
                            } else
                                EndTime.setText(time);
                        }


                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);

        timePickerDialog.show();


    }

    public ArrayList<DateModal> printDates(String from, String to) {

        ArrayList<DateModal> dateModalArrayList = new ArrayList<>();


        long value = TimeUnit.SECONDS.toMillis(Long.parseLong(from));
        long value1 = TimeUnit.SECONDS.toMillis(Long.parseLong(to));

        Date date1 = new Date(value);
        Date date2 = new Date(value1);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        while (!cal1.after(cal2)) {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());

            String[] datime = fmt.format(cal1.getTime()).split("/");

            dateModalArrayList.add(new DateModal(cal1.get(Calendar.DAY_OF_MONTH) + "", getWeek(cal1.get(Calendar.DAY_OF_WEEK)), datime[1], cal1.get(Calendar.YEAR) + ""));

            cal1.add(Calendar.DATE, 1);
        }

        return dateModalArrayList;
    }

    String getWeek(int number) {
        switch (number) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "";
        }
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        switch (apiFlag) {
            case UPDATE_AGENDA:
                mListener.hideProgressDialog();
                SweetAlertDialog sweetAlertDialog = Utilities.succesDialog(getActivity(), "Agenda updated successfully");
                sweetAlertDialog.show();
                Utilities.addPositiveButtonMargin(sweetAlertDialog);
                sweetAlertDialog.setOnDismissListener(dialogInterface -> {
                    datumArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                    if (agendaDialog != null && agendaDialog.isShowing()) {
                        agendaDialog.dismiss();
                    }
                    fileAttached = null;
                    isPhotoAttached = false;
                    getAllAgendas();
                });
                break;
            case DELETE_AGENDA:
                mListener.hideProgressDialog();
                SweetAlertDialog removeDialog = Utilities.succesDialog(getActivity(), "Agenda deleted successfully");
                removeDialog.show();
                Utilities.addPositiveButtonMargin(removeDialog);
                removeDialog.setOnDismissListener(dialogInterface -> {
                    //datumArrayList.remove(apiCallbackParams.getAgenda());
                    datumArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                    getAllAgendas();
                });
                break;
            case LIST_AGENDA:
                progressBar.setVisibility(View.GONE);
                listAgendaResponse = new Gson().fromJson(responseBodyString, ListAgendaResponse.class);
                datumArrayList.clear();
                datumArrayList.addAll(listAgendaResponse.getData());
                Collections.reverse(datumArrayList);
                if (listAgendaResponse.getData().size() == 0) {
                    add.setVisibility(View.INVISIBLE);
                    emptyContainer.setVisibility(View.VISIBLE);
                } else {
                    emptyContainer.setVisibility(View.INVISIBLE);
                    add.setVisibility(View.VISIBLE);
                }
                mAdapter.notifyDataSetChanged();
                break;
            case CREATE_AGENDA:
                mListener.hideProgressDialog();
                SweetAlertDialog createdSuccesfullyDialog = Utilities.succesDialog(getActivity(), "Agenda created successfully");
                createdSuccesfullyDialog.show();
                Utilities.addPositiveButtonMargin(createdSuccesfullyDialog);
                createdSuccesfullyDialog.setOnDismissListener(dialogInterface -> {
                    datumArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                    if (agendaDialog != null && agendaDialog.isShowing()) {
                        agendaDialog.dismiss();
                    }
                    isPhotoAttached = false;
                    fileAttached = null;
                    getAllAgendas();
                });
                break;
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        switch (apiFlag) {
            case UPDATE_AGENDA:
            case CREATE_AGENDA:
                mListener.hideProgressDialog();
                break;
            case LIST_AGENDA:
                progressBar.setVisibility(View.GONE);
                break;
        }
        Toast.makeText(getContext(), SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
    }

    public void updateEvent(GetEventByIdResponse.Data data) {
        this.data = data;
        if(data.getEvent().get(0).getIsRecurrence()==1)
        {
            int arr=data.getEvent().size()-1;
            this.eventDetail = data.getEvent().get(arr);
        }
       else{
            this.eventDetail = data.getEvent().get(0);
        }

        if (eventDetail.isPastEvent())
            mAdapter.setIsAdmin(false);
        else mAdapter.setIsAdmin(eventDetail.isAdminViewing());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventDetail = getArguments().getParcelable(Constants.Bundle.DATA);
        }
    }

    public interface DateClicked {
        void itemClicked(DateModal dateModal, int position);
    }

    public interface AgendaEdit {
        void agendaEditclicked(ListAgendaResponse.Datum datum, boolean b);
    }

    public class DateModal {

        String day;
        String week;
        String month;
        String year;
        boolean isSelected = false;

        public DateModal(String day, String week, String month, String year) {
            this.day = day;
            this.week = week;
            this.month = month;
            this.year = year;
        }

        public String getYear() {
            return year;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public String getDay() {
            return day;
        }

        public String getWeek() {
            return week;
        }

        public String getMonth() {
            return month;
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = Utilities.getListener(this, ProgressListener.class);
        if (mListener == null)
            throw new RuntimeException("Agenda Fragment's Activity should implement Progress Listener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void showPermission() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogue_permission);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.btn_continue).setOnClickListener(view -> {
            dialog.dismiss();
            isReadStoragePermissionGranted();
        });
        dialog.show();
    }
}
