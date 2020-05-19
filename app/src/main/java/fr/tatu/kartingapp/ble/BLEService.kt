package fr.tatu.kartingapp.ble

import android.bluetooth.BluetoothGattCharacteristic
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup


class BLEService(title: String, characteristics: MutableList<BluetoothGattCharacteristic>) :
    ExpandableGroup<BluetoothGattCharacteristic>(title, characteristics)