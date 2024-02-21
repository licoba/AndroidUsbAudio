package com.moriafly.usbaudio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.moriafly.usbaudio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.initListener()
    }

    fun ActivityMainBinding.initListener() {
        btnGetUsbDevices.setOnClickListener {
            showToast("获取USB设备")
        }
    }
}