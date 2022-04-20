package fastcampus.aop.part2.myfinedust.data.models.tmcoordinates


import com.google.gson.annotations.SerializedName

data class TmCoordinatesResponse(
    @SerializedName("documents")
    val documents: List<Document>?,
    @SerializedName("meta")
    val meta: Meta?
)