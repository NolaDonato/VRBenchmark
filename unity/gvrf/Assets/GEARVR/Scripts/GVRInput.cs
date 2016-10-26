using UnityEngine;
using System.Collections;

public class GVRInput : MonoBehaviour {


	//static variable to check tap event
	public static bool tapEvent, longPressEvent;
	//static variable to check swipe event;
	public static GVRSwipeEvent swipeEvent;

	private float longPressTimer = 0;

	public float xTrashold = 0.05f, yTrashold = 0.05f;
	private static Vector3  touchLastPos;

	void Start(){
		//Create Swipe Event Instance
		GVRInput.swipeEvent = new GVRSwipeEvent ();
	}

	void Update () {


		//Reset Click Every Frame
		tapEvent = false;
		//Reset LongPress Every Frame
		longPressEvent = false;
		//Reset swipe event
		swipeEvent.SetSwipe(GVRSwipeEvent.SwipeType.none);

		if (Input.GetButtonDown ("Fire1")) {
			longPressTimer = Time.time;
			touchLastPos = Input.mousePosition;
		}
		if(Input.GetButtonUp("Fire1")){
			Debug.Log(string.Format("GVRInput Update () Fire1 "));

			if (touchLastPos != Input.mousePosition) {
				//check for new swipe events
				HandleSwipe ();				
			} 
			if(swipeEvent.swipeType != GVRSwipeEvent.SwipeType.none)
				return;
			if(longPressTimer+.7f < Time.time)
				longPressEvent = true;//Longpress is true
			else
				tapEvent = true;//Tap is true

		}

				
	}

	void HandleSwipe(){

		float xDiff = (touchLastPos.x - Input.mousePosition.x)/Screen.width;
		float yDiff = (touchLastPos.y - Input.mousePosition.y)/Screen.height;
		// check trashold for both axis
		if (Mathf.Abs (xDiff) < xTrashold && Mathf.Abs (yDiff) < yTrashold)
			return;

		//if Swipe is identified, disable click 
		tapEvent = false;

		if (Mathf.Abs (xDiff) > Mathf.Abs (yDiff)) {
			if (xDiff > 0) {
				swipeEvent.SetSwipe (GVRSwipeEvent.SwipeType.SwipeLeft);
			} else if (xDiff < 0) {
				swipeEvent.SetSwipe (GVRSwipeEvent.SwipeType.SwipeRight);
			} 
		}
		else{
			if (yDiff > 0) {
				swipeEvent.SetSwipe (GVRSwipeEvent.SwipeType.SwipeDown);
			} else if (yDiff < 0) {
				swipeEvent.SetSwipe (GVRSwipeEvent.SwipeType.SwipeUp);
			} 
		}
		//store 
		touchLastPos = Input.mousePosition;

	}
}
