package com.github.heyalex.bottomdrawer

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

class BottomDrawer : FrameLayout {

    private var container: FrameLayout
    private val rect: Rect = Rect()

    private val backgroundDrawable =
        ContextCompat.getDrawable(context, R.drawable.bottom_drawer_corner_bg)
    private val cornerRadiusDrawable = GradientDrawable()
    private val cornerArray: FloatArray =
        floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
    private var drawerBackground: Int
    private var cornerRadius: Float
    private var extraPadding: Int = 0
    private var currentCornerRadius: Float = 0f
    private var defaultCorner = false
    private var diffWithStatusBar: Int = 0
    private var translationView: Float = 0f

    private var shouldDrawUnderStatus = false
    private var shouldDrawUnderHandle = false

    private var translationUpdater: TranslationUpdater? = null
    private var handleView: View? = null

    private var isEnoughToFullExpand: Boolean = false
    private var isEnoughToCollapseExpand: Boolean = false

    private val fullHeight: Int
    private val collapseHeight: Int

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr) {

        initAttributes(context, attrs)
        setWillNotDraw(false)
        drawerBackground = ContextCompat.getColor(context, R.color.bottom_drawer_background)
        cornerRadius = resources.getDimensionPixelSize(R.dimen.bottom_sheet_corner_radius).toFloat()
        cornerRadiusDrawable.setColor(drawerBackground)

        calculateDiffStatusBar(0)

        val heightPixels = context.resources.displayMetrics.heightPixels
        fullHeight = heightPixels
        collapseHeight = heightPixels / 2

        container = FrameLayout(context).apply {
            val params =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.topMargin =
                resources.getDimensionPixelSize(R.dimen.default_bottom_sheet_top_container_margin)

            layoutParams = params
        }
        super.addView(container)
        container.clipToPadding = false
        onSlide(0f)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        var attr: TypedArray? = null
        try {
            attr = context.obtainStyledAttributes(attrs, R.styleable.BottomDrawer, 0, 0)
            extraPadding = attr!!.getDimensionPixelSize(
                R.styleable.BottomDrawer_bottom_sheet_extra_padding,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_extra_padding)
            )
        } finally {
            attr?.recycle()
        }
    }

    override fun addView(child: View?) {
        container.addView(child)
    }

    fun <T> addHandleView(
        view: T,
        shouldDrawUnderStatusBar: Boolean,
        shouldDrawContentUnderHandle: Boolean
    ) where T : View, T : TranslationUpdater {
        super.addView(view)
        handleView = view
        shouldDrawUnderStatus = shouldDrawUnderStatusBar
        shouldDrawUnderHandle = shouldDrawContentUnderHandle
        val marginLayoutParams = handleView?.layoutParams as MarginLayoutParams
        val height = marginLayoutParams.height + marginLayoutParams.topMargin

        if (!shouldDrawUnderStatus) {
            calculateDiffStatusBar(height)
        }
        if (!shouldDrawUnderHandle) {
            container.setMarginExtensionFunction(0, height, 0, 0)
        }

        translationUpdater = view
    }

    override fun onDraw(canvas: Canvas) {
        if (!defaultCorner) {
            cornerRadiusDrawable.bounds = rect
            cornerRadiusDrawable.draw(canvas)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(left, top, right - left, bottom - top)

        val measuredHeight = (container.parent as ViewGroup).measuredHeight
        isEnoughToFullExpand =
            measuredHeight >= fullHeight
        isEnoughToCollapseExpand = measuredHeight > collapseHeight

        defaultCorner = !isEnoughToFullExpand
    }

    fun onSlide(value: Float) {
        if (handleNonExpandableViews()) {
            return
        }

        if (value <= offsetTrigger) {
            if (!defaultCorner) {
                ViewCompat.setBackground(this, backgroundDrawable)
                defaultCorner = true
                invalidate()
            }
            container.translationY = 0f
            if (shouldDrawUnderStatus) {
                handleView?.translationY = 0f
            }
            translationUpdater?.updateTranslation(0f)
            return
        }
        if (defaultCorner) {
            ViewCompat.setBackground(this, null)
            defaultCorner = false
        }
        val offset = ((value - offsetTrigger) * (1f / (1f - offsetTrigger)))
        translateViews(offset)
        translationUpdater?.updateTranslation(offset)
        val invert = 1.0f - offset
        currentCornerRadius = cornerRadius * invert
        val fArr = cornerArray
        fArr[3] = currentCornerRadius
        fArr[2] = currentCornerRadius
        fArr[1] = currentCornerRadius
        fArr[0] = currentCornerRadius
        cornerRadiusDrawable.cornerRadii = fArr
        invalidate()
    }

    private fun handleNonExpandableViews(): Boolean {
        if (!isEnoughToFullExpand) {
            if (!defaultCorner) {
                ViewCompat.setBackground(this, backgroundDrawable)
                defaultCorner = true
            }

            translationUpdater?.updateTranslation(0f)
            translateViews(0f)
            return true
        }
        return false
    }

    private fun translateViews(value: Float) {
        translateViews(value, diffWithStatusBar)
    }

    internal fun globalTranslationViews() {
        if (isEnoughToFullExpand) {
            //if view is expanded, we need to make a correct translation depends on change orientation
            val diff = diffWithStatusBar - top
            val translationView = if (diff in 0..diffWithStatusBar) {
                diff.toFloat()
            } else {
                0f
            }
            translateViews(1f, translationView.toInt())
        } else {
            defaultCorner = true
            translationUpdater?.updateTranslation(0f)
        }
    }

    private fun translateViews(offset: Float, height: Int) {
        translationView = height * offset
        container.translationY = translationView
        if (shouldDrawUnderStatus) {
            handleView?.translationY = translationView
        }

        val paddingBottom = translationView.toInt()
        if (top == 0 && translationView != 0f && container.paddingBottom != paddingBottom) {
            container.setPadding(0, 0, 0, paddingBottom)
        }
    }

    private fun calculateDiffStatusBar(handleViewTopMargin: Int) {
        val statusBarHeight = getStatusBarHeight(context)
        diffWithStatusBar =
            when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> 0
                handleViewTopMargin < statusBarHeight -> (statusBarHeight - handleViewTopMargin)
                else -> 0
            }

        diffWithStatusBar += extraPadding
    }

    private fun getStatusBarHeight(context: Context): Int {
        var height = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = context.resources.getDimensionPixelSize(resourceId)
        }
        return height
    }

    private fun View.setMarginExtensionFunction(left: Int, top: Int, right: Int, bottom: Int) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(left, top, right, bottom)
        layoutParams = params
    }

    companion object {
        const val offsetTrigger: Float = 0.75f
    }
}