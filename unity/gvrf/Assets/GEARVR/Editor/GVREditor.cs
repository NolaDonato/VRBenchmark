using UnityEngine;
using UnityEditor;
using System.Xml;
using System.Collections;
using UnityEngine.EventSystems;

public class GVREditor : Editor {

	public static string titleMessage = "Camera already set", 
						 bodyMessage = "Your camera already have GVRCamera script, " +
		"if you really want to configure this camera, remove this script", 
						 confirmationMessage = "ok";


	//GVR Rig build parameters
	static XmlDocument doc;
	public struct GVREyeParameters
	{
		public bool copyParent;
		public float camerasOffset;
		public string name;

		public GVREyeParameters(string name){


			copyParent = GetCopyParent(doc);
			camerasOffset = GetCameraOffset(doc);
			this.name = name;
		}

	};

	public static GVREyeParameters rightEyeParameters, leftEyeParameters;

	public static void CreateCamera(GameObject obj, GameObject camRig, GVREyeParameters parameters){
		GameObject newObj = new GameObject ("GVR" + parameters.name+"Camera");
		Camera cam = newObj.AddComponent<Camera> ();
		if(parameters.copyParent)
			cam.CopyFrom(obj.GetComponent<Camera>());
		cam.transform.parent = camRig.transform;

		Debug.Log("GVREditor::CreateCamera cam.targetTexture  Resources.Load "+ "GVRCameraRenderTexture"+ parameters.name);
		cam.targetTexture = (RenderTexture)Resources.Load ("GVRCameraRenderTexture"+ parameters.name);

		if (parameters.name == "Left") {
			parameters.camerasOffset *= -1;
			newObj.AddComponent<GVRLeftEye> ();
			obj.GetComponent<GVRCamera> ().SetLeftTexture (cam.targetTexture);
			obj.GetComponent<GVRCamera>().SetLeftCamera(cam);
		} else {
			newObj.AddComponent<GVRRightEye>();
			obj.GetComponent<GVRCamera> ().SetRightTexture(cam.targetTexture);
			obj.GetComponent<GVRCamera>().SetRightCamera(cam);
		}
		cam.transform.localPosition = new Vector3 (parameters.camerasOffset, 0, 0);
	}

	public static void PrepareCamera(Camera cam){
		doc = GetXMLDocument();
		PlayerSettings.allowedAutorotateToPortraitUpsideDown = false;
		PlayerSettings.allowedAutorotateToPortrait = false;
		PlayerSettings.allowedAutorotateToLandscapeRight = false;
		InitializeParameters ();

		EventSystem eventSystem = GameObject.FindObjectOfType<EventSystem> ();
		if (eventSystem) {
			if(!eventSystem.gameObject.GetComponent<GVRInput>()){
				eventSystem.gameObject.AddComponent<GVRInput>();
			}
			if(!eventSystem.gameObject.GetComponent<GVRInputModule>()){
				eventSystem.gameObject.AddComponent<GVRInputModule>();
			}
		} else {
			GameObject obj = (GameObject)Instantiate (Resources.Load("GVREventSystem"));
			obj.name = obj.name.Replace("(Clone)","");
		}

		CreateCameras (cam.gameObject,CreateCameraRig (cam.gameObject));
		PlayerSettings.defaultInterfaceOrientation = UIOrientation.LandscapeLeft;

		cam.clearFlags = CameraClearFlags.Nothing;//Can be CameraClearFlags.Skybox, CameraClearFlags.SolidColor, CameraClearFlags.Depth or CameraClearFlags.Nothing.
		cam.cullingMask = 0;
	}

	public static void InitializeParameters(){

		rightEyeParameters = new GVREyeParameters ("Right");
		leftEyeParameters = new GVREyeParameters ("Left");
	}

