package com.familheey.app.Need;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;

public class ViewFamiliesActivity extends AppCompatActivity {

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.familiesList)
    RecyclerView familiesList;
    private List<Group> groups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_families);
        ButterKnife.bind(this);
        groups = getIntent().getParcelableArrayListExtra(DATA);
        initializeToolbar();
        initializeAdapter();
    }

    private void initializeAdapter() {
        ViewFamilyAdapter viewFamilyAdapter = new ViewFamilyAdapter(groups);
        familiesList.setAdapter(viewFamilyAdapter);
        familiesList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Families");
    }

    @OnClick(R.id.goBack)
    public void onClick() {
        finish();
    }
}
