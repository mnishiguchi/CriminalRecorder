package com.mnishiguchi.criminalrecorder.ui

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * To use this behavior, add the layout_behavior attribute to the Floating Action Button.
 * https://www.sitepoint.com/animating-android-floating-action-button/
 *
 * Usage:
 *     app:layout_behavior="com.mnishiguchi.criminalrecorder.ui.HideOnScrollFabBehavior"
 */
class HideOnScrollFabBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout,
                                child: FloatingActionButton,
                                target: View,
                                dxConsumed: Int,
                                dyConsumed: Int,
                                dxUnconsumed: Int,
                                dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)

        when {
            dyConsumed > 0 -> {
                val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
                val fab_bottomMargin = layoutParams.bottomMargin
                child.animate()
                        .translationY((child.height + fab_bottomMargin).toFloat())
                        .setInterpolator(LinearInterpolator())
                        .start()
            }
            dyConsumed < 0 -> {
                child.animate()
                        .translationY(0f)
                        .setInterpolator(LinearInterpolator())
                        .start()
            }
        }
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout,
                                     child: FloatingActionButton,
                                     directTargetChild: View,
                                     target: View,
                                     nestedScrollAxes: Int
    ): Boolean = nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
}