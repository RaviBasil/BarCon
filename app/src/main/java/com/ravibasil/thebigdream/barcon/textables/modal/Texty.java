package com.ravibasil.thebigdream.barcon.textables.modal;

/**
 * Created by ravibasil on 2/1/18.
 */

public class Texty {
    String name, art,category;
    int id;

    public Texty( int id,String category, String name, String art, int favorite) {
        this.name = name;
        this.art = art;
        this.category = category;
        this.id = id;
        this.favorite = favorite;
    }

    int favorite;


    public Texty() {

    }

    public Texty(String category, String name, String art) {
        this.name = name;
        this.art = art;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

}
