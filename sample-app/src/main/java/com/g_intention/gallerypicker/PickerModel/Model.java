package com.g_intention.gallerypicker.PickerModel;

public class Model {
    // -- Implementing
    // 1-:) We need to label of media picker as string.
    // 2-:)  Thumbnails id for storing thumbnail id.
    // 2-:) Leave one empty constructor
    private String label_name;
    private int thumbnail_id;

    public Model(String label_name, int thumbnail_id) {
        this.label_name = label_name;
        this.thumbnail_id = thumbnail_id;
    }

    public String getLabel_name() {
        return label_name;
    }

    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }

    public int getThumbnail_id() {
        return thumbnail_id;
    }

    public void setThumbnail_id(int thumbnail_id) {
        this.thumbnail_id = thumbnail_id;
    }
}
