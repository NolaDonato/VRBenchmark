
package com.samsung.mps.java.gvrf.vrbenchmark.scenes;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;

import org.gearvrf.GVRTexture;
import org.gearvrf.IAssetEvents;

import com.samsung.mps.java.gvrf.vrbenchmark.ResultData;
import com.samsung.mps.java.gvrf.vrbenchmark.SceneExt;
import com.samsung.mps.java.gvrf.vrbenchmark.TestCompleteListener;
import com.samsung.mps.java.gvrf.vrbenchmark.TestDescOnCameraRig;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;


public class TestMeshLoadScene extends SceneExt {

    private static final String TAG = "Test_MeshLoadScene";

    private static final String TEST_NAME = "Mesh";
    private static final int SCENE_ID = 2;
    private static final String SCENE_NAME = "MeshLoad";
    private static int TIME_AFTER_LOAD = 1000; //ie 1 second after load
    private static final int MAX_NUM_LOADS = 6; //ie 6 since there are two models

    private TestCompleteListener mTestCompleteListener;
    private GVRContext mGVRContext;
    private GVRSceneObject mModelSceneObject, mLoadingInfoSceneObject;
    private static final String FBX_BMW = "TestMesh/bmw/bmw.FBX";
    private static final String FBX_S8 = "TestMesh/s8/s8_car_model.FBX";
    private final String[] arrFbxFiles = {FBX_BMW, FBX_S8};
    private final String loadingScreenFbx = "TestMesh/Loading/LoadingModel.FBX";
    private long mTimeAfterModelLoad = -1;
    private TestDescOnCameraRig mTestDescDisp;
    private HashMap<String, ArrayList<Long>> mMapResult = new HashMap<String, ArrayList<Long>>();
    private int mCurrLoadIndex = 0;
    private boolean isLoaded = false, showLoadingScreen = true;
    private GVRCameraRig mMainCameraRig;
    private long mStartTimeModelLoad = 0;


    public TestMeshLoadScene(GVRContext gvrContext) {
        super(gvrContext);
        mGVRContext = gvrContext;

    }

    public void onInit() {
        // set background color
        mMainCameraRig = this.getMainCameraRig();
        mMainCameraRig.getLeftCamera()
                .setBackgroundColor(Color.GRAY);
        mMainCameraRig.getRightCamera()
                .setBackgroundColor(Color.GRAY);

        mTestDescDisp = new TestDescOnCameraRig(mGVRContext,mMainCameraRig);
        mTestDescDisp.setTestName("MeshLoad Test");

        ArrayList<Long> listTimeTakenBy1stModel = new ArrayList<Long>();
        ArrayList<Long> listTimeTakenBy2ndModel = new ArrayList<Long>();
        mMapResult.put(arrFbxFiles[0], listTimeTakenBy1stModel);
        mMapResult.put(arrFbxFiles[1], listTimeTakenBy2ndModel);
        mGVRContext.getEventReceiver().addListener(assetEventListener);

        try {
            mLoadingInfoSceneObject = mGVRContext.getAssetLoader().loadModel(loadingScreenFbx, this);
            mLoadingInfoSceneObject.getTransform().setPosition(0.0f, 0.0f, -50.0f);
            mLoadingInfoSceneObject.getTransform().setScale(0.5f, 0.5f, 0.5f);
        } catch (Exception e) {
            Log.e(TAG, "exception: ", e);
        }

        loadModelInAnotherThread(mCurrLoadIndex);
    }

    @Override
    public void setExtraInfo(String info) {

    }

    @Override
    public String getSceneName() {
        return SCENE_NAME;
    }

    private void loadIntermediateScreen() {
        mTestDescDisp.enableDisplay(false);
        mMainCameraRig.getTransform().setPosition(0, 1.0f, 7.0f);
        showLoadingScreen = true;
    }

    private void unloadIntermediateScreen() {
        mTestDescDisp.enableDisplay(true);
        removeSceneObject(mLoadingInfoSceneObject);
        showLoadingScreen = false;

    }

    private void loadModelInAnotherThread(final int index) {
        if (mModelSceneObject != null) {
            removeSceneObject(mModelSceneObject);
            mModelSceneObject = null;
        }
        loadIntermediateScreen();

        new Thread() {
            public void run() {
                try {
                    String fbxFile = arrFbxFiles[index];
                    mStartTimeModelLoad = System.currentTimeMillis();
                    mGVRContext.getAssetLoader().loadModel(fbxFile);

                } catch (Exception e) {
                    Log.e(TAG, "exception: ", e);
                }
            }
        }.start();
    }


