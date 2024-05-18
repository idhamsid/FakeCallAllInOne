package com.javadroid.fakecall.model;

public class ModelKontak {
    private String id;
    private String image;
    private String nama;
    private String suara;
    private String video;

    public ModelKontak() {
        this.id = id;
        this.image = image;
        this.nama = nama;
        this.suara = suara;
        this.video = video;
    }

    public String getId() {
        return  id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getSuara() {
        return suara;
    }

    public void setSuara(String suara) {
        this.suara = suara;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

}
