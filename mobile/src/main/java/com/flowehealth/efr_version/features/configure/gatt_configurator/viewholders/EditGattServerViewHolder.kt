package com.flowehealth.efr_version.features.configure.gatt_configurator.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.features.configure.gatt_configurator.activities.GattServerActivity
import com.flowehealth.efr_version.features.configure.gatt_configurator.adapters.EditGattServerAdapter.ServiceListener
import com.flowehealth.efr_version.features.configure.gatt_configurator.dialogs.CharacteristicDialog
import com.flowehealth.efr_version.features.configure.gatt_configurator.models.Characteristic
import com.flowehealth.efr_version.features.configure.gatt_configurator.models.Service
import com.flowehealth.efr_version.features.configure.gatt_configurator.utils.removeAsking
import com.flowehealth.efr_version.features.configure.gatt_configurator.views.GattCharacteristicView
import kotlinx.android.synthetic.main.adapter_edit_gatt_server.view.*

class EditGattServerViewHolder(view: View, val list: ArrayList<Service>, val listener: ServiceListener) : RecyclerView.ViewHolder(view) {
    private val llCharacteristics = view.ll_characteristics
    private val llCharacteristicsOuter = view.ll_characteristics_outer
    private val expandArrow = view.expand_arrow
    private val tvName = view.tv_service_name
    private val tvUuid = view.tv_service_uuid
    private val tvType = view.tv_service_type
    private val ibCopy = view.ib_copy
    private val ibRemove = view.ib_remove
    private val btnAddCharacteristic = view.btn_add_characteristic

    fun bind(service: Service) {
        llCharacteristics.removeAllViews()
        expandArrow.setOnClickListener { expandOrCollapseView() }
        initCharacteristics(service.characteristics)

        tvName.text = service.name
        tvUuid.text = service.uuid?.getAsFormattedText()
        tvType.text = itemView.context.getText(service.type.textResId)

        handleServiceClickEvents(service)
        handleAddCharacteristicClickEvent(service.characteristics)
    }

    private fun handleServiceClickEvents(service: Service) {
        ibCopy.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onCopyService(service)
            }
        }

        ibRemove.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onRemoveService(adapterPosition)
            }
        }
    }

    private fun initCharacteristics(characteristics: ArrayList<Characteristic>) {
        for (characteristic in characteristics) {
            val view = GattCharacteristicView(itemView.context, characteristic)
            handleCharacteristicClickEvents(view, characteristics)
            llCharacteristics.addView(view)
        }
    }

    private fun handleCharacteristicClickEvents(view: GattCharacteristicView, characteristics: ArrayList<Characteristic>) {
        view.setCharacteristicListener(object : GattCharacteristicView.CharacteristicListener {
            override fun onCopyCharacteristic(characteristic: Characteristic) {
                copyCharacteristic(characteristic, characteristics)
            }

            override fun onEditCharacteristic(characteristic: Characteristic) {
                editCharacteristic(characteristic, characteristics)
            }

            override fun onRemoveCharacteristic(characteristic: Characteristic) {
                view.removeAsking(R.string.characteristic) {
                    removeCharacteristic(characteristic, characteristics)
                }
            }
        })
    }

    private fun handleAddCharacteristicClickEvent(characteristics: ArrayList<Characteristic>) {
        btnAddCharacteristic.setOnClickListener {
            CharacteristicDialog(object : CharacteristicDialog.CharacteristicChangeListener {
                override fun onCharacteristicChanged(characteristic: Characteristic) {
                    addCharacteristic(characteristic, characteristics)
                }
            }).show((itemView.context as GattServerActivity).supportFragmentManager, "dialog_characteristic")
        }
    }

    private fun copyCharacteristic(characteristic: Characteristic, characteristics: ArrayList<Characteristic>) {
        val copiedCharacteristic = characteristic.deepCopy()
        val view = GattCharacteristicView(itemView.context, copiedCharacteristic)

        characteristics.add(copiedCharacteristic)
        handleCharacteristicClickEvents(view, characteristics)
        llCharacteristics.addView(view)
    }

    private fun editCharacteristic(characteristic: Characteristic, characteristics: ArrayList<Characteristic>) {
        CharacteristicDialog(object : CharacteristicDialog.CharacteristicChangeListener {
            override fun onCharacteristicChanged(characteristic: Characteristic) {
                val index = characteristics.indexOf(characteristic)
                (llCharacteristics[index] as GattCharacteristicView).refreshView()
            }
        }, characteristic).show((itemView.context as GattServerActivity).supportFragmentManager, "dialog_characteristic")
    }

    private fun removeCharacteristic(characteristic: Characteristic, characteristics: ArrayList<Characteristic>) {
        val index = characteristics.indexOf(characteristic)
        llCharacteristics.removeViewAt(index)
        characteristics.remove(characteristic)
    }

    private fun addCharacteristic(characteristic: Characteristic, characteristics: ArrayList<Characteristic>) {
        val view = GattCharacteristicView(itemView.context, characteristic)
        handleCharacteristicClickEvents(view, characteristics)

        characteristics.add(characteristic)
        llCharacteristics.addView(view)
    }

    companion object {
        fun create(parent: ViewGroup, list: ArrayList<Service>, listener: ServiceListener): EditGattServerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_edit_gatt_server, parent, false)
            return EditGattServerViewHolder(view, list, listener)
        }
    }

    private fun expandOrCollapseView() {
        if (llCharacteristicsOuter.visibility == View.VISIBLE) {
            expandArrow.setState(false)
            llCharacteristicsOuter.visibility = View.GONE
        } else {
            expandArrow.setState(true)
            llCharacteristicsOuter.visibility = View.VISIBLE
        }
    }

}
