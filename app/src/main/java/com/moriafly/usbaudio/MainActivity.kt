package com.moriafly.usbaudio

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import au.id.jms.usbaudio.AudioPlayback
import au.id.jms.usbaudio.UsbAudio
import com.kongzue.dialogx.DialogX
import com.moriafly.usbaudio.databinding.ActivityMainBinding
import org.libusb.UsbHelper

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding
    lateinit var mUsbAudio: UsbAudio


    companion object {

        val TAG = "UsbAudio"
        val ACTION_USB_PERMISSION = "com.minelab.droidspleen.USB_PERMISSION";

    }

    var mPermissionIntent: PendingIntent? = null
    var mUsbManager: UsbManager? = null
    var mAudioDevice: UsbDevice? = null
    private var mUsbPermissionReciever: UsbReciever? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        DialogX.init(this)
        initLib()
        initPermission()
        mBinding.initListener()
    }


    fun initPermission() {
        // Register for permission

        // Register for permission
        mPermissionIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_MUTABLE
        )
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        mUsbPermissionReciever = UsbReciever()
        registerReceiver(mUsbPermissionReciever, filter)

        // Request permission from user

        // Request permission from user
        if (mAudioDevice != null && mPermissionIntent != null) {
            mUsbManager!!.requestPermission(mAudioDevice, mPermissionIntent)
        } else {
            Log.e(
                TAG,
                "Device not present? Can't request peremission"
            )
        }
    }

    private fun initLib() {

        // Grab the USB Device so we can get permission
        mUsbManager = getSystemService(USB_SERVICE) as UsbManager
        val deviceList = mUsbManager!!.deviceList
        val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
        while (deviceIterator.hasNext()) {
            val device = deviceIterator.next()
            val intf = device.getInterface(0)
            if (intf.interfaceClass == UsbConstants.USB_CLASS_AUDIO) {
                Log.d(TAG, "Audio class device: $device")
                mAudioDevice = device
            }
        }

        // Load native lib
        System.loadLibrary("usb1.0")
        UsbHelper.useContext(applicationContext)
        mUsbAudio = UsbAudio()
        AudioPlayback.setup()

    }

    fun ActivityMainBinding.initListener() {
        btnGetUsbDevices.setOnClickListener {
            showToast("获取USB设备")
        }

        startButton.setOnClickListener {
            Log.d(TAG, "Start pressed")
            if (mUsbAudio.setup(0, 0, 0) == true) {
                startButton.isEnabled = false
                stopButton.isEnabled = true
            }
            Thread {
                while (true) {
                    mUsbAudio.loop()
                }
            }.start()
        }

        stopButton.setOnClickListener {
            Log.d(TAG, "Stop pressed")
            mUsbAudio.stop()
            mUsbAudio.close()
            startButton.isEnabled = true
            stopButton.isEnabled = false
        }
    }


    private class UsbReciever : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val device =
                intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
            if (MainActivity.ACTION_USB_PERMISSION == action) {
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    showToast("权限已获取！")
                } else {
                    Log.d(
                        MainActivity.TAG,
                        "Permission denied for device $device"
                    )
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mUsbPermissionReciever)
        if (mUsbAudio != null) {
            mUsbAudio.stop()
            mUsbAudio.close()
        }
    }
}