package com.github.heyalex.bottomdrawer

import android.content.Context
import android.support.annotation.StyleRes
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatDialog
import android.view.Window

class BottomDrawerDialog(context: Context, @StyleRes theme: Int = R.style.BottomDialogTheme) :
    AppCompatDialog(context, theme) {

    private var behavior: BottomSheetBehavior<BottomDrawer>? = null
    private lateinit var drawer: BottomDrawer

    private lateinit var coordinator: CoordinatorLayout

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

}