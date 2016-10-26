
package com.samsung.mps.java.gvrf.vrbenchmark.scenes;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import com.samsung.mps.java.gvrf.vrbenchmark.BenchMarkController;
import com.samsung.mps.java.gvrf.vrbenchmark.FPSCounter;
import com.samsung.mps.java.gvrf.vrbenchmark.ResultData;
import com.samsung.mps.java.gvrf.vrbenchmark.SceneExt;
import com.samsung.mps.java.gvrf.vrbenchmark.TestCompleteListener;
import com.samsung.mps.java.gvrf.vrbenchmark.TestDescOnCameraRig;
import com.samsung.mps.java.gvrf.vrbenchmark.Utils;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;

import org.gearvrf.GVRLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.scene_objects.GVRVideoSceneObject;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;


public class TestCpuGpuLoad extends SceneExt {

    private static final String TAG = "Test_TestCpuGpuLoad";
    public static final float TEST_DURATION_IN_MILLISECS = 30.0f * 1000.0f;

    private static final String SCENE_NAME = "Cpu&Gpu Load";
    private static final String TEST_NAME = SCENE_NAME + " Test";
    private static final int SCENE_ID = 5;

    private TestCompleteListener mTestCompleteListener;
    private GVRContext mGVRContext;

    //media used
    private final String CAR_FBX_PATH = "TestCpuGpu/bmw_interior_ascii.FBX";
    private final String EXTERNAL_VIDEO_CYLINDER_FBX_PATH = "TestCpuGpu/videocylinder.obj";
    private final static String VIDEO_EXTERNAL_HIGH_QUALITY = "test5VideoOutside.mp4";
    private final static String VIDEO_EXTERNAL_LOW_QUALITY = "TestCpuGpu/zion_hd.mp4";
    private final static String VIDEO_INTERIOR_HIGH_QUALITY = "test5VideoInside.mp4";
    private final static String VIDEO_INTERIOR_LOW_QUALITY = "TestCpuGpu/zion_hd.mp4";


    //objects
    private static final String LEFT_DOOR_NAME = "videoscreen_360_left";
    private static final String RIGHT_DOOR_NAME = "videoscreen_360_right";
    private static final String RIGHT_WINDOW_SCREEN_NAME = "window_screen_right";
    private static final String LEFT_WINDOW_SCREEN_NAME = "window_screen_left";
    private static final String FRONT_WINDOW_SCREEN_NAME = "window_screen_front";
    private static final String BACK_WINDOW_SCREEN_NAME = "window_screen_back";
    private static final String FRONT_GLASS_DISPLAY_NAME = "front_glass_display";
    private static final String RIGHT_WINDOW_NAME = "window_right";
    private static final String LEFT_WINDOW_NAME = "window_left";
    private static final String BUTTON_R_UP = "r_button_up";
    private static final String BUTTON_R_DOWN = "r_button_down";
    private static final String BUTTON_R_RIGHT = "r_button_right";
    private static final String BUTTON_L_UP = "l_button_up";
    private static final String BUTTON_L_DOWN = "l_button_down";
    private static final String BUTTON_L_RIGHT = "l_button_right";
    private static final String MAIN_DISPLAY_2 = "main_display2";

    private String[] ObjNamesVideoDisp = {LEFT_DOOR_NAME, RIGHT_DOOR_NAME};
    private String[] ObjNamesToRemove = {RIGHT_WINDOW_SCREEN_NAME, LEFT_WINDOW_SCREEN_NAME, FRONT_WINDOW_SCREEN_NAME, BACK_WINDOW_SCREEN_NAME, RIGHT_WINDOW_NAME, LEFT_WINDOW_NAME, FRONT_GLASS_DISPLAY_NAME,
            MAIN_DISPLAY_2, BUTTON_R_UP, BUTTON_R_DOWN, BUTTON_R_RIGHT, BUTTON_L_UP, BUTTON_L_DOWN, BUTTON_L_RIGHT};


    private FPSCounter mFpsCounter;
    private TestDescOnCameraRig mTestDescDisp;
    private GVRSceneObject mCarSceneObject;
    private GVRVideoSceneObject mExternalVideoSceneObject, mInternalVideoSceneObject;
    private GVRCameraRig mMainCameraRig;
    private long mStartTime = -1;
    private boolean isResultSent = false;
    private GVRMaterial mVideoMaterial;


    public TestCpuGpuLoad(GVRContext gvrContext) {
        super(gvrContext);
        mGVRContext = gvrContext;
        mFpsCounter = new FPSCounter();
    }