    private void onModelLoadedWithTexture(GVRSceneObject model, String filePath) {
        //get the time taken to fully load ...wih textures
        long currTime = System.currentTimeMillis();
        long timeTaken = currTime - mStartTimeModelLoad;
        if (filePath.contains("bmw")) {
            filePath = FBX_BMW;
        } else if (filePath.contains("s8")) {
            filePath = FBX_S8;
        }
        ArrayList<Long> listTimeTaken = mMapResult.get(filePath);
        listTimeTaken.add(timeTaken);
        mMapResult.put(filePath, listTimeTaken);

        //prepare for display on screen
        unloadIntermediateScreen();
        mModelSceneObject = model;

        //position camera
        if (filePath.contains("bmw")) {
            //bmw
            mMainCameraRig.getTransform().setPosition(1.0f, 1.0f, 4.0f);
        } else if (filePath.contains("s8")) {
            mMainCameraRig.getTransform().setPosition(0.0f, 3.0f, 4.0f);
        }

        //Position model
        mModelSceneObject.getTransform().rotateByAxis(90, 0, 1, 0);
        if (filePath.contains("bmw")) {
            mModelSceneObject.getTransform().setPosition(0.0f, 1.0f, 0.0f);
            mTestDescDisp.setSubTestName("BMW");
        }
        if (filePath.contains("s8")) {
            mModelSceneObject.getTransform().setPosition(1.0f, 2.0f, 0.0f);
            mTestDescDisp.setSubTestName("S8");
        }
        mModelSceneObject.getTransform().setScale(1.0f, 1.0f, 1.0f);

        //add to main scene
        addSceneObject(mModelSceneObject);

        mTestDescDisp.setTimeTakenToLoad(timeTaken);
        mTestDescDisp.displayTestData();

        //prepare for one sec on screen
        mTimeAfterModelLoad = System.currentTimeMillis();
        isLoaded = true;
    }

    IAssetEvents assetEventListener = new IAssetEvents() {
        @Override
        public void onAssetLoaded(GVRContext context, GVRSceneObject model, String filePath,
                                  String errors) {

            Log.d(TAG, "asset loaded:" + filePath);
            //not seeing it getting called all the time...
            if ((filePath.equals(FBX_BMW) == false) && (filePath.equals(FBX_S8) == false)) {
                return;
            }
            onModelLoadedWithTexture(model,filePath);

        }

        @Override
        public void onModelLoaded(GVRContext context, GVRSceneObject model, String filePath) {
            Log.d(TAG, "model loaded:" + filePath);
/*            if ((filePath.contains("bmw") == false) && (filePath.contains("s8") == false)) {
                return;
            }
            onModelLoadedWithTexture(model, filePath);*/
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


    @Override
    public void registerTestCompleteListener(TestCompleteListener testCompleteListener) {
        mTestCompleteListener = testCompleteListener;

    }

    public void onStep() {
        int totalLoads = 0;
        Set<String> keyModelNames = mMapResult.keySet();
        for (String key : keyModelNames) {
            ArrayList<Long> listValues = mMapResult.get(key);
            totalLoads += listValues.size();
        }

        long currTimeInMs = System.currentTimeMillis();


        if (isLoaded && ((currTimeInMs - mTimeAfterModelLoad) >= TIME_AFTER_LOAD)) {
            isLoaded = false;

            if (totalLoads == MAX_NUM_LOADS) {
                sendResult();
                return;
            }
            if (mCurrLoadIndex == 0) {
                mCurrLoadIndex = 1;
            } else {
                mCurrLoadIndex = 0;
            }

            loadModelInAnotherThread(mCurrLoadIndex);
        }

    }

    private void sendResult() {
        mGVRContext.getEventReceiver().removeListener(assetEventListener);
        assetEventListener = null;
        mTestDescDisp.removeTestDescDisplay();
        removeAllSceneObjects();
        //test is over
        float avgLoadTime1 = getAverageTimeTaken(0);
        float avgLoadTime2 = getAverageTimeTaken(1);

        LinkedHashMap<String, String> testResultData = new LinkedHashMap<String, String>();
        testResultData.put("Average Load Time of 1st Model", String.format("%4.0f", avgLoadTime1) + "ms");
        testResultData.put("Average Load Time of 2nd Model", String.format("%4.0f", avgLoadTime2) + "ms");

        ResultData result = new
                ResultData(SCENE_ID, "MeshLoad Test", testResultData);
        mTestCompleteListener.onResult(result);
    }


    private long getAverageTimeTaken(int index) {
        String fbxFile = arrFbxFiles[index];
        ArrayList<Long> listTimeTaken = mMapResult.get(fbxFile);

        long sumTimeTaken = 0;
        for (long timeTaken : listTimeTaken) {
            sumTimeTaken += timeTaken;
        }
        long averageTimeTaken = sumTimeTaken / listTimeTaken.size();
        return averageTimeTaken;
    }


    @Override
    public int getSceneID() {
        return SCENE_ID;

    }


}
