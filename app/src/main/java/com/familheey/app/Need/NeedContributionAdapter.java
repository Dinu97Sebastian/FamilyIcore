package com.familheey.app.Need;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Barrier;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NeedContributionAdapter extends RecyclerView.Adapter<NeedContributionAdapter.ViewHolder> {

    private List<Need> needs;

    NeedContributionAdapter(List<Need> needs) {
        this.needs = needs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_needs_contribution, parent, false);
        return new ViewHolder(listItem);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Need need = needs.get(position);

    }

    @Override
    public int getItemCount() {
        return needs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemName)
        TextView itemName;
        @BindView(R.id.edit)
        ImageView edit;
        @BindView(R.id.barrier)
        Barrier barrier;
        @BindView(R.id.totalItems)
        TextView totalItems;
        @BindView(R.id.labelOf)
        TextView labelOf;
        @BindView(R.id.itemsHave)
        TextView itemsHave;
        @BindView(R.id.itemStatus)
        TextView itemStatus;
        @BindView(R.id.itemTotalCountHighlight)
        TextView itemTotalCountHighlight;
        @BindView(R.id.eventSignUp)
        MaterialButton eventSignUp;
        @BindView(R.id.viewContributions)
        MaterialButton viewContributions;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}