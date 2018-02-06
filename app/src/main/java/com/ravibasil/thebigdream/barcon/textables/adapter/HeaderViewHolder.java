package com.ravibasil.thebigdream.barcon.textables.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ravibasil.thebigdream.barcon.R;

/**
 * Created by ravibasil on 2/3/18.
 */

public class HeaderViewHolder extends RecyclerView.ViewHolder{
    public TextView headerTitle;
    public HeaderViewHolder(View itemView) {
        super(itemView);
        headerTitle = (TextView)itemView.findViewById(R.id.header_id);
    }
}
