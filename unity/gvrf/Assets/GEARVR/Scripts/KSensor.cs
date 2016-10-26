#if UNITY_ANDROID && !UNITY_EDITOR
    #define KSENSOR_ENABLE
#endif

#define DEBUG_

using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;

// Oculus Sensor
public class KSensor : SensorInterface {
#if KSENSOR_ENABLE
	public delegate void KSensorCallbackDelegate(float w, float x, float y, float z, float gyro_x, float gyro_y, float gyro_z, long time);

	[DllImport ("gvrf")]
	private static extern void SetKSensorCallback(KSensorCallbackDelegate fp);

	[DllImport ("gvrf")]
	private static extern void KSensorStart();

	[DllImport ("gvrf")]
	private static extern void KSensorStop();

    [DllImport ("gvrf")]
	private static extern void KSensorPoll();
#endif

    private SensorListener mListener;

    private UnityEngine.Quaternion mQuaternion = new UnityEngine.Quaternion();
    private Vector3 mGyro = new Vector3();

    public KSensor() {
        instance = this;

#if KSENSOR_ENABLE
		SetKSensorCallback(new KSensorCallbackDelegate(callbackProc));
#endif
    }

    static KSensor instance;
    static void callbackProc(float w, float x, float y, float z, float gyro_x, float gyro_y, float gyro_z, long time) {
#if DEBUG
        // Callback received
        Debug.LogFormat("KSensor callback wxyz:{0} {1} {2} {3} gyro:{4} {5} {6} time:{7}", w, x, y, z, gyro_x, gyro_y, gyro_z, time);
#endif

        if (instance != null)
        {
            instance.mQuaternion.w = w;
            instance.mQuaternion.x = x;
            instance.mQuaternion.y = y;
            instance.mQuaternion.z = z;

            instance.mGyro.x = gyro_x;
            instance.mGyro.y = gyro_y;
            instance.mGyro.z = gyro_z;
        }

        if (instance.mListener == null)
			return;

		// Note: reverse sign for w and z before setting to camera
        instance.mListener.onNewData(instance, instance.mQuaternion.w, instance.mQuaternion.x,
            instance.mQuaternion.y, instance.mQuaternion.z, 
            gyro_x, gyro_y, gyro_z,
            time);
    }

    public void setListener(SensorListener listener) {
		mListener = listener;
	}

	public void start() {
#if KSENSOR_ENABLE
		KSensorStart ();
#endif
	}

	public void stop() {
#if KSENSOR_ENABLE
		KSensorStop ();
#endif
	}

    public UnityEngine.Quaternion poll()
    {
#if KSENSOR_ENABLE
        KSensorPoll();
#endif
        return mQuaternion;
    }

    public void pause()
    {

    }

    public void resume()
    {

    }

    public static UnityEngine.Quaternion toCamera(UnityEngine.Quaternion raw)
    {
        UnityEngine.Quaternion conv = new UnityEngine.Quaternion();
        conv.w = -raw.w;
        conv.x = raw.x;
        conv.y = raw.y;
        conv.z = -raw.z;
        return conv;
    }
}
