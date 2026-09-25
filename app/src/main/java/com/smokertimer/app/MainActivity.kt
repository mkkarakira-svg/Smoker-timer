package com.smokertimer.app
import android.app.*
import android.os.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity: Activity(){
 lateinit var count:TextView; lateinit var timer:TextView; lateinit var last:TextView; lateinit var status:TextView
 var interval=60; var end=0L; var n=0; val h=Handler(Looper.getMainLooper())
 val prefs by lazy{getSharedPreferences("smoke",MODE_PRIVATE)}
 override fun onCreate(b:Bundle?){super.onCreate(b);n=prefs.getInt("count",0);end=prefs.getLong("end",0);interval=prefs.getInt("interval",60)
 if(Build.VERSION.SDK_INT>=33&&checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=PackageManager.PERMISSION_GRANTED)requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"),9)
 val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(36,36,36,20);setBackgroundColor(Color.rgb(247,247,250))}
 fun tv(t:String,s:Float)=TextView(this).apply{text=t;textSize=s;setTextColor(Color.rgb(25,25,30));gravity=Gravity.CENTER}
 count=tv(n.toString()+" cigarettes today",24f);root.addView(count);timer=tv("READY",38f);root.addView(timer,LinearLayout.LayoutParams(-1,180));last=tv("Last —",14f);root.addView(last)
 root.addView(Button(this).apply{text="🚬  I SMOKED";textSize=18f;setOnClickListener{smoked()}},LinearLayout.LayoutParams(-1,120))
 root.addView(tv("INTERVAL",13f));val row=LinearLayout(this);listOf(30,45,60,90).forEach{minutes->row.addView(Button(this).apply{text=minutes.toString()+"m";setOnClickListener{interval=minutes;prefs.edit().putInt("interval",minutes).apply();status.text="Interval: "+minutes+" min"}},LinearLayout.LayoutParams(0,100,1f))};root.addView(row)
 root.addView(Button(this).apply{text="↩ Undo";setOnClickListener{if(n>0)n--;prefs.edit().putInt("count",n).apply();count.text=n.toString()+" cigarettes today"}})
 status=tv("Ready",12f);root.addView(status);setContentView(root);tick()}
 fun smoked(){n++;val now=System.currentTimeMillis();end=now+interval*60000L;prefs.edit().putInt("count",n).putLong("end",end).apply();count.text=n.toString()+" cigarettes today";last.text="Last "+SimpleDateFormat("HH:mm",Locale.getDefault()).format(Date(now));schedule();status.text="✓ Logged"}
 fun schedule(){val am=getSystemService(ALARM_SERVICE) as AlarmManager;val pi=PendingIntent.getBroadcast(this,7,Intent(this,AlarmReceiver::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE);try{if(Build.VERSION.SDK_INT>=31&&am.canScheduleExactAlarms())am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,end,pi) else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,end,pi)}catch(e:Exception){am.set(AlarmManager.RTC_WAKEUP,end,pi)}}
 fun tick(){val d=end-System.currentTimeMillis();timer.text=if(end==0L)"READY" else if(d<=0)"🔔 NOW" else String.format(Locale.US,"%02d:%02d",d/60000,(d/1000)%60);h.postDelayed({tick()},1000)}
}