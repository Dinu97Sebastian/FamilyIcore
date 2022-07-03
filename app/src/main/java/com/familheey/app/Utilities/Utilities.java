package com.familheey.app.Utilities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.familheey.app.Activities.SubscriptionDetailActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.HomeInteractor;
import com.familheey.app.Models.Response.Document;
import com.familheey.app.R;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class Utilities {

    public static SweetAlertDialog getProgressDialog(Context context) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#27323e"));
        pDialog.setCancelable(false);
        return pDialog;
    }


    public static SweetAlertDialog confirmCancel(Context context) {
        return new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Do you want to edit the event!")
                .setConfirmText("Yes")
                .setCancelText("No");
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showCircularReveal(View myView) {
        // Check if the runtime version is at least Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = myView.getWidth() / 2;
            int cy = myView.getHeight() / 2;

            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius);

            // make the view visible and start the animation
            myView.setVisibility(View.VISIBLE);
            anim.start();
        } else myView.setVisibility(View.VISIBLE);
    }


    public static void hideCircularReveal(ConstraintLayout myView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = myView.getWidth() / 2;
            int cy = myView.getHeight() / 2;

            float initialRadius = (float) Math.hypot(cx, cy);

            Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0f);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.GONE);
                }
            });

            anim.start();
        }
    }


    public static SweetAlertDialog deleteConfirmCancel(Context context) {
        return new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Do you want to delete the agenda ?")
                .setConfirmText("Yes")
                .setCancelText("No");
    }

    public static SweetAlertDialog getErrorDialog(Context context, String content) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#27323e"));
        pDialog.setTitleText("Error");
        pDialog.setContentText(content);
        pDialog.setCancelable(false);
        pDialog.setConfirmButton("ok", SweetAlertDialog::dismissWithAnimation);
        return pDialog;
    }


    public static SweetAlertDialog getErrorDialog(SweetAlertDialog progressDialog, String content) {
        progressDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        // progressDialog.setTitleText("Error");
        progressDialog.setContentText(content);
        progressDialog.setCancelable(false);
        progressDialog.setConfirmButton("Ok", SweetAlertDialog::dismissWithAnimation);
        return progressDialog;
    }


    public static SweetAlertDialog getErrorDialogWithoutTitle(SweetAlertDialog progressDialog, String content) {
        progressDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        progressDialog.setTitleText(content);
//        progressDialog.setContentText(content);
        progressDialog.setCancelable(false);
        progressDialog.setConfirmButton("Ok", SweetAlertDialog::dismissWithAnimation);
        return progressDialog;
    }


    public static SweetAlertDialog succesDialog(Context context, String content) {
        SweetAlertDialog progressDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#27323e"));
        progressDialog.setCancelable(false);
        progressDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        progressDialog.setTitleText("Success");
        progressDialog.setContentText(content);
        progressDialog.setCancelable(false);
        progressDialog.setConfirmButton("Ok", SweetAlertDialog::dismissWithAnimation);
        return progressDialog;
    }

    public static SweetAlertDialog getContentNotFoundDialog(Context context) {
        return new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops!")
                .setContentText("This content is no longer available")
                .setConfirmText("OK");
    }

    public static SweetAlertDialog getContentNotFoundDialog(Context context, String msg) {
        return new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Oops!")
                .setContentText(msg)
                .setConfirmText("OK");
    }

    public static boolean isSizeLess(Context context, Uri uri) {

        File file = FileUtil.getFile(context, uri);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));

        if (file_size > 204800) {
            Toast.makeText(context, "Please upload video of size less than 200 mb", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    public static void attachEmptyListener(EditText... editText) {
        for (int i = 0; i < editText.length; i++) {
            int finalI = i;
            editText[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() > 0)
                        editText[finalI].setError(null);
                    else editText[finalI].setError("Required");
                }
            });
        }
    }

    public static void makeEditTextClickOnly(EditText... editTexts) {
        for (int i = 0; i < editTexts.length; i++) {
            editTexts[i].setFocusable(false);
            editTexts[i].setClickable(true);
            editTexts[i].setLongClickable(false);
        }
    }



    @Nullable
    public static <T> T getListener(@NonNull Fragment fragment, @NonNull Class<T> listenerClass) {
        T listener = null;
        if (listenerClass.isInstance(fragment.getParentFragment())) {
            listener = listenerClass.cast(fragment.getParentFragment());
        } else if (listenerClass.isInstance(fragment.getActivity())) {
            listener = listenerClass.cast(fragment.getActivity());
        }
        return listener;
    }


    public static RequestOptions getCurvedRequestOptions() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(32));
        return requestOptions;
    }


    public static RequestOptions getCurvedRequestOptionsSmall() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        return requestOptions;
    }

    public static DrawableCrossFadeFactory getDrawableCrossFadeFactory() {
        return new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
    }



    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }

    public static void addImageToMultiPartBuilder(MultipartBody.Builder builder, Uri imageUri, String keyname) {
        if (imageUri == null)
            return;
        File imageFile = FileUtil.getFile(FamilheeyApplication.getInstance().getApplicationContext(), imageUri);
        final MediaType MEDIA_TYPE = imageUri.toString().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
        try {
            File compressedImageFile = new Compressor(FamilheeyApplication.getInstance().getApplicationContext())
                    .setQuality(50)
                    .compressToFile(imageFile);
            if (compressedImageFile != null)
                imageFile = compressedImageFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody profilePictureRequestBody = RequestBody.create(MEDIA_TYPE, imageFile);
        builder.addFormDataPart(keyname, imageFile.getName(), profilePictureRequestBody);
    }

    public static void removeEditableButClickable(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setLongClickable(false);
            editText.setFocusable(false);
            editText.setClickable(true);
        }
    }


    public static String getS3ImageUrl(String url, int width, int height) {
        return Constants.ApiPaths.IMAGE_BASE_URL + "width=" + width + "&height=" + height + "&gravity=smart&url=" + url;
    }

    public static String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = FamilheeyApplication.getInstance().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }


    public static String capitalizeFirstLetter(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }
        return capMatcher.appendTail(capBuffer).toString();
    }

    public static void downloadDocuments(Context context, String url, String fileName) {

        /*File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Familheey");
        boolean success = true;
        if (!file.exists()) {
            success = file.mkdir();
        }*/

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        /*if(success){
            request.setDestinationUri(Uri.fromFile(file));
        }*/
        request.setTitle("Familheey");
        request.setDescription("Downloading " + fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setMimeType(getMimeFromFileName(fileName));
        assert downloadManager != null;
        long downloadID = downloadManager.enqueue(request);
    }

    private static String getMimeFromFileName(String fileName) {

        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(fileName.replace(" ", ""));
        return map.getMimeTypeFromExtension(ext);
    }

    public static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }


    public static JsonObject getDefaultEventSearchJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("query", "");
        return jsonObject;
    }

    public static void showMainMenuPopup(Context context, ImageView imgMore) {
        PopupMenu popup = new PopupMenu(context, imgMore);
        popup.getMenuInflater().inflate(R.menu.main_navigation_more, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.feedback) {
                HomeInteractor homeInteractor = ((HomeInteractor) context);
                homeInteractor.loadFeedback();
            }
            if (item.getItemId() == R.id.calendar) {
                HomeInteractor homeInteractor = ((HomeInteractor) context);
                homeInteractor.loadCalender();
            }
            if (item.getItemId() == R.id.announcement) {
                HomeInteractor homeInteractor = ((HomeInteractor) context);
                homeInteractor.loadAnnouncement();
            }
            if (item.getItemId() == R.id.itemSubscription) {
                context.startActivity(new Intent(context, SubscriptionDetailActivity.class));
            }
            //Dinu(05/07/2021) for remove quickTour
//            if (item.getItemId() == R.id.quickTour) {
//                HomeInteractor homeInteractor = ((HomeInteractor) context);
//                homeInteractor.loadSpotlight();
//            }
            return true;
        });
        popup.show();
    }


    public static String getFormattedDate(String createdAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        TimeZone tzInAmerica = TimeZone.getTimeZone("IST");
        sdf.setTimeZone(tzInAmerica);
        try {
            Date date = sdf.parse(createdAt);
            PrettyTime p = new PrettyTime();
            String re = p.format(date);
            if (re.contains("moment")) {
                return "36s";
            } else {
                String[] arr = re.split(" ");
                if (arr[1].contains("minute")) {
                    return arr[0] + "m";
                } else if (arr[1].contains("hour")) {
                    return arr[0] + "h";
                } else if (arr[1].contains("day")) {
                    return arr[0] + "d";
                } else if (arr[1].contains("week")) {
                    return arr[0] + "w";
                } else if (arr[1].contains("month")) {
                    return arr[0] + "mo";
                } else if (arr[1].contains("year")) {
                    return arr[0] + "y";
                }
            }

            return re;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0 m";
    }

    public static String getLocalDateFromTimestamp(String timeStamp) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        TimeZone tzInAmerica = TimeZone.getTimeZone("IST");
        sdf.setTimeZone(tzInAmerica);
        Date neededDate = sdf.parse(timeStamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy hh:mm aa", Locale.getDefault());
        return dateFormat.format(neededDate);
    }

    public static void loadProfilePicture(ImageView imageView) {
        Glide.with(FamilheeyApplication.getInstance().getApplicationContext())
                .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + SharedPref.getUserRegistration().getPropic())
                .apply(Utilities.getCurvedRequestOptions())
                .placeholder(R.drawable.avatar_male)
                .into(imageView);
    }

    public static void setEditTextHorizontallyScrollable(Context context, EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setScroller(new Scroller(context));
            editText.setMaxLines(1);
            editText.setHorizontallyScrolling(true);
            editText.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    public static void changeTint(Context context, boolean isSelected, View... views) {
        for (View view : views) {
            if (view instanceof ImageView) {
                if (isSelected)
                    ImageViewCompat.setImageTintList((ImageView) view, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.greenTextColor)));
                else
                    ImageViewCompat.setImageTintList((ImageView) view, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.titleColor)));
            } else {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (isSelected)
                        ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.greenTextColor));
                    else
                        ((TextView) view).setTextColor(ContextCompat.getColor(context, R.color.titleColor));
                } else {
                    if (isSelected)
                        ((TextView) view).setTextColor(context.getResources().getColor(R.color.greenTextColor));
                    else
                        ((TextView) view).setTextColor(context.getResources().getColor(R.color.titleColor));
                }
            }
        }
    }


    public static List<Long> getDatesBetween(Long fromDate, Long toDate) {
        if (fromDate != null && toDate != null) {
            DateTime fromDateTime = new DateTime(TimeUnit.SECONDS.toMillis(fromDate));
            DateTime toDateTime = new DateTime(TimeUnit.SECONDS.toMillis(toDate));
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd");
            int numberOfDays = Days.daysBetween(LocalDate.parse(dtfOut.print(fromDateTime).trim()), LocalDate.parse(dtfOut.print(toDateTime).trim())).getDays();
            ArrayList<Long> dates = new ArrayList<Long>();
            dates.add(fromDateTime.getMillis());
            if (numberOfDays != 0) {
                for (int i = 1; i <= numberOfDays; i++) {
                    dates.add(fromDateTime.plusDays(i).getMillis());
                }
            }

            return dates;
        } else return new ArrayList<>();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static void addPositiveButtonMargin(SweetAlertDialog sweetAlertDialog) {
        Button confirmButton = sweetAlertDialog.findViewById(R.id.confirm_button);
        if (confirmButton == null)
            return;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 0, 16);
        confirmButton.setLayoutParams(layoutParams);
    }

    public static void addNegativeButtonMargin(SweetAlertDialog sweetAlertDialog) {
        Button confirmButton = sweetAlertDialog.findViewById(R.id.cancel_button);
        if (confirmButton == null)
            return;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 16, 0, 16);
        confirmButton.setLayoutParams(layoutParams);
    }

    public static boolean isCoverImageSelected(List<Document> documents, List<Long> selectedDocumentId, String coverPic) {
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getUrl().equalsIgnoreCase(coverPic) && selectedDocumentId.contains(documents.get(i).getId())) {
                return true;
            }
        }
        return false;
    }

    public static String getFirstLetterCapitalizedWordInSentence(String sentence) {
        if (sentence == null)
            return "";
        String[] strArray = sentence.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }


    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Boolean isValidURL(String urlString) {
        try {
            URL url = new URL(urlString);
            return URLUtil.isValidUrl(url.toString()) && Patterns.WEB_URL.matcher(url.toString()).matches();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setClipboard(Context context, String text) {
        if (TextUtils.isEmpty(text))
            return;
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "PIN copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public static void setClipboard(Context context, String text, String msg) {
        if (TextUtils.isEmpty(text))
            return;
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
