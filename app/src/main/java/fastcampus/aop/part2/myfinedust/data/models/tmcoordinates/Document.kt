package fastcampus.aop.part2.myfinedust.data.models.tmcoordinates


import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("x")
    val x: Double?,
    @SerializedName("y")
    val y: Double?
)