package com.familheey.app.Fragments.Events;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familheey.app.Adapters.SliderImageAdapter;
import com.familheey.app.Interfaces.Bottomupinterface;
import com.familheey.app.Interfaces.ProgressListener;
import com.familheey.app.Interfaces.RetrofitListener;
import com.familheey.app.Models.ApiCallbackParams;
import com.familheey.app.Models.ErrorData;
import com.familheey.app.Models.Response.Document;
import com.familheey.app.Models.Response.ListEventAlbumsResponse;
import com.familheey.app.Networking.Retrofit.ApiServiceProvider;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.familheey.app.Utilities.Utilities;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageFullScreenFragment extends Fragment {
    @BindView(R.id.imageSlider)
    SliderView sliderView;
    @BindView(R.id.more)
    ImageView more;
    ArrayList<Document> documents = new ArrayList<>();
    Bottomupinterface mListener;
    ProgressListener progressListener;
    BottomSheetDialog dialog;
    RetrofitListener retrofitListener = new RetrofitListener() {
        @Override
        public void onResponseSuccess(String responseBodyString, ApiCallbackParams apiCallbackParams, int apiFlag) {

            if (apiFlag == Constants.ApiFlags.MAKE_PIC_COVER) {
                JsonParser jsonParser = new JsonParser();
                JsonObject responseJson = (JsonObject) jsonParser.parse(responseBodyString);
                JsonObject responseJson1 = responseJson.getAsJsonObject("data");
                ListEventAlbumsResponse.Datum datum = new Gson().fromJson(responseJson1.toString(), ListEventAlbumsResponse.Datum.class);
                mListener.updateCoverPic(datum);
                dialog.dismiss();
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                progressListener.hideProgressDialog();
                getActivity().getSupportFragmentManager().popBackStack();

            }
            if (apiFlag == Constants.ApiFlags.DELETE_FILE) {
                progressListener.hideProgressDialog();
                Toast.makeText(getActivity(), "Deleted Success", Toast.LENGTH_SHORT).show();
                mListener.deletedFile();
                dialog.dismiss();
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

                getActivity().getSupportFragmentManager().popBackStack();

            }

        }

        @Override
        public void onResponseError(ErrorData errorData, ApiCallbackParams apiCallbackParams, Throwable throwable, int apiFlag) {
            progressListener.showErrorDialog(errorData.getMessage());
        }
    };

    public static Fragment newInstance(List<Document> document) {

        ImageFullScreenFragment fragment = new ImageFullScreenFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Bundle.DATA, new Gson().toJson(document));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Bottomupinterface)
            mListener = (Bottomupinterface) context;

        progressListener = Utilities.getListener(this, ProgressListener.class);
        if (progressListener == null) {
            throw new RuntimeException("Prograss listener not implemented");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_image_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            documents = (ArrayList<Document>) fromJson(getArguments().getString(Constants.Bundle.DATA),
                    new TypeToken<ArrayList<Document>>() {
                    }.getType());

        }
    }

    public static Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        SliderImageAdapter adapter = new SliderImageAdapter(getActivity(), documents);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setIndicatorSelectedColor(Color.GREEN);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        /*if (document.getUrl() != null) {
            Glide.with(this)
                    .load(document.getUrl())
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.family_dashboard_background)
                    .into(image);
        }
        */

        more.setOnClickListener(view1 -> {
            createBottomDialog(documents.get(sliderView.getCurrentPagePosition()));
        });

    }

    private void createBottomDialog(Document document) {
        View dialogView = getLayoutInflater().inflate(R.layout.image_full_screen_bottom_dialog, null);

        dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTransparent);

        dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        dialog.setContentView(dialogView);
        dialog.show();


        TextView delete = dialogView.findViewById(R.id.delete);
        TextView share = dialogView.findViewById(R.id.share);
        TextView album_cover = dialogView.findViewById(R.id.album_cover);
        Button cancel = dialogView.findViewById(R.id.cancel);

        delete.setOnClickListener(view -> {

            progressListener.showProgressDialog();
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("id", document.getId() + "");


            apiServiceProvider.deleteFile(jsonObject, null, retrofitListener);

        });
        share.setOnClickListener(view -> {

            Toast.makeText(getActivity(), "Will be available soon", Toast.LENGTH_SHORT).show();
        });
        album_cover.setOnClickListener(view -> {

//            make_pic_cover
            progressListener.showProgressDialog();
            ApiServiceProvider apiServiceProvider = ApiServiceProvider.getInstance(getActivity());
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("album_id", document.getFolderId().toString());


            jsonObject.addProperty("cover_pic", document.getUrl());
            apiServiceProvider.makePicCover(jsonObject, null, retrofitListener);
        });
        cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });


    }


}
