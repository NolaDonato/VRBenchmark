using UnityEngine;
using System.Collections;

public class GVRManager {
	private static GVRManager sInstance;

	private SensorManager mSensorManager;

    // FR configuration
    private bool mEnableFrameRegulator = true;
    private FrameRegulator mFrameRegulator;

	private GVRManager() {
		Debug.Log("GVRManager::GVRManager");
		mSensorManager = SensorManager.Instance;
        mFrameRegulator = FrameRegulator.Instance;

		Debug.Log("GVRManager::GVRManager  // FR init and start");
        // FR init and start
        if (mEnableFrameRegulator) {
			
            mFrameRegulator.initialize();
        }
    }
	
    public void onPause()
    {
        mSensorManager.pause();
    }

    public void onResume()
    {
        mSensorManager.resume();
    }

	public static GVRManager Instance
	{
		get 
		{
			if (sInstance == null)
			{
				sInstance = new GVRManager();
			}
			return sInstance;
		}
	}

	public SensorManager getSensorManager() {
		return mSensorManager;
	}

    public FrameRegulator getFrameRegulator()
    {
        return mFrameRegulator;
    }

    // API

    // Update stereo camera settings with main camera settings.
    // An application should call this function after it changes
    // main camera settings.
    public void updateStereoCameraSettings()
    {
        // TODO
    }
}
