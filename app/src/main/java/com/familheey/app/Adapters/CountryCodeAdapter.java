package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Models.Country;
import com.familheey.app.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.ViewHolder> implements Filterable {

    private CountryMediatorListener mListener;
    private List<Country> countries;
    private List<Country> countriesFiltered;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    countriesFiltered = countries;
                } else {
                    List<Country> filteredList = new ArrayList<>();
                    for (Country country : countries) {
                        if (country.getCountryName().toLowerCase().contains(charString.toLowerCase())) {// || country.getCountryCode().contains(constraint)
                            filteredList.add(country);
                        }
                    }
                    countriesFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = countriesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                countriesFiltered = (ArrayList<Country>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.countryName)
        TextView countryName;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick
        void onClick(View view) {
            mListener.OnCountrySelected(countriesFiltered.get(getAdapterPosition()));
        }
    }

    public CountryCodeAdapter(CountryMediatorListener mListener, List<Country> countries) {
        this.countries = countries;
        this.countriesFiltered = countries;
        this.mListener = mListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_country, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Country country = countriesFiltered.get(position);
        holder.countryName.setText(country.getCountryName());
    }

    @Override
    public int getItemCount() {
        return countriesFiltered.size();
    }

    public interface CountryMediatorListener {
        void OnCountrySelected(Country selectedCountry);
    }
}