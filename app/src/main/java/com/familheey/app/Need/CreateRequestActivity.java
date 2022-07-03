package com.familheey.app.Need;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
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
import com.familheey.app.Stripe.Error;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.luseen.autolinklibrary.AutoLinkMode;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.Bundle.ADDRESS;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ID;

public class CreateRequestActivity extends AppCompatActivity implements CreateRequestAdapterListner, ProgressListener {
    public static final int REQUEST_CREATE_REQUEST_CODE = 101;
    public static final int REQUEST_CREATE_REQUEST_CODE2 = 102;
    public static final int REQUEST_CODE = 2001;
    public CompositeDisposable subscriptions;
    @BindView(R.id.btn_add)
    LinearLayout btn_add;
    @BindView(R.id.cart_item)
    RecyclerView cart_item;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.img_status)
    ImageView img_status;
    @BindView(R.id.main_view_request)
    androidx.coordinatorlayout.widget.CoordinatorLayout main_view;
    @BindView(R.id.post_spinner)
    FSpinner postSpinner;

    @BindView(R.id.txt_family_cout_from_family)
    TextView txt_family_cout_from_family;
    @BindView(R.id.txt_count)
    TextView txt_count;
    @BindView(R.id.txt_warning)
    TextView txt_warning;
    @BindView(R.id.etxt_location)
    EditText etxt_location;
    @BindView(R.id.edtxDateTimeEnd)
    EditText edtxDateTimeEnd;
    @BindView(R.id.edtxDateTimeStart)
    EditText edtxDateTimeStart;
    @BindView(R.id.no_item_view)
    LinearLayout no_item_view;

    @BindView(R.id.bank_account_view)
    LinearLayout bank_account_view;
    @BindView(R.id.add_item)
    LinearLayout add_item;
    @BindView(R.id.rb_general)
    RadioButton rb_general;
    @BindView(R.id.rb_funding)
    RadioButton rb_funding;
    @BindView(R.id.txt_bank_tittle)
    TextView txt_bank_tittle;
    @BindView(R.id.btn_add_bank)
    TextView btn_add_bank;

    @BindView(R.id.txt_status)
    TextView txt_status;
    @BindView(R.id.txt_account_number)
    TextView txt_account_number;
    @BindView(R.id.btn_submit_temp)
    com.google.android.material.button.MaterialButton btn_submit_temp;
    @BindView(R.id.btn_submit)
    com.google.android.material.button.MaterialButton btn_submit;
    private SweetAlertDialog progressDialog;
    private ArrayAdapter spinnerArrayAdapter;
    ArrayList<SelectFamilys> family = new ArrayList<>();
    private Boolean isSpinnerSelection = true;
    private Boolean isSpinnerFirstSelection = true;

    private CreateNeedsAdapter createNeedsAdapter;
    private final ArrayList<Item> items = new ArrayList<>();
    private String businessAccount = "";
    private Boolean is_account_valid = false;
    private String from = "";
    private Boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        ButterKnife.bind(this);
        init();
        Bundle b = getIntent().getExtras();
        postSpinner.setVisibility(View.VISIBLE);
        if (b != null) {
            family = new ArrayList<>();
            SelectFamilys obj = new SelectFamilys();
            obj.setId(b.getString(ID));
            obj.setPost_create(b.getString(DATA));
            businessAccount = b.getString("ACC");
            isAdmin = b.getBoolean("isAdmin");
            family.add(obj);
            isSpinnerSelection = false;
            from = "FAMILY";
            postSpinner.setVisibility(View.GONE);
            postSpinner.setSelection(2);
            //Dinu(24/07/2021) For remove post this request to
            txt_family_cout_from_family.setVisibility(View.GONE);
            txt_family_cout_from_family.setText(b.getString("NAME") + " Selected");
            txt_count.setText("");
            if (!isAdmin)
                rb_funding.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_REQUEST_CODE && resultCode == RESULT_OK) {
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
        } else if (requestCode == REQUEST_CREATE_REQUEST_CODE2 && resultCode == RESULT_OK) {
            family = new ArrayList<>();
            SelectFamilys familys = new SelectFamilys();
            assert data != null;
            familys.setId(Objects.requireNonNull(data.getExtras()).getString("DATA"));
            family.add(familys);
            txt_count.setVisibility(View.VISIBLE);
            txt_count.setText(Objects.requireNonNull(data.getExtras()).getString("NAME") + " Family selected");
            businessAccount = data.getExtras().getString("ACC");
            if ("".equals(businessAccount)) {
                txt_bank_tittle.setVisibility(View.VISIBLE);
                btn_add_bank.setVisibility(View.VISIBLE);
                bank_account_view.setVisibility(View.GONE);
            }
        } else if (requestCode == REQUEST_CODE) {
            checkBusinessAccountStatus(true);
        } else if (requestCode == AddressFetchingActivity.RequestCode && resultCode == Activity.RESULT_OK) {
            assert data != null;
            etxt_location.setText(data.getStringExtra(ADDRESS));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rb_funding.isChecked() && family != null && family.size() == 1 && !businessAccount.equals("")) {
            checkBusinessAccountStatus(false);
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
                    createNeed();
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
        subscriptions = new CompositeDisposable();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy, hh:mm aa");
        edtxDateTimeStart.setText(format.format(calendar.getTime()));
        edtxDateTimeStart.setOnClickListener(view -> date(edtxDateTimeStart));
        edtxDateTimeEnd.setOnClickListener(view -> date(edtxDateTimeEnd));
        etxt_location.setText(SharedPref.getUserRegistration().getLocation());
        setUpRecyclerView();

        String[] options = {"Select", "All My Families", "Selected Families"};
        String[] funding_options = {"Select", "Selected Families"};
        spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, options);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postSpinner.setAdapter(spinnerArrayAdapter);
        postSpinner.setSelection(0);
        postSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 2:
                        if (isSpinnerSelection) {
                            txt_count.setText("");
                            if (family != null && family.size() > 0) {
                                startActivityForResult(new Intent(CreateRequestActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "NEED").putExtra("type", "FAMILY").putExtra("refid", new Gson().toJson(family)), REQUEST_CREATE_REQUEST_CODE);
                            } else {
                                startActivityForResult(new Intent(CreateRequestActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "NEED").putExtra("type", "FAMILY"), REQUEST_CREATE_REQUEST_CODE);
                            }
                        }
                        isSpinnerSelection = true;
                        break;
                    case 1:
                        if (rb_general.isChecked()) {
                            txt_count.setVisibility(View.GONE);
                            txt_count.setText("");
                        } else {

                            if (isSpinnerFirstSelection && family != null && family.size() > 0) {
                            } else
                                startActivityForResult(new Intent(CreateRequestActivity.this, SelectMyfamilies.class), REQUEST_CREATE_REQUEST_CODE2);
                            isSpinnerFirstSelection = false;
                        }
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (postSpinner.getSelectedItemPosition() == 2) {
                    txt_count.setText("");
                    startActivityForResult(new Intent(CreateRequestActivity.this, SelectFamilesOrPeopleActivity.class).putExtra("from", "POST").putExtra("type", "FAMILY"), REQUEST_CREATE_REQUEST_CODE);
                }
            }
        });

        rb_general.setOnClickListener(view -> {
            if (rb_general.isChecked()) {
                if (from.equals("")) {
                    spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, options);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    postSpinner.setAdapter(spinnerArrayAdapter);
                    family = null;
                    txt_count.setVisibility(View.GONE);
                    btn_add_bank.setVisibility(View.GONE);
                    items.clear();
                    createNeedsAdapter.notifyDataSetChanged();
                    btn_submit_temp.setVisibility(View.VISIBLE);
                    btn_submit.setEnabled(false);
                }
            }
            btn_add_bank.setVisibility(View.GONE);
            bank_account_view.setVisibility(View.GONE);
            txt_bank_tittle.setVisibility(View.GONE);
        });


        rb_funding.setOnClickListener(view -> {
            if (rb_funding.isChecked()) {
                spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, funding_options);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                postSpinner.setAdapter(spinnerArrayAdapter);
                if (from.equals("FAMILY")) {

                    // postSpinner.setSelection(1, false);
                    // firstClick = false;
                    if ("".equals(businessAccount)) {
                        txt_bank_tittle.setVisibility(View.VISIBLE);
                        btn_add_bank.setVisibility(View.VISIBLE);
                        bank_account_view.setVisibility(View.GONE);
                    } else {
                        checkBusinessAccountStatus(false);
                    }

                } else {
                    family = null;
                    txt_count.setVisibility(View.GONE);
                    txt_count.setText("");

                    items.clear();
                    btn_submit_temp.setVisibility(View.VISIBLE);
                    btn_submit.setEnabled(false);
                    createNeedsAdapter.notifyDataSetChanged();
                }
            }
        });
        btn_add_bank.setOnClickListener(view -> {
            if (family != null && family.size() > 0)
                generateBankAddLink();
        });

        img_status.setOnClickListener(view -> {
            if (!is_account_valid)
                dialogOpenLink();
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
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();

    }

    @Override
    public void showErrorDialog(String errorMessage) {

    }

    private void setUpRecyclerView() {
        createNeedsAdapter = new CreateNeedsAdapter(items, this);
        cart_item.setLayoutManager(new LinearLayoutManager(this));
        cart_item.setAdapter(createNeedsAdapter);
    }

    private void addToCart(int qty, String des, String tittle) {
        Item item = new Item();
        item.setItem_quantity(qty);
        item.setRequest_item_description(des);
        item.setRequest_item_title(tittle);

        item.setType("ganeral");
        if (rb_funding.isChecked()) {
            item.setType("fund");
        }
        items.add(item);
        if (items.size() > 0) {
            btn_add.setVisibility(View.VISIBLE);
            cart_item.setVisibility(View.VISIBLE);
            no_item_view.setVisibility(View.GONE);
            btn_submit_temp.setVisibility(View.GONE);
            createNeedsAdapter.notifyDataSetChanged();
            btn_submit.setEnabled(true);
            if (rb_funding.isChecked()) {
                if (is_account_valid) {
                    btn_submit_temp.setVisibility(View.GONE);
                    btn_submit.setEnabled(true);
                } else {
                    btn_submit.setEnabled(false);
                    btn_submit_temp.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private void createNeed() {
        showProgressDialog();
        Need request = new Need();
        request.setRequest_type("general");
        if (rb_funding.isChecked()) {
            request.setRequest_type("fund");
        }
        request.setUser_id(SharedPref.getUserRegistration().getId());
        request.setItems(items);
        request.setRequest_location(etxt_location.getText().toString().trim());
        if (from.equals("")) {
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
        } else {
            request.setGroup_type("selected_family");
            ArrayList<Integer> fa = new ArrayList<>();
            for (SelectFamilys f : family) {
                fa.add(Integer.parseInt(f.getId()));
            }
            request.setTo_group_id(fa);
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
        subscriptions.add(apiServices.createNeed(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                            hideProgressDialog();
                            if (response.code() == 200) {
                                Toast.makeText(this,
                                        "Request successfully created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }, throwable -> hideProgressDialog()
                ));
    }


    private void generateBankAddLink() {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", family.get(0).getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        FamilheeyApplication application = FamilheeyApplication.get(this);
        subscriptions.add(authService.stripe_oauth_link_generation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                            hideProgressDialog();
                            if (response.code() == 200) {
                                assert response.body() != null;
                                String url = response.body().get("link").getAsString();

                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back_white_24dp);
                                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder()
                                        .setShowTitle(true)
                                        .setToolbarColor(getResources().getColor(R.color.buttoncolor))
                                        .setSecondaryToolbarColor(getResources().getColor(R.color.white))
                                        .setCloseButtonIcon(bitmap);
                                CustomTabsIntent customTabsIntent = builder.build();

                                customTabsIntent.intent.setData(Uri.parse(url));
                                startActivityForResult(customTabsIntent.intent, REQUEST_CODE);
                                //customTabsIntent.launchUrl(CreateRequestActivity.this, Uri.parse(url));
                            }
                        }, throwable -> hideProgressDialog()
                ));
    }

    private void checkBusinessAccountStatus(Boolean isCreate) {
        txt_bank_tittle.setVisibility(View.GONE);
        btn_add_bank.setVisibility(View.GONE);
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", family.get(0).getId());
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        final ApiServices authService = RetrofitBase.createRxResource(getApplicationContext(), ApiServices.class);
        FamilheeyApplication application = FamilheeyApplication.get(this);
        subscriptions.add(authService.stripeGetaccountById(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {

                            hideProgressDialog();
                            assert response.body() != null;
                    if (response.code() == 200 && response.body().get("payouts_enabled").getAsBoolean() &&
                            response.body().get("charges_enabled").getAsBoolean()) {
                        btn_submit_temp.setVisibility(View.GONE);
                        btn_submit.setEnabled(true);
                        is_account_valid = true;
                        txt_status.setText("Active");
                        txt_warning.setVisibility(View.GONE);
                        img_status.setImageResource(R.drawable.ic_check_circle_black_24dp);
                        bank_account_view.setVisibility(View.VISIBLE);
                        txt_account_number.setText("Bank Account");
                    } else if (response.code() == 500) {
                        Type type = new TypeToken<Error>() {
                        }.getType();
                        Error errorResponse = new Gson().fromJson(response.errorBody().charStream(), type);
                        bankAccPending();
                        txt_warning.setText(errorResponse.getMessage());
                    } else {
                        if (isCreate) {
                            bankAccWarning();
                        } else {
                            bankAccPending();
                        }
                    }
                        }, throwable -> {
                            hideProgressDialog();
                            if (isCreate) {
                                bankAccWarning();
                            } else {
                                bankAccPending();
                            }
                        }
                ));
    }

    private void bankAccWarning() {

        WarningMessage();
        txt_bank_tittle.setVisibility(View.VISIBLE);
        btn_add_bank.setVisibility(View.VISIBLE);
        bank_account_view.setVisibility(View.GONE);
    }


    private void bankAccPending() {

        btn_submit_temp.setVisibility(View.VISIBLE);
        btn_submit.setEnabled(false);
        is_account_valid = false;
        bank_account_view.setVisibility(View.VISIBLE);
        txt_account_number.setText("Bank Account");
        txt_warning.setVisibility(View.VISIBLE);
        img_status.setImageResource(R.drawable.ic_error_outline_black_24dp);
        txt_status.setText("Pending");
    }

    private void WarningMessage() {
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_user_block);
        com.luseen.autolinklibrary.AutoLinkTextView textView = dialog.findViewById(R.id.txt_msg);
        textView.addAutoLinkMode(AutoLinkMode.MODE_EMAIL);
        dialog.findViewById(R.id.btn_login).setVisibility(View.GONE);
        dialog.setCanceledOnTouchOutside(false);
        textView.setAutoLinkText("If you have completed the stripe account creation, kindly wait for some time. We will notify you once account creation is successful.Otherwise complete the stripe account creation for raising fund request.");
        dialog.findViewById(R.id.btn_close).setOnClickListener(view -> dialog.dismiss());

        dialog.show();
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
                    createNeedsAdapter.notifyDataSetChanged();
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
        //   datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
        confirmation(pos);
    }


    private void confirmation(int pos) {

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Do you really want to delete this Item?")
                .setConfirmText("Yes")
                .setCancelText("No");

        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
            items.remove(pos);
            createNeedsAdapter.notifyDataSetChanged();
            if (items.size() == 0) {
                cart_item.setVisibility(View.GONE);
                btn_add.setVisibility(View.GONE);
                no_item_view.setVisibility(View.VISIBLE);
                btn_submit_temp.setVisibility(View.VISIBLE);
                btn_submit.setEnabled(false);
            }
            pDialog.cancel();
        });
        pDialog.setCancelClickListener(SweetAlertDialog::cancel);
    }

    private boolean validate() {

        if (items.size() == 0) {
            Toast.makeText(this, "Please add the item.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (postSpinner.getVisibility() == View.VISIBLE) {
            if (postSpinner.getSelectedItemPosition() != 1 && family.size() == 0 || postSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select the family", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (family.size() == 0) {
                Toast.makeText(this, "Please select the family", Toast.LENGTH_SHORT).show();
                return false;
            }
        }


        if (edtxDateTimeStart.getText().toString().length() > 0 && edtxDateTimeEnd.getText().toString().length() > 0) {

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

    @Override
    public void onBackPressed() {
        if (items.size() > 0) {
            confirmationBack();
        } else {
            finish();

            overridePendingTransition(R.anim.left,
                    R.anim.right);
        }
    }

    private void confirmationBack() {

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Are you sure want to close?")
                .setConfirmText("Yes")
                .setCancelText("No");

        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
            pDialog.cancel();
            finish();
            overridePendingTransition(R.anim.left,
                    R.anim.right);
        });
        pDialog.setCancelClickListener(SweetAlertDialog::cancel);
    }

    private void dialogOpenLink() {
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_user_block);
        com.luseen.autolinklibrary.AutoLinkTextView textView = dialog.findViewById(R.id.txt_msg);
        TextView btn_close = dialog.findViewById(R.id.btn_close);
        textView.addAutoLinkMode(AutoLinkMode.MODE_URL);
        textView.setUrlModeColor(ContextCompat.getColor(this, R.color.buttoncolor));
        dialog.setCanceledOnTouchOutside(false);
        btn_close.setText("Open");
        textView.setAutoLinkText("Your account is not active, few details might be missing, open the link below and sign into stripe to see details.\nhttps://dashboard.stripe.com");

        textView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            dialog.dismiss();
            openLink();
        });

        btn_close.setOnClickListener(view -> {
            dialog.dismiss();
            openLink();
        });
        dialog.show();
    }

    private void openLink() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(CreateRequestActivity.this, Uri.parse("https://dashboard.stripe.com"));
    }
}
