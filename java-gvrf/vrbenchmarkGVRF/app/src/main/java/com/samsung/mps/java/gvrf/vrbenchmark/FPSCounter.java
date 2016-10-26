/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samsung.mps.java.gvrf.vrbenchmark;

import android.util.Log;
import java.util.Collection;
import java.util.HashMap;

public class FPSCounter {
    private static final String TAG = "vrbenchmark_FPSCounter";
    private static int frames = 0, key = 0;
    private HashMap<Integer, Float> mMapFrameNumFps = new HashMap<Integer, Float>();
    private long mLastIntervalFrameTimeMillis = 0, mLastFrameTimeMillis = 0, mLastFpsDisplayTime = 0;
    long worstTimeTakenInMillis = 0;
    private static final long interval = 2000;
    private static long TIME_INTERVAL_FOR_DISPLAY = 1000;


    public void setIntervalForFpsDisplay(long timeInMs) {
        TIME_INTERVAL_FOR_DISPLAY = timeInMs;
    }

    public float tick() {
        frames++;
        long currTimeInMillis = System.currentTimeMillis();
        long timeTakenInMillis = currTimeInMillis - mLastIntervalFrameTimeMillis;
        long timeTakenLastFrameInMillis = currTimeInMillis - mLastFrameTimeMillis;
        mLastFrameTimeMillis = currTimeInMillis;


        if (timeTakenInMillis >= interval) {
            float timeTakenInSecs = ((float) interval / 1000.0f);
            float fps = (frames / timeTakenInSecs);
            Log.d(TAG, "fps is:" + fps);
            mMapFrameNumFps.put(key, fps);
            key++;
            frames = 0;
            mLastIntervalFrameTimeMillis = currTimeInMillis;
        }
        if(checkForCurrFpsDisplay()) {
            float timeTakenInSecsCurrFrame = ((float) timeTakenLastFrameInMillis / 1000.0f);
            float fpsCurrFrame = (1.0f / timeTakenInSecsCurrFrame);
            return fpsCurrFrame;
        }
        return -1;
    }

    private boolean checkForCurrFpsDisplay() {
        boolean isTimeForDisplay = false;
        long currTimeInMillis = System.currentTimeMillis();
        long timePassedSinceLastDisplay = currTimeInMillis - mLastFpsDisplayTime;
        if (timePassedSinceLastDisplay > TIME_INTERVAL_FOR_DISPLAY) {
            isTimeForDisplay = true;
            mLastFpsDisplayTime = currTimeInMillis;
        }
    return isTimeForDisplay;
}

    public void reset() {
        mLastIntervalFrameTimeMillis = 0;
        mLastFrameTimeMillis = 0;
        frames = 0;
        key = 0;
        mLastIntervalFrameTimeMillis = 0;
        mLastFpsDisplayTime = 0;
        mMapFrameNumFps.clear();
    }

    public float getAverageFps() {
        float averageFps = 0.0f;
        if (mMapFrameNumFps.size() > 0) {

            Collection<Float> fpsCollection = mMapFrameNumFps.values();
            int numTimes = mMapFrameNumFps.size();
            float sumFps = 0;

            for (float value : fpsCollection) {
                sumFps += value;
            }
            Log.d(TAG, "num of times is:" + numTimes);
            averageFps = sumFps / numTimes;
        }
        return averageFps;
    }


}
