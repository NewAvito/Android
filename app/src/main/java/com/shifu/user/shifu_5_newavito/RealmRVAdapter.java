package com.shifu.user.shifu_5_newavito;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.shifu.user.shifu_5_newavito.json.JRequestPushLike;
import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.model.Product;

import org.jetbrains.annotations.NotNull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

import static com.shifu.user.shifu_5_newavito.AppGlobals.LOGIN;

class RealmRVAdapter extends RealmRecyclerViewAdapter<Product, RealmRVAdapter.ViewHolder> {

    private final String TAG = "RA";
    private ImageRequester imageRequester;


    private static RealmRVAdapter instance;
    public static RealmRVAdapter getInstance(){
        return instance;
    }


    private RealmController rc = RealmController.getInstance();
    private ApiInterface api = ApiClient.getInstance().getApi();
    private ActivityMain activity;

    class ViewHolder extends RecyclerView.ViewHolder{


        NetworkImageView productImage;
        ImageButton productLike;
        Long l;
        TextView productTitle, productPrice, productLocation, productText;

        // TODO Добавить поле даты "сегодня/вчера/на этой неделе/на прошлой неделе/в этом месяце/в прошлом месяце/больше месяца назад"

        public Product data;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            productLocation = itemView.findViewById(R.id.product_location);
            productImage = itemView.findViewById(R.id.product_image);
//            productLike = itemView.findViewById(R.id.like_view);
            productText = itemView.findViewById(R.id.photo_text);

//            productLike.setOnClickListener(view -> {
//                l = (l == 0) ? l + 1 : l - 1;
//                Disposable d = api.pushLike(new JRequestPushLike(data.getUpid(), rc.getUsername()))
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(response -> Log.d("Like", "Response: "+response.body()));
//                rc.updateLike(data.getUpid(), l);
//                notifyDataSetChanged();
//            });

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(activity, ActivityProduct.class);
                intent.putExtra("requestCode", 1);
                intent.putExtra("upid", data.getUpid());
                activity.startActivity(intent);
            });
        }

        void setItemContent(Product item){
            data = item;

            String text = item.getTitle();
            productTitle.setText((text==null)?"":text);

            text = Long.toString(item.getCost());
            productPrice.setText((text==null)?"":text+" руб.");

            text = item.getLocation();
            productLocation.setText((text==null)?"":text);

            text = item.getUrl();
             if (text != null) {
                 if (imageRequester.setImageFromUrl(productImage, text)) {
                     productText.setVisibility(View.GONE);
                 } else {
                     productText.setText("Невозможно загрузить, нет соединения с сервером");
                 }
             }

//            l = item.getLikes();
//            if (l == null || rc.getSize(Author.class) == 0) {
//                productLike.setVisibility(View.GONE);
//            }
//            else if (l == null || l == 0) {
//                productLike.setImageResource(R.drawable.icons8_like_32);
//            } else
//                productLike.setImageResource(R.drawable.icons8_like_26);
            }

    }

    RealmRVAdapter(Context context, OrderedRealmCollection<Product> data, ActivityMain activity) {
        //for (Product item: items) Log.d("RA Init: ",item.toString());
        super(data, true);
        Log.d(TAG, "setDataSize: "+data.size()+" from baseSize: "+RealmController.getInstance().getSize(Product.class));
        setHasStableIds(true);
        imageRequester = ImageRequester.getInstance(context);
        instance = this;

        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_card, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, int position) {
        if (getItem(position) != null) {
            Log.d("Bind", "position: " + position + " id_article: " + getItem(position).getUpid() + " likes: " + getItem(position).getLikes());
        }
        viewHolder.setItemContent(getItem(position));
    }

    @Override
    public long getItemId(int index) {
        return getItem(index).getUpid();
    }

    public void setData(OrderedRealmCollection<Product> data){
        Log.d(TAG, "setDataSize: "+data.size()+" from baseSize: "+RealmController.getInstance().getSize(Product.class));
        updateData(data);
        notifyDataSetChanged();
    }

}
