package com.moriafly.usbaudio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import au.id.jms.usbaudio.UsbAudio
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.moriafly.usbaudio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding
    lateinit var mUsbAudio: UsbAudio

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initLib()
        initPermission()
        mBinding.initListener()
    }


    fun initPermission() {
        val permissions = mutableListOf(Permission.RECORD_AUDIO)
        // 请求USB读写权限
        XXPermissions.with(this).permission(permissions).request(null)
    }

    private fun initLib() {
//        System.loadLibrary("usbaudio");
//        mUsbAudio = UsbAudio()
//        mUsbAudio.loop()
    }

    fun ActivityMainBinding.initListener() {
        btnGetUsbDevices.setOnClickListener {
            showToast("获取USB设备")
        }
    }
}