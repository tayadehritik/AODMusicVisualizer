package com.example.example

import com.google.gson.annotations.SerializedName


data class Track (

  @SerializedName("num_samples"               ) var numSamples              : Int?    = null,
  @SerializedName("duration"                  ) var duration                : Double? = null,
  @SerializedName("sample_md5"                ) var sampleMd5               : String? = null,
  @SerializedName("offset_seconds"            ) var offsetSeconds           : Int?    = null,
  @SerializedName("window_seconds"            ) var windowSeconds           : Int?    = null,
  @SerializedName("analysis_sample_rate"      ) var analysisSampleRate      : Int?    = null,
  @SerializedName("analysis_channels"         ) var analysisChannels        : Int?    = null,
  @SerializedName("end_of_fade_in"            ) var endOfFadeIn             : Double? = null,
  @SerializedName("start_of_fade_out"         ) var startOfFadeOut          : Double? = null,
  @SerializedName("loudness"                  ) var loudness                : Double? = null,
  @SerializedName("tempo"                     ) var tempo                   : Double? = null,
  @SerializedName("tempo_confidence"          ) var tempoConfidence         : Double? = null,
  @SerializedName("time_signature"            ) var timeSignature           : Int?    = null,
  @SerializedName("time_signature_confidence" ) var timeSignatureConfidence : Double? = null,
  @SerializedName("key"                       ) var key                     : Int?    = null,
  @SerializedName("key_confidence"            ) var keyConfidence           : Double? = null,
  @SerializedName("mode"                      ) var mode                    : Int?    = null,
  @SerializedName("mode_confidence"           ) var modeConfidence          : Double? = null,
  @SerializedName("codestring"                ) var codestring              : String? = null,
  @SerializedName("code_version"              ) var codeVersion             : Double? = null,
  @SerializedName("echoprintstring"           ) var echoprintstring         : String? = null,
  @SerializedName("echoprint_version"         ) var echoprintVersion        : Double? = null,
  @SerializedName("synchstring"               ) var synchstring             : String? = null,
  @SerializedName("synch_version"             ) var synchVersion            : Int?    = null,
  @SerializedName("rhythmstring"              ) var rhythmstring            : String? = null,
  @SerializedName("rhythm_version"            ) var rhythmVersion           : Int?    = null

)