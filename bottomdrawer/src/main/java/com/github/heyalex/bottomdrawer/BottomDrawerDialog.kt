package com.github.heyalex.bottomdrawer

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.concurrent.CopyOnWriteArrayList

open class BottomDrawerDialog(context: Context, @StyleRes theme: Int = R.style.BottomDialogTheme) :
    AppCompatDialog(context, theme) {

    internal var behavior: BottomSheetBehavior<BottomDrawer>? = null
    internal lateinit var drawer: BottomDrawer
    private lateinit var coordinator: CoordinatorLayout
    private val callbacks: CopyOnWriteArrayList<BottomSheetBehavior.BottomSheetCallback> =
        CopyOnWriteArrayList()

    private var offset = 0f
    private var isCancelableOnTouchOutside = true

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.let {
            AlertDialog.Builder(context).create()

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
            override fun onStateChanged(sheet: View, state: Int) {
                callbacks.forEach { callback ->
                    callback.onStateChanged(sheet, state)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                callbacks.forEach { callback ->
                    callback.onSlide(bottomSheet, slideOffset)
                }
            }
        })

        addBottomSheetCallback {
            onSlide { _: View, slideOffset: Float ->
                offset = if (slideOffset != slideOffset) {
                    0f
                } else {
                    slideOffset
                }

                offset++
                updateBackgroundOffset()
                drawer.onSlide(offset / 2f)
            }

            onStateChanged { _: View, newState: Int ->
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        dismiss()
                    }
                }
            }
        }

        coordinator.findViewById<View>(R.id.touch_outside)
            .setOnClickListener {
                if (isCancelableOnTouchOutside) {
                    behavior?.state = BottomSheetBehavior.STATE_HIDDEN
                }
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

    fun addBottomSheetCallback(func: BottomSheetCallback.() -> Unit): BottomSheetBehavior.BottomSheetCallback {
        val listener = BottomSheetCallback()
        listener.func()
        callbacks.add(listener)
        return listener
    }

    fun removeBottomSheetCallback(callback: BottomSheetBehavior.BottomSheetCallback) {
        callbacks.remove(callback)
    }

    override fun onSaveInstanceState(): Bundle? {
        val superState = super.onSaveInstanceState()
        superState.putFloat("offset", offset)
        return superState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        offset = savedInstanceState.getFloat("offset")
        updateBackgroundOffset()
    }

    private fun updateBackgroundOffset() {
        if (offset <= 1) {
            coordinator.background?.alpha = (255 * offset).toInt()
        } else {
            coordinator.background?.alpha = 255
        }
    }

    class BottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {
        private var _onSlide: ((view: View, slideOffset: Float) -> Unit)? = null
        private var _onStateChanged: ((view: View, state: Int) -> Unit)? = null

        override fun onSlide(view: View, slideOffset: Float) {
            _onSlide?.invoke(view, slideOffset)
        }

        fun onSlide(func: (view: View, slideOffset: Float) -> Unit) {
            _onSlide = func
        }

        override fun onStateChanged(view: View, state: Int) {
            _onStateChanged?.invoke(view, state)
        }

        fun onStateChanged(func: (view: View, state: Int) -> Unit) {
            _onStateChanged = func
        }
    }

    companion object {
        inline fun build(context: Context, block: Builder.() -> Unit) =
            Builder(context).apply(block).build()
    }

    class Builder(
        val context: Context
    ) {
        var theme: Int = R.style.BottomDialogTheme
        var isCancelableOnTouchOutside: Boolean = true

        fun build() = BottomDrawerDialog(context, theme).apply {
            this.isCancelableOnTouchOutside = isCancelableOnTouchOutside
        }
    }
}