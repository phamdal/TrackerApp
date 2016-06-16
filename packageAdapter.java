package com.example.dalena.trackerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Dalena on 6/4/2016.
 */
public class packageAdapter extends BaseAdapter {
    private Context mContext;
    private packageTrack[] packages;

    public packageAdapter(Context context, packageTrack[] packageArray) {
        mContext = context;
        packages = packageArray;
    }

    @Override
    public int getCount() {
        return packages.length;
    }

    @Override
    public Object getItem(int position) {
        return packages[position].getTracking_num();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            // brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.package_item, null);
            holder = new ViewHolder();
            holder.packageName = (TextView) convertView.findViewById(R.id.packageName);
            holder.carrier = (TextView) convertView.findViewById(R.id.carrierName);
            holder.status = (TextView) convertView.findViewById(R.id.statusName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Recycle view holder
        packageTrack currentPackage = packages[position];
        String nickName = currentPackage.getNickName();
        if(nickName.equals("-")) {
            holder.packageName.setText(currentPackage.getTracking_num());
        } else {
            holder.packageName.setText(currentPackage.getNickName());
        }
        holder.carrier.setText(currentPackage.getCarrier());
        holder.status.setText(currentPackage.getStatus());

        return convertView;
    }

    private static class ViewHolder {
        TextView packageName;
        TextView carrier;
        TextView status;
    }
}
