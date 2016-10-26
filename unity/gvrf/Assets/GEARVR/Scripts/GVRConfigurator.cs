using UnityEngine;
using System.Collections;

public class GVRConfigurator {
	 
	public static float realScreenWidthPixels = 2560;
	public static float realScreenHeightPixels = 1440;

	public static float xdpi = 960;
	public static float ydpi = 540;

	public static int mDistortionGridSize = 50;
	public static float mK0 = 1.0f;
	public static float mK1 = 0.1589f;
	public static float mK2 = -0.0151f;
	public static float mK3 = 0.0721f;
	public static float mScale = 0.8226f;
	public static float freeParam1 = 0;
	public static float freeParam2 = 0;
	private static float cpuLevel = 0, gpuLevel = 0;

	public static void SetGPULevel(int level){

		gpuLevel = level;
		UpdateCPUGPULevel ();
	}

	public static void SetCPULevel(int level){

		cpuLevel = level;
		UpdateCPUGPULevel ();
	}

	public static void UpdateCPUGPULevel(){
		#if UNITY_ANDROID && !UNITY_EDITOR
		using (
			AndroidJavaClass unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer"),
			inputManager = new AndroidJavaClass("org.gearvrf.GVRActivity")
			) {
			//Get IVRManager Interface Class
			AndroidJavaClass IVRManager = new AndroidJavaClass("android.app.IVRManager");
			//Get application Activity
			AndroidJavaObject activityInstance = unityPlayerClass.GetStatic<AndroidJavaObject>("currentActivity");
			//Get Application Context (to get app package later)
			AndroidJavaObject context = activityInstance.Call<AndroidJavaObject>("getApplicationContext");
			//Get IVRManager Interface Instance
			AndroidJavaObject vrManager =  activityInstance.Call<AndroidJavaObject>("getSystemService","vr");
			
			//Set VRClocks 
			vrManager.Call<int[]>("SetVrClocks",context.Call<string>("getPackageName"),cpuLevel,gpuLevel);

			
		}
#endif
	}

	public static void DisplayMetricsAndroid() {

		#if UNITY_ANDROID && !UNITY_EDITOR
			if (Application.platform != RuntimePlatform.Android) {
				return;
			}
					
			using (
				AndroidJavaClass unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer"),
				metricsClass = new AndroidJavaClass("android.util.DisplayMetrics")
				) {
				using (
					AndroidJavaObject metricsInstance = new AndroidJavaObject("android.util.DisplayMetrics"),
					activityInstance = unityPlayerClass.GetStatic<AndroidJavaObject>("currentActivity"),
					windowManagerInstance = activityInstance.Call<AndroidJavaObject>("getWindowManager"),
					displayInstance = windowManagerInstance.Call<AndroidJavaObject>("getDefaultDisplay")
					) {
					displayInstance.Call("getMetrics", metricsInstance);
					realScreenHeightPixels = metricsInstance.Get<int>("heightPixels");
					realScreenWidthPixels = metricsInstance.Get<int>("widthPixels");
					xdpi = metricsInstance.Get<float>("xdpi");
					ydpi = metricsInstance.Get<float>("ydpi");
				}
			}
#endif
		}



}