    public void onInit() {

        try {

            mMainCameraRig = this.getMainCameraRig();
            mMainCameraRig.getTransform().setPosition(0f, 0.0f, 0.0f);

            mTestDescDisp = new TestDescOnCameraRig(mGVRContext, mMainCameraRig);
            mTestDescDisp.setPosition(3.0f, 0, -4);
            mTestDescDisp.setSize(4.0f);
             mTestDescDisp.setTestName(TEST_NAME);

            //load the car model
            mCarSceneObject = loadCarModel();
            if(mCarSceneObject != null) {
                initVideoInteriorDisplayList();
                loadInteriorVideo();
                playVideoInterior();
            }


            //load the surrounding model.
            mExternalVideoSceneObject = loadSurroundingVideoModel();
            if(mExternalVideoSceneObject != null) {
                addSceneObject(mExternalVideoSceneObject);
                //play external video
                playMediaInScObj(mExternalVideoSceneObject);
            }

            //attach dir light to the root
            GVRDirectLight dirLight = initializeDirLight();
            dirLight.setEnable(true);
            getRoot().attachLight(dirLight);

            mStartTime = System.currentTimeMillis();

        } catch (Exception e) {
            Log.e("Papiya", "exception: ", e);
        }

    }


    private GVRSceneObject loadCarModel() throws Exception {
        GVRSceneObject carSceneObject = mGVRContext.getAssetLoader().loadModel(CAR_FBX_PATH, this);
        if(carSceneObject != null) {
            carSceneObject.getTransform().setRotationByAxis(180.0f, 0, 1, 0);
            carSceneObject.getTransform().setScale(3.0f, 3.0f, 3.0f);
        }
        return carSceneObject;

    }


    @Override
    public void setExtraInfo(String info) {

    }

    @Override
    public String getSceneName() {
        return SCENE_NAME;
    }

    private void updateTestDesc(float currFps) {
        if (currFps >= 0) {
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
            if ((currTime - mStartTime) >= TEST_DURATION_IN_MILLISECS) {
                storeResult();
                sendResult();
                isResultSent = true;
                return;
            }
        }
    }

    LinkedHashMap<String, String> mTestResultData = new LinkedHashMap<String, String>();

    private void sendResult() {

        ResultData result = new
                ResultData(SCENE_ID, TEST_NAME , mTestResultData);

        mTestDescDisp.removeTestDescDisplay();
        //release all attached media
        releaseMediaInScObj(mExternalVideoSceneObject);
        releaseMediaInScObj(mInternalVideoSceneObject);
        removeAllSceneObjects();
        //this was not attached to the scene
        if (mInternalVideoSceneObject != null)
            removeSceneObject(mInternalVideoSceneObject);

        mTestCompleteListener.onResult(result);
    }


    private void storeResult() {
        float averageFps =
                mFpsCounter.getAverageFps();

        mTestResultData.put("Average fps: ", String.format("%4.2f", averageFps));

    }

    @Override
    public void registerTestCompleteListener(TestCompleteListener testCompleteListener) {
        mTestCompleteListener = testCompleteListener;

    }

    @Override
    public int getSceneID() {
        return SCENE_ID;

    }


    private GVRDirectLight initializeDirLight() {
        GVRDirectLight light = new GVRDirectLight(mGVRContext);

        light.setPosition(0.0f, 0.0f, 0.0f);
        final float MUL = 1.0f;
        light.setAmbientIntensity(0.5f, 0.5f, 0.5f, 1.0f);
        light.setDiffuseIntensity(1.0f * MUL, 1.0f * MUL, 1.0f * MUL, 1.0f);
        light.setSpecularIntensity(1.0f * MUL, 0.6f * MUL, 0.5f * MUL, 1.0f);
        return light;
    }


    //Load the surrounding video

    private GVRVideoSceneObject loadSurroundingVideoModel() {

        // create spherical video
        float sphereRadius = 10.0f;
        float[] spherePositionVector = {
                mMainCameraRig.getTransform().getPositionX(),
                mMainCameraRig.getTransform().getPositionY() - 8f,
                mMainCameraRig.getTransform().getPositionZ()
        };

        GVRVideoSceneObject sphVideoScObject = null;

        try {

            MediaPlayer mediaPlayer = createMediaPlayerFromFilePath(VIDEO_EXTERNAL_HIGH_QUALITY, VIDEO_EXTERNAL_LOW_QUALITY);
            GVRMesh mesh = mGVRContext.loadMesh(new GVRAndroidResource(mGVRContext, EXTERNAL_VIDEO_CYLINDER_FBX_PATH));

            sphVideoScObject = createVideoSceneObject(mesh, sphereRadius, spherePositionVector, mediaPlayer);
            if(sphVideoScObject != null) {
                sphVideoScObject.getTransform().setRotationByAxis(180.0f, 0, 1, 0);
            }
        } catch (Exception e) {
            Log.e(TAG, "exception", e);
        }

        return sphVideoScObject;

    }

