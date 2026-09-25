package com.smokertimer.app
import android.app.*
import android.os.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.provider.Settings
import android.view.Gravity
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity: Activity(){
 lateinit var count:TextView; lateinit var timer:TextView; lateinit var last:TextView; lateinit var status:TextView
 var interval=60; var end=0L; var n=0; var lastMs=0L; val h=Handler(Looper.getMainLooper())
 val prefs by lazy{getSharedPreferences("smoke",MODE_PRIVATE)}
 override fun onCreate(b:Bundle?){super.onCreate(b);val today=SimpleDateFormat("yyyyMMdd",Locale.US).format(Date());if(prefs.getString("day","")!=today){val old=prefs.getInt("count",0);val month=SimpleDateFormat("yyyyMM",Locale.US).format(Date());prefs.edit().putInt("month_"+month,prefs.getInt("month_"+month,0)+old).putInt("count",0).putString("day",today).putLong("end",0).putLong("last",0).apply();}n=prefs.getInt("count",0);end=prefs.getLong("end",0);lastMs=prefs.getLong("last",0);interval=prefs.getInt("interval",60)
 if(Build.VERSION.SDK_INT>=31){val am=getSystemService(ALARM_SERVICE) as AlarmManager;if(!am.canScheduleExactAlarms())try{startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))}catch(_:Exception){}}
 if(Build.VERSION.SDK_INT>=33&&checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=PackageManager.PERMISSION_GRANTED)requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"),9)
 val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(36,36,36,20);setBackgroundColor(Color.rgb(247,247,250))}
 fun tv(t:String,s:Float)=TextView(this).apply{text=t;textSize=s;setTextColor(Color.rgb(25,25,30));gravity=Gravity.CENTER}
 root.addView(tv("SMOKER TIMER",14f));val monthKey="month_"+SimpleDateFormat("yyyyMM",Locale.US).format(Date());val monthTotal=prefs.getInt(monthKey,0)+n;root.addView(tv("This month: "+monthTotal+" cigarettes",14f));count=tv(n.toString()+" cigarettes today",22f);root.addView(count);timer=tv("READY",42f);timer.background=GradientDrawable().apply{setColor(Color.WHITE);cornerRadius=160f};root.addView(timer,LinearLayout.LayoutParams(360,190));last=tv(if(lastMs>0)"Last "+SimpleDateFormat("HH:mm",Locale.getDefault()).format(Date(lastMs)) else "Last —",14f);root.addView(last)
 root.addView(Button(this).apply{text="🚬  I SMOKED";textSize=18f;setOnClickListener{smoked()}},LinearLayout.LayoutParams(-1,120))
 root.addView(tv("INTERVAL",13f));val row=LinearLayout(this);listOf(30,45,60,90).forEach{minutes->row.addView(Button(this).apply{text=minutes.toString()+"m";setOnClickListener{interval=minutes;prefs.edit().putInt("interval",minutes).apply();status.text="Interval: "+minutes+" min"}},LinearLayout.LayoutParams(0,100,1f))};root.addView(row)
 val actions=LinearLayout(this);actions.addView(Button(this).apply{text="↩ UNDO";setOnClickListener{if(n>0)n--;end=0;lastMs=0;(getSystemService(ALARM_SERVICE) as AlarmManager).cancel(PendingIntent.getBroadcast(this@MainActivity,7,Intent(this@MainActivity,AlarmReceiver::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE));prefs.edit().putInt("count",n).putLong("end",0).putLong("last",0).apply();count.text=n.toString()+" cigarettes today";last.text="Last —"}},LinearLayout.LayoutParams(0,90,1f));actions.addView(Button(this).apply{text="NEW DAY";setOnClickListener{n=0;end=0;lastMs=0;prefs.edit().putInt("count",0).putLong("end",0).putLong("last",0).apply();count.text="0 cigarettes today";last.text="Last —";status.text="New day started"}},LinearLayout.LayoutParams(0,90,1f));root.addView(actions)
status=tv("Ready",12f);root.addView(status);setContentView(root);tick()}
 fun smoked(){n++;val now=System.currentTimeMillis();end=now+interval*60000L;prefs.edit().putInt("count",n).putLong("end",end).putLong("last",now).apply();count.text=n.toString()+" cigarettes today";last.text="Last "+SimpleDateFormat("HH:mm",Locale.getDefault()).format(Date(now));schedule();status.text="✓ Logged"}
 fun schedule(){val am=getSystemService(ALARM_SERVICE) as AlarmManager;val pi=PendingIntent.getBroadcast(this,7,Intent(this,AlarmReceiver::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE);try{if(Build.VERSION.SDK_INT>=31&&am.canScheduleExactAlarms())am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,end,pi) else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,end,pi)}catch(e:Exception){am.set(AlarmManager.RTC_WAKEUP,end,pi)}}
 fun tick(){val d=end-System.currentTimeMillis();timer.text=if(end==0L)"READY" else if(d<=0)"🔔 NOW" else String.format(Locale.US,"%02d:%02d",d/60000,(d/1000)%60);h.postDelayed({tick()},1000)}
}