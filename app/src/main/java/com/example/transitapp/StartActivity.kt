package com.example.transitapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.OnSuccessListener

class StartActivity : AppCompatActivity() {
    //part of the package
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private val REQUEST_CODE = 100 //for permission request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //
        // Setup Location services
        // Ask permission and get the location
        //
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this) //get the location
        getLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        ) //call the super class, valid override.

        //Check to see if this is the location permission 100
        if (requestCode == REQUEST_CODE) {
            //Check to see if permission granted or denied.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //get the location
                getLocation()
            }
            //permission denied.
        }
    }


    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient?.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
                ?.addOnSuccessListener(object : OnSuccessListener<Location?> {
                    override fun onSuccess(location: Location?) {
                        if (location != null) {
                            //add intent to redirect to MainActivity
                            val intent = Intent(this@StartActivity, MainActivity::class.java)
                            intent.putExtra("latitude", location.latitude)
                            intent.putExtra("longitude", location.longitude)
                            startActivity(intent)
                        }
                    }
                })
        } else {
            //ask for permission
            askPermission()
        }
    }

    private fun askPermission() {
        //ask for permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }
}