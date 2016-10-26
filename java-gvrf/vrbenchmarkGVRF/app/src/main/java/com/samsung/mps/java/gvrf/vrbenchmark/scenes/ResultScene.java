
package com.samsung.mps.java.gvrf.vrbenchmark.scenes;

import com.samsung.mps.java.gvrf.vrbenchmark.ResultData;
import com.samsung.mps.java.gvrf.vrbenchmark.SceneExt;
import com.samsung.mps.java.gvrf.vrbenchmark.TestCompleteListener;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRRenderData.GVRRenderingOrder;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class ResultScene extends SceneExt {
    private static final String TAG = "vrbenchmark_ResultScene";

    private static final int SCENE_ID = 6;
    private static final int MAX_RESULT_SCREEN = 2;
    private static final int MAX_RESULTS_PER_SCREEN = 3;

    private static final String SCENE_NAME = "Result";

    private static final float START_Y_POS = 400.0f;
    private static final float WIDTH_TITLE_SCENE_OBJ = 1500;
    private static final float HEIGHT_TITLE_SCENE_OBJ = 100;
    private static final float WIDTH_RESULT_SCENE_OBJ = 1500;
    private static final float HEIGHT_ONE_LINE_SCENE_OBJ = 50;

    private GVRContext mGVRContext;
    private GVRSceneObject[] mRootSceneObject = new GVRSceneObject[MAX_RESULT_SCREEN];
    private final String FBX_PATH = "benchmark-fw/results.FBX";
    private ArrayList<ResultData> mListResults = new ArrayList<ResultData>();
    private float yTranslate = 0;
    private GVRSceneObject mLastChild = null;



    public ResultScene(GVRContext gvrContext) {
        super(gvrContext);
        mGVRContext = gvrContext;
    }

    public void onInit() {
        // set background color
        GVRCameraRig mainCameraRig = this.getMainCameraRig();
        mainCameraRig.getLeftCamera()
                .setBackgroundColor(Color.BLACK);
        mainCameraRig.getRightCamera()
                .setBackgroundColor(Color.BLACK);
        mainCameraRig.getTransform().setPosition(322.0f, 169.0f, -10.0f);

        try {
            // show the result here
            for (int i = 0; i < MAX_RESULT_SCREEN; i++) {
                mRootSceneObject[i] = new GVRSceneObject(mGVRContext);
                mRootSceneObject[i] = mGVRContext.getAssetLoader().loadModel(FBX_PATH, this);
                mRootSceneObject[i].getTransform().setScale(0.5546685f, 0.5546685f, 0.5546685f);
                GVRSceneObject bgNode = mRootSceneObject[i].getSceneObjectByName("Background");
                bgNode.getRenderData().setRenderingOrder(GVRRenderingOrder.BACKGROUND);
            }

            mRootSceneObject[0].getTransform().setPosition(271f, 152.0f, -459.0f);
            mRootSceneObject[1].getTransform().setPosition(805f, 152f, 29f);
            mRootSceneObject[1].getTransform().rotateByAxis(-95, 0f, 1f, 0f);
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


    public void updateResult(ArrayList<ResultData> listResults) {
        mListResults = listResults;
        String titleStr = new String();
        String resultStr = new String();
        int displayIndex = 0;
        int resultIndex = 0;
        for (ResultData result : mListResults) {
            titleStr = "";
            GVRTextViewSceneObject textViewTitle = createSceneObjForTitle();
            String testName = result.getTestName();
            titleStr += testName + " :\n";
            textViewTitle.setText(titleStr);

            LinkedHashMap<String, String> testResultData = result.getTestData();
            GVRTextViewSceneObject textViewResult = createSceneObjForResult(testResultData.size());

            resultStr = "";
            Set<String> keys = testResultData.keySet();

            for (String key : keys) {
                resultStr += " * " + key + " : ";
                resultStr += testResultData.get(key) + "\n";
            }
            textViewResult.setText(resultStr);
            mRootSceneObject[displayIndex].addChildObject(textViewTitle);
            mRootSceneObject[displayIndex].addChildObject(textViewResult);
            resultIndex++;
            if (resultIndex % MAX_RESULTS_PER_SCREEN == 0) {
                if (displayIndex < MAX_RESULT_SCREEN) {
                    displayIndex++;
                    mLastChild = null;
                    yTranslate = 0;
                }
            }
        }

    }


    /*
    * create scene object for title
    */
    private GVRTextViewSceneObject createSceneObjForTitle() {
        if (mGVRContext == null) {
            return null;
        }

        GVRTextViewSceneObject textViewSceneObject = new GVRTextViewSceneObject(
                mGVRContext, WIDTH_TITLE_SCENE_OBJ, HEIGHT_TITLE_SCENE_OBJ, "");
        if (mLastChild != null) {
            float yPos = mLastChild.getTransform().getPositionY();
            yTranslate += HEIGHT_TITLE_SCENE_OBJ / 2;
            float yPosOfThisSceneObj = yPos - yTranslate;
            textViewSceneObject.getTransform().setPosition(0, yPosOfThisSceneObj, 1.0f);

        } else {
            textViewSceneObject.getTransform().setPosition(0, START_Y_POS, 1.0f);
        }


        yTranslate = HEIGHT_TITLE_SCENE_OBJ / 2;
        mLastChild = textViewSceneObject;
        textViewSceneObject.setTextSize(6.0f);
        textViewSceneObject.setTextColor(Color.BLACK);
        // textViewSceneObject.setBackgroundColor(Color.YELLOW);


        textViewSceneObject.getRenderData().setRenderingOrder(GVRRenderingOrder.TRANSPARENT);
        return textViewSceneObject;
    }

    /*
   * create scene object for result
   */

    private static final float OFFSET_BETWEEN_TEST_RESULTS = 20;

    private GVRTextViewSceneObject createSceneObjForResult(int numOfLines) {
        if (mGVRContext == null) {
            return null;
        }
        float heightTitleSceneObj = (HEIGHT_ONE_LINE_SCENE_OBJ * numOfLines);
        GVRTextViewSceneObject textViewSceneObject = new GVRTextViewSceneObject(
                mGVRContext, WIDTH_RESULT_SCENE_OBJ, heightTitleSceneObj, "");

        if (mLastChild != null) {
            float yPos = mLastChild.getTransform().getPositionY();
            yTranslate += (heightTitleSceneObj / 2);
            float yPosOfThisSceneObj = yPos - yTranslate;
            textViewSceneObject.getTransform().setPosition(0, yPosOfThisSceneObj, 1.0f);

        } else {
            textViewSceneObject.getTransform().setPosition(0, START_Y_POS, 1.0f);
        }


        yTranslate = (heightTitleSceneObj / 2) + OFFSET_BETWEEN_TEST_RESULTS;
        mLastChild = textViewSceneObject;
        textViewSceneObject.setTextSize(3.0f);
        textViewSceneObject.setTextColor(Color.BLUE);
        // textViewSceneObject.setBackgroundColor(Color.GRAY);
        textViewSceneObject.getRenderData().setRenderingOrder(GVRRenderingOrder.TRANSPARENT);
        return textViewSceneObject;
    }

    public void onStep() {
    }

    @Override
    public void registerTestCompleteListener(TestCompleteListener testCompleteListener) {

    }

    @Override
    public int getSceneID() {
        return SCENE_ID;

    }

}
