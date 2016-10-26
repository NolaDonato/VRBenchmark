using UnityEngine;
using UnityEditor;
using System.Collections;

public class GVRWindow : EditorWindow {
	
	bool groupEnabled;
	
	
	public static bool unityAntialiasing = false;
	public static GVRCamera.UnityAntialiasingLevel antialiasingLevel = GVRCamera.UnityAntialiasingLevel.X2Sample;
	bool brokenPipe = false;
	bool isCamera = false;
	bool preparedCameraExists = false;
	
	[MenuItem ("Gear VR/Prepare VR Camera...")]
	static void Init () {
		
		GVRWindow window = (GVRWindow)EditorWindow.GetWindow (typeof (GVRWindow));
		window.Show();
	}
	
	void Update(){

		Repaint ();
	}
	void OnGUI () {
		
		brokenPipe = false;preparedCameraExists = false;
		GVRCamera gvrcamera = FindObjectOfType<GVRCamera>();
		
		if (Selection.activeGameObject != null) {
			
			
			if (!Selection.activeGameObject.GetComponent<GVRCamera> ()) {
				
				brokenPipe = true;
			}
			
			if (!Selection.activeGameObject.GetComponent<GVRCameraRig> ()) {
				
				brokenPipe = true;
			}
			
			if (!Selection.activeGameObject.transform.FindChild ("GVRRightCamera")) {
				
				brokenPipe = true;
			}
			
			if (!Selection.activeGameObject.transform.FindChild ("GVRLeftCamera")) {			
				
				brokenPipe = true;
			}
			
			
		}
		if(gvrcamera){
			brokenPipe = false;
			preparedCameraExists = true;

			#if UNITY_5
			if (PlayerSettings.virtualRealitySupported == true)
				PlayerSettings.virtualRealitySupported = false;
			

					
			#endif
			PlayerSettings.defaultInterfaceOrientation = UIOrientation.LandscapeLeft;
		}
		if (Selection.activeGameObject == null || Selection.activeGameObject.GetComponent<Camera> () == null) {
			GUI.enabled = false;
			isCamera = false;
		} else {
			isCamera = true;
			GUI.enabled = true;
		}
		
		if (!brokenPipe) {
			GUI.enabled = false;
		}
		
		GUILayout.Label ("Gear VR Camera Configuration", EditorStyles.boldLabel);
		
		unityAntialiasing = EditorGUILayout.Toggle ("Use Unity Antialiasing ", unityAntialiasing);
		if (unityAntialiasing == true) {
			antialiasingLevel = (GVRCamera.UnityAntialiasingLevel) EditorGUILayout.EnumPopup ("Unity Antialiasing Level",antialiasingLevel);
		}
		
		
		if(brokenPipe && isCamera){
			
			GUI.enabled = true;
			#if UNITY_5
			GUILayout.Label ("This camera is not prepared for Gear VR use, please prepare it", EditorStyles.helpBox);
			#else
			GUILayout.Label ("This camera is not prepared for Gear VR use, please prepare it",EditorStyles.whiteLabel);
			#endif
			
			
			
			if (GUILayout.Button ("Prepare VR Camera...")) {
				
				GVREditor.PrepareCamera (Selection.activeGameObject.GetComponent<Camera> ());
			}
		}
		if(preparedCameraExists){
			#if UNITY_5
			GUILayout.Label ("There's already one prepared camera", EditorStyles.helpBox);
			#else
			GUILayout.Label ("There's already one prepared camera",EditorStyles.whiteLabel);
			#endif
			
			GUI.enabled = true;	
			GUILayout.Label("Gear VR Prepared Camera");
			EditorGUILayout.ObjectField(gvrcamera,typeof(GVRCamera),true);
			if (GUILayout.Button ("Unprepare VR Camera...")) {
				
				GVREditor.UnprepareCamera (gvrcamera.gameObject);
			}
			#if UNITY_5
			GUILayout.Label ("All elements inside 'GVRCameraRig' are going to be removed", EditorStyles.helpBox);
			#else
			GUILayout.Label ("All elements inside 'GVRCameraRig' are going to be removed",EditorStyles.whiteLabel);
			#endif
		}
		else{
			if(!isCamera)
			#if UNITY_5
				GUILayout.Label ("Please select a camera", EditorStyles.helpBox);
			#else
				GUILayout.Label ("Please select a camera",EditorStyles.whiteLabel);
			#endif
			
		}
		
		
	}
}
