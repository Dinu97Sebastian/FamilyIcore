package com.familheey.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.ChatImageFullView;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Models.ChatModel;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Networking.Retrofit.ApiServices;
import com.familheey.app.Networking.Retrofit.RetrofitBase;
import com.familheey.app.Post.VideoActivity;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.JsonObject;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;

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

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatModel> chatModelList;
    private final Context context;
    private final OnChatLongClickListener longClickListener;
    public CompositeDisposable subscriptions;
    private static final int NORMAL = 1;
    private static final int AUDIO = 2;
    private boolean isChecked=false;
    public ChatAdapter(List<ChatModel> chatModelList, Context context, OnChatLongClickListener longClickListener) {
        this.chatModelList = chatModelList;
        this.context = context;
        this.longClickListener = longClickListener;
        subscriptions = new CompositeDisposable();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == NORMAL) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.message_list, parent, false);
            return new MyViewHolder(listItem);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.message_audio_list, parent, false);
            return new ChatViewHolder(listItem);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ChatModel chatModel = chatModelList.get(position);
        switch (getItemViewType(position)) {
            case NORMAL:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                if (chatModelList.get(position).isOwner()) {
                    myViewHolder.constraintLayoutLeft.setVisibility(View.GONE);
                    myViewHolder.constraintLayoutRight.setVisibility(View.VISIBLE);
                    showSenderViews(myViewHolder, position);
                } else {
                    myViewHolder.constraintLayoutRight.setVisibility(View.GONE);
                    myViewHolder.constraintLayoutLeft.setVisibility(View.VISIBLE);
                    showReceiveView(myViewHolder, chatModelList.get(position), position);
                }
                break;
            case AUDIO:

                ChatViewHolder chatViewHolder = (ChatViewHolder) holder;

                if (chatModelList.get(position).isOwner()) {
                    chatViewHolder.constraintLayoutRight.setVisibility(View.VISIBLE);
                    chatViewHolder.constraintLayoutLeft.setVisibility(View.GONE);
                    chatViewHolder.txtTimeRight.setText(dateFormat(chatModel.getCreatedAt()));
                    chatViewHolder.txtReceiverName.setText("");
                    String url = (Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename());
                    String sourc = "<source src=\"" + url + "\"  type=\"audio/mpeg\">";
                    String html = "<html><body><audio  controls controlsList=\"nodownload\">" + sourc + "</audio></body></html>";
                    chatViewHolder.webviewright.setBackgroundColor(Color.TRANSPARENT);
                    chatViewHolder.webviewright.loadData(html, "text/html", "UTF-8");
                    chatViewHolder.webviewright.setOnLongClickListener(v -> {
                        onLongClicked(position);
                        return true;
                    });
                    chatViewHolder.webviewright.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus){
                                chatViewHolder.webviewright.setBackgroundColor(Color.TRANSPARENT);
                                chatViewHolder.webviewright.loadData(html, "text/html", "UTF-8");
                            }
                        }
                    });

                } else {
                    chatViewHolder.constraintLayoutRight.setVisibility(View.GONE);
                    chatViewHolder.constraintLayoutLeft.setVisibility(View.VISIBLE);
                    chatViewHolder.txtTimeLeft.setText(dateFormat(chatModel.getCreatedAt()));
                    chatViewHolder.txtReceiverName.setText(chatModel.getSenderName());
                    if (!chatModel.getProfilePic().equals("")) {
                        Glide.with(context)
                                .load(chatModel.getProfilePic())
                                .into(chatViewHolder.image_received);

                        chatViewHolder.image_received.setOnClickListener(view -> {
                            Intent intent = new Intent(context, ProfileActivity.class);
                            FamilyMember familyMember = new FamilyMember();
                            familyMember.setId(Integer.parseInt(SharedPref.getUserRegistration().getId()));
                            familyMember.setUserId(chatModel.getUserId());
                            intent.putExtra(DATA, familyMember);
                            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
                            context.startActivity(intent);
                        });

                    } else {
                        chatViewHolder.image_received.setImageResource(R.drawable.avatar_male);
                    }
                    String url = (Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename());
                    String sourc = "<source src=\"" + url + "\"  type=\"audio/mpeg\">";
                    String html = "<html><body><audio controls controlsList=\"nodownload\">" + sourc + "</audio></body></html>";
                    chatViewHolder.webviewright.setOnLongClickListener(v -> {
                        onLongClicked(position);
                        return true;
                    });
                    chatViewHolder.webviewleft.setOnLongClickListener(v -> {
                        onLongClicked(position);
                        return true;
                    });
                    chatViewHolder.webviewleft.getSettings().setJavaScriptEnabled(true);
                    chatViewHolder.webviewleft.setBackgroundColor(Color.TRANSPARENT);
                    chatViewHolder.webviewleft.loadData(html, "text/html", "UTF-8");
                    chatViewHolder.webviewleft.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus){
                                chatViewHolder.webviewleft.setBackgroundColor(Color.TRANSPARENT);
                                chatViewHolder.webviewleft.loadData(html, "text/html", "UTF-8");
                            }
                        }
                    });
                }
                break;
        }
        holder.itemView.setOnLongClickListener(v -> {/*
            if (chatModelList.get(position).isOwner())*/
            longClickListener.onChatLongClicked(position);
            return true;
        });

    }


    private void showReceiveView(MyViewHolder holder, ChatModel chatModel, int position) {
//        holder.txtTimeLeft.setText(getFormattedDate(chatModel.getCreatedAt()));

        if (chatModel.getQuoted_item() != null && !chatModel.getQuoted_item().isEmpty()) {
            holder.qoute_msg_received_view.setVisibility(View.VISIBLE);
            holder.text_qoute_received.setText(chatModel.getQuoted_item());
            if (chatModel.getQuoted_user() != null) {
                holder.text_qoute_name_date_received.setText(chatModel.getQuoted_user() + ", " + dateFormat(chatModel.getQuoted_date()) + "  ");
            }
        } else {
            holder.qoute_msg_received_view.setVisibility(View.GONE);
        }

        holder.txtTimeLeft.setText(dateFormat(chatModel.getCreatedAt()));
        String description = chatModel.getChatText().trim();
        description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                .replaceAll("Https:", "https:")
                .replaceAll("HTTPS:", "https:");
        description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");

        holder.constraintLayoutLeft.setOnLongClickListener(view -> {
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


        holder.txtMessageReceived.setOnLongClickListener(view -> {

            String ftype = "";
            if (chatModel.getAttachmentList() != null && chatModel.getAttachmentList().size() > 0) {
                ftype = chatModel.getAttachmentList().get(0).getFilename().toLowerCase();
            }

            if (!chatModel.isOwner() &&
                    ftype.contains("mp4") ||
                    ftype.contains("mov") ||
                    ftype.contains("wmv") ||
                    ftype.contains("webm") ||
                    ftype.contains("mkv") ||
                    ftype.contains("flv") ||
                    ftype.contains("avi")) {

            } else {
                onLongClicked(position);
            }
            return true;
        });

        holder.txtMessageReceived.setAutoLinkText(description);
        holder.txtReceiverName.setText(chatModel.getSenderName());
        holder.txtMessageReceived.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {

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
                    /*
                    need to handle
                     */
                }
            }

        });

        holder.imgAttachmentReceived.setOnLongClickListener(v -> {
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
        if (!chatModel.getProfilePic().equals("")) {
            Glide.with(context)
                    .load(chatModel.getProfilePic())
                    .into(holder.imageReceiver);
        } else {
            holder.imageReceiver.setImageResource(R.drawable.avatar_male);
        }
        holder.imageReceiver.setOnClickListener(view -> {

            Intent intent = new Intent(context, ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setId(Integer.parseInt(SharedPref.getUserRegistration().getId()));
            familyMember.setUserId(chatModel.getUserId());
            intent.putExtra(DATA, familyMember);
            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
            context.startActivity(intent);
        });

        if (chatModel.getAttachmentList() != null && chatModel.getAttachmentList().size() > 0) {
            holder.cardAttachLeft.setVisibility(View.VISIBLE);
            holder.txtMessageReceived.setVisibility(View.GONE);
            if (chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("mp4") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("mov") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("wmv") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("webm") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("mkv") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("flv") || chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("avi")) {
                holder.imgPlayIconLeft.setImageResource(R.drawable.exo_icon_play);
                holder.imgPlayIconLeft.setVisibility(View.VISIBLE);

                holder.img_count_left_bg.setVisibility(View.GONE);
                holder.img_count_left.setVisibility(View.GONE);
                holder.imgAttachmentReceived.setOnClickListener(view -> {
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("URL", Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename());
                    intent.putExtra("NAME", chatModel.getAttachmentList().get(0).getFilename());
                    context.startActivity(intent);
                });
                Glide.with(context)
                        .load(Constants.ApiPaths.IMAGE_BASE_URL + chatModel.getAttachmentList().get(0).getVideo_thumb())
                        .into(holder.imgAttachmentReceived);
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
                holder.imgAttachmentReceived.setImageResource(0);
                holder.imgAttachmentReceived.setImageDrawable(null);
                holder.img_count_left_bg.setVisibility(View.GONE);
                holder.img_count_left.setVisibility(View.GONE);

                if(chatModel.getAttachmentList().get(0).getType().contains("pdf")){
                    holder.imgPlayIconLeft.setImageResource(R.drawable.icons_pdf);
                }
                else if(chatModel.getAttachmentList().get(0).getType().contains("xls")||chatModel.getAttachmentList().get(0).getType().contains("excel")||chatModel.getAttachmentList().get(0).getType().contains("sheet"))
                {
                    holder.imgPlayIconLeft.setImageResource(R.drawable.ms_excel);
                }
                else if(chatModel.getAttachmentList().get(0).getType().contains("ppt")||chatModel.getAttachmentList().get(0).getType().contains("presentation")||chatModel.getAttachmentList().get(0).getType().contains("powerpoint"))
                {
                    holder.imgPlayIconLeft.setImageResource(R.drawable.ms_powerpoint);
                }
                else if(chatModel.getAttachmentList().get(0).getType().contains("doc")||chatModel.getAttachmentList().get(0).getType().contains("word")  && (!chatModel.getAttachmentList().get(0).getType().contains("presentation") || !chatModel.getAttachmentList().get(0).getType().contains("sheet") ||!chatModel.getAttachmentList().get(0).getType().contains("xls")))

                {
                    holder.imgPlayIconLeft.setImageResource(R.drawable.ms_word);
                }
                else if(chatModel.getAttachmentList().get(0).getType().contains("zip")||chatModel.getAttachmentList().get(0).getType().contains("rar")||chatModel.getAttachmentList().get(0).getType().contains("octet-stream"))
                {
                    holder.imgPlayIconLeft.setImageResource(R.drawable.zip);
                }
                else {
                    /*
                     * megha,25/11/2021
                     * for checking the exceptional case of images with filename "Document-12233.jpg" */
                    if(chatModel.getAttachmentList().get(0).getType().contains("image/png") || chatModel.getAttachmentList().get(0).getType().contains("image")){

                        holder.imgPlayIconLeft.setVisibility(View.GONE);
                        holder.imgAttachmentReceived.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(S3_DEV_IMAGE_URL_SQUARE_DETAILED + Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename())
                                .into(holder.imgAttachmentReceived);
                    }else{
                        holder.imgPlayIconLeft.setImageResource(R.drawable.doc);
                    }
                }
                //holder.imgPlayIconLeft.setImageResource(R.drawable.document_default_item);
//                        myViewHolder.imgPlayIconLeft.setVisibility(View.VISIBLE);
                if(chatModel.getAttachmentList().get(0).getType().contains("image/png") || chatModel.getAttachmentList().get(0).getType().contains("image")) {

                    holder.imgPlayIconLeft.setVisibility(View.GONE);
                    holder.imgAttachmentReceived.setVisibility(View.VISIBLE);
                    holder.imgAttachmentReceived.setOnClickListener(view -> {

                        holder.imgPlayIconLeft.setVisibility(View.GONE);
                        Intent intent = new Intent(context, ChatImageFullView.class);
                        intent.putExtra(DATA, chatModel.getImagesUrls());
                        context.startActivity(intent);
                    });
                }else{
                    holder.imgPlayIconLeft.setVisibility(View.VISIBLE);
                    holder.imgPlayIconLeft.setOnClickListener(view -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename()));
                        context.startActivity(browserIntent);
                    });

                }
                /*
                 * end of condition*/
//                else
//                {
//                    holder.imgPlayIconLeft.setImageResource(R.drawable.doc);
//                }
//                //holder.imgPlayIconLeft.setImageResource(R.drawable.document_default_item);
//                holder.imgPlayIconLeft.setVisibility(View.VISIBLE);
//                holder.imgPlayIconLeft.setOnClickListener(view -> {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename()));
//                    context.startActivity(browserIntent);
//                });


            } else {

                if (chatModel.getAttachmentList().size() == 1) {
                    holder.img_count_left_bg.setVisibility(View.GONE);
                    holder.img_count_left.setVisibility(View.GONE);
                } else {
                    holder.img_count_left_bg.setVisibility(View.VISIBLE);
                    holder.img_count_left.setVisibility(View.VISIBLE);
                    holder.img_count_left.setText("+ " + (chatModel.getAttachmentList().size() - 1));
                }
                holder.imgPlayIconLeft.setVisibility(View.GONE);
                Glide.with(context)
                        .load(S3_DEV_IMAGE_URL_SQUARE_DETAILED + Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename())
                        .into(holder.imgAttachmentReceived);

                holder.imgAttachmentReceived.setOnClickListener(view -> {

                    Intent intent = new Intent(context, ChatImageFullView.class);
                    intent.putExtra(DATA, chatModel.getImagesUrls());
                    context.startActivity(intent);
                });
            }
        } else {
            holder.cardAttachLeft.setVisibility(View.GONE);
            holder.txtMessageReceived.setVisibility(View.VISIBLE);
        }
    }

    private void showSenderViews(MyViewHolder holder, int position) {
        ChatModel chatModel = chatModelList.get(position);
        if (chatModel.getQuoted_item() != null && !chatModel.getQuoted_item().isEmpty()) {
            holder.qoute_msg_view.setVisibility(View.VISIBLE);
            holder.view_separation.setVisibility(View.VISIBLE);
            holder.text_qoute_right.setText(chatModel.getQuoted_item());
            if (chatModel.getQuoted_user() != null) {
                holder.text_qoute_name_date.setText(chatModel.getQuoted_user() + ", " + dateFormat(chatModel.getQuoted_date()) + "  ");
            }
        } else {
            holder.qoute_msg_view.setVisibility(View.GONE);
            holder.view_separation.setVisibility(View.GONE);
        }

        holder.txtTimeRight.setText(dateFormat(chatModel.getCreatedAt()));
        holder.txtMessageSend.setOnLongClickListener(v -> {
            onLongClicked(position);
            return true;
        });
        String description = chatModel.getChatText().trim();
        description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                .replaceAll("Https:", "https:")
                .replaceAll("HTTPS:", "https:");
        description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
        holder.txtMessageSend.setAutoLinkText(description);

        holder.txtMessageSend.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {

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
                       // context.startActivity(new Intent(context, BrowserActivity.class).putExtra("URL", url));
                    }
                } catch (Exception e) {
                    /*
                    Need to handle
                     */
                }
            }

        });

        if (chatModel.getAttachmentList() != null && chatModel.getAttachmentList().size() > 0) {
            holder.txtMessageSend.setVisibility(View.GONE);
            holder.cardAttachRight.setVisibility(View.VISIBLE);

            if (chatModel.getAttachmentList().get(0).getFilename().toLowerCase().contains("mp4")) {
                /*holder.imgAttachmentSend.setImageResource(0);
                holder.imgAttachmentSend.setImageDrawable(null);*/

                holder.img_count_right_bg.setVisibility(View.GONE);
                holder.img_count_right.setVisibility(View.GONE);
                holder.imgAttachmentSend.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(Constants.ApiPaths.IMAGE_BASE_URL + chatModel.getAttachmentList().get(0).getVideo_thumb())
                        .into(holder.imgAttachmentSend);
                holder.imgPlayIconRight.setImageResource(R.drawable.exo_icon_play);
                holder.imgPlayIconRight.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("URL", Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename());
                    intent.putExtra("NAME", chatModel.getAttachmentList().get(0).getFilename());
                    context.startActivity(intent);
                });
                holder.imgAttachmentSend.setOnClickListener(view -> {
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("URL", Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename());
                    intent.putExtra("NAME", chatModel.getAttachmentList().get(0).getFilename());
                    context.startActivity(intent);
                });

                holder.itemView.setOnLongClickListener(v -> {
                    onLongClicked(position);
                    return true;
                });

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
                if(chatModel.getAttachmentList().get(0).getType().contains("pdf")){
                    holder.imgPlayIconRight.setImageResource(R.drawable.icons_pdf);
                }
                else if(chatModel.getAttachmentList().get(0).getType().contains("xls")||chatModel.getAttachmentList().get(0).getType().contains("excel")||chatModel.getAttachmentList().get(0).getType().contains("sheet"))
                {
                    holder.imgPlayIconRight.setImageResource(R.drawable.ms_excel);
                }
                else if(chatModel.getAttachmentList().get(0).getType().contains("ppt")||chatModel.getAttachmentList().get(0).getType().contains("presentation")||chatModel.getAttachmentList().get(0).getType().contains("powerpoint"))
                {
                    holder.imgPlayIconRight.setImageResource(R.drawable.ms_powerpoint);
                }
                else if(chatModel.getAttachmentList().get(0).getType().contains("doc")||chatModel.getAttachmentList().get(0).getType().contains("word") && !chatModel.getAttachmentList().get(0).getType().contains("xls") && (!chatModel.getAttachmentList().get(0).getType().contains("presentation") || !chatModel.getAttachmentList().get(0).getType().contains("sheet")))
                {
                    holder.imgPlayIconRight.setImageResource(R.drawable.ms_word);
                }
                else if(chatModel.getAttachmentList().get(0).getType().contains("zip")||chatModel.getAttachmentList().get(0).getType().contains("rar")||chatModel.getAttachmentList().get(0).getType().contains("octet-stream"))
                {
                    holder.imgPlayIconRight.setImageResource(R.drawable.zip);
                }
                else {
                    /*
                     * megha,25/11/2021
                     * for checking the exceptional case of images with filename "Document-12233.jpg" */
                    if(chatModel.getAttachmentList().get(0).getType().contains("image/png") || chatModel.getAttachmentList().get(0).getType().contains("image")){

                        holder.imgPlayIconRight.setVisibility(View.GONE);
                        holder.imgAttachmentSend.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(S3_DEV_IMAGE_URL_SQUARE_DETAILED + Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename())
                                .into(holder.imgAttachmentSend);
                    }else{
                        holder.imgPlayIconRight.setImageResource(R.drawable.doc);
                    }
                }
                //holder.imgPlayIconLeft.setImageResource(R.drawable.document_default_item);
