package com.rohan.tabletannotator;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
  InkView canvas;
  int purple = Color.rgb(91,91,214);
  @Override public void onCreate(Bundle state) { super.onCreate(state);
    FrameLayout root = new FrameLayout(this); root.setBackgroundColor(Color.rgb(247,247,251));
    canvas = new InkView(this); root.addView(canvas, new FrameLayout.LayoutParams(-1,-1));
    TextView hint = new TextView(this); hint.setText("Write, draw, and mark up ideas"); hint.setTextSize(18); hint.setTextColor(Color.DKGRAY); hint.setPadding(dp(24),dp(18),0,0);
    root.addView(hint, new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.START));
    LinearLayout bar = new LinearLayout(this); bar.setGravity(Gravity.CENTER); bar.setOrientation(LinearLayout.HORIZONTAL); bar.setPadding(dp(8),dp(6),dp(8),dp(6));
    GradientDrawable bg = new GradientDrawable(); bg.setColor(Color.WHITE); bg.setCornerRadius(dp(22)); bg.setStroke(1,0x18000000); bar.setBackground(bg); bar.setElevation(dp(8));
    addButton(bar,"✎", "Pen", v -> { canvas.erase=false; canvas.width=dp(4); });
    addButton(bar,"▰", "Marker", v -> { canvas.erase=false; canvas.width=dp(14); });
    addButton(bar,"⌫", "Eraser", v -> { canvas.erase=true; canvas.width=dp(28); });
    addButton(bar,"●", "Purple", v -> canvas.color=purple);
    addButton(bar,"●", "Blue", v -> canvas.color=Color.rgb(33,150,243));
    addButton(bar,"●", "Coral", v -> canvas.color=Color.rgb(244,100,90));
    addButton(bar,"↶", "Undo", v -> canvas.undo());
    addButton(bar,"✕", "Clear", v -> new AlertDialog.Builder(this).setTitle("Clear annotations?").setMessage("This removes all marks on this page.").setNegativeButton("Keep",null).setPositiveButton("Clear",(d,w)->canvas.clear()).show());
    FrameLayout.LayoutParams bp = new FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL); bp.setMargins(dp(16),0,dp(16),dp(24)); root.addView(bar,bp);
    setContentView(root);
  }
  void addButton(LinearLayout bar,String text,String label,View.OnClickListener action) { TextView b=new TextView(this); b.setText(text); b.setTextSize(23); b.setTextColor(Color.DKGRAY); b.setGravity(Gravity.CENTER); b.setContentDescription(label); b.setBackground(ripple()); b.setOnClickListener(action); bar.addView(b,new LinearLayout.LayoutParams(dp(48),dp(48))); }
  Drawable ripple(){ GradientDrawable g=new GradientDrawable(); g.setColor(Color.TRANSPARENT); g.setCornerRadius(dp(18)); return g; }
  int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}

  public static class InkView extends View {
    static class Stroke { Path path=new Path(); Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG); }
    ArrayList<Stroke> strokes=new ArrayList<>(); Stroke active; int color=Color.rgb(91,91,214),width=12; boolean erase=false;
    InkView(android.content.Context c){super(c); setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    protected void onDraw(Canvas c){ super.onDraw(c); for(Stroke s:strokes)c.drawPath(s.path,s.paint); }
    public boolean onTouchEvent(android.view.MotionEvent e){ float x=e.getX(),y=e.getY(); if(e.getAction()==MotionEvent.ACTION_DOWN){active=new Stroke(); active.path.moveTo(x,y); active.paint.setStyle(Paint.Style.STROKE); active.paint.setStrokeCap(Paint.Cap.ROUND); active.paint.setStrokeJoin(Paint.Join.ROUND); active.paint.setStrokeWidth(width); active.paint.setColor(erase?Color.TRANSPARENT:color); if(erase)active.paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)); strokes.add(active); invalidate(); return true;} if(e.getAction()==MotionEvent.ACTION_MOVE){active.path.lineTo(x,y); invalidate(); return true;} if(e.getAction()==MotionEvent.ACTION_UP){active.path.lineTo(x,y); invalidate(); return true;} return true; }
    void undo(){if(!strokes.isEmpty()){strokes.remove(strokes.size()-1);invalidate();}} void clear(){strokes.clear();invalidate();}
  }
}
