package com.github.heyalex.bottomdrawerexample

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
import com.github.heyalex.bottomdrawerexample.handle.RotateHandleView
import com.github.heyalex.utils.changeNavigationIconColor
import com.github.heyalex.utils.changeStatusBarIconColor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rtugeek.android.colorseekbar.ColorSeekBar

class RotateExampleDialog : BottomDrawerFragment() {

    private lateinit var cornerRadiusSeekBar: AppCompatSeekBar

    private lateinit var navigation: AppCompatCheckBox
    private lateinit var statusBar: AppCompatCheckBox
    private lateinit var colorSeekBar: ColorSeekBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.rotate_task_example_layout, container, false)
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

        navigation = view.findViewById(R.id.navigation_bar_accent)
        navigation.setOnCheckedChangeListener { _, isChecked ->
            changeNavigationIconColor(isChecked)
        }
        statusBar = view.findViewById(R.id.status_bar_accent)
        statusBar.setOnCheckedChangeListener { _, isChecked ->
            changeStatusBarIconColor(isChecked)
        }

        colorSeekBar = view.findViewById(R.id.colorSlider)
        colorSeekBar.setOnColorChangeListener { _, _, color ->
            if (!colorSeekBar.isFirstDraw) {
                changeBackgroundColor(color)
            }
        }

        return view
    }

    override fun prepareBottomDrawerDialog(): BottomDrawerDialog {
        return BottomDrawerDialog.build(context!!) {
            theme = R.style.Rotate
            handleView = RotateHandleView(context).apply {
                val widthHandle =
                    resources.getDimensionPixelSize(R.dimen.rotate_sample_bottom_sheet_handle_width)
                val heightHandle =
                    resources.getDimensionPixelSize(R.dimen.rotate_sample_bottom_sheet_handle_height)
                val params =
                    FrameLayout.LayoutParams(widthHandle, heightHandle, Gravity.END)

                params.topMargin =
                    resources.getDimensionPixelSize(R.dimen.rotate_sample_bottom_sheet_handle_margin)

                params.rightMargin =
                    resources.getDimensionPixelSize(R.dimen.rotate_sample_bottom_sheet_handle_margin)

                layoutParams = params
                background =
                    ContextCompat.getDrawable(context, R.drawable.ic_expand_less_black_24dp)

                setOnClickListener {
                    when (getCurrentState()) {
                        BottomSheetBehavior.STATE_EXPANDED -> dismissWithBehavior()
                        BottomSheetBehavior.STATE_COLLAPSED,
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> expandWithBehaivor()

                    }
                }
            }
        }
    }
}
