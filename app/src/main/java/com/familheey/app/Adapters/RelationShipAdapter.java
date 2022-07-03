package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.RelationShip;
import com.familheey.app.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RelationShipAdapter extends RecyclerView.Adapter<RelationShipAdapter.ViewHolder> implements Filterable {

    private final RelationShipSelectionListener mListener;
    private final List<RelationShip> relationShips;
    private List<RelationShip> relationShipsFiltered;
    private final FamilyMember familyMember;

    public RelationShipAdapter(RelationShipSelectionListener mListener, List<RelationShip> relationShips, FamilyMember familyMember) {
        this.relationShips = relationShips;
        this.relationShipsFiltered = relationShips;
        this.mListener = mListener;
        this.familyMember = familyMember;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    relationShipsFiltered = relationShips;
                } else {
                    ArrayList<RelationShip> filteredList = new ArrayList<>();
                    for (RelationShip relationShip : relationShips) {
                        if (relationShip.getRelationship().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(relationShip);
                        }
                    }
                    relationShipsFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = relationShipsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                relationShipsFiltered = (ArrayList<RelationShip>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_family_relationship, parent, false);
        RelationShipAdapter.ViewHolder vh = new RelationShipAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RelationShip familyRelationship = relationShipsFiltered.get(position);
        holder.relationShip.setText(familyRelationship.getRelationship());
        if (familyMember.getRelationShip() != null) {
            if (familyMember.getRelationShip().equalsIgnoreCase(familyRelationship.getRelationship())) {
                holder.isSelected.setVisibility(View.VISIBLE);
            } else holder.isSelected.setVisibility(View.INVISIBLE);
        } else holder.isSelected.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        if (relationShipsFiltered==null){
            return 0;
        }
        return relationShipsFiltered.size();
    }

    public interface RelationShipSelectionListener {
        void OnRelationShipSelected(RelationShip relationShipSelected);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relationShip)
        TextView relationShip;
        @BindView(R.id.isSelected)
        TextView isSelected;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick
        void onClick(View view) {
            mListener.OnRelationShipSelected(relationShipsFiltered.get(getAdapterPosition()));
        }
    }
}