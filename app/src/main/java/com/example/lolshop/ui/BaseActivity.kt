package com.example.lolshop.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bỏ giới hạn layout để nội dung tràn lên thanh trạng thái nếu cần
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Làm thanh trạng thái trong suốt
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }

        // Thay đổi giao diện thanh trạng thái để biểu tượng và văn bản có màu tối
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
    }
}
