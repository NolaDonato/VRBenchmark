#if UNITY_ANDROID && !UNITY_EDITOR
#define ANDROID_PLUGIN
#endif

using UnityEngine;
using System.Collections;
using System;
using System.Runtime.InteropServices;

public class AndroidRotationSensor : SensorInterface {
#if ANDROID_PLUGIN
	[DllImport ("gvrf")]
	private static extern long GetCurrentTime();
#endif

    private SensorListener mListener;
    private static UnityEngine.Quaternion sComplemtaryRotation = new UnityEngine.Quaternion();
    private UnityEngine.Quaternion mCurRotation = new UnityEngine.Quaternion();
    public Vector3 mRotationRate = new Vector3();

    // TODO: cancel initial rotation
    private static readonly bool REALIGN_ENABLE = false;
    private bool needOrigin = false;

    public void setListener(SensorListener listener)
    {
        mListener = listener;
    }

    public void start()
    {
#if ANDROID_PLUGIN
        Input.gyro.enabled = true;
        needOrigin = true;
#endif
    }

    public void stop()
    {
#if ANDROID_PLUGIN
        Input.gyro.enabled = false;
#endif
    }

    public UnityEngine.Quaternion poll()
    {
#if ANDROID_PLUGIN
        if (REALIGN_ENABLE && needOrigin) {
            sComplemtaryRotation.w = Input.gyro.attitude.w;
            sComplemtaryRotation.x = Input.gyro.attitude.x;
            sComplemtaryRotation.y = Input.gyro.attitude.y;
            sComplemtaryRotation.z = Input.gyro.attitude.z;
            sComplemtaryRotation = UnityEngine.Quaternion.Inverse(sComplemtaryRotation);
            
            needOrigin = false;
        }

        mCurRotation.w = Input.gyro.attitude.w;
        mCurRotation.x = Input.gyro.attitude.x;
        mCurRotation.y = Input.gyro.attitude.y;
        mCurRotation.z = Input.gyro.attitude.z;

        mRotationRate = Input.gyro.rotationRateUnbiased;

        if (mListener != null) {
            long timestamp = GetCurrentTime();
            mListener.onNewData(this, mCurRotation.w, mCurRotation.x, mCurRotation.y, mCurRotation.z,
                                mRotationRate.x, mRotationRate.y, mRotationRate.z,
                                timestamp);
        }
#endif

        return mCurRotation;
    }

    public void pause()
    {
    }

    public void resume()
    {
        needOrigin = true;
    }

    public static UnityEngine.Quaternion toCamera(UnityEngine.Quaternion raw)
    {
        UnityEngine.Quaternion conv = new UnityEngine.Quaternion();

        conv.w = raw.w;
        conv.x = raw.x;
        conv.y = raw.y;
        conv.z = raw.z;

        if (REALIGN_ENABLE)
        {
            conv = sComplemtaryRotation * conv;
        }

        conv.w = conv.w * -1;
        conv.x = conv.x * -1;
        conv.y = conv.y * -1;

        if (!REALIGN_ENABLE)
        {
            conv *= UnityEngine.Quaternion.Euler(-90, 0, 0);
        }

        conv = UnityEngine.Quaternion.Inverse(conv);
        
        return conv;
    }
}
