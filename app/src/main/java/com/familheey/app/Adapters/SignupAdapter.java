package com.familheey.app.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Activities.CreatedEventDetailActivity;
import com.familheey.app.Models.Response.EventSignUp;
import com.familheey.app.R;
import com.familheey.app.Utilities.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.util.List;

public class SignupAdapter extends RecyclerView.Adapter<SignupAdapter.MyViewHolder> {
    private int selectedPosition;
    private List<EventSignUp> eventItemList;
    private boolean isLongPressed;
    private OnSignUpListener mListener;
    private SignupInterface signupInterface;
    private boolean doResrict = false;

    public SignupAdapter(OnSignUpListener mListener, List<EventSignUp> eventItemList, Context context) {
        this.eventItemList = eventItemList;
        this.mListener = mListener;
        signupInterface = (CreatedEventDetailActivity) context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.signup_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtTitle.setText(eventItemList.get(position).getSlotTitle());
        holder.txtNeeded.setText("of " + eventItemList.get(position).getItemQuantity());
        holder.txtCollected.setText("" + eventItemList.get(position).getmNeeded());
        holder.txtDescription.setText(eventItemList.get(position).getSlotDescription() == null ? "" : eventItemList.get(position).getSlotDescription());
        addViewMoreLogic(eventItemList.get(position).getSlotDescription() == null ? "" : eventItemList.get(position).getSlotDescription(), holder.txtDescription, holder.textTemp, holder.txt_less_or_more);
        holder.itemView.setOnClickListener(view -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });

        holder.eventSignUp.setOnClickListener(v ->
        {
            if (doResrict)
                return;
            mListener.addSignUp(eventItemList.get(position));
        });

        holder.viewSignUp.setOnClickListener(v ->
        {
            if (doResrict)
                return;
            mListener.viewSignUp(eventItemList.get(position));
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (doResrict)
                return false;
            isLongPressed = !isLongPressed;
            showSnackBar(holder.itemView, eventItemList.get(position).getId());
            return false;
        });

        if (isLongPressed) {
            if (position == selectedPosition) {
                holder.constraintBg.setBackgroundResource(R.drawable.corner_accent);
            } else {
                holder.constraintBg.setBackgroundResource(R.color.white);

            }
        }

        if (String.valueOf(eventItemList.get(position).getUserId()).equals(SharedPref.getUserRegistration().getId())) {
            holder.imgEdit.setVisibility(View.VISIBLE);
        }


        holder.imgEdit.setOnClickListener(view -> {
            if (doResrict)
                return;
            mListener.editSignUp(eventItemList.get(position));
        });

        if (eventItemList.get(position).getNeeded() <= 0) {
            holder.completed.setVisibility(View.VISIBLE);
            holder.labelCompleted.setVisibility(View.VISIBLE);
            holder.txtCollected.setVisibility(View.INVISIBLE);
            holder.txtNeeded.setVisibility(View.INVISIBLE);
            holder.eventSignUp.setVisibility(View.INVISIBLE);
            holder.txtLabelAvailable.setVisibility(View.INVISIBLE);
        } else {
            holder.completed.setVisibility(View.INVISIBLE);
            holder.labelCompleted.setVisibility(View.INVISIBLE);
            holder.txtCollected.setVisibility(View.VISIBLE);
            holder.txtNeeded.setVisibility(View.VISIBLE);
            holder.eventSignUp.setVisibility(View.VISIBLE);
            holder.txtLabelAvailable.setVisibility(View.VISIBLE);
        }

        try {
            holder.timeRange.setText(eventItemList.get(position).getTimeRange());
        } catch (ParseException e) {
            holder.timeRange.setText("");
            e.printStackTrace();
        }
    }

    private void showSnackBar(View itemView, int id) {
        Snackbar snackbar = Snackbar
                .make(itemView, "Delete Selected ", Snackbar.LENGTH_INDEFINITE)
                .setAction("Delete", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signupInterface.onDeleteSignupClicked(String.valueOf(id));
                    }
                });

        snackbar.show();
    }


    @Override
    public int getItemCount() {
        return eventItemList.size();
    }

    public interface OnSignUpListener {
        void viewSignUp(EventSignUp eventSignUp);

        void addSignUp(EventSignUp eventSignUp);

        void editSignUp(EventSignUp eventSignUp);
    }

    void addViewMoreLogic(String description, TextView albumDescription, TextView textTemp, TextView txt_less_or_more) {
        albumDescription.setText(description);
        textTemp.setText(description);
        textTemp.post(() -> {
            if (textTemp.getLineCount() > 2) {
                txt_less_or_more.setVisibility(View.VISIBLE);
                albumDescription.setMaxLines(2);
                albumDescription.setEllipsize(TextUtils.TruncateAt.END);
                albumDescription.setText(description);
            } else {
                txt_less_or_more.setVisibility(View.GONE);
                albumDescription.setText(description);
            }
        });
        albumDescription.setOnClickListener(v -> {
        });
        textTemp.setOnClickListener(v -> {
        });
        txt_less_or_more.setOnClickListener(v -> {
            if (txt_less_or_more.getText().equals("Read More")) {
                txt_less_or_more.setText("Read Less");
                albumDescription.setText(description);
                albumDescription.setMaxLines(Integer.MAX_VALUE);
                albumDescription.setEllipsize(null);
            } else {
                txt_less_or_more.setText("Read More");
                albumDescription.setMaxLines(2);
                albumDescription.setEllipsize(TextUtils.TruncateAt.END);
            }
        });
    }

    public void setRestriction(boolean doResrict) {
        this.doResrict = doResrict;
    }

    public interface SignupInterface {
        void onDeleteSignupClicked(String id);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintBg;
        TextView txtTitle, txtNeeded, txtCollected, txtDescription, timeRange, txtLabelAvailable, labelCompleted;
        ImageView imgEdit, completed;
        MaterialButton viewSignUp, eventSignUp;
        TextView txt_less_or_more;
        TextView textTemp;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCollected = itemView.findViewById(R.id.txtCollected);
            txtNeeded = itemView.findViewById(R.id.txtNeeded);
            constraintBg = itemView.findViewById(R.id.constraintBg);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            viewSignUp = itemView.findViewById(R.id.viewSignUp);
            eventSignUp = itemView.findViewById(R.id.eventSignUp);
            timeRange = itemView.findViewById(R.id.timeRange);
            completed = itemView.findViewById(R.id.completed);
            txtLabelAvailable = itemView.findViewById(R.id.textView45);
            labelCompleted = itemView.findViewById(R.id.labelCompleted);
            txt_less_or_more = itemView.findViewById(R.id.txt_less_or_more);
            textTemp = itemView.findViewById(R.id.txt_temp);
        }
    }
}
