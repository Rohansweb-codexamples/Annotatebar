package com.rohan.tabletannotator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
  int dp(int n){ return (int)(n*getResources().getDisplayMetrics().density+.5f); }
  @Override public void onCreate(Bundle state) { super.onCreate(state);
    LinearLayout page=new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setGravity(Gravity.CENTER); page.setPadding(dp(32),dp(32),dp(32),dp(32)); page.setBackgroundColor(Color.rgb(247,247,251));
    TextView title=new TextView(this); title.setText("Annotate anywhere"); title.setTextSize(30); title.setTextColor(Color.rgb(35,35,55));
    TextView info=new TextView(this); info.setText("Start the overlay, then switch to any app and draw on top of it. Use the floating bar’s stop button when you are finished."); info.setTextSize(17); info.setPadding(0,dp(12),0,dp(28));
    Button start=new Button(this); start.setText("START ANNOTATION BAR"); start.setTextSize(16); start.setOnClickListener(v->startOverlay());
    page.addView(title); page.addView(info); page.addView(start,new LinearLayout.LayoutParams(-1,dp(58))); setContentView(page);
  }
  void startOverlay(){ if(!Settings.canDrawOverlays(this)){ startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()))); Toast.makeText(this,"Allow ‘Display over other apps’, then return and tap Start.",Toast.LENGTH_LONG).show(); return; } startForegroundService(new Intent(this,OverlayService.class)); moveTaskToBack(true); }
}
