package com.shifu.user.shifu_5_newavito.fake;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.shifu.user.shifu_5_newavito.R;

import java.util.Collections;
import java.util.List;

/**
 * Adapter used to show a simple grid of products.
 */
public class FakeRVAdapter extends RecyclerView.Adapter<FakeRVAdapter.ItemViewHolder> {

    private List<FakeItemEntry> productList;
    private FakeImageRequester imageRequester;

     class ItemViewHolder extends RecyclerView.ViewHolder {

         NetworkImageView productImage;
         TextView productTitle;
         TextView productPrice;
         TextView productLocation;
         ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            productLocation = itemView.findViewById(R.id.product_location);
        }
    }

    public FakeRVAdapter(Context context, List<FakeItemEntry> productList) {
        this.productList = productList;
        Collections.sort(this.productList, new FakeListComparator());
        imageRequester = FakeImageRequester.getInstance(context);
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        return new ItemViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (productList != null && position < productList.size()) {
            FakeItemEntry product = productList.get(position);
            holder.productTitle.setText(product.title);
            holder.productPrice.setText(product.cost);
            holder.productLocation.setText(product.location);
            imageRequester.setImageFromUrl(holder.productImage, product.url);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
