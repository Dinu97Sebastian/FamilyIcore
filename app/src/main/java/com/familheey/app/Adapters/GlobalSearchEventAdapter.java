package com.familheey.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Activities.ProfileActivity;
import com.familheey.app.Activities.ShareEventActivity;
import com.familheey.app.Activities.SharelistActivity;
import com.familheey.app.Models.Response.Event;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_COVER;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Paths.EVENT_IMAGE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class GlobalSearchEventAdapter extends RecyclerView.Adapter<GlobalSearchEventAdapter.MyViewHolder> {
    private final Context context;
    private final List<Event> eventssharedEvents;
    private final RequestOptions requestOptions;

    public GlobalSearchEventAdapter(Context context, List<Event> events) {
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
        //holder.container.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_transition_animation));
        holder.txtName.setText(eventssharedEvents.get(position).getFullName());
        holder.txtTitle.setText(eventssharedEvents.get(position).getEventName());
        holder.txtLocation.setText(eventssharedEvents.get(position).getLocation());
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
                    .putExtra("event_id", eventssharedEvents.get(position).getId().toString())
                    .putExtra("user_id", eventssharedEvents.get(position).getUserId().toString()));
        });

        holder.profileImage.setOnClickListener(v -> {
            Intent userIntent = new Intent(context, ProfileActivity.class);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setUserId(eventssharedEvents.get(position).getUserId());
            userIntent.putExtra(DATA, familyMember);
            context.startActivity(userIntent);
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
        holder.date.setText(dateFormat(eventssharedEvents.get(position).getFromDate(), "dd"));
        holder.month.setText(dateFormat(eventssharedEvents.get(position).getFromDate(), "MMM"));
        if (eventssharedEvents.get(position).getCreatedAtListing() != null) {
            holder.txtTime.setText(dateFormat((long) Double.parseDouble(eventssharedEvents.get(position).getCreatedAtListing()), "MMM dd yyyy hh:mm aa"));
            holder.txtTime.setVisibility(View.VISIBLE);
        } else {
            holder.txtTime.setVisibility(View.INVISIBLE);
        }

        if(eventssharedEvents.get(position).getPropic()!=null){
            Glide.with(context)
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + eventssharedEvents.get(position).getPropic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .apply(requestOptions).transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.avatar_male)
                    .into(holder.profileImage);
        }else {
            Glide.with(context)
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .apply(requestOptions).transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.profileImage);
        }

        if(eventssharedEvents.get(position).getEventImage()!=null){
            Glide.with(holder.itemView.getContext())
                    .load(S3_DEV_IMAGE_URL_COVER + IMAGE_BASE_URL + EVENT_IMAGE + eventssharedEvents.get(position).getEventImage())
                    .placeholder(R.drawable.default_event_image)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.eventImage);
        }else {
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
            String fromTime1 = dateFormat(eventssharedEvents.get(position).getFromDate(), "hh:mm aa");
            String toTime1 = dateFormat(eventssharedEvents.get(position).getToDate(), "hh:mm aa");

            String fromTime = dateFormat(eventssharedEvents.get(position).getFromDate(), "EEE");
            holder.time.setText(String.format(fromTime + ", " + "%s - %s", fromTime1, toTime1));
        } else {

            String fromTime = dateFormat(eventssharedEvents.get(position).getFromDate(), "EEE MMM dd");
            String toTime = dateFormat(eventssharedEvents.get(position).getToDate(), "EEE MMM dd, yyyy");
            holder.time.setText(String.format("%s - %s", fromTime, toTime));
        }


        holder.itemView.setOnClickListener(view -> {
            context.startActivity(new Intent(context, CreatedEventDetailActivity.class).putExtra(ID, eventssharedEvents.get(position).getId() + ""));
        });

        holder.share.setOnClickListener(view -> {
            Intent intent = new Intent(context, ShareEventActivity.class);
            intent.putExtra(Constants.Bundle.DATA, eventssharedEvents.get(position).getId() + "");
            intent.putExtra(Constants.Bundle.TYPE, "EVENT");
            context.startActivity(intent);
        });
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

    public interface ExplorerInterface {
        void onExploreItemClick(String id);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtTitle, txtLocation, date, month, going, interested, time, txtTime, txtJoin, txt_category, txt_type;
        View card_ticketed, container, share;
        ImageView eventImage, profileImage, shareuser;


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
        }
    }
}
