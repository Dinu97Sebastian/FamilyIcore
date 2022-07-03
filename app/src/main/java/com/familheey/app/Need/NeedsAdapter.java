package com.familheey.app.Need;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Barrier;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class NeedsAdapter extends RecyclerView.Adapter<NeedsAdapter.ViewHolder> {
    public static final int REQUEST_NEED_REQUEST_CODE = 1000;
    private List<Item> needs;
    private Need needRequest;
    private Fragment fragment;

    NeedsAdapter(Fragment fragment, Need needRequest) {
        this.needs = needRequest.getItems();
        this.needRequest = needRequest;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_needs, parent, false);
        return new ViewHolder(listItem);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item need = needs.get(position);
        holder.itemName.setText(need.getRequest_item_title());
        holder.itemDescription.setText(need.getRequest_item_description());
        holder.itemsHave.setText(String.valueOf(Math.round((need.getItem_quantity() - need.getReceived_amount()))));
        holder.totalItems.setText(String.valueOf(need.getItem_quantity()));
        holder.itemView.setOnClickListener(v -> {
            Intent requestDetailedIntent = new Intent(fragment.getContext(), NeedRequestDetailedActivity.class);
            requestDetailedIntent.putExtra(DATA, String.valueOf(needRequest.getPost_request_id()));
            fragment.startActivityForResult(requestDetailedIntent, REQUEST_NEED_REQUEST_CODE);
        });
        if (position == 2) {
            holder.itemView.setAlpha(0.8f);
            holder.applyGradient.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setAlpha(1f);
            holder.applyGradient.setVisibility(View.INVISIBLE);
        }
        if (need.isContributionCompleted()) {
            holder.itemNeeded.setVisibility(View.INVISIBLE);
            holder.itemContributionCompleted.setVisibility(View.VISIBLE);
        } else {
            holder.itemNeeded.setVisibility(View.VISIBLE);
            holder.itemContributionCompleted.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return needs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemName)
        TextView itemName;
        @BindView(R.id.itemDescription)
        TextView itemDescription;
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
        @BindView(R.id.applyGradient)
        ImageView applyGradient;
        @BindView(R.id.itemContributionCompleted)
        TextView itemContributionCompleted;
        @BindView(R.id.itemNeeded)
        LinearLayout itemNeeded;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}