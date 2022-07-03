package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Fragments.Events.AgendaFragment.DateClicked;
import com.familheey.app.Fragments.Events.AgendaFragment.DateModal;
import com.familheey.app.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseDateAdapter extends RecyclerView.Adapter<ChooseDateAdapter.ViewHolder> {
    ArrayList<DateModal> dateModals;
    DateClicked dateClicked;
    int prevSelect = 0;

    public ChooseDateAdapter(ArrayList<DateModal> dateModals, DateClicked dateClicked) {
        this.dateModals = dateModals;
        this.dateClicked = dateClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.choose_date, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.date.setText(dateModals.get(position).getDay());
        holder.month.setText(dateModals.get(position).getMonth());
        holder.week.setText(dateModals.get(position).getWeek());

        if (dateModals.get(position).isSelected()) {
            holder.parent.setBackground(holder.parent.getContext().getResources().getDrawable(R.drawable.layout_border_outline_color_selected));
        } else {

            holder.parent.setBackground(holder.parent.getContext().getResources().getDrawable(R.drawable.layout_border_outline_color));


        }
        holder.parent.setOnClickListener(view -> {

            if (prevSelect != 0 && prevSelect - 1 != position) {

                dateModals.get(prevSelect - 1).setSelected(false);
                notifyDataSetChanged();

            }

            prevSelect = position + 1;
            dateModals.get(prevSelect - 1).setSelected(true);

            view.setBackground(view.getContext().getResources().getDrawable(R.drawable.layout_border_outline_color_selected));
            dateClicked.itemClicked(dateModals.get(position), position);


        });
    }

    @Override
    public int getItemCount() {
        return dateModals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.month)
        TextView month;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.week)
        TextView week;
        @BindView(R.id.parent)
        View parent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
