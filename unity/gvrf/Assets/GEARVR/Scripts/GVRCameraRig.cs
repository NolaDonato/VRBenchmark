using UnityEngine;
using System.Collections;
[DisallowMultipleComponent]

public class GVRCameraRig : MonoBehaviour {
    GVRManager gvrm;
	
	UnityEngine.Quaternion curRotation = new UnityEngine.Quaternion();

    void Start () {
        // Initialize GearVR Plugin
        Debug.Log("GVRCameraRig::Start()");
		gvrm = GVRManager.Instance;
	}

	void Update () {
        SensorManager sensor = gvrm.getSensorManager();

#if UNITY_ANDROID && !UNITY_EDITOR        
		curRotation.w = -sensor.w;
		curRotation.x = sensor.x;
		curRotation.y = sensor.y;
		curRotation.z = -sensor.z;

        if (!FrameRegulator.USE_PREDICTION) {
            this.transform.rotation = curRotation;
        }
#endif
    }
}