	public static void CreateCameras(GameObject cam, GameObject camRig){

		if (!cam.gameObject.GetComponent<GVRCamera> ()) {
			cam.gameObject.AddComponent<GVRCamera> ();
		} 

		if (!cam.gameObject.transform.FindChild ("GVRLeftCamera")) {
			CreateCamera (cam.gameObject, camRig, leftEyeParameters);
		} else {

			cam.gameObject.GetComponent<GVRCamera> ().SetLeftTexture (cam.gameObject.transform.FindChild ("GVRLeftCamera").GetComponent<Camera>().targetTexture);
		}
		if (!cam.gameObject.transform.FindChild ("GVRRightCamera")) {
			CreateCamera (cam.gameObject, camRig, rightEyeParameters);
			
			
		} else {
			
			cam.gameObject.GetComponent<GVRCamera> ().SetRightTexture (cam.gameObject.transform.FindChild ("GVRRightCamera").GetComponent<Camera>().targetTexture);
		}

		cam.gameObject.GetComponent<GVRCamera> ().useUnityAntialiasing = GVRWindow.unityAntialiasing;
		cam.gameObject.GetComponent<GVRCamera> ().antialiasingLevel = GVRWindow.antialiasingLevel;
	}

	static GameObject CreateCameraRig (GameObject obj){

		GameObject cameraRig = new GameObject ("GVRCameraRig");
		cameraRig.gameObject.AddComponent<GVRCameraRig> ();
		//Camera to be used exclusivelly for UI events, rendering will be disabled
		cameraRig.gameObject.AddComponent<Camera> ().enabled = false;

		cameraRig.transform.parent = obj.transform;
		float headModelDepth = float.Parse (doc.GetElementsByTagName ("HeadModelDepth") [0].InnerText);
		float headModelHeight = float.Parse (doc.GetElementsByTagName ("HeadModelHeight") [0].InnerText);
		cameraRig.transform.localPosition = new Vector3 (0, -headModelHeight, headModelDepth);
		return cameraRig;
	}

	static bool GetCopyParent(XmlDocument doc){

		try{
		return (bool.Parse(doc.GetElementsByTagName("CopyParentCamera")[0].InnerText));
		}
		catch{
			Debug.LogError("CopyParentCamera must be a boolean (true/false) value");
			return false;
		}
	}

	static float GetCameraOffset(XmlDocument doc){

		try{
		return  float.Parse (doc.GetElementsByTagName ("EyeDistance") [0].InnerText);
		}
		catch{
			Debug.LogError("EyeDistance must be a float value");
			return 0;
		}
	}


	public static void UnprepareCamera(GameObject obj){
		if (!obj)
			return;
		GVRCamera gvrcamera = obj.GetComponent<GVRCamera> ();
		GVRCameraRig gvrrig = obj.GetComponentInChildren<GVRCameraRig> ();
		GVRRightEye rightEye = obj.GetComponentInChildren<GVRRightEye> ();
		GVRLeftEye leftEye = obj.GetComponentInChildren<GVRLeftEye> ();
		GVRInput gvrInput = FindObjectOfType<GVRInput> ();
		GVRInputModule gvrInputModule = FindObjectOfType<GVRInputModule> ();

		if (gvrcamera) {
			DestroyImmediate (gvrcamera);
		}

		if (gvrrig) {
			DestroyImmediate (gvrrig.gameObject);
		}
		if (rightEye) {
			DestroyImmediate (rightEye.gameObject);
		}
		if (leftEye) {
			DestroyImmediate(leftEye.gameObject);
		}
		if (gvrInput) {
			DestroyImmediate(gvrInput);
		}
		if (gvrInputModule) {
			DestroyImmediate(gvrInputModule);
		}
	}


	public static void UnprepareAllSceneCameras(){



	}

	public static XmlDocument GetXMLDocument(){

		XmlDocument doc = new XmlDocument ();

		try{ 
			doc.Load (Application.dataPath + "/GEARVR/config.xml");

		}

		catch{
			Debug.LogWarning("Config.xml not found, creating a new one with default parameters");
			CreateBaseXML.BaseXML();
			doc.Load (Application.dataPath + "/GEARVR/config.xml");
		}
		return doc;
	}
}
