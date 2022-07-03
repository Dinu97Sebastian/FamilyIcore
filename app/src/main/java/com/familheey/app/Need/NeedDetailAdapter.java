package com.familheey.app.Need;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.R;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NeedDetailAdapter extends RecyclerView.Adapter<NeedDetailAdapter.ViewHolder> {

    private Need need;
    private OnUserSupportListener mListener;

    NeedDetailAdapter(OnUserSupportListener mListener, Need need) {
        this.need = need;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_need_detail, parent, false);
        return new ViewHolder(listItem);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item need = this.need.getItems().get(position);
        holder.itemName.setText(need.getRequest_item_title());
        holder.itemDescription.setText(need.getRequest_item_description());
        if ("fund".equals(this.need.getRequest_type())) {
            holder.itemsHave.setText("$" + (Math.round((need.getItem_quantity() - need.getReceived_amount()))));
            holder.totalItems.setText("$" + need.getItem_quantity());
        } else {
            holder.itemsHave.setText(String.valueOf(Math.round((need.getItem_quantity() - need.getReceived_amount()))));
            holder.totalItems.setText(need.getItem_quantity() + "");
        }

        holder.support.setOnClickListener(v -> mListener.onUserContributionPrompted(need));
        holder.viewContributions.setOnClickListener(v -> {
            if (need.getTotalContribution() > 0)
                mListener.onUserContributorsRequested(need);
            else
                Toast.makeText(holder.itemView.getContext(), "No one has contributed", Toast.LENGTH_SHORT).show();
        });
        if (need.isContributionCompleted()) {
            holder.itemContributionCompleted.setVisibility(View.VISIBLE);
            holder.contributionsGroup.setVisibility(View.INVISIBLE);
        } else {
            holder.itemContributionCompleted.setVisibility(View.GONE);
            holder.contributionsGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return need.getItems().size();
    }

    public interface OnUserSupportListener {
        void onUserContributionPrompted(Item selectedContribution);

        void onUserContributorsRequested(Item selectedContribution);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemName)
        TextView itemName;
        @BindView(R.id.itemDescription)
        TextView itemDescription;
        @BindView(R.id.support)
        MaterialButton support;
        @BindView(R.id.totalItems)
        TextView totalItems;
        @BindView(R.id.labelOf)
        TextView labelOf;
        @BindView(R.id.itemsHave)
        TextView itemsHave;
        @BindView(R.id.itemStatus)
        TextView itemStatus;
        @BindView(R.id.viewContributions)
        MaterialButton viewContributions;
        @BindView(R.id.itemContributionCompleted)
        TextView itemContributionCompleted;
        @BindView(R.id.contributionsGroup)
        Group contributionsGroup;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}