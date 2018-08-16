package com.shifu.user.shifu_5_newavito;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.shifu.user.shifu_5_newavito.fake.FakeImageRequester;
import com.shifu.user.shifu_5_newavito.model.Product;

import org.jetbrains.annotations.NotNull;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

//import static com.shifu.user.truechat.ListFragment.dateFormat;

class RealmRVAdapter extends RealmRecyclerViewAdapter<Product, RealmRVAdapter.ViewHolder> {

    private final String TAG = "RA";
    private FakeImageRequester imageRequester;

    private static RealmRVAdapter instance;
    public static RealmRVAdapter getInstance(){
        return instance;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        NetworkImageView productImage;
        ImageButton productLike;
        TextView productTitle, productPrice, productLocation;

        // TODO Добавить поле даты "сегодня/вчера/на этой неделе/на прошлой неделе/в этом месяце/в прошлом месяце/больше месяца назад"

        public Product data;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            productLocation = itemView.findViewById(R.id.product_location);
            productImage = itemView.findViewById(R.id.product_image);
            productLike = itemView.findViewById(R.id.like_view);
        }

        void setItemContent(Product item){
            data = item;

            String text = item.getTitle();
            productTitle.setText((text==null)?"":text);

            text = item.getCost();
            productPrice.setText((text==null)?"":text);

            text = item.getLocation();
            productLocation.setText((text==null)?"":text);

            text = item.getUrl();
            if (text != null) imageRequester.setImageFromUrl(productImage, text);

            Long l = item.getLikes();
            if (l > 0) {
                productImage.setImageResource(R.drawable.icons8_like_26);
            } else {
                productImage.setImageResource(R.drawable.icons8_like_32);
            }

            // TODO добавить поле даты
        }
    }

    RealmRVAdapter(Context context, OrderedRealmCollection<Product> data) {
        //for (Product item: items) Log.d("RA Init: ",item.toString());
        super(data, true);
        Log.d(TAG, "setDataSize: "+data.size()+" from baseSize: "+RealmController.getInstance().getSize(Product.class));
        setHasStableIds(true);
        imageRequester = FakeImageRequester.getInstance(context);
        instance = this;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_card, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, int position) {
        viewHolder.setItemContent(getItem(position));
    }

    @Override
    public long getItemId(int index) {
        //Log.d("RA.getItemId", getItem(index).toString());
        // TODO выяснить, как корректно обработать здесь NullPointerException применительно к Realm recycle view
        return getItem(index).getUpid();
    }

    public void setData(OrderedRealmCollection<Product> data){
        Log.d(TAG, "setDataSize: "+data.size()+" from baseSize: "+RealmController.getInstance().getSize(Product.class));
        updateData(data);
        notifyDataSetChanged();
    }

}
