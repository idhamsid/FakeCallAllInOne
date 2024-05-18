package com.javadroid.fakecall.adapter.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.model.chat.PertanyaanModel;

import java.util.List;

public class PertanyaanAdapter extends RecyclerView.Adapter<PertanyaanAdapter.ViewHolder> {

    private List<PertanyaanModel> daftarPertanyaan;

    private static OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public PertanyaanAdapter(List<PertanyaanModel> daftarPertanyaan) {
        this.daftarPertanyaan = daftarPertanyaan;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_pertanyaan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PertanyaanModel pertanyaan = daftarPertanyaan.get(position);
        holder.textPertanyaan.setText(pertanyaan.getPertanyaan());
        holder.bt.setOnClickListener(v -> {
            if (mListener != null) mListener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return daftarPertanyaan.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textPertanyaan;
        public MaterialCardView bt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPertanyaan = itemView.findViewById(R.id.tv_title);
            bt = itemView.findViewById(R.id.ly);
        }
    }
}
