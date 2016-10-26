//using UnityEngine;
//using System.Collections;
//using UnityEngine.UI;
//using System.Collections.Generic;
//using System.Text;
//
//public class DebugBoard : MonoBehaviour
//{
//    Text text;
//    float timer;
//    LinkedList<string> contents = new LinkedList<string>();
//    readonly int nLines = 10;
//	string fullText;
//
//	int mFlashTriggered = 0;
//	Color mFlashColor;
//	Color mOriginalColor;
//
//	static readonly int sFlashFrames = 1;
//
//    // Use this for initialization
//    void Start()
//    {
//        sInstance = this;
//        text = GetComponent<Text>();
//		mOriginalColor = text.color;
//		fullText = text.text;
//    }
//
//	void Update() {
//		if (mFlashTriggered == 0)
//			return;
//		
//		if (mFlashTriggered == 1) {
//			text.color = mFlashColor;
//			text.text = fullText;
//		}
//
//		if (mFlashTriggered > 0 && mFlashTriggered <= sFlashFrames) {
//			mFlashTriggered++;
//			return;
//		} else {
//			mFlashTriggered = 0;
//			text.color = mOriginalColor;
//			text.text = fullText;
//		}
//	}
//
//    public void print(string msg)
//    {
//        if (contents.Count == nLines)
//        {
//            contents.RemoveFirst();
//        }
//        contents.AddLast(msg);
//
//        StringBuilder sb = new StringBuilder();
//        foreach (string line in contents)
//        {
//            sb.AppendLine(line);
//        }
//
//		fullText = sb.ToString();
//		text.text = fullText;
//    }
//
//	public void flash() {
//		flash(Color.red);
//	}
//
//	public void flash(Color c) {
//		mFlashColor = c;
//		mFlashTriggered = 1;
//	}
//
//	private static DebugBoard sInstance;
//    
//    public static DebugBoard Instance
//    {
//        get {
//            return sInstance;
//        }
//    }
//}
