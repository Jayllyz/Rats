package com.rats.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rats.R
import com.rats.ui.HomeActivity
import com.rats.ui.MyWagonActivity

class BottomNavigationFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstance: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false)

        val mapTextView: TextView = view.findViewById<TextView>(R.id.tv_map)
        val wagonTextView: TextView = view.findViewById<TextView>(R.id.tv_wagon)

        mapTextView.setOnClickListener{
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
        }

        wagonTextView.setOnClickListener{
            val intent = Intent(activity, MyWagonActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}