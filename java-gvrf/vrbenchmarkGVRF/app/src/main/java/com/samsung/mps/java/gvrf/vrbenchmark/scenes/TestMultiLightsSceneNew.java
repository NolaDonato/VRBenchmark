
package com.samsung.mps.java.gvrf.vrbenchmark.scenes;

import android.util.Log;

import com.samsung.mps.java.gvrf.vrbenchmark.BenchMarkController;
import com.samsung.mps.java.gvrf.vrbenchmark.FPSCounter;
import com.samsung.mps.java.gvrf.vrbenchmark.ResultData;
import com.samsung.mps.java.gvrf.vrbenchmark.SceneExt;
import com.samsung.mps.java.gvrf.vrbenchmark.TestCompleteListener;
import com.samsung.mps.java.gvrf.vrbenchmark.TestDescOnCameraRig;
import com.samsung.mps.java.gvrf.vrbenchmark.Utils;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRLightBase;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters.TextureWrapType;
import org.gearvrf.IAssetEvents;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class TestMultiLightsSceneNew extends SceneExt {

    private static final String TAG = "Test_MultiLightsScene";

    private static final String SCENE_NAME = "Multiple Lights";

    private static final int SCENE_ID = 3;
    private TestCompleteListener mTestCompleteListener;
    private GVRContext mGVRContext;

    private final String HEAVY_FBX_NAME = "TestLightsNew.FBX";
    private final String LIGHT_FBX_NAME = "TestLightsNew_NoLights.FBX";
    private final String HEAVY_FBX_PATH = "Common/" + HEAVY_FBX_NAME;
    private final String LIGHT_FBX_PATH = "Common/" + LIGHT_FBX_NAME;
    private long mStartTime = -1;
    private GVRSceneObject mPtLightNode1, mPtLightNode2, mPtLightNode3, mPtLightNode4,
            mPtLightNode5, mPtLightNode6;

    private ArrayList<GVRSceneObject> listObjTexWrapping = new ArrayList<GVRSceneObject>();
    private FPSCounter mFpsCounter;
    private LinkedHashMap<String, String> mTestResultData = new LinkedHashMap<String, String>();

    private GVRSceneObject mSceneObjectLightModel;
    boolean mIsHeavyLoadTest = false;
    private TestDescOnCameraRig mTestDescDisp;

    public TestMultiLightsSceneNew(GVRContext gvrContext) {
        super(gvrContext);
        mGVRContext = gvrContext;
        mFpsCounter = new FPSCounter();

    }

    public void onInit() {
        mGVRContext.getEventReceiver().addListener(assetEventListener);
        // set background color
        GVRCameraRig mainCameraRig = this.getMainCameraRig();
        mainCameraRig.getTransform().setPosition(0, 1.5f, 3.5f);

        mTestDescDisp = new TestDescOnCameraRig(mGVRContext,mainCameraRig);
        mTestDescDisp.setTestName("MultiLights Test");

        try {
            mGVRContext.getAssetLoader().loadModel(LIGHT_FBX_PATH);
        } catch (Exception e) {

            Log.e(TAG, "exception: ", e);
        }

    }

    @Override
    public void setExtraInfo(String info) {

    }

    @Override
    public String getSceneName() {
        return SCENE_NAME;
    }


    private void afterModelLoad(GVRSceneObject model, String filePath) {
        if (filePath.equals(LIGHT_FBX_NAME)) {
            mSceneObjectLightModel = model;
        }
        model.getTransform().setPosition(0.0f, 0.0f, 0.0f);
        model.getTransform().setRotationByAxis(180.0f, 0, 1, 0);
        model.getTransform().setScale(1.0f, 1.0f, 1.0f);

        GVRSceneObject wall = model.getSceneObjectByName("wall");
        listObjTexWrapping.add(wall);
        GVRSceneObject windowWall2 = model.getSceneObjectByName("windowwall (2)");
        listObjTexWrapping.add(windowWall2);

        setMaterials(model);
        updateLights(model);
        if (mIsHeavyLoadTest) {
            removeSceneObject(mSceneObjectLightModel);
        }
        addSceneObject(model);

        mStartTime = System.currentTimeMillis();
    }


    IAssetEvents assetEventListener = new IAssetEvents() {
        @Override
        public void onAssetLoaded(GVRContext context, GVRSceneObject model, String filePath,
                                  String errors) {
            Log.d(TAG, "onAssetLoaded : filename is: " + filePath);
        }

        @Override
        public void onModelLoaded(GVRContext context, GVRSceneObject model, String filePath) {
            Log.d(TAG, "modelloaded : filename is: " + filePath);
            afterModelLoad(model, filePath);
        }

        @Override
        public void onTextureLoaded(GVRContext context, GVRTexture texture, String filePath) {

        }

        @Override
        public void onModelError(GVRContext context, String error, String filePath) {

        }

        @Override
        public void onTextureError(GVRContext context, String error, String filePath) {

        }
    };


    private void updateLights(GVRSceneObject model) {

        GVRSceneObject dirLightNode = model.getSceneObjectByName("Directional Light");
        setLightParams(dirLightNode);
        mPtLightNode1 = model.getSceneObjectByName("Point Light");
        setLightParams(mPtLightNode1);
        mPtLightNode2 = model.getSceneObjectByName("Point Light (2)");
        setLightParams(mPtLightNode2);
        mPtLightNode3 = model.getSceneObjectByName("Point Light (3)");
        setLightParams(mPtLightNode3);
        mPtLightNode4 = model.getSceneObjectByName("Point Light (4)");
        setLightParams(mPtLightNode4);
        mPtLightNode5 = model.getSceneObjectByName("Point Light (5)");
        setLightParams(mPtLightNode5);
        mPtLightNode6 = model.getSceneObjectByName("Point Light (6)");
        setLightParams(mPtLightNode6);
    }

    private void setLightParams(GVRSceneObject lightNode) {
        if (lightNode == null) {
            return;
        }
        GVRLightBase light = (GVRLightBase) lightNode.getLight();

        if (lightNode.getName().equals("Directional Light")) {
            GVRDirectLight dl = (GVRDirectLight) light;
            if (mIsHeavyLoadTest) {
                dl.getTransform().rotateByAxis(-60, 1, 0, 0);
                dl.getTransform().rotateByAxis(45, 0, 1, 0);
                dl.getTransform().rotateByAxis(0, 0, 0, 1);
            } else {
                dl.getTransform().setPosition(0, 3, 0);
                dl.getTransform().rotateByAxis(110, -1, 0, 0); //60 + 50
                dl.getTransform().rotateByAxis(75, 0, -1, 0);
            }
        } else {
            GVRPointLight pl = (GVRPointLight) light;
            // range made 75 based on
            // http://www.ogre3d.org/tikiwiki/-Point+Light+Attenuation
            pl.setAttenuation(1.0f, 0.06f, 0.013f);
        }
    }

    public void onStep() {
        if (mStartTime != -1) {
            float fps = mFpsCounter.tick();
            if(fps >= 0) {
                mTestDescDisp.setFps(fps);
                if (mIsHeavyLoadTest) {
                    mTestDescDisp.setSubTestName("Heavy Scene");
                } else {
                    mTestDescDisp.setSubTestName("Light Scene");
                }
                mTestDescDisp.displayTestData();
            }
        }

        animatePointLights();

        long currTime = System.currentTimeMillis();
        if ((currTime - mStartTime) >= (BenchMarkController.TEST_DURATION_IN_MILLISECS * 2)) {
            if (mIsHeavyLoadTest) {
                storeResult(mIsHeavyLoadTest);
                sendResult();
            } else {
                storeResult(mIsHeavyLoadTest);
                try {
                    //start heavy load test
                    mGVRContext.getAssetLoader().loadModel(HEAVY_FBX_PATH);
                } catch (Exception e) {
                    Log.e(TAG, "exception: ", e);
                }
                mIsHeavyLoadTest = true;
            }
        }
    }

    private void sendResult() {

        ResultData result = new
                ResultData(SCENE_ID, "MultiLights Test", mTestResultData);

        mGVRContext.getEventReceiver().removeListener(assetEventListener);
        assetEventListener = null;

        removeAllSceneObjects();
        mTestDescDisp.removeTestDescDisplay();
        mTestCompleteListener.onResult(result);
    }


    private void storeResult(boolean isHeavyLoad) {
        float averageFps =
                mFpsCounter.getAverageFps();

        if (isHeavyLoad == true) {
            mTestResultData.put("Heavy scene average fps: ", String.format("%4.2f", averageFps));
        } else {
            mTestResultData.put("Light scene average fps: ", String.format("%4.2f", averageFps));
        }


        Log.d(TAG, "average fps is: " + averageFps + " is high load: " + isHeavyLoad);
        //reset fps counter
        mFpsCounter.reset();

    }

    // temp code
    private void setMaterials(GVRSceneObject model) {

        GVRSceneObject glass = model.getSceneObjectByName("glass");
        glass.getRenderData().getMaterial().setOpacity(0);
        glass.getRenderData().setRenderMask(0);

        GVRSceneObject glass2 = model.getSceneObjectByName("glass (2)");
        glass2.getRenderData().getMaterial().setOpacity(0);
        glass2.getRenderData().setRenderMask(0);
        GVRSceneObject glass3 = model.getSceneObjectByName("glass (3)");
        glass3.getRenderData().getMaterial().setOpacity(0);
        glass3.getRenderData().setRenderMask(0);
        GVRSceneObject glass4 = model.getSceneObjectByName("glass (4)");
        glass4.getRenderData().getMaterial().setOpacity(0);
        glass4.getRenderData().setRenderMask(0);

        GVRSceneObject lowerPanel = model.getSceneObjectByName("lowerpanel");
        lowerPanel.getRenderData().getMaterial()
                .setDiffuseColor(0.21926828f, 0.24264705f, 0.17484862f, 1.0f);

        GVRSceneObject lowerPanel2 = model.getSceneObjectByName("lowerpanel (2)");
        lowerPanel2.getRenderData().getMaterial()
                .setDiffuseColor(0.21926828f, 0.24264705f, 0.17484862f, 1.0f);

    }


    private void animatePointLights() {
        if ((mPtLightNode1 == null) || (mPtLightNode2 == null) || (mPtLightNode3 == null)
                || (mPtLightNode4 == null) || (mPtLightNode5 == null) || (mPtLightNode6 == null)) {
            return;
        }
        long currTime = System.currentTimeMillis();
        float timeLeftForTest = currTime - mStartTime;
        float totalTimeForTest = BenchMarkController.TEST_DURATION_IN_MILLISECS;
        if (timeLeftForTest > (totalTimeForTest * (3f / 4f))) {
            movePointLightsLeft();
        } else if (timeLeftForTest > (totalTimeForTest / 2f)) {
            movePointLightsRight();
        } else if (timeLeftForTest > (totalTimeForTest / 4f)) {
            movePointLightsForward();
        } else {
            movePointLightsBackward();
        }
    }

    private void movePointLightsLeft() {
        float moveXBy = -1.0f;
        mPtLightNode1.getTransform().translate(moveXBy, 0, 0);
        mPtLightNode2.getTransform().translate(moveXBy, 0, 0);
        mPtLightNode3.getTransform().translate(moveXBy, 0, 0);
        mPtLightNode4.getTransform().translate(moveXBy, 0, 0);
        mPtLightNode5.getTransform().translate(moveXBy, 0, 0);
        mPtLightNode6.getTransform().translate(moveXBy, 0, 0);
    }

    private void movePointLightsRight() {
        float moveXBy = 1.0f;
        mPtLightNode1.getTransform().translate(moveXBy, 0, 0);
        mPtLightNode2.getTransform().translate(moveXBy, 0, 0);
        mPtLightNode3.getTransform().translate(moveXBy, 0, 0);
        mPtLightNode4.getTransform().translate(moveXBy, 0, 0);
        mPtLightNode5.getTransform().translate(moveXBy, 0, 0);
        mPtLightNode6.getTransform().translate(moveXBy, 0, 0);
    }

    private void movePointLightsForward() {
        float moveZBy = -1.0f;
        mPtLightNode1.getTransform().translate(0, 0, moveZBy);
        mPtLightNode2.getTransform().translate(0, 0, moveZBy);
        mPtLightNode3.getTransform().translate(0, 0, moveZBy);
        mPtLightNode4.getTransform().translate(0, 0, moveZBy);
        mPtLightNode5.getTransform().translate(0, 0, moveZBy);
        mPtLightNode6.getTransform().translate(0, 0, moveZBy);
    }

    private void movePointLightsBackward() {
        float moveZBy = 1.0f;
        mPtLightNode1.getTransform().translate(0, 0, 0);
        mPtLightNode1.getTransform().translate(0, 0, moveZBy);
        mPtLightNode2.getTransform().translate(0, 0, moveZBy);
        mPtLightNode3.getTransform().translate(0, 0, moveZBy);
        mPtLightNode4.getTransform().translate(0, 0, moveZBy);
        mPtLightNode5.getTransform().translate(0, 0, moveZBy);
        mPtLightNode6.getTransform().translate(0, 0, moveZBy);
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
