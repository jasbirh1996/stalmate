package com.stalmate.user.modules.contactSync

import android.app.Service
import android.content.Context
import android.content.Intent

import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import com.stalmate.user.base.App


class SyncService : Service() {
    override
    fun onCreate() {
        Log.d("Sync Service created.","sdfsfd")
        synchronized(sSyncAdapterLock) {
            if (mSyncAdapter == null) {
                mSyncAdapter = SyncAdapter(
                    App.getInstance().applicationContext,
                    true
                )
            }
        }
    }

    @Nullable
    override
    fun onBind(intent: Intent?): IBinder {
        Log.d("Sync Service created.","sdasdadfsfd")
        return mSyncAdapter!!.syncAdapterBinder
    }

    companion object {
        private val sSyncAdapterLock = Any()
        private var mSyncAdapter: SyncAdapter? = null
    }
}