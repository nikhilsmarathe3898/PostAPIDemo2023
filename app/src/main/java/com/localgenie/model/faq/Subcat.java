
package com.localgenie.model.faq;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Subcat implements Serializable {

    @SerializedName("Name")
    @Expose
    private String name;
   /* @SerializedName("desc")
    @Expose
    private String desc;*/
    @SerializedName("link")
    @Expose
    private String link;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   /* public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }*/

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}