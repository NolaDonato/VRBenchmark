//using UnityEngine;
//using System.Collections;
//using UnityEngine.UI;
//
//public class FRTest : MonoBehaviour {
//
//	// Use this for initialization
//	void Start () {
//        GVRManager gmgr = GVRManager.Instance;
//        FrameRegulator frm = gmgr.getFrameRegulator();
//
//        // FR startup logic has been moved to GVRManager. Use this only for debugging exceptions.
//        if (false)
//        {
//            GameObject debug = GameObject.Find("Text");
//            Text text = debug.GetComponent<Text>();
//            frm.setDebug(text);
//            frm.initialize();
//            bool succ = frm.start();
//
//            if (!succ)
//            {
//                text.text = "Failed to start";
//            }
//            else
//            {
//                text.text = "start() successfully";
//            }
//        }
//    }
//	
//	// Update is called once per frame
//	void Update () {
//	
//	}
//}
