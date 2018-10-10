package com.example.faari.perfectplaylsit;

import android.app.Activity;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecentSongsAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] title;
    private final String[] artist;
    private final String[] album;
    private final Integer[] imgid;

    public RecentSongsAdapter(Activity context, String[] title, String[] artist, String[] album, Integer[] imgid) {
        super(context, R.layout.list_view_recent, title);

        this.context=context;
        this.title =title;
        this.artist =artist;
        this.album =album;
        this.imgid=imgid;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_view_recent, null);
            holder = new ViewHolder();
            holder.titleText = (TextView) convertView.findViewById(R.id.title);
            holder.artistText = (TextView) convertView.findViewById(R.id.artist);
            holder.albumText = (TextView) convertView.findViewById(R.id.album);
            holder.albumImage = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    };

    static class ViewHolder {
        private TextView titleText;
        private TextView artistText;
        private TextView albumText;
        private ImageView albumImage;
    }
}