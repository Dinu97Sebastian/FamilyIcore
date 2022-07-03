package com.familheey.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Models.MyCompleteEvent;
import com.familheey.app.R;

import java.util.List;

public class MyEventsParentAdapter extends RecyclerView.Adapter<MyEventsParentAdapter.HomeViewHolder> {
    Context context;
    List<MyCompleteEvent> myCompleteEvents;
    ExploreAdapter exploreAdapter;

    public MyEventsParentAdapter(Context context, List<MyCompleteEvent> myCompleteEvents) {
        this.context = context;
        this.myCompleteEvents = myCompleteEvents;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View theView = LayoutInflater.from(context).inflate(R.layout.row_layout_home, parent, false);

        return new HomeViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, final int position) {
        MyCompleteEvent myEvent = myCompleteEvents.get(position);
        holder.date.setText(myEvent.getEventType());
        exploreAdapter = new ExploreAdapter(context, myEvent.getEvents());
        holder.recyclerViewHorizontal.setAdapter(exploreAdapter);
    }


    @Override
    public int getItemCount() {
        return myCompleteEvents.size();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewHorizontal;
        TextView date;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerViewHorizontal = itemView.findViewById(R.id.home_recycler_view_horizontal);
            date = itemView.findViewById(R.id.date);
            recyclerViewHorizontal.setLayoutManager(new LinearLayoutManager(context));
        }
    }
}
