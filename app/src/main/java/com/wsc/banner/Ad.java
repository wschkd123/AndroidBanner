package com.wsc.banner;

/**
 * Created by wsc on 16-4-21.
 */
public class Ad {
    private Integer img;
    private String title;
    private String subtitle;

    public Ad(Integer img, String title, String subtitle) {
        this.img = img;
        this.title = title;
        this.subtitle = subtitle;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }
}
