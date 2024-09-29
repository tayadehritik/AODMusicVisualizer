package com.example.example

import com.google.gson.annotations.SerializedName


data class Segments (

  @SerializedName("start"             ) var start           : Double?              = null,
  @SerializedName("duration"          ) var duration        : Double?           = null,
  @SerializedName("confidence"        ) var confidence      : Double?              = null,
  @SerializedName("loudness_start"    ) var loudnessStart   : Double?              = null,
  @SerializedName("loudness_max_time" ) var loudnessMaxTime : Double?              = null,
  @SerializedName("loudness_max"      ) var loudnessMax     : Double?              = null,
  @SerializedName("loudness_end"      ) var loudnessEnd     : Double?              = null,
  @SerializedName("pitches"           ) var pitches         : ArrayList<Double> = arrayListOf(),
  @SerializedName("timbre"            ) var timbre          : ArrayList<Double> = arrayListOf()

)