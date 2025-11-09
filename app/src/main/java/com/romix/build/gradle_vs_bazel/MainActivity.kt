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
        appComponent.feat2Config.loadSnapshot(1)
        appComponent.feat3Config.loadSnapshot(1)
        appComponent.feat4Config.loadSnapshot(1)
        appComponent.feat5Config.loadSnapshot(1)
        appComponent.feat6Config.loadSnapshot(1)
        appComponent.feat7Config.loadSnapshot(1)
        appComponent.feat8Config.loadSnapshot(1)
        appComponent.feat9Config.loadSnapshot(1)
        appComponent.feat10Config.loadSnapshot(1)
    }
}
