package com.example.transitapp.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.transitapp.R
import com.example.transitapp.databinding.AnnotationBinding
import com.example.transitapp.databinding.FragmentHomeBinding
import com.google.transit.realtime.GtfsRealtime.FeedMessage
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import java.net.URL


class HomeFragment : Fragment() {

    private var mapView: MapView? = null
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0


    //////////////////////////////DESTORY

    private lateinit var mapboxMap: MapboxMap
    private lateinit var viewAnnotationManager: ViewAnnotationManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        arguments?.let { bundle ->
            longitude = bundle.getDouble("longitude", longitude)
            latitude = bundle.getDouble("latitude", latitude)
        }

        // MapView setup
        mapView = root.findViewById(R.id.mapView)
        // mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

        ////////////////////////////////////////DESTROY - Maybe remove to get buses to show?
        viewAnnotationManager = mapView!!.viewAnnotationManager

        mapboxMap = binding.mapView.getMapboxMap().apply {
            loadStyleUri(Style.TRAFFIC_DAY) {
                // Get the center point of the map
                val userLocation = Point.fromLngLat(longitude, latitude)
                val cameraOptions = CameraOptions.Builder().center(userLocation).zoom(14.0).build()
                mapboxMap.setCamera(cameraOptions)
            } //Style.MAPBOX_STREETS
        }

        val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")
        val feed = FeedMessage.parseFrom(url.openStream())
        for (entity in feed.entityList) {
            val routeId = entity.vehicle.trip.routeId.toString()
            val latitudetest = entity.vehicle.position.latitude.toString()
            val longitudetest = entity.vehicle.position.longitude.toString()

            val latitude = entity.vehicle.position.latitude.toDouble()
            val longitude = entity.vehicle.position.longitude.toDouble()

            // Log the information
            Log.v(
                "FINDME2",
                "Route ID: $routeId, Latitude: $latitudetest, Longitude: $longitudetest"
            )
            val textView: TextView = binding.textHome
            textView.text = " "

            val point = Point.fromLngLat(entity.vehicle.position.longitude.toDouble(),
                (entity.vehicle.position.latitude.toDouble()))

            addViewAnnotation(point, routeId)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
        _binding = null
    }
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    ///////////////////////////////DESTORY
    private fun addViewAnnotation(point: Point, routeId: String) {
        // Define the view annotation
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            // Specify the layout resource id
            resId = R.layout.annotation,
            // Set any view annotation options
            options = viewAnnotationOptions {
                geometry(point)
            }
        )


        var routeExists = false

        try {
            context?.openFileInput("savedRoutes")?.bufferedReader()?.useLines { lines ->
                routeExists = lines.any { line -> line.trim() == routeId }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val textView = viewAnnotation.findViewById<TextView>(R.id.annotation)
        textView.text = routeId

        if (routeExists) {

            textView.setBackgroundColor(Color.BLACK)
            textView.setTextColor(Color.WHITE)
        }
        else {

            textView.setBackgroundColor(Color.BLUE)
            textView.setTextColor(Color.WHITE)
        }

        AnnotationBinding.bind(viewAnnotation)


    }
}