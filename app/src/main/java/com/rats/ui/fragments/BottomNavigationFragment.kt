package com.rats.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rats.R
import com.rats.ui.activities.HomeActivity
import com.rats.ui.activities.MyWagonActivity
import com.rats.ui.activities.SettingsActivity

class BottomNavigationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstance: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false)

        val mapTextView: TextView = view.findViewById<TextView>(R.id.tv_map)
        val wagonTextView: TextView = view.findViewById<TextView>(R.id.tv_wagon)
        val parametersView: TextView = view.findViewById<TextView>(R.id.tv_settings)

        mapTextView.setOnClickListener {
            if (activity !is HomeActivity) {
                val intent = Intent(activity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        wagonTextView.setOnClickListener {
            if (activity !is MyWagonActivity) {
                val intent = Intent(activity, MyWagonActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        parametersView.setOnClickListener {
            if (activity !is SettingsActivity) {
                val intent = Intent(activity, SettingsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        return view
    }
}
