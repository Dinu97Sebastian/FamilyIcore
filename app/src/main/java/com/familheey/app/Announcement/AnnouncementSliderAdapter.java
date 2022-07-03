package com.familheey.app.Announcement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnnouncementSliderAdapter extends SliderViewAdapter<AnnouncementSliderAdapter.SliderAdapterVH> {

    private Context context;
    private ArrayList<HistoryImages> documents = new ArrayList<>();

    public AnnouncementSliderAdapter(Context context) {
        this.context = context;
        documents.add(null);
        documents.add(null);
        documents.add(null);
        documents.add(null);
        documents.add(null);

    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement_detailed_view, null);
        return new AnnouncementSliderAdapter.SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

    }

    @Override
    public int getCount() {
        return documents.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        @BindView(R.id.announcementThumb)
        ImageView announcementThumb;
        @BindView(R.id.familyName)
        TextView familyName;
        @BindView(R.id.postedBy)
        TextView postedBy;
        @BindView(R.id.postedDateTime)
        TextView postedDateTime;
        @BindView(R.id.mediaSlider)
        ImageView mediaSlider;
        @BindView(R.id.description)
        com.luseen.autolinklibrary.AutoLinkTextView description;
        @BindView(R.id.conversationIcon)
        ImageView conversationIcon;
        @BindView(R.id.conversationCountIndicator)
        TextView conversationCountIndicator;
        @BindView(R.id.newConversationIndicator)
        ImageView newConversationIndicator;
        @BindView(R.id.documentsIndicator)
        ImageView documentsIndicator;
        @BindView(R.id.attachmentCount)
        TextView attachmentCount;
        @BindView(R.id.moreOptions)
        ImageView moreOptions;

        SliderAdapterVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
