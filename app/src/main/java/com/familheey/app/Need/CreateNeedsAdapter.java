package com.familheey.app.Need;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateNeedsAdapter extends RecyclerView.Adapter<CreateNeedsAdapter.ViewHolder> {

    private List<Item> needs;
    private CreateRequestAdapterListner listner;

    CreateNeedsAdapter(List<Item> needs, CreateRequestAdapterListner listner) {
        this.needs = needs;
        this.listner = listner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_create_need, parent, false);
        return new ViewHolder(listItem);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item need = needs.get(position);

        holder.text_tittle.setText(need.getRequest_item_title());
        holder.text_description.setText(need.getRequest_item_description());
        if ("fund".equals(need.getType())) {
            holder.txt_qty_lebal.setText("Amount");
            holder.txt_qty.setText("$" + need.getItem_quantity());
        } else {
            holder.txt_qty_lebal.setText("Quantity");
            holder.txt_qty.setText(need.getItem_quantity() + "");
        }

        holder.btn_delete.setOnClickListener(view -> listner.onDelete(position));
        holder.btn_edit.setOnClickListener(view -> listner.onEdit(position));
    }

    @Override
    public int getItemCount() {
        return needs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btn_edit)
        ImageView btn_edit;
        @BindView(R.id.btn_delete)
        ImageView btn_delete;
        @BindView(R.id.text_tittle)
        TextView text_tittle;
        @BindView(R.id.text_description)
        TextView text_description;
        @BindView(R.id.txt_qty)
        TextView txt_qty;
        @BindView(R.id.txt_qty_lebal)
        TextView txt_qty_lebal;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}