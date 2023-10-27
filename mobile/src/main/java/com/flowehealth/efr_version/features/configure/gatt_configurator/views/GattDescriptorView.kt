package com.flowehealth.efr_version.features.configure.gatt_configurator.views

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.features.configure.gatt_configurator.models.Descriptor
import com.flowehealth.efr_version.features.configure.gatt_configurator.models.Property
import kotlinx.android.synthetic.main.view_gatt_descriptor.view.*
import kotlinx.android.synthetic.main.view_gatt_descriptor.view.ib_copy
import kotlinx.android.synthetic.main.view_gatt_descriptor.view.ib_edit
import kotlinx.android.synthetic.main.view_gatt_descriptor.view.ib_remove
import kotlinx.android.synthetic.main.view_gatt_descriptor.view.tv_property_read
import kotlinx.android.synthetic.main.view_gatt_descriptor.view.tv_property_write

class GattDescriptorView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {
    private var descriptor: Descriptor? = null

    constructor(context: Context, descriptor: Descriptor) : this(context) {
        this.descriptor = descriptor

        initView(descriptor)
        if(descriptor.isPredefined) hideButtons()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_gatt_descriptor, this, true)
    }

    private fun initView(descriptor: Descriptor) {
        tv_descriptor_name.text = descriptor.name
        tv_descriptor_uuid.text = buildBoldHeaderTextLine(context.getString(R.string.UUID_colon_space), descriptor.uuid?.getAsFormattedText())
        tv_descriptor_value.text = buildBoldHeaderTextLine(context.getString(R.string.value_colon_space), descriptor.value?.getAsFormattedText())
        showSelectedProperties(descriptor)
    }

    private fun buildBoldHeaderTextLine(header: String, content: String?): Spanned? {
        val htmlString = buildString {
            append("<b>")
            append(header)
            append("</b>")
            append(content)
        }
        return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
    }

    fun refreshView() {
        descriptor?.let {
            initView(it)
        }
    }

    private fun hideButtons() {
        ib_copy.visibility = View.GONE
        ib_remove.visibility = View.GONE
        ib_edit.visibility = View.GONE
    }

    private fun hideAllProperties() {
        tv_property_read.visibility = View.GONE
        tv_property_write.visibility = View.GONE
    }

    private fun showSelectedProperties(descriptor: Descriptor) {
        hideAllProperties()
        descriptor.properties.apply {
            if (containsKey(Property.READ)) tv_property_read.visibility = View.VISIBLE
            if (containsKey(Property.WRITE)) tv_property_write.visibility = View.VISIBLE
        }
    }

    fun setDescriptorListener(listener: DescriptorListener) {
        ib_copy.setOnClickListener {
            descriptor?.let {
                listener.onCopyDescriptor(it)
            }
        }
        ib_edit.setOnClickListener {
            descriptor?.let {
                listener.onEditDescriptor(it)
            }
        }
        ib_remove.setOnClickListener {
            descriptor?.let {
                listener.onRemoveDescriptor(it)
            }
        }
    }

    interface DescriptorListener {
        fun onCopyDescriptor(descriptor: Descriptor)
        fun onEditDescriptor(descriptor: Descriptor)
        fun onRemoveDescriptor(descriptor: Descriptor)
    }
}