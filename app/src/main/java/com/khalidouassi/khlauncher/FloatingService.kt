package com.khalidouassi.khlauncher

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import kotlin.math.abs

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager

    private lateinit var bubble: TextView

    private var popup: PopupWindow? = null

    private val gold = Color.rgb(212, 175, 55)

    private val dark = Color.rgb(18, 18, 18)

    override fun onCreate() {

        super.onCreate()

        createNotification()

        windowManager =
            getSystemService(WINDOW_SERVICE) as WindowManager

        createBubble()
    }

    private fun createNotification() {

        val channelId = "KH_LAUNCHER"

        val manager =
            getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "KH Launcher",
                NotificationManager.IMPORTANCE_LOW
            )

            manager.createNotificationChannel(channel)
        }

        val notification =
            Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_kh)
                .setContentTitle("KH Launcher")
                .setContentText("KH يعمل في الخلفية")
                .setOngoing(true)
                .build()

        startForeground(100, notification)
    }

    private fun createBubble() {

        bubble = TextView(this)

        bubble.text = "KH"

        bubble.textSize = 18f

        bubble.gravity = Gravity.CENTER

        bubble.setTextColor(Color.BLACK)

        bubble.background = createCircle()

        bubble.elevation = 15f

        val size = 64

        val params = WindowManager.LayoutParams(

            size,
            size,

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,

            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

            PixelFormat.TRANSLUCENT
        )

        params.gravity =
            Gravity.TOP or Gravity.START

        params.x = 25

        params.y = 250

        var startX = 0

        var startY = 0

        var downX = 0f

        var downY = 0f

        var moved = false

        bubble.setOnTouchListener { _, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    downX = event.rawX

                    downY = event.rawY

                    startX = params.x

                    startY = params.y

                    moved = false

                    true
                }

                MotionEvent.ACTION_MOVE -> {

                    val dx =
                        (event.rawX - downX).toInt()

                    val dy =
                        (event.rawY - downY).toInt()

                    if (abs(dx) > 8 || abs(dy) > 8) {

                        moved = true
                    }

                    params.x = startX + dx

                    params.y = startY + dy

                    windowManager.updateViewLayout(
                        bubble,
                        params
                    )

                    true
                }

                MotionEvent.ACTION_UP -> {

                    if (!moved) {

                        showApps()
                    }

                    true
                }

                else -> false
            }
        }

        windowManager.addView(
            bubble,
            params
        )
    }

    private fun showApps() {

        if (popup?.isShowing == true) {

            return
        }

        val container = LinearLayout(this)

        container.orientation =
            LinearLayout.VERTICAL

        container.setPadding(
            20,
            20,
            20,
            20
        )

        container.background =
            createRoundedBackground()

        val title = TextView(this)

        title.text = "KH  •  تطبيقاتي"

        title.textSize = 20f

        title.setTextColor(gold)

        title.setPadding(
            10,
            5,
            10,
            20
        )

        container.addView(title)

        val applications =
            packageManager
                .getInstalledApplications(0)
                .filter {
                    it.packageName != packageName
                }
                .sortedBy {
                    packageManager
                        .getApplicationLabel(it)
                        .toString()
                        .lowercase()
                }
                .take(40)

        for (application in applications) {

            val name =
                packageManager
                    .getApplicationLabel(application)
                    .toString()

            val item = TextView(this)

            item.text = name

            item.textSize = 16f

            item.setTextColor(Color.WHITE)

            item.setPadding(
                15,
                18,
                15,
                18
            )

            item.setOnClickListener {

                val launchIntent =
                    packageManager
                        .getLaunchIntentForPackage(
                            application.packageName
                        )

                if (launchIntent != null) {

                    launchIntent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )

                    startActivity(launchIntent)
                }

                popup?.dismiss()
            }

            container.addView(item)
        }

        val scrollView = ScrollView(this)

        scrollView.addView(container)

        popup = PopupWindow(
            scrollView,
            320,
            550,
            true
        )

        popup?.isOutsideTouchable = true

        popup?.elevation = 20f

        popup?.setBackgroundDrawable(
            createRoundedBackground()
        )

        popup?.showAtLocation(
            bubble,
            Gravity.CENTER,
            0,
            0
        )
    }

    private fun createCircle():
            GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.OVAL

            setColor(gold)
        }
    }

    private fun createRoundedBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(dark)

            cornerRadius = 25f

            setStroke(
                2,
                gold
            )
        }
    }

    override fun onDestroy() {

        if (::bubble.isInitialized) {

            windowManager.removeView(bubble)
        }

        popup?.dismiss()

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }
}
