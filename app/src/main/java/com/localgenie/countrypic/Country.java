package com.localgenie.countrypic;

/**
 * Created by embed on 6/9/16.
 */
public class Country
{
    private String code;
    private String name;
    private String dial_code;
    private int flag;
    private String min_digits;
    private String max_digits;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getDial_code() {
        return dial_code;
    }

    public void setDial_code(String dial_code) {
        this.dial_code = dial_code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMin_digits() {
        return min_digits;
    }

    public void setMin_digits(String min_digits) {
        this.min_digits = min_digits;
    }

    public String getMax_digits() {
        return max_digits;
    }

    public void setMax_digits(String max_digits) {
        this.max_digits = max_digits;
    }
}
