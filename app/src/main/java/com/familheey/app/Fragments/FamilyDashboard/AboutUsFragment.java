package com.familheey.app.Fragments.FamilyDashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.ImageSliderActivity;
import com.familheey.app.Activities.TextEditActivity;
import com.familheey.app.EditHistoryActivity;
import com.familheey.app.Interfaces.FamilyDashboardInteractor;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.Parsers.FamilyParser;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Bundle.MEMBER;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;
import static com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers.TypeAboutUsFragment;

public class AboutUsFragment extends BackableFragment implements RetrofitListener, ProgressListener {

    @BindView(R.id.intro)
    TextView intro;
    @BindView(R.id.btn_add_history)
    Button btn_add_history;
    @BindView(R.id.familyName)
    TextView familyName;
    @BindView(R.id.familyIntroduction)
    TextView familyIntroduction;
    @BindView(R.id.editFamily)
    ImageView editFamily;
    @BindView(R.id.historyName)
    TextView historyName;
    @BindView(R.id.historyNameUnderLine)
    ImageView historyNameUnderLine;
    //@BindView(R.id.labelHistoryIntroduction)
    //TextView labelHistoryIntroduction;
    @BindView(R.id.editHistory)
    ImageView editHistory;
    @BindView(R.id.familyToolBarTitle)
    TextView toolbar;
    @BindView(R.id.webview)
    WebView webview;
    private Family family;
    private Boolean isMember;
    private FamilyDashboardInteractor familyDashboardInteractor;
    public static String fromPage="";
    public String fromParent;
    public String introData="";
    public static final int HISTORY_EDIT_REQUEST_CODE = 101;
    public static final int TEXT_EDIT_REQUEST_CODE = 102;

    HashTagHelper hashTagHelper;
    String resultText="";
    SweetAlertDialog progressDialog;
    public AboutUsFragment() {
        // Required empty public constructor
    }

    public static AboutUsFragment newInstance(Family family,Boolean isMember) {
        AboutUsFragment fragment = new AboutUsFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        args.putBoolean(MEMBER,isMember);
        fragment.setArguments(args);
        return fragment;
    }
    public static AboutUsFragment newInstance(Family family,Boolean isMember,String fromTab) {
        AboutUsFragment fragment = new AboutUsFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA, family);
        args.putBoolean(MEMBER,isMember);
        fromPage=fromTab;
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            family = getArguments().getParcelable(DATA);
            introData=family.getIntro();
            isMember=getArguments().getBoolean(MEMBER);
            fromParent=fromPage;
            //getArguments().clear();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        ButterKnife.bind(this, view);
        if(fromParent.equals("fromGrid")){
            setFragmentTitle();
        }
        return view;
    }
    private void setFragmentTitle() {
            toolbar.setText(getResources().getString(R.string.fragment_dashboard_menu_aboutUs));
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webview.setVerticalScrollBarEnabled(true);
        webview.setHorizontalScrollBarEnabled(true);
        getFamilyDetails();
        initRestrictions();
    }
/** Defined by Devika on 24/08/2021 **/
    @Override
    public void onBackButtonPressed() {
        if(isMember){
            ((FamilyDashboardActivity)requireActivity()).backAction();
        }else {
            requireActivity().onBackPressed();
        }
    }

    private void initRestrictions() {
        if (family != null && family.isAdmin()) {
            editFamily.setVisibility(View.VISIBLE);
            editHistory.setVisibility(View.VISIBLE);
            if (family.getHistoryText() == null || family.getHistoryText().isEmpty() && family.getHistoryImages() == null || family.getHistoryImages().size() > 0) {
                webview.setVisibility(View.GONE);
            }

        } else {
            editFamily.setVisibility(View.INVISIBLE);
            editHistory.setVisibility(View.INVISIBLE);
        }
        //loadHtml();
    }

    private void loadHtml() {
        String text = "";
        String imageHtml = "";
        String html = "";
        if (family.getHistoryText() != null) {
            text = family.getHistoryText();
            /**to display links in history**/
            Spannable sp = new SpannableString(text);
            Linkify.addLinks(sp, Linkify.WEB_URLS|Linkify.EMAIL_ADDRESSES|Linkify.PHONE_NUMBERS);
            resultText = "<body>" + Html.toHtml(sp) + "</body>";
            /**end**/
        }
        if (family.getHistoryImages() != null && family.getHistoryImages().size() > 0) {
            String url = IMAGE_BASE_URL + "history_images/" + family.getHistoryImages().get(0).getFilename();
            imageHtml = "<a href=\"https://www.gallery.com/\"><img align=\"left\"\"style=\"margin:0 16px 5px 0\" src=\"" + url + "\" align=\"left\" width=\"150px\" /></a>";
        }

        if (resultText.isEmpty() && imageHtml.isEmpty()) {
            webview.setVisibility(View.GONE);
        } else if (!resultText.isEmpty() && imageHtml.isEmpty()) {

        } else if (resultText.isEmpty() && !imageHtml.isEmpty()) {

        }
        webview.setVisibility(View.VISIBLE);
        html = "<html><body><style>div {width: 100%; }div.b {white-space:pre-wrap;}</style><div class=\"b\">" + imageHtml +  resultText + "</div></body></html>";
        webview.setWebViewClient(new MyWebViewClient(getContext()));
        webview.loadData(html, "text/html", null);

    }
