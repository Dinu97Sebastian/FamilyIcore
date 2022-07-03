package com.familheey.app.Post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.familheey.app.R;

import java.util.ArrayList;
import java.util.Arrays;

public class DraftActivity extends AppCompatActivity {
RecyclerView rv_drafts;
Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft);
        rv_drafts = findViewById(R.id.rv_drafted_items);
        toolbar = findViewById(R.id.toolbar_draft);
        initToolbar();
        initRecycler();
    }

    private void initToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_drafts.setLayoutManager(linearLayoutManager);
        DraftAdapter customAdapter = new DraftAdapter();
        rv_drafts.setAdapter(customAdapter);
    }
}