package com.shifu.user.shifu_5_newavito;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.model.Product;

public class ActivityProduct extends AppCompatActivity {

    private static RealmController rc = RealmController.getInstance();
    private static Long l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageButton bClose = findViewById(R.id.button_close);
        bClose.setOnClickListener(view -> finish());

//        ImageView vLike;

        Intent intent = getIntent();
        Long upid = intent.getLongExtra("upid", -1L);

        if (rc != null && upid >=0) {
            Product item = rc.getItem(Product.class, upid);
            if (item != null) {
                setText(R.id.product_title, item.getTitle());
                setText(R.id.description, getResources().getString(R.string.description_field, item.getDescription()));
                setText(R.id.price, getResources().getString(R.string.price_field, Long.toString(item.getCost()) + " руб."));
                setText(R.id.location, getResources().getString(R.string.location_field, item.getLocation()));
                setText(R.id.category, getResources().getString(R.string.category, item.getCategory()));
                setText(R.id.mobile, getResources().getString(R.string.mobile_field, item.getMobile()));

                TextView vPhotoText = findViewById(R.id.photo_text);
                NetworkImageView vPhoto = findViewById(R.id.product_image);
                if (item.getUrl() != null) {
                    vPhotoText.setVisibility(View.GONE);
                    ImageRequester.getInstance(this).setImageFromUrl(vPhoto, item.getUrl());
                }

//                l = item.getLikes();
//                vLike = findViewById(R.id.like_view);
//                if (l == null || rc.getSize(Author.class) == 0) {
//                    vLike.setVisibility(View.GONE);
//                } else if (l == 0) {
//                    vLike.setImageResource(R.drawable.icons8_like_32);
//                } else {
//                    vLike.setImageResource(R.drawable.icons8_like_26);
//                }
//
//                vLike.setOnClickListener(view -> {
//                    if (l == 0) {
//                        l++;
//                        vLike.setImageResource(R.drawable.icons8_like_26);
//                    } else {
//                        l--;
//                        vLike.setImageResource(R.drawable.icons8_like_32);
//                    }
//                    rc.updateLike(upid, l);
//                });


            }
        }

    }

    private void setText(Integer resPath, String text) {
        TextView out = findViewById(resPath);
        out.setText((text==null)?"":text);
    }
}
