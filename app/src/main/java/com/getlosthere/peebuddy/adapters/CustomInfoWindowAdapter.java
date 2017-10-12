package com.getlosthere.peebuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.getlosthere.peebuddy.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by violetaria on 8/6/17.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;

    public CustomInfoWindowAdapter(Context context) {
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    private void render(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvName = ((TextView) view.findViewById(R.id.tvName));
        if (title != null) {
            tvName.setText(title);
        } else {
            tvName.setText("");
        }

        String snippet = marker.getSnippet();
        TextView tvRating = ((TextView) view.findViewById(R.id.tvRating));
        if (snippet != null) {
            tvRating.setText(snippet);
        } else {
            tvRating.setText("");
        }
    }}
