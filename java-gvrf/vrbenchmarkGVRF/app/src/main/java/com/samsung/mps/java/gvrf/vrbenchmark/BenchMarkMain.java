
package com.samsung.mps.java.gvrf.vrbenchmark;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;

public class BenchMarkMain extends GVRMain {
    private BenchMarkController mBenchMarkController;

    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        mBenchMarkController = new BenchMarkController(gvrContext);

    }

    @Override
    public void onStep() {
        mBenchMarkController.getCurrentScene().onStep();
    }

}
