package com.flowehealth.efr_version.features.configure.gatt_configurator.import_export.migration

import android.content.Context
import com.flowehealth.efr_version.features.configure.gatt_configurator.import_export.data.CharacteristicImportData
import com.flowehealth.efr_version.features.configure.gatt_configurator.import_export.data.DescriptorImportData
import com.flowehealth.efr_version.features.configure.gatt_configurator.import_export.data.ServerImportData
import com.flowehealth.efr_version.features.configure.gatt_configurator.import_export.data.ServiceImportData
import com.flowehealth.efr_version.features.configure.gatt_configurator.models.Characteristic
import com.flowehealth.efr_version.features.configure.gatt_configurator.models.Descriptor
import com.flowehealth.efr_version.features.configure.gatt_configurator.models.GattServer
import com.flowehealth.efr_version.features.configure.gatt_configurator.models.Service
import com.flowehealth.efr_version.features.configure.gatt_configurator.utils.GattConfiguratorStorage

class Migrator(private val context: Context) {

    fun migrate() {
        GattConfiguratorStorage(context).let {
            val migratedServers = it.loadGattServerList().map { server ->
                val migratedServices = server.services.map { service ->
                    val migratedCharacteristics = service.characteristics.map { characteristic ->
                        val migratedDescriptors = characteristic.descriptors.map { descriptor ->
                            descriptor.copy(importedData = DescriptorImportData())
                        } as ArrayList<Descriptor>
                        characteristic.copy(descriptors = migratedDescriptors, importedData = CharacteristicImportData())
                    } as ArrayList<Characteristic>
                    service.copy(characteristics = migratedCharacteristics, importedData = ServiceImportData())
                } as ArrayList<Service>
                server.copy(services = migratedServices, importedData = ServerImportData())
            } as ArrayList<GattServer>
            it.saveGattServerList(migratedServers)
        }
    }
}