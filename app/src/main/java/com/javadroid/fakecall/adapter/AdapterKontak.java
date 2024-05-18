package com.javadroid.fakecall.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.model.ModelKontak;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AdapterKontak extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ModelKontak> itemsList;
    private final Context context;
    private final LayoutInflater mLayoutInflater;
    private static final int DEFAULT_VIEW_TYPE = 1;

    public AdapterKontak(List<ModelKontak> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = mLayoutInflater.inflate(R.layout.list_kontak, parent, false);
        return new ImageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (!(holder instanceof ImageViewHolder)) {
            return;
        }
        ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
        if (!(itemsList.get(position) ==null)) {
            imageViewHolder.nama.setText(itemsList.get(position).getNama());
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

        });

    }

    @Override
    public int getItemViewType(int position) {

        return DEFAULT_VIEW_TYPE;
    }


    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nama;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.list_image);
            nama = itemView.findViewById(R.id.name);
        }

    }


}
