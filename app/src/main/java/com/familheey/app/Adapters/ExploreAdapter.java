package com.familheey.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Activities.ShareEventActivity;
import com.familheey.app.Activities.SharelistActivity;
import com.familheey.app.Models.Response.Event;
import com.familheey.app.Models.Response.EventDetail;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.RecurrenceDate;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_COVER;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Paths.EVENT_IMAGE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.MyViewHolder>{
    private final Context context;
    private final List<Event> eventssharedEvents;
    private EventDetail details = new EventDetail();

    private final RequestOptions requestOptions;
    private List<RecurrenceDate> RecurrenceDateList;
    private Long recurFromDate;
    private Long recurToDate;
    private String recurDate;
    private String recrToDate;
    private String recurFromTime;
    private String recurToTime;
    private Long newNextDate;
    private Long formattedNewDate;
    private String day;
    private String month;
    private String months;
    private String strmonths;


    public ExploreAdapter(Context context, List<Event> events) {
        this.context = context;
        this.eventssharedEvents = events;
        requestOptions = new RequestOptions();
        requestOptions.transforms(new RoundedCorners(25));
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.explore_item_row, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Event event = eventssharedEvents.get(position);
        long millis = System.currentTimeMillis();
        if(position==4) {
            String formattedCurrentDate = formatDate(millis);
        }
        //holder.container.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_transition_animation));
        holder.txtName.setText(event.getFullName());
        holder.txtTitle.setText(event.getEventName());

        if(event.getIsRecurrence()==1){
            holder.txt_event_type.setText(event.getRecurrenceType());
            holder.txt_event_type.setVisibility(View.VISIBLE);
            holder.event_type_dot.setVisibility(View.VISIBLE);
            holder.txtOccurrence.setVisibility(View.VISIBLE);
            holder.txtOccurrence.setText("Occurrence : "+String.valueOf(event.getRecurrenceCount()));
            holder.txt_isRecurring.setVisibility(View.VISIBLE);
            holder.isRecurring_dot.setVisibility(View.VISIBLE);

        }
        else{
            holder.txt_isRecurring.setVisibility(View.GONE);
            holder.isRecurring_dot.setVisibility(View.GONE);
            holder.txt_event_type.setVisibility(View.GONE);
            holder.event_type_dot.setVisibility(View.GONE);
            holder.txtOccurrence.setVisibility(View.GONE);
        }
        if (event.isOnlineEvent()) {
            holder.meetingLink.addAutoLinkMode(
                    AutoLinkMode.MODE_URL);
            holder.meetingLink.setUrlModeColor(ContextCompat.getColor(context, R.color.buttoncolor));
            holder.locationIcon.setVisibility(View.INVISIBLE);
            holder.txtLocation.setVisibility(View.INVISIBLE);
            holder.meetingLink.setAutoLinkText(event.getFormattedMeetingLink());
            holder.meetingIcon.setVisibility(View.VISIBLE);
            holder.meetingLink.setVisibility(View.VISIBLE);
        } else {
            holder.meetingIcon.setVisibility(View.INVISIBLE);
            holder.meetingLink.setVisibility(View.INVISIBLE);
            if (eventssharedEvents.get(position).getLocation() != null && eventssharedEvents.get(position).getLocation().trim().length() > 0) {
                holder.txtLocation.setText(eventssharedEvents.get(position).getLocation());
                holder.locationIcon.setVisibility(View.VISIBLE);
                holder.txtLocation.setVisibility(View.VISIBLE);
            } else {
                holder.locationIcon.setVisibility(View.INVISIBLE);
                holder.txtLocation.setVisibility(View.INVISIBLE);
            }
        }
        if (event.getMeetingLogo() != null)
            Glide.with(holder.itemView.getContext())
                    .load(IMAGE_BASE_URL + event.getMeetingLogo())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.cursor_icon)
                    .into(holder.meetingIcon);
        else holder.meetingIcon.setImageResource(R.drawable.cursor_icon);
        holder.meetingLink.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            if (autoLinkMode == AutoLinkMode.MODE_URL) {
                try {
                    String url = matchedText.trim();
                    if (!url.contains("http")) {
                        url = url.replaceAll("www.", "http://www.");
                    }
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Nothing Can add copy to clipboard
            }
        });
        holder.txt_category.setText(Utilities.getFirstLetterCapitalizedWordInSentence(eventssharedEvents.get(position).getCategory()));
        holder.txt_type.setText(Utilities.getFirstLetterCapitalizedWordInSentence(eventssharedEvents.get(position).getEventType()));
        if (eventssharedEvents.get(position).getIsShared() != null && eventssharedEvents.get(position).getIsShared().equals("SHARE")) {
            holder.shareuser.setVisibility(View.VISIBLE);
        } else {
            holder.shareuser.setVisibility(View.GONE);
        }
        holder.shareuser.setOnClickListener(v -> {
            context.startActivity(new Intent(context, SharelistActivity.class)
                    .putExtra(Constants.Bundle.TYPE, "EVENT")
                    .putExtra("event_id", eventssharedEvents.get(position).getEventId().toString())
                    .putExtra("user_id", eventssharedEvents.get(position).getUserId().toString()));
        });

        holder.profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setId(Integer.parseInt(SharedPref.getUserRegistration().getId()));
            familyMember.setUserId(eventssharedEvents.get(position).getUserId());
            intent.putExtra(DATA, familyMember);
            intent.putExtra(Constants.Bundle.FOR_EDITING, true);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, holder.profileImage, "profile");
            context.startActivity(intent, options.toBundle());
        });

        holder.txtName.setOnClickListener(v -> {
            Intent userIntent = new Intent(context, ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setUserId(eventssharedEvents.get(position).getUserId());
            userIntent.putExtra(DATA, familyMember);
            context.startActivity(userIntent);
        });

        if (eventssharedEvents.get(position).getTicketType() != null && eventssharedEvents.get(position).getTicketType().contains("ticket"))
            holder.card_ticketed.setVisibility(View.INVISIBLE);
        else
            holder.card_ticketed.setVisibility(View.INVISIBLE);

        if (eventssharedEvents.get(position).getPublicEvent() != null && eventssharedEvents.get(position).getPublicEvent()) {
            holder.share.setVisibility(View.VISIBLE);
        } else {
            holder.share.setVisibility(View.INVISIBLE);
        }
//        holder.date.setText(dateFormat(eventssharedEvents.get(position).getFromDate(), "dd"));
//        holder.month.setText(dateFormat(eventssharedEvents.get(position).getFromDate(), "MMM"));
        if (eventssharedEvents.get(position).getCreatedAtListing() != null) {
            holder.txtTime.setText(dateFormat((long) Double.parseDouble(eventssharedEvents.get(position).getCreatedAtListing()), "MMM dd yyyy hh:mm aa"));
            holder.txtTime.setVisibility(View.VISIBLE);
        } else {
            holder.txtTime.setVisibility(View.INVISIBLE);
        }

        if (eventssharedEvents.get(position).getPropic() != null) {
            Glide.with(context)
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + eventssharedEvents.get(position).getPropic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .apply(requestOptions).transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.profileImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.profileImage);
        }
        if (eventssharedEvents.get(position).getEventImage() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(S3_DEV_IMAGE_URL_COVER + IMAGE_BASE_URL + EVENT_IMAGE + eventssharedEvents.get(position).getEventImage())
                    .placeholder(R.drawable.default_event_image)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.eventImage);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.default_event_image)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.eventImage);
        }

        int goingCount = 0;
        if (eventssharedEvents.get(position).getGoingCount() != null) {
            goingCount = Integer.parseInt(eventssharedEvents.get(position).getGoingCount());

        }
        int interestedCount = 0;
        if (eventssharedEvents.get(position).getInterestedCount() != null) {
            interestedCount = Integer.parseInt(eventssharedEvents.get(position).getInterestedCount());

        }


        if (goingCount == 0) {
            holder.going.setVisibility(View.GONE);
        } else {
            holder.going.setVisibility(View.VISIBLE);
        }

        if (interestedCount == 0) {
            holder.interested.setVisibility(View.GONE);
        } else {
            holder.interested.setVisibility(View.VISIBLE);
        }


        holder.interested.setText(String.format("%s interested", eventssharedEvents.get(position).getInterestedCount()));

        if (interestedCount == 0) {
            holder.going.setText(String.format("%s going", eventssharedEvents.get(position).getGoingCount()));
        } else {
            holder.going.setText(String.format("%s going | ", eventssharedEvents.get(position).getGoingCount()));
        }

        int count = 0;

        if (eventssharedEvents.get(position).getGoingCount() != null) {
            count = Integer.parseInt(eventssharedEvents.get(position).getGoingCount()) - 1;

        }


        if (eventssharedEvents.get(position).getFirstPersonGoing() != null && count > 0) {
            holder.txtJoin.setText(eventssharedEvents.get(position).getFirstPersonGoing() + " and " + count + " others are going");
        } else if (eventssharedEvents.get(position).getFirstPersonGoing() != null && count == 0) {
            holder.txtJoin.setText(eventssharedEvents.get(position).getFirstPersonGoing() + " is going");
        } else {
            holder.txtJoin.setText("");
        }


        if (isSameDate(eventssharedEvents.get(position).getFromDate(), eventssharedEvents.get(position).getToDate())) {

            if (event.getIsRecurrence()==1 && event.getRecurrenceDate().size()>0) {
                RecurrenceDateList = event.getRecurrenceDate();
                for (int j = 0; j < RecurrenceDateList.size(); j++) {
                    int compareValue1= compareDate(RecurrenceDateList.get(j).getRecurrence_from_date());
                    int compareValue2= compareDate(RecurrenceDateList.get(j).getRecurrence_to_date());
                        /* 0 comes when two date are same,
                        1 comes when date1 is higher then date2
                        -1 comes when date1 is lower then date2*/

                    if((compareValue1==1 && compareValue2== -1) ||compareValue1== -1 || compareValue1==0){
                        String fromTime1 = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "hh:mm aa");
                        String toTime1 = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "hh:mm aa");

                        String fromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE");
                        holder.date.setText(dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "dd"));
                        holder.month.setText(dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "MMM"));
                        holder.time.setText(String.format(fromTime + ", " + "%s - %s", fromTime1, toTime1));
                        break;
                    }
                    else if ((j==RecurrenceDateList.size()-1) && (compareValue1==1 && compareValue2==1)){
                        String fromTime1 = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "hh:mm aa");
                        String toTime1 = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "hh:mm aa");

                        String fromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE");
                        holder.date.setText(dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "dd"));
                        holder.month.setText(dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "MMM"));
                        holder.time.setText(String.format(fromTime + ", " + "%s - %s", fromTime1, toTime1));
                        break;
                    }
                }
            }
            else {


                String fromTime1 = dateFormat(eventssharedEvents.get(position).getFromDate(), "hh:mm aa");
                String toTime1 = dateFormat(eventssharedEvents.get(position).getToDate(), "hh:mm aa");

                String fromTime = dateFormat(eventssharedEvents.get(position).getFromDate(), "EEE");

                holder.date.setText(dateFormat(eventssharedEvents.get(position).getFromDate(), "dd"));
                holder.month.setText(dateFormat(eventssharedEvents.get(position).getFromDate(), "MMM"));
                holder.time.setText(String.format(fromTime + ", " + "%s - %s", fromTime1, toTime1));
            }
        } else {

            if (event.getIsRecurrence()==1) {
                RecurrenceDateList = event.getRecurrenceDate();
                for (int j = 0; j < RecurrenceDateList.size(); j++) {
                    int compareValue1= compareDate(RecurrenceDateList.get(j).getRecurrence_from_date());
                    int compareValue2= compareDate(RecurrenceDateList.get(j).getRecurrence_to_date());
                        /* 0 comes when two date are same,
                        1 comes when date1 is higher then date2
                        -1 comes when date1 is lower then date2*/

                    if((compareValue1==1 && compareValue2== -1) ||compareValue1== -1 || compareValue1==0){
                        String fromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE MMM dd");
                        String toTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "EEE MMM dd, yyyy");
                        holder.time.setText(String.format("%s - %s", fromTime, toTime));
                        holder.date.setText(dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "dd"));
                        holder.month.setText(dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "MMM"));
                        break;
                    }
                    else if ((j==RecurrenceDateList.size()-1) && (compareValue1==1 && compareValue2==1)){
                        String fromTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "EEE MMM dd");
                        String toTime = dateFormat(RecurrenceDateList.get(j).getRecurrence_to_date(), "EEE MMM dd, yyyy");
                        holder.time.setText(String.format("%s - %s", fromTime, toTime));
                        holder.date.setText(dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "dd"));
                        holder.month.setText(dateFormat(RecurrenceDateList.get(j).getRecurrence_from_date(), "MMM"));
                        break;
                    }

                }
            }
            else {
                String fromTime = dateFormat(eventssharedEvents.get(position).getFromDate(), "EEE MMM dd");
                String toTime = dateFormat(eventssharedEvents.get(position).getToDate(), "EEE MMM dd, yyyy");
                holder.time.setText(String.format("%s - %s", fromTime, toTime));
                holder.date.setText(dateFormat(eventssharedEvents.get(position).getFromDate(), "dd"));
                holder.month.setText(dateFormat(eventssharedEvents.get(position).getFromDate(), "MMM"));
            }
        }

            holder.itemView.setOnClickListener(view -> {
            context.startActivity(new Intent(context, CreatedEventDetailActivity.class)
                    .putExtra("OBJECT", eventssharedEvents.get(position))
                    .putExtra(ID, eventssharedEvents.get(position).getEventId() + ""));

        });

        holder.share.setOnClickListener(view -> {
            Intent intent = new Intent(context, ShareEventActivity.class);
            intent.putExtra(Constants.Bundle.DATA, eventssharedEvents.get(position).getEventId() + "");
            intent.putExtra(Constants.Bundle.TYPE, "EVENT");
            context.startActivity(intent);
        });
    }
    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
    private String formatRecuEndDate(Long endDate){
        Date date = new Date(endDate * 1000L);
// format of the date
        SimpleDateFormat jdf = new SimpleDateFormat("EEE, MMM dd yyyy");
        String java_date = jdf.format(date);
        return java_date;
    }

    private String dateFormats(Long value, String format) {
        if (value != null) {
            value = TimeUnit.SECONDS.toMillis(value);
            DateTime dateTime = new DateTime(value);

            DateTimeFormatter dtfOut = DateTimeFormat.forPattern(format);
            return dtfOut.print(dateTime);
        }
        return "";
    }

    public boolean isPastTime(Long recurToDateInfo) {
        if (recurToDateInfo== null)
            return true;
        Long toTime = TimeUnit.SECONDS.toMillis(recurToDateInfo);
        DateTime dateTime = new DateTime(toTime);
        DateTime currentTime = DateTime.now();
        return dateTime.isBefore(currentTime);
    }
    public int compareDate(Long formatDate){
        Calendar toDayCalendar = Calendar.getInstance();
        Date date1 = toDayCalendar.getTime();
        long timestampLong = Long.parseLong(formatDate.toString())*1000;
        Date date2 = new Date(timestampLong);

// date1 is a present date and date2 is tomorrow date
        int value =date1.compareTo(date2);
        return value;

//            //  0 comes when two date are same,
//            //  1 comes when date1 is higher then date2
//            // -1 comes when date1 is lower then date2




    }
    private String formatDate(long milliseconds) /* This is your topStory.getTime()*1000 */ {
        DateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);
        return sdf.format(calendar.getTime());
    }

    private String dateFormat(Long value, String format) {
        if (value != null) {
            value = TimeUnit.SECONDS.toMillis(value);
            DateTime dateTime = new DateTime(value);
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern(format);
            return dtfOut.print(dateTime);
        }
        return "";
    }


    @Override
    public int getItemCount() {
        return eventssharedEvents.size();
    }


    public interface ExplorerInterface {
        void onExploreItemClick(String id);
    }

    private Boolean isSameDate(Long from, Long to) {

        if (from != null && to != null) {
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyyMMdd");
            from = TimeUnit.SECONDS.toMillis(from);
            to = TimeUnit.SECONDS.toMillis(to);
            DateTime date1 = new DateTime(from);
            DateTime date2 = new DateTime(to);
            return dtfOut.print(date1).equals(dtfOut.print(date2));
        } else return false;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtTitle, txtLocation, date, month, going, interested, time, txtTime, txtJoin, txt_category, txt_type,txt_event_type,txtOccurrence,txt_isRecurring;
        View card_ticketed, container, share,event_type_dot,isRecurring_dot;
        ImageView eventImage, profileImage, shareuser, locationIcon, meetingIcon;
        AutoLinkTextView meetingLink;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJoin = itemView.findViewById(R.id.txt_join);
            txtName = itemView.findViewById(R.id.txtName);
            going = itemView.findViewById(R.id.going_tv);
            time = itemView.findViewById(R.id.time);
            txtTime = itemView.findViewById(R.id.txtTime);
            interested = itemView.findViewById(R.id.interested_tv);

            date = itemView.findViewById(R.id.date);
            month = itemView.findViewById(R.id.month);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            card_ticketed = itemView.findViewById(R.id.card_ticketed);
            container = itemView.findViewById(R.id.container);
            share = itemView.findViewById(R.id.share);
            eventImage = itemView.findViewById(R.id.roundedImageView);
            profileImage = itemView.findViewById(R.id.imageViewUser);
            shareuser = itemView.findViewById(R.id.shareuser);
            txt_category = itemView.findViewById(R.id.txt_category);
            txt_type = itemView.findViewById(R.id.txt_type);
            locationIcon = itemView.findViewById(R.id.venuicon);
            meetingLink = itemView.findViewById(R.id.meetingLink);
            meetingIcon = itemView.findViewById(R.id.meetingIcon);
            txt_event_type=itemView.findViewById(R.id.txt_event_type);
            event_type_dot=itemView.findViewById(R.id.event_type_dot);
            txtOccurrence=itemView.findViewById(R.id.txtOccurrence);
            txt_isRecurring=itemView.findViewById(R.id.txt_isRecurring);
            isRecurring_dot=itemView.findViewById(R.id.isRecurring_dot);
        }
    }


}
