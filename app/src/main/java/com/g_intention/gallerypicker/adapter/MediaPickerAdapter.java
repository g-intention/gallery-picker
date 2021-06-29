package com.g_intention.gallerypicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.g_intention.gallerypicker.PickerModel.Model;
import com.g_intention.gallerypicker.R;

import java.util.ArrayList;

public class MediaPickerAdapter extends ArrayAdapter<Model> {
    public MediaPickerAdapter(@NonNull Context context, ArrayList<Model> modelList) {
        super(context, 0, modelList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }
        Model Model = getItem(position);
        TextView labelTV = (TextView) convertView.findViewById(R.id.idlabelTV);
        ImageView thumbnailIV = (ImageView) convertView.findViewById(R.id.idIthumbnailIV);
        thumbnailIV.setImageResource(Model.getThumbnail_id());
        labelTV.setText(Model.getLabel_name());
        return convertView;
    }
}
