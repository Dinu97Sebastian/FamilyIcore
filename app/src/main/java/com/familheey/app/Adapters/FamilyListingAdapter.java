package com.familheey.app.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.FamilyDashboardActivity;
import com.familheey.app.Interfaces.HomeInteractor;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.LOGO;

public class FamilyListingAdapter extends RecyclerView.Adapter<FamilyListingAdapter.ViewHolder> implements Filterable {


    private List<Family> familyList;
    private final List<Family> FiltetrfamilyList;
    private final String type;

    public FamilyListingAdapter(List<Family> familyList, String type) {
        this.familyList = familyList;
        this.FiltetrfamilyList = familyList;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_family_list, parent, false);
        return new ViewHolder(listItem);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Family currentFamily = familyList.get(position);
        holder.familyName.setText(currentFamily.getGroupName());
        holder.familyLocation.setText(currentFamily.getBaseRegion());
        holder.familyType.setText(currentFamily.getGroupCategory());
        holder.createdBy.setText(currentFamily.getCreatedBy());
        if (type.equals("SELECT")) {
            holder.btn_select.setVisibility(View.VISIBLE);
        } else {

            holder.btn_select.setVisibility(View.GONE);
        }
        if (currentFamily.getPostCount() == null) {
            holder.labeleventsCount.setVisibility(View.INVISIBLE);
            holder.eventsCount.setVisibility(View.INVISIBLE);
        } else {
            if (currentFamily.getPostCount().equalsIgnoreCase("0")) {
                holder.labeleventsCount.setVisibility(View.INVISIBLE);
                holder.eventsCount.setVisibility(View.INVISIBLE);
            } else {
                holder.labeleventsCount.setVisibility(View.VISIBLE);
                holder.eventsCount.setVisibility(View.VISIBLE);
            }
            holder.eventsCount.setText(currentFamily.getPostCount());
        }
        if (currentFamily.getMembersCount() == null) {
            holder.labelMembersCount.setVisibility(View.INVISIBLE);
            holder.membersCount.setVisibility(View.INVISIBLE);
        } else {
            if (currentFamily.getMembersCount().equalsIgnoreCase("0")) {
                holder.labelMembersCount.setVisibility(View.INVISIBLE);
                holder.membersCount.setVisibility(View.INVISIBLE);
            } else {
                holder.labelMembersCount.setVisibility(View.VISIBLE);
                holder.membersCount.setVisibility(View.VISIBLE);
            }
            holder.membersCount.setText(currentFamily.getMembersCount());
        }
        if (currentFamily.getLogo() != null) {
            Glide.with(holder.familyLogo.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + LOGO + currentFamily.getLogo())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.family_logo)
                    .into(holder.familyLogo);
        } else {
            Glide.with(holder.familyLogo.getContext())
                    .load(R.drawable.family_logo)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.familyLogo);
        }

        holder.btn_select.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return familyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.familyLogo)
        ImageView familyLogo;
        @BindView(R.id.familyName)
        TextView familyName;
        @BindView(R.id.createdBy)
        TextView createdBy;
        @BindView(R.id.familyType)
        TextView familyType;
        @BindView(R.id.familyLocation)
        TextView familyLocation;
        @BindView(R.id.membersCount)
        TextView membersCount;
        @BindView(R.id.eventsCount)
        TextView eventsCount;
        @BindView(R.id.familyOptions)
        ImageView familyOptions;
        @BindView(R.id.labeleventsCount)
        TextView labeleventsCount;
        @BindView(R.id.labelMembersCount)
        TextView labelMembersCount;
        @BindView(R.id.btn_select)
        TextView btn_select;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {

            if (familyList.get(getAdapterPosition()).getIsBlocked() != null && familyList.get(getAdapterPosition()).getIsBlocked()) {
                Toast.makeText(v.getContext(), "You have been blocked from this family!!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(v.getContext(), FamilyDashboardActivity.class);
            intent.putExtra(Constants.Bundle.DATA, familyList.get(getAdapterPosition()));
            if (android.os.Build.VERSION.SDK_INT > 21) {

                Pair logo = Pair.create(familyLogo, "logo");
                Pair name = Pair.create(familyName, "name");
                Pair type = Pair.create(familyType, "type");
                Pair member = Pair.create(labelMembersCount, "member");
                Pair post = Pair.create(labeleventsCount, "post");
                Pair location = Pair.create(familyLocation, "location");
                Pair mcount = Pair.create(membersCount, "mcount");
                Pair pcount = Pair.create(eventsCount, "pcount");


                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(), logo, name, type, location, member, post, mcount, pcount);
                v.getContext().startActivity(intent, transitionActivityOptions.toBundle());
            } else {

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) v.getContext(), familyLogo, "logo");
                v.getContext().startActivity(intent, options.toBundle());
            }

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                try {
                    String charString = charSequence.toString().toLowerCase();
                    if (charString.isEmpty()) {
                        familyList = FiltetrfamilyList;
                    } else {
                        List<Family> filteredData = new ArrayList<>();
                        for (Family row : FiltetrfamilyList) {
                            if (row.getGroupName().contains(charString)) {
                                filteredData.add(row);
                            }
                        }
                        familyList = filteredData;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = familyList;
                    return filterResults;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults == null)
                    return;
                familyList = (List<Family>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
