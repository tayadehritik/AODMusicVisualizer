package com.example.example

import com.google.gson.annotations.SerializedName


data class Images (

  @SerializedName("url"    ) var url    : String? = null,
  @SerializedName("height" ) var height : Int?    = null,
  @SerializedName("width"  ) var width  : Int?    = null

)