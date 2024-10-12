package com.example.example

import com.google.gson.annotations.SerializedName


data class Meta (

  @SerializedName("analyzer_version" ) var analyzerVersion : String? = null,
  @SerializedName("platform"         ) var platform        : String? = null,
  @SerializedName("detailed_status"  ) var detailedStatus  : String? = null,
  @SerializedName("status_code"      ) var statusCode      : Int?    = null,
  @SerializedName("timestamp"        ) var timestamp       : Int?    = null,
  @SerializedName("analysis_time"    ) var analysisTime    : Double? = null,
  @SerializedName("input_process"    ) var inputProcess    : String? = null

)