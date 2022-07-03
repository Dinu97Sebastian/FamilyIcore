package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FamilyLinkingAdapter extends RecyclerView.Adapter<FamilyLinkingAdapter.ViewHolder> {


    private final ArrayList<Family> families;

    public FamilyLinkingAdapter(ArrayList<Family> families) {
        this.families = families;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_family_linking, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Family family = families.get(position);
        holder.familyName.setText(family.getGroupName());
        holder.familyName.setSelected(family.isDevSelected());
    }


    @Override
    public int getItemCount() {
        return families.size();
    }

    public ArrayList<Family> getLinkedFamilies() {
        ArrayList<Family> selectedFamilies = new ArrayList<>();
        for (int i = 0; i < families.size(); i++) {
            if (families.get(i).isDevSelected())
                selectedFamilies.add(families.get(i));
        }
        return selectedFamilies;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        @BindView(R.id.familyName)
        CheckBox familyName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            familyName.setOnCheckedChangeListener(this::onCheckedChanged);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            families.get(getAdapterPosition()).setDevSelected(isChecked);
        }
    }
}