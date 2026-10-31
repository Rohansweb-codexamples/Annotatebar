package com.rohan.tabletannotator;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.view.*;
import android.widget.*;
import java.util.*;

public class OverlayService extends Service {
  WindowManager wm; FrameLayout overlay; int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
  @Override public IBinder onBind(Intent i){return null;}
  @Override public void onCreate(){super.onCreate(); startForeground(7,notification()); wm=(WindowManager)getSystemService(WINDOW_SERVICE); buildOverlay();}
  Notification notification(){ String id="annotation_overlay"; NotificationChannel c=new NotificationChannel(id,"Annotation bar",NotificationManager.IMPORTANCE_LOW); ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c); return new Notification.Builder(this,id).setContentTitle("Annotation bar is active").setContentText("Tap the app to stop or change tools.").setSmallIcon(android.R.drawable.ic_menu_edit).build(); }
  void buildOverlay(){ overlay=new FrameLayout(this); overlay.setBackgroundColor(Color.TRANSPARENT); Ink ink=new Ink(this); overlay.addView(ink,new FrameLayout.LayoutParams(-1,-1));
    LinearLayout bar=new LinearLayout(this); bar.setGravity(Gravity.CENTER); bar.setPadding(dp(8),dp(7),dp(8),dp(7)); GradientDrawable bg=new GradientDrawable(); bg.setColor(Color.WHITE); bg.setCornerRadius(dp(22)); bg.setStroke(1,0x24000000); bar.setBackground(bg); bar.setElevation(dp(9));
    add(bar,"✎","Pen",v->{ink.erase=false;ink.width=dp(4);}); add(bar,"▰","Marker",v->{ink.erase=false;ink.width=dp(14);}); add(bar,"⌫","Eraser",v->{ink.erase=true;ink.width=dp(28);}); add(bar,"●","Purple",v->{ink.erase=false;ink.color=Color.rgb(91,91,214);}); add(bar,"●","Blue",v->{ink.erase=false;ink.color=Color.rgb(33,150,243);}); add(bar,"↶","Undo",v->ink.undo()); add(bar,"✕","Stop",v->stopSelf());
    FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL); bp.setMargins(dp(12),0,dp(12),dp(24)); overlay.addView(bar,bp);
    WindowManager.LayoutParams p=new WindowManager.LayoutParams(-1,-1,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT); wm.addView(overlay,p);
  }
  void add(LinearLayout bar,String icon,String desc,View.OnClickListener click){ TextView b=new TextView(this); b.setText(icon); b.setTextSize(22); b.setTextColor(Color.DKGRAY); b.setGravity(Gravity.CENTER); b.setContentDescription(desc); b.setOnClickListener(click); bar.addView(b,new LinearLayout.LayoutParams(dp(47),dp(47))); }
  @Override public void onDestroy(){if(overlay!=null)wm.removeView(overlay);super.onDestroy();}
  class Ink extends View { class Stroke {Path p=new Path(); Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);} ArrayList<Stroke> all=new ArrayList<>(); Stroke active; int color=Color.rgb(91,91,214),width=12; boolean erase=false; Ink(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);} protected void onDraw(Canvas c){for(Stroke s:all)c.drawPath(s.p,s.paint);} public boolean onTouchEvent(MotionEvent e){float x=e.getX(),y=e.getY();if(e.getAction()==0){active=new Stroke();active.p.moveTo(x,y);active.paint.setStyle(Paint.Style.STROKE);active.paint.setStrokeCap(Paint.Cap.ROUND);active.paint.setStrokeJoin(Paint.Join.ROUND);active.paint.setStrokeWidth(width);active.paint.setColor(erase?Color.TRANSPARENT:color);if(erase)active.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));all.add(active);}else if(e.getAction()==2||e.getAction()==1)active.p.lineTo(x,y);invalidate();return true;} void undo(){if(!all.isEmpty())all.remove(all.size()-1);invalidate();}}
}
