//using UnityEngine;
//using System.Collections;
//using UnityEngine.UI;
//public class FPSTEST : MonoBehaviour {
//
//	Text text;
//	float timer;
//	// Use this for initialization
//	void Start () {
//		text = GetComponent<Text> ();
//	}
//	
//	// Update is called once per frame
//	void Update () {
//
//		if (timer < Time.time) {
//			timer = Time.time + 0.5f;
//			//text.text = "" + (int)(1 / Time.deltaTime);
//            string text = "" + (int)(1 / Time.deltaTime);
//            Debug.Log("using UnityPlugin FPS is: " + text);
//        }
//	}
//}
