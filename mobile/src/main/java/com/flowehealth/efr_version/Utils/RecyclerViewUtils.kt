package com.flowehealth.efr_version.utils

import androidx.recyclerview.widget.RecyclerView

object RecyclerViewUtils {

    fun <T : RecyclerView.ViewHolder> withProperAdapterPosition(holder: T, action: (pos: Int) -> Unit) {
        if (holder.adapterPosition != RecyclerView.NO_POSITION) action(holder.adapterPosition)
    }
}