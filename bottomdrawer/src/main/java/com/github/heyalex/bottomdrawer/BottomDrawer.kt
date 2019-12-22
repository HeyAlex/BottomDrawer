package com.github.heyalex.bottomdrawer

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapePathModel

class BottomDrawer : FrameLayout {

    private var container: ViewGroup
    private val rect: Rect = Rect()

    private val backgroundDrawable = MaterialShapeDrawable()
    private var drawerBackground: Int = 0
    private var cornerRadius: Float = 0f
    private var extraPadding: Int = 0
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

        val shapePathModel = ShapePathModel().apply {
            topLeftCorner = RoundedCornerTreatment(cornerRadius)
            topRightCorner = RoundedCornerTreatment(cornerRadius)
        }
        backgroundDrawable.apply {
            shapedViewModel = shapePathModel
            setTint(drawerBackground)
            paintStyle = Paint.Style.FILL
        }

        calculateDiffStatusBar(0)

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val smallest = Point()
        val tallest = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            display.getCurrentSizeRange(smallest, tallest)
        }

        heightPixels = tallest.y
        fullHeight = heightPixels
        collapseHeight = heightPixels / 2

        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                heightPixels = context.resources.displayMetrics.heightPixels
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    heightPixels -= insets.systemWindowInsetTop
                }

                fullHeight = heightPixels
                collapseHeight = heightPixels / 2

                calculateDiffStatusBar(insets.systemWindowInsetTop)
            }
            insets.consumeSystemWindowInsets()
        }

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
        if (!rect.isEmpty) {
            backgroundDrawable.bounds = rect
            backgroundDrawable.draw(canvas)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(left, top, right - left, bottom - top)

        val measuredHeight = (container.parent as ViewGroup).measuredHeight
        isEnoughToFullExpand =
            measuredHeight >= fullHeight
        isEnoughToCollapseExpand = measuredHeight >= collapseHeight

        backgroundDrawable.interpolation = if (!isEnoughToFullExpand) 1f else 0f
    }

    fun onSlide(value: Float) {
        if (handleNonExpandableViews()) {
            return
        }

        if (value <= offsetTrigger) {
            backgroundDrawable.interpolation = 1f
            container.translationY = 0f
            if (!shouldDrawUnderStatus) {
                handleView?.translationY = 0f
            }
            translationUpdater?.updateTranslation(0f)
            return
        }
        val offset = ((value - offsetTrigger) * (1f / (1f - offsetTrigger)))
        translateViews(offset)
        translationUpdater?.updateTranslation(offset)
        val invert = 1.0f - offset
        backgroundDrawable.interpolation = invert
        invalidate()
    }

    private fun handleNonExpandableViews(): Boolean {
        if (!isEnoughToFullExpand) {
            backgroundDrawable.interpolation = 1f
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
                backgroundDrawable.interpolation = 1f
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
        cornerRadius = radius
        val shapePathModel = ShapePathModel().apply {
            topLeftCorner = RoundedCornerTreatment(cornerRadius)
            topRightCorner = RoundedCornerTreatment(cornerRadius)
        }
        backgroundDrawable.shapedViewModel = shapePathModel
        backgroundDrawable.interpolation = 1f

        invalidate()
    }

    fun changeBackgroundColor(color: Int) {
        drawerBackground = color
        backgroundDrawable.setTint(drawerBackground)
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