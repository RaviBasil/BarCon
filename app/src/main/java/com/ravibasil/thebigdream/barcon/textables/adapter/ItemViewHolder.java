package com.ravibasil.thebigdream.barcon.textables.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ravibasil.thebigdream.barcon.R;


/**
 * Created by ravibasil on 2/3/18.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder{
    public TextView tv_name, tv_art;
   public final View rootView;
   public ImageView imageViewStar;
    public ItemViewHolder(View view) {
        super(view);
        rootView = view;
        imageViewStar = (ImageView) view.findViewById(R.id.imageViewStar);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_art = (TextView) view.findViewById(R.id.tv_art);
    }
}