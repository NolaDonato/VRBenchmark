// on OpenGL ES there is no way to query texture extents from native texture id
#if UNITY_ANDROID && !UNITY_EDITOR
	#define UNITY_GLES_RENDERER
#endif

using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;

#if UNITY_GLES_RENDERER
public enum RenderEventID
{
	Rendered = 0,
	Pause = 100,
	Resume = 101,
}
#endif

[DisallowMultipleComponent]

public class GVRCamera : MonoBehaviour {
	private const bool DEBUG_PER_FRAME = false;

	public bool useChromaticAberration = false;
	public bool useTimeWarp = false;
	public bool overrideEyeBufferSize;
	public int eyeBufferSize = 1024;

	private GVRConfigurator configurator;
	[HideInInspector]
	public bool useUnityAntialiasing = false;
	public Camera leftCamera, rightCamera;

	public XMLParser.DEVICES device = XMLParser.DEVICES.Automatic;

	public enum UnityAntialiasingLevel{

		X2Sample= 2, X4Sample = 4, X8Sample = 8
	}

	[HideInInspector]
	public UnityAntialiasingLevel antialiasingLevel = UnityAntialiasingLevel.X2Sample;
    public RenderTexture[] rightTexture;
    public RenderTexture[] leftTexture;

    GVRManager gvrm;
    UnityEngine.Quaternion curRotation = new UnityEngine.Quaternion();

    // For debugging
    static GVRCamera sGVRCamera;
    static int sDrawCount;

    /// </summary>

#if UNITY_GLES_RENDERER

	[DllImport ("gvrf")]
	private static extern void GVRInitializer();

	[DllImport ("gvrf")]
	private static extern void SetLeftTextureFromUnity(int bufferId, int textureId, int w, int h);

	[DllImport ("gvrf")]
	private static extern void SetRightTextureFromUnity(int bufferId, int textureId, int w, int h);

	[DllImport ("gvrf")]
	private static extern void SetViewportParameters(float mrenderDiameterMeters, float mrealScreenWidthMeters, float mrealScreenWidthPixels,
	                                                 float mrealScreenHeightPixels, float mshiftScreenCenterMeters, float mlensesDPIMeters);
	[DllImport ("gvrf")]
	private static extern void SetGridParameters(int mdistortion_grid_size, float mk0, float mk1, float mk2, float mk3, float mscale, float mfreeParam1, float mfreeparam2);

	[DllImport ("gvrf")]
	private static extern void SetChromaticAberration(bool chromaticAberration);

	[DllImport ("gvrf")]
	private static extern void SetTimeWarp(bool timeWarp);

	[DllImport ("gvrf")]
	private static extern void SetBufferCnt(long bufCnt);

	[DllImport ("gvrf")]
	private static extern void BeginRendering(int bufferIdx, bool isRight);

	[DllImport ("gvrf")]
	private static extern void EndRendering(int bufferIdx, bool isRight);

    [DllImport ("gvrf")]
	private static extern void DebugFunc(int cmd);
#endif

    void Awake(){
		Debug.Log("GVRCamera::Awake");

		if(device == XMLParser.DEVICES.Automatic){
			if (SystemInfo.deviceModel.ToString ().Trim().Contains ("N910"))
				device = XMLParser.DEVICES.SamsungGalaxyNote4;
			else if (SystemInfo.deviceModel.ToString ().Trim().Contains ("G925") || SystemInfo.deviceModel.ToString ().Trim().Contains ("G920"))
				device = XMLParser.DEVICES.SamsungGalaxyS6;
			else
				device = XMLParser.DEVICES.SamsungGalaxyNote4;
		}
#if UNITY_GLES_RENDERER
		GVRInitializer(); //TODO: find a correct location after code cleaning

		Debug.Log("GVRCamera::Awake vSyncCount = 0;");

		// we don't want Unity's vsync as the triggering will be handled by GVRf plugin
		QualitySettings.vSyncCount = 0; // VSync must be disabled for this to work (set vSyncCount to 0)
		Application.targetFrameRate = 60; // can be set bigger, but it will be controlled GVRf plugin
#endif
	}

    private void initTextureArray() {
        if (rightTexture == null)
        {
            rightTexture = new RenderTexture[FrameRegulator.BUFFER_NUM];
        }

        if (leftTexture == null)
        {
            leftTexture = new RenderTexture[FrameRegulator.BUFFER_NUM];
        }
    }

