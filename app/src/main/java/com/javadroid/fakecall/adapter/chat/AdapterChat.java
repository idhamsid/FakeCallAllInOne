package com.javadroid.fakecall.adapter.chat;

import static maes.tech.intentanim.CustomIntent.customType;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javadroid.fakecall.R;
import com.javadroid.fakecall.activity.Main;
import com.javadroid.fakecall.activity.Splash;
import com.javadroid.fakecall.model.chat.ModelChat;

import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder> {

    public static final int VIEW_TYPE_KANAN = 1;
    public static final int VIEW_TYPE_KIRI = 2;

    List<Integer> listViewType;
    List<ModelChat> listDataSms;

    public AdapterChat(List<Integer> listViewType, List<ModelChat> listDataSms) {
        this.listViewType = listViewType;
        this.listDataSms = listDataSms;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_KANAN) {
             view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sisi_kanan, null);
            return new ItemSisiKananViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sisi_kiri, null);
            return new ItemSisiKiriViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewType = listViewType.get(position);
        ModelChat dataSms = listDataSms.get(position);
        if (viewType == VIEW_TYPE_KANAN) {
            ItemSisiKananViewHolder itemSisiKananViewHolder = (ItemSisiKananViewHolder) holder;
            itemSisiKananViewHolder.textViewPesanItemSisiKanan.setText(dataSms.getPesan());
            itemSisiKananViewHolder.textViewWaktuPesanItemSisiKanan.setText(dataSms.getWaktu());
        } else {
            ItemSisiKiriViewHolder itemSisiKiriViewHolder = (ItemSisiKiriViewHolder) holder;
            new CountDownTimer(2000, 1000) {
                @Override
                public void onFinish() {
                    itemSisiKiriViewHolder.textViewPesanItemSisiKiri.setText(dataSms.getPesan());
                    itemSisiKiriViewHolder.textViewWaktuPesanItemSisiKiri.setText(dataSms.getWaktu());
                }

                @Override
                public void onTick(long millisUntilFinished) {
                }
            }.start();
        }
    }

    @Override
    public int getItemCount() {
        return listViewType.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listViewType.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ItemSisiKananViewHolder extends ViewHolder {

        private final TextView textViewPesanItemSisiKanan;
        private final TextView textViewWaktuPesanItemSisiKanan;

        public ItemSisiKananViewHolder(View itemView) {
            super(itemView);
            textViewPesanItemSisiKanan = (TextView) itemView.findViewById(R.id.text_view_pesan_item_sisi_kanan);
            textViewWaktuPesanItemSisiKanan = (TextView) itemView.findViewById(R.id.text_view_waktu_pesan_item_sisi_kanan);
        }
    }

    public static class ItemSisiKiriViewHolder extends ViewHolder {

        private final TextView textViewPesanItemSisiKiri;
        private final TextView textViewWaktuPesanItemSisiKiri;

        public ItemSisiKiriViewHolder(View itemView) {
            super(itemView);
            textViewPesanItemSisiKiri = (TextView) itemView.findViewById(R.id.text_view_pesan_item_sisi_kiri);
            textViewWaktuPesanItemSisiKiri = (TextView) itemView.findViewById(R.id.text_view_waktu_pesan_item_sisi_kiri);
        }
    }
}
