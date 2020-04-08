package com.example.taso.client;
//Tsoukas Anastasios AM:3140213
//Koulouridis Mixail AM:3120082
//Saitis Georgios AM:3120161
//Stavrianoudakis Vasilios AM:3140193

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class adapter extends RecyclerView.Adapter<adapter.PoiViewHolder>{
    List<Poi> data;
    ArrayList<Bitmap> images;

    adapter(List<Poi> data,ArrayList<Bitmap> images){
        this.data = data;
        this.images=images;
    }

    public static class PoiViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView PoiName;
        TextView PoiCategory;
        TextView PoiId;
        ImageView PoiPhoto;
        PoiViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            PoiId =(TextView)itemView.findViewById(R.id.poi_Id);
            PoiName = (TextView)itemView.findViewById(R.id.poi_name);
            PoiCategory = (TextView)itemView.findViewById(R.id.poi_category);
            PoiPhoto = (ImageView)itemView.findViewById(R.id.poi_photo);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public PoiViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        PoiViewHolder pvh = new PoiViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PoiViewHolder poiViewHolder, int i) {
        poiViewHolder.PoiName.setText(data.get(i).getName());
        poiViewHolder.PoiCategory.setText("Category: "+data.get(i).getCategory());
        if(images.get(i)==null){
            poiViewHolder.PoiPhoto.setImageResource(R.drawable.no_found);
        }else{
            poiViewHolder.PoiPhoto.setImageBitmap(images.get(i));
        }
        poiViewHolder.PoiId.setText("Id: "+data.get(i).getId()+"");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}