package com.flowehealth.efr_version.common.other

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flowehealth.efr_version.common.views.MainActionButton
import com.flowehealth.efr_version.home_screen.views.HidableBottomNavigationView

class LinearLayoutManagerWithHidingUIElements(private val actionButton: MainActionButton?, private val hidableNav: HidableBottomNavigationView?, context: Context?) : LinearLayoutManager(context) {

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        actionButton?.let {
            if (dy > 0) {
                it.hide()
            } else if (dy < 0) {
                it.show()
            }
        }
        hidableNav?.let {
            if (dy > 0) {
                it.hide()
            } else if (dy < 0) {
                it.show()
            }
        }
        return super.scrollVerticallyBy(dy, recycler, state)
    }
}

