package com.familheey.app.Need;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.AddressFetchingActivity;
import com.familheey.app.CustomViews.FSpinner;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Models.Response.SelectFamilys;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Post.SelectFamilesOrPeopleActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.ADDRESS;

public class EditRequestActivity extends AppCompatActivity implements CreateRequestAdapterListner, ProgressListener {
    public static final int REQUEST_EDIT_REQUEST_CODE = 101;
    public static final int REQUEST_EDIT_REQUEST_CODE2 = 102;
    public static final int REQUEST_CODE = 2001;

    public CompositeDisposable subscriptions;
    @BindView(R.id.btn_add)
    LinearLayout btn_add;
    @BindView(R.id.cart_item)
    RecyclerView cart_item;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.main_view_request)
    androidx.coordinatorlayout.widget.CoordinatorLayout main_view;
    @BindView(R.id.post_spinner)
    FSpinner postSpinner;
    @BindView(R.id.txt_count)
    TextView txt_count;
    @BindView(R.id.etxt_location)
    EditText etxt_location;
    @BindView(R.id.edtxDateTimeEnd)
    EditText edtxDateTimeEnd;
    @BindView(R.id.edtxDateTimeStart)
    EditText edtxDateTimeStart;

    @BindView(R.id.rb_general)
    RadioButton rb_general;

    @BindView(R.id.rb_funding)
    RadioButton rb_funding;
    @BindView(R.id.no_item_view)
    LinearLayout no_item_view;
    @BindView(R.id.add_item)
    LinearLayout add_item;

    @BindView(R.id.txt_bank_tittle)
    TextView txt_bank_tittle;
    @BindView(R.id.btn_add_bank)
    TextView btn_add_bank;
    @BindView(R.id.shimmerLoader)
    com.facebook.shimmer.ShimmerFrameLayout shimmer_view;

    @BindView(R.id.btn_submit_temp)
    com.google.android.material.button.MaterialButton btn_submit_temp;
    @BindView(R.id.btn_submit)
    com.google.android.material.button.MaterialButton btn_submit;

    @BindView(R.id.bank_account_view)
    LinearLayout bank_account_view;
    private SweetAlertDialog progressDialog;
    ArrayAdapter spinnerArrayAdapter;
    ArrayList<SelectFamilys> family;
    private Boolean isSpinnerSelection = true;
    private CreateNeedsAdapter createNeedsAdapter;
    private ArrayList<Item> items = new ArrayList<>();

    private Need need;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        ButterKnife.bind(this);
        Bundle b = getIntent().getExtras();
        subscriptions = new CompositeDisposable();
        if (b != null) {
            showShimmer();
            need = new Gson().fromJson(b.getString(Constants.Bundle.DATA), Need.class);
            getNeedRequestDetails();
        }
        init();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            family = new Gson().fromJson(Objects.requireNonNull(data.getExtras()).getString("DATA"), new TypeToken<ArrayList<SelectFamilys>>() {
            }.getType());
            assert family != null;
            if (family.size() == 1) {
                txt_count.setVisibility(View.VISIBLE);
                txt_count.setText("1 Family selected");
            } else if (family.size() > 1) {
                txt_count.setVisibility(View.VISIBLE);
                txt_count.setText(family.size() + " Family selected");
            } else {
                txt_count.setVisibility(View.GONE);
                postSpinner.setSelection(0);

            }
        } else if (requestCode == AddressFetchingActivity.RequestCode && resultCode == Activity.RESULT_OK) {
            etxt_location.setText(data.getStringExtra(ADDRESS));
        }
    }


    @OnClick({R.id.goBack, R.id.add_item, R.id.btn_add, R.id.btn_submit, R.id.etxt_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goBack:
                onBackPressed();
                break;
            case R.id.add_item:

            case R.id.btn_add:
                showDialogueForCreateNeed(null);
                break;
            case R.id.btn_submit:
                if (validate()) {
                    updateNeed();
                }
                break;
            case R.id.etxt_location:
            case R.id.venue:
                Intent livingInAddressintent = new Intent(this, AddressFetchingActivity.class);
                startActivityForResult(livingInAddressintent, AddressFetchingActivity.RequestCode);
                break;
        }
    }

    private void init() {
        edtxDateTimeStart.setOnClickListener(view -> date(edtxDateTimeStart));
        edtxDateTimeEnd.setOnClickListener(view -> date(edtxDateTimeEnd));

        String[] options = {"Select", "All My Families", "Selected Families"};
        String[] funding_options = {"Select", "Selected Families"};
        spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, options);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postSpinner.setAdapter(spinnerArrayAdapter);


        postSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 2:
                        if (isSpinnerSelection) {
                            txt_count.setText("");
                            if (family != null && family.size() > 0) {
                                startActivityForResult(new Intent(EditRequestActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "NEED").putExtra("type", "FAMILY").putExtra("refid", new Gson().toJson(family)), REQUEST_EDIT_REQUEST_CODE);
                            } else {
                                startActivityForResult(new Intent(EditRequestActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "NEED").putExtra("type", "FAMILY"), REQUEST_EDIT_REQUEST_CODE);
                            }
                        }
                        isSpinnerSelection = true;
                        break;
                    case 1:
                        if (rb_general.isChecked()) {
                            txt_count.setVisibility(View.GONE);
                            txt_count.setText("");
                        } else {
                            startActivityForResult(new Intent(EditRequestActivity.this, SelectMyfamilies.class), REQUEST_EDIT_REQUEST_CODE2);
                        }
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (postSpinner.getSelectedItemPosition() == 2) {
                    txt_count.setText("");
                    startActivityForResult(new Intent(EditRequestActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "POST").putExtra("type", "FAMILY"), REQUEST_EDIT_REQUEST_CODE);
                }
            }
        });


        /*rb_general.setOnClickListener(view -> {
            if (rb_general.isChecked()) {
                spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, options);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                postSpinner.setAdapter(spinnerArrayAdapter);
                family = null;
                txt_count.setVisibility(View.GONE);
                txt_count.setText("");
                txt_bank_tittle.setVisibility(View.GONE);
                btn_add_bank.setVisibility(View.GONE);
                items.clear();
                createNeedsAdapter.notifyDataSetChanged();

                btn_submit_temp.setVisibility(View.VISIBLE);
                btn_submit.setEnabled(false);
                bank_account_view.setVisibility(View.GONE);
            }
        });


        rb_funding.setOnClickListener(view -> {
            if (rb_funding.isChecked()) {
                spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, funding_options);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                postSpinner.setAdapter(spinnerArrayAdapter);
                family = null;
                txt_count.setVisibility(View.GONE);
                txt_count.setText("");
                items.clear();
                btn_submit_temp.setVisibility(View.VISIBLE);
                btn_submit.setEnabled(false);
                createNeedsAdapter.notifyDataSetChanged();
            }
        });*/

        setUpUi();
    }


    private void setUpUi() {

        family = new ArrayList<>();
        for (Group group : need.getTo_groups()) {
            SelectFamilys familys = new SelectFamilys();
            familys.setId(group.getId() + "");
            family.add(familys);
        }

        if (family.size() == 1) {
            txt_count.setText("1 Family selected");
        } else if (family.size() > 1) {
            txt_count.setText(family.size() + " Family selected");
        }

        if ("fund".equals(need.getRequest_type())) {
            rb_funding.setChecked(true);
            rb_general.setVisibility(View.GONE);
            postSpinner.setVisibility(View.GONE);
            String[] funding_options = {"Select", "Selected Families"};
            spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, funding_options);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            postSpinner.setAdapter(spinnerArrayAdapter);
            postSpinner.setSelection(1);
        } else {

            rb_funding.setVisibility(View.GONE);
            rb_general.setVisibility(View.VISIBLE);
            isSpinnerSelection = false;
            if (need.getGroup_type().equals("selected_family")) {
                postSpinner.setSelection(2);
            } else {
                postSpinner.setSelection(1);
            }
        }
        etxt_location.setText(need.getRequest_location());

        if (need.getStart_date() > 1) {
            edtxDateTimeStart.setText(getFormattedDate(need.getStart_date()));
        }

        if (need.getEnd_date() > 1) {
            edtxDateTimeEnd.setText(getFormattedDate(need.getEnd_date()));
        }


    }

    public String getFormattedDate(long date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMM yyyy, hh:mm aa");
        Long value = TimeUnit.SECONDS.toMillis(date);
        DateTime dateTime = new DateTime(value);
        return formatter.print(dateTime);
    }


    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    @Override
    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();

    }

    @Override
    public void showErrorDialog(String errorMessage) {

    }


    private void addToCart(int qty, String des, String tittle) {
        Item item = new Item();
        item.setItem_quantity(qty);
        item.setRequest_item_description(des);
        item.setRequest_item_title(tittle);

        itemNeedCreate(item);

    }


    private void showDialogueForCreateNeed(Item item) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_need);
        Window window = dialog.getWindow();
        assert window != null;
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        setAnimation(dialog.findViewById(R.id.full_View));
        EditText edtxrequest = dialog.findViewById(R.id.edtxrequest);
        EditText edtxdescription = dialog.findViewById(R.id.edtxdescription);
        EditText edtxqty = dialog.findViewById(R.id.edtxqty);
        TextView tebal2 = dialog.findViewById(R.id.tebal2);
        if (rb_funding.isChecked()) {
            tebal2.setText("Amount");
        }
        edtxqty.setFilters(new InputFilter[]{new InputFilterMinMax("1", "999999")});
        com.google.android.material.button.MaterialButton btn_submit = dialog.findViewById(R.id.btn_submit);
        if (item != null) {
            btn_submit.setText("Update");
            edtxqty.setText(item.getItem_quantity() + "");
            edtxdescription.setText(item.getRequest_item_description() + "");
            edtxrequest.setText(item.getRequest_item_title() + "");
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> dialog.dismiss());
        btn_submit.setOnClickListener(view -> {
            if (!edtxqty.getText().toString().trim().isEmpty() && !edtxrequest.getText().toString().trim().isEmpty()) {
                if (item != null) {
                    item.setItem_quantity(Integer.parseInt(edtxqty.getText().toString().trim()));
                    item.setRequest_item_description(edtxdescription.getText().toString().trim());
                    item.setRequest_item_title(edtxrequest.getText().toString().trim());
                    itemNeedUpdate(item);
                } else {
                    addToCart(Integer.parseInt(edtxqty.getText().toString().trim()), edtxdescription.getText().toString().trim(), edtxrequest.getText().toString().trim());
                }
                dialog.dismiss();
            } else {
                if (edtxrequest.getText().toString().trim().isEmpty()) {
                    edtxrequest.setError("Title is required");
                }
                if (edtxqty.getText().toString().trim().isEmpty()) {
                    edtxqty.setError("Quentity is required");
                }
            }

        });


        dialog.show();


    }

    private void setAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(400);
        view.startAnimation(alphaAnimation);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.75f, 1.0f, 0.75f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(400);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        view.startAnimation(animationSet);
    }

    private void date(EditText editText) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    time(editText);

                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void time(EditText editText) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    DateTime dateTime = DateTime.parse(editText.getText() + " " + hourOfDay + ":" + minute,
                            DateTimeFormat.forPattern("dd-MM-yyyy HH:mm"));
                    DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd MMM yyyy, hh:mm aa");
                    editText.setText(dtfOut.print(dateTime));
                }, mHour, mMinute, false);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dialog_cancel), (dialog, which) -> {
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                editText.setText("");
            }
        });
        timePickerDialog.show();
    }

    @Override
    public void onEdit(int pos) {
        showDialogueForCreateNeed(items.get(pos));
    }

    @Override
    public void onDelete(int pos) {
        confirmation(items.get(pos), pos);
    }

    private boolean validate() {
        if (postSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select the family", Toast.LENGTH_SHORT).show();
            return false;
        } else if (items.size() == 0) {
            Toast.makeText(this, "Please add the item.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtxDateTimeEnd.getText().toString().length() > 0) {

            DateTime startDateTime = DateTime.parse(edtxDateTimeStart.getText().toString().trim(), DateTimeFormat.forPattern("dd MMM yyyy, hh:mm aa"));
            DateTime endDateTime = DateTime.parse(edtxDateTimeEnd.getText().toString().trim(), DateTimeFormat.forPattern("dd MMM yyyy, hh:mm aa"));


            if (startDateTime.isBefore(endDateTime)) {
                return true;
            } else {

                Toast.makeText(this, "Please choose valid Start date & End date", Toast.LENGTH_SHORT).show();
                return false;
            }

        }


        return true;
    }


    private void updateNeed() {
        showProgressDialog();
        Need request = new Need();
        request.setRequest_type("general");
        if (rb_funding.isChecked()) {
            request.setRequest_type("fund");
        }
        request.setPost_request_id(need.getPost_request_id());
        request.setUser_id(SharedPref.getUserRegistration().getId());
        // request.setItems(items);
        request.setRequest_location(etxt_location.getText().toString().trim());
        request.setUser_id(need.getUser_id());
        switch (postSpinner.getSelectedItemPosition()) {
            case 1:
                if (rb_general.isChecked()) {
                    request.setGroup_type("all_family");
                    request.setTo_group_id(new ArrayList<>());
                    break;
                } else {
                    request.setGroup_type("selected_family");
                    ArrayList<Integer> fa = new ArrayList<>();
                    for (SelectFamilys f : family) {
                        fa.add(Integer.parseInt(f.getId()));
                    }
                    request.setTo_group_id(fa);
                    break;
                }
            case 2:
                request.setGroup_type("selected_family");
                ArrayList<Integer> fa = new ArrayList<>();
                for (SelectFamilys f : family) {
                    fa.add(Integer.parseInt(f.getId()));
                }
                request.setTo_group_id(fa);
                break;
        }
        if (edtxDateTimeStart.getText().toString().trim().length() > 0) {
            DateTime startDateTime = DateTime.parse(edtxDateTimeStart.getText().toString(), DateTimeFormat.forPattern("dd MMM yyyy, hh:mm aa"));
            request.setStart_date(startDateTime.getMillis() / 1000);
        }
        if (edtxDateTimeEnd.getText().toString().trim().length() > 0) {
            DateTime endDateTime = DateTime.parse(edtxDateTimeEnd.getText().toString(), DateTimeFormat.forPattern("dd MMM yyyy, hh:mm aa"));
            request.setEnd_date(endDateTime.getMillis() / 1000);
        }
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.updateNeed(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                            hideProgressDialog();
                            if (response.code() == 200) {
                                Toast.makeText(this,
                                        "Request successfully updates", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }, throwable -> hideProgressDialog()
                ));
    }

    private void confirmation(Item item, int pos) {

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Do you really want to delete this Item?")
                .setConfirmText("Yes")
                .setCancelText("No");

        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
            itemNeedDelete(item, pos);
            pDialog.cancel();
        });
        pDialog.setCancelClickListener(SweetAlertDialog::cancel);
    }

    private void itemNeedDelete(Item item, int pos) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", item.getItemId() + "");
        jsonObject.addProperty("post_request_id", need.getPost_request_id());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.deleteItemsNeed(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                            hideProgressDialog();
                            if (response.code() == 200) {
                                items.remove(pos);
                                createNeedsAdapter.notifyDataSetChanged();
                                if (items.size() == 0) {
                                    cart_item.setVisibility(View.GONE);
                                    btn_add.setVisibility(View.GONE);
                                    no_item_view.setVisibility(View.VISIBLE);
                                    btn_submit_temp.setVisibility(View.VISIBLE);
                                    btn_submit.setEnabled(false);
                                }
                            }
                        }, throwable -> hideProgressDialog()
                ));
    }

    private void itemNeedUpdate(Item item) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", item.getItemId() + "");
        jsonObject.addProperty("post_request_id", need.getPost_request_id());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("request_item_title", item.getRequest_item_title());
        jsonObject.addProperty("request_item_description", item.getRequest_item_description());
        jsonObject.addProperty("item_quantity", item.getItem_quantity());

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.updateItemsNeed(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                            hideProgressDialog();
                            if (response.code() == 200) {
                                createNeedsAdapter.notifyDataSetChanged();
                            }
                        }, throwable -> hideProgressDialog()
                ));
    }

    private void itemNeedCreate(Item item) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_request_id", need.getPost_request_id());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("request_item_title", item.getRequest_item_title());
        jsonObject.addProperty("request_item_description", item.getRequest_item_description());
        jsonObject.addProperty("item_quantity", item.getItem_quantity() + "");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(this);
        ApiServices apiServices = RetrofitBase.createRxResource(this, ApiServices.class);
        subscriptions.add(apiServices.createItemsNeed(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                            hideProgressDialog();
                            if (response.code() == 200) {
                                Item item1 = new Item();
                                assert response.body() != null;
                                item1.setItem_quantity(response.body().data.getItem_quantity());
                                item1.setRequest_item_title(response.body().data.getRequest_item_title());
                                item1.setRequest_item_description(response.body().data.getRequest_item_description());
                                item1.setItemId(response.body().data.getId());
                                item1.setType("ganeral");
                                if (rb_funding.isChecked()) {
                                    item1.setType("fund");
                                }
                                items.add(item1);
                                if (items.size() > 0) {
                                    btn_add.setVisibility(View.VISIBLE);
                                    cart_item.setVisibility(View.VISIBLE);
                                    no_item_view.setVisibility(View.GONE);
                                    btn_submit_temp.setVisibility(View.GONE);
                                    createNeedsAdapter.notifyDataSetChanged();
                                    btn_submit.setEnabled(true);
                                }
                            }
                        }, throwable -> hideProgressDialog()
                ));
    }

    void getNeedRequestDetails() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_request_id", need.getPost_request_id());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        subscriptions.add(authService.getNeedsRequestDetail(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                    assert response.body() != null;
                    need = response.body().getNeedRequest();
                    items = need.getItems();
                    hideShimmer();
                    if (items.size() > 0) {

                        if ("fund".equals(need.getRequest_type())) {
                            for (Item item : items) {
                                item.setType("fund");
                            }
                        }
                        createNeedsAdapter = new CreateNeedsAdapter(items, this);
                        cart_item.setLayoutManager(new LinearLayoutManager(this));
                        cart_item.setAdapter(createNeedsAdapter);
                        btn_add.setVisibility(View.VISIBLE);
                        cart_item.setVisibility(View.VISIBLE);
                        no_item_view.setVisibility(View.GONE);
                        btn_submit_temp.setVisibility(View.GONE);
                        btn_submit.setEnabled(true);
                    } else {
                        createNeedsAdapter = new CreateNeedsAdapter(items, this);
                        cart_item.setLayoutManager(new LinearLayoutManager(this));
                        cart_item.setAdapter(createNeedsAdapter);
                        no_item_view.setVisibility(View.VISIBLE);
                    }
                }, throwable -> {
                }));
    }

    private void showShimmer() {


        shimmer_view.setVisibility(View.VISIBLE);
        no_item_view.setVisibility(View.GONE);
        if (shimmer_view != null) {
            shimmer_view.setVisibility(View.VISIBLE);
            shimmer_view.startShimmer();
        }
    }

    private void hideShimmer() {
        if (shimmer_view != null) {
            shimmer_view.stopShimmer();
            shimmer_view.setVisibility(View.GONE);
        }
    }
}
