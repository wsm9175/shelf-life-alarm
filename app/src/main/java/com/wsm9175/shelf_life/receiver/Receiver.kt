package com.wsm9175.shelf_life.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.wsm9175.shelf_life.App
import com.wsm9175.shelf_life.R
import com.wsm9175.shelf_life.db.entity.FoodEntity
import com.wsm9175.shelf_life.repository.DBRepository
import com.wsm9175.shelf_life.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class Receiver : BroadcastReceiver(){
    private val TAG = BroadcastReceiver::class.java.simpleName
    private val dbRepository = DBRepository()

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")
        if(intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)){
            val receiver = ComponentName(context!!, Receiver::class.java)

            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            val alarmManager : AlarmManager = App.context().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(context, 0, Intent(context, Receiver::class.java), PendingIntent.FLAG_MUTABLE)
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 0)
            }

            Log.d(TAG, calendar.time.toString())

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_HALF_DAY, pendingIntent)
        }else{
            Log.d(TAG, "onReceive")
            getFoodItemByDate(LocalDate.now(), context)
        }
    }

    fun getFoodItemByDate(date: LocalDate, context: Context?) = CoroutineScope(Dispatchers.IO).launch{
        val foodEntityList = dbRepository.getFoodDataByShelfLife(date)
        if(foodEntityList.size > 0){
            makeNotification(foodEntityList, context)
        }
    }

    fun makeNotification(foodEntityList: List<FoodEntity>, context: Context?){
        Log.d(TAG, "makeNotification")
        val title = "유통기한 알리미"
        var text : StringBuilder = StringBuilder()
        for (foodEntity in foodEntityList){
            text.append(foodEntity.name).append(" ")
        }
        var nameList = text.trim().toString()

        val content = nameList + "의 유통기한을 확인 해 주세요"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = context?.let {
            NotificationCompat.Builder(it, "CHANNEL_ID")
                .setSmallIcon(R.drawable.baseline_alarm_on_24)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
        }
        val notificationManager: NotificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "descriptionText"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, builder!!.build())


    }

    private fun registerAlarm(){

    }
}