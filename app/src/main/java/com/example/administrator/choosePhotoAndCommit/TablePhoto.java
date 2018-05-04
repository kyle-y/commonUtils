package com.example.administrator.choosePhotoAndCommit;

/**
 * Created by yxb on 2017/10/19.
 */

public class TablePhoto {
    private int type;  //0表示正常图片， 1表示照相机
    private String path;

    public TablePhoto(int type, String path) {
        this.type = type;
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public String getPath() {
        return path;
    }
}
