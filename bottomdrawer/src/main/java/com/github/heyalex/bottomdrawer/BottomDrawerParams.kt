package com.github.heyalex.bottomdrawer

import android.view.View

data class BottomDrawerParams(
    var handleView: View?,
    var shouldDrawUnderStatus: Boolean?,
    var shouldDrawUnderHandle: Boolean?
)