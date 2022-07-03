package com.familheey.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Models.Response.FetchCalendarResponse;
import com.familheey.app.Networking.utils.GsonUtils;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.ID;
import static com.familheey.app.Utilities.Constants.Paths.EVENT_IMAGE;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> implements Filterable {
    Context context;
    private List<FetchCalendarResponse.Datum> calendarEvents;
    private List<FetchCalendarResponse.Datum> calendarEventsFiltered;

    public CalendarAdapter(Context context, List<FetchCalendarResponse.Datum> calendarEvents) {
        this.context = context;
        this.calendarEvents = calendarEvents;
        this.calendarEventsFiltered = calendarEvents;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.calendar_recy_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FetchCalendarResponse.Datum calendarEvent = calendarEventsFiltered.get(position);
        if(calendarEvent.getEventImage()!=null){
            Glide.with(holder.itemView.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + EVENT_IMAGE + calendarEvent.getEventImage())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.family_logo)
                    .into(holder.familyLogo);
        }else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.family_logo)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.familyLogo);
        }
        if (calendarEvent.getCreatedByName() == null) {
            holder.createdBy.setVisibility(View.INVISIBLE);
            holder.labelCreatedBy.setVisibility(View.INVISIBLE);
        } else {
            holder.createdBy.setVisibility(View.VISIBLE);
            holder.labelCreatedBy.setVisibility(View.VISIBLE);
            holder.createdBy.setText(calendarEvent.getCreatedByName());
        }
        if (calendarEvent.getPublic() != null && calendarEvent.getPublic())
            holder.familyType.setText("Public event");
        else holder.familyType.setText("Private event");

        if (calendarEvent.getLocation() == null) {
            holder.familyLocation.setVisibility(View.INVISIBLE);
            holder.locationIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.familyLocation.setText(calendarEvent.getLocation());
            holder.familyLocation.setVisibility(View.VISIBLE);
            holder.locationIcon.setVisibility(View.VISIBLE);
        }
        holder.familyName.setText(calendarEvent.getEventName());
        holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(context, CreatedEventDetailActivity.class)
                .putExtra(ID, calendarEvent.getEventId() + "")
                .putExtra("OBJECT", calendarEventsFiltered.get(position) + "")
        ));
        if(calendarEvent.getIs_recurrence()==1){
            if (isSameDate(calendarEvent.getRecurrence_from_date(), calendarEvent.getRecurrence_to_date())) {
                if(calendarEvent.getIs_recurrence()==1){
                    String fromTime1 = dateFormat(calendarEvent.getRecurrence_from_date(), "hh:mm aa");
                    String toTime1 = dateFormat(calendarEvent.getRecurrence_to_date(), "hh:mm aa");

                    String fromTime = dateFormat(calendarEvent.getRecurrence_from_date(), "EEE");


                    holder.time.setText(String.format(fromTime + ", " + "%s - %s", fromTime1, toTime1));
                }
                else {
                    String fromTime1 = dateFormat(calendarEvent.getRecurrence_from_date(), "hh:mm aa");
                    String toTime1 = dateFormat(calendarEvent.getRecurrence_to_date(), "hh:mm aa");

                    String fromTime = dateFormat(calendarEvent.getRecurrence_from_date(), "EEE");


                    holder.time.setText(String.format(fromTime + ", " + "%s - %s", fromTime1, toTime1));
                }
            } else {

                String fromTime = dateFormat(calendarEvent.getRecurrence_from_date(), "EEE MMM dd");
                String toTime = dateFormat(calendarEvent.getRecurrence_to_date(), "EEE MMM dd, yyyy");

                String fromTime1 = dateFormat(calendarEvent.getRecurrence_from_date(), "hh:mm aa");
                String toTime1 = dateFormat(calendarEvent.getRecurrence_to_date(), "hh:mm aa");
                holder.time.setText(String.format("%s - %s" + " \n" + "%s - %s", fromTime, toTime, fromTime1, toTime1));
            }
        }
        else {
            if (isSameDate(calendarEvent.getFromDate(), calendarEvent.getToDate())) {


                String fromTime1 = dateFormat(calendarEvent.getFromDate(), "hh:mm aa");
                String toTime1 = dateFormat(calendarEvent.getToDate(), "hh:mm aa");

                String fromTime = dateFormat(calendarEvent.getFromDate(), "EEE");


                holder.time.setText(String.format(fromTime + ", " + "%s - %s", fromTime1, toTime1));

            } else {

                String fromTime = dateFormat(calendarEvent.getFromDate(), "EEE MMM dd");
                String toTime = dateFormat(calendarEvent.getToDate(), "EEE MMM dd, yyyy");

                String fromTime1 = dateFormat(calendarEvent.getFromDate(), "hh:mm aa");
                String toTime1 = dateFormat(calendarEvent.getToDate(), "hh:mm aa");
                holder.time.setText(String.format("%s - %s" + " \n" + "%s - %s", fromTime, toTime, fromTime1, toTime1));
            }

        }
    }

    @Override
    public int getItemCount() {
        return calendarEventsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<String> eventDatesSelected = GsonUtils.getInstance().getGson().fromJson(charSequence.toString(), new TypeToken<ArrayList<String>>() {
                }.getType());
                if (eventDatesSelected.size() == 0) {
                    calendarEventsFiltered = calendarEvents;
                } else {
                    List<FetchCalendarResponse.Datum> filteredList = new ArrayList<>();
                    for (FetchCalendarResponse.Datum row : calendarEvents) {
                        for (String selectedEventDate : eventDatesSelected) {
                            if (row.getFormattedDateForComparison().equalsIgnoreCase(selectedEventDate)) {
                                filteredList.add(row);
                            }
                        }
                    }
                    calendarEventsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = calendarEventsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                calendarEventsFiltered = (ArrayList<FetchCalendarResponse.Datum>) results.values;
                notifyDataSetChanged();
            }
        };
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
        @BindView(R.id.familyLogo)
        ImageView familyLogo;
        @BindView(R.id.familyName)
        TextView familyName;
        @BindView(R.id.labelCreatedBy)
        TextView labelCreatedBy;
        @BindView(R.id.createdBy)
        TextView createdBy;
        @BindView(R.id.familyType)
        TextView familyType;
        @BindView(R.id.locationIcon)
        ImageView locationIcon;
        @BindView(R.id.familyLocation)
        TextView familyLocation;
        @BindView(R.id.time)
        TextView time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
