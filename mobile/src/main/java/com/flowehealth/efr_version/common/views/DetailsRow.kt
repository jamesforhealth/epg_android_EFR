package com.flowehealth.efr_version.common.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.flowehealth.efr_version.R
import kotlinx.android.synthetic.main.list_item_details_row.view.*

class DetailsRow : LinearLayout {

    init {
        LayoutInflater.from(context).inflate(R.layout.list_item_details_row, this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, title: String?, text: String?) : super(context) {
        tv_title.text = title
        tv_details.text = text
    }
}