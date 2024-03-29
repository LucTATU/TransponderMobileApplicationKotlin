package fr.tatu.kartingapp.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.synthetic.main.activity_b_l_e_device_characteristic_cell.view.*
import kotlinx.android.synthetic.main.activity_b_l_e_device_service_cell.view.*
import java.util.*
import kotlin.collections.ArrayList

class BLEServiceAdapter(
    private val context: Context,
    private val serviceList: MutableList<BLEService>,
    private val readCharacteristicCallback: (BluetoothGattCharacteristic) -> Unit,
    private val writeCharacteristicCallback: (BluetoothGattCharacteristic) -> Unit,
    private val notifyCharacteristicCallback: (BluetoothGattCharacteristic, Boolean) -> Unit
) : ExpandableRecyclerViewAdapter<BLEServiceAdapter.ServiceViewHolder, BLEServiceAdapter.CharacteristicViewHolder>(serviceList) {

    class ServiceViewHolder(itemView: View) : GroupViewHolder(itemView) {
        val serviceName: TextView = itemView.serviceName
        val serviceUuid: TextView = itemView.serviceUuid
    }

    class CharacteristicViewHolder(itemView: View) : ChildViewHolder(itemView) {
        val characteristicName: TextView = itemView.characteristicName
        val characteristicUuid: TextView = itemView.characteristicUuid
        val characteristicProperties: TextView = itemView.characteristicProperties
        val characteristicValue: TextView = itemView.characteristicValue

        val characteristicReadAction: Button = itemView.readAction
        val characteristicWriteAction: Button = itemView.writeAction
        val characteristicNotifyAction: Button = itemView.notifyAction
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder =
        ServiceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.activity_b_l_e_device_service_cell, parent, false))

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int):
            CharacteristicViewHolder =
        CharacteristicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.activity_b_l_e_device_characteristic_cell, parent, false))

    override fun onBindGroupViewHolder(holder: ServiceViewHolder, flatPosition: Int, group: ExpandableGroup<*>)
    {
        val title = BLEUUIDAttributes.getBLEAttributeFromUUID(group.title).title
        holder.serviceName.text = title
        holder.serviceUuid.text = group.title
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onBindChildViewHolder(holder: CharacteristicViewHolder, flatPosition: Int, group: ExpandableGroup<*>, childIndex: Int)
    {
        val characteristic = (group.items[childIndex] as BluetoothGattCharacteristic)
        val title = BLEUUIDAttributes.getBLEAttributeFromUUID(characteristic.uuid.toString()).title

        val uuidMessage = "UUID : ${characteristic.uuid}"
        holder.characteristicUuid.text = uuidMessage

        holder.characteristicName.text = title
        val properties = arrayListOf<String>()

        addPropertyFromCharacteristic(characteristic, properties,"Read",
            BluetoothGattCharacteristic.PROPERTY_READ, holder.characteristicReadAction,
            readCharacteristicCallback)

        addPropertyFromCharacteristic(characteristic, properties,"Write",
            (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE),
            holder.characteristicWriteAction, writeCharacteristicCallback)

        addPropertyNotificationFromCharacteristic(characteristic, properties, holder.characteristicNotifyAction,
            notifyCharacteristicCallback)

        val proprietiesMessage = "Property : ${properties.joinToString()}"
        holder.characteristicProperties.text = proprietiesMessage
        characteristic.value?.let {
            val hex = it.joinToString("") { byte -> "%02x".format(byte) }.toUpperCase(Locale.FRANCE)
            val value = "Value : ${String(it)} Hex : 0x$hex"
            holder.characteristicValue.visibility = View.VISIBLE
            holder.characteristicValue.text = value
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun addPropertyFromCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        properties: ArrayList<String>,
        propertyName: String,
        propertyType: Int,
        propertyAction: Button,
        propertyCallBack: (BluetoothGattCharacteristic) -> Unit)
    {
        if ((characteristic.properties and propertyType) != 0)
        {
            properties.add(propertyName)
            propertyAction.isEnabled = true
            propertyAction.alpha = 1f
            propertyAction.setOnClickListener {
                propertyCallBack.invoke(characteristic)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun addPropertyNotificationFromCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        properties: ArrayList<String>,
        propertyAction: Button,
        propertyCallBack: (BluetoothGattCharacteristic, Boolean) -> Unit
    ) {
        if ((characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            properties.add("Notify")
            propertyAction.isEnabled = true
            propertyAction.alpha = 1f
            val isNotificationEnable = characteristic.descriptors.any {
                it.value?.contentEquals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) ?: false
            }
            if (isNotificationEnable) {
                propertyAction.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary))
                propertyAction.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            } else {
                propertyAction.setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent))
                propertyAction.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
            }
            propertyAction.setOnClickListener {
                propertyCallBack.invoke(characteristic, !isNotificationEnable)
            }
        }
    }

    fun updateFromChangedCharacteristic(characteristic: BluetoothGattCharacteristic?) {
        serviceList.forEach {
            val index = it.items.indexOf(characteristic)
            if (index != -1) {
                it.items.removeAt(index)
                it.items.add(index, characteristic)
            }
        }
    }
}
