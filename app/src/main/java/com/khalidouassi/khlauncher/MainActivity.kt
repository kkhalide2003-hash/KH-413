package com.khalidouassi.khlauncher

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val gold = Color.rgb(212, 175, 55)
    private val background = Color.rgb(11, 11, 11)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showIntro()
    }

    private fun showIntro() {

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL

        root.gravity = Gravity.CENTER

        root.setPadding(32, 32, 32, 32)

        root.setBackgroundColor(background)

        val logo = TextView(this)

        logo.text = "KH"

        logo.textSize = 56f

        logo.gravity = Gravity.CENTER

        logo.setTextColor(gold)

        val creator = TextView(this)

        creator.text = "صنع من قبل Khalid Ouassi"

        creator.textSize = 20f

        creator.gravity = Gravity.CENTER

        creator.setTextColor(Color.WHITE)

        val description = TextView(this)

        description.text =
            "فقاعة عائمة تمنحك وصولاً سريعاً إلى تطبيقاتك من أي مكان."

        description.textSize = 16f

        description.gravity = Gravity.CENTER

        description.setTextColor(Color.LTGRAY)

        description.setPadding(0, 25, 0, 30)

        val button = Button(this)

        button.text = "تشغيل KH"

        button.setTextColor(Color.BLACK)

        button.setBackgroundColor(gold)

        button.setOnClickListener {

            startLauncher()

        }

        root.addView(
            logo,
            layoutParams()
        )

        root.addView(
            creator,
            layoutParams()
        )

        root.addView(
            description,
            layoutParams()
        )

        root.addView(
            button,
            layoutParams()
        )

        setContentView(root)
    }

    private fun layoutParams(): LinearLayout.LayoutParams {

        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {

            bottomMargin = 12

        }
    }

    private fun startLauncher() {

        if (!Settings.canDrawOverlays(this)) {

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )

            startActivity(intent)

            return
        }

        val serviceIntent =
            Intent(this, FloatingService::class.java)

        startService(serviceIntent)

        finish()
    }
}
