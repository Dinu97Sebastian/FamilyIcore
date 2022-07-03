package com.familheey.app.Announcement;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.familheey.app.Activities.ChatActivity;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Activities.ShareEventActivity;
import com.familheey.app.Activities.SharelistActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Post.PostCommentActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.SUB_TYPE;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class AnnouncementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<PostData> repositories;
    private final int ITEM = 0;
    private final int SHARE = 1;
    private final int HEAD = 2;
    int unreadCount = 0;
    int readCount = 0;
    private RequestOptions requestOptions;
    private CompositeDisposable subscriptions;
    private OnAnnouncementSelectedListener listener;

    public AnnouncementAdapter(List<PostData> repositories, OnAnnouncementSelectedListener listener) {
        this.repositories = repositories;
        this.listener = listener;
        subscriptions = new CompositeDisposable();
        requestOptions = new RequestOptions();
        requestOptions.transforms(new RoundedCorners(16));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.item_announcement_detailed, parent, false);
            return new ViewHolderOne(listItem);
        } else if (viewType == HEAD) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.item_announcement_header, parent, false);
            return new ViewHolderHeader(listItem);
        } else {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.item_announcement_detailed_share, parent, false);
            return new ViewHolderTwo(listItem);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        this.context = holder.itemView.getContext();
        PostData postData = repositories.get(position);
        String id = postData.getCreated_by() + "";
        switch (getItemViewType(position)) {

            case HEAD:
                ViewHolderHeader head = (ViewHolderHeader) holder;
                if (postData.getPost_ref_id().equals("UNREAD"))
                    head.txt_header.setText("Unread Announcements");
                else
                    head.txt_header.setText("Past Announcements");
                break;
            case ITEM:
                ViewHolderOne holderOne = (ViewHolderOne) holder;
                Glide.with(holder.itemView.getContext())
                        .load(IMAGE_BASE_URL + Constants.Paths.LOGO + postData.getFamily_logo())
                        .placeholder(R.drawable.family_dashboard_background)
                        .apply(requestOptions).into(holderOne.userProfileImage);

                holderOne.userName.setText(postData.getGroup_name());
                holderOne.postedin.setText("Posted by " + postData.getCreated_user_name());
                holderOne.txt_date.setText(dateFormat(postData.getCreatedAt()));
                holderOne.txt_chat_count.setText(postData.getConversation_count());
                holderOne.txt_view_count.setText(postData.getViews_count());

                if (repositories.get(position).getRead_status() != null && postData.getRead_status()) {
                    holderOne.txt_des.setTypeface(null, Typeface.NORMAL);
                } else {
                    holderOne.txt_des.setTypeface(null, Typeface.BOLD);
                }


                holderOne.txt_des.setText(postData.getSnap_description());


                if (postData.getConversation_enabled()) {
                    holderOne.btn_conversation.setVisibility(View.VISIBLE);
                    holderOne.txt_chat_count.setVisibility(View.VISIBLE);
                } else {

                    holderOne.txt_chat_count.setVisibility(View.GONE);
                    holderOne.btn_conversation.setVisibility(View.GONE);
                }


                holderOne.btn_more.setOnClickListener(v -> {
                    if (id.equals(SharedPref.getUserRegistration().getId())) {
                        showMenusOwner(v, position);
                    } else {
                        showMenusNormalUser(v, position);
                    }
                });

                if (postData.is_shareable) {
                    holderOne.btn_share.setVisibility(View.VISIBLE);
                } else {
                    holderOne.btn_share.setVisibility(View.GONE);
                }
                holderOne.btn_conversation.setOnClickListener(v -> context.startActivity(new Intent(context, PostCommentActivity.class)
                        .putExtra(DATA, postData)
                        .putExtra("POS", position)
                        .putExtra(TYPE, "")
                        .putExtra(SUB_TYPE, "ANNOUNCEMENT")));

                ((Activity) holder.itemView.getContext()).overridePendingTransition(R.anim.enter,
                        R.anim.exit);
                holderOne.btn_share.setOnClickListener(v ->
                        {
                            addViewCount(repositories.get(position).getPost_id() + "");
                            context.startActivity(new Intent(context, ShareEventActivity.class).putExtra(Constants.Bundle.TYPE, "ANNOUNCEMENT").putExtra("Post_id", repositories.get(position).getPost_id() + ""));
                            ((Activity) holder.itemView.getContext()).overridePendingTransition(R.anim.enter,
                                    R.anim.exit);
                        }
                );

                holderOne.btn_view.setOnClickListener(v -> {
                    if (Integer.parseInt(postData.getViews_count()) > 0) {
                        context.startActivity(new Intent(context, SharelistActivity.class)
                                .putExtra(Constants.Bundle.TYPE, "POSTVIEW")
                                .putExtra("event_id", postData.getPost_id() + "")
                                .putExtra("user_id", postData.getCreated_by() + ""));

                        ((Activity) holder.itemView.getContext()).overridePendingTransition(R.anim.enter,
                                R.anim.exit);
                    }
                });
                holderOne.userProfileImage.setOnClickListener(v -> {
                    Family family = new Family();
                    family.setId(Integer.parseInt(postData.getTo_group_id()));
                    Intent intent = new Intent(holder.itemView.getContext(), FamilyDashboardActivity.class);
                    intent.putExtra(DATA, family);
                    holder.itemView.getContext().startActivity(intent);

                    ((Activity) holder.itemView.getContext()).overridePendingTransition(R.anim.enter,
                            R.anim.exit);
                });

                holderOne.itemView.setOnClickListener(v -> {

                            FamilheeyApplication.announcementData = new Gson().toJson(repositories);


                            listener.onAnnouncementselected(position);
                        }


                );

                if (id.equals(SharedPref.getUserRegistration().getId())) {
                    holderOne.btn_view.setVisibility(View.VISIBLE);
                    holderOne.txt_view_count.setVisibility(View.VISIBLE);
                } else {
                    holderOne.btn_view.setVisibility(View.GONE);
                    holderOne.txt_view_count.setVisibility(View.GONE);
                }

                break;
            case SHARE:
                ViewHolderTwo holderTwo = (ViewHolderTwo) holder;
                Glide.with(holder.itemView.getContext())
                        .load(IMAGE_BASE_URL + Constants.Paths.LOGO + postData.getParent_family_logo())
                        .placeholder(R.drawable.family_dashboard_background)
                        .apply(requestOptions).into(holderTwo.innerprofileImage);
                holderTwo.tittlepost.setText(postData.getShared_user_name() + " shared an announcement");
                holderTwo.announcementgroup.setText("Shared in " + postData.getGroup_name());
                holderTwo.announcement_date.setText(dateFormat(postData.getCreatedAt()));
                holderTwo.innerusername.setText(postData.getParent_post_grp_name());
                holderTwo.innergroup.setText("Posted by " + postData.getParent_post_created_user_name());
                holderTwo.txt_des.setText(postData.getSnap_description());

                holderTwo.txt_view_count.setText(postData.getViews_count());
                if (postData.getConversation_enabled()) {
                    holderTwo.img_con.setVisibility(View.VISIBLE);
                    holderTwo.txt_chat_count.setVisibility(View.VISIBLE);
                    holderTwo.txt_chat_count.setText(postData.getConversation_count());

                } else {

                    holderTwo.img_con.setVisibility(View.GONE);
                    holderTwo.txt_chat_count.setVisibility(View.GONE);
                }

                holderTwo.itemView.setOnClickListener(v -> {

                            FamilheeyApplication.announcementData = new Gson().toJson(repositories);
                            listener.onAnnouncementselected(position);

                        }
                );

                holderTwo.btn_more.setOnClickListener(v -> {
                    if (id.equals(SharedPref.getUserRegistration().getId())) {
                        showMenusOwnerShare(v, position);
                    } else {
                        showMenusNormalUser(v, position);
                    }
                });

                holderTwo.img_con.setOnClickListener(v -> {
                    listener.onAnnouncementChat(position);
                });

                holderTwo.btn_view.setOnClickListener(v -> {
                    addViewCount(postData.getPost_id() + "");
                    if (Integer.parseInt(postData.getViews_count()) > 0) {
                        context.startActivity(new Intent(context, SharelistActivity.class)
                                .putExtra(Constants.Bundle.TYPE, "POSTVIEW")
                                .putExtra("event_id", postData.getPost_id() + "")
                                .putExtra("user_id", postData.getCreated_by() + ""));
                    }
                });


                if (id.equals(SharedPref.getUserRegistration().getId())) {
                    holderTwo.btn_view.setVisibility(View.VISIBLE);
                    holderTwo.txt_view_count.setVisibility(View.VISIBLE);
                } else {
                    holderTwo.btn_view.setVisibility(View.GONE);
                    holderTwo.txt_view_count.setVisibility(View.GONE);
                }

                break;

        }

    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }


    @Override
    public int getItemViewType(int position) {

        PostData postData = repositories.get(position);
        if (postData.getShared_user_names() != null)
            return SHARE;
        else if (postData.getPost_ref_id() != null && (postData.getPost_ref_id().equals("READ") || postData.getPost_ref_id().equals("UNREAD")))
            return HEAD;
        else return ITEM;
    }


    private void showMenusNormalUser(View v, int position) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_announcement_user, popup.getMenu());
        if (repositories.get(position).getMuted() != null && repositories.get(position).getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute Conversation");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute Conversation");
        }
        if (repositories.get(position).getRead_status() != null && repositories.get(position).getRead_status()) {
            popup.getMenu().getItem(3).setTitle("Mark as unread");
        } else {
            popup.getMenu().getItem(3).setTitle("Mark as read");
        }

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mute:
                    muteConversation(position);
                    break;
                case R.id.removepost:
                    removePost(repositories.get(position).getPost_id() + "", position);
                    break;
                case R.id.report:
                    Dialoguereport(position);
                    break;


                case R.id.unread:
                    if (repositories.get(position).getRead_status() != null && repositories.get(position).getRead_status()) {
                        repositories.get(position).setRead_status(false);
                        unreadStatus(repositories.get(position).getPost_id() + "", "false");
                    } else {
                        repositories.get(position).setRead_status(true);
                        unreadStatus(repositories.get(position).getPost_id() + "", "true");
                    }

                    repositories.remove(position);
                    notifyDataSetChanged();
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void showMenusOwner(View v, int position) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_announcement_owner, popup.getMenu());
        if (repositories.get(position).getMuted() != null && repositories.get(position).getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute Conversation");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute Conversation");
        }
        if (repositories.get(position).getRead_status() != null && repositories.get(position).getRead_status()) {
            popup.getMenu().getItem(3).setTitle("Mark as unread");
        } else {
            popup.getMenu().getItem(3).setTitle("Mark as read");
        }
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mute:
                    muteConversation(position);
                    break;
                case R.id.edit:
                    listener.onAnnouncementEdit(position);
                    break;
                case R.id.delete:
                    confirmation(position);
                    break;

                case R.id.unread:
                    if (repositories.get(position).getRead_status() != null && repositories.get(position).getRead_status()) {
                        repositories.get(position).setRead_status(false);
                        unreadStatus(repositories.get(position).getPost_id() + "", "false");
                    } else {
                        repositories.get(position).setRead_status(true);
                        unreadStatus(repositories.get(position).getPost_id() + "", "true");
                    }
                    repositories.remove(position);
                    notifyDataSetChanged();
                    break;
            }
            return true;
        });

        popup.show();
    }


    private void showMenusOwnerShare(View v, int position) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.popup_menu_announcement_owner, popup.getMenu());
        if (repositories.get(position).getMuted() != null && repositories.get(position).getMuted()) {
            popup.getMenu().getItem(0).setTitle("UnMute Conversation");
        } else {
            popup.getMenu().getItem(0).setTitle("Mute Conversation");
        }
        if (repositories.get(position).getRead_status() != null && repositories.get(position).getRead_status()) {
            popup.getMenu().getItem(3).setTitle("Mark as unread");
        } else {
            popup.getMenu().getItem(3).setTitle("Mark as read");
        }
        popup.getMenu().getItem(1).setVisible(false);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mute:
                    muteConversation(position);
                    break;
                case R.id.delete:
                    confirmation(position);
                    break;
                case R.id.unread:
                    if (repositories.get(position).getRead_status() != null && repositories.get(position).getRead_status()) {
                        repositories.get(position).setRead_status(false);
                        unreadStatus(repositories.get(position).getPost_id() + "", "false");
                    } else {
                        repositories.get(position).setRead_status(true);
                        unreadStatus(repositories.get(position).getPost_id() + "", "true");
                    }
                    repositories.remove(position);
                    notifyDataSetChanged();
                    break;
            }
            return true;

        });

        popup.show();
    }

    private String dateFormat(String sdate) {
        if (sdate != null) {
            DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(sdate);
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy hh:mm aa");
            return dtfOut.print(dateTime);
        } else return "";
    }

    static class ViewHolderOne extends RecyclerView.ViewHolder {
        @BindView(R.id.userProfileImage)
        ImageView userProfileImage;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.postedin)
        TextView postedin;
        @BindView(R.id.txt_date)
        TextView txt_date;
        @BindView(R.id.txt_des)
        TextView txt_des;

        @BindView(R.id.txt_chat_count)
        TextView txt_chat_count;
        @BindView(R.id.txt_view_count)
        TextView txt_view_count;

        @BindView(R.id.txt_less_or_more)
        TextView txt_less_or_more;

        @BindView(R.id.btn_more)
        ImageView btn_more;

        @BindView(R.id.btn_share)
        ImageView btn_share;

        @BindView(R.id.btn_view)
        ImageView btn_view;

        @BindView(R.id.btn_conversation)
        ImageView btn_conversation;


        ViewHolderOne(View view) {

            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ViewHolderTwo extends RecyclerView.ViewHolder {

        @BindView(R.id.tittlepost)
        TextView tittlepost;
        @BindView(R.id.innerprofileImage)
        ImageView innerprofileImage;
        @BindView(R.id.announcementgroup)
        TextView announcementgroup;
        @BindView(R.id.announcement_date)
        TextView announcement_date;


        @BindView(R.id.innerusername)
        TextView innerusername;

        @BindView(R.id.innergroup)
        TextView innergroup;

        @BindView(R.id.txt_des)
        TextView txt_des;


        @BindView(R.id.img_con)
        ImageView img_con;

        @BindView(R.id.txt_chat_count)
        TextView txt_chat_count;


        @BindView(R.id.btn_more)
        ImageView btn_more;


        @BindView(R.id.btn_view)
        ImageView btn_view;

        @BindView(R.id.txt_view_count)
        TextView txt_view_count;

        ViewHolderTwo(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void unreadStatus(String post_id, String read_status) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        jsonObject.addProperty("read_status", read_status);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.unread_Unread_Post(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                    /*
                    Need to handle
                     */
                }));
    }


    private void deletePost(String id, int position) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(context);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("id", id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.deletePost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    repositories.remove(position);
                    notifyDataSetChanged();
                    progressDialog.dismiss();
                }, throwable -> {
                    progressDialog.dismiss();
                }));
    }

    private void muteConversation(int position) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", repositories.get(position).getPost_id() + "");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.muteConversation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    assert response.body() != null;
                    repositories.get(position).setMuted(response.body().getData().getIs_active());
                    notifyDataSetChanged();
                }, throwable -> {

                }));
    }

    private void removePost(String post_id, int position) {
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(context);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.removePost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    repositories.remove(position);
                    notifyDataSetChanged();
                    progressDialog.dismiss();
                }, throwable -> {
                    progressDialog.dismiss();
                }));
    }

    static class ViewHolderHeader extends RecyclerView.ViewHolder {


        @BindView(R.id.txt_header)
        TextView txt_header;

        ViewHolderHeader(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void confirmation(int position) {

        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Do you really want to delete this Announcement? Deleting this post will delete it from everywhere you posted it to and you will lose all associated conversations.")
                .setConfirmText("Yes")
                .setCancelText("No");

        pDialog.show();
        pDialog.setConfirmClickListener(sDialog -> {
            deletePost(repositories.get(position).getPost_id() + "", position);
            pDialog.cancel();
        });
        pDialog.setCancelClickListener(SweetAlertDialog::cancel);
    }

    private void addViewCount(String post_id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("post_id", post_id);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.addViewCount(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                }, throwable -> {
                }));
    }


    private void Dialoguereport(int position) {

        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_report);
        dialog.setCanceledOnTouchOutside(false);
        TextInputEditText editText = dialog.findViewById(R.id.description);
        dialog.findViewById(R.id.btn_close).setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.findViewById(R.id.btndone).setOnClickListener(v -> {
            if (("").equals(editText.getText().toString().trim())) {
                Toast.makeText(context, "Reason is required", Toast.LENGTH_LONG).show();
            } else {
                reportPost(repositories.get(position).getPost_id() + "", editText.getText().toString().trim(), dialog);
                onClick(v);
            }
        });

        dialog.show();
    }

    private void reportPost(String post_id, String des, Dialog dialog) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().getId());
        jsonObject.addProperty("type_id", post_id);
        jsonObject.addProperty("description", des);
        jsonObject.addProperty("spam_page_type", "announcement");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.reportPost(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    dialog.dismiss();
                    Toast.makeText(context, "Reported successfully.We will review the post and do the needful,thank you", Toast.LENGTH_LONG).show();
                }, throwable -> {
                    /*
                    Need to handle
                     */
                }));
    }

    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public interface OnAnnouncementSelectedListener {
        void onAnnouncementselected(int pos);

        void onAnnouncementChat(int pos);

        void onAnnouncementEdit(int pos);
    }
}