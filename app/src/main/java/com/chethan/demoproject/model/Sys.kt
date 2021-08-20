package com.chethan.demoproject.model


import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("sunrise")
    val sunrise: Int?,
    @SerializedName("sunset")
    val sunset: Int?,
    @SerializedName("type")
    val type: Int?
)