#if UNITY_ANDROID && !UNITY_EDITOR
#define UNITY_GLES_RENDERER
#endif

using UnityEngine;
using System.Collections;
using System.IO;
using System.Xml;
using System.Xml.Serialization;
using UnityEngine.UI;
using System;

public class XMLParser {

	public static XMLParser instance = new XMLParser();

	public static string XML_NOTE4 =  System.IO.Path.Combine(Application.streamingAssetsPath, "Xml/note4.xml");
	public static string XML_SAMSUNG_GALAXY_S6    = System.IO.Path.Combine(Application.streamingAssetsPath, "Xml/s6.xml");

	private string deviceXmlPath;
	private DeviceConfig config;

	[HideInInspector]
	public enum DEVICES{
		SamsungGalaxyS6 = 0,//"Samsung Galaxy s6",
		SamsungGalaxyNote4 = 1,//"Galaxy Note 4"
		Automatic
	}
  
	public DEVICES device = DEVICES.SamsungGalaxyNote4;

	[XmlRoot("device")]
	public class DeviceConfig{
		public float renderDiameterMeters = 0.062f;
		public float realScreenWidthMeters = 0.125f;
		public float realScreenWidthPixels = 2560.0f;
		public float realScreenHeightPixels = 1440.0f;
		public float lensesIPDMeters = 0.063f;
		public float shiftScreenCenterMeters = -0.0625f;

	}

	public void LoadConfiguration () {


		switch (device) {
		case DEVICES.SamsungGalaxyS6:
			deviceXmlPath = XML_SAMSUNG_GALAXY_S6;
			break;
		case DEVICES.SamsungGalaxyNote4:
			deviceXmlPath = XML_NOTE4;
			break;

		}
		Debug.Log (deviceXmlPath);
		WWW url = new WWW (deviceXmlPath);
		while (!url.isDone)
			new WaitForSeconds (0.02f); // waitting the load.	

		if (!String.IsNullOrEmpty (url.error)) {
			LogXMLError (deviceXmlPath);
		}
		string xmlData = url.text;
		if (!String.IsNullOrEmpty (xmlData)) {
			Debug.Log ("Load Device XML: " + deviceXmlPath + "");
			Debug.Log ("XML data: " + xmlData + "");
			XmlTextReader reader = new XmlTextReader (new StringReader (xmlData));			
			var serializer = new XmlSerializer (typeof(DeviceConfig));
			config = serializer.Deserialize (reader) as DeviceConfig;
		} else {
			Debug.Log ("Load Device XML fail");
		
		}
	}
	public float GetRenderDiameterMeters(){
		return config.renderDiameterMeters;
	}

	public float GetRealScreenWidthMeters(){
		#if UNITY_4_6
			return config.realScreenWidthMeters;
#else
			return config.realScreenWidthMeters;
#endif

	}

	public float GetRealScreenWidthPixels(){
		return GVRConfigurator.realScreenWidthPixels/2;
	}

	public float GetRealScreenHeightPixels(){
		return GVRConfigurator.realScreenHeightPixels;
	}

	public float GetLensesIPDMeters(){
		return config.lensesIPDMeters;
	}

	public float GetShiftScreenCenterMeters(){
		return config.shiftScreenCenterMeters;
	}

	private void LogXMLError(string errorFile){

		Debug.LogError ("Error loading file " + errorFile + " please reimport plugin package");

	}

}
