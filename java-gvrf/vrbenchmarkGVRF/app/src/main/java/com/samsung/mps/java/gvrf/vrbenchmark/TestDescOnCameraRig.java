package com.samsung.mps.java.gvrf.vrbenchmark;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRRenderData;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;

/**
 * Created by pnath on 10/14/16.
 */
public class TestDescOnCameraRig {

    private GVRContext mGVRContext;
    private GVRCameraRig mGVRCameraRig;
    private GVRTextViewSceneObject mTextViewSceneObj;
    private String mTestName, mSubTestName, mDeviceName, mFpsNum, mTimeTakenToLoad, mOtherErrors;
    private boolean isEnabled = true;

    public TestDescOnCameraRig(GVRContext gvrContext, GVRCameraRig gvrCameraRig) {
        mGVRContext = gvrContext;
        mGVRCameraRig = gvrCameraRig;
        mTextViewSceneObj = createTextDisplaySceneObject();
        //get the model number and set it
        mDeviceName = Utils.findDeviceName();

    }


    private GVRTextViewSceneObject createTextDisplaySceneObject() {
        if (mGVRContext == null) {
            return null;
        }
        GVRTextViewSceneObject textViewSceneObject = new GVRTextViewSceneObject(mGVRContext, 2.0f, 2.0f, " ");
        textViewSceneObject.setGravity(Gravity.LEFT);
        textViewSceneObject
                .setTextSize(textViewSceneObject.getTextSize() * 0.1f);
        textViewSceneObject.setTextColor(Color.RED);
      //  textViewSceneObject.setBackgroundColor(Color.WHITE);
        textViewSceneObject.getTransform().translate(2.5f, 0.5f, -6.5f);


        mGVRCameraRig.addChildObject(textViewSceneObject);
        textViewSceneObject.getRenderData().setDepthTest(false);
        textViewSceneObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.OVERLAY);
        return textViewSceneObject;
    }

    public void setPosition(float x, float y, float z) {
        mTextViewSceneObj.getTransform().setPosition(x, y, z);

    }

    public void setSize(float size) {
        mTextViewSceneObj.setTextSize(size);

    }

    public void setRenderingOrder(int renderOrder) {
        mTextViewSceneObj.getRenderData().setRenderingOrder(renderOrder);

    }


    public void setTestName(String testName) {
        mTestName = testName;
    }

    public void setSubTestName(String subTestName) {
        mSubTestName = subTestName;
    }

    public void setFps(float fps) {
        String formattedStr = String.format("%4.2f", fps);
        mFpsNum = formattedStr + " fps";
    }

    public void setTimeTakenToLoad(long loadTimeInMs) {
        mTimeTakenToLoad = loadTimeInMs + " ms";
    }

    public void setErrorsToDisplay(String errors) {
        mOtherErrors = errors;
    }

    public void displayTestData() {
        if ((mTextViewSceneObj == null) || (isEnabled == false)) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (mDeviceName != null) {
            stringBuilder.append(mDeviceName);
        }
        if (mTestName != null) {
            stringBuilder.append("\n");
            stringBuilder.append(mTestName);
        }
        if (mSubTestName != null) {
            stringBuilder.append("\n");
            stringBuilder.append("(");
            stringBuilder.append(mSubTestName);
            stringBuilder.append(")");
        }
        if (mFpsNum != null) {
            stringBuilder.append("\n");
            stringBuilder.append(mFpsNum);
        }
        if (mTimeTakenToLoad != null) {
            stringBuilder.append("\n");
            stringBuilder.append(mTimeTakenToLoad);
        }
        if (mOtherErrors != null) {
            stringBuilder.append("\n");
            stringBuilder.append("** ");
            stringBuilder.append(mOtherErrors);
        }

        String displayString = stringBuilder.toString();
        mTextViewSceneObj.setText(displayString);
    }

    public void enableDisplay(boolean isEnable) {
        isEnabled = isEnable;
        if (isEnable == false) {
            mTextViewSceneObj.setText("");
        }
    }


    public void removeTestDescDisplay() {
        mGVRCameraRig.removeAllChildren();
    }
}
