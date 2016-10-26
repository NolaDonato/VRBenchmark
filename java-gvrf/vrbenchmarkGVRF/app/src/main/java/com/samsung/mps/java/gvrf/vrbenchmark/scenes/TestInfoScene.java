
package com.samsung.mps.java.gvrf.vrbenchmark.scenes;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;

import com.samsung.mps.java.gvrf.vrbenchmark.ResultData;
import com.samsung.mps.java.gvrf.vrbenchmark.SceneExt;
import com.samsung.mps.java.gvrf.vrbenchmark.TestCompleteListener;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;

public class TestInfoScene extends SceneExt {
    private static final String TAG = "vrbenchmark_TestInfo";
    public static final float DISPLAY_DURATION_IN_SECS = 2.0f * 1000.0f; // 10
    // seconds
    private static final int SCENE_ID = -1;
    private static final String SCENE_NAME = "TestInfo";

    private static final int BACKGROUND_COLOR = Color.rgb(189, 215, 238);

    private static final float WIDTH_SCENE_OBJ = 1500;
    private static final float HEIGHT_SCENE_OBJ = 700;
    private TestCompleteListener mTestCompleteListener;
    private GVRContext mGVRContext;
    private GVRSceneObject mRootSceneObject;
    private final String FBX_PATH = "benchmark-fw/NextTestInfo.FBX";
    private long mStartTime;
    private GVRTextViewSceneObject mTextSceneObj;

    public TestInfoScene(GVRContext gvrContext) {
        super(gvrContext);
        mGVRContext = gvrContext;
    }

    public void onInit() {
        mStartTime = System.currentTimeMillis();

        // set background color
        GVRCameraRig mainCameraRig = this.getMainCameraRig();
        mainCameraRig.getLeftCamera()
                .setBackgroundColor(Color.BLACK);
        mainCameraRig.getRightCamera()
                .setBackgroundColor(Color.BLACK);
        mainCameraRig.getTransform().setPosition(0, 0, 0.0f);

        try {

            mRootSceneObject = mGVRContext.getAssetLoader().loadModel(FBX_PATH, this);
            mRootSceneObject.getTransform().setPosition(-1.234497f, 0.0f, -707f);
            mRootSceneObject.getTransform().setScale(0.7149087f, 0.7149087f, 0.7149087f);
            mTextSceneObj = createSceneObjForTestDisplay();
        } catch (Exception e) {
            Log.e(TAG, "exception: ", e);
        }

    }

    @Override
    public void setExtraInfo(String info) {
        mTextSceneObj.setText("Starting " + info + " test ...");
    }

    @Override
    public String getSceneName() {
        return SCENE_NAME;
    }


    private GVRTextViewSceneObject createSceneObjForTestDisplay() {
        if (mGVRContext == null) {
            return null;
        }

        GVRTextViewSceneObject textViewSceneObject = new GVRTextViewSceneObject(
                mGVRContext, WIDTH_SCENE_OBJ, HEIGHT_SCENE_OBJ, "");
        textViewSceneObject.getTransform().translate(0, 100.0f, 1.0f);
        textViewSceneObject.setTextSize(6.0f);
        textViewSceneObject.setTextColor(Color.BLACK);
        textViewSceneObject.setGravity(Gravity.CENTER);
        mRootSceneObject.addChildObject(textViewSceneObject);
        GVRSceneObject bgNode = mRootSceneObject.getSceneObjectByName("Background");
        bgNode.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.BACKGROUND);
        textViewSceneObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        return textViewSceneObject;
    }

    public void onStep() {
        long currTime = System.currentTimeMillis();
        if ((currTime - mStartTime) >= DISPLAY_DURATION_IN_SECS) {
            ResultData result = new ResultData(SCENE_ID, "TestInfoScene", null);
            removeSceneObject(mRootSceneObject);
            mTestCompleteListener.onResult(result);
        }
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
