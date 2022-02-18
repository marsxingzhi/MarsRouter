package com.mars.infra.router.runtime.builder

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Created by JohnnySwordMan on 2/18/22
 */
object ActivityBuilder {

    private var mActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            // 不是所有的Activity都有Builder，这里加上try-catch
            try {
                // 反射调用LoginActivityBuilder中的inject方法
                val className = activity.componentName.className+"Builder"
                val cls = Class.forName(className)
                val mInjectMethod = cls.getDeclaredMethod("inject", Activity::class.java, Bundle::class.java)
                mInjectMethod.invoke(null, activity, savedInstanceState)
            } catch (e: Exception) {

            }
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

    }

    fun init(context: Context) {
        val application = context.applicationContext as Application
        application.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

    fun startActivity(context: Context, intent: Intent) {
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}