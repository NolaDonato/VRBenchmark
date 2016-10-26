
package com.samsung.mps.java.gvrf.vrbenchmark.scenes;

import com.samsung.mps.java.gvrf.vrbenchmark.ResultData;
import com.samsung.mps.java.gvrf.vrbenchmark.SceneExt;
import com.samsung.mps.java.gvrf.vrbenchmark.TestCompleteListener;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;

public class StartScene extends SceneExt {
    private static final String TAG = "vrbenchmark_StartScene";
    public static final float DISPLAY_DURATION_IN_MILLISECS = 10.0f * 1000.0f;
    // seconds
    private static final float WIDTH_SCENE_OBJ = 1500;
    private static final float HEIGHT_SCENE_OBJ = 700;
    private static final int SCENE_ID = 0;
    private static final String SCENE_NAME = "Start";
    private TestCompleteListener mTestCompleteListener;
    private GVRContext mGVRContext;
    private GVRSceneObject mRootSceneObject;
    private final String FBX_PATH = "benchmark-fw/startLogo.FBX";
    private long mStartTime;
    private GVRTextViewSceneObject mTextSceneObj;

    public StartScene(GVRContext gvrContext) {
        super(gvrContext);
        mGVRContext = gvrContext;
    }

    public void onInit() {
        mStartTime = System.currentTimeMillis();
        this.getMainCameraRig().reset();

        try {
            mRootSceneObject = mGVRContext.getAssetLoader().loadModel(FBX_PATH, this);
            mRootSceneObject.getTransform().setPosition(-1.234497f, 0.0f, -707f);
            mRootSceneObject.getTransform().setScale(0.7149087f, 0.7149087f, 0.7149087f);
            mTextSceneObj = createSceneObjForTestDisplay();
            displayConfigInfo();
        } catch (Exception e) {
            Log.e(TAG, "exception: ", e);
        }

    }

    private void displayConfigInfo() {
        String packageName = mGVRContext.getContext().getPackageName();
        String strConfig = new String();
        if (packageName.contains("pure")) {
            strConfig = "GVRF + PURE BACKEND";
        } else if (packageName.contains("hybrid")) {
            strConfig = "GVRF + OCULUS BACKEND";
        }
        mTextSceneObj.setText(strConfig);
    }

    @Override
    public void setExtraInfo(String info) {

    }

    @Override
    public String getSceneName() {
        return SCENE_NAME;
    }


    public void onStep() {
        long currTime = System.currentTimeMillis();
        if ((currTime - mStartTime) >= DISPLAY_DURATION_IN_MILLISECS) {
            ResultData result = new ResultData(SCENE_ID, "StartScene", null);
            removeSceneObject(mRootSceneObject);
            mTestCompleteListener.onResult(result);
        }
    }

    private GVRTextViewSceneObject createSceneObjForTestDisplay() {
        if (mGVRContext == null) {
            return null;
        }

        GVRTextViewSceneObject textViewSceneObject = new GVRTextViewSceneObject(
                mGVRContext, WIDTH_SCENE_OBJ, HEIGHT_SCENE_OBJ, "");
        textViewSceneObject.getTransform().translate(0, 100.0f, 1.0f);
        textViewSceneObject.setTextSize(6.0f);
        textViewSceneObject.setTextColor(Color.WHITE);
        textViewSceneObject.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        //  textViewSceneObject.setBackgroundColor(Color.YELLOW);
        mRootSceneObject.addChildObject(textViewSceneObject);
        GVRSceneObject bgNode = mRootSceneObject.getSceneObjectByName("Background");
        bgNode.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.BACKGROUND);
        textViewSceneObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        return textViewSceneObject;
    }

    @Override
    public void registerTestCompleteListener(TestCompleteListener testCompleteListener) {
        mTestCompleteListener = testCompleteListener;

    }

    @Override
    public int getSceneID() {
        return SCENE_ID;

    }

}
