
package com.samsung.mps.java.gvrf.vrbenchmark.scenes;

import com.samsung.mps.java.gvrf.vrbenchmark.BenchMarkController;
import com.samsung.mps.java.gvrf.vrbenchmark.FPSCounter;
import com.samsung.mps.java.gvrf.vrbenchmark.ResultData;
import com.samsung.mps.java.gvrf.vrbenchmark.SceneExt;
import com.samsung.mps.java.gvrf.vrbenchmark.TestCompleteListener;
import com.samsung.mps.java.gvrf.vrbenchmark.TestDescOnCameraRig;

import org.gearvrf.*;
import org.gearvrf.GVRTextureParameters.TextureWrapType;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class TestMultiLightsScene_20fps extends SceneExt {

    private static final String TAG = "Test_MultiLightsScene";

    private static final String TEST_NAME = "Multiple Lights";

    private static final int SCENE_ID = -100;
    private static final String SCENE_NAME = "Multiple Lights";
    private TestCompleteListener mTestCompleteListener;
    private GVRContext mGVRContext;
    private GVRSceneObject mRootSceneObject;
    private final String FBX_PATH = "Common/Test1.FBX";
    private long mStartTime = -1;
    private boolean textWrappingDone = false;
    private GVRSceneObject mPtLightNode1, mPtLightNode2, mPtLightNode3, mPtLightNode4,
            mPtLightNode5, mPtLightNode6;

    private ArrayList<String> listRemoveObjNames = new ArrayList<String>();
    ArrayList<GVRSceneObject> listObjTexWrapping = new ArrayList<GVRSceneObject>();
    private FPSCounter mFpsCounter;
    private TestDescOnCameraRig mTestDescDisp;


    public TestMultiLightsScene_20fps(GVRContext gvrContext) {
        super(gvrContext);
        mGVRContext = gvrContext;
        listRemoveObjNames.add("BaseSculpt_Male");
        listRemoveObjNames.add("ui_2d3d (2)");
        listRemoveObjNames.add("ui_colorpicker");
        listRemoveObjNames.add("ui_edit (2)");
        listRemoveObjNames.add("ui_editmenu (2)");
        listRemoveObjNames.add("ui_play");
        mFpsCounter = new FPSCounter();

    }

    public void onInit() {
        // set background color
        GVRCameraRig mainCameraRig = this.getMainCameraRig();
        mainCameraRig.getLeftCamera()
                .setBackgroundColor(Color.BLACK);
        mainCameraRig.getRightCamera()
                .setBackgroundColor(Color.BLACK);
        mainCameraRig.getTransform().setPosition(0, 1.5f, 3.5f);
        mTestDescDisp = new TestDescOnCameraRig(mGVRContext,mainCameraRig);
        mTestDescDisp.setTestName("MultiLights Test");


        try {
            mRootSceneObject = mGVRContext.loadModel(FBX_PATH);
            mRootSceneObject.getTransform().setPosition(0.0f, 0.0f, 0.0f);
            mRootSceneObject.getTransform().setRotationByAxis(180.0f, 0, 1, 0);
            mRootSceneObject.getTransform().setScale(1.0f, 1.0f, 1.0f);

            GVRSceneObject wall = mRootSceneObject.getSceneObjectByName("wall");
            Log.d(TAG, "wall has mesh?" + wall.hasMesh());
            Log.d(TAG, "wall render data" + wall.getRenderData());
            listObjTexWrapping.add(wall);
            GVRSceneObject windowWall2 = mRootSceneObject.getSceneObjectByName("windowwall (2)");
            Log.d(TAG, "windowWall2 render data" + windowWall2.getRenderData());
            listObjTexWrapping.add(windowWall2);

            setMaterials();
            updateLights();
            addSceneObject(mRootSceneObject);
            mStartTime = System.currentTimeMillis();

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



    private void updateLights() {

        GVRSceneObject dirLightNode = mRootSceneObject.getSceneObjectByName("Directional Light");
        setLightParams(dirLightNode);
        mPtLightNode1 = mRootSceneObject.getSceneObjectByName("Point Light");
        setLightParams(mPtLightNode1);
        mPtLightNode2 = mRootSceneObject.getSceneObjectByName("Point Light (2)");
        setLightParams(mPtLightNode2);
        mPtLightNode3 = mRootSceneObject.getSceneObjectByName("Point Light (3)");
        setLightParams(mPtLightNode3);
        mPtLightNode4 = mRootSceneObject.getSceneObjectByName("Point Light (4)");
        setLightParams(mPtLightNode4);
        mPtLightNode5 = mRootSceneObject.getSceneObjectByName("Point Light (5)");
        setLightParams(mPtLightNode5);
        mPtLightNode6 = mRootSceneObject.getSceneObjectByName("Point Light (6)");
        setLightParams(mPtLightNode6);
    }

    private void setLightParams(GVRSceneObject lightNode) {
        GVRLightBase light = (GVRLightBase) lightNode.getLight();

        if (lightNode.getName().equals("Directional Light")) {
            GVRDirectLight dl = (GVRDirectLight) light;
            lightNode.getTransform().setRotationByAxis(-60, 1, 0, 0);
            lightNode.getTransform().setRotationByAxis(45, 0, 1, 0);
            lightNode.getTransform().setRotationByAxis(0, 0, 0, 1);
            light.setCastShadow(true);
        } else {
            GVRPointLight pl = (GVRPointLight) light;
            // range made 75 based on
            // http://www.ogre3d.org/tikiwiki/-Point+Light+Attenuation
            pl.setAttenuation(1.0f, 0.06f, 0.013f);
            // light.setCastShadow(true); //Point lights don't cast shadows
        }
    }

    public void onStep() {
        if (mStartTime != -1) {
            float fps = mFpsCounter.tick();
            if(fps >= 0) {
                mTestDescDisp.setFps(fps);
                mTestDescDisp.displayTestData();
            }
        }
        if (!textWrappingDone) {
            mGVRContext.runOnGlThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            setTextureWrapping();
                        }
                    });
        }

        animatePointLights();

        long currTime = System.currentTimeMillis();

        if ((currTime - mStartTime) >= BenchMarkController.TEST_DURATION_IN_MILLISECS) {
            float averageFps =
                    mFpsCounter.getAverageFps();
            LinkedHashMap<String,String> testResultData = new LinkedHashMap<String, String>();
            testResultData.put("Average fps: ", String.format("%4.2f", averageFps));
            ResultData result = new
                    ResultData(SCENE_ID, "MultiLights Test:", testResultData);
            removeSceneObject(mRootSceneObject);
            mTestDescDisp.removeTestDescDisplay();
            mTestCompleteListener.onResult(result);
        }

    }

    // temp code
    private void setMaterials() {

        for (String objName : listRemoveObjNames) {
            GVRSceneObject[] objs = mRootSceneObject.getSceneObjectsByName(objName);
            if (objs.length >= 1) {
                for (GVRSceneObject obj : objs) {
                    if (obj.getRenderData() != null) {
                        obj.getRenderData().setRenderMask(0);
                    }
                }
            }
        }
        GVRSceneObject glass = mRootSceneObject.getSceneObjectByName("glass");
        glass.getRenderData().getMaterial().setOpacity(0);
        glass.getRenderData().setRenderMask(0);

        GVRSceneObject glass2 = mRootSceneObject.getSceneObjectByName("glass (2)");
        glass2.getRenderData().getMaterial().setOpacity(0);
        glass2.getRenderData().setRenderMask(0);
        GVRSceneObject glass3 = mRootSceneObject.getSceneObjectByName("glass (3)");
        glass3.getRenderData().getMaterial().setOpacity(0);
        glass3.getRenderData().setRenderMask(0);
        GVRSceneObject glass4 = mRootSceneObject.getSceneObjectByName("glass (4)");
        glass4.getRenderData().getMaterial().setOpacity(0);
        glass4.getRenderData().setRenderMask(0);

        GVRSceneObject grass = mRootSceneObject.getSceneObjectByName("Grass Clumps 01");
        grass.getRenderData().getMaterial().setDiffuseColor(0.09804626f, 0.32594937f, 0, 1.0f);

        GVRSceneObject lowerPanel = mRootSceneObject.getSceneObjectByName("lowerpanel");
        lowerPanel.getRenderData().getMaterial()
                .setDiffuseColor(0.21926828f, 0.24264705f, 0.17484862f, 1.0f);

        GVRSceneObject lowerPanel2 = mRootSceneObject.getSceneObjectByName("lowerpanel (2)");
        lowerPanel2.getRenderData().getMaterial()
                .setDiffuseColor(0.21926828f, 0.24264705f, 0.17484862f, 1.0f);

    }

    private synchronized void setTextureWrapping() {

        int doneCount = 0;
        for (GVRSceneObject obj : listObjTexWrapping) {
            List<GVRSceneObject> children = obj.getChildren();
            for (GVRSceneObject child : children) {
                GVRRenderData renderData = child.getRenderData();
                if (renderData != null) {
                    GVRMaterial material = renderData.getMaterial();

                    if (material != null) {
                        Set<String> keys = material.getTextureNames();
                        for (String key : keys) {
                            GVRTexture bmT = (GVRTexture) material.getTexture(key);
                            if (bmT != null) {

                                if (bmT instanceof GVRBitmapTexture) {
                                    GVRTextureParameters texParams = new GVRTextureParameters(
                                            mGVRContext);
                                    texParams.setWrapSType(TextureWrapType.GL_REPEAT);
                                    texParams.setWrapTType(TextureWrapType.GL_REPEAT);
                                    bmT.updateTextureParameters(texParams);
                                    renderData.setMaterial(material);
                                    doneCount++;

                                } else {
                                    Log.d(TAG,
                                            "texture is not GVRBitmapTexture " + ":"
                                                    + child.getName());
                                }
                            } else {
                                Log.d(TAG, "texture is null " + ":" + child.getName());
                            }
                        }
                    } else {
                        Log.d(TAG, "material is null " + ":" + obj.getName());
                    }
                } else {
                    Log.d(TAG, "renderdata is null " + ":" + obj.getName());
                }
            }

        }

        if (doneCount == 2) {
            textWrappingDone = true;
        }

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
