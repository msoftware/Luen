package com.itemstudio.luen.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itemstudio.luen.R;
import com.itemstudio.luen.model.AppItem;

import java.util.ArrayList;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.DataObjectHolder> {
    private ArrayList<AppItem> mDataset;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView label;
        TextView dateTime;
        ImageView icon;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            icon = (ImageView) itemView.findViewById(R.id.imageView);
        }



    }

    public AppsAdapter(ArrayList<AppItem> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_app, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.label.setText(mDataset.get(position).getAppName());
        holder.dateTime.setText(mDataset.get(position).getPackageName());
        holder.icon.setImageDrawable(mDataset.get(position).getIcon());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public String getPackage(int position) {
        return mDataset.get(position).getPackageName();
    }
}