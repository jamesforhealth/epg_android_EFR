package com.flowehealth.efr_version.features.configure.advertiser.services

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.flowehealth.efr_version.features.configure.advertiser.fragments.AdvertiserFragment
import com.flowehealth.efr_version.features.configure.advertiser.utils.AdvertiserStorage
import com.flowehealth.efr_version.R

class AdvertiserService : Service() {
    companion object {
        private const val CHANNEL_ID = "ForegroundAdvertiserService"
        private const val ADVERTISER_NOTIFICATION_REQUEST_CODE = 127

        fun startService(context: Context) {
            val serviceIntent = Intent(context, AdvertiserService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }

        fun stopService(context: Context) {
            AdvertiserStorage(context).clearRunningAdvertisers()
            val serviceIntent = Intent(context, AdvertiserService::class.java)
            context.stopService(serviceIntent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(receiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = prepareNotification()
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(CHANNEL_ID, "Advertiser Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }

    private fun prepareNotification(): Notification {
        createNotificationChannel()
        val notificationIntent = Intent(this, AdvertiserFragment::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this, ADVERTISER_NOTIFICATION_REQUEST_CODE, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return Notification.Builder(this, CHANNEL_ID).apply {
            setContentTitle("EFR Connect")
            setContentText("Advertiser is running...")
            setSmallIcon(R.mipmap.efr_redesign_launcher)
            setContentIntent(pendingIntent)
            setShowWhen(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            }
        }.build()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

                when (state) {
                    BluetoothAdapter.STATE_OFF -> { context?.let {
                        AdvertiserStorage(it).clearRunningAdvertisers()
                        stopSelf()
                    } }
                }
            }
        }
    }
}