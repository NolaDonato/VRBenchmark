// C# example:
using UnityEngine;
using UnityEditor;
using UnityEditor.Callbacks;
using System.IO;

public class PostProcessChecker {
	[PostProcessBuildAttribute(0)]
	public static void OnPostprocessBuild(BuildTarget target, string pathToBuiltProject) {

		if(!File.Exists(Application.dataPath + "/Plugins/Android/AndroidManifest.xml"))
			Debug.LogError ("Missing Manifest.xml, the build will not work properlly, please reimport the plugin");
		if(!File.Exists(Application.dataPath + "/Plugins/Android/libassimp.so"))
			Debug.LogError ("Missing libassimp.so file, the build will not work properlly, please reimport the plugin");
		if(!File.Exists(Application.dataPath + "/Plugins/Android/libgvrf.so"))
			Debug.LogError ("Missing libgvrf.so file, the build will not work properlly, please reimport the plugin");
//		if(!File.Exists(Application.dataPath + "/Plugins/Android/libGVRFPlugin.so"))
//			Debug.LogError ("Missing libGVRFPlugin.so file, the build will not work properlly, please reimport the plugin");
		pathToBuiltProject = null;
	}
}
