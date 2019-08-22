package com.github.heyalex.bottomdrawer

import android.view.View

data class BottomDrawerParams(
    var handleView: View? = null,
    var shouldDrawUnderStatus: Boolean = false,
    var shouldDrawUnderHandle: Boolean = false
)