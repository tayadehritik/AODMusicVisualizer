package com.example.example

import com.google.gson.annotations.SerializedName


data class TrackAnalysis (

  @SerializedName("meta"     ) var meta     : Meta?               = Meta(),
  @SerializedName("track"    ) var track    : Track?              = Track(),
  @SerializedName("bars"     ) var bars     : ArrayList<Bars>     = arrayListOf(),
  @SerializedName("beats"    ) var beats    : ArrayList<Beats>    = arrayListOf(),
  @SerializedName("sections" ) var sections : ArrayList<Sections> = arrayListOf(),
  @SerializedName("segments" ) var segments : ArrayList<Segments> = arrayListOf(),
  @SerializedName("tatums"   ) var tatums   : ArrayList<Tatums>   = arrayListOf()

)