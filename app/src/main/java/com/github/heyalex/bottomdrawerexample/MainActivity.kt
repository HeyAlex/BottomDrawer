package com.github.heyalex.bottomdrawerexample

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // for drawing behind status bar
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //make system bar to be translucent
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                //make status bar color transparent
                window.statusBarColor = Color.TRANSPARENT

                var flags = it.decorView.systemUiVisibility
                // make dark status bar icons
                flags =
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // make dark navigation bar icons
                    flags =
                        flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
                window.decorView.systemUiVisibility = flags
            }
        }

        show_button.setOnClickListener {
            supportFragmentManager.beginTransaction().add(GoogleTaskExampleDialog(), "test")
                .commit()
        }

        show_button_sample.setOnClickListener {
            supportFragmentManager.beginTransaction().add(PullExampleDialog(), "test1")
                .commit()
        }

        show_button_second_sample.setOnClickListener {
            supportFragmentManager.beginTransaction().add(RotateExampleDialog(), "test1")
                .commit()
        }
    }
}
