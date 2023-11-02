package com.example.zxingwithroundedscanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class SinglePermissionObserver(
    private val owner: LifecycleOwner,
    private val permission: String,
    private val onDismiss: (() -> Unit)? = null,
    private val onDenied: (() -> Unit)? = null,
    private val onPermanentlyDenied: (() -> Unit)? = null,
    private val onGranted: (() -> Unit)? = null
) : DefaultLifecycleObserver {

    private val activity by lazy {
        when (owner) {
            is FragmentActivity -> {
                owner
            }

            is Fragment -> {
                owner.activity
            }

            else -> {
                null
            }
        }
    }

    init {
        when (owner) {
            is FragmentActivity -> {
                owner.lifecycle.addObserver(this)
            }

            is Fragment -> {
                owner.lifecycle.addObserver(this)
            }
        }
    }

    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var deniedPermanentlyCount = 0

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        if (!hasPermissions(activity!!, permission) &&
            !shouldShowRequestPermissionRationale(activity!!, permission)
        ) {
            deniedPermanentlyCount++
        }
        requestPermissionLauncher =
            activity?.activityResultRegistry?.register("$permission ${owner::class.simpleName} ${System.currentTimeMillis()}",
                owner,
                ActivityResultContracts.RequestPermission(),
                ActivityResultCallback<Boolean> { isGranted: Boolean ->
                    if (isGranted) {
                        deniedPermanentlyCount = 0
                        onGranted?.invoke()
                        onDismiss?.invoke()
                    } else {
                        if (!shouldShowRequestPermissionRationale(activity!!, permission)) {
                            deniedPermanentlyCount++
                            if (deniedPermanentlyCount > 1) {
                                onPermanentlyDenied?.invoke()
                                    ?: onDismiss?.invoke()
                                    ?: onDismiss?.invoke()
                                return@ActivityResultCallback
                            }
                        } else {
                            deniedPermanentlyCount = 0
                        }

                        onDenied?.invoke()
                        onDismiss?.invoke()
                    }
                })
    }

    override fun onDestroy(owner: LifecycleOwner) {
        requestPermissionLauncher?.unregister()
        super.onDestroy(owner)
    }

    fun requestPermission() {
        requestPermissionLauncher?.launch(permission)
    }
}

fun hasPermissions(context: Context, vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }

    return true
}

fun Context.hasCameraPermission(): Boolean {
    return hasPermissions(this, Manifest.permission.CAMERA)
}
