package com.pojo;

import java.io.Serializable;

/**
 * <h>ProviderEvents</h>
 * Created by Ali on 2/13/2018.
 */

public class ProviderEvents implements Serializable
{
    /*"_id":"59cc98e4b05549255330ae12",
"name":"Birthday Party",
"selectImage":"https://s3.amazonaws.com/iserve/8988260408472.png",
"unselectImage":"https://s3.amazonaws.com/iserve/4937064970285.png",
"status":true*/
    private String _id, name, selectImage, unselectImage;
    private boolean status;

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getSelectImage() {
        return selectImage;
    }

    public String getUnselectImage() {
        return unselectImage;
    }

    public boolean isStatus() {
        return status;
    }
}
