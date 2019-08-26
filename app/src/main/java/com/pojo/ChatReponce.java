package com.pojo;

import java.io.Serializable;

/**
 * Created by Ali on 12/22/2017.
 */

public class ChatReponce implements Serializable
{
    /*"data":{
"bid":"1513941569904",
"content":"Gdf",
"fromID":"5a3cbbf7b121331d381cd5be",
"targetId":"5a150c87b121331d381ccb5d",
"timestamp":1513941666,
"type":1
}*/

    /*"_id":"5a61dd6bb626eb76eb0201b8",
"type":1,    6
"timestamp":1516363114,     5
"content":"m coming bro",  2
"fromID":"5a4f6568b121331d381cd8c8",  3
"bid":"1516198616724",  1
"targetId":"5a4636a2b121331d381cd762"   4 */


   private ChatData data;

    public ChatData getData() {
        return data;
    }
    public void setData(ChatData data) {
        this.data = data;
    }

}
