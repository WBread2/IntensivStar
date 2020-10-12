package ru.mikhailskiy.intensiv.data

import com.google.gson.annotations.SerializedName

data class MovieCreditsResponse (
    @SerializedName("id")
    var id: Int,
    @SerializedName("cast")
    var cast: List<CastItem>,
    @SerializedName("crew")
    var crew: List<CrewItem>
)