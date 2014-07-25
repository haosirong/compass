package com.example.drawclock;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener{
	
	SensorManager sensormanager;
	StickRoundPanel stickroundpanel;
	int accuracy=0;
	float[] values=null;
	int[] isScan=new int[180];
	String Tag="stickroundpanel";
//	int count1=0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	setContentView(R.layout.activity_main);
		isScan[45]=1;
		isScan[90]=1;
		
		
		stickroundpanel =new StickRoundPanel(this);
		setContentView(stickroundpanel);
		sensormanager=(SensorManager) getSystemService(SENSOR_SERVICE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensormanager.registerListener(this, sensormanager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);
		sensormanager.registerListener(this, sensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensormanager.unregisterListener(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class ClockView extends View{
		Paint paint;    
		
		public ClockView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			paint = new Paint(); //设置一个笔刷大小是3的黄色的画笔    
            paint.setColor(Color.YELLOW);    
            paint.setStrokeJoin(Paint.Join.ROUND);    
            paint.setStrokeCap(Paint.Cap.ROUND);    
            paint.setStrokeWidth(3);    
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
//			super.onDraw(canvas);
		    paint.setAntiAlias(true);    
		    paint.setStyle(Style.STROKE);  
		    canvas.drawColor(Color.BLACK);
		    canvas.translate(canvas.getWidth()/2, canvas.getHeight()/2); //将位置移动画纸的坐标点:150,150    
		    canvas.drawCircle(0, 0, 100, paint); //画圆圈    
		          
		    //使用path绘制路径文字    
		    canvas.save();    
		    canvas.translate(-150, -150);    
		    Path path = new Path();
		    path.addArc(new RectF(0,0,300,300), -180, 180);    
		    canvas.drawRect(new RectF(0,0,300,300), paint);
		    Paint citePaint = new Paint(paint);    
		    citePaint.setTextSize(20);    
		    citePaint.setStrokeWidth(1);    
		    canvas.drawPath(path, paint);
		    canvas.drawTextOnPath("http://www.android777.com", path, 100, 0, citePaint);    
		    canvas.restore();    
		          
		    Paint tmpPaint = new Paint(paint); //小刻度画笔对象    
		    tmpPaint.setStrokeWidth(1);    
		    tmpPaint.setTextSize(40);
		          
		    float  y=200;    
		    int count = 60; //总刻度数    
		          
		    for(int i=0 ; i <count ; i++){    
		        if(i%5 == 0){    
		            canvas.drawLine(0f, y, 0, y+36f, paint);    
		            canvas.drawText(String.valueOf(i/5+1), -16f, y+80f, tmpPaint);    
		          
		        }else{    
		            canvas.drawLine(0f, y, 0f, y +5f, tmpPaint);    
		        }    
		        canvas.rotate(360/count,0f,0f); //旋转画纸    
		    }    
		          
		    //绘制指针    
		    tmpPaint.setColor(Color.GRAY);    
		    tmpPaint.setStrokeWidth(4);    
		    canvas.drawCircle(0, 0, 7, tmpPaint);    
		    tmpPaint.setStyle(Style.FILL);    
		    tmpPaint.setColor(Color.YELLOW);    
		    canvas.drawCircle(0, 0, 5, tmpPaint);    
		    canvas.drawLine(0, 10, 0, -65, paint);  
		    canvas.drawText("ABC", 0, 0, tmpPaint);
		}
		
		
	}	
		
	class StickRoundPanel extends View{
		Paint paint;
		public StickRoundPanel(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			paint=new Paint();
			paint.setStrokeWidth(3);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setAntiAlias(true);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
//			paint.setColor(Color.GRAY);
			canvas.drawColor(Color.BLACK);
			canvas.translate(canvas.getWidth()/2, canvas.getHeight()/2);
			
			float y=200;
			int count=180;
			
			if(values != null){
//				String text="x:"+String.valueOf(values[0])+"\n"+"y:"+String.valueOf(values[1])+"\n"+"z:"+String.valueOf(values[2]);
				canvas.drawText("x:"+String.valueOf(values[0]), 0f, 0f, paint);
				canvas.drawText("y:"+String.valueOf(values[1]), 0f, 12f, paint);
				canvas.drawText("z:"+String.valueOf(values[2]), 0f, 24f, paint);
				canvas.drawText("accuracy:" + String.valueOf(accuracy), 0f, 36f, paint);
			}
			
//			count1++;
//			if(count1 == 180)count1=0;
//			
//			isScan[count1]=1;
			
			int degree=getDegree(values[0], values[1]);
			int degreeZ=calcAngleZ(values[0], values[1], values[2]);
			int index=-1;
			if(degree != -1){
				degree=transformDegree(degree);
				index=(int) Math.floor(degree/2);
				Log.d(Tag, "=====> index is " + index + "=====> degree is " + degree + "=====> degreeZ is " + degreeZ);
				if(isScan[index] == 0){
					if(degreeZ < 30){
						isScan[index]=1;
						if(index > 0)
							isScan[index-1]=1;
						if(index < 179)
							isScan[index+1]=1;
					}
					else{
						isScan[index]=2;
						if(index > 0)
							isScan[index-1]=2;
						if(index < 179)
							isScan[index+1]=2;
					}
				}
				if(isScan[index] == 1){
					if(degreeZ >= 30){
						isScan[index]=2;
						if(index > 0)
							isScan[index-1]=2;
						if(index < 179)
							isScan[index+1]=2;				
					}
				}
			}
//			canvas.drawCircle(-5, y, 5, paint);
			for(int i=0;i<count;i++){
				if(isScan[i] == 0){
					paint.setColor(Color.GRAY);
					canvas.drawLine(0f, y, 0f, y+80, paint);
				}
				else{
					paint.setColor(Color.WHITE);
					if(isScan[i] == 1){
						canvas.drawLine(0f, y, 0f, y+40, paint);
						paint.setColor(Color.GRAY);
						canvas.drawLine(0f, y+40, 0f, y+80, paint);
					}
					if(isScan[i] == 2)
						canvas.drawLine(0f, y, 0f, y+80, paint);
				}
//				if(i == index)
//					paint.setColor(Color.RED);
//					canvas.drawCircle(-20, y-20, 20, paint);
				
				canvas.rotate(360/count, 0f, 0f);
			}
//			isScan[count1]=0;
		}
		
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		switch(arg0.sensor.getType()){
			case Sensor.TYPE_GRAVITY:
				values=arg0.values;
				stickroundpanel.invalidate();
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:			
				accuracy=arg0.accuracy;
				break;
			default:
				break;
		}
	}
	/***
	 *@return degree from 0 to 360 according to
	 *arctan2(x/y).
	 */
	public int getDegree(float x,float y){
		if(x == 0 && y == 0)
			return -1;
		
		double angle=Math.atan2(y,x);
		angle=Math.toDegrees(angle);
			if(angle < 0)
				angle += 360;
		return (int) Math.floor(angle);
	}

	/***
	 * 
	 * 	0~90->90~0

		90~180->360~270

		180~270->270~180

		270~360->180~90
	 * @param degree
	 * @return
	 */
	public int transformDegree(int degree){
//		if((0 <= degree && degree < 90) || ( 180 <= degree && degree < 270))
//			degree+=90;
//		else
//			degree-=90;
//		return degree;
		if(0 <= degree && degree <90)
			degree=90-degree;
		else
			degree=450-degree;
		if(degree == 360)
			degree=0;
		
		return degree;
	}
	
	public int calcAngleZ(float x,float y,float z){
		Vector3D zaxis=new Vector3D(0f,0f,1.0f);
		Vector3D direct=new Vector3D(x,y,z);
		float angle=direct.angle(zaxis);
		
		return (int) Math.floor(Math.toDegrees(angle));
	}
}
