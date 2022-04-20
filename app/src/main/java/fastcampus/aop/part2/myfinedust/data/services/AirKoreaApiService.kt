package fastcampus.aop.part2.myfinedust.data.services

import fastcampus.aop.part2.myfinedust.BuildConfig
import fastcampus.aop.part2.myfinedust.data.models.monitoringstation.MonitoringStationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AirKoreaApiService {
    @GET("/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList" +
        "?serviceKey=${BuildConfig.AIRKOREA_SERVICE_KEY}" +
        "&returnType=json")
    suspend fun getNearbyMonitoringStation(
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ): Response<MonitoringStationResponse>
}