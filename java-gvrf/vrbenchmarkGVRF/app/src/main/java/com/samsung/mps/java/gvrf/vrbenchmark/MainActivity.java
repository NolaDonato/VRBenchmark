
package com.samsung.mps.java.gvrf.vrbenchmark;

import org.gearvrf.GVRActivity;
import android.os.Bundle;

public class MainActivity extends GVRActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        BenchMarkMain benchMarkMain = new BenchMarkMain();
        this.setMain(benchMarkMain, "gvr.xml");
    }
}
