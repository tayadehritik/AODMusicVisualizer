package com.example.example

import com.google.gson.annotations.SerializedName


data class Bars (

  @SerializedName("start"      ) var start      : Double? = null,
  @SerializedName("duration"   ) var duration   : Double? = null,
  @SerializedName("confidence" ) var confidence : Double? = null

)