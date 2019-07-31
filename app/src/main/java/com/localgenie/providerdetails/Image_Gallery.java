package com.localgenie.providerdetails;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;

import java.util.ArrayList;

/**
 * Created by embed on 8/11/16.
 *
 */
public class
Image_Gallery extends AppCompatActivity
{
    ArrayList<String> arrayList;
    private ViewPager viewPager;
    private LinearLayout thumbnailsContainer;
    View btnNext,btnPrev;
    Toolbar toolbar;

    TextView tv_center;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagegallery);
        if(getIntent().getExtras()!=null)
            arrayList = getIntent().getStringArrayListExtra("Imagelist");
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        toolbar =  findViewById(R.id.toolbar);
        tv_center =  findViewById(R.id.tv_center);
        thumbnailsContainer = (LinearLayout) findViewById(R.id.container);
         btnNext = findViewById(R.id.next);
         btnPrev = findViewById(R.id.prev);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_center.setText(getResources().getString(R.string.jobimage));
        tv_center.setTypeface(AppTypeface.getInstance(this).getHind_semiBold());
        btnPrev.setOnClickListener(onClickListener(0));
        btnNext.setOnClickListener(onClickListener(1));

        FragmentStatePagerAdapter adapter = new ViewPagerImageAdapter(getSupportFragmentManager(), arrayList);
        viewPager.setAdapter(adapter);

        inflateThumbnails();
    }

    private View.OnClickListener onClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i > 0) {
                    //next page
                    if (viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                } else {
                    //previous page
                    if (viewPager.getCurrentItem() > 0) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                    }
                }
            }
        };
    }


    private void inflateThumbnails() {
        for (int i = 0; i < arrayList.size(); i++) {
            View imageLayout = getLayoutInflater().inflate(R.layout.imagethumbnail, thumbnailsContainer,false);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.img_thumb);
            imageView.setOnClickListener(onChagePageClickListener(i));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            options.inDither = false;

            Glide.with(Image_Gallery.this)
                    .load(arrayList.get(i))
                    .into(imageView);
            thumbnailsContainer.addView(imageLayout);
        }
    }

    private View.OnClickListener onChagePageClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(i);
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class ViewPagerImageAdapter extends FragmentStatePagerAdapter {
        private ArrayList<String> images;

        public ViewPagerImageAdapter(FragmentManager fm, ArrayList<String> images) {
            super(fm);
            this.images = images;
        }

        @Override
        public Fragment getItem(int position) {
            return Image_Gallery_Frag.getInstance(images.get(position));
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }
}
