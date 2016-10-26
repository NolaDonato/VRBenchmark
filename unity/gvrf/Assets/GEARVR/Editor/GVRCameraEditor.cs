using UnityEngine;
using UnityEditor;
using System.Collections;

[CustomEditor (typeof(GVRCamera))]
public class GVRCameraEditor : Editor {

	private GVRCamera instance;

	public override void OnInspectorGUI(){
		base.OnInspectorGUI ();
		instance = (GVRCamera)target;
		instance.useUnityAntialiasing = EditorGUILayout.Toggle ("Use Antialiasing ", instance.useUnityAntialiasing);
		if (instance.useUnityAntialiasing) {

			instance.antialiasingLevel = (GVRCamera.UnityAntialiasingLevel) EditorGUILayout.EnumPopup ("Antialiasing Level",instance.antialiasingLevel);
		}


//		
//		instance.useUnityAntialiasing = EditorGUILayout.Toggle ("Use Antialiasing ", instance.useUnityAntialiasing);
//		if (instance.useUnityAntialiasing == true) {
//			instance.antialiasingLevel = (GVRCamera.UnityAntialiasingLevel) EditorGUILayout.EnumPopup ("Antialiasing Level",instance.antialiasingLevel);
//		}
//		
//		EditorGUILayout.Space ();
//		EditorGUILayout.Space ();
//		EditorGUILayout.Space ();
//		instance.rightTexture = (RenderTexture)EditorGUILayout.ObjectField ("Right eye render texture", instance.rightTexture, typeof(RenderTexture),false);
//		instance.leftTexture = (RenderTexture)EditorGUILayout.ObjectField ("Left eye render texture", instance.leftTexture, typeof(RenderTexture),false);
//
	
	}

}