//                        myViewHolder.imgPlayIconLeft.setVisibility(View.VISIBLE);
                if(chatModel.getAttachmentList().get(0).getType().contains("image/png") || chatModel.getAttachmentList().get(0).getType().contains("image")) {

                    holder.imgPlayIconRight.setVisibility(View.GONE);
                    holder.imgAttachmentSend.setVisibility(View.VISIBLE);
                    holder.imgAttachmentSend.setOnClickListener(view -> {

                        holder.imgPlayIconRight.setVisibility(View.GONE);
                        Intent intent = new Intent(context, ChatImageFullView.class);
                        intent.putExtra(DATA, chatModel.getImagesUrls());
                        context.startActivity(intent);
                    });
                }else{
                    holder.imgAttachmentSend.setVisibility(View.GONE);
                    holder.img_count_right_bg.setVisibility(View.GONE);
                    holder.img_count_right.setVisibility(View.GONE);
                    holder.imgPlayIconRight.setVisibility(View.VISIBLE);
                    holder.imgPlayIconRight.setOnClickListener(view -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename()));
                        context.startActivity(browserIntent);
                    });

                }
                /*
                 * end of condition*/
//                else
//                {
//                    holder.imgPlayIconRight.setImageResource(R.drawable.doc);
//                }
//                // holder.imgAttachmentSend.setImageResource(0);
//                // holder.imgAttachmentSend.setImageDrawable(null);
//                holder.imgAttachmentSend.setVisibility(View.GONE);
//                holder.img_count_right_bg.setVisibility(View.GONE);
//                holder.img_count_right.setVisibility(View.GONE);
//                holder.imgPlayIconRight.setVisibility(View.VISIBLE);
//                holder.imgPlayIconRight.setOnClickListener(view -> {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename()));
//                    context.startActivity(browserIntent);
//                });
                holder.imgPlayIconRight.setOnLongClickListener(v -> {
                    onLongClicked(position);
                    return true;
                });
            } else {
                holder.imgAttachmentSend.setVisibility(View.VISIBLE);
                if (chatModel.getAttachmentList().size() == 1) {
                    holder.img_count_right_bg.setVisibility(View.GONE);
                    holder.img_count_right.setVisibility(View.GONE);
                } else {
                    holder.img_count_right_bg.setVisibility(View.VISIBLE);
                    holder.img_count_right.setVisibility(View.VISIBLE);
                    holder.img_count_right.setText("+ " + (chatModel.getAttachmentList().size() - 1));
                }
                holder.imgPlayIconRight.setVisibility(View.GONE);
                holder.imgAttachmentSend.setOnClickListener(view -> {

                    Intent intent = new Intent(context, ChatImageFullView.class);
                    intent.putExtra(DATA, chatModel.getImagesUrls());
                    context.startActivity(intent);
                });
                holder.imgAttachmentSend.setOnLongClickListener(v -> {
                    onLongClicked(position);
                    return true;
                });

                Glide.with(context)
                        .load(Constants.ApiPaths.S3_DEV_IMAGE_URL_ALBUM + Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + chatModel.getAttachmentList().get(0).getFilename())
                        .into(holder.imgAttachmentSend);
            }

        } else {
            holder.cardAttachRight.setVisibility(View.GONE);
            holder.txtMessageSend.setVisibility(View.VISIBLE);
        }
    }

//    private String getFormattedDate(String createdAt) {
////for getting the date, on 17/06/2021 by Megha
//        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");
//        Date day = null;
//        try {
//            day = outputFormat.parse(createdAt);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        String formattedDate = outputFormat.format(day);
//        String currentDate = new LocalDate().toString();
//        String newFormattedDate = newFormat.format(day);
//
//        for (int i = 0; i < chatModelList.size(); i++) {
//            if (currentDate.equals(formattedDate)) {
//                isChecked = true;
//                break;
//            } else {
//                isChecked = false;
//            }
//        }
//        if (isChecked) {
//            //condition for getting the current day time by Megha on 17/06/2021
//            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
//            TimeZone tzInAmerica = TimeZone.getTimeZone("IST");
//            dateFormatter.setTimeZone(tzInAmerica);
//            Date date = null;
//            try {
//                date = dateFormatter.parse(createdAt);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            PrettyTime p = new PrettyTime();
//            return p.format(date);
//        }
//        return newFormattedDate;
//    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
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
                    return p.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(time);
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMM dd yyyy");
            return dtfOut.print(dateTime);

        }

    @Override
    public int getItemViewType(int position) {

        ChatModel model = chatModelList.get(position);
        if (model.getType().contains("audio")||model.getType().contains("mp3")) {
            return AUDIO;
        } else return NORMAL;
    }

    private void onLongClicked(int position) {

        longClickListener.onChatLongClicked(position);
    }

    public interface OnChatLongClickListener {
        void onChatLongClicked(int position);
        //void onClickImage(int position);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image_received;
        private final TextView txtTimeRight;
        private final TextView txtTimeLeft;
        private final TextView txtReceiverName;
        private final ConstraintLayout constraintLayoutLeft;
        private final ConstraintLayout constraintLayoutRight;

        private final WebView webviewright;
        private final WebView webviewleft;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayoutLeft = itemView.findViewById(R.id.constraintLayoutLeftAudio);
            constraintLayoutRight = itemView.findViewById(R.id.constraintLayoutRightAudio);
            image_received = itemView.findViewById(R.id.image_received_audio);
            txtTimeRight = itemView.findViewById(R.id.txtTimeRightaudio);
            txtTimeLeft = itemView.findViewById(R.id.txtTimeLeft_audio);
            txtReceiverName = itemView.findViewById(R.id.text_message_name_received_audio);
            webviewright = itemView.findViewById(R.id.webviewright);
            webviewleft = itemView.findViewById(R.id.webviewleft);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final AutoLinkTextView txtMessageSend;
        private final AutoLinkTextView txtMessageReceived;
        private final TextView txtReceiverName;
        private final TextView img_count_right;
        private final TextView img_count_left;
        private final ImageView imageReceiver;
        private final ImageView imgAttachmentReceived;
        private final ImageView imgAttachmentSend;
        private final ImageView imgPlayIconRight;
        private final ImageView imgPlayIconLeft;
        private final View img_count_right_bg;
        private final View img_count_left_bg;
        private final ConstraintLayout constraintLayoutLeft;
        private final ConstraintLayout constraintLayoutRight;
        private final CardView cardAttachRight;
        private final CardView cardAttachLeft;
        private final TextView txtTimeRight;
        private final TextView txtTimeLeft;
        private final LinearLayout qoute_msg_received_view;
        private final LinearLayout qoute_msg_view;
        private final TextView text_qoute_received;
        private final TextView text_qoute_right;
        private final TextView text_qoute_name_date_received;
        private final TextView text_qoute_name_date;
        private final TextView view_separation;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessageSend = itemView.findViewById(R.id.text_message_body_send);
            txtMessageReceived = itemView.findViewById(R.id.text_message_body_received);
            txtMessageSend.addAutoLinkMode(AutoLinkMode.MODE_URL);
            txtMessageSend.setUrlModeColor(ContextCompat.getColor(context, R.color.buttoncolor));
            txtMessageReceived.addAutoLinkMode(AutoLinkMode.MODE_URL);
            txtMessageReceived.setUrlModeColor(ContextCompat.getColor(context, R.color.buttoncolor));
            txtTimeRight = itemView.findViewById(R.id.txtTimeRight);
            txtTimeLeft = itemView.findViewById(R.id.txtTimeLeft);
            constraintLayoutLeft = itemView.findViewById(R.id.constraintLayoutLeft);
            constraintLayoutRight = itemView.findViewById(R.id.constraintLayoutRight);
            imgPlayIconRight = itemView.findViewById(R.id.imgPlayIcon);
            imgPlayIconLeft = itemView.findViewById(R.id.imgPlayIconLeft);
            img_count_right = itemView.findViewById(R.id.img_count_right);
            imageReceiver = itemView.findViewById(R.id.image_received);
            imgAttachmentReceived = itemView.findViewById(R.id.imgAttachmentReceived);
            imgAttachmentSend = itemView.findViewById(R.id.imgAttachmentSend);
            txtReceiverName = itemView.findViewById(R.id.text_message_name_received);
            img_count_right_bg = itemView.findViewById(R.id.img_count_right_bg);
            cardAttachRight = itemView.findViewById(R.id.cardAttachRight);
            cardAttachLeft = itemView.findViewById(R.id.cardAttachLeft);
            qoute_msg_received_view = itemView.findViewById(R.id.qoute_msg_received_view);
            qoute_msg_view = itemView.findViewById(R.id.qoute_msg_view);
            text_qoute_received = itemView.findViewById(R.id.text_qoute_received);
            text_qoute_right = itemView.findViewById(R.id.text_qoute_right);
            text_qoute_name_date_received = itemView.findViewById(R.id.text_qoute_name_date_received);
            text_qoute_name_date = itemView.findViewById(R.id.text_qoute_name_date);
            view_separation = itemView.findViewById(R.id.view_separation);
            img_count_left = itemView.findViewById(R.id.img_count_left);
            img_count_left_bg = itemView.findViewById(R.id.img_count_left_bg);
        }
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
                    if(response.body()!=null) {
                        if (response.body().getData().getSub_type().equals("family_link")) {
                            response.body().getData().setSub_type("");
                        }
                        response.body().getData().goToCorrespondingDashboard(context);
                    }
                    else{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    }
                }, throwable -> progressDialog.dismiss()));
    }
}
