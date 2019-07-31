package com.pojo.address_pojo;

/**
 * <h>Drop_Location_Google_Pojo</h>
 * Created by embed on 27/3/17.
 */

public class Drop_Location_Google_Pojo
{
  //  private Result_Google_Pojo result;

   private Result_Data result;

    private String status;


    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public Result_Data getResult() {
        return result;
    }

    /* @Override
    public String toString()
    {
        return "ClassPojo [result = "+result+", html_attributions = "+html_attributions+", status = "+status+"]";
    }*/
}
