package com.pojo;

import java.io.Serializable;

/**
 * Created by Ali on 5/30/2018.
 */
public class HelpReason implements Serializable
{
    /*"_id":"5b0e8ce161596a224660c6cf",
"res_id":1,
"reasons":{
"en":"texst",
"fench":"tefd"
},
"res_for":"provider",
"name":"texst"*/

    private String name,res_for;

    public String getName() {
        return name;
    }

    public String getRes_for() {
        return res_for;
    }
}
