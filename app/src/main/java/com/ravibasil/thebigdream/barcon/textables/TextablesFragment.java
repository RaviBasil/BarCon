package com.ravibasil.thebigdream.barcon.textables;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ravibasil.thebigdream.barcon.R;
import com.ravibasil.thebigdream.barcon.database.DatabaseHandler;
import com.ravibasil.thebigdream.barcon.textables.adapter.HeaderRecyclerViewSection;
import com.ravibasil.thebigdream.barcon.textables.modal.Texty;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextablesFragment extends Fragment {


    public TextablesFragment() {
        // Required empty public constructor
    }
    private List<Texty> textyList = new ArrayList<>();
    private RecyclerView recyclerView;
  //  private TextAdapter textAdapter;
    Context context;
    private SectionedRecyclerViewAdapter sectionedAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_textables, container, false);
        context = getActivity();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Textables");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewTextables);



        sectionedAdapter = new SectionedRecyclerViewAdapter();

     //   textAdapter = new TextAdapter(context,textyList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);//new GridLayoutManager(context,2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(textAdapter);
        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(sectionedAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(sectionedAdapter);

       /* try {
            parseJsonFile();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        //use the mikespenz

        //create adapter
       getTextablesFromDatabase();
    }

    public void getTextablesFromDatabase() {
        //clear
        sectionedAdapter.removeAllSections();

        DatabaseHandler db = new DatabaseHandler(context);

       List<Texty> data = db.getFavoriteAllTexty();
        List<Texty> favoriteList = new ArrayList<Texty>();
        for(Texty cn : data){
            Log.d("Favorite-->",cn.getName());
            favoriteList.add(new Texty(cn.getId(),cn.getCategory(),cn.getName(),
                    cn.getArt(),cn.getFavorite()));
        }
        if(favoriteList != null && favoriteList.size()>0) {
            HeaderRecyclerViewSection section = new HeaderRecyclerViewSection(context, "Favorite",
                    favoriteList, sectionedAdapter, TextablesFragment.this);
            sectionedAdapter.addSection(section);
        }
       // HeaderRecyclerViewSection section = new HeaderRecyclerViewSection(context,category,data,sectionedAdapter);
        //sectionedAdapter.addSection(section);

        //getheaders
        String headers="";
        List<Texty> dataList = db.getNotFavoriteAllTexty();
        List<Texty> itemList = new ArrayList<Texty>();

        for (Texty texty : dataList){


        //Log.d("hello",texty.getCategory()+"---"+texty.getName()+"--");

            if(headers.equalsIgnoreCase("")){
                headers = texty.getCategory();
                itemList.add(new Texty(texty.getId(),texty.getCategory(),texty.getName(),
                        texty.getArt(),texty.getFavorite()));
            }else if(headers.equalsIgnoreCase(texty.getCategory())){
                itemList.add(new Texty(texty.getId(),texty.getCategory(),texty.getName(),
                        texty.getArt(),texty.getFavorite()));
            }else {
                HeaderRecyclerViewSection section1 = new HeaderRecyclerViewSection(context, headers, itemList, sectionedAdapter, TextablesFragment.this);
                sectionedAdapter.addSection(section1);
                //clear
                itemList = new ArrayList<Texty>();
                itemList.add(new Texty(texty.getId(),texty.getCategory(),texty.getName(),
                        texty.getArt(),texty.getFavorite()));
                headers = texty.getCategory();
            }

        }
        recyclerView.setAdapter(sectionedAdapter);
    }

}
