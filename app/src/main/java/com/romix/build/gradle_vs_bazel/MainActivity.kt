package com.romix.build.gradle_vs_bazel

import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private val appComponent by lazy {
        AppComponent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appComponent.feat1Config.loadSnapshot(1)
    }
}
