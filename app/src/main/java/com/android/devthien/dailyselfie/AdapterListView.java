package com.android.devthien.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AdapterListView extends BaseAdapter {

    private List<ImageModel> imageModelList;
    private Context context;
    private LayoutInflater layoutInflater;

    public AdapterListView ( List<ImageModel> images, Context context){
        this.imageModelList = images;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return imageModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return imageModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = layoutInflater.inflate(R.layout.list_view_item, viewGroup, false);
        }
        ImageView photo = view.findViewById(R.id.image_item);
        TextView name = view.findViewById(R.id.image_name);
        TextView des = view.findViewById(R.id.image_des);

        photo.setImageBitmap(imageModelList.get(i).getPhoto());
        name.setText(imageModelList.get(i).getName());
        des.setText(imageModelList.get(i).getDes());

        return view;
    }
}
