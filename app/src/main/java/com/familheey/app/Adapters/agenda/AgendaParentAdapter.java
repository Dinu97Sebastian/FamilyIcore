package com.familheey.app.Adapters.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Fragments.Events.AgendaFragment.AgendaEdit;
import com.familheey.app.Models.Response.ListAgendaResponse;
import com.familheey.app.R;

import java.util.ArrayList;

public class AgendaParentAdapter extends RecyclerView.Adapter<AgendaParentAdapter.HomeViewHolder> {
    Context context;
    AgendaChildAdapter horizontalAdapter;
    ArrayList<ListAgendaResponse.Datum> listAgendaResponse;
    AgendaEdit agendaEdit;
    private boolean isAdmin = false;

    public AgendaParentAdapter(Context context, ArrayList<ListAgendaResponse.Datum> listAgendaResponse, AgendaEdit agendaEdit) {
        this.context = context;
        this.listAgendaResponse = listAgendaResponse;
        this.agendaEdit = agendaEdit;

    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View theView = LayoutInflater.from(context).inflate(R.layout.row_layout_home, parent, false);

        return new HomeViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, final int position) {
        ListAgendaResponse.Datum datum = listAgendaResponse.get(position);
        holder.date.setText(datum.getFormattedDate());
        horizontalAdapter = new AgendaChildAdapter(context, datum.getData(), agendaEdit, isAdmin);
        holder.recyclerViewHorizontal.setAdapter(horizontalAdapter);

    }


    @Override
    public int getItemCount() {
        return listAgendaResponse.size();

    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        notifyDataSetChanged();
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