    void Start () {
		Debug.Log("GVRCamera::Start");
        sGVRCamera = this;

        gvrm = GVRManager.Instance;
        gvrm.onResume();

        rightCamera.enabled = false;
		leftCamera.enabled = false;

        initTextureArray();
        for (int i = 0; i < FrameRegulator.BUFFER_NUM; ++i)
        {
            leftTexture[i] = new RenderTexture(eyeBufferSize, eyeBufferSize, 1);
            rightTexture[i] = new RenderTexture(eyeBufferSize, eyeBufferSize, 1);
        }

#if UNITY_GLES_RENDERER
		gameObject.GetComponent<Camera> ().targetTexture = null;
		gameObject.GetComponent<Camera> ().enabled = false;
		SetDefaultVRProperties(gameObject.GetComponent<Camera> ());
		SetDefaultVRProperties(leftCamera);
		SetDefaultVRProperties(rightCamera);
#endif

#if UNITY_GLES_RENDERER
		GVRConfigurator.DisplayMetricsAndroid ();

		XMLParser deviceXml = XMLParser.instance;
		deviceXml.device = device;
		deviceXml.LoadConfiguration ();

		SetViewportParameters (
			deviceXml.GetRenderDiameterMeters (),
			deviceXml.GetRealScreenWidthMeters (),
			deviceXml.GetRealScreenWidthPixels (),
			deviceXml.GetRealScreenHeightPixels (),
			deviceXml.GetShiftScreenCenterMeters (),
			deviceXml.GetLensesIPDMeters ()
			);

		SetChromaticAberration(useChromaticAberration);
		SetTimeWarp(useTimeWarp);

		GVRInitializer();

#endif // UNITY_GLES_RENDERER

    }

    void Update()
    {
		if (DEBUG_PER_FRAME) {
			Debug.Log("GVRCamera::Update. Camera.enabled? " + gameObject.GetComponent<Camera> ().enabled + ", leftCamera.enabled? " +leftCamera.enabled);
		}

#if UNITY_GLES_RENDERER
		// begin the frame
		FrameRegulator.Instance.beginFrame(); // for debug
#endif

#if UNITY_GLES_RENDERER
		SensorManager sensor = gvrm.getSensorManager();
		FrameRegulator fr = gvrm.getFrameRegulator();

		if (!fr.isReady()) {
			Debug.Log("FR not ready, skip..");
			return;
		}

		long bufCnt = fr.getBufferCount();
		int bufIdx = fr.getBufferIdx();

		if (DEBUG_PER_FRAME) {
			Debug.Log("SetBufferCnt bufCnt " + bufCnt + ", bufIdx=" + bufIdx);
		}
		SetBufferCnt(bufCnt);

		// Predict left camera and render
		if (FrameRegulator.USE_PREDICTION) {
			UnityEngine.Quaternion predicted = fr.predict(false);
			leftCamera.transform.rotation = convertSensorToCamera(predicted);
			if (DEBUG_PER_FRAME) {
				Debug.Log(string.Format("left predict {0} {1} {2} {3}", predicted.w, predicted.x, predicted.y, predicted.z));
			}
		}

		BeginRendering(bufIdx, false);
		leftCamera.targetTexture = leftTexture[bufIdx];
		leftCamera.Render();
		EndRendering(bufIdx, false);

		// Predict right camera and render
		if (FrameRegulator.USE_PREDICTION) {
			UnityEngine.Quaternion predicted = fr.predict(true);
			rightCamera.transform.rotation = convertSensorToCamera(predicted);
			if (DEBUG_PER_FRAME) {
				Debug.Log(string.Format("right predict {0} {1} {2} {3}", predicted.w, predicted.x, predicted.y, predicted.z));
			}
		}

		BeginRendering(bufIdx, true);
		rightCamera.targetTexture = rightTexture[bufIdx];
		rightCamera.Render();
		EndRendering(bufIdx, true);

		for (int i = 0; i < FrameRegulator.BUFFER_NUM; i++) {
			SetLeftTextureFromUnity(i, leftTexture[i].GetNativeTextureID(), leftTexture[i].width, leftTexture[i].height);
			SetRightTextureFromUnity(i, rightTexture[i].GetNativeTextureID(), rightTexture[i].width, rightTexture[i].height);
		}

		if (FrameRegulator.TEST_DRAW_TO_SHOW_SYNC) {
			if (sDrawCount++ % 10 != 0) {
				FrameRegulator.Instance.endFrame(); // for debug
				return;
			}
			DebugFunc(1 /* glFlush */);
			System.Threading.Thread.Sleep(500);
		}
#endif

#if UNITY_GLES_RENDERER
		// end the current frame: this part can be splitted to onPostRender as needed
		if (DEBUG_PER_FRAME) {
			Debug.Log("GL.IssuePluginEvent " + (int)RenderEventID.Rendered);
		}
		GL.IssuePluginEvent((int)RenderEventID.Rendered);

		// Update mBufferIdx for next frame.
		fr.incBufferCount();

		FrameRegulator.Instance.endFrame(); // for debug
#endif
    }


#if UNITY_GLES_RENDERER

