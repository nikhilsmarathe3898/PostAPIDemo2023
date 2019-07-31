package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryResponse {

@SerializedName("message")
@Expose
private String message;
@SerializedName("data")
@Expose
private CategoryData data = null;

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

    public CategoryData getData() {
        return data;
    }

    public void setData(CategoryData data) {
        this.data = data;
    }

    public class CategoryData  implements Serializable
    {
     //   @SerializedName("category")
        private ArrayList<CatDataArray>catArr;

     //   @SerializedName("cityData")
        private CityData cityData;

        private ArrayList<Category>trendingArr;
        private ArrayList<Category>recommendedArr;

        public ArrayList<CatDataArray> getCatArr() {
            return catArr;
        }

        public CityData getCityData() {
            return cityData;
        }

        public ArrayList<Category> getTrendingArr() {
            return trendingArr;
        }

        public ArrayList<Category> getRecommendedArr() {
            return recommendedArr;
        }
    }
}