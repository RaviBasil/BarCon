package com.ravibasil.thebigdream.barcon.textables.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ravibasil.thebigdream.barcon.R;
import com.ravibasil.thebigdream.barcon.database.DatabaseHandler;
import com.ravibasil.thebigdream.barcon.textables.TextablesFragment;
import com.ravibasil.thebigdream.barcon.textables.modal.Texty;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by ravibasil on 2/3/18.
 */

public class HeaderRecyclerViewSection extends StatelessSection {
    private static final String TAG = HeaderRecyclerViewSection.class.getSimpleName();
    private String title;
    private List<Texty> list;
    private Context context;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private TextablesFragment textablesFragment;

    public HeaderRecyclerViewSection(Context context, String title, List<Texty> list,
                                     SectionedRecyclerViewAdapter sectionAdapter,
                                     TextablesFragment textablesFragment) {
        super(R.layout.header_layout, R.layout.single_texty_item);
        this.title = title;
        this.list = list;
        this.context = context;
        this.sectionAdapter = sectionAdapter;
        this.textablesFragment =textablesFragment;
    }
    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder iHolder = (ItemViewHolder)holder;
        iHolder.tv_name.setText(list.get(position).getName());
        iHolder.tv_art.setText(list.get(position).getArt());
        if(list.get(position).getFavorite() == 1){
            Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_star_golden);
            iHolder.imageViewStar.setImageDrawable(myDrawable);
        }

        iHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int i = sectionAdapter.getPositionInSection(iHolder.getAdapterPosition());
                /*Toast.makeText(context,list.get(position).getArt()
                        ,Toast.LENGTH_SHORT).show();*/
                openIntentSendTo(list.get(position).getArt());

            }
        });

        iHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showBottomSheetDialog(list.get(position).getArt(),
                        list.get(position).getFavorite(), list.get(position).getId());


                return true;
            }
        });
    }

    public void openIntentSendTo(String text){

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,text);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public void showBottomSheetDialog(final String art, final int favorite, final int id) {
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_textables, null);


        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);

        TextView tv_text = (TextView) dialog.findViewById(R.id.tv_text);
        tv_text.setText("Text :"+art);
        //add to favorite
        Button btn_favorate = (Button) dialog.findViewById(R.id.btn_add_favorite);
        if(favorite ==0){
            btn_favorate.setText("Add Favorite");
        }else{

            btn_favorate.setText("Remove Favorite");
        }
        //handling bottom sheet favoratite button click

        btn_favorate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler db =new DatabaseHandler(context);

                if(favorite==0){
                    int i =db.updateList(id, 1);
                    //Toast.makeText(context,id+"--"+favorite+"--"+i,Toast.LENGTH_SHORT).show();
                    textablesFragment.getTextablesFromDatabase();
                }else{
                    int i =db.updateList(id, 0);
                   // Toast.makeText(context,id+"--"+favorite+"--"+i,Toast.LENGTH_SHORT).show();
                    textablesFragment.getTextablesFromDatabase();
                }
                dialog.dismiss();
            }
        });

        //copy clipboard
        dialog.findViewById(R.id.btn_copy_clipboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(context,art);
            }
        });

        //send to
        dialog.findViewById(R.id.btn_send_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openIntentSendTo(art);
                dialog.dismiss();
            }
        });

        //cancel
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context,"Copied "+text,Toast.LENGTH_SHORT).show();
    }
    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder hHolder = (HeaderViewHolder)holder;
        hHolder.headerTitle.setText(title);
    }
}