package com.github.heyalex.bottomdrawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

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
    private var offsetTrigger: Float
    private var currentCornerRadius: Float = 0f
    private var defaultCorner = false
    private var diffWithStatusBar: Int = 0
    private var translationView: Float = 0f

    //TODO as attribute
    private val shouldDrawUnder = false

    private var translationUpdater: TranslationUpdater? = null
    private var handleView: View? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr) {
        setWillNotDraw(false)
        drawerBackground = ContextCompat.getColor(context, R.color.bottom_drawer_background)
        cornerRadius = resources.getDimensionPixelSize(R.dimen.bottom_sheet_corner_radius).toFloat()
        cornerRadiusDrawable.setColor(drawerBackground)
        //TODO as attribute
        offsetTrigger = 0.75f

        calculateDiffStatusBar(0)

        container = FrameLayout(context).apply {
            val params =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.topMargin =
                //TODO make it configurable through attributes
                resources.getDimensionPixelSize(R.dimen.default_bottom_sheet_top_container_margin)

            layoutParams = params
        }
        super.addView(container)
        container.clipToPadding = false
        onSlide(0f)
    }

    override fun addView(child: View?) {
        container.addView(child)
    }

    fun <T> addHandleView(view: T) where T : View, T : TranslationUpdater {
        super.addView(view)
        handleView = view
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
    }

    fun onSlide(value: Float) {
        if (value <= offsetTrigger) {
            if (!defaultCorner) {
                ViewCompat.setBackground(this, backgroundDrawable)
                defaultCorner = true
                invalidate()
            }
            container.translationY = 0f
            if(!shouldDrawUnder) {
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

    internal fun translateViews(value: Float) {
        translationView = diffWithStatusBar * value
        container.translationY = translationView

        if(!shouldDrawUnder) {
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
    }

    fun getStatusBarHeight(context: Context): Int {
        var h = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            h = context.resources.getDimensionPixelSize(resourceId)
        }
        return h
    }
}