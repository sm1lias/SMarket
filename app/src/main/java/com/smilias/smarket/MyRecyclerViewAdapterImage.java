package com.smilias.smarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewAdapterImage extends RecyclerView.Adapter<MyRecyclerViewAdapterImage.ViewHolder> {

    private List<String> mData;
    private List<String> mPrice;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapterImage(Context context, List<String> data, List<String> prices) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mPrice = prices;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row_with_image, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = mData.get(position);
        String price= mPrice.get(position);
        holder.myTextView2.setText("SUPERMARKET: "+item);
        holder.myTextView1.setText("PRICE: "+price);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView1, myTextView2;
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView1 = itemView.findViewById(R.id.price);
            myTextView2 = itemView.findViewById(R.id.category);
            button = itemView.findViewById(R.id.button3);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
