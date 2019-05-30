package com.github.heyalex.bottomdrawer

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.AccessibilityDelegateCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.support.v7.app.AppCompatDialog
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout

open class BottomDrawerDialog(context: Context, @StyleRes theme: Int = R.style.BottomDialogTheme) :
    AppCompatDialog(context, theme) {

    private var behavior: BottomSheetBehavior<BottomDrawer>? = null
    private lateinit var drawer: BottomDrawer
    private lateinit var coordinator: CoordinatorLayout

    private var offset = 0f

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

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
                it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                it.statusBarColor = Color.TRANSPARENT

                var flags = it.decorView.systemUiVisibility
                flags =
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                it.decorView.systemUiVisibility = flags
            }
        }
    }

    override fun setContentView(@LayoutRes layoutResId: Int) {
        super.setContentView(wrapInBottomSheet(layoutResId, null, null))
    }

    override fun setContentView(view: View) {
        super.setContentView(wrapInBottomSheet(0, view, null))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(wrapInBottomSheet(0, view, params))
    }

    private fun wrapInBottomSheet(
        layoutResId: Int,
        view: View?,
        params: ViewGroup.LayoutParams?
    ): View {
        var wrappedView = view
        val container = View.inflate(context, R.layout.bottom_drawer_layout, null) as FrameLayout
        coordinator = container.findViewById(R.id.bottom_sheet_coordinator)
        if (layoutResId != 0 && wrappedView == null) {
            wrappedView = layoutInflater.inflate(layoutResId, coordinator, false)
        }
        drawer = coordinator.findViewById<View>(R.id.bottom_sheet_drawer) as BottomDrawer
        behavior = BottomSheetBehavior.from(drawer)
        behavior?.state = BottomSheetBehavior.STATE_HIDDEN
        val metrics = context.resources.displayMetrics
        behavior?.peekHeight = metrics.heightPixels / 2
        behavior?.isHideable = true

        if (params == null) {
            drawer.addView(wrappedView)
        } else {
            drawer.addView(wrappedView, params)
        }

        coordinator.background.alpha = offset.toInt()
        behavior?.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                offset = if (slideOffset != slideOffset) {
                    0f
                } else {
                    slideOffset
                }

                offset++
                if (offset <= 1) {
                    coordinator.background?.alpha = (255 * offset).toInt()
                } else {
                    coordinator.background?.alpha = 255
                }
                drawer.onSlide(offset / 2f)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        dismiss()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })

        coordinator.findViewById<View>(R.id.touch_outside)
            .setOnClickListener {
                behavior?.state = BottomSheetBehavior.STATE_HIDDEN
            }

        // Handle accessibility events
        ViewCompat.setAccessibilityDelegate(
            drawer,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View, info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.isDismissable = true
                }

                override fun performAccessibilityAction(
                    host: View,
                    action: Int,
                    args: Bundle
                ): Boolean {
                    if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS) {
                        cancel()
                        return true
                    }
                    return super.performAccessibilityAction(host, action, args)
                }
            })
        drawer.setOnTouchListener { view, event ->
            // Consume the event and prevent it from falling through
            true
        }
        return container
    }

    override fun onStart() {
        super.onStart()
        Handler(Looper.getMainLooper()).postDelayed({
            behavior?.let {
                if (window != null && it.state == BottomSheetBehavior.STATE_HIDDEN) {
                    it.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }
        }, 50)
    }

    override fun onBackPressed() {
        behavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }
}