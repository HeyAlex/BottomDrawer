package com.github.heyalex.bottomdrawerexample

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.statusBarColor = Color.WHITE
            }
        }

        setContentView(R.layout.activity_main)
        show_button.setOnClickListener {
            supportFragmentManager?.beginTransaction()?.add(GoogleTaskExampleDialog(), "test")
                ?.commit()
        }

        show_button_sample.setOnClickListener {
            supportFragmentManager?.beginTransaction()?.add(CustomExampleDialog(), "test1")
                ?.commit()
        }
    }
}
