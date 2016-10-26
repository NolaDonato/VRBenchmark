#if UNITY_ANDROID && !UNITY_EDITOR
#define ANDROID_PLUGIN
#endif

// Make sure DEBUG and DEBUG_BOARD are undefined for production
#define DEBUG_
#define DEBUG_BOARD_

using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System;
using System.Runtime.InteropServices;

// This is the Unity's interface into Frame Regulator implementation
public class FrameRegulator {
#if ANDROID_PLUGIN
    public delegate void JavaMessageCallbackDelegate(int msgId, float w, float x, float y, float z);

    [DllImport ("gvrf")]
	private static extern void SetJavaMessageCallback(JavaMessageCallbackDelegate fp);

	[DllImport ("gvrf")]
	private static extern long GetCurrentTime();
#endif
    public const bool TEST_DRAW_TO_SHOW_SYNC = false;

    // Specify the number of draw buffers: need to be consistent with native and Java
    public const int BUFFER_NUM = 3;
    public const bool USE_PREDICTION = true;

    // Before drawing each frame, wait for signal from eye-show.
    // TODO: This can reduce FPS and needs to be REVISITed
    public const bool WAIT_FOR_PULSE = false;
    
    // GVRf configuration file in StreamingAssets
    private const string sGvrXmlFile = "Xml/gvr.xml";
    private const bool sEnableFrameRegulator = true;
    
    // Singleton
    private static FrameRegulator sInstance;
    protected AndroidJavaObject gvrUnityPlugin;

	// Buffer management
	private long bufferCnt;
	private int bufferIdx;

    // Prediction
    private static readonly bool USE_ANDROIDJAVAPROXY = false; // AndroidJavaProxy is unsafe for Unity 5.3
    private static UnityEngine.Quaternion predictedPose = new UnityEngine.Quaternion();

	// Debug
	private long frameStartTime;
	private long frameEndTime;
	private long warnFrameTimeNanos = (long)(1000000000 / 60 * 1.0);
	private int slowFrameCnt;
	private int totalFrameCnt;

    private FrameRegulator() {
		Debug.Log("SensorManager::FrameRegulator");
#if ANDROID_PLUGIN
        debugBoardCb = new DebugBoardCallback();

		SetJavaMessageCallback(new JavaMessageCallbackDelegate(javaMessageCbProc));
#endif
		bufferCnt = 0;
		bufferIdx = 0;
    }

    public static FrameRegulator Instance
    {
        get
        {
            if (sInstance == null)
            {
                sInstance = new FrameRegulator();
            }
            return sInstance;
        }
    }

    // API

    Text txtDebug = null;
    public void setDebug(Text debugText)
    {
        txtDebug = debugText;
    }

    class DebugBoardCallback : AndroidJavaProxy
    {
        public DebugBoardCallback() : base("org.gearvrf.GVRUnityPlugin$DebugBoardListener") { }
        void print(string msg)
        {
#if DEBUG_BOARD
            Debug.Log(msg);
            DebugBoard.Instance.print(msg);
#endif
        }

        void setEntry(int id, string msg)
        {
#if DEBUG_BOARD
            if (DebugStatus.Instance != null) {
                DebugStatus.Instance.setEntry(id, msg);
            }
#endif
        }

        void setEyeShowFPS(float fps)
        {
            if (EyeShowFPS.Instance != null) {
            	EyeShowFPS.Instance.setFPS(fps);
			}
        }
    }

    private DebugBoardCallback debugBoardCb;

    // Initialize the Frame Regulator and establish links with Framework
    public void initialize()
    {
        Debug.Log("FrameRegulator::initialize");
        linkToJava();

        if (gvrUnityPlugin != null)
        {
#if DEBUG_BOARD
            if (!USE_ANDROIDJAVAPROXY)
            {
                // Log warning if USE_ANDROIDJAVAPROXY is not enabled
                Debug.Log("WARNING: Using AndroidJavaProxy for debugging.... may crash in Unity 5.3. Make sure DEBUG_BOARD is disabled in production.");
            }
            gvrUnityPlugin.Call("setDebugBoardListener", debugBoardCb);
#endif
        }
    }

    // Start the view. If FR is enabled, it runs the ShowEye thread; if not, it
    // enables the distorter.
    public bool start()
    {
        if (gvrUnityPlugin == null)
            return false;

        return gvrUnityPlugin.Call<bool>("start");
    }

