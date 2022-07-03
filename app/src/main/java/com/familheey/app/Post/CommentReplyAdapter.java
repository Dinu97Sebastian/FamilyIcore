package com.familheey.app.Post;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.familheey.app.Interfaces.PostCommentListener;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.GetCommentReplyResponse;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;

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

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE_DETAILED;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.ChildViewHolder>{
    private List<GetCommentReplyResponse> childItemList;
    private final Context context;
    private static final int NORMAL = 1;
    private static final int AUDIO = 2;
    private final PostCommentListener postCommentListener;
    private int parentPosition=-1;
    private boolean isChecked=false;
    // Constuctor
    CommentReplyAdapter(List<GetCommentReplyResponse> childItemList,int parentPosition, Context context, PostCommentListener postCommentListener )
    {
        this.postCommentListener=postCommentListener;
        this.childItemList = childItemList;
        this.context = context;
        this.parentPosition=parentPosition;
    }
    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_comment_reply_list, viewGroup, false);
            return new ChildViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder childViewHolder, int position)
    {
        GetCommentReplyResponse childItem = childItemList.get(position);
        ChildViewHolder myViewHolder = (ChildViewHolder) childViewHolder;
        if(String.valueOf(childItem.getUserId()).equals(SharedPref.getUserRegistration().getId())){
            myViewHolder.text_message_body_received_view.setBackgroundResource(R.drawable.rounded_rectangle_grey);
            myViewHolder.cardAttachLeft.setBackgroundResource(R.drawable.rounded_rectangle_grey);
        }
        else {
            myViewHolder.text_message_body_received_view.setBackgroundResource(R.drawable.rounded_rectangle_orange);
            myViewHolder.cardAttachLeft.setBackgroundResource(R.drawable.rounded_rectangle_orange);
        }
            myViewHolder.constraintLayoutRight.setVisibility(View.VISIBLE);
            myViewHolder.txtReceiverName.setText(childItem.getRepliedUser());
        String description = childItem.getComment().trim();
        description = description.replaceAll("HTTP:", "http:").replaceAll("Http:", "http:")
                .replaceAll("Https:", "https:")
                .replaceAll("HTTPS:", "https:");
        description = description.replaceAll("WWW.", "www.").replaceAll("Www.", "www.");
        myViewHolder.txtMessageReceived.setText(description);
        if(myViewHolder.txtMessageReceived!=null){
            Linkify.addLinks(myViewHolder.txtMessageReceived, Linkify.ALL); // linkify all links in text.
            //you can set link color with the help of text view property
            myViewHolder.txtMessageReceived.setLinkTextColor(ContextCompat.getColor(context,R.color.buttoncolor));
        }
        if (childItem.getProfilePic()!=null ) {
        String pro_url = S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + childItem.getProfilePic();

            Glide.with(context)
                    .load(pro_url)
                    .into(myViewHolder.imageReceiver);
        } else {
            myViewHolder.imageReceiver.setImageResource(R.drawable.avatar_male);
        }

        myViewHolder.txtMessageReceived.setOnLongClickListener(view -> {

                onLongClicked(position,parentPosition);

            return true;
        });
        myViewHolder.imgAttachmentReceived.setOnLongClickListener(v -> {

                onLongClicked(position,parentPosition);

            return true;
        });
        myViewHolder.txtTimeLeft.setText(dateFormat(childItem.getCreatedAt()));
        myViewHolder.imageReceiver.setOnClickListener(view -> {

            Intent intent = new Intent(context, ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setId(Integer.parseInt(SharedPref.getUserRegistration().getId()));
            familyMember.setUserId(Integer.parseInt(childItem.getUserId()));
            intent.putExtra(DATA, familyMember);
            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
            context.startActivity(intent);
        });
        if (childItem.getAttachmentList() != null && childItem.getAttachmentList().size() > 0) {
            myViewHolder.cardAttachLeft.setVisibility(View.VISIBLE);
            myViewHolder.txtMessageReceived.setVisibility(View.GONE);
            if (childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("mp4") || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("mov") || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("wmv") || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("webm") || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("mkv") || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("flv") || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("avi")) {
                myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.exo_icon_play);
                myViewHolder.imgPlayIconLeft.setVisibility(View.VISIBLE);

                myViewHolder.img_count_left_bg.setVisibility(View.GONE);
                myViewHolder.img_count_left.setVisibility(View.GONE);
                myViewHolder.imgAttachmentReceived.setOnClickListener(view -> {
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("URL", Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + childItem.getAttachmentList().get(0).getFilename());
                    intent.putExtra("NAME", childItem.getAttachmentList().get(0).getFilename());
                    context.startActivity(intent);
                });
                Glide.with(context)
                        .load(Constants.ApiPaths.IMAGE_BASE_URL + childItem.getAttachmentList().get(0).getVideo_thumb())
                        .into(myViewHolder.imgAttachmentReceived);
            } else if (childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("docx")
                    || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("pdf")
                    || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("doc")
                    || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("xls")
                    || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("xlsx")
                    || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("txt")
                    || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("ppt")
                    || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("zip")
                    || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("rar")
                    || childItem.getAttachmentList().get(0).getFilename().toLowerCase().contains("rtf")
            ) {
                myViewHolder.imgAttachmentReceived.setImageResource(0);
                myViewHolder.imgAttachmentReceived.setImageDrawable(null);
                myViewHolder.img_count_left_bg.setVisibility(View.GONE);
                myViewHolder.img_count_left.setVisibility(View.GONE);

                if(childItem.getAttachmentList().get(0).getType().contains("pdf")){
                    myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.icons_pdf);
                }
                else if(childItem.getAttachmentList().get(0).getType().contains("xls")||childItem.getAttachmentList().get(0).getType().contains("excel")||childItem.getAttachmentList().get(0).getType().contains("sheet"))
                {
                    myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.ms_excel);
                }
                else if(childItem.getAttachmentList().get(0).getType().contains("ppt")||childItem.getAttachmentList().get(0).getType().contains("presentation")||childItem.getAttachmentList().get(0).getType().contains("powerpoint"))
                {
                    myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.ms_powerpoint);
                }
                else if(childItem.getAttachmentList().get(0).getType().contains("doc")||childItem.getAttachmentList().get(0).getType().contains("word")  && (!childItem.getAttachmentList().get(0).getType().contains("presentation") || !childItem.getAttachmentList().get(0).getType().contains("sheet") ||!childItem.getAttachmentList().get(0).getType().contains("xls")))

                {
                    myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.ms_word);
                }
                else if(childItem.getAttachmentList().get(0).getType().contains("zip")||childItem.getAttachmentList().get(0).getType().contains("rar")||childItem.getAttachmentList().get(0).getType().contains("octet-stream"))
                {
                    myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.zip);
                }
