package com.example.weatherapp.network.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import com.example.weatherapp.data.CurrentLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class WeatherDataRepository {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation: CurrentLocation) -> Unit,
        onFailure: () -> Unit
    ) {
        // Create a CancellationTokenSource for handling location request cancellation (optional)
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
            .addOnSuccessListener { location ->
                if (location == null) {
                    onFailure()  // If location is null, handle the failure
                } else {
                    // Create and pass the CurrentLocation object on success
                    onSuccess(
                        CurrentLocation(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                }
            }
            .addOnFailureListener {
                onFailure()  // Handle any failure that occurs during the location request
            }
    }

    @Suppress("DEPRECATION")
    fun updateAddressText(
        currentLocation: CurrentLocation,
        geocoder: Geocoder
    ): CurrentLocation {
        val latitude = currentLocation.latitude ?: return currentLocation
        val longitude = currentLocation.longitude ?: return currentLocation

        return try {
            geocoder.getFromLocation(latitude, longitude, 1)?.let { addresses ->
                val address = addresses[0]
                val addressText = StringBuilder()

                address.locality?.let { addressText.append(it).append(", ") }
                address.adminArea?.let { addressText.append(it).append(", ") }
                address.countryName?.let { addressText.append(it) }

                currentLocation.copy(location = addressText.toString())
            } ?: currentLocation
        } catch (e: Exception) {
            e.printStackTrace()
            currentLocation
        }
    }
}