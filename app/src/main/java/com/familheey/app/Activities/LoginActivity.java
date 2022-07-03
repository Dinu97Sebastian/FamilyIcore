package com.familheey.app.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.familheey.app.Dialogs.CountryCodeDialogFragment;
import com.familheey.app.Interfaces.CountrySelectedListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.Country;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Request.LoginRequest;
import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.NewUserWelcomeActivity;
import com.familheey.app.R;
import com.familheey.app.SplashScreen;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static com.familheey.app.Utilities.Constants.ApiFlags.FETCH_GROUP_EVENTS;
import static com.familheey.app.Utilities.Constants.ApiFlags.MOBILE_NUMBER_DETAILS;
import static com.familheey.app.Utilities.Constants.ApiFlags.VALIDATE_MOBILE_NUMBER;
import static com.familheey.app.Utilities.Constants.ApiFlags.VERIFIED_MOBILE_NUMBER;
import static com.familheey.app.Utilities.Constants.ApiPaths.PRIVACY_URL;
import static com.familheey.app.Utilities.Constants.Bundle.JOIN_FAMILY_ID;
import static com.familheey.app.Utilities.Constants.Bundle.TO_CREATE_FAMILY;

public class LoginActivity extends AppCompatActivity implements RetrofitListener, CountrySelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // region Countries
    private final Country[] COUNTRIES = {
            new Country("AD", "Andorra", "+376", R.drawable.flag_ad, "EUR"),
            new Country("AE", "United Arab Emirates", "+971", R.drawable.flag_ae, "AED"),
            new Country("AF", "Afghanistan", "+93", R.drawable.flag_af, "AFN"),
            new Country("AG", "Antigua and Barbuda", "+1-268", R.drawable.flag_ag, "XCD"),
            new Country("AI", "Anguilla", "+1-264", R.drawable.flag_ai, "XCD"),
            new Country("AL", "Albania", "+355", R.drawable.flag_al, "ALL"),
            new Country("AM", "Armenia", "+374", R.drawable.flag_am, "AMD"),
            new Country("AN","Netherlands Antilles", "+599",R.drawable.flag_an,"ANG"),
            new Country("AO", "Angola", "+244", R.drawable.flag_ao, "AOA"),
            new Country("AQ", "Antarctica", "+672", R.drawable.flag_aq, "USD"),
            new Country("AR", "Argentina", "+54", R.drawable.flag_ar, "ARS"),
            new Country("AS", "American Samoa", "+1-684", R.drawable.flag_as, "USD"),
            new Country("AT", "Austria", "+43", R.drawable.flag_at, "EUR"),
            new Country("AU", "Australia", "+61", R.drawable.flag_au, "AUD"),
            new Country("AW", "Aruba", "+297", R.drawable.flag_aw, "AWG"),
            new Country("AZ", "Azerbaijan", "+994", R.drawable.flag_az, "AZN"),
            new Country("BA", "Bosnia and Herzegovina", "+387", R.drawable.flag_ba, "BAM"),
            new Country("BB", "Barbados", "+1-246", R.drawable.flag_bb, "BBD"),
            new Country("BD", "Bangladesh", "+880", R.drawable.flag_bd, "BDT"),
            new Country("BE", "Belgium", "+32", R.drawable.flag_be, "EUR"),
            new Country("BF", "Burkina Faso", "+226", R.drawable.flag_bf, "XOF"),
            new Country("BG", "Bulgaria", "+359", R.drawable.flag_bg, "BGN"),
            new Country("BH", "Bahrain", "+973", R.drawable.flag_bh, "BHD"),
            new Country("BI", "Burundi", "+257", R.drawable.flag_bi, "BIF"),
            new Country("BJ", "Benin", "+229", R.drawable.flag_bj, "XOF"),
            new Country("BL", "Saint Barthelemy", "+590", R.drawable.flag_bl, "EUR"),
            new Country("BM", "Bermuda", "+1-441", R.drawable.flag_bm, "BMD"),
            new Country("BN", "Brunei", "+673", R.drawable.flag_bn, "BND"),
            new Country("BO", "Bolivia", "+591", R.drawable.flag_bo, "BOB"),
            new Country("BR", "Brazil", "+55", R.drawable.flag_br, "BRL"),
            new Country("BS", "Bahamas", "+1-242", R.drawable.flag_bs, "BSD"),
            new Country("BT", "Bhutan", "+975", R.drawable.flag_bt, "BTN"),
            new Country("BW", "Botswana", "+267", R.drawable.flag_bw, "BWP"),
            new Country("BY", "Belarus", "+375", R.drawable.flag_by, "BYR"),
            new Country("BZ", "Belize", "+501", R.drawable.flag_bz, "BZD"),
            new Country("CA", "Canada", "+1", R.drawable.flag_ca, "CAD"),
            new Country("CC", "Cocos Islands", "+61", R.drawable.flag_cc, "AUD"),
            new Country("CD", "Democratic Republic of the Congo", "+243", R.drawable.flag_cd, "CDF"),
            new Country("CF", "Central African Republic", "+236", R.drawable.flag_cf, "XAF"),
            new Country("CG", "Republic of the Congo", "+242", R.drawable.flag_cg, "XAF"),
            new Country("CH", "Switzerland", "+41", R.drawable.flag_ch, "CHF"),
            new Country("CI", "Ivory Coast", "+225", R.drawable.flag_ci, "XOF"),
            new Country("CK", "Cook Islands", "+682", R.drawable.flag_ck, "NZD"),
            new Country("CL", "Chile", "+56", R.drawable.flag_cl, "CLP"),
            new Country("CM", "Cameroon", "+237", R.drawable.flag_cm, "XAF"),
            new Country("CN", "China", "+86", R.drawable.flag_cn, "CNY"),
            new Country("CO", "Colombia", "+57", R.drawable.flag_co, "COP"),
            new Country("CR", "Costa Rica", "+506", R.drawable.flag_cr, "CRC"),
            new Country("CU", "Cuba", "+53", R.drawable.flag_cu, "CUP"),
            new Country("CV", "Cape Verde", "+238", R.drawable.flag_cv, "CVE"),
            new Country("CW", "Curacao", "+599", R.drawable.flag_cw, "ANG"),
            new Country("CX", "Christmas Island", "+61", R.drawable.flag_cx, "AUD"),
            new Country("CY", "Cyprus", "+357", R.drawable.flag_cy, "EUR"),
            new Country("CZ", "Czech Republic", "+420", R.drawable.flag_cz, "CZK"),
            new Country("DE", "Germany", "+49", R.drawable.flag_de, "EUR"),
            new Country("DJ", "Djibouti", "+253", R.drawable.flag_dj, "DJF"),
            new Country("DK", "Denmark", "+45", R.drawable.flag_dk, "DKK"),
            new Country("DM", "Dominica", "+1-767", R.drawable.flag_dm, "XCD"),
            new Country("DO", "Dominican Republic", "+1-849", R.drawable.flag_do, "DOP"),
            new Country("DZ", "Algeria", "+213", R.drawable.flag_dz, "DZD"),
            new Country("EC", "Ecuador", "+593", R.drawable.flag_ec, "USD"),
            new Country("EE", "Estonia", "+372", R.drawable.flag_ee, "EUR"),
            new Country("EG", "Egypt", "+20", R.drawable.flag_eg, "EGP"),
            new Country("EH", "Western Sahara", "+212", R.drawable.flag_eh, "MAD"),
            new Country("ER", "Eritrea", "+291", R.drawable.flag_er, "ERN"),
            new Country("ES", "Spain", "+34", R.drawable.flag_es, "EUR"),
            new Country("ET", "Ethiopia", "+251", R.drawable.flag_et, "ETB"),
            new Country("FI", "Finland", "+358", R.drawable.flag_fi, "EUR"),
            new Country("FJ", "Fiji", "+679", R.drawable.flag_fj, "FJD"),
            new Country("FK", "Falkland Islands", "+500", R.drawable.flag_fk, "FKP"),
            new Country("FM", "Micronesia", "+691", R.drawable.flag_fm, "USD"),
            new Country("FO", "Faroe Islands", "+298", R.drawable.flag_fo, "DKK"),
            new Country("FR", "France", "+33", R.drawable.flag_fr, "EUR"),
            new Country("GA", "Gabon", "+241", R.drawable.flag_ga, "XAF"),
            new Country("GB", "United Kingdom", "+44", R.drawable.flag_gb, "GBP"),
            new Country("GD", "Grenada", "+1-473", R.drawable.flag_gd, "XCD"),
            new Country("GE", "Georgia", "+995", R.drawable.flag_ge, "GEL"),
            new Country("GG", "Guernsey", "+44-1481", R.drawable.flag_gg, "GGP"),
            new Country("GH", "Ghana", "+233", R.drawable.flag_gh, "GHS"),
            new Country("GI", "Gibraltar", "+350", R.drawable.flag_gi, "GIP"),
            new Country("GL", "Greenland", "+299", R.drawable.flag_gl, "DKK"),
            new Country("GM", "Gambia", "+220", R.drawable.flag_gm, "GMD"),
            new Country("GN", "Guinea", "+224", R.drawable.flag_gn, "GNF"),
            new Country("GQ", "Equatorial Guinea", "+240", R.drawable.flag_gq, "XAF"),
            new Country("GR", "Greece", "+30", R.drawable.flag_gr, "EUR"),
            new Country("GT", "Guatemala", "+502", R.drawable.flag_gt, "GTQ"),
            new Country("GU", "Guam", "+1-671", R.drawable.flag_gu, "USD"),
            new Country("GW", "Guinea-Bissau", "+245", R.drawable.flag_gw, "XOF"),
            new Country("GY", "Guyana", "+592", R.drawable.flag_gy, "GYD"),
            new Country("HK", "Hong Kong", "+852", R.drawable.flag_hk, "HKD"),
            new Country("HN", "Honduras", "+504", R.drawable.flag_hn, "HNL"),
            new Country("HR", "Croatia", "+385", R.drawable.flag_hr, "HRK"),
            new Country("HT", "Haiti", "+509", R.drawable.flag_ht, "HTG"),
            new Country("HU", "Hungary", "+36", R.drawable.flag_hu, "HUF"),
            new Country("ID", "Indonesia", "+62", R.drawable.flag_id, "IDR"),
            new Country("IE", "Ireland", "+353", R.drawable.flag_ie, "EUR"),
            new Country("IL", "Israel", "+972", R.drawable.flag_il, "ILS"),
            new Country("IM", "Isle of Man", "+44-1624", R.drawable.flag_im, "GBP"),
            new Country("IN", "India", "+91", R.drawable.flag_in, "INR"),
            new Country("IO", "British Indian Ocean Territory", "+246", R.drawable.flag_io, "USD"),
            new Country("IQ", "Iraq", "+964", R.drawable.flag_iq, "IQD"),
            new Country("IR", "Iran", "+98", R.drawable.flag_ir, "IRR"),
            new Country("IS", "Iceland", "+354", R.drawable.flag_is, "ISK"),
            new Country("IT", "Italy", "+39", R.drawable.flag_it, "EUR"),
            new Country("JE", "Jersey", "+44-1534", R.drawable.flag_je, "JEP"),
            new Country("JM", "Jamaica", "+1-876", R.drawable.flag_jm, "JMD"),
            new Country("JO", "Jordan", "+962", R.drawable.flag_jo, "JOD"),
            new Country("JP", "Japan", "+81", R.drawable.flag_jp, "JPY"),
            new Country("KE", "Kenya", "+254", R.drawable.flag_ke, "KES"),
            new Country("KG", "Kyrgyzstan", "+996", R.drawable.flag_kg, "KGS"),
            new Country("KH", "Cambodia", "+855", R.drawable.flag_kh, "KHR"),
            new Country("KI", "Kiribati", "+686", R.drawable.flag_ki, "AUD"),
            new Country("KM", "Comoros", "+269", R.drawable.flag_km, "KMF"),
            new Country("KN", "Saint Kitts and Nevis", "+1-869", R.drawable.flag_kn, "XCD"),
            new Country("KP", "North Korea", "+850", R.drawable.flag_kp, "KPW"),
            new Country("KR", "South Korea", "+82", R.drawable.flag_kr, "KRW"),
            new Country("KW", "Kuwait", "+965", R.drawable.flag_kw, "KWD"),
            new Country("KY", "Cayman Islands", "+1-345", R.drawable.flag_ky, "KYD"),
            new Country("KZ", "Kazakhstan", "+7", R.drawable.flag_kz, "KZT"),
            new Country("LA", "Laos", "+856", R.drawable.flag_la, "LAK"),
            new Country("LB", "Lebanon", "+961", R.drawable.flag_lb, "LBP"),
            new Country("LC", "Saint Lucia", "+1-758", R.drawable.flag_lc, "XCD"),
            new Country("LI", "Liechtenstein", "+423", R.drawable.flag_li, "CHF"),
            new Country("LK", "Sri Lanka", "+94", R.drawable.flag_lk, "LKR"),
            new Country("LR", "Liberia", "+231", R.drawable.flag_lr, "LRD"),
            new Country("LS", "Lesotho", "+266", R.drawable.flag_ls, "LSL"),
            new Country("LT", "Lithuania", "+370", R.drawable.flag_lt, "LTL"),
            new Country("LU", "Luxembourg", "+352", R.drawable.flag_lu, "EUR"),
            new Country("LV", "Latvia", "+371", R.drawable.flag_lv, "LVL"),
            new Country("LY", "Libya", "+218", R.drawable.flag_ly, "LYD"),
            new Country("MA", "Morocco", "+212", R.drawable.flag_ma, "MAD"),
            new Country("MC", "Monaco", "+377", R.drawable.flag_mc, "EUR"),
            new Country("MD", "Moldova", "+373", R.drawable.flag_md, "MDL"),
            new Country("ME", "Montenegro", "+382", R.drawable.flag_me, "EUR"),
            new Country("MF", "Saint Martin", "+590", R.drawable.flag_mf, "EUR"),
            new Country("MG", "Madagascar", "+261", R.drawable.flag_mg, "MGA"),
            new Country("MH", "Marshall Islands", "+692", R.drawable.flag_mh, "USD"),
            new Country("MK", "Macedonia", "+389", R.drawable.flag_mk, "MKD"),
            new Country("ML", "Mali", "+223", R.drawable.flag_ml, "XOF"),
            new Country("MM", "Myanmar", "+95", R.drawable.flag_mm, "MMK"),
            new Country("MN", "Mongolia", "+976", R.drawable.flag_mn, "MNT"),
            new Country("MO", "Macao", "+853", R.drawable.flag_mo, "MOP"),
            new Country("MP", "Northern Mariana Islands", "+1-670", R.drawable.flag_mp, "USD"),
            new Country("MR", "Mauritania", "+222", R.drawable.flag_mr, "MRO"),
            new Country("MS", "Montserrat", "+1-664", R.drawable.flag_ms, "XCD"),
            new Country("MT", "Malta", "+356", R.drawable.flag_mt, "EUR"),
            new Country("MU", "Mauritius", "+230", R.drawable.flag_mu, "MUR"),
            new Country("MV", "Maldives", "+960", R.drawable.flag_mv, "MVR"),
            new Country("MW", "Malawi", "+265", R.drawable.flag_mw, "MWK"),
            new Country("MX", "Mexico", "+52", R.drawable.flag_mx, "MXN"),
            new Country("MY", "Malaysia", "+60", R.drawable.flag_my, "MYR"),
            new Country("MZ", "Mozambique", "+258", R.drawable.flag_mz, "MZN"),
            new Country("NA", "Namibia", "+264", R.drawable.flag_na, "NAD"),
            new Country("NC", "New Caledonia", "+687", R.drawable.flag_nc, "XPF"),
            new Country("NE", "Niger", "+227", R.drawable.flag_ne, "XOF"),
            new Country("NG", "Nigeria", "+234", R.drawable.flag_ng, "NGN"),
            new Country("NI", "Nicaragua", "+505", R.drawable.flag_ni, "NIO"),
            new Country("NL", "Netherlands", "+31", R.drawable.flag_nl, "EUR"),
            new Country("NO", "Norway", "+47", R.drawable.flag_no, "NOK"),
            new Country("NP", "Nepal", "+977", R.drawable.flag_np, "NPR"),
            new Country("NR", "Nauru", "+674", R.drawable.flag_nr, "AUD"),
            new Country("NU", "Niue", "+683", R.drawable.flag_nu, "NZD"),
            new Country("NZ", "New Zealand", "+64", R.drawable.flag_nz, "NZD"),
            new Country("OM", "Oman", "+968", R.drawable.flag_om, "OMR"),
            new Country("PA", "Panama", "+507", R.drawable.flag_pa, "PAB"),
            new Country("PE", "Peru", "+51", R.drawable.flag_pe, "PEN"),
            new Country("PF", "French Polynesia", "+689", R.drawable.flag_pf, "XPF"),
            new Country("PG", "Papua New Guinea", "+675", R.drawable.flag_pg, "PGK"),
            new Country("PH", "Philippines", "+63", R.drawable.flag_ph, "PHP"),
            new Country("PK", "Pakistan", "+92", R.drawable.flag_pk, "PKR"),
            new Country("PL", "Poland", "+48", R.drawable.flag_pl, "PLN"),
            new Country("PM", "Saint Pierre and Miquelon", "+508", R.drawable.flag_pm, "EUR"),
            new Country("PN", "Pitcairn", "+64", R.drawable.flag_pn, "NZD"),
            new Country("PR", "Puerto Rico", "+1-939", R.drawable.flag_pr, "USD"),
            new Country("PS", "Palestinian", "+970", R.drawable.flag_ps, "ILS"),
            new Country("PT", "Portugal", "+351", R.drawable.flag_pt, "EUR"),
            new Country("PW", "Palau", "+680", R.drawable.flag_pw, "USD"),
            new Country("PY", "Paraguay", "+595", R.drawable.flag_py, "PYG"),
            new Country("QA", "Qatar", "+974", R.drawable.flag_qa, "QAR"),
            new Country("RE", "Reunion", "+262", R.drawable.flag_re, "EUR"),
            new Country("RO", "Romania", "+40", R.drawable.flag_ro, "RON"),
            new Country("RS", "Serbia", "+381", R.drawable.flag_rs, "RSD"),
            new Country("RU", "Russia", "+7", R.drawable.flag_ru, "RUB"),
            new Country("RW", "Rwanda", "+250", R.drawable.flag_rw, "RWF"),
            new Country("SA", "Saudi Arabia", "+966", R.drawable.flag_sa, "SAR"),
            new Country("SB", "Solomon Islands", "+677", R.drawable.flag_sb, "SBD"),
            new Country("SC", "Seychelles", "+248", R.drawable.flag_sc, "SCR"),
            new Country("SD", "Sudan", "+249", R.drawable.flag_sd, "SDG"),
            new Country("SE", "Sweden", "+46", R.drawable.flag_se, "SEK"),
            new Country("SG", "Singapore", "+65", R.drawable.flag_sg, "SGD"),
            new Country("SH", "Saint Helena", "+290", R.drawable.flag_sh, "SHP"),
            new Country("SI", "Slovenia", "+386", R.drawable.flag_si, "EUR"),
            new Country("SJ", "Svalbard and Jan Mayen", "+47", R.drawable.flag_sj, "NOK"),
            new Country("SK", "Slovakia", "+421", R.drawable.flag_sk, "EUR"),
            new Country("SL", "Sierra Leone", "+232", R.drawable.flag_sl, "SLL"),
            new Country("SM", "San Marino", "+378", R.drawable.flag_sm, "EUR"),
            new Country("SN", "Senegal", "+221", R.drawable.flag_sn, "XOF"),
            new Country("SO", "Somalia", "+252", R.drawable.flag_so, "SOS"),
            new Country("SR", "Suriname", "+597", R.drawable.flag_sr, "SRD"),
            new Country("SS", "South Sudan", "+211", R.drawable.flag_ss, "SSP"),
            new Country("ST", "Sao Tome and Principe", "+239", R.drawable.flag_st, "STD"),
            new Country("SV", "El Salvador", "+503", R.drawable.flag_sv, "SVC"),
            new Country("SX", "Sint Maarten", "+1-721", R.drawable.flag_sx, "ANG"),
            new Country("SY", "Syria", "+963", R.drawable.flag_sy, "SYP"),
            new Country("SZ", "Swaziland", "+268", R.drawable.flag_sz, "SZL"),
            new Country("TC", "Turks and Caicos Islands", "+1-649", R.drawable.flag_tc, "USD"),
            new Country("TD", "Chad", "+235", R.drawable.flag_td, "XAF"),
            new Country("TG", "Togo", "+228", R.drawable.flag_tg, "XOF"),
            new Country("TH", "Thailand", "+66", R.drawable.flag_th, "THB"),
            new Country("TJ", "Tajikistan", "+992", R.drawable.flag_tj, "TJS"),
            new Country("TK", "Tokelau", "+690", R.drawable.flag_tk, "NZD"),
            new Country("TL", "East Timor", "+670", R.drawable.flag_tl, "USD"),
            new Country("TM", "Turkmenistan", "+993", R.drawable.flag_tm, "TMT"),
            new Country("TN", "Tunisia", "+216", R.drawable.flag_tn, "TND"),
            new Country("TO", "Tonga", "+676", R.drawable.flag_to, "TOP"),
            new Country("TR", "Turkey", "+90", R.drawable.flag_tr, "TRY"),
            new Country("TT", "Trinidad and Tobago", "+1-868", R.drawable.flag_tt, "TTD"),
            new Country("TV", "Tuvalu", "+688", R.drawable.flag_tv, "AUD"),
            new Country("TW", "Taiwan", "+886", R.drawable.flag_tw, "TWD"),
            new Country("TZ", "Tanzania", "+255", R.drawable.flag_tz, "TZS"),
            new Country("UA", "Ukraine", "+380", R.drawable.flag_ua, "UAH"),
            new Country("UG", "Uganda", "+256", R.drawable.flag_ug, "UGX"),
            new Country("US", "United States", "+1", R.drawable.flag_us, "USD"),
            new Country("UY", "Uruguay", "+598", R.drawable.flag_uy, "UYU"),
            new Country("UZ", "Uzbekistan", "+998", R.drawable.flag_uz, "UZS"),
            new Country("VA", "Vatican", "+379", R.drawable.flag_va, "EUR"),
            new Country("VC", "Saint Vincent and the Grenadines", "+1-784", R.drawable.flag_vc, "XCD"),
            new Country("VE", "Venezuela", "+58", R.drawable.flag_ve, "VEF"),
            new Country("VG", "British Virgin Islands", "+1-284", R.drawable.flag_vg, "USD"),
            new Country("VI", "U.S. Virgin Islands", "+1-340", R.drawable.flag_vi, "USD"),
            new Country("VN", "Vietnam", "+84", R.drawable.flag_vn, "VND"),
            new Country("VU", "Vanuatu", "+678", R.drawable.flag_vu, "VUV"),
            new Country("WF", "Wallis and Futuna", "+681", R.drawable.flag_wf, "XPF"),
            new Country("WS", "Samoa", "+685", R.drawable.flag_ws, "WST"),
            new Country("XK", "Kosovo", "+383", R.drawable.flag_xk, "EUR"),
            new Country("YE", "Yemen", "+967", R.drawable.flag_ye, "YER"),
            new Country("YT", "Mayotte", "+262", R.drawable.flag_yt, "EUR"),
            new Country("ZA", "South Africa", "+27", R.drawable.flag_za, "ZAR"),
            new Country("ZM", "Zambia", "+260", R.drawable.flag_zm, "ZMW"),
            new Country("ZW", "Zimbabwe", "+263", R.drawable.flag_zw, "USD"),
    };
    // endregion

    private GoogleApiClient mCredentialsApiClient;
    private ArrayList<Country> countries = new ArrayList<>();
    private SweetAlertDialog progressDialog;
    private boolean toCreateFamily = false;
    private boolean isExistingUser = false;
    private boolean isDynamicLink = false;
    private String joinFamilyId = "";

    @BindView(R.id.countrySelector)
    EditText countrySelector;
    @BindView(R.id.arrowDown)
    ImageView arrowDown;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.countryCode)
    EditText countryCode;
    @BindView(R.id.countryFlag)
    ImageView countryFlag;


    @BindView(R.id.termsAndConditions)
    AutoLinkTextView termsAndConditions;
    @BindView(R.id.continu)
    Button continu;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.labelConfirmNumber)
    TextView labelConfirmNumber;
    @BindView(R.id.labelIsExisting)
    TextView labelIsExisting;


    String lastChar = " ";
    String phoneString = "";
    String countryCodeString="";
    int countryFlags;

    private String otpReceived = null;
    private String mobileNumber = null;
    private String userId = null;
    TextWatcher telephoneTextChangeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            labelIsExisting.setVisibility( View.GONE );
            int digits = phone.getText().toString().length();
            if (digits > 1)
                lastChar = phone.getText().toString().substring(digits - 1);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            int digits = phone.getText().toString().length();
            if (!lastChar.equals("-")) {
                if (digits == 3 || digits == 7) {
                    phone.append("-");
                }
            }

            if (digits > 7) {
                continu.setBackgroundTintList( ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                continu.setEnabled(true);

            } else {
                continu.setEnabled(false);
                continu.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.quantum_grey300)));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        ButterKnife.bind(this);
        toCreateFamily=getIntent().getBooleanExtra(Constants.Bundle.TO_CREATE_FAMILY, false);
        joinFamilyId=getIntent().getStringExtra( JOIN_FAMILY_ID);
        isExistingUser=getIntent().getBooleanExtra( Constants.Bundle.IS_EXISTING_USER, false);
        isDynamicLink=getIntent().getBooleanExtra( Constants.Bundle.IS_DYNAMIC, false);
        if(isDynamicLink){
            back.setVisibility(View.GONE);
        }
        if(isExistingUser){
            labelConfirmNumber.setText( "Confirm your number" );
        }
        else
        {
            labelConfirmNumber.setText( "Sign Up" );
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            countryCodeString = bundle.getString( "CountryCode", "" );
            phoneString = bundle.getString( "Phone", "" );
            countryFlags = getIntent().getIntExtra( "countryFlag", 0 );
        }
        if(countryCodeString=="" && phoneString==""){
            requestHint();
        }
        else{
            phone.setText( phoneString );
            countryCode.setText( "("+countryCodeString+")" );
            countryFlag.setImageResource(countryFlags);
            countrySelector.setText( getIntent().getStringExtra("countryName" ));
            continu.setBackgroundTintList( ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            continu.setEnabled(true);

        }
        initialize();

    }

    private void initialize() {
        Locale locale = Locale.getDefault();
        Utilities.attachEmptyListener(countrySelector);
        Utilities.makeEditTextClickOnly(countrySelector);

        TelephonyManager tm = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();


        countries = new ArrayList<>( Arrays.asList(COUNTRIES));
        sortCountries(countries);
        if(countryCodeString=="" && phoneString=="") {
            for (int i = 0; i < countries.size(); i++) {
                Country c = countries.get( i );
                if (countryCodeValue!=""){
                    if (countryCodeValue.toLowerCase().equalsIgnoreCase( c.getCountryCode().toLowerCase())) {
                        countrySelector.setText( c.getCountryName() );
                        countryCode.setText( "(" + c.getDialCode() + ")" );
                        countryCodeString = c.getDialCode();
                        countryFlag.setImageResource( c.getFlag() );
                        countryFlags = c.getFlag();
                    }
                }
                else{
                    if (locale.getCountry().equalsIgnoreCase( c.getCountryCode())) {
                        countrySelector.setText( c.getCountryName() );
                        countryCode.setText( "(" + c.getDialCode() + ")" );
                        countryCodeString = c.getDialCode();
                        countryFlag.setImageResource( c.getFlag() );
                        countryFlags = c.getFlag();
                    }
                }
            }
        }
        phone.addTextChangedListener(telephoneTextChangeWatcher);
        back.setOnClickListener(view -> {
            onBackPressed();
        });
        continu.setOnClickListener(view -> {
            phoneString = phone.getText().toString().trim();
            phoneString = phoneString.replaceAll("-", "");

            phoneString = phoneString.substring(phoneString.lastIndexOf(")") + 1);
            if (validate())
             //   verifyGoogleReCAPTCHA();
              //  validatePhoneNumber();
                validatePhoneNumber();

        });

        arrowDown.setOnClickListener(v -> countrySelector.callOnClick());
        countrySelector.setOnClickListener(v -> CountryCodeDialogFragment.newInstance(countries).show(getSupportFragmentManager(), "CountryCodeDialogFragment"));
        termsAndConditions.setCustomModeColor( ContextCompat.getColor(getApplicationContext(), R.color.black));
        termsAndConditions.addAutoLinkMode( AutoLinkMode.MODE_CUSTOM);
        termsAndConditions.setCustomRegex("\\bterms and conditions\\b | \\bprivacy policy\\b");
        termsAndConditions.setAutoLinkText(getString(R.string.terms1_5));
        termsAndConditions.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            Intent httpIntent = new Intent(Intent.ACTION_VIEW);
            if (matchedText.trim().equalsIgnoreCase("terms and conditions")) {
                httpIntent.setData( Uri.parse(PRIVACY_URL + "termsofservice"));
            } else {
                httpIntent.setData(Uri.parse(PRIVACY_URL + "policy"));

            }
            startActivity(httpIntent);
        });
    }
    private void sortCountries(@NonNull List<Country> countries) {
        Collections.sort(countries, new Comparator<Country>() {
            @Override
            public int compare(Country country1, Country country2) {
                return country1.getCountryName().trim().compareToIgnoreCase(country2.getCountryName().trim());
            }
        });
    }
    private boolean validate() {
        boolean isValid = true;
        if (countryCode.length() == 0) {
            isValid = false;
            Toast.makeText(this, "Please select a country", Toast.LENGTH_SHORT).show();
        }
        if (phoneString.length() < 7) {
            isValid = false;
            phone.setError("Required");
        }
        return isValid;
    }
    private LoginRequest generateLoginRequest() {
        String phoneNo=phoneString.trim();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setData();
        loginRequest.setCountryCode(countryCodeString);
        char first = phoneNo.charAt(0);
        String s=String.valueOf(first);
        if(s.equals("0"))
            phoneNo=phoneNo.substring(1);
        loginRequest.setMobileNumber(phoneNo);
        loginRequest.setDevice_unique_id(Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID));
        loginRequest.setDevice(Utilities.getDeviceName());
        if(isExistingUser)
            loginRequest.setIsExisting( "0" );
        else
            loginRequest.setIsExisting( "1" );
        return loginRequest;
    }
    private void registerUser() {
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.registerUser(generateLoginRequest(), null, this);
    }

    @Override
    public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {
        progressDialog.dismiss();
        /*JSONObject jObject = null;
        try {
            jObject = new JSONObject(responseBodyString);
            JSONObject mobileData = jObject.getJSONObject("data");
            userId = String.valueOf(mobileData.getInt("id"));
            String message = jObject.getString("message");
            if (message.equalsIgnoreCase("user is blocked")) {
                DialogReverifyUser(userId);
                //Toast.makeText(this, "Sorry.. you cannot request for OTP more than 2 times in 12 hours", Toast.LENGTH_LONG).show();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }*/
        switch (apiFlag) {
            case VERIFIED_MOBILE_NUMBER:
                verifyGoogleReCAPTCHA();
                break;
            case VALIDATE_MOBILE_NUMBER:
                /**ticket 693**/
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(responseBodyString);

                    String message = jObject.getString("message");
                    if (message.equalsIgnoreCase("user is blocked")) {
                        JSONObject mobileData = jObject.getJSONObject("data");
                        userId = String.valueOf(mobileData.getInt("id"));
                        DialogReverifyUser(userId,mobileData.getBoolean("allow_reverification"),mobileData.getBoolean("user_is_blocked"));
                        break;
                        //Toast.makeText(this, "Sorry.. you cannot request for OTP more than 2 times in 12 hours", Toast.LENGTH_LONG).show();
                    }
                    if(message.equalsIgnoreCase("api limit exceeded")){
                        progressDialog = Utilities.getProgressDialog(this);
                        progressDialog.show();
                        Utilities.getErrorDialog(progressDialog, "You have exceeded the maximum number of requests per day");
                   break;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                        Gson gson = new Gson();
                        UserRegistrationResponse userRegistrationResponse = gson.fromJson( responseBodyString, UserRegistrationResponse.class );
                        if(userRegistrationResponse.getStatusCode()==null){
                            labelIsExisting.setVisibility( View.GONE );
                            proceedToOTPVerification( userRegistrationResponse );
                        }
                        else{
                            labelIsExisting.setVisibility( View.VISIBLE );
                        }
                        break;

            case MOBILE_NUMBER_DETAILS :  JSONObject jObject1 = null;
                try {
                    jObject1 = new JSONObject(responseBodyString);
                    String message = jObject1.getString("message");
                    if(message.equalsIgnoreCase("otp limit exceeded")) {
                        Toast.makeText(this, "Your OTP limit has exceeded. Please try again after 12 hours.", Toast.LENGTH_LONG).show();
                        break;
                    }
                        JSONObject mobileData = jObject1.getJSONObject("data");
                        otpReceived = mobileData.getString("otp");
                        mobileNumber = mobileData.getString("phone");
                        Intent intent = new Intent(getApplicationContext(), OTPVerificationActivity.class);
                        intent.putExtra("otpReceived",otpReceived );
                        intent.putExtra("mobileNumber",mobileNumber );
                        startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //end of modification for ticket 693 
       /* *//** ticket 693 to check whether a user is blocked or not on click of "continue" button **//*
            case MOBILE_NUMBER_DETAILS :*/
        }
    }

    @Override
    public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
        switch (apiFlag) {
            case VERIFIED_MOBILE_NUMBER:
                progressDialog.dismiss();
                if (errorData.getCode() == Constants.ErrorCodes.INTERNAL_ERROR && errorData.getMessage().equalsIgnoreCase("invalid")) {
                    Toast.makeText(LoginActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                }
                break;
            case VALIDATE_MOBILE_NUMBER:
                 if (errorData.getCode() == Constants.ErrorCodes.INTERNAL_ERROR && errorData.getMessage().equalsIgnoreCase(Constants.ErrorNames.USER_ALREADY_EXISTS)) {
            progressDialog.dismiss();
            } else if(errorData.getCode() == Constants.ErrorCodes.INTERNAL_ERROR && errorData.getMessage().equalsIgnoreCase(Constants.ErrorNames.UPDATE_REQUIRED)){
                     Utilities.getErrorDialog(progressDialog, Constants.UPDATE_APP);
                     }
                 else if(errorData.getMessage().equalsIgnoreCase("device is blocked")){
                     Utilities.getErrorDialog(progressDialog, "Your device has been blocked");
                 }
                 else {
                     Utilities.getErrorDialog(progressDialog, Constants.SOMETHING_WRONG);
                 }
                break;
        }
    }
    /**ticket 693**/
    private void DialogReverifyUser(String id,boolean allow_reverification,boolean user_is_blocked) {
        //progressBar.setVisibility(View.INVISIBLE);
        if(!allow_reverification && user_is_blocked){
            final Dialog dialog = new Dialog(this);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialogue_user_block);
            com.luseen.autolinklibrary.AutoLinkTextView textView = dialog.findViewById(R.id.txt_msg);
            textView.addAutoLinkMode(AutoLinkMode.MODE_EMAIL);
            textView.setEmailModeColor(ContextCompat.getColor(this, R.color.buttoncolor));
            dialog.setCanceledOnTouchOutside(false);
            textView.setAutoLinkText("Sorry, your account have been suspended. Please email us on contact@familheey.com to reactivate your account.");

            textView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{matchedText});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Reactivate Account - " + id);
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));
                finish();
            });

            dialog.findViewById(R.id.btn_close).setOnClickListener(view -> dialog.dismiss());
            dialog.findViewById(R.id.btn_login).setOnClickListener(view -> {
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), NewUserWelcomeActivity.class));
                finish();
            });
            dialog.show();
        }
        else{
            final Dialog dialog = new Dialog(this);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_user_reverification);

            com.luseen.autolinklibrary.AutoLinkTextView textView = dialog.findViewById(R.id.txt_msg);
            dialog.setCanceledOnTouchOutside(false);
            textView.setText("Your account has been suspended due to malicious behaviour detected.You need to verify your account to continue with Familheey App.");

            dialog.findViewById(R.id.btn_verify).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDetailsForVerification(id);
                }
            });
            dialog.show();
        }


    }
    private void getDetailsForVerification(String id) {
        JsonObject jsonObject = new JsonObject();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(this);
        jsonObject.addProperty("user_id", id);
        apiServiceProvider.getMobileDetailsFromUserId(jsonObject,null,this);
    }
    /** end **/
    private void proceedToOTPVerification(UserRegistrationResponse userRegistrationResponse) {
        if (userRegistrationResponse == null)
            userRegistrationResponse = new UserRegistrationResponse();
        Intent intent = new Intent(getApplicationContext(), OTPVerificationActivity.class);
        userRegistrationResponse.setCapturedCountryCode(countryCodeString);
        userRegistrationResponse.setCapturedMobileNumber(phoneString);
        intent.putExtra(Constants.Bundle.DATA, userRegistrationResponse);
        intent.putExtra(TO_CREATE_FAMILY, toCreateFamily);
        intent.putExtra(JOIN_FAMILY_ID, joinFamilyId);
        intent.putExtra(Constants.Bundle.IS_EXISTING_USER, isExistingUser);
        intent.putExtra("countryFlag",countryFlags );
        intent.putExtra("countryName",countrySelector.getText().toString());
        startActivity(intent);
    }
    private void requestHint() {
        try {
            mCredentialsApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .enableAutoManage(this, this)
                    .addApi( Auth.CREDENTIALS_API)
                    .build();
            HintRequest hintRequest = new HintRequest.Builder()
                    .setHintPickerConfig(new CredentialPickerConfig.Builder().setShowCancelButton(true).build())
                    .setPhoneNumberIdentifierSupported(true)
                    .build();

            PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                    mCredentialsApiClient, hintRequest);
            startIntentSenderForResult(intent.getIntentSender(),
                    599, null, 0, 0, 0);
        } catch (Exception e) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 599) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                assert credential != null;
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(this);
                try {
                    Phonenumber.PhoneNumber numberProto = phoneUtil.parse(credential.getId(), "");
                    for (int i = 0; i < countries.size(); i++) {
                        Country c = countries.get(i);
                        String code = "+" + numberProto.getCountryCode();
                        if (code.equals(c.getDialCode())) {
                            phone.setText(numberProto.getNationalNumber() + "");
                            countryCode.setText( "("+c.getDialCode()+")" );
                            countryCodeString=c.getDialCode();
                            countrySelector.setText(c.getCountryName());
                            countryFlag.setImageResource(c.getFlag());
                            countryFlags=c.getFlag();
                        }
                    }


                } catch (NumberParseException e) {

//                    String wc = credential.getId().replace(countryCode.getText().toString().trim(), "");
//                    phone.setText(wc);
                }
                continu.performClick();
            }
        }
    }

    @Override
    public void OnCountrySelected(Country selectedCountry) {
        phone.setText("");
        countrySelector.setText(selectedCountry.getCountryName());
        countryCode.setText( "("+selectedCountry.getDialCode()+")" );
        countryFlag.setImageResource(selectedCountry.getFlag());
        countryFlags=selectedCountry.getFlag();
        countryCodeString=selectedCountry.getDialCode();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), NewUserWelcomeActivity.class));
        finish();
    }

    private void verifyGoogleReCAPTCHA() {

        // below line is use for getting our safety
        // net client and verify with reCAPTCHA
        SafetyNet.getClient(this).verifyWithRecaptcha(getString(R.string.sitekey))
                // after getting our client we have
                // to add on success listener.
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        // in below line we are checking the response token.
                        if (!response.getTokenResult().isEmpty()) {
                            // if the response token is not empty then we
                            // are calling our verification method.
                            //   handleVerification(response.getTokenResult());
                            registerUser();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // this method is called when we get any error.
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            // below line is use to display an error message which we get.
                            Log.d("TAG", "Error message: " +
                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            // below line is use to display a toast message for any error.
                            Toast.makeText(LoginActivity.this, "Error found is : " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void validatePhoneNumber() {
        progressDialog = Utilities.getProgressDialog(this);
        progressDialog.show();
        ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getApplicationContext());
        apiServiceProvider.validatePhoneNumber(generateLoginRequest(), null, this);
    }


}
