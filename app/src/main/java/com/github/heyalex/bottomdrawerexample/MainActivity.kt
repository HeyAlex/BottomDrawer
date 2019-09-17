package com.github.heyalex.bottomdrawerexample

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.github.heyalex.bottomdrawer.BottomDrawer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    internal var behavior: BottomSheetBehavior<BottomDrawer>? = null
    internal lateinit var drawer: BottomDrawer
    private lateinit var coordinator: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main_test)
        coordinator = findViewById(R.id.bottom_sheet_coordinator)
        drawer = coordinator.findViewById(R.id.bottom_sheet_drawer) as BottomDrawer
        behavior = BottomSheetBehavior.from(drawer)

//        drawer.addView(wrappedView)
//        drawer.addHandleView(handleView)

        window?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                it.statusBarColor = Color.TRANSPARENT

                var flags = it.decorView.systemUiVisibility
                flags =
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags =
                        flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
                it.decorView.systemUiVisibility = flags
            }
        }

        show_button.setOnClickListener {
            supportFragmentManager?.beginTransaction()?.add(GoogleTaskExampleDialog(), "test")
                ?.commit()
        }

        show_button_sample.setOnClickListener {
            //            supportFragmentManager?.beginTransaction()?.add(ExampleDialog(), "test1")
//                ?.commit()
            behavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        }
    }
}
