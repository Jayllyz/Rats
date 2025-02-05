package com.rats.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.rats.MoreMenuActivity
import com.rats.R
import com.rats.ui.activities.HomeActivity
import com.rats.ui.activities.MyWagonActivity

class BottomMenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstance: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_menu, container, false)

        val reportLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_report)
        val mapLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_map)
        val wagonLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_wagon)
        val chatLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_chat)
        val moreLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_more)

        reportLayout.setOnClickListener {
            println("cliced on report")
        }

        mapLayout.setOnClickListener {
            if (activity !is HomeActivity) {
                val intent = Intent(activity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        wagonLayout.setOnClickListener {
            if (activity !is MyWagonActivity) {
                val intent = Intent(activity, MyWagonActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        chatLayout.setOnClickListener {
            println("clicked chat")
        }

        moreLayout.setOnClickListener {
            if (activity !is MoreMenuActivity) {
                val intent = Intent(activity, MoreMenuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        return view
    }
}