    private static final float VIDEO_OPACITY_MIN = 0f;
    private static final float VIDEO_OPACITY_MAX = 0.9f;

    private GVRVideoSceneObject loadInteriorVideo() {

        try {

            MediaPlayer mediaPlayer = createMediaPlayerFromFilePath(VIDEO_INTERIOR_HIGH_QUALITY, VIDEO_INTERIOR_LOW_QUALITY);
            if (mediaPlayer != null) {
                mInternalVideoSceneObject = new GVRVideoSceneObject(mGVRContext, 2,
                        1, mediaPlayer, GVRVideoSceneObject.GVRVideoType.MONO);
                mInternalVideoSceneObject.getRenderData().setRenderMask(0);

                mVideoMaterial = mInternalVideoSceneObject.getRenderData().getMaterial();
                mVideoMaterial.setOpacity(VIDEO_OPACITY_MAX);
            }
        } catch (Exception e) {
            Log.e(TAG, "exception", e);
        }
        return mInternalVideoSceneObject;
    }


    //Create Video Scene Object

    private GVRVideoSceneObject createVideoSceneObject(GVRMesh mesh,
                                                       float sphereRadius, float[] spherePositionVector,
                                                       MediaPlayer mediaPlayer) {
        if((mediaPlayer == null) || (mesh == null)) {
            return null;
        }
        GVRVideoSceneObject videoSceneObject = new GVRVideoSceneObject(mGVRContext, mesh, mediaPlayer, GVRVideoSceneObject.GVRVideoType.MONO);

        videoSceneObject.setName("video");

        videoSceneObject.getTransform().setPosition(spherePositionVector[0],
                spherePositionVector[1], spherePositionVector[2]);
        videoSceneObject.getTransform().setScale(sphereRadius, sphereRadius, sphereRadius);
        return videoSceneObject;
    }

    //Create Media Player. If higher quality video is present, then take that...else the low quality one.

    private MediaPlayer createMediaPlayerFromFilePath(String filePathHighQuality, String filePathLowQuality) throws Exception {

        MediaPlayer mPlayer = new MediaPlayer();
        if (Utils.isHighQualityVideoAvailable(filePathHighQuality)) {
            filePathHighQuality = Environment.getExternalStorageDirectory()
                    + File.separator + filePathHighQuality;
            mPlayer.setDataSource(filePathHighQuality);

        } else {

            AssetFileDescriptor afd = mGVRContext.getContext().getAssets().openFd(filePathLowQuality);
            mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

        }
        if (mPlayer != null) {
            mPlayer.prepare();
            mPlayer.setLooping(true);
        }
        return mPlayer;
    }

    private void playMediaInScObj(GVRVideoSceneObject videoSceneObj) {
        if (videoSceneObj == null) {
            return;

        }
        MediaPlayer mediaPlayer = (MediaPlayer) videoSceneObj.getMediaPlayer().getPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

    }

    private void releaseMediaInScObj(GVRVideoSceneObject videoSceneObj) {
        if (videoSceneObj == null) {
            return;

        }
        MediaPlayer mediaPlayer = (MediaPlayer) videoSceneObj.getMediaPlayer().getPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

    }

    private void playVideoInterior() {
        if ((ObjNamesVideoDisp == null) || (ObjNamesVideoDisp.length == 0) || (mVideoMaterial == null)) {
            return;
        }
        playMediaInScObj(mInternalVideoSceneObject);
        for (String objName : ObjNamesVideoDisp) {
            GVRSceneObject videoScObj = mCarSceneObject.getSceneObjectByName(objName);
            videoScObj.getRenderData().setMaterial(mVideoMaterial);
        }
    }

    public void activateVideoDisplays() {
        for (String objName : ObjNamesVideoDisp) {
            GVRSceneObject videoScObj = mCarSceneObject.getSceneObjectByName(objName);
            Utils.setRenderMaskAndOpacity(videoScObj,
                    GVRRenderData.GVRRenderMaskBit.Left | GVRRenderData.GVRRenderMaskBit.Right, 1);
        }
    }

    public void deactivateSomeInteriorDisplays() {
        for (String objName : ObjNamesToRemove) {
            GVRSceneObject scObj = mCarSceneObject.getSceneObjectByName(objName);
            Utils.setRenderMask(scObj, 0);
        }
    }


    //get the list of objects on which interior video will be displayed
    private void initVideoInteriorDisplayList() {
        activateVideoDisplays();
        deactivateSomeInteriorDisplays();
    }


}
