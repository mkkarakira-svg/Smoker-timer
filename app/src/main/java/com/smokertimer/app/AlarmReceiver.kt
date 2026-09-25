package com.smokertimer.app
import android.app.*
import android.content.*
import android.media.RingtoneManager
import android.os.Build
class AlarmReceiver: BroadcastReceiver(){
 override fun onReceive(c:Context,i:Intent){
  val nm=c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  val sound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
  if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(NotificationChannel("smoke_alarm_v2","Smoker Timer Alarm",NotificationManager.IMPORTANCE_HIGH).apply{enableVibration(true);vibrationPattern=longArrayOf(0,700,300,700,300,1200);setSound(sound,android.media.AudioAttributes.Builder().setUsage(android.media.AudioAttributes.USAGE_ALARM).build())})
  val open=PendingIntent.getActivity(c,8,Intent(c,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  val b=if(Build.VERSION.SDK_INT>=26)Notification.Builder(c,"smoke_alarm_v2") else Notification.Builder(c).setSound(sound).setVibrate(longArrayOf(0,700,300,700))
  nm.notify(7,b.setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("Smoker Timer 🔔").setContentText("Your interval is finished").setContentIntent(open).setAutoCancel(true).setPriority(Notification.PRIORITY_MAX).build())
 }
}