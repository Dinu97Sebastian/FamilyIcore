package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Response.SignUpContributor;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.familheey.app.Utilities.Utilities;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Paths.PROFILE_PIC;

public class VolunteersAdapter extends RecyclerView.Adapter<VolunteersAdapter.ViewHolder> {


    private final OnSignUpVolunteersChangedListener mListener;
    private final List<SignUpContributor> signUpContributors;
    private boolean isAdmin = false;

    public VolunteersAdapter(OnSignUpVolunteersChangedListener mListener, List<SignUpContributor> signUpContributors, boolean isAdmin) {
        this.signUpContributors = signUpContributors;
        this.mListener = mListener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_event_volunteer, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SignUpContributor signUpContributor = signUpContributors.get(position);
        if(signUpContributor.getPropic()!=null){
            Glide.with(holder.itemView.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + PROFILE_PIC + signUpContributor.getPropic())
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .placeholder(R.drawable.avatar_male)
                    .into(holder.volunteerLogo);
        }else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.avatar_male)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.volunteerLogo);
        }
        holder.volunteerName.setText(signUpContributor.getFullName());
        holder.slot.setText(signUpContributor.getQuantityCollected() + " slots");
        if (signUpContributor.getFormateedRepliedDate() == null) {
            holder.volunteerDate.setText("");
            holder.volunteerReply.setVisibility(View.INVISIBLE);
        } else {
            holder.volunteerDate.setText(signUpContributor.getFormateedRepliedDate());
            holder.volunteerReply.setVisibility(View.VISIBLE);
        }
        if ((SharedPref.getUserRegistration().getId().equalsIgnoreCase(signUpContributor.getContributeUserId().toString()))) {
            holder.editQuantity.setVisibility(View.VISIBLE);
        } else holder.editQuantity.setVisibility(View.GONE);
        holder.editQuantity.setOnClickListener(v -> mListener.onSignUpVolunteerQuantityUpdateRequested(signUpContributor));
    }

    @Override
    public int getItemCount() {
        return signUpContributors.size();
    }

    public interface OnSignUpVolunteersChangedListener {
        void onSignUpVolunteerQuantityUpdateRequested(SignUpContributor signUpContributor);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.volunteerLogo)
        ImageView volunteerLogo;
        @BindView(R.id.volunteerName)
        TextView volunteerName;
        @BindView(R.id.volunteerReply)
        TextView volunteerReply;
        @BindView(R.id.volunteerDate)
        TextView volunteerDate;
        @BindView(R.id.slot)
        TextView slot;
        @BindView(R.id.editQuantity)
        ImageView editQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
