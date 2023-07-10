# PostAPIDemo2023
Send data in API and Get Response


STEP 1 :  Create Empty Activity.

STEP 2 :  Insert dependency implementation.

            implementation 'com.squareup.retrofit2:retrofit:2.9.0'
            implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

STEP 3 :  Create RETROFITCLIENT.java file

             private static Retrofit retrofit;                               /** Add Retrofit Initialization */
                private static String BASE_URL = "https://reqres.in/";          /** Add Retrofit BASE URL Link Initialization */
                public static Retrofit getRetrofitInstance(){                   /** Add Retrofit Instance Initialization */

                    if(retrofit != null){
                        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                    }
                    return retrofit;
                }


STEP 4 :  Study on Response and then create POJO file. "postApiPojo"

            -variables, Constructor, Getter and Setter done.
            If Response like :
            {
                "name": "morpheus",
                "job": "leader",
                "id": "469",
                "createdAt": "2022-11-22T04:31:00.355Z"
            }

STEP 5 :  Create Interface to declare a method that calls an API with the name Methods.java

            public interface Methods {

                @FormUrlEncoded
                @POST("/api/users")
                Call<Model> getUserData(@Field("name")String name,@Field("job") String job);
            }

STEP 6 :   Create UI on Main Activity or where you need to show function.  "activity_post_api_final.xml"

STEP 7 :   Declare UI's field variable in "MainActivity.java" file

            private EditText txtName,txtJob;
            private TextView lblOutput;
            private Button btnPostData;


STEP 8 :   Create Button Click. Checks all The data is not null.

STEP 9 :   Give Internet Permission.

STEP 10 :   In the Button Click, Create Interface initialization and call the Pojo.

STEP 11 :   Store Response Data in textview as an output.
