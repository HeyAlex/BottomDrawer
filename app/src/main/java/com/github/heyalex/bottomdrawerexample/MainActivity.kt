package com.github.heyalex.bottomdrawerexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        show_button.setOnClickListener {
            supportFragmentManager?.beginTransaction()
                ?.add(GoogleTaskExampleDialog(), "test")?.commit()
        }

    }
}
