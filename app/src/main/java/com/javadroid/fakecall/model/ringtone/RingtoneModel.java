package com.javadroid.fakecall.model.ringtone;

public class RingtoneModel {
    private int id;
    private String title;
    private String source;

    private String sourceOff33;

    public RingtoneModel(int id, String title, String source, String sourceOff33) {
        this.id = id;
        this.title = title;
        this.source = source;
        this.sourceOff33 = sourceOff33;


    }
    public String getSourceOff33() {
        return sourceOff33;
    }

    public void setSourceOff33(String sourceOff33) {
        this.sourceOff33 = sourceOff33;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
