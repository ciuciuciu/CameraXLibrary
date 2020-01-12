package com.ciuciu.camerax.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ciuciu.camerax.ui.CameraFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, CameraFragment.newInstance())
            .commit()
    }
}
