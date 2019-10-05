package com.github.heyalex.bottomdrawerexample

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSeekBar
import com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.github.heyalex.handle.PullHandleView
import com.github.heyalex.utils.changeNavigationIconColor
import com.github.heyalex.utils.changeStatusBarIconColor
import com.rtugeek.android.colorseekbar.ColorSeekBar

class CustomExampleDialog : BottomDrawerFragment() {

    private var alphaCancelButton = 0f
    private lateinit var cancelButton: ImageView

    private lateinit var cornerRadiusSeekBar: AppCompatSeekBar

    private lateinit var navigation: AppCompatCheckBox
    private lateinit var statusBar: AppCompatCheckBox
    private lateinit var colorSeekBar: ColorSeekBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.google_task_example_layout, container, false)
        cancelButton = view.findViewById(R.id.cancel)
        addBottomSheetCallback {
            onSlide { _, slideOffset ->
                alphaCancelButton = (slideOffset - PERCENT) * (1f / (1f - PERCENT))
                cancelButton.alpha = alphaCancelButton
                cancelButton.isEnabled = alphaCancelButton > 0
            }
        }
        cancelButton.setOnClickListener { dismissWithBehavior() }

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
        colorSeekBar.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(colorBarPosition: Int, alphaBarPosition: Int, color: Int) {
                changeBackgroundColor(color)
            }
        })

        return view
    }

    override fun prepareBottomDrawerDialog(): BottomDrawerDialog {
        return BottomDrawerDialog.build(context!!) {
            theme = R.style.Pull
            handleView = PullHandleView(context).apply {
                val widthHandle =
                    resources.getDimensionPixelSize(R.dimen.sample_bottom_sheet_handle_width)
                val heightHandle =
                    resources.getDimensionPixelSize(R.dimen.sample_bottom_sheet_handle_height)
                val params =
                    FrameLayout.LayoutParams(widthHandle, heightHandle, Gravity.CENTER_HORIZONTAL)

                params.topMargin =
                    resources.getDimensionPixelSize(R.dimen.sample_bottom_sheet_handle_top_margin)

                layoutParams = params
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat("alphaCancelButton", alphaCancelButton)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        alphaCancelButton = savedInstanceState?.getFloat("alphaCancelButton") ?: 0f
    }

    companion object {
        const val PERCENT = 0.65f
    }
}
