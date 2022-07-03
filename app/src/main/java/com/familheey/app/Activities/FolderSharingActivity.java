package com.familheey.app.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.R;

import butterknife.BindView;

public class FolderSharingActivity extends AppCompatActivity {

    @BindView(R.id.clearSearch)
    ImageView clearSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_sharing);
        initializeSearchClearCallback();
    }

    private void initializeSearchClearCallback() {
        /*searchFamily.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    clearSearch.setVisibility(View.INVISIBLE);
                else clearSearch.setVisibility(View.VISIBLE);
            }
        });
        clearSearch.setOnClickListener(v -> {
            searchFamily.setText("");
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH);
        });*/
    }
}
