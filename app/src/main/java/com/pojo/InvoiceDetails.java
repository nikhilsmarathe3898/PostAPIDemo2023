package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>InvoiceDetails</h>
 * Created by Ali on 11/25/2017.
 */

public class InvoiceDetails implements Serializable {
    /*"message":"sucess",
"data":{}*/
    private String message;
    private InvoiceData data;

    public String getMessage() {
        return message;
    }

    public InvoiceData getData() {
        return data;
    }

    public class InvoiceData {
        /*"bookingId":1511588491962,
"bookingRequestedFor":1511588492,
"bookingModel":2,
"status":10,
"statusMsg":"Raise invoice",
"addLine1":"",
"providerData":{},
"accounting":{},
"service":{},
"additionalService":[],
"signURL":""*/

        private long bookingId;
        private long bookingRequestedFor;
        private String signURL,addLine1,currencySymbol,categoryId,reminderId,categoryName;
        private ProviderInvoiceDetails providerData;
        private BookingAccounting accounting;
        private CartInfo cart;
        private ArrayList<CustomerRating>customerRating;
        private ArrayList<AdditionalService>additionalService;
        private int bookingModel;
        private String dropNotes,pickupNotes;
        private ArrayList<String> pickupImages;
        private ArrayList<String> dropImages;


        public String getCategoryName() {
            return categoryName;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public int getBookingModel() {
            return bookingModel;
        }

        public String getReminderId() {
            return reminderId;
        }

        public ArrayList<AdditionalService> getAdditionalService() {
            return additionalService;
        }

        public ArrayList<CustomerRating> getCustomerRating() {
            return customerRating;
        }

        public String getAddLine1() {
            return addLine1;
        }

        public long getBookingId() {
            return bookingId;
        }

        public long getBookingRequestedFor() {
            return bookingRequestedFor;
        }

        public String getSignURL() {
            return signURL;
        }

        public ProviderInvoiceDetails getProviderData() {
            return providerData;
        }

        public BookingAccounting getAccounting() {
            return accounting;
        }

        public CartInfo getCart() {
            return cart;
        }


        public String getDropNotes() {
            return dropNotes;
        }

        public String getPickupNotes() {
            return pickupNotes;
        }

        public ArrayList<String> getPickupImages() {
            return pickupImages;
        }

        public ArrayList<String> getDropImages() {
            return dropImages;
        }

        public class ProviderInvoiceDetails {
            /*"firstName":"Piyush",
"lastName":"kumar",
"profilePic":"https://s3.amazonaws.com/livemapplication/Provider/ProfilePics/Profile1511587166634.png",
"phone":"+918505997523"*/

            private String firstName, lastName, profilePic, phone,providerId;

            public String getFirstName() {
                return firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public String getProfilePic() {
                return profilePic;
            }

            public String getPhone() {
                return phone;
            }

            public String getProviderId() {
                return providerId;
            }
        }


    }

    public class CustomerRating implements Serializable
    {
        /*"_id":"5b0952b561596a47cc4f3e86",
"name":""*/
        String _id,name;
        private float rating;

        public float getRatings() {
            return rating;
        }

        public void setRatings(float ratings) {
            this.rating = ratings;
        }

        public String get_id() {
            return _id;
        }

        public String getName() {
            return name;
        }
    }
}
