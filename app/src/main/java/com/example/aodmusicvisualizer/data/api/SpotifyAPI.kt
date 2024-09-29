package com.example.aodmusicvisualizer.data.api

import com.example.example.TrackAnalysis
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query



data class authResponse (
    @SerializedName("access_token") var access_token:String,
    @SerializedName("token_type") var token_type:String,
    @SerializedName("expires_in") var expires_in:Int
)
interface SpotifyAPI {

    @GET("audio-analysis/{id}")
    suspend fun getTrackAnalysis(@Path("id") trackURI:String,
                                 @Header("Authorization") token:String):Response<TrackAnalysis>


}

interface SpotifyAuthAPI {

    @FormUrlEncoded
    @POST("api/token")
    suspend fun getToken(
        @Field("grant_type") grant_type: String = "client_credentials",
        @Field("client_id") client_id:String = "74aaba15df23432ea5795d9668f8d058",
        @Field("client_secret") client_secret:String = "95816c711c6a40fa87b72ab9f0d49630")
    :Response<authResponse>
}

