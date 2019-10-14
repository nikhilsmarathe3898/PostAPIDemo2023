package com.pojo;

import com.localgenie.model.Location;
import com.localgenie.model.youraddress.YourAddrData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>ProviderDetailsResponse</h>
 * Created by Ali on 2/5/2018.
 */

public class ProviderDetailsResponse implements Serializable
{
    /*"message":"Provider details received successfully.",
"data":{}*/
    String message;

    private ProviderResponseDetails data;

    public String getMessage() {
        return message;
    }

    public ProviderResponseDetails getData() {
        return data;
    }

    public class ProviderResponseDetails implements Serializable
    {
        /*"firstName":"Joe",
"lastName":"Yas",
"profilePic":"https://s3.amazonaws.com/livemapplication/Provider/ProfilePics/1516625352385_0_01.png",
"email":"joe@mobifyi.com",
"bannerImage":"",
"lastActive":1517653544,
"photo":"",
"noOfReview":0,
"rating":0,
"location":{},
"rateCard":[],
"countryCode":"",
"mobile":"",
"about":"Having Exp. in Teaching of more than 10 Years. It your call to chose best among best.",
"events":[],
"rules":"",
"musicGenres":"",
"instrument":"",
"review":[],
"services":[],
"distance":0,
"amount":45,
"youtubeUrlLink":"www.youTube.com",
"currencySymbol":"$",
"currency":"USD",
"distanceMatrix":1,
"experties":"Guitar,Piano,Flute,Drum",
"lanKnow":"English,hindi,French"*/

        /*"rateCard":[],
"countryCode":"",
"mobile":"",
"about":"",
"events":[],
"rules":"",
"musicGenres":"",
"instrument":"",
"review":[],
"services":[],
"slotData":[],
"address":{},
"metaDataArr[]"*/

        private String firstName,lastName,profilePic,email,
                countryCode,mobile,about
                ,currencySymbol;  //experties,lanKnow,rules,musicGenres,instrument,youtubeUrlLink,bannerImage,photo
        private int noOfReview,distanceMatrix,totalBooking;
        private float rating;
        private double distance,amount;
        private ArrayList<ReviewList> review;
        private ArrayList<ServiceCategory>services;
        private ArrayList<ProviderEvents> events;
        private ArrayList<MetaDataArray>metaDataArr;
        private ArrayList<RatingLog>ratingLog;
        private ArrayList<String>workImage;
       // private ArrayList<Slots>slotData;
        private Location location;
        private YourAddrData address;

        public YourAddrData getAddress() {
            return address;
        }

/*
        public ArrayList<Slots> getSlotData() {
            return slotData;
        }
*/

        public Location getLocation() {
            return location;
        }

        public ArrayList<RatingLog> getRatingLog() {
            return ratingLog;
        }

        public int getTotalBooking() {
            return totalBooking;
        }

        public ArrayList<String> getWorkImage() {
            return workImage;
        }

        public ArrayList<ProviderEvents> getEvents() {
            return events;
        }

        public ArrayList<MetaDataArray> getMetaDataArr() {
            return metaDataArr;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public String getEmail() {
            return email;
        }

   /*     public String getBannerImage() {
            return bannerImage;
        }

        public String getPhoto() {
            return photo;
        }*/

        public String getCountryCode() {
            return countryCode;
        }

        public String getMobile() {
            return mobile;
        }

        public String getAbout() {
            return about;
        }

      /*  public String getRules() {
            return rules;
        }

        public String getMusicGenres() {
            return musicGenres;
        }

        public String getInstrument() {
            return instrument;
        }

        public String getYoutubeUrlLink() {
            return youtubeUrlLink;
        }
*/
        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public int getDistanceMatrix() {
            return distanceMatrix;
        }

/*        public String getExperties() {
            return experties;
        }

        public String getLanKnow() {
            return lanKnow;
        }*/

        public int getNoOfReview() {
            return noOfReview;
        }

        public float getRating() {
            return rating;
        }

        public double getDistance() {
            return distance;
        }

        public double getAmount() {
            return amount;
        }

        public ArrayList<ReviewList> getReview() {
            return review;
        }

        public ArrayList<ServiceCategory> getServices() {
            return services;
        }

        public class ReviewList implements Serializable
        {
            private String bookingId, userId, firstName, lastName;
            private String review, reviewBy, profilePic;
            private float rating;
            private long reviewAt;

            public String getBookingId() {
                return bookingId;
            }

            public String getUserId() {
                return userId;
            }

            public String getFirstName() {
                return firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public String getProfilePic() {
                return profilePic;
            }

            public String getReview() {
                return review;
            }

            public float getRating() {
                return rating;
            }

            public String getReviewBy() {
                return reviewBy;
            }

            public long getReviewAt() {
                return reviewAt;
            }

/*
            public void setReviewAt(long reviewAt) {
                this.reviewAt = reviewAt;
            }
*/
        }

        public class ServiceCategory implements Serializable
        {
            /*"id":"5a40fc0761596a7b7c1de952",
"name":"15",
"unit":"MINS",
"price":45*/

            private String id,name,description;  //unit
            private double price;
          //  private int getTempQuant;


        /*    public int getGetTempQuant() {
                return getTempQuant;
            }

            public void setGetTempQuant(int getTempQuant) {
                this.getTempQuant = getTempQuant;
            }
*/
            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

/*
            public String getUnit() {
                return unit;
            }
*/

            public String getDescription() {
                return description;
            }

            public double getPrice() {
                return price;
            }
        }
    }


    public class MetaDataArray implements Serializable
    {
        String fieldName,data;
        int fieldType;
        private ArrayList<PredefinedArray>preDefined;

        public ArrayList<PredefinedArray> getPreDefined() {
            return preDefined;
        }

        public String getFieldName() {
            return fieldName;
        }

        public int getFieldType() {
            return fieldType;
        }

        public String getData() {
            return data;
        }
    }

    public class PredefinedArray implements Serializable
    {
        /*"_id":"5ac1df8061596a79013b72eb",
"name":"finger print",
"icon":"https://s3.amazonaws.com/livemapplication/13431340645921.png"*/
        private String name,icon;

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }
    }

    public class RatingLog implements Serializable
    {
        /*"name":"Initial Impression",
"_id":"5b0952b561596a47cc4f3e86",
"rating":1,
"totalRating":1*/
        String name,_id;
        float rating;//totalRating;

        public String getName() {
            return name;
        }

        public String get_id() {
            return _id;
        }

        public float getRating() {
            return rating;
        }

/*
        public float getTotalRating() {
            return totalRating;
        }
*/
    }
}
