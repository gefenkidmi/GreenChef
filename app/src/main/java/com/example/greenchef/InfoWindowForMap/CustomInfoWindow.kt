package com.example.greenchef.InfoWindowsForMap

import android.widget.LinearLayout
import android.widget.TextView
import com.example.greenchef.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomInfoWindow(
    mapView: MapView,
    private val title: String,
    private val snippet: String
) : InfoWindow(R.layout.custom_info_window, mapView) {

    override fun onOpen(item: Any?) {
        // Customization of the info window content
        val layout = mView.findViewById<LinearLayout>(R.id.custom_info_window_layout)
        val titleTextView = mView.findViewById<TextView>(R.id.custom_info_window_title)
        val snippetTextView = mView.findViewById<TextView>(R.id.custom_info_window_text)

        titleTextView.text = title
        snippetTextView.text = snippet

        // Set an OnClickListener on the layout to close the info window when clicked
        layout.setOnClickListener {
            close()
        }

        // Adjust the size of the info window layout if needed
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onClose() {
        // Perform any cleanup or actions when the info window is closed
    }
}