package com.familheey.app.Dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.familheey.app.Adapters.RelationShipAdapter;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.RelationShip;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static com.familheey.app.Utilities.Constants.Bundle.IDENTIFIER;
import static com.familheey.app.Utilities.Constants.Bundle.MEMBER;
import static com.familheey.app.Utilities.Constants.Bundle.RELATIONSHIP;

public class RelationShipSelectionActivity extends AppCompatActivity implements RelationShipAdapter.RelationShipSelectionListener {

    public static final int REQUEST_CODE = 0;

    @BindView(R.id.searchRelationShip)
    EditText searchRelationShip;
    @BindView(R.id.relationShipList)
    RecyclerView relationShipList;
    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;

    private List<RelationShip> relationShips;
    private FamilyMember familyMember;
    private int TYPE = 0;
    private RelationShipAdapter relationShipAdapter;
    private Boolean isRegularFamily = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation_ship_selection);
        ButterKnife.bind(this);
        relationShips = getIntent().getParcelableArrayListExtra(RELATIONSHIP);
        familyMember = getIntent().getParcelableExtra(MEMBER);
        TYPE = getIntent().getIntExtra(Constants.Bundle.TYPE, 0);
        isRegularFamily = getIntent().getBooleanExtra(IDENTIFIER, true);
        initializeToolbar();
        initAdapter();
        initListener();
    }

    private void initListener() {
        searchRelationShip.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
               /* RelationShip relationShip = new RelationShip();
                relationShip.setRelationship(searchRelationShip.getText().toString());
                relationShip.setId(0);
                relationShips.add(relationShip);*/
                relationShipAdapter.getFilter().filter(searchRelationShip.getText().toString());
            }
            return false;
        });
    }

    private void initializeToolbar() {
        if (isRegularFamily) {
            toolBarTitle.setText("Select Relationship");
            searchRelationShip.setHint("Search Relationship");
        } else {
            toolBarTitle.setText("Select Role");
            searchRelationShip.setHint("Search Role");
        }
        goBack.setOnClickListener(v -> finish());
    }

    private void initAdapter() {
        relationShipAdapter = new RelationShipAdapter(this, relationShips, familyMember);
        relationShipList.setAdapter(relationShipAdapter);
        relationShipList.setLayoutManager(new LinearLayoutManager(this));
        relationShipList.addItemDecoration(new DividerItemDecoration(relationShipList.getContext(), DividerItemDecoration.VERTICAL));
    }

    @OnTextChanged(R.id.searchRelationShip)
    protected void onTextChanged(CharSequence text) {
        relationShipAdapter.getFilter().filter(text.toString());
    }

    @Override
    public void OnRelationShipSelected(RelationShip relationShipSelected) {
        Intent intent = new Intent();
        Bundle args = new Bundle();
        args.putParcelable(RELATIONSHIP, relationShipSelected);
        args.putParcelable(MEMBER, familyMember);
        args.putInt(Constants.Bundle.TYPE, TYPE);
        intent.putExtras(args);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
