
package com.samsung.mps.java.gvrf.vrbenchmark;

import com.samsung.mps.java.gvrf.vrbenchmark.scenes.*;

import org.gearvrf.GVRContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BenchMarkController implements TestCompleteListener {
    public static final float TEST_DURATION_IN_MILLISECS = 15.0f * 1000.0f;
    private HashMap<Integer, SceneExt> mapSceneID = new HashMap<Integer, SceneExt>();
    private ArrayList<ResultData> listResults = new ArrayList<ResultData>();
    private GVRContext mGVRContext;
    private SceneExt mCurrScene, mStartScene, mTestInfoScene;
    private int mLastTestSceneId = 0, mTotalNumOfTests = 0;
    private static final boolean IS_DEBUG_ONE_SCENE = false;
    private static final boolean IS_DEBUG_ONLY_RESULT_SCENE = false;

    public BenchMarkController(GVRContext gvrContext) {
        mGVRContext = gvrContext;
        if (!IS_DEBUG_ONLY_RESULT_SCENE) {
            // start scene
            if (!IS_DEBUG_ONE_SCENE) {
                mStartScene = new StartScene(mGVRContext);
                registerTest(mStartScene);

                mTestInfoScene = new TestInfoScene(mGVRContext);
                registerTest(mTestInfoScene);
            }

            registerAllTests();
            runAllTests();
        } else {
            LinkedHashMap<String, String> testResultData = new LinkedHashMap<String, String>();
            testResultData.put("Average fps", String.format("%4.2f", 150.75f));
            ResultData result = new
                    ResultData(0, "MultiLights Test", testResultData);
            saveResult(result);

            testResultData = new LinkedHashMap<String, String>();
            testResultData.put("Average fps", String.format("%4.2f", 150.75f));
            result = new ResultData(1, "Shadows Test", testResultData);
            saveResult(result);

            testResultData = new LinkedHashMap<String, String>();
            testResultData.put("Num. of times loaded", String.valueOf(10));
            testResultData.put("Average Load Time of 1st Model", String.format("%4.0f", 100000f) + "ms");
            testResultData.put("Average Load Time of 2nd Model", String.format("%4.0f", 100000f) + "ms");
            result = new ResultData(2, "MeshLoad Test", testResultData);
            saveResult(result);

            testResultData = new LinkedHashMap<String, String>();
            testResultData.put("Num. of times loaded", String.valueOf(10));
            testResultData.put("Average Load Time of 1st Model", String.format("%4.0f", 100000f) + "ms");
            testResultData.put("Average Load Time of 2nd Model", String.format("%4.0f", 100000f) + "ms");
            result = new ResultData(2, "HeadPointee Test", testResultData);
            saveResult(result);
            showResult();
        }
    }

    private void registerAllTests() {
        if (!IS_DEBUG_ONE_SCENE) {
            SceneExt testScene = new TestMultiLightsSceneNew(mGVRContext);
            registerTest(testScene);
            mTotalNumOfTests++;
            testScene = new TestShadowsSceneNew(mGVRContext);
            registerTest(testScene);
            mTotalNumOfTests++;
            testScene = new TestMeshLoadScene(mGVRContext);
            registerTest(testScene);

             mTotalNumOfTests++;
            testScene = new TestHeadPointee1(mGVRContext);
            registerTest(testScene);

            mTotalNumOfTests++;
            testScene = new TestCpuGpuLoad(mGVRContext);
            registerTest(testScene);
        } else {
            SceneExt testScene = new TestCpuGpuLoad(mGVRContext);
            registerTest(testScene);
        }
        mTotalNumOfTests++;
    }

    private void registerTest(SceneExt testScene) {
        mapSceneID.put(testScene.getSceneID(), testScene);
        testScene.registerTestCompleteListener(this);
    }

    private void runAllTests() {
        int testID = 0;
        SceneExt testScene = mapSceneID.get(testID);
        runTest(testScene);
    }

    private void runTest(SceneExt testScene) {
        mCurrScene = testScene;
        mCurrScene.onInit();
        if (!IS_DEBUG_ONE_SCENE) {
            if (mapSceneID.get(mTestInfoScene.getSceneID()) == testScene) {
                SceneExt nextTestScene = mapSceneID.get(mLastTestSceneId + 1);
                String nextTextText = nextTestScene.getSceneName();
                mCurrScene.setExtraInfo(nextTextText);
            }
        }
        mGVRContext.setMainScene(testScene);


    }

    private void showResult() {
        ResultScene resultScene = new ResultScene(mGVRContext);
        resultScene.onInit();
        resultScene.updateResult(listResults);
        mCurrScene = resultScene;
        mGVRContext.setMainScene(resultScene);
    }

    private void saveResult(ResultData resultData) {
        listResults.add(resultData);
    }

    @Override
    public void onResult(ResultData resultData) {
        if (resultData == null) {
            return;
        }
        int currSceneID = resultData.mSceneID;
        if ((currSceneID != mStartScene.getSceneID()) && (currSceneID != mTestInfoScene.getSceneID())) {
            saveResult(resultData);
        }


        if ((currSceneID >= 0) && ((mLastTestSceneId + 1) <= mTotalNumOfTests)) {
            //we can go for another test
            runTest(mapSceneID.get(mTestInfoScene.getSceneID()));
        } else {
            startNewScene(++mLastTestSceneId);
        }
    }

    private void startNewScene(int newSceneId) {
        if (newSceneId <= mTotalNumOfTests) {
            SceneExt testScene = mapSceneID.get(newSceneId);
            runTest(testScene);
        } else {
            showResult();
        }
    }


    public SceneExt getCurrentScene() {
        return mCurrScene;
    }

}
