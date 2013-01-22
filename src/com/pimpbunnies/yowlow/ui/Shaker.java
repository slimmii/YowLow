package com.pimpbunnies.yowlow.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

public class Shaker {
	
	private DeviceShaked shaked;
	private Context context;
	
	public Shaker(Context context, DeviceShaked shaked, float shakeThreshold) {
		this.context = context;
		this.shaked = shaked;
		this.shakeThreshold = shakeThreshold;
		
		mySensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE); // (1)
		mySensorManager.registerListener(mySensorEventListener, mySensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL); // (2)
	}
	
	/* Here we store the current values of acceleration, one for each axis */
	private float xAccel;
	private float yAccel;
	private float zAccel;

	/* And here the previous ones */
	private float xPreviousAccel;
	private float yPreviousAccel;
	private float zPreviousAccel;

	/* Used to suppress the first shaking */
	private boolean firstUpdate = true;

	/*What acceleration difference would we assume as a rapid movement? */
	private float shakeThreshold = 1.5f;

	/* Has a shaking motion been started (one direction) */
	private boolean shakeInitiated = false;

	/* The connection to the hardware */
	private SensorManager mySensorManager;

	/* The SensorEventListener lets us wire up to the real hardware events */
	private final SensorEventListener mySensorEventListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			updateAccelParameters(se.values[0], se.values[1], se.values[2]);   // (1)
			if ((!shakeInitiated) && isAccelerationChanged()) {                                      // (2) 
				shakeInitiated = true; 
			} else if ((shakeInitiated) && isAccelerationChanged()) {                              // (3)
				executeShakeAction();
			} else if ((shakeInitiated) && (!isAccelerationChanged())) {                           // (4)
				shakeInitiated = false;
			}
		}
		
		/* If the values of acceleration have changed on at least two axises, we are probably in a shake motion */
		private boolean isAccelerationChanged() {
			float deltaX = Math.abs(xPreviousAccel - xAccel);
			float deltaY = Math.abs(yPreviousAccel - yAccel);
			float deltaZ = Math.abs(zPreviousAccel - zAccel);
			return (deltaX > shakeThreshold && deltaY > shakeThreshold)
					|| (deltaX > shakeThreshold && deltaZ > shakeThreshold)
					|| (deltaY > shakeThreshold && deltaZ > shakeThreshold);
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			/* can be ignored in this example */
		}

		/* Store the acceleration values given by the sensor */
		private void updateAccelParameters(float xNewAccel, float yNewAccel,
				float zNewAccel) {
			/* we have to suppress the first change of acceleration, it results from first values being initialized with 0 */
			if (firstUpdate) {  
				xPreviousAccel = xNewAccel;
				yPreviousAccel = yNewAccel;
				zPreviousAccel = zNewAccel;
				firstUpdate = false;
			} else {
				xPreviousAccel = xAccel;
				yPreviousAccel = yAccel;
				zPreviousAccel = zAccel;
			}
			xAccel = xNewAccel;
			yAccel = yNewAccel;
			zAccel = zNewAccel;
		}
		
        private void executeShakeAction() {
        	shaked.shaked();
        }
	};
}