package com.familheey.app.Post;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.ChatImageFullView;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Interfaces.PostCommentListener;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.PostComment;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE_DETAILED;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class PostCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<PostComment> chatModelList;
    private final Context context;
    private final PostCommentListener postCommentListener;
    public CompositeDisposable subscriptions;
    private static final int NORMAL = 1;
    private static final int AUDIO = 2;
    private boolean isChecked=false;
    private static int currentPosition =-1;
    private RecyclerView.RecycledViewPool
            viewPool = new RecyclerView.RecycledViewPool();
    public PostCommentAdapter(List<PostComment> chatModelList, Context context, PostCommentListener postCommentListener) {
        this.chatModelList = chatModelList;
        this.context = context;
        this.postCommentListener = postCommentListener;
        subscriptions = new CompositeDisposable();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.post_comment_list, parent, false);
            return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostComment chatModel = chatModelList.get(position);

        switch (getItemViewType(position)) {
            case NORMAL:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                myViewHolder.textViewName.setText(chatModel.getSenderName());
if(chatModel.isOwner()){
    myViewHolder.text_message_body_received_view.setBackgroundResource(R.drawable.rounded_rectangle_grey);
    myViewHolder.webviewleft.setBackgroundResource(R.drawable.rounded_rectangle_grey);
    myViewHolder.cardAttachLeft.setBackgroundResource(R.drawable.rounded_rectangle_grey);
}
else {
    myViewHolder.text_message_body_received_view.setBackgroundResource(R.drawable.rounded_rectangle_orange);
    myViewHolder.webviewleft.setBackgroundResource(R.drawable.rounded_rectangle_orange);
    myViewHolder.cardAttachLeft.setBackgroundResource(R.drawable.rounded_rectangle_orange);
}
                String description = chatModel.getChatText().trim();
                description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                        .replaceAll("Https:", "https:")
                        .replaceAll("HTTPS:", "https:");
                description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
//                myViewHolder.linearLayout.setVisibility(View.GONE);
                /**modified on 23-11-2021**/
                myViewHolder.txtMessageReceived.setText(description);
                if(myViewHolder.txtMessageReceived!=null){
                    String url = myViewHolder.txtMessageReceived.getText().toString();
                     /*if (url.contains("familheey")) {
                        openAppGetParams(url);
                    }*/
                    Linkify.addLinks(myViewHolder.txtMessageReceived, Linkify.ALL); // linkify all links in text.
                    //you can set link color with the help of text view property
                    myViewHolder.txtMessageReceived.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
                }
                myViewHolder.txtTimeLeft.setText(dateFormat(chatModel.getCreatedAt()));
                if(chatModel.getReply_count().equals("0")){
                    myViewHolder.reply.setText("Reply");
                }else if(chatModel.getReply_count().equals("1")){
                    myViewHolder.reply.setText(chatModel.getReply_count()+" Reply");
                }
                else{
                    myViewHolder.reply.setText(chatModel.getReply_count()+" Replies");
                }
                if (!chatModel.getProfilePic().equals("")) {
                    Glide.with(context)
                            .load(chatModel.getProfilePic())
                            .into(myViewHolder.imageReceiver);
                } else {
                    myViewHolder.imageReceiver.setImageResource(R.drawable.avatar_male);
                }
                myViewHolder.imageReceiver.setOnClickListener(view -> {

                    Intent intent = new Intent(context, ProfileActivity.class);
                    FamilyMember familyMember = new FamilyMember();
                    familyMember.setId(Integer.parseInt(SharedPref.getUserRegistration().getId()));
                    familyMember.setUserId(chatModel.getUserId());
                    intent.putExtra(DATA, familyMember);
                    intent.putExtra(Constants.Bundle.FOR_EDITING, true);
                    context.startActivity(intent);
                });
               if(chatModel.getType().contains("audio")||chatModel.getType().contains("mp3")){
                   myViewHolder.cardAttachLeft.setVisibility(View.GONE);
                   myViewHolder.txtMessageReceived.setVisibility(View.GONE);
                   myViewHolder.received_audio_view.setVisibility(View.VISIBLE);

                   myViewHolder.txtMessageReceived.setVisibility(View.GONE);
                   String url = (Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename());
                   String sourc = "<source src=\"" + url + "\"  type=\"audio/mpeg\">";
                   String html = "<html><body><audio controls controlsList=\"nodownload\">" + sourc + "</audio></body></html>";
                   myViewHolder.webviewleft.setOnLongClickListener(v -> {
                       onLongClicked(position);
                       return true;
                   });
                   myViewHolder.webviewleft.getSettings().setJavaScriptEnabled(true);
                   myViewHolder.webviewleft.setBackgroundColor(Color.TRANSPARENT);
                   myViewHolder.webviewleft.loadData(html, "text/html", "UTF-8");
                   myViewHolder.webviewleft.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                       @Override
                       public void onFocusChange(View v, boolean hasFocus) {
                           if (!hasFocus) {
                               myViewHolder.webviewleft.setBackgroundColor(Color.TRANSPARENT);
                               myViewHolder.webviewleft.loadData(html, "text/html", "UTF-8");
                           }
                       }
                   });

               }
               else if (chatModel.getAttachmentList() != null && chatModel.getAttachmentList().size() > 0) {
                    myViewHolder.cardAttachLeft.setVisibility(View.VISIBLE);
                    myViewHolder.txtMessageReceived.setVisibility(View.GONE);
                   myViewHolder.received_audio_view.setVisibility(View.GONE);
                    if (chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("mp4") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("mov") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("wmv") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("webm") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("mkv") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("flv") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("avi")) {
                        myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.exo_icon_play);
                        myViewHolder.imgPlayIconLeft.setVisibility(View.VISIBLE);

                        myViewHolder.img_count_left_bg.setVisibility(View.GONE);
                        myViewHolder.img_count_left.setVisibility(View.GONE);
                        myViewHolder.imgAttachmentReceived.setOnClickListener(view -> {
                            Intent intent = new Intent(context, VideoActivity.class);
                            intent.putExtra("URL", Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename());
                            intent.putExtra("NAME", chatModel.getAttachmentList().get(0).getFilename());
                            context.startActivity(intent);
                        });
                        Glide.with(context)
                                .load(Constants.ApiPaths.IMAGE_BASE_URL + chatModel.getAttachmentList().get(0).getVideo_thumb())
                                .into(myViewHolder.imgAttachmentReceived);
                    } else if (chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("docx")
                            || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("pdf")
                            || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("doc")
                            || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("xls")
                            || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("xlsx")
                            || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("txt")
                            || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("ppt")
                            || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("zip")
                            || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("rar")
                            || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("rtf")
                    ) {
                        myViewHolder.imgAttachmentReceived.setImageResource(0);
                        myViewHolder.imgAttachmentReceived.setImageDrawable(null);
                        myViewHolder.img_count_left_bg.setVisibility(View.GONE);
                        myViewHolder.img_count_left.setVisibility(View.GONE);

                        if(chatModel.getAttachmentList().get(0).getType().contains("pdf")){
                            myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.icons_pdf);
                        }
                        else if(chatModel.getAttachmentList().get(0).getType().contains("xls")||chatModel.getAttachmentList().get(0).getType().contains("excel")||chatModel.getAttachmentList().get(0).getType().contains("sheet"))
                        {
                            myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.ms_excel);
                        }
                        else if(chatModel.getAttachmentList().get(0).getType().contains("ppt")||chatModel.getAttachmentList().get(0).getType().contains("presentation")||chatModel.getAttachmentList().get(0).getType().contains("powerpoint"))
                        {
                            myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.ms_powerpoint);
                        }
                        else if(chatModel.getAttachmentList().get(0).getType().contains("doc")||chatModel.getAttachmentList().get(0).getType().contains("word")  && (!chatModel.getAttachmentList().get(0).getType().contains("presentation") || !chatModel.getAttachmentList().get(0).getType().contains("sheet") ||!chatModel.getAttachmentList().get(0).getType().contains("xls")))

                        {
                            myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.ms_word);
                        }
                        else if(chatModel.getAttachmentList().get(0).getType().contains("zip")||chatModel.getAttachmentList().get(0).getType().contains("rar")||chatModel.getAttachmentList().get(0).getType().contains("octet-stream"))
                        {
                            myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.zip);
                        }
