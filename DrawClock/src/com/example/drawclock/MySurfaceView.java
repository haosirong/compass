package com.example.drawclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	private final String Tag="stickroundsurface";
	private final int GRAVITY_RECIEVE=1;
	
	private float[] values=new float[3];
	private int[] isScan=new int[180];
	private int accuracy=0;
//	private int frequency=0;
	
	private SurfaceThread mythread=null;
	private SurfaceHolder holder=null;
	public Handler myhandler=null;
	public boolean detect_flag=false;
	
	public MySurfaceView(Context context){
		super(context);
		// TODO Auto-generated constructor stub
		holder=getHolder();
		holder.addCallback(this);
		mythread=new SurfaceThread(holder);
		Log.d(Tag, "0000000000000 in surface"+Thread.currentThread().getName());
		
	}
	
	
//	@Override
//	public void draw(Canvas canvas) {
//		// TODO Auto-generated method stub
//		super.draw(canvas);
//		canvas.drawColor(Color.BLACK);
//	}


	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Log.d(Tag,"66666666666666 in surface changed"+Thread.currentThread().getName());
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.d(Tag, "555555555555555 surface create,thread ready start"+Thread.currentThread().getName());
		mythread.setDaemon(true);
		mythread.start();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mythread.isRun=false;
		Log.d(Tag, "bbbbbbbbbbbbbb LALALA");
		
	}
	
	class SurfaceThread extends Thread{
		private SurfaceHolder holder;
		public boolean isRun=true;
		
		SurfaceThread(SurfaceHolder holder){
			this.holder=holder;
			isRun=true;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			super.run();
//			Looper looper;
			
			Looper.prepare();
//			looper=Looper.myLooper();
			myhandler=new Handler(){

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					switch(msg.what){
						case GRAVITY_RECIEVE:
							
							Bundle b=new Bundle();
							b=msg.getData();
							values=b.getFloatArray("gravity");
							accuracy=b.getInt("accuracy");
							Log.d(Tag, "aaaaaaaaaaaaaa recieve data" + "x:" + values[0] + "y:" + values[1] + "z:" + values[2]);
							//if(isRun == true)
								Draw();
							break;
					}
				}
				
			};
			detect_flag=true;
			Log.d(Tag, "777777777777 begin loop"+Thread.currentThread().getName());
			Looper.loop();
			Log.d(Tag, "88888888888888 after loop"+Thread.currentThread().getName());
			
		}
		
		
		private void Draw(){
			Canvas canvas = null;
			synchronized (holder) {
				canvas = holder.lockCanvas();
				if(canvas != null){ 

				Paint paint = new Paint();
				
				paint.setTextSize(20);
				paint.setStrokeWidth(3);// £¡£¡£¡ this will affect the animation effect
//				paint.setStrokeCap(Paint.Cap.ROUND);
//				paint.setStrokeJoin(Paint.Join.ROUND);
				paint.setAntiAlias(true);
				paint.setColor(Color.WHITE);
				canvas.drawColor(Color.BLACK);
				canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);

				float y = 200;
				int count = 180;

				if (values != null) {
					// String
					// text="x:"+String.valueOf(values[0])+"\n"+"y:"+String.valueOf(values[1])+"\n"+"z:"+String.valueOf(values[2]);
					canvas.drawText("x:" + String.valueOf(values[0]), 0f, 0f, paint);
					canvas.drawText("y:" + String.valueOf(values[1]), 0f, 20f,
							paint);
					canvas.drawText("z:" + String.valueOf(values[2]), 0f, 40f,
							paint);
					canvas.drawText("accuracy:" + String.valueOf(accuracy), 0f,
							60f, paint);
				}

				// count1++;
				// if(count1 == 180)count1=0;
				//
				// isScan[count1]=1;

				int degree = getDegree(values[0], values[1]);
				int degreeZ = calcAngleZ(values[0], values[1], values[2]);
				int index = -1;
				if (degree != -1) {
					degree = transformDegree(degree);
					index = (int) Math.floor(degree / 2);
					Log.d(Tag, "=====> index is " + index + "=====> degree is "
							+ degree + "=====> degreeZ is " + degreeZ);
					if (isScan[index] == 0) {
						if (degreeZ < 30) {
							isScan[index] = 1;
							if (index > 0)
								if (isScan[index - 1] == 0)
									isScan[index - 1] = 1;
							if (index < 179)
								if (isScan[index + 1] == 0)
									isScan[index + 1] = 1;
						} else {
							isScan[index] = 2;
							if (index > 0)
								isScan[index - 1] = 2;
							if (index < 179)
								isScan[index + 1] = 2;
						}
					}
					if (isScan[index] == 1) {
						if (degreeZ >= 30) {
							isScan[index] = 2;
							if (index > 0)
								isScan[index - 1] = 2;
							if (index < 179)
								isScan[index + 1] = 2;
						}
					}
				}
				// canvas.drawCircle(-5, y, 5, paint);
				for (int i = 0; i < count; i++) {
					if (isScan[i] == 0) {
						paint.setColor(Color.GRAY);
						canvas.drawLine(0f, y, 0f, y + 80, paint);
					} else {
						paint.setColor(Color.WHITE);
						if (isScan[i] == 1) {
							canvas.drawLine(0f, y, 0f, y + 40, paint);
							paint.setColor(Color.GRAY);
							canvas.drawLine(0f, y + 40, 0f, y + 80, paint);
						}
						if (isScan[i] == 2)
							canvas.drawLine(0f, y, 0f, y + 80, paint);
					}
					if (i == index) {
						paint.setColor(Color.RED);
						canvas.drawCircle(0, y - 20, 20, paint);
					}

					canvas.rotate(360 / count, 0f, 0f);
				}
				// isScan[count1]=0;
				holder.unlockCanvasAndPost(canvas);
			}
		}}
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
