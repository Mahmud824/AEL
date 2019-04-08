package com.example.xyz.Interface;

import com.example.xyz.NearBy.NearByPlacesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NearByResponseServices {
    @GET
    Call<NearByPlacesResponse> getNearByService(@Url String endUrl);
}
