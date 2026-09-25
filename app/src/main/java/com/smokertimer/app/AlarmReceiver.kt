package com.smokertimer.app
import android.app.*
import android.content.*
import android.os.Build
class AlarmReceiver: BroadcastReceiver(){
 override fun onReceive(c:Context,i:Intent){
  val nm=c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  if(Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(NotificationChannel("smoke","Smoke Timer",NotificationManager.IMPORTANCE_HIGH).apply{enableVibration(true)})
  val b=if(Build.VERSION.SDK_INT>=26) Notification.Builder(c,"smoke") else Notification.Builder(c)
  nm.notify(7,b.setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("Smoker Timer").setContentText("Time for your next cigarette").setAutoCancel(true).build())
 }
}