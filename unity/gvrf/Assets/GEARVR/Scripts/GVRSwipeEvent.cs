using UnityEngine;
using System.Collections;

public class GVRSwipeEvent
{

	public enum SwipeType { SwipeUp, SwipeDown, SwipeRight, SwipeLeft, none};
	public SwipeType swipeType = new SwipeType ();
	public GVRSwipeEvent(){

		swipeType = SwipeType.none;
	}

	public void SetSwipe(SwipeType swipe){

		swipeType = swipe;
	}

}