    void OnApplicationPause(bool pauseStatus) {
		
        Debug.Log("OnApplicationPause(): " + pauseStatus);
        //paused = pauseStatus;

        // This will be serialized in the event queue with the same thread.
        // Another approach can be using Player's Activity class, but it can
        // interrupt/intervene the on-going gl thread in the middle of drawing
        // in a sudden way. By queuing the Pause/Resume events, it would best have
        // chance of smoothly handling GVRf’s internal states, including thread
        // shutdown.
        if (pauseStatus) {
                GL.IssuePluginEvent((int)RenderEventID.Pause);
                if (gvrm != null) gvrm.onPause();
        } else {
                if (gvrm != null) gvrm.onResume();
                GL.IssuePluginEvent((int)RenderEventID.Resume);
        }
    }

    void OnApplicationFocus(bool focusStatus) {
        Debug.Log("OnApplicationFocus(): " + focusStatus);
        if (focusStatus) {
            // Disable screen dimming
            Screen.sleepTimeout = SleepTimeout.NeverSleep;
        } else {
            // Set screen dimming as system default
            Screen.sleepTimeout = SleepTimeout.SystemSetting;
        }
    }

#endif

    public void SetRightTexture(RenderTexture tex){
        initTextureArray();
        rightTexture[0] = tex;
	}

	public void SetLeftTexture(RenderTexture tex){
        initTextureArray();
        leftTexture[0] = tex;
	}

	public void SetRightCamera(Camera cam){
		rightCamera = cam;
	}

	public void SetLeftCamera(Camera cam){
		leftCamera = cam;
	}

	public void SetDefaultVRProperties(Camera cam){
		cam.fieldOfView = 90;
		cam.rect = new Rect (0, 0, 1, 1);
	}

	public Transform GetRoot(){
		return transform.FindChild ("GVRCameraRig");
	}

	void OnDestroy(){
		Debug.Log("GVRCamera::OnDestroy");
        for (int i = 0; i < FrameRegulator.BUFFER_NUM; ++i)
        {
            rightTexture[i].Release();
            leftTexture[i].Release();
        }
	}

	public static UnityEngine.Quaternion convertSensorToCamera(UnityEngine.Quaternion from) {
		if (DEBUG_PER_FRAME) {
			Debug.Log ("GVRCamera::convertSensorToCamera");
		}
		if (SensorManager.Instance.isUsingKSensor ()) {
			if (DEBUG_PER_FRAME) {
				Debug.Log ("GVRCamera::convertSensorToCamera KSensor.toCamera (from);");
			}
			return KSensor.toCamera (from);
		} else {
			if (DEBUG_PER_FRAME) {
				Debug.Log ("GVRCamera::convertSensorToCamera AndroidRotationSensor.toCamera (from);");
			}
			return AndroidRotationSensor.toCamera (from);
		}
	}

    // For debug
    public static GVRCamera getDebugInstance()
    {
        return sGVRCamera;
    }

    public void debugClearBuffer(int bufferIdx)
    {
        RenderTexture current = RenderTexture.active;

        RenderTexture.active = leftTexture[bufferIdx];
        GL.Clear(true, true, Color.red);

        RenderTexture.active = rightTexture[bufferIdx];
        GL.Clear(true, true, Color.red);

        RenderTexture.active = current;
    }
}
