package fastcampus.aop.part2.myfinedust.data.models.monitoringstation


import com.google.gson.annotations.SerializedName

data class MonitoringStationResponse(
    @SerializedName("response")
    val response: Response?
)