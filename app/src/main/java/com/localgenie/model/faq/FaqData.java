
package com.localgenie.model.faq;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FaqData implements Serializable {

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("subcat")
    @Expose
    private List<Subcat> subcat = null;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("link")
    @Expose
    private String link;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Subcat> getSubcat() {
        return subcat;
    }

    public void setSubcat(List<Subcat> subcat) {
        this.subcat = subcat;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}