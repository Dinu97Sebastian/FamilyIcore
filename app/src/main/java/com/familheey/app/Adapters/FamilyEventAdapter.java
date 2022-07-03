package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Models.Response.Event;
import com.familheey.app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FamilyEventAdapter extends RecyclerView.Adapter<FamilyEventAdapter.MyViewHolder> {

    private final List<Event> events;
    private final OnFamilyEventSelectedListener mListener;

    public FamilyEventAdapter(OnFamilyEventSelectedListener mListener, List<Event> events) {
        this.events = events;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.my_events_row, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Event familyEvent = events.get(position);
        holder.share.setVisibility(View.INVISIBLE);
        //holder.container.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_transition_animation));
        holder.txtName.setText(familyEvent.getCreatedByName());
        holder.txtTitle.setText(familyEvent.getEventName());
        holder.txtLocation.setText(familyEvent.getLocation());
        if (familyEvent.getTicketType().contains("ticket")) {
            holder.ticketed_card.setVisibility(View.VISIBLE);
        }


        String datetime = familyEvent.getCreatedAt(); //


        try {

            if (datetime != null) {

//                String[] temp = datetime.split("T")[0].split("-");
//                String dateParsed = temp[1];
//                String month = temp[2];

                Calendar cal = Calendar.getInstance();

                String s;
                if (datetime.contains("."))
                    s = datetime.split("\\.")[0];
                else
                    s = datetime;
                long temp = Long.parseLong(s);
                String createdTime = formatdate(temp);


                Date date = new Date(TimeUnit.SECONDS.toMillis(temp));
                cal.setTime(date);

                SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.getDefault());
                SimpleDateFormat vdate = new SimpleDateFormat("dd", Locale.getDefault());


                String month_name = month_date.format(cal.getTime());
                String da = vdate.format(cal.getTime());

                holder.month.setText(month_name);
                holder.date.setText(da);
                holder.txtTime.setText(createdTime);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        String fromTime = formatdate(events.get(position).getFromDate());
        String toTime = formatdate(events.get(position).getToDate());
        holder.time.setText(String.format("%s - %s", fromTime, toTime));


        holder.container.setOnClickListener(view -> {
            mListener.onFamilyEventSelected(familyEvent);

        });
    }

    private String formatdate(Long value) {


        value = TimeUnit.SECONDS.toMillis(value);

        Date date = new Date(value);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public interface OnFamilyEventSelectedListener {
        void onFamilyEventSelected(Event event);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtTitle, txtLocation, date, month, time, txtTime;
        View ticketed_card, container;
        ImageView share;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            ticketed_card = itemView.findViewById(R.id.ticketed_card);
            container = itemView.findViewById(R.id.container);

            date = itemView.findViewById(R.id.date);
            month = itemView.findViewById(R.id.month);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            container = itemView.findViewById(R.id.container);
            share = itemView.findViewById(R.id.imageView9);

            time = itemView.findViewById(R.id.time);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}