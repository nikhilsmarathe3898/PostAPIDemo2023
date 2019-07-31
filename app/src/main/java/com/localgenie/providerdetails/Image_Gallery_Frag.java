package com.localgenie.providerdetails;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.localgenie.R;

/**
 * Created by embed on 8/11/16.
 *
 */
public class Image_Gallery_Frag extends Fragment {


    private String imageResource;


    public static Image_Gallery_Frag getInstance(String resourceID) {
        Image_Gallery_Frag f = new Image_Gallery_Frag();
        Bundle args = new Bundle();
        args.putString("image_source", resourceID);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageResource = getArguments().getString("image_source");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_galllery_farg, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_single_large);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        o.inDither = false;
        Glide.with(getActivity())
                .load(imageResource)
                .into(imageView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

