//using UnityEngine;
//using UnityEngine.UI;
//using System.Collections;
//
//public class ShowEvents : MonoBehaviour {
//
//	public Toggle tapToggle, longPressToggle, rightToggle, upToggle, leftToggle, downToggle ;
//	//Select what kind of event will be checked
//
//	bool showTouchpadEvents = true;
//	void Start () {
//
//	}
//	
//	// Update is called once per frame
//	void Update () {
//	
//		if (showTouchpadEvents) {
//			tapToggle.isOn = GVRInput.tapEvent;
//			longPressToggle.isOn = GVRInput.longPressEvent;
//			rightToggle.isOn = GVRInput.swipeEvent.swipeType == GVRSwipeEvent.SwipeType.SwipeRight;
//			upToggle.isOn = GVRInput.swipeEvent.swipeType == GVRSwipeEvent.SwipeType.SwipeUp;
//			leftToggle.isOn = GVRInput.swipeEvent.swipeType == GVRSwipeEvent.SwipeType.SwipeLeft;
//			downToggle.isOn = GVRInput.swipeEvent.swipeType == GVRSwipeEvent.SwipeType.SwipeDown;
//
//		} 
//	}
//
//	public void ShowHideTouchEvents(){
//
//		showTouchpadEvents = !showTouchpadEvents;
//		tapToggle.gameObject.SetActive(showTouchpadEvents);
//		longPressToggle.gameObject.SetActive(showTouchpadEvents);
//		rightToggle.gameObject.SetActive(showTouchpadEvents);
//		upToggle.gameObject.SetActive(showTouchpadEvents);
//		leftToggle.gameObject.SetActive(showTouchpadEvents);
//		downToggle.gameObject.SetActive(showTouchpadEvents);
//	}
//}