/*

 */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        familyDashboardInteractor = Utilities.getListener(this, FamilyDashboardInteractor.class);
        if (familyDashboardInteractor == null)
            throw new RuntimeException(context.toString() + " must implement FamilyDashboardInteractor");
    }

   /* public void fillDetails(Family family) {
        this.family = family;
        familyIntroduction.setText(family.getIntro());
        initRestrictions();
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        familyDashboardInteractor = null;

    }

    @OnClick({R.id.intro, R.id.editHistory, R.id.editFamily})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.editHistory:
                startActivityForResult(new Intent(getActivity(), EditHistoryActivity.class).putExtra(DATA, new Gson().toJson(family)), HISTORY_EDIT_REQUEST_CODE );
                //mListener.updateFamily(FamilyEditDialogFragment.TYPE_HISTORY, family);
                break;
            case R.id.editFamily:
               // mListener.updateFamily(FamilyEditDialogFragment.TYPE_INTRODUCTION, family);
                startActivityForResult(new Intent(getActivity(), TextEditActivity.class).putExtra(ID, family.getId() + "").putExtra("type", "family").putExtra("tittle", "Edit Introduction").putExtra(DATA, familyIntroduction.getText() + ""), TEXT_EDIT_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(requireActivity());
        progressDialog.show();
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        if (progressDialog == null) {
            progressDialog = Utilities.getErrorDialog(requireActivity(), errorMessage);
            progressDialog.show();
            return;
        }
        Utilities.getErrorDialog(progressDialog, errorMessage);
    }

    public class MyWebViewClient extends WebViewClient {

        private Context context;

        public MyWebViewClient(Context context) {
            this.context = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.contains("https://www.gallery.com")) {
                Intent intent = new Intent(getContext(), ImageSliderActivity.class);
                intent.putExtra(DATA, family.getHistoryImagesUrls());
                intent.putExtra(POSITION, 0);
                startActivity(intent);
            } else {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
            return true;
        }    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == HISTORY_EDIT_REQUEST_CODE) {
            family.setHistoryText(data.getExtras().getString("history"));
            family.setHistoryImages((ArrayList<HistoryImages>) fromJson(data.getExtras().getString("images"),
                    new TypeToken<ArrayList<HistoryImages>>() {
                    }.getType()));
            loadHtml();
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == TEXT_EDIT_REQUEST_CODE) {
            familyIntroduction.setText(data.getExtras().getString(DATA));
            /**display links in intro **/
            if(familyIntroduction!=null){
                hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(requireActivity(),R.color.linkcolor), null);
                hashTagHelper.handle(familyIntroduction);
                Linkify.addLinks(familyIntroduction, Linkify.ALL); // linkify all links in text.
                familyIntroduction.setLinkTextColor(ContextCompat.getColor(requireActivity(),R.color.linkcolor));
            }
        }
    }

    public static Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }
/** modified on 27-10-21 for navigating to dashboard after viewing the image set for about us history
 * then returning to about us from there to dashboard instead of family listing page**/
    @Override
    public void onResume() {
        super.onResume();
        if (familyDashboardInteractor != null)
            familyDashboardInteractor.onFamilyAddComponentHidden(TypeAboutUsFragment);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    if(isMember){
                        ((FamilyDashboardActivity)requireActivity()).backAction();
                    }else {
                        requireActivity().onBackPressed();
                    }
                    return true;
                }
                return false;
            }
        });
    }
    public void getFamilyDetails() {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        jsonObject.addProperty("group_id", family.getId().toString());
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        apiServiceProvider.getFamilyDetailsByID(jsonObject, null, this);
    }
    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        hideProgressDialog();
        family = FamilyParser.parseLinkedFamilies(responseBodyString).get(0);
        familyIntroduction.setText(family.getIntro());
        /**links in intro**/
        if(familyIntroduction!=null){
            hashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(requireActivity(),R.color.linkcolor), null);
            hashTagHelper.handle(familyIntroduction);
            Linkify.addLinks(familyIntroduction, Linkify.ALL); // linkify all links in text.
            familyIntroduction.setLinkTextColor(ContextCompat.getColor(requireActivity(),R.color.linkcolor));
        }
        loadHtml();
    }
    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {

    }

}
