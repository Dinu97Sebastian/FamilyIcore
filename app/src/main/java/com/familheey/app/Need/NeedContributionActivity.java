package com.familheey.app.Need;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Decorators.BottomAdditionalMarginDecorator;
import com.familheey.app.R;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NeedContributionActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.labelNeedDescription)
    TextView labelNeedDescription;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.labelNeedsList)
    TextView labelNeedsList;
    @BindView(R.id.needsList)
    RecyclerView needsList;
    @BindView(R.id.addContribution)
    CardView addContribution;
    @BindView(R.id.publishNeeds)
    MaterialButton publishNeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_contribution);
        ButterKnife.bind(this);
        initializeToolbar();
        NeedContributionAdapter needContributionAdapter = new NeedContributionAdapter(new NeedRequest().getNeeds());
        needsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        needsList.setAdapter(needContributionAdapter);
        ViewCompat.setNestedScrollingEnabled(needsList, false);
        needsList.addItemDecoration(new BottomAdditionalMarginDecorator());
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Post A Need");
        goBack.setOnClickListener(v -> finish());
    }
}
