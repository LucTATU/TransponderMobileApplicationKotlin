package fr.tatu.kartingapp

import android.bluetooth.*
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_b_l_e_device.*
import java.util.Timer
import java.util.TimerTask


class BLEDeviceActivity : AppCompatActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var adapter: BLEServiceAdapter
    private var timer: Timer? = null

    private val uuidServiceWanted = "6e400001-b5a3-f393-e0a9-e50e24dcca9e" //UART Bluefruit LE Uuid

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_l_e_device)

        val device = intent.getParcelableExtra<BluetoothDevice>("ble_device")
        deviceName.text = device?.name ?: getString(R.string.ble_scan_default_name)
        deviceStatus.text =
            getString(
                R.string.ble_device_status,
                getString(BLEConnexionState.STATE_CONNECTING.text)
            )
        progressBarService.visibility = View.VISIBLE
        divider.visibility = View.INVISIBLE

        connectToDevice(device)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun connectToDevice(device: BluetoothDevice?) {
        bluetoothGatt = device?.connectGatt(this, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                onConnectionStateChange(newState, gatt)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                onServicesDiscovered(gatt)
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                runOnUiThread {
                    adapter.updateFromChangedCharacteristic(characteristic)
                    adapter.notifyDataSetChanged()
                }
            }
        })
        bluetoothGatt?.connect()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun onConnectionStateChange(newState: Int, gatt: BluetoothGatt?) {
        BLEConnexionState.getBLEConnexionStateFromState(
            newState
        )?.let {
            runOnUiThread {
                progressBarService.visibility = View.INVISIBLE
                divider.visibility = View.VISIBLE
                deviceStatus.text =
                    getString(R.string.ble_device_status, getString(it.text))
            }

            if (it.state == BLEConnexionState.STATE_CONNECTED.state) {
                gatt?.discoverServices()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun onServicesDiscovered(gatt: BluetoothGatt?) {

        gatt?.services?.let {
            runOnUiThread {
                adapter = BLEServiceAdapter(
                    this,
                    it.filter { it.uuid.toString() == uuidServiceWanted }
                        .map { service ->
                            BLEService(
                                service.uuid.toString(),
                                service.characteristics
                            )
                        }
                        .toMutableList(),
                    { characteristic -> gatt.readCharacteristic(characteristic) },
                    { characteristic -> writeIntoCharacteristic(gatt, characteristic) },
                    { characteristic, enable ->
                        toggleNotificationOnCharacteristic(
                            gatt,
                            characteristic,
                            enable
                        )
                    }
                )
                bleServicesList.adapter = adapter
                bleServicesList.addItemDecoration(
                    DividerItemDecoration(
                        this@BLEDeviceActivity,
                        LinearLayoutManager.VERTICAL
                    )
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun toggleNotificationOnCharacteristic(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        enable: Boolean
    ) {
        characteristic.descriptors.forEach {
            it.value =
                if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(it)
        }
        gatt.setCharacteristicNotification(characteristic, enable)
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                gatt.readCharacteristic(characteristic)
            }
        }, 0, 1000)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun writeIntoCharacteristic(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        runOnUiThread {
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(16, 0, 16, 0)
            input.layoutParams = params

            AlertDialog.Builder(this@BLEDeviceActivity)
                .setTitle(R.string.ble_device_write_characteristic_title)
                .setView(input)
                .setPositiveButton(R.string.ble_device_write_characteristic_confirm) { _, _ ->
                    characteristic.value = input.text.toString().toByteArray()
                    gatt.writeCharacteristic(characteristic)
                    gatt.readCharacteristic(characteristic)
                }
                .setNegativeButton(R.string.ble_device_write_characteristic_cancel) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onStop() {
        super.onStop()
        closeBluetoothGatt()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun closeBluetoothGatt() {
        timer?.cancel()
        timer = null
        deviceStatus.text =
            getString(
                R.string.ble_device_status,
                getString(BLEConnexionState.STATE_DISCONNECTED.text)
            )
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
