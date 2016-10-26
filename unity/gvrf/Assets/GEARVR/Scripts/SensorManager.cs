#if UNITY_ANDROID && !UNITY_EDITOR
#define ANDROID_PLUGIN
#endif

#define DEBUG_

using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;

public class SensorManager : SensorListener {
#if ANDROID_PLUGIN
	[DllImport ("gvrf")]
	private static extern void SensorCallback(float w, float x, float y, float z, float gyro_x, float gyro_y, float gyro_z, long bufferCnt, long time);
#endif
	private const bool DEBUG_PER_FRAME = false;

    private static SensorManager sInstance;

	private List<SensorInterface> mSensors;

    private long mLastKSensorTime = 0;
    public bool mUseKSensor = false;
    
    private SensorManager() {
		Debug.Log("SensorManager::SensorManager");
		// Initialize all sensors
		mSensors = new List<SensorInterface> ();

		// Add sensors in order of priorities. The first active sensor will be used.
		addSensor (new KSensor ());
		addSensor (new AndroidRotationSensor ());
		Debug.Log("SensorManager::SensorManager <--");
    }

	private void addSensor(SensorInterface sensor) {
		mSensors.Add (sensor);
		sensor.setListener (this);
		sensor.start ();
    }

	public static SensorManager Instance
	{
		//Debug.Log("SensorManager Instance ");
		get 
		{
			if (sInstance == null)
			{
				sInstance = new SensorManager();
			}
			return sInstance;
		}
	}

	public void onConnected(SensorInterface sensor) {
   
    }

	public void onDisconnected(SensorInterface sensor) {
       
    }
	
    public void pause()
    {
        foreach (SensorInterface s in mSensors)
        {
            s.pause();
        }
    }

    public void resume()
    {
        foreach (SensorInterface s in mSensors)
        {
            s.resume();
        }
    }

    // Polls data from internal sensor
    public void poll()
    {
        foreach (SensorInterface s in mSensors)
        {
            s.poll();
        }
    }

    private readonly object mDataLock = new object();
    public void onNewData(SensorInterface sensor, float w, float x, float y, float z, float gyroX, float gyroY, float gyroZ, long time) {
        lock (mDataLock)
        {
#if DEBUG
            Debug.LogFormat("onNewData {0} wxyz:{1} {2} {3} {4} gyro:{5} {6} {7} time:{8}", sensor, w, x, y, z, gyroX, gyroY, gyroZ, time);
#endif

            // Integrate data
            if (sensor is KSensor)
            {
				if (mLastKSensorTime != time) {
					mLastKSensorTime = time;

                    if (!mUseKSensor)
                    {
                        Debug.Log("Switch to ksensor");
                        mUseKSensor = true;
                        GVRManager.Instance.getFrameRegulator().setKSensor(true);
                    }
                } else {
					if (mUseKSensor && time - mLastKSensorTime > 250000000 /* .25s */)
                    {
                        Debug.Log("Switch to Android sensor");
                        mUseKSensor = false;
                        GVRManager.Instance.getFrameRegulator().setKSensor(false);
                    }
                }
				if (!mUseKSensor) {
					return;
				}
            }

            if (sensor is AndroidRotationSensor && mUseKSensor)
            {
				if (time - mLastKSensorTime > 250000000 /* .25 sec */)
                {
                    Debug.Log("Switch to Android sensor (ksensor stops)");
                    mUseKSensor = false;
                    GVRManager.Instance.getFrameRegulator().setKSensor(false);
                }
                return;
            }

            if (w != double.NaN && double.PositiveInfinity != w && double.NegativeInfinity != w)
                this.w = w;

            if (x != double.NaN && double.PositiveInfinity != x && double.NegativeInfinity != x)
                this.x = x;

            if (y != double.NaN && double.PositiveInfinity != y && double.NegativeInfinity != y)
                this.y = y;

            if (z != double.NaN && double.PositiveInfinity != z && double.NegativeInfinity != z)
                this.z = z;

            if (gyroX != double.NaN && double.PositiveInfinity != gyroX && double.NegativeInfinity != gyroX)
                this.gyroX = gyroX;

            if (gyroY != double.NaN && double.PositiveInfinity != gyroY && double.NegativeInfinity != gyroY)
                this.gyroY = gyroY;

            if (gyroZ != double.NaN && double.PositiveInfinity != gyroZ && double.NegativeInfinity != gyroZ)
                this.gyroZ = gyroZ;

            if (time != double.NaN && double.PositiveInfinity != time && double.NegativeInfinity != time)
                this.time = time;

            // Update data in Java
#if ANDROID_PLUGIN
			if (DEBUG_PER_FRAME) {
				Debug.Log("onNewData SensorCallback" + GVRManager.Instance.getFrameRegulator().getBufferCount());
			}
			SensorCallback(w, x, y, z, gyroX, gyroY, gyroZ,
				GVRManager.Instance.getFrameRegulator().getBufferCount(), time);
#endif // ANDROID_PLUGIN
        }
    }

    public bool isUsingKSensor()
    {
        return mUseKSensor;
    }

    public float w = 0.0f;
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;

    public float gyroX = 0.0f;
    public float gyroY = 0.0f;
    public float gyroZ = 0.0f;

    public long time = 0;
}
