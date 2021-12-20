package com.wuwei.filestorage.entity;

import com.google.gson.Gson;

public class ChunkUpResultDto {

    private boolean ban;

    private boolean canSetMainAccess;

    private boolean doc;

    private boolean img;

    private boolean mainAccess;

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }

    public boolean isCanSetMainAccess() {
        return canSetMainAccess;
    }

    public void setCanSetMainAccess(boolean canSetMainAccess) {
        this.canSetMainAccess = canSetMainAccess;
    }

    public boolean isDoc() {
        return doc;
    }

    public void setDoc(boolean doc) {
        this.doc = doc;
    }

    public boolean isImg() {
        return img;
    }

    public void setImg(boolean img) {
        this.img = img;
    }

    public boolean isMainAccess() {
        return mainAccess;
    }

    public void setMainAccess(boolean mainAccess) {
        this.mainAccess = mainAccess;
    }

    @Override
    public String toString() {
        return "ChunkUpResultDto{" +
                "ban=" + ban +
                ", fileKey='" + canSetMainAccess + '\'' +
                ", doc='" + doc + '\'' +
                ", success='" + img + '\'' +
                ", tenantKey='" + mainAccess + '\'' +
                '}';
    }


}
