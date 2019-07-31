package com.pojo;

/**
 * <h>ProviderDetailsBooking</h>
 * Created by Ali on 2/20/2018.
 */

public class ProviderDetailsBooking
{
    double distance;
    private int distanceMatrix,reviewCount,proStatus;
    private float averageRating;
    private String firstName,lastName,phone,providerId,profilePic;
    private ProLocation proLocation;

    public double getDistance() {
        return distance;
    }

    public int getDistanceMatrix() {
        return distanceMatrix;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getProStatus() {
        return proStatus;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public ProLocation getProLocation() {
        return proLocation;
    }
}
