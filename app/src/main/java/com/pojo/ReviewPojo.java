package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ali on 4/25/2018.
 */
public class ReviewPojo implements Serializable
{



    private String message;  // = "";
    private SignUpDataSid data;

    public String getMessage() {
        return message;
    }

    public SignUpDataSid getData() {
        return data;
    }

    public class SignUpDataSid implements Serializable
    {
        private float averageRating; // for Review Fragment
        private int reviewCount; // for Review Fragment

        private ArrayList<ProviderDetailsResponse.ProviderResponseDetails.ReviewList> reviews; //ArrayList For Reviews

        public float getAverageRating() {
            return averageRating;
        }

        public int getReviewCount() {
            return reviewCount;
        }

        public ArrayList<ProviderDetailsResponse.ProviderResponseDetails.ReviewList> getReviews() {
            return reviews;
        }
    }

}