//                        else
//                        {
//                            myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.doc);
//                        }
//                        //holder.imgPlayIconLeft.setImageResource(R.drawable.document_default_item);
//                        myViewHolder.imgPlayIconLeft.setVisibility(View.VISIBLE);
//                        myViewHolder.imgPlayIconLeft.setOnClickListener(view -> {
//                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename()));
//                            context.startActivity(browserIntent);
//                        });
                        else {
                            /*
                             * megha,25/11/2021
                             * for checking the exceptional case of images with filename "Document-12233.jpg" */
                            if(chatModel.getAttachmentList().get(0).getType().contains("image/png") || chatModel.getAttachmentList().get(0).getType().contains("image")){

                                myViewHolder.imgPlayIconLeft.setVisibility(View.GONE);
                                myViewHolder.imgAttachmentReceived.setVisibility(View.VISIBLE);
                                Glide.with(context)
                                        .load(S3_DEV_IMAGE_URL_SQUARE_DETAILED + Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename())
                                        .into(myViewHolder.imgAttachmentReceived);
                            }else{
                                myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.doc);
                            }
                        }
                        //holder.imgPlayIconLeft.setImageResource(R.drawable.document_default_item);
//                        myViewHolder.imgPlayIconLeft.setVisibility(View.VISIBLE);
                        if(chatModel.getAttachmentList().get(0).getType().contains("image/png") || chatModel.getAttachmentList().get(0).getType().contains("image")) {

                            myViewHolder.imgPlayIconLeft.setVisibility(View.GONE);
                            myViewHolder.imgAttachmentReceived.setVisibility(View.VISIBLE);
                            myViewHolder.imgAttachmentReceived.setOnClickListener(view -> {

                                myViewHolder.imgPlayIconLeft.setVisibility(View.GONE);
                                Intent intent = new Intent(context, ChatImageFullView.class);
                                intent.putExtra(DATA, chatModel.getImagesUrls());
                                context.startActivity(intent);
                            });
                        }else{
                            myViewHolder.imgPlayIconLeft.setVisibility(View.VISIBLE);
                            myViewHolder.imgPlayIconLeft.setOnClickListener(view -> {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename()));
                                context.startActivity(browserIntent);
                            });

                        }
                        /*
                         * end of condition*/

                        myViewHolder.imgPlayIconLeft.setOnLongClickListener(v -> {
                            onLongClicked(position);
                            return true;
                        });
                    } else {

                        if (chatModel.getAttachmentList().size() == 1) {
                            myViewHolder.img_count_left_bg.setVisibility(View.GONE);
                            myViewHolder.img_count_left.setVisibility(View.GONE);
                        } else {
                            myViewHolder.img_count_left_bg.setVisibility(View.VISIBLE);
                            myViewHolder.img_count_left.setVisibility(View.VISIBLE);
                            myViewHolder.img_count_left.setText("+ " + (chatModel.getAttachmentList().size() - 1));
                        }
                        myViewHolder.imgPlayIconLeft.setVisibility(View.GONE);
                        Glide.with(context)
                                .load(S3_DEV_IMAGE_URL_SQUARE_DETAILED + Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename())
                                .into(myViewHolder.imgAttachmentReceived);

                        myViewHolder.imgAttachmentReceived.setOnClickListener(view -> {

                            Intent intent = new Intent(context, ChatImageFullView.class);
                            intent.putExtra(DATA, chatModel.getImagesUrls());
                            context.startActivity(intent);
                        });
                    }
                } else {
                    myViewHolder.cardAttachLeft.setVisibility(View.GONE);
                    myViewHolder.txtMessageReceived.setVisibility(View.VISIBLE);
                   myViewHolder.received_audio_view.setVisibility(View.GONE);
                }

                myViewHolder.linearLayout.setVisibility(View.GONE);
                //if the position is equals to the item position which is to be expanded
                if (currentPosition == position) {
                    //creating an animation
                    Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
                    //toggling visibility
                    myViewHolder.linearLayout.setVisibility(View.VISIBLE);
                    //adding sliding effect
                    myViewHolder.linearLayout.startAnimation(slideDown);
                }
                myViewHolder.reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //getting the position of the item to expand it
                        postCommentListener.commentReplyClickListener(position,currentPosition);
                        currentPosition = position;
                        //reloding the list
                        if(position!=currentPosition) {
                            notifyDataSetChanged();
                        }

                    }
                });

                myViewHolder.txtMessageReceived.setOnLongClickListener(view -> {
                    String ftype = "";
                    if (chatModel.getAttachmentList() != null && chatModel.getAttachmentList().size() > 0) {
                        ftype = chatModel.getAttachmentList().get(0).getFilename().toLowerCase();
                    }
                    if (!chatModel.isOwner() && ftype.contains("mp4")
                            || ftype.contains("mov")
                            || ftype.contains("wmv")
                            || ftype.contains("webm")
                            || ftype.contains("mkv")
                            || ftype.contains("flv")
                            || ftype.contains("avi")) {

                    } else {
                        onLongClicked(position);

                    }
                    return true;
                });


 /*               myViewHolder.txtMessageReceived.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {

                    if (autoLinkMode == AutoLinkMode.MODE_URL) {
                        try {
                            String url = matchedText.trim();
                            if (!url.contains("http")) {
                                url = url.replaceAll("www.", "http://www.");
                            }
                            if (url.contains("blog.familheey.com")) {
                                // Dinu(09/11/2021) for open another app
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                context.startActivity(intent);
                            }
                            else if (url.contains("familheey")) {
                                openAppGetParams(url);
                            } else {
                                // Dinu(15/07/2021) for open another app
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                context.startActivity(intent);
                                //  context.startActivity(new Intent(context, BrowserActivity.class).putExtra("URL", url));
                            }
                        } catch (Exception e) {
                    *//*
                    need to handle
                     *//*
                        }
                    }

                });*/

                myViewHolder.imgAttachmentReceived.setOnLongClickListener(v -> {
                    String ftype = "";
                    if (chatModel.getAttachmentList() != null && chatModel.getAttachmentList().size() > 0) {
                        ftype = chatModel.getAttachmentList().get(0).getFilename().toLowerCase();
                    }

                    if (!chatModel.isOwner()
                            && ftype.contains("mp4")
                            || ftype.contains("mov")
                            || ftype.contains("wmv")
                            || ftype.contains("webm")
                            || ftype.contains("mkv")
                            || ftype.contains("flv")
                            || ftype.contains("avi")) {

                    } else {
                        onLongClicked(position);
                    }
                    return true;
                });

                //For set childItemAdapter
                LinearLayoutManager layoutManager = new LinearLayoutManager(myViewHolder.childRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
                layoutManager.setInitialPrefetchItemCount(2);
                CommentReplyAdapter childItemAdapter = new CommentReplyAdapter(chatModel.getCommentReply(),position,context,postCommentListener);
                myViewHolder.childRecyclerView.setLayoutManager(layoutManager);
                myViewHolder.childRecyclerView.setAdapter(childItemAdapter);
                myViewHolder.childRecyclerView.setRecycledViewPool(viewPool);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image_received;
        private final TextView txtTimeLeft;
        private final TextView txtReceiverName;
        private final ConstraintLayout constraintLayoutLeft;
        private final TextView reply;
        private final WebView webviewleft;
        private final LinearLayout linearLayout;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayoutLeft = itemView.findViewById(R.id.constraintLayoutLeftAudio);
            image_received = itemView.findViewById(R.id.image_received_audio);
            reply=itemView.findViewById(R.id.reply);
            txtTimeLeft = itemView.findViewById(R.id.txtTimeLeft_audio);
            txtReceiverName = itemView.findViewById(R.id.text_message_name_received_audio);
            webviewleft = itemView.findViewById(R.id.webviewleft);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout received_audio_view;
        private final WebView webviewleft;
        private final RecyclerView childRecyclerView;
        private final TextView reply;
        private final TextView textViewName;
        private final LinearLayout linearLayout;
        //        private final AutoLinkTextView txtMessageSend;
        private final TextView txtMessageReceived;
//        private final TextView txtReceiverName;
//        private final TextView img_count_right;
        private final TextView img_count_left;
        private final ImageView imageReceiver;
        private final ImageView imgAttachmentReceived;
//        private final ImageView imgAttachmentSend;
//        private final ImageView imgPlayIconRight;
        private final ImageView imgPlayIconLeft;
//        private final View img_count_right_bg;
        private final View img_count_left_bg;
        private final ConstraintLayout constraintLayoutLeft;
//        private final CardView cardAttachRight;
        private final CardView cardAttachLeft;
//        private final TextView txtTimeRight;
        private final TextView txtTimeLeft;
        private final LinearLayout text_message_body_received_view;
//        private final LinearLayout qoute_msg_received_view;
//        private final LinearLayout qoute_msg_view;
//        private final TextView text_qoute_received;
//        private final TextView text_qoute_right;
//        private final TextView text_qoute_name_date_received;
//        private final TextView text_qoute_name_date;
//        private final TextView view_separation;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            received_audio_view=itemView.findViewById(R.id.received_audio_view);
            webviewleft = itemView.findViewById(R.id.webviewleft);
            childRecyclerView = itemView.findViewById(R.id.child_recyclerview);
            reply=itemView.findViewById(R.id.reply);
            textViewName = itemView.findViewById(R.id.text_message_name_received);
            linearLayout = itemView.findViewById(R.id.linearLayout);
//            txtMessageSend = itemView.findViewById(R.id.text_message_body_send);
            txtMessageReceived = itemView.findViewById(R.id.text_reply_received);
//            txtMessageSend.addAutoLinkMode(AutoLinkMode.MODE_URL);
//            txtMessageSend.setUrlModeColor(ContextCompat.getColor(context, R.color.buttoncolor));
           /* txtMessageReceived.addAutoLinkMode(AutoLinkMode.MODE_URL);
            txtMessageReceived.setUrlModeColor(ContextCompat.getColor(context, R.color.buttoncolor));*/
//            txtTimeRight = itemView.findViewById(R.id.txtTimeRight);
            txtTimeLeft = itemView.findViewById(R.id.txtTimeLeft);
            constraintLayoutLeft = itemView.findViewById(R.id.constraintLayoutLeft);
//            imgPlayIconRight = itemView.findViewById(R.id.imgPlayIcon);
            imgPlayIconLeft = itemView.findViewById(R.id.imgPlayIconLeft);
//            img_count_right = itemView.findViewById(R.id.img_count_right);
            imageReceiver = itemView.findViewById(R.id.image_received);
            imgAttachmentReceived = itemView.findViewById(R.id.imgAttachmentReceived);
//            imgAttachmentSend = itemView.findViewById(R.id.imgAttachmentSend);
//            txtReceiverName = itemView.findViewById(R.id.text_message_name_received);
//            img_count_right_bg = itemView.findViewById(R.id.img_count_right_bg);
//            cardAttachRight = itemView.findViewById(R.id.cardAttachRight);
            cardAttachLeft = itemView.findViewById(R.id.cardAttachLeft);
//            qoute_msg_received_view = itemView.findViewById(R.id.qoute_msg_received_view);
//            qoute_msg_view = itemView.findViewById(R.id.qoute_msg_view);
//            text_qoute_received = itemView.findViewById(R.id.text_qoute_received);
//            text_qoute_right = itemView.findViewById(R.id.text_qoute_right);
//            text_qoute_name_date_received = itemView.findViewById(R.id.text_qoute_name_date_received);
//            text_qoute_name_date = itemView.findViewById(R.id.text_qoute_name_date);
//            view_separation = itemView.findViewById(R.id.view_separation);
           img_count_left = itemView.findViewById(R.id.img_count_left);
            img_count_left_bg = itemView.findViewById(R.id.img_count_left_bg);
            text_message_body_received_view=itemView.findViewById(R.id.text_message_body_received_view);
        }
    }


    private void onLongClicked(int position) {
        postCommentListener.commentReplyClickListener(position,currentPosition);
        postCommentListener.longClickListener(position);
        currentPosition = position;
        //reloding the list
        if(position!=currentPosition) {
            notifyDataSetChanged();
        }

    }

    public interface OnChatLongClickListener {
        void onChatLongClicked(int position);
        //void onClickImage(int position);
    }


    private String getFormattedDate(String createdAt) {
//for getting the date, on 17/06/2021 by Megha
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");
        Date day = null;
        try {
            day = outputFormat.parse(createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = outputFormat.format(day);
        String currentDate = new LocalDate().toString();
        String newFormattedDate = newFormat.format(day);

        for (int i = 0; i < chatModelList.size(); i++) {
            if (currentDate.equals(formattedDate)) {
                isChecked = true;
                break;
            } else {
                isChecked = false;
            }
        }
        if (isChecked) {
            //condition for getting the current day time by Megha on 17/06/2021
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            TimeZone tzInAmerica = TimeZone.getTimeZone("IST");
            dateFormatter.setTimeZone(tzInAmerica);
            Date date = null;
            try {
                date = dateFormatter.parse(createdAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            PrettyTime p = new PrettyTime();
            String ll=p.format(date);
            return p.format(date);
        }
        return newFormattedDate;
    }
    private String dateFormat(String time) {
        //Megha(29/07/2021) for changing timezone
        for (int i=0;i<chatModelList.size();i++){
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date day = null;
            try {
                day = outputFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String formattedDate = outputFormat.format(day);
            String currentDate = new LocalDate().toString();
            if (currentDate.equals(formattedDate)){
                isChecked = true;
                break;
            }else{
                isChecked = false;
            }
        }if (isChecked){
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            TimeZone tz = TimeZone.getTimeZone("TimeZone.getDefault");
            dateFormatter.setTimeZone(tz);

            try {
                Date date = dateFormatter.parse(time);
                PrettyTime p = new PrettyTime();
                if(p.format(date).equals("moments ago"))
                    return "Just now";
                    else if(p.format(date).equals("moments from now"))
                    return "moments ago";
                    else
                    return p.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time);
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy");
        return dtfOut.print(dateTime);
    }
    private void openAppGetParams(String url) {
        // UserNotification
        SweetAlertDialog progressDialog = Utilities.getProgressDialog(context);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", url);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        FamilheeyApplication application = FamilheeyApplication.get(context);
        ApiServices apiServices = RetrofitBase.createRxResource(context, ApiServices.class);
        subscriptions.add(apiServices.openAppGetParams(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(response -> {
                    progressDialog.dismiss();
                    if(response.body()!=null){
                    if(response.body().getData().getSub_type().equals("family_link")){
                        response.body().getData().setSub_type("");
                    }
                    response.body().getData().goToCorrespondingDashboard(context);
                    }else{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    }
                }, throwable -> progressDialog.dismiss()));
    }
    @Override
    public int getItemViewType(int position) {

            return NORMAL;
    }
    private ItemClickListener listener;
    //set method
    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }
    //Defining interface
    public interface ItemClickListener{
        //Achieve the click method, passing the subscript.
        void onItemClick(int position,int previousPosition);
    }
}
