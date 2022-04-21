package fastcampus.aop.part2.myfinedust

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import fastcampus.aop.part2.myfinedust.data.Repository
import fastcampus.aop.part2.myfinedust.data.models.airquality.Grade
import fastcampus.aop.part2.myfinedust.data.models.airquality.MeasuredValue
import fastcampus.aop.part2.myfinedust.data.models.monitoringstation.MonitoringStation
import fastcampus.aop.part2.myfinedust.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var cancellationTokenSource: CancellationTokenSource? = null

    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVariables()
        requestLocationPermissions()
    }

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_ACCESS_LOCATION_PERMISSIONS
        )
    }

    @SuppressLint("MissingPermission")
    private fun fetchFineDustData() {
        cancellationTokenSource = CancellationTokenSource()

        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource!!.token
        ).addOnSuccessListener { location ->
            scope.launch {
                val monitoringStation =
                    Repository.getNearbyMonitoringStation(location.latitude, location.longitude)

                val measuredValue =
                    Repository.getLatestAirQualityData(monitoringStation!!.stationName!!)

                displayAirQualityData(monitoringStation, measuredValue!!)
            }

        }
    }

    @SuppressLint("SetTextI18n")
    fun displayAirQualityData(monitoringStation: MonitoringStation, measuredValue: MeasuredValue) {
        binding.apply {
            binding.measuringStationNameTextView.text = monitoringStation.stationName
            binding.measuringStationAddressTextView.text = "측정소 위치: ${monitoringStation.addr}"

            (measuredValue.khaiGrade ?: Grade.UNKOWN).let { grade ->
                root.setBackgroundResource(grade.colorResId)
                totalGradeLabelTextView.text = grade.label
                totalGradleEmojiTextView.text = grade.emoji
            }

            with(measuredValue) {
                findDustInfoTextView.text =
                    "미세먼지: $pm10Value ㎍/㎥ ${(pm10Grade ?: Grade.UNKOWN).emoji}"
                binding.ultraFindDustInfoTextView.text =
                    "초미세먼지: $pm25Value ㎍/㎥ ${(pm25Grade ?: Grade.UNKOWN).emoji}"

                with(binding.so2Item) {
                    labelTextView.text = "아황산가스"
                    gradeTextView.text = (so2Grade ?: Grade.UNKOWN).toString()
                    valueTextView.text = "$so2Value ppm"
                }

                with(binding.coItem) {
                    labelTextView.text = "일산화탄소"
                    gradeTextView.text = (coGrade ?: Grade.UNKOWN).toString()
                    valueTextView.text = "$coValue ppm"
                }

                with(binding.o3Item) {
                    labelTextView.text = "오존"
                    gradeTextView.text = (o3Grade ?: Grade.UNKOWN).toString()
                    valueTextView.text = "$o3Value ppm"
                }

                with(binding.no2Item) {
                    labelTextView.text = "이산화질소"
                    gradeTextView.text = (no2Grade ?: Grade.UNKOWN).toString()
                    valueTextView.text = "$no2Value ppm"
                }

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val locationPermissionGranted =
            requestCode == REQUEST_ACCESS_LOCATION_PERMISSIONS &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        if (!locationPermissionGranted) {
            finish()
        } else {
            fetchFineDustData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource?.cancel()
        scope.cancel()
    }

    companion object {
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 100
    }
}