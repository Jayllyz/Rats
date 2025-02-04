package com.rats.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rats.R
import com.rats.ui.activities.HomeActivity
import com.rats.ui.activities.MyWagonActivity
import com.rats.ui.activities.SettingsActivity

class BottomMenuFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstance: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_menu, container, false)

        val report_layout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_report);
        val map_layout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_map);
        val wagon_layout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_wagon);
        val chat_layout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_chat);
        val more_layout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_more);

        report_layout.setOnClickListener {
            println("cliced on report")
        }

        map_layout.setOnClickListener {
            if (activity !is HomeActivity) {
                val intent = Intent(activity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        wagon_layout.setOnClickListener {
            if (activity !is MyWagonActivity) {
                val intent = Intent(activity, MyWagonActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        chat_layout.setOnClickListener {
            println("clicked chat")
        }

        more_layout.setOnClickListener {
            println("more clicked")
        }

        return view
    }
}