package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Models.Response.ListGroupFoldersResponse;
import com.familheey.app.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.ViewHolder> {
    List<ListGroupFoldersResponse.Row> rows;
    FolderAdapterInterface folderAdapterInterface;
    String groupId;
    boolean isAdmin = false;
    boolean canCreate = false;
    boolean canUpdate = false;
    String parentId;

    public FoldersAdapter(List<ListGroupFoldersResponse.Row> rows, FolderAdapterInterface folderAdapterInterface, String id) {
        this.rows = rows;
        this.folderAdapterInterface = folderAdapterInterface;
        groupId = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_folder, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListGroupFoldersResponse.Row folder = rows.get(position);
        holder.txtFolderName.setText(rows.get(position).getFolderName());
        holder.createdBy.setText("By " + folder.getCreatedByName());
        holder.createdDate.setText(rows.get(position).getFormattedDate());
        if (folder.isLongPressed())
            holder.selectionIndicator.setVisibility(View.VISIBLE);
        else holder.selectionIndicator.setVisibility(View.INVISIBLE);
        holder.itemView.setOnClickListener(v -> {
            if (isAlreadyInSelectionMode()) {
                folder.setLongPressed(!folder.isLongPressed());
                handleDeleteButton();
                notifyDataSetChanged();
            } else {
                folderAdapterInterface.onFolderClick(groupId, String.valueOf(rows.get(position).getId()), rows.get(position).getFolderName(), rows.get(position).getDescription(), isAdmin, canCreate, folder.canUpdate(parentId));
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (isAdmin || folder.canUpdate(folderAdapterInterface.getParentId())) {
                folder.setLongPressed(!folder.isLongPressed());
                handleDeleteButton();
                notifyDataSetChanged();
                return true;
            }else return false;
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    public boolean isAlreadyInSelectionMode() {
        for (ListGroupFoldersResponse.Row folder : rows) {
            if (folder.isLongPressed())
                return true;
        }
        return false;
    }

    public void handleDeleteButton() {
        List<Integer> selectedElementIds = new ArrayList<>();
        for (ListGroupFoldersResponse.Row folder : rows) {
            if (folder.isLongPressed())
                selectedElementIds.add(folder.getId());
        }
        if (selectedElementIds.size() == 0)
            folderAdapterInterface.onFolderDeselected();
        else folderAdapterInterface.onFolderSelectedForDeletion(selectedElementIds);
    }

    public void setAdminStatus(boolean isAdmin, boolean canCreate, boolean canUpdate, String parentId) {
        this.isAdmin = isAdmin;
        this.canCreate = canCreate;
        this.canUpdate = canUpdate;
        this.parentId = parentId;
        notifyDataSetChanged();
    }

    public interface FolderAdapterInterface {
        void onFolderClick(String id, String folderId, String title, String description, boolean isAdmin, boolean canCreate, boolean canUpdate);

        void onFolderSelectedForDeletion(List<Integer> selectedElementIds);

        void onFolderDeselected();

        String getParentId();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.txtCount)
//        TextView txtCount;
        @BindView(R.id.txtFolderName)
        TextView txtFolderName;
        @BindView(R.id.imgFolder)
        ImageView imgFolder;
        @BindView(R.id.createdBy)
        TextView createdBy;
        @BindView(R.id.createdDate)
        TextView createdDate;
        @BindView(R.id.selectionIndicator)
        ImageView selectionIndicator;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
