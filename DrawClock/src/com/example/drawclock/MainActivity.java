package com.example.drawclock;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener{
	
	//constant
	final int SENSOR_FREQ=1;
	private final int GRAVITY_RECIEVE=1;
	final String Tag="stickroundpanel";
	
	//variable
	SensorManager sensormanager;
//	StickRoundPanel stickroundpanel;
	MySurfaceView stickroundpanel;
//	ClockView stickroundpanel;
	
	boolean aFilterFlag=true;
	float alpha1=0.9f;
	
	int accuracy=0;
	int frequency=0;
	float[] values={0f,0f,0f};
	int[] isScan=new int[180];
	
	
	Timer timer=null;
//	TimerTask crontab=null;
	Handler uihandler;
	Handler surfacehandler;
//	int count1=0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	setContentView(R.layout.activity_main);
//		Resources res = getResources(); 
//		Drawable drawable = res.getDrawable(R.drawable.bkgcolor);
//		this.getWindow().setBackgroundDrawable(drawable);
		isScan[45]=1;
		isScan[90]=1;
		
		Log.d(Tag, "0000000000000 in main"+Thread.currentThread().getName());
//		stickroundpanel=new ClockView(this);
//		stickroundpanel =new StickRoundPanel(this);
		stickroundpanel=new MySurfaceView(this);
		Log.d(Tag,"11111111111111111" + "before setcontentview");
		setContentView(stickroundpanel);
		
		Log.d(Tag,"222222222222222222" + "after setcontentview");
		
		sensormanager=(SensorManager) getSystemService(SENSOR_SERVICE);
		
		uihandler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Bundle b=msg.getData();
				switch (msg.what){
					case SENSOR_FREQ:
						Log.d("crontab", "====> sensor frequency is : " + b.getInt("sensor_freq"));
						
						break;
				}
			}
			
		};
		
		crontabTask();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(Tag,"33333333333333 on resume ,register sensor");
		sensormanager.registerListener(this, sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
		sensormanager.registerListener(this, sensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensormanager.unregisterListener(this);
		finish();
		timer.cancel();
		surfacehandler.removeMessages(GRAVITY_RECIEVE);
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
			paint.setTextSize(20);
			paint.setStrokeWidth(3);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setAntiAlias(true);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			paint.setColor(Color.WHITE);
			canvas.drawColor(Color.BLACK);
			canvas.translate(canvas.getWidth()/2, canvas.getHeight()/2);
			
			float y=200;
			int count=180;
			
			if(values != null){
//				String text="x:"+String.valueOf(values[0])+"\n"+"y:"+String.valueOf(values[1])+"\n"+"z:"+String.valueOf(values[2]);
				canvas.drawText("x:"+String.valueOf(values[0]), 0f, 0f, paint);
				canvas.drawText("y:"+String.valueOf(values[1]), 0f, 20f, paint);
				canvas.drawText("z:"+String.valueOf(values[2]), 0f, 40f, paint);
				canvas.drawText("accuracy:" + String.valueOf(accuracy), 0f, 60f, paint);
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
							if(isScan[index-1] == 0)
								isScan[index-1]=1;
						if(index < 179)
							if(isScan[index+1] == 0)
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
				if(i == index){
					paint.setColor(Color.RED);
					canvas.drawCircle(0, y-20, 20, paint);
				}
				
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
		Log.d(Tag,"44444444444444444" + "sensor detect");
		
		switch(arg0.sensor.getType()){
			case Sensor.TYPE_ACCELEROMETER:
				Log.d(Tag,"GRAVITY val :" + "x:" + values[0] + "y:" + values[1] + "z:" + values[2]);
                if(aFilterFlag == true)
                {
                	values=arg0.values.clone();
                	aFilterFlag = false;
                	Log.d(Tag,"Iamhere");
                }
	            values[0] = alpha1 * values[0] + (1 - alpha1) * arg0.values[0];
	            values[1] = alpha1 * values[1] + (1 - alpha1) * arg0.values[1];
	            values[2] = alpha1 * values[2] + (1 - alpha1) * arg0.values[2];
	            Log.d(Tag, "aaaaaaaaaaaaaa send data" + "x:" + values[0] + "y:" + values[1] + "z:" + values[2]);
//				stickroundpanel.invalidate();
				frequency++;// this is not unsynchronized due to crontab clear,
				//is it necessary to add a synchronized method ?
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:			
				accuracy=arg0.accuracy;
				break;
			default:
				break;
		}
		if(stickroundpanel.detect_flag == true){
			Log.d(Tag,"99999999 detect_flag is set" + surfacehandler);
			Bundle b =new Bundle();
			b.putFloatArray("gravity", values);
			b.putInt("accuracy", accuracy);
			Message message=new Message();
			message.setData(b);
			message.what=GRAVITY_RECIEVE;
			surfacehandler=stickroundpanel.myhandler;
			surfacehandler.sendMessage(message);
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
	
	private void crontabTask(){
		timer=new Timer(true);
		TimerTask crontab=new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message=new Message();
				Bundle bundle=new Bundle();
				bundle.putInt("sensor_freq", frequency);
				frequency=0;
				message.setData(bundle);
				message.what=SENSOR_FREQ;
				uihandler.sendMessage(message);
			}
		};
//		TimerTask detectdelay=new TimerTask(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				detect_flag=true;
//				surfacehandler=stickroundpanel.myhandler;
//			}
//			
//		};
		timer.schedule(crontab,1000,1000);
//		timer.schedule(detectdelay, 500);
	}
}
