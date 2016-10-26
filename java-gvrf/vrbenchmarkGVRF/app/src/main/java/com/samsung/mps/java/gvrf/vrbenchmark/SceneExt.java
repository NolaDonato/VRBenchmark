
package com.samsung.mps.java.gvrf.vrbenchmark;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;

public abstract class SceneExt extends GVRScene {
    public SceneExt(GVRContext gvrContext) {
        super(gvrContext);
    }

    public abstract void registerTestCompleteListener(
            TestCompleteListener testCompleteListener);

    public abstract int getSceneID();

    public abstract String getSceneName();

    public abstract void onStep();

    public abstract void onInit();

    public abstract void setExtraInfo(String info);

}
