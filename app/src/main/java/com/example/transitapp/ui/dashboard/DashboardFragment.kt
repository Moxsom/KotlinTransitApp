package com.example.transitapp.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.transitapp.R
import com.example.transitapp.databinding.FragmentDashboardBinding
import com.google.transit.realtime.GtfsRealtime
import java.net.URL

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textDashboard
        //textView.text = "This is the Dashboard Fragment"



        //DESTROY IF NO WORKY
        val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")
        val feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream())

        val routeIds = mutableListOf<String>()

        for (entity in feed.entityList) {
            val routeId = entity.vehicle.trip.routeId.toString()
            routeIds.add(routeId)
        }


        //Get The AutoCompleteTextView
        val autoCompleteTextView: AutoCompleteTextView = binding.autoCompleteTextView

        // Get bus route data from Strings
        val busRoutesArray = resources.getStringArray(R.array.bus_routes_array)

        //create ArrayAdapter and set it to AutoCompleteTextView
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, busRoutesArray)
        autoCompleteTextView.setAdapter(adapter)


       ///////
        try {
            // Check if the file exists
            val fileExists = context?.fileList()?.contains("savedRoutes") == true

            val routeList = mutableListOf<String>()

            if (fileExists) {
                // Read the file if it exists
                context?.openFileInput("savedRoutes")?.bufferedReader()?.useLines { lines ->
                    routeList.addAll(lines.filter { it.isNotBlank() })

                    for (route in routeList) {
                        val textView = TextView(context).apply {
                            this.text = route
                            textSize = 26f
                            textAlignment = View.TEXT_ALIGNMENT_CENTER
                            setTextColor(Color.BLACK)
                            setPadding(20, 10, 20, 10)

                            val layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                bottomMargin = 30
                            }
                            this.layoutParams = layoutParams
                        }

                        //val drawable = context?.let { ContextCompat.getDrawable(it, com.example.transitapp.R.drawable.rounded_corners) }
                        //textView.background = drawable

                        binding.linearLayoutRoutes.addView(textView)

                        textView.setOnClickListener {
                            context?.let { ctx ->
                                val updatedRoutes = routeList.filter { it != route }

                                ctx.openFileOutput("savedRoutes", Context.MODE_PRIVATE).use { outputStream ->
                                    updatedRoutes.forEach { line ->
                                        outputStream.write((line + System.lineSeparator()).toByteArray())
                                    }
                                }

                                // Remove the route from routeList and update the UI
                                routeList.remove(route)
                                binding.linearLayoutRoutes.removeView(textView)
                            }
                        }
                    }
                }
            }

            val button: Button = binding.buttonAdd
            button.setOnClickListener {

                var routeExists = false

                val fileExists = context?.fileList()?.contains("savedRoutes") == true

                if (fileExists) {
                    context?.openFileInput("savedRoutes")?.bufferedReader()?.useLines { lines ->
                        routeExists =
                            lines.any { line -> line.trim() == autoCompleteTextView.text.toString() }
                    }
                }

                // Check if there is text in the search box
                if (autoCompleteTextView.text.toString().isEmpty()
                    || autoCompleteTextView.text.toString() !in routeIds) {
                    autoCompleteTextView.error = "Not a route!"
                }
                else if (routeExists) {
                    autoCompleteTextView.error = "Route already saved!"
                }
                else {
                    val filename = "savedRoutes"
                    val fileContents = autoCompleteTextView.text.toString()

                    context?.openFileOutput(filename, Context.MODE_APPEND).use {
                        it?.apply {
                            write("\n".toByteArray())
                            write(fileContents.toByteArray())
                        }
                    }

                    val route = autoCompleteTextView.text.toString()

                    val textView = TextView(context).apply {
                        this.text = route
                        textSize = 26f
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        setTextColor(Color.BLACK)
                        setPadding(20, 10, 20, 10)

                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            bottomMargin = 30
                        }
                        this.layoutParams = layoutParams
                    }

                    //val drawable = context?.let { ContextCompat.getDrawable(it, com.example.transitapp.R.drawable.rounded_corners) }
                    //textView.background = drawable

                    binding.linearLayoutRoutes.addView(textView)

                    textView.setOnClickListener {
                        context?.let { ctx ->
                            val updatedRoutes = routeList.filter { it != route }

                            ctx.openFileOutput("savedRoutes", Context.MODE_PRIVATE).use { outputStream ->
                                updatedRoutes.forEach { line ->
                                    outputStream.write((line + System.lineSeparator()).toByteArray())
                                }
                            }

                            // Remove the route from routeList and update the UI
                            routeList.remove(route)
                            binding.linearLayoutRoutes.removeView(textView)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ///////////

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}