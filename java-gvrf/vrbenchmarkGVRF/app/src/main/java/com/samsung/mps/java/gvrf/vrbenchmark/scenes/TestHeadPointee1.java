
package com.samsung.mps.java.gvrf.vrbenchmark.scenes;

import android.util.Log;

import com.samsung.mps.java.gvrf.vrbenchmark.BenchMarkController;
import com.samsung.mps.java.gvrf.vrbenchmark.FPSCounter;
import com.samsung.mps.java.gvrf.vrbenchmark.ResultData;
import com.samsung.mps.java.gvrf.vrbenchmark.SceneExt;
import com.samsung.mps.java.gvrf.vrbenchmark.TestCompleteListener;
import com.samsung.mps.java.gvrf.vrbenchmark.TestDescOnCameraRig;


import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRLightBase;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.IAssetEvents;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class TestHeadPointee1 extends SceneExt {

    private static final String TAG = "Test_HeadPointeeScene1";

    private static final String SCENE_NAME = "HeadPointee";
    private static final float NORMAL_CURSOR_SIZE = 0.5f;
    private static final int CURSOR_RENDER_ORDER = 100000;


    private static final int SCENE_ID = 4;
    private TestCompleteListener mTestCompleteListener;
    private GVRContext mGVRContext;
    private GVRSceneObject mSceneObjectHeavyModel, mSceneObjectLightModel;
    private final String HEAVY_FBX_NAME = "room.FBX";
    private final String LIGHT_FBX_NAME = "room_no_lights.FBX";
    private final String HEAVY_FBX_PATH = "Common/" + HEAVY_FBX_NAME;
    private final String LIGHT_FBX_PATH = "Common/" + LIGHT_FBX_NAME;

    private final String HEAD_POINTEE_FILE_PATH = "Common/headtrackingpointer.png";
    private long mStartTime = -1;
    private ArrayList<String> listRemoveObjNames = new ArrayList<String>();
    private ArrayList<GVRSceneObject> listObjTexWrapping = new ArrayList<GVRSceneObject>();
    boolean mIsHeavyLoadTest = false;
    private boolean isResultSent = false;


    private FPSCounter mFpsCounter;
    private TestDescOnCameraRig mTestDescDisp;
    private GVRSceneObject mHeadPointeeSceneObject;

    public TestHeadPointee1(GVRContext gvrContext) {
        super(gvrContext);
        mGVRContext = gvrContext;
        listRemoveObjNames.add("ui_2d3d (2)");
        listRemoveObjNames.add("ui_colorpicker");
        listRemoveObjNames.add("ui_edit (2)");
        listRemoveObjNames.add("ui_editmenu (2)");
        listRemoveObjNames.add("ui_play");
        mFpsCounter = new FPSCounter();



    }

    GVRMaterial mat;

    public void onInit() {

        try {
            mGVRContext.getEventReceiver().addListener(assetEventListener);
            mHeadPointeeSceneObject = new GVRSceneObject(mGVRContext);
            mHeadPointeeSceneObject.attachRenderData(createRenderData(mGVRContext));

            // set background color and position head pointee
            GVRCameraRig mainCameraRig = this.getMainCameraRig();
            mainCameraRig.getTransform().setPosition(0, 1.5f, 3.5f);
            mHeadPointeeSceneObject.getTransform().translate(0f, 0f, -5.5f);
            mHeadPointeeSceneObject.getTransform().setScale(0.3f, 0.3f, 0.3f);
            mainCameraRig.addChildObject(mHeadPointeeSceneObject);
            mTestDescDisp = new TestDescOnCameraRig(mGVRContext,mainCameraRig);
            mTestDescDisp.setTestName("HeadPointee Test");


            //load the light model
            mGVRContext.getAssetLoader().loadModel(LIGHT_FBX_PATH);
            // mFpsCounter.createFPSDisplaySceneObject();
            mStartTime = System.currentTimeMillis();

            //Load the light fbx in another thread
            loadHeavyFbxInAnotherThread();

        } catch (Exception e) {
            Log.e(TAG, "exception: ", e);
        }

    }

    private void loadHeavyFbxInAnotherThread() {
        new Thread() {
            public void run() {
                try {

                    mGVRContext.getAssetLoader().loadModel(HEAVY_FBX_PATH);

                } catch (Exception e) {
                    Log.e(TAG, "exception: ", e);
                }
            }
        }.start();
    }


    private void afterModelLoad(GVRSceneObject model, String filePath) {
        if (filePath.contains(HEAVY_FBX_NAME)) {
            mSceneObjectHeavyModel = model;
        } else if (filePath.equals(LIGHT_FBX_NAME)) {
            mSceneObjectLightModel = model;
        }

        model.getTransform().setPosition(0.0f, 0.0f, 0.0f);
        model.getTransform().setRotationByAxis(180.0f, 0, 1, 0);
        model.getTransform().setScale(1.0f, 1.0f, 1.0f);

        listObjTexWrapping.clear();
        GVRSceneObject wall = model.getSceneObjectByName("wall");
        listObjTexWrapping.add(wall);
        GVRSceneObject windowWall2 = model.getSceneObjectByName("windowwall (2)");
        listObjTexWrapping.add(windowWall2);

        setMaterials(model);
        GVRSceneObject dirLightNode = model.getSceneObjectByName("Directional Light");
        setDirLightParams(dirLightNode);
        if (filePath.contains(LIGHT_FBX_NAME)) {
            addSceneObject(model);
        }

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


    private GVRRenderData createRenderData(GVRContext gvrContext) throws Exception {
        GVRMaterial material = new GVRMaterial(gvrContext);
        GVRMesh mesh = gvrContext.createQuad(NORMAL_CURSOR_SIZE, NORMAL_CURSOR_SIZE);
        material.setMainTexture(gvrContext.loadTexture(new GVRAndroidResource(gvrContext, HEAD_POINTEE_FILE_PATH)));
        GVRRenderData renderData = new GVRRenderData(gvrContext);
        renderData.setMaterial(material);
        renderData.setMesh(mesh);
        renderData.setDepthTest(false);
        renderData.setRenderingOrder(CURSOR_RENDER_ORDER);
        return renderData;
    }

    @Override
    public void setExtraInfo(String info) {

    }

    @Override
    public String getSceneName() {
        return SCENE_NAME;
    }

    private void updateTestDesc(float currFps) {
        if(currFps >= 0) {
            if (mIsHeavyLoadTest) {
                mTestDescDisp.setSubTestName("Heavy Scene");
            } else {
                mTestDescDisp.setSubTestName("Light Scene");
            }

            mTestDescDisp.setFps(currFps);
            mTestDescDisp.displayTestData();
        }
    }


    public void onStep() {
        if (isResultSent) {
            return;
        }

        if (mStartTime != -1) {
            float currFps = mFpsCounter.tick();
            updateTestDesc(currFps);


            long currTime = System.currentTimeMillis();
            if ((currTime - mStartTime) >= (BenchMarkController.TEST_DURATION_IN_MILLISECS * 2)) {
                storeResult(mIsHeavyLoadTest);
                sendResult();
                isResultSent = true;
                return;
            } else if (((currTime - mStartTime) >= BenchMarkController.TEST_DURATION_IN_MILLISECS) && (mIsHeavyLoadTest == false) && (mSceneObjectHeavyModel != null)) {
                storeResult(mIsHeavyLoadTest);
                //start heavy load test
                removeSceneObject(mSceneObjectLightModel);
                addSceneObject(mSceneObjectHeavyModel);
                mIsHeavyLoadTest = true;

            }
        }
    }

    LinkedHashMap<String, String> mTestResultData = new LinkedHashMap<String, String>();

    private void sendResult() {

        ResultData result = new
                ResultData(SCENE_ID, "HeadPointee Test", mTestResultData);

        mGVRContext.getEventReceiver().removeListener(assetEventListener);
        assetEventListener = null;

        mTestDescDisp.removeTestDescDisplay();
        removeAllSceneObjects();
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

    @Override
    public void registerTestCompleteListener(TestCompleteListener testCompleteListener) {
        mTestCompleteListener = testCompleteListener;

    }

    @Override
    public int getSceneID() {
        return SCENE_ID;

    }

    private void setMaterials(GVRSceneObject model) {

        for (String objName : listRemoveObjNames) {
            GVRSceneObject[] objs = model.getSceneObjectsByName(objName);
            if (objs.length >= 1) {
                for (GVRSceneObject obj : objs) {
                    if (obj.getRenderData() != null) {
                        obj.getRenderData().setRenderMask(0);
                    }
                }
            }
        }
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

        GVRSceneObject grass = model.getSceneObjectByName("Grass Clumps 01");
        grass.getRenderData().getMaterial().setDiffuseColor(0.09804626f, 0.32594937f, 0, 1.0f);

        GVRSceneObject lowerPanel = model.getSceneObjectByName("lowerpanel");
        lowerPanel.getRenderData().getMaterial()
                .setDiffuseColor(0.21926828f, 0.24264705f, 0.17484862f, 1.0f);

        GVRSceneObject lowerPanel2 = model.getSceneObjectByName("lowerpanel (2)");
        lowerPanel2.getRenderData().getMaterial()
                .setDiffuseColor(0.21926828f, 0.24264705f, 0.17484862f, 1.0f);

    }




    private void setDirLightParams(GVRSceneObject lightNode) {
        GVRLightBase light = (GVRLightBase) lightNode.getLight();

        if (lightNode.getName().equals("Directional Light")) {
            GVRDirectLight dl = (GVRDirectLight) light;
            //    dl.setCastShadow(true);
            dl.getTransform().setPosition(0, 3, 0);
            dl.getTransform().rotateByAxis(110, -1, 0, 0); //60 + 50
            dl.getTransform().rotateByAxis(75, 0, -1, 0); //45 + 30
        }
    }


}
