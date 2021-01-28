package com.gtsr.gtsr;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by WAVE on 10/20/2017.
 */

public interface ServerApisInterface {
    String home_URL = "http://34.199.165.142:3000/api/";/*"http://54.210.61.0:8096/spectrocare/";*/
    String home_Image_URL = "http://54.210.61.0:8096";
    String GOOGLE_MAPS_BASE_URL = "https://maps.googleapis.com";

    @POST("testitems/fetch")
    Call<JsonObject> fetchTestItemsApi(@Header("x-access-token") String accessToken, @Body JsonObject body);

    @HTTP(method = "GET", path = "lang", hasBody = false)
    Call<LanguageServerObjects> getLanguages();


}
