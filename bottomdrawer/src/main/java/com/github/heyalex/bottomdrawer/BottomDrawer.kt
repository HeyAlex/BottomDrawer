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
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

class BottomDrawer : FrameLayout {

    private var container: ViewGroup
    private val rect: Rect = Rect()

    private val defaultBackgroundDrawable = GradientDrawable()
    private val cornerRadiusDrawable = GradientDrawable()
    private val cornerArray: FloatArray =
        floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
    private var drawerBackground: Int = 0
    private var cornerRadius: Float = 0f
    private var extraPadding: Int = 0
    private var currentCornerRadius: Float = 0f
    private var defaultCorner = false
    private var diffWithStatusBar: Int = 0
    private var translationView: Float = 0f

    private var translationUpdater: TranslationUpdater? = null
    private var handleView: View? = null

    private var isEnoughToFullExpand: Boolean = false
    private var isEnoughToCollapseExpand: Boolean = false

    private var heightPixels: Int
    private var fullHeight: Int
    private var collapseHeight: Int

    private var shouldDrawUnderStatus: Boolean = false
    private var shouldDrawUnderHandle: Boolean = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr) {

        initAttributes(context, attrs)
        setWillNotDraw(false)
        cornerRadiusDrawable.setColor(drawerBackground)
        defaultBackgroundDrawable.setColor(drawerBackground)

        calculateDiffStatusBar(0)

        heightPixels = context.resources.displayMetrics.heightPixels
        fullHeight = heightPixels
        collapseHeight = heightPixels / 2

        container = FrameLayout(context).apply {
            val params =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            layoutParams = params
        }
        super.addView(container)
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

            cornerRadius = attr.getDimensionPixelSize(
                R.styleable.BottomDrawer_bottom_sheet_corner_radius,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_corner_radius)
            ).toFloat()

            val cornerArray: FloatArray =
                floatArrayOf(
                    cornerRadius,
                    cornerRadius,
                    cornerRadius,
                    cornerRadius,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.0f
                )
            defaultBackgroundDrawable.cornerRadii = cornerArray

            shouldDrawUnderStatus = attr.getBoolean(
                R.styleable.BottomDrawer_should_draw_under_status_bar,
                false
            )

            shouldDrawUnderHandle = attr.getBoolean(
                R.styleable.BottomDrawer_should_draw_content_under_handle_view,
                false
            )

            drawerBackground = attr.getColor(
                R.styleable.BottomDrawer_bottom_drawer_background,
                ContextCompat.getColor(context, R.color.bottom_drawer_background)
            )

        } finally {
            attr?.recycle()
        }
    }

    override fun addView(child: View?) {
        container.addView(child)
    }

    override fun onDraw(canvas: Canvas) {
        if (!defaultCorner && !rect.isEmpty) {
            cornerRadiusDrawable.bounds = rect
            defaultBackgroundDrawable.bounds = rect
            cornerRadiusDrawable.draw(canvas)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(left, top, right - left, bottom - top)

        val measuredHeight = (container.parent as ViewGroup).measuredHeight
        isEnoughToFullExpand =
            measuredHeight >= fullHeight
        isEnoughToCollapseExpand = measuredHeight >= collapseHeight

        defaultCorner = !isEnoughToFullExpand
    }

    fun onSlide(value: Float) {
        if (handleNonExpandableViews()) {
            return
        }

        if (value <= offsetTrigger) {
            if (!defaultCorner) {
                ViewCompat.setBackground(this, defaultBackgroundDrawable)
                defaultCorner = true
                invalidate()
            }
            container.translationY = 0f
            if (!shouldDrawUnderStatus) {
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
                ViewCompat.setBackground(this, defaultBackgroundDrawable)
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
        if (isEnoughToFullExpand && top < fullHeight - collapseHeight) {
            updateTranslationOnGlobalLayoutChanges()
        } else {
            if (container.paddingBottom != 0) {
                container.setPadding(0, 0, 0, 0)
            }
            translationUpdater?.updateTranslation(0f)
            if (top == fullHeight - collapseHeight || !rect.isEmpty) {
                defaultCorner = true
                ViewCompat.setBackground(this, defaultBackgroundDrawable)
                translateViews(0f)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isEnoughToFullExpand) {
                updateTranslationOnGlobalLayoutChanges()
            }
        }
    }

    private fun updateTranslationOnGlobalLayoutChanges() {
        //if view is expanded, we need to make a correct translation depends on change orientation
        val diff = diffWithStatusBar - top
        val translationView = if (diff in 0..diffWithStatusBar) {
            diff.toFloat()
        } else {
            0f
        }
        translateViews(1f, translationView.toInt())
        if (translationView == 0f && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            translationUpdater?.updateTranslation(0f)
        } else if (top == 0) {
            translationUpdater?.updateTranslation(1f)
        }
    }

    private fun translateViews(offset: Float, height: Int) {
        translationView = height * offset
        container.translationY = translationView
        if (!shouldDrawUnderStatus) {
            handleView?.translationY = translationView
        }

        val paddingBottom = translationView.toInt()
        if (top == 0 && translationView != 0f && container.paddingBottom != paddingBottom) {
            container.setPadding(0, 0, 0, paddingBottom)
        }
    }

    private fun calculateDiffStatusBar(topInset: Int) {
        diffWithStatusBar =
            when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> 0
                else -> topInset
            }

        diffWithStatusBar += extraPadding
    }

    fun addHandleView(newHandleView: View?) {
        handleView = newHandleView
        handleView?.let { view ->
            super.addView(view)
            handleView = view
            val marginLayoutParams = handleView?.layoutParams as MarginLayoutParams
            val height = marginLayoutParams.height + marginLayoutParams.topMargin

            if (!shouldDrawUnderHandle) {
                container.setMarginExtensionFunction(0, height, 0, 0)
            } else {
                container.setMarginExtensionFunction(0, 0, 0, 0)
            }

            translationUpdater = view as TranslationUpdater
        }
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            heightPixels = context.resources.displayMetrics.heightPixels
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                heightPixels -= insets.systemWindowInsetTop
            }

            fullHeight = heightPixels
            collapseHeight = heightPixels / 2

            calculateDiffStatusBar(insets.systemWindowInsetTop)
        }
        return super.onApplyWindowInsets(insets)
    }

    private fun View.setMarginExtensionFunction(left: Int, top: Int, right: Int, bottom: Int) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(left, top, right, bottom)
        layoutParams = params
    }

    fun changeCornerRadius(radius: Float) {
        onSlide(1f)
        cornerRadius = radius
        val cornerArray: FloatArray =
            floatArrayOf(
                cornerRadius,
                cornerRadius,
                cornerRadius,
                cornerRadius,
                0.0f,
                0.0f,
                0.0f,
                0.0f
            )
        defaultBackgroundDrawable.cornerRadii = cornerArray
        invalidate()
    }

    fun changeBackgroundColor(color: Int) {
        drawerBackground = color
        cornerRadiusDrawable.setColor(drawerBackground)
        defaultBackgroundDrawable.setColor(drawerBackground)
        invalidate()
    }

    fun changeExtraPadding(extraPadding: Int) {
        this.extraPadding = extraPadding
        invalidate()
    }

    fun shouldDrawUnderHandleView(shouldDrawUnderHandleView: Boolean) {
        shouldDrawUnderHandle = shouldDrawUnderHandleView
        super.removeView(handleView)
        addHandleView(handleView)
        invalidate()
    }

    fun shouldDrawUnderStatusBar(shouldDrawerUnderStatus: Boolean) {
        shouldDrawUnderStatus = shouldDrawerUnderStatus
        invalidate()
    }

    companion object {
        const val offsetTrigger: Float = 0.75f
    }
}