//                else
//                {
//                    myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.doc);
//                }
//                //holder.imgPlayIconLeft.setImageResource(R.drawable.document_default_item);
//                myViewHolder.imgPlayIconLeft.setVisibility(View.VISIBLE);
//                myViewHolder.imgPlayIconLeft.setOnClickListener(view -> {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + childItem.getAttachmentList().get(0).getFilename()));
//                    context.startActivity(browserIntent);
//                });
                else
                {
                    /*
                     * megha,25/11/2021
                     * for checking the exceptional case of images with filename "Document-12233.jpg" */
                    if(childItem.getAttachmentList().get(0).getType().contains("image/png") || childItem.getAttachmentList().get(0).getType().contains("image")){
                        myViewHolder.imgPlayIconLeft.setVisibility(View.GONE);
                        myViewHolder.imgAttachmentReceived.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(S3_DEV_IMAGE_URL_SQUARE_DETAILED + Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + childItem.getAttachmentList().get(0).getFilename())
                                .into(myViewHolder.imgAttachmentReceived);
                    }else{
                        myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.doc);
                    }
//                    myViewHolder.imgPlayIconLeft.setImageResource(R.drawable.doc);
                }
                //holder.imgPlayIconLeft.setImageResource(R.drawable.document_default_item);
                if(childItem.getAttachmentList().get(0).getType().contains("image/png") || childItem.getAttachmentList().get(0).getType().contains("image")) {
                    myViewHolder.imgPlayIconLeft.setVisibility(View.GONE);
                    myViewHolder.imgAttachmentReceived.setVisibility(View.VISIBLE);
                    myViewHolder.imgAttachmentReceived.setOnClickListener(view -> {
                        Intent intent = new Intent(context, ChatImageFullView.class);
                        intent.putExtra(DATA, childItem.getImagesUrls());
                        context.startActivity(intent);
                    });
                }else{
                    myViewHolder.imgPlayIconLeft.setVisibility(View.VISIBLE);
                    myViewHolder.imgPlayIconLeft.setOnClickListener(view -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + childItem.getAttachmentList().get(0).getFilename()));
                        context.startActivity(browserIntent);
                    });

                }
                /*
                 * end of condition*/

                myViewHolder.imgPlayIconLeft.setOnLongClickListener(v -> {
                    onLongClicked(position,parentPosition);
                    return true;
                });
            } else {

                if (childItem.getAttachmentList().size() == 1) {
                    myViewHolder.img_count_left_bg.setVisibility(View.GONE);
                    myViewHolder.img_count_left.setVisibility(View.GONE);
                } else {
                    myViewHolder.img_count_left_bg.setVisibility(View.VISIBLE);
                    myViewHolder.img_count_left.setVisibility(View.VISIBLE);
                    myViewHolder.img_count_left.setText("+ " + (childItem.getAttachmentList().size() - 1));
                }
                myViewHolder.imgPlayIconLeft.setVisibility(View.GONE);
                Glide.with(context)
                        .load(S3_DEV_IMAGE_URL_SQUARE_DETAILED + Constants.ApiPaths.IMAGE_BASE_URL + "file_name/" + childItem.getAttachmentList().get(0).getFilename())
                        .into(myViewHolder.imgAttachmentReceived);

                myViewHolder.imgAttachmentReceived.setOnClickListener(view -> {

                    Intent intent = new Intent(context, ChatImageFullView.class);
                    intent.putExtra(DATA, childItem.getImagesUrls());
                    context.startActivity(intent);
                });
            }
        }else {
            myViewHolder.cardAttachLeft.setVisibility(View.GONE);
            myViewHolder.txtMessageReceived.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public int getItemCount() {
        return childItemList.size();
    }

    public interface OnChatLongClickListener {
        void onChatLongClicked(int position);
    }

    class ChildViewHolder
            extends RecyclerView.ViewHolder {
        private final ConstraintLayout constraintLayoutRight;
        private final TextView txtReceiverName;
        private final TextView txtMessageReceived;
        //private final TextView txtReplyRecieved;
        private final ImageView imageReceiver;
        private final TextView txtTimeLeft;
        private final CardView cardAttachLeft;
        private final ImageView imgPlayIconLeft;
        private final View img_count_left_bg;
        private final TextView img_count_left;
        private final ImageView imgAttachmentReceived;
private final LinearLayout text_message_body_received_view;
        ChildViewHolder(View itemView)
        {
            super(itemView);
            constraintLayoutRight = itemView.findViewById(R.id.constraintLayoutRight);
            txtReceiverName = itemView.findViewById(R.id.text_message_name_received);
            txtMessageReceived = itemView.findViewById(R.id.text_reply_received);

            imageReceiver = itemView.findViewById(R.id.image_received);
            txtTimeLeft = itemView.findViewById(R.id.txtTimeLeft);
            cardAttachLeft=itemView.findViewById(R.id.cardAttachLeft);
            imgPlayIconLeft = itemView.findViewById(R.id.imgPlayIconLeft);
            img_count_left_bg = itemView.findViewById(R.id.img_count_left_bg);
            img_count_left = itemView.findViewById(R.id.img_count_left);
            imgAttachmentReceived = itemView.findViewById(R.id.imgAttachmentReceived);
            text_message_body_received_view=itemView.findViewById(R.id.text_message_body_received_view);
        }
    }
    private void onLongClicked(int childPosition, int parentPosition) {
        postCommentListener.onReplyLongClickListener(childPosition,parentPosition);
    }
    private String dateFormat(String time) {
        //Megha(29/07/2021) for changing timezone
        for (int i=0;i<childItemList.size();i++){
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
}
