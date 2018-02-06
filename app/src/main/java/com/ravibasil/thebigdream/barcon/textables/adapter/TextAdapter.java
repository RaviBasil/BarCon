package com.ravibasil.thebigdream.barcon.textables.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ravibasil.thebigdream.barcon.R;
import com.ravibasil.thebigdream.barcon.textables.modal.Texty;

import java.util.List;

/**
 * Created by ravibasil on 2/1/18.
 */

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.MyViewHolder>{

    private Context context;
    private List<Texty> textyList;
    private List<Texty> textyListFiltered;



    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_name, tv_art;

        public MyViewHolder(View view){
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_art = (TextView) view.findViewById(R.id.tv_art);
        }
    }

    public TextAdapter(Context context,List<Texty> textyList){
        this.textyList = textyList;
        this.context =context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewTypes){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_texty_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Texty texty = textyList.get(position);
        holder.tv_name.setText(texty.getName());
        holder.tv_art.setText(texty.getArt());
    }

    @Override
    public int getItemCount() {
        return textyList.size();
    }


}
