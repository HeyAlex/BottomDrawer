package com.github.heyalex.bottomdrawerexample

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.github.heyalex.handle.PlainHandleView

class ExampleDialog : BottomDrawerFragment() {
    private lateinit var cornerRadiusSeekBar: AppCompatSeekBar
    private lateinit var extraPaddingSeekBar: AppCompatSeekBar

    private lateinit var navigation: AppCompatCheckBox
    private lateinit var statusBar: AppCompatCheckBox

    private lateinit var drawUnderHandle: AppCompatCheckBox
    private lateinit var drawUnderStatus: AppCompatCheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.example_layout, container, false)
        cornerRadiusSeekBar = view.findViewById(R.id.corner_radius_seek_bar)
        cornerRadiusSeekBar.max = 80
        cornerRadiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                changeCornerRadius(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        extraPaddingSeekBar = view.findViewById(R.id.extra_padding_seek_bar)
        extraPaddingSeekBar.max = 180
        extraPaddingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                changeExtraPadding(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        navigation = view.findViewById(R.id.navigation_bar_accent)
        navigation.setOnCheckedChangeListener { _, isChecked ->
            changeNavigationIconColor(isChecked)
        }
        statusBar = view.findViewById(R.id.status_bar_accent)
        statusBar.setOnCheckedChangeListener { _, isChecked ->
            changeStatusBarIconColor(isChecked)
        }

        drawUnderStatus = view.findViewById(R.id.should_draw_under_status_bar)
        drawUnderStatus.setOnCheckedChangeListener { _, isChecked ->
            shouldDrawUnderStatusBar(isChecked)
        }

        drawUnderHandle = view.findViewById(R.id.should_draw_under_handle_view)
        drawUnderHandle.setOnCheckedChangeListener { _, isChecked ->
            shouldDrawUnderHandleView(isChecked)
        }

        return view
    }

    override fun prepareBottomDrawerDialog(): BottomDrawerDialog {
        return BottomDrawerDialog.build(context!!) {
            theme = R.style.Plain
            handleView = PlainHandleView(context).apply {
                val widthHandle =
                    resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_width)
                val heightHandle =
                    resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_height)
                val params =
                    FrameLayout.LayoutParams(widthHandle, heightHandle, Gravity.CENTER_HORIZONTAL)

                params.topMargin =
                    resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_top_margin)

                layoutParams = params
            }
        }
    }

    private fun changeNavigationIconColor(isLight: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog?.window?.let {
                var flags = it.decorView.systemUiVisibility
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags = if (isLight) {
                        flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    } else {
                        flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    }
                }
                it.decorView.systemUiVisibility = flags
            }
        }
    }

    private fun changeStatusBarIconColor(isLight: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog?.window?.let {
                var flags = it.decorView.systemUiVisibility
                flags = if(isLight) {
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                it.decorView.systemUiVisibility = flags
            }
        }
    }
}
