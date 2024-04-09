package com.example.transitapp.ui.notifications

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.transitapp.databinding.FragmentNotificationsBinding
import com.google.transit.realtime.GtfsRealtime
import java.net.URL

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val url = URL("https://gtfs.halifax.ca/realtime/Alert/Alerts.pb")
        val feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream())

        for (entity in feed.entityList) {
            val text = entity.alert.headerText.getTranslation(0).text.toString()

            val textView = TextView(context).apply {
                this.text = text
                textSize = 26f
                setTextColor(Color.BLACK)
                setPadding(20, 10, 20, 10)

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 30
                }
                this.layoutParams = layoutParams
            }




            binding.linearLayoutAlerts.addView(textView)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}