    // Stop the view. If FR is enabled, stops the ShowEye thread; if not, it does nothing.
    public void stop()
    {
        if (gvrUnityPlugin == null)
            return;

        gvrUnityPlugin.Call("stop");
    }

    public bool isReady()
    {
        return gvrUnityPlugin != null;
    }

    public void beforeDraw()
    {
        if (gvrUnityPlugin == null)
            return;

		gvrUnityPlugin.Call("beforeDraw", getBufferIdx());
    }

    public void setKSensor(bool useKSensor) {
        if (gvrUnityPlugin == null)
            return;
        gvrUnityPlugin.Call("setKSensor", useKSensor);
    }

	public void incBufferCount() {
		bufferCnt++;
		if (++bufferIdx == BUFFER_NUM) {
			bufferIdx = 0;
		}
	}

	public long getBufferCount() {
		return bufferCnt;
	}

	public int getBufferIdx() {
		return bufferIdx;
	}

	// Must match GVRUnityPlugin.java
	enum MsgId {
		QUATERNION = 1,
		POLL_SENSOR = 2,
        FLASH_SCREEN = 90,
        CLEAR_BUFFER = 91,
	};

    static void javaMessageCbProc(int msgId, float w, float x, float y, float z)
    {
		//Debug.Log("javaMessageCbProc msgId" +msgId);

		switch (msgId) {
		case (int)MsgId.QUATERNION:
            predictedPose.w = w;
            predictedPose.x = x;
            predictedPose.y = y;
            predictedPose.z = z;
			break;

		case (int)MsgId.POLL_SENSOR:
			SensorManager.Instance.poll();
			break;
                
        case (int)MsgId.FLASH_SCREEN:
#if DEBUG_BOARD
			if (DebugBoard.Instance != null) {				
				DebugBoard.Instance.flash();
			}
#endif
			break;

        case (int)MsgId.CLEAR_BUFFER:
                if (GVRCamera.getDebugInstance() != null)
                {
                    GVRCamera.getDebugInstance().debugClearBuffer((int)(w + .1));
                }
            break;
        }
    }

    public UnityEngine.Quaternion predict(bool isRight)
    {
        gvrUnityPlugin.Call("predict", null, bufferIdx, isRight);

#if DEBUG
        Debug.Log(string.Format("predict w:{0} x:{1} y:{2} z:{3}", predictedPose.w, predictedPose.x,
                predictedPose.y, predictedPose.z));
#endif

        return predictedPose;
    }

	public void beginFrame() {
#if ANDROID_PLUGIN
		frameStartTime = GetCurrentTime();
#endif
	}

	public void endFrame() {
#if ANDROID_PLUGIN
		#if DEBUG_BOARD
		frameEndTime = GetCurrentTime();
		totalFrameCnt++;
		if (frameEndTime - frameStartTime > warnFrameTimeNanos) {
	        if (DebugBoard.Instance != null) {
				DebugBoard.Instance.flash();
	        }
		}
		#endif // DEBUG_BOARD
#endif // ANDROID_PLUGIN
    }

    private void linkToJava()
    {
        string result = "Not android";

#if ANDROID_PLUGIN
        try
        {
            AndroidJavaClass cls_GVRUnityPlugin = new AndroidJavaClass("org.gearvrf.GVRUnityPlugin");
            if (cls_GVRUnityPlugin == null)
            {
                result = "ERROR: cannot find the class org.gearvrf.GVRUnityPlugin!";
            }
            else
            {
                gvrUnityPlugin = cls_GVRUnityPlugin.CallStatic<AndroidJavaObject>("get");
                if (gvrUnityPlugin == null)
                {
                    result = "ERROR: gvrUnityPlugin is null";
                }
                else
                {
                    // Initialize FrameRegulator
                    AndroidJavaClass jcUnityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
                    AndroidJavaObject joActivity = jcUnityPlayer.GetStatic<AndroidJavaObject>("currentActivity");

                    bool initOK = gvrUnityPlugin.Call<bool>("initialize", joActivity, sGvrXmlFile, sEnableFrameRegulator);
                    result = "OK: " + initOK.ToString();
                }
            }
        }
        catch (Exception e)
        {
            result = e.ToString();
        }
#endif

#if DEBUG
        if (txtDebug != null)
        {
            txtDebug.text = result;
        }
        Debug.Log("FrameRegulator: " + result);
#endif
    }
}
