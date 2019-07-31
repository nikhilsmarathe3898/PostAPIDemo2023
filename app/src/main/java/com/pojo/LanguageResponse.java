package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ali on 5/16/2018.
 */
public class LanguageResponse implements Serializable
{
    /*{"message":"Got The Details.",
    "data":[{"_id":"5a7165bf61596a014a04e799","lan_id":1,"lan_name":"French","code":"fench","Active":1},
    {"lan_name":"English","code":"en"}]}*/
    private ArrayList<LanguagesLists>data;

    public ArrayList<LanguagesLists> getLanguagesLists() {
        return data;
    }

    public class LanguagesLists
    {
        /*"_id":"5a7165bf61596a014a04e799",
"lan_id":1,
"lan_name":"French",
"code":"fench",
"Active":1*/
       private String code,lan_name;

        public String getCode() {
            return code;
        }

        public String getLan_name() {
            return lan_name;
        }
    }
}
