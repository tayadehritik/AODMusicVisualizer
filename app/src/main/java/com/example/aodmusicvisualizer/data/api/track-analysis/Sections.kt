package com.example.example

import com.google.gson.annotations.SerializedName


data class Sections (

  @SerializedName("start"                     ) var start                   : Double?    = null,
  @SerializedName("duration"                  ) var duration                : Double? = null,
  @SerializedName("confidence"                ) var confidence              : Double?    = null,
  @SerializedName("loudness"                  ) var loudness                : Double? = null,
  @SerializedName("tempo"                     ) var tempo                   : Double? = null,
  @SerializedName("tempo_confidence"          ) var tempoConfidence         : Double? = null,
  @SerializedName("key"                       ) var key                     : Double?    = null,
  @SerializedName("key_confidence"            ) var keyConfidence           : Double? = null,
  @SerializedName("mode"                      ) var mode                    : Double?    = null,
  @SerializedName("mode_confidence"           ) var modeConfidence          : Double? = null,
  @SerializedName("time_signature"            ) var timeSignature           : Double?    = null,
  @SerializedName("time_signature_confidence" ) var timeSignatureConfidence : Double?    = null

)