package com.familheey.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.AddContactActivity;
import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Models.Response.EventContacts;
import com.familheey.app.R;

import java.util.List;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    List<EventContacts> contactList;
    Context context;
    boolean isAdmin = false;
    ContactsAdapter.ContactsAdapterInterface contactsAdapterInterface;

    public ContactsAdapter(List<EventContacts> contactList, Context context, boolean isAdmin) {
        this.contactList = contactList;
        this.context = context;
        this.isAdmin = isAdmin;
        contactsAdapterInterface = (CreatedEventDetailActivity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_contacts_events, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewNumber.setText(contactList.get(position).getPhone());
        holder.textViewName.setText(contactList.get(position).getName());
        holder.textViewEmail.setText(contactList.get(position).getEmail());
        if (isAdmin)
            holder.imageViewMore.setVisibility(View.VISIBLE);
        else holder.imageViewMore.setVisibility(View.INVISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactList.get(position).getPhone(), null));
                context.startActivity(intent);
            }
        });
        holder.imageViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(holder.imageViewMore, position);
            }
        });
    }

    private void showPopup(ImageView imgMore, int position) {
        PopupMenu popup = new PopupMenu(context, imgMore);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu_edit, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menuEdit) {
                    context.startActivity(new Intent(context, AddContactActivity.class).putExtra(DATA, contactList.get(position)));
                }

                if (item.getItemId() == R.id.menuDelete) {
                    contactsAdapterInterface.onDeleteContact(contactList.get(position).getId());
                }

                return true;
            }
        });

        popup.show();//showing popup menu
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEmail, textViewName, textViewNumber;
        ImageView imageViewMore;

        ViewHolder(View view) {
            super(view);
            textViewEmail = view.findViewById(R.id.textViewEmail);
            textViewNumber = view.findViewById(R.id.textViewNumber);
            textViewName = view.findViewById(R.id.textViewName);
            imageViewMore = view.findViewById(R.id.imageViewMore);

        }
    }

    public interface ContactsAdapterInterface {
        void onDeleteContact(String id);
    }

    public void setAdminStatus(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}