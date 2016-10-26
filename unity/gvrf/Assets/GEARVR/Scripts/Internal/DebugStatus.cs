//using UnityEngine;
//using System.Collections;
//using UnityEngine.UI;
//using System.Collections.Generic;
//using System.Text;
//using System.Linq;
//
//public class DebugStatus : MonoBehaviour
//{
//    public static readonly int ENTRY_TIMEWARP = 0;
//    public static readonly int ENTRY_SENSOR = 1;
//	public static readonly int ENTRY_SLOW_RENDER = 2;
//	public static readonly int ENTRY_PREDICT_SSE = 3; // from java
//
//    Text text;
//    float timer;
//    static readonly int MAX_ENTRIES = 5;
//
//    List<string> contents = new List<string>(MAX_ENTRIES);
//
//    // Use this for initialization
//    void Start()
//    {
//        sInstance = this;
//        contents.AddRange(Enumerable.Repeat("", MAX_ENTRIES));
//        text = GetComponent<Text>();
//    }
//
//    public void setEntry(int id, string msg)
//    {
//        if (id < 0 || id >= MAX_ENTRIES)
//        {
//            return;
//        }
//
//        contents[id] = msg;
//
//        StringBuilder sb = new StringBuilder();
//        foreach (string line in contents)
//        {
//            if (line != null)
//            {
//                sb.AppendLine(line);
//            }
//        }
//
//        text.text = sb.ToString();
//    }
//
//    private static DebugStatus sInstance;
//
//    public static DebugStatus Instance
//    {
//        get
//        {
//            return sInstance;
//        }
//    }
//}
