
package com.samsung.mps.java.gvrf.vrbenchmark;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ResultData {
    int mSceneID;
    String mTestName;
    LinkedHashMap<String,String> mTestData;


    public ResultData(int sceneID, String testName, LinkedHashMap<String,String> testData) {
        mSceneID = sceneID;
        mTestName = testName;
        mTestData = testData;
    }

    public int getSceneID() {
        return mSceneID;
    }

    public String getTestName() {
        return mTestName;
    }

    public LinkedHashMap<String,String> getTestData() {
        return mTestData;
    }
}
