package com.liqq.photolistdemo.bean;

/**
 * Created by liqq on 2018/12/22.
 * 主要为解决当缓存清除之后重新获取导致条目变化的问题。
 */

public class LruPhoto {

    private int width;
    private int height;
    private boolean isCreate;
    private Integer imageResources;


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public Integer getImageResources() {
        return imageResources;
    }

    public void setImageResources(Integer imageResources) {
        this.imageResources = imageResources;
    }


}
