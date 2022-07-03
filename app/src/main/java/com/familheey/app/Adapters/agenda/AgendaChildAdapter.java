package com.familheey.app.Adapters.agenda;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Activities.ImageChangerActivity;
import com.familheey.app.Fragments.Events.AgendaFragment.AgendaEdit;
import com.familheey.app.Models.Response.ListAgendaResponse;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;
import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.TYPE;

public class AgendaChildAdapter extends RecyclerView.Adapter<AgendaChildAdapter.MovieViewHolder> {

    private boolean isAdmin = false;
    AgendaEdit agendaEdit;

    public AgendaChildAdapter(Context context, List<ListAgendaResponse.Datum> data, AgendaEdit agendaEdit, boolean isAdmin) {
        this.context = context;
        this.data = data;
        this.agendaEdit = agendaEdit;
        this.isAdmin = isAdmin;
    }

    private Context context;
    List<ListAgendaResponse.Datum> data;


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MovieViewHolder(LayoutInflater.from(context).inflate(R.layout.agenda_child_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, final int position) {
        ListAgendaResponse.Datum datum = data.get(position);

        holder.txtCardTitle.setText(datum.getTitle());
        holder.txtStartTime.setText(datum.getFormattedStartTime());
        holder.txtEndTime.setText(datum.getFormattedEndTime());
        addViewMoreLogic(datum.getDescription(), holder.description, holder.textTemp, holder.txt_less_or_more);

        holder.edit.setOnClickListener(view -> {
            agendaEdit.agendaEditclicked(datum, true);
        });
        holder.delete.setOnClickListener(view -> {
            agendaEdit.agendaEditclicked(datum, false);
        });
        if (isAdmin) {
            holder.edit.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.edit.setVisibility(View.INVISIBLE);
            holder.delete.setVisibility(View.INVISIBLE);
        }
        if (datum.getAgendaPic() != null && datum.getAgendaPic().trim().length() > 0) {
            Glide.with(holder.itemView.getContext())
                    .load(S3_DEV_IMAGE_URL_SQUARE + IMAGE_BASE_URL + Constants.Paths.AGENDA_PIC + datum.getAgendaPic())
                    .placeholder(R.drawable.default_event_image)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ImageChangerActivity.class);
                intent.putExtra(TYPE, Constants.ImageUpdate.GENERAL);
                intent.putExtra(DATA, IMAGE_BASE_URL + Constants.Paths.AGENDA_PIC + datum.getAgendaPic());
                intent.putExtra(IDENTIFIER, false);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
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
                txt_less_or_more.setVisibility(View.INVISIBLE);
                albumDescription.setText(description);
            }
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
        albumDescription.setOnClickListener(v -> {
        });
        textTemp.setOnClickListener(v -> {
        });
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtStartTime)
        TextView txtStartTime;
        @BindView(R.id.txtEndTime)
        TextView txtEndTime;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.edit)
        ImageView edit;
        @BindView(R.id.txtCardTitle)
        TextView txtCardTitle;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.txt_less_or_more)
        TextView txt_less_or_more;
        @BindView(R.id.txt_temp)
        TextView textTemp;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}