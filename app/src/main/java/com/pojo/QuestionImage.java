package com.pojo;

/**
 * Created by Ali on 7/24/2018.
 */
public class QuestionImage
{
    private String image;
    private boolean isImage;
    private int imagePostion;

    public QuestionImage(String image, boolean isImage,int imagePostion) {
        this.image = image;
        this.isImage = isImage;
        this.imagePostion  = imagePostion;
    }

    public int getImagePostion() {
        return imagePostion;
    }

    public void setImagePostion(int imagePostion) {
        this.imagePostion = imagePostion;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }
}
