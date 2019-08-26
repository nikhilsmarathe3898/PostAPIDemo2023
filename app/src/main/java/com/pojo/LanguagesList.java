package com.pojo;

import java.io.Serializable;

/**
 * Created by Ali on 5/16/2018.
 */
public class LanguagesList implements Serializable
{
    String code,lan_name;
    public LanguagesList(String code, String name, int isRTL)
    {
        this.code = code;
        this.lan_name = name;
        this.langDirection = isRTL;
    }

    private int langDirection;
    /*"data":[{"_id":"5a7165bf61596a014a04e799","lan_id":1,"lan_name":"French","code":"fench","Active":1},
    {"lan_name":"English","code":"en"}]}*/

    public String getCode() {
        return code;
    }

    public int getLangDirection() {
        return langDirection;
    }

    public String getName() {
        return lan_name;
    }
}
