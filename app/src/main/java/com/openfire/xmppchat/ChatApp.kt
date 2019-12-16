package com.openfire.xmppchat

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast

class ChatApp : Application() {
    override fun onCreate() {
        super.onCreate()
        registerComponentCallbacks(object : ComponentCallbacks2 {
            override fun onTrimMemory(level: Int) {
                Toast.makeText(applicationContext, "onTrimMemory", Toast.LENGTH_SHORT).show()
            }

            override fun onLowMemory() {
                Toast.makeText(applicationContext, "onLowMemory", Toast.LENGTH_SHORT).show()
            }

            override fun onConfigurationChanged(newConfig: Configuration) {
            }

        })
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                Toast.makeText(applicationContext, "Trminatee", Toast.LENGTH_SHORT).show()

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityResumed(activity: Activity) {
            }
        })
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
//        Toast.makeText(applicationContext, "Trim", Toast.LENGTH_SHORT).show()
    }

    override fun onTerminate() {
        super.onTerminate()
        Toast.makeText(applicationContext, "Trminatee", Toast.LENGTH_SHORT).show()
    }
}