package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.FamilheeyApplication;
import com.familheey.app.Models.Response.ViewContents;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubFoldersAdapter extends RecyclerView.Adapter<SubFoldersAdapter.ViewHolder> {
    List<ViewContents.Document> documents;
    OnDocumentSelectedListener mListener;
    boolean isAdmin;

    public SubFoldersAdapter(List<ViewContents.Document> documents, OnDocumentSelectedListener mListener, boolean isAdmin) {
        this.documents = documents;
        this.mListener = mListener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_file, parent, false);
        SubFoldersAdapter.ViewHolder viewHolder = new SubFoldersAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewContents.Document document = documents.get(position);
        int resourceid = R.drawable.document_default_item;
        String url = documents.get(position).getUrl();
        if (url != null) {
            if (document.getFileType().contains("pdf")) {
                resourceid = R.drawable.pdf;
            }
            else if(document.getFileType().contains("xls")|| document.getFileType().contains("excel")||document.getFileType().contains("sheet"))
            {
                resourceid = R.drawable.ms_excel;
            }
            else if(document.getFileType().contains("ppt")||document.getFileType().contains("presentation")||document.getFileType().contains("powerpoint"))
            {
                resourceid = R.drawable.ms_powerpoint;
            }
            else if(document.getFileType().contains("doc")||document.getFileType().contains("word") &&(!document.getFileType().contains("presentation")||!document.getFileType().contains("sheet")))
            {
                resourceid = R.drawable.ms_word;
            }
            else if(document.getFileType().contains("zip")||document.getFileType().contains("rar")||document.getFileType().contains("octet-stream"))
            {
                resourceid = R.drawable.zip;
            }
            else
            {
                resourceid = R.drawable.doc;
            }

        }
        if (url != null) {
            Glide.with(holder.itemView.getContext())
                    .load(Utilities.getS3ImageUrl(url, 100, 100))
                    .placeholder(resourceid)
                    .into(holder.imgFolder);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(resourceid)
                    .into(holder.imgFolder);
        }
        if (document.isLongPressed())
            holder.selectionIndicator.setVisibility(View.VISIBLE);
        else holder.selectionIndicator.setVisibility(View.INVISIBLE);
        holder.itemView.setOnClickListener(v -> {
            if (isAdmin) {
                if (document.getId().equals(0L))
                    return;
                if (isAlreadyInSelectionMode()) {
                    document.setLongPressed(!document.isLongPressed());
                    handleDeleteButton();
                    notifyDataSetChanged();
                } else {
                    mListener.onDocumentElementSelected(document, position);
                }
            } else {

                mListener.onDocumentElementSelected(document, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (isAdmin) {
                if (document.getId().equals(0L))
                    return true;
                document.setLongPressed(!document.isLongPressed());
                handleDeleteButton();
                notifyDataSetChanged();
                return true;
            }
            return false;
        });

        holder.imageViewMore.setOnClickListener(v -> {
            if (document.getId().equals(0L))
                return;
            showPopup(holder.imageViewMore, holder.getAdapterPosition());
        });

        if (document.getOriginalname() != null) {
            holder.txtFolderName.setText(document.getOriginalname());
            holder.txtFolderName.setVisibility(View.VISIBLE);
        } else {
            holder.txtFolderName.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    private void showPopup(ImageView imgMore, int position) {
        PopupMenu popup = new PopupMenu(imgMore.getContext(), imgMore);
        popup.getMenuInflater().inflate(R.menu.popup_menu_document, popup.getMenu());
        if (!isAdmin) {
            Menu m = popup.getMenu();
            m.removeItem(R.id.editName);
        }


        popup.setOnMenuItemClickListener(item -> {
            ViewContents.Document document = documents.get(position);
            if (item.getItemId() == R.id.downloadDocument) {
                if (document.getUrl() != null) {
                    Toast.makeText(imgMore.getContext(), "Downloading " + document.getOriginalname() + "...", Toast.LENGTH_SHORT).show();
                    if (document.getOriginalname() != null)
                        Utilities.downloadDocuments(FamilheeyApplication.getInstance().getApplicationContext(), document.getUrl(), document.getOriginalname());
                    else
                        Utilities.downloadDocuments(FamilheeyApplication.getInstance().getApplicationContext(), document.getUrl(), "document.pdf");
                } else {
                    Toast.makeText(imgMore.getContext(), "Unable to download " + document.getOriginalname(), Toast.LENGTH_SHORT).show();
                }
            }
            if (item.getItemId() == R.id.editName) {
                mListener.onDocumentSelectedForEditing(document, position);
            }
            return true;
        });
        popup.show();//showing popup menu
    }

    public boolean isAlreadyInSelectionMode() {
        for (ViewContents.Document document : documents) {
            if (document.isLongPressed())
                return true;
        }
        return false;
    }

    public void handleDeleteButton() {
        List<Long> selectedElementIds = new ArrayList<>();
        for (ViewContents.Document document : documents) {
            if (document.isLongPressed())
                selectedElementIds.add(document.getId());
        }
        if (selectedElementIds.size() == 0)
            mListener.onDocumentDeselected();
        else mListener.onDocumentSelectedForDeletion(selectedElementIds);
    }

    public interface OnDocumentSelectedListener {
        void onDocumentElementSelected(ViewContents.Document document, int position);

        void onDocumentSelectedForDeletion(List<Long> selectedElementIds);

        void onDocumentDeselected();

        void onDocumentSelectedForEditing(ViewContents.Document document, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtFolderName)
        TextView txtFolderName;
        @BindView(R.id.imgFolder)
        ImageView imgFolder;
        @BindView(R.id.selectionIndicator)
        ImageView selectionIndicator;
        @BindView(R.id.imageViewMore)
        ImageView imageViewMore;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}