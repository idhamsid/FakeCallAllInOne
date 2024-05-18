package com.javadroid.fakecall.adapter.wallpaper;

import static maes.tech.intentanim.CustomIntent.customType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.ads.applovin.Inter;
import com.javadroid.fakecall.model.ModelListWall;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AdapterList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ModelListWall> itemsList;
    private final Context context;
    private final LayoutInflater mLayoutInflater;
    private static final int DEFAULT_VIEW_TYPE = 1;

    public AdapterList(List<ModelListWall> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = mLayoutInflater.inflate(R.layout.list_item_wall, parent, false);
        return new ImageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (!(holder instanceof ImageViewHolder)) {
            return;
        }
        ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
        if (!(itemsList.get(position) ==null)) {
            InputStream inputstream= null;
            try {
                inputstream = context.getAssets().open(itemsList.get(position).getImage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Drawable drawable = Drawable.createFromStream(inputstream, null);
            imageViewHolder.imageView.setImageDrawable(drawable);
            /*Glide.with(context)
                    .load(itemsList.get(position).getImage())
                    .placeholder(R.drawable.background)
                    .error(R.drawable.icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewHolder.imageView);

             */

        }

        holder.itemView.setOnClickListener(v -> {
            go_detail(position);
        });

    }

    @Override
    public int getItemViewType(int position) {

        return DEFAULT_VIEW_TYPE;
    }


    public void go_detail(int position){
        Inter.SHOW((Activity) context);
        Intent intent = new Intent(context, com.javadroid.fakecall.wallpaper.View.class);
        intent.putExtra("img",itemsList.get(position).getImage());
        context.startActivity(intent);
        customType(context,"fadein-to-fadeout");
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.list_image);
        }

    }


}
