package com.familheey.app.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.familheey.app.Adapters.ImageSliderAdapter;
import com.familheey.app.R;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.Bundle.DATA;
import static com.familheey.app.Utilities.Constants.Bundle.POSITION;

public class ImageSliderActivity extends AppCompatActivity {

    @BindView(R.id.goBack)
    ImageView goBack;
    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.imageSlider)
    SliderView imageSlider;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        ButterKnife.bind(this);
        imageUrls = getIntent().getStringArrayListExtra(DATA);
        position = getIntent().getIntExtra(POSITION, 0);
        initializeToolbar();
        initAdapter();
    }

    private void initializeToolbar() {
        toolBarTitle.setText("Photos");
        goBack.setOnClickListener(v -> finish());
    }

    private void initAdapter() {
        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(getApplicationContext(), imageUrls);
        imageSlider.setSliderAdapter(imageSliderAdapter);
        imageSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSlider.setIndicatorSelectedColor(Color.GREEN);
        imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        imageSlider.setCurrentPagePosition(position);
    }
}
