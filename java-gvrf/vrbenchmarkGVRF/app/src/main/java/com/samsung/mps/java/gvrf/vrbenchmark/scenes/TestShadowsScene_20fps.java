
package com.samsung.mps.java.gvrf.vrbenchmark.scenes;

import com.samsung.mps.java.gvrf.vrbenchmark.FPSCounter;
import com.samsung.mps.java.gvrf.vrbenchmark.ResultData;
import com.samsung.mps.java.gvrf.vrbenchmark.SceneExt;
import com.samsung.mps.java.gvrf.vrbenchmark.TestCompleteListener;
import com.samsung.mps.java.gvrf.vrbenchmark.TestDescOnCameraRig;


import org.gearvrf.*;
import org.gearvrf.GVRTextureParameters.TextureWrapType;
import org.joml.Vector3f;
import org.joml.Matrix4f;


import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class TestShadowsScene_20fps extends SceneExt {

    private static final String TAG = "Test_ShadowScene";

    private static final String SCENE_NAME = "Shadows";

    private static final int SCENE_ID = -200;


    private static final int MULTIPLIER = 10;
    public static boolean SUBTEST_DIR_LIGHT = false;
    public static boolean SUBTEST_SPOT_LIGHT = false;
    public static boolean SUBTEST_POINT_LIGHT = false;
    public static float TOTAL_SUBTEST_DIRLIGHT_DURATION = 20.0f * 1000.0f;;
    public static float TOTAL_SUBTEST_SPOTLIGHT_DURATION = 20.0f * 1000.0f;;
    public static float TOTAL_SUBTEST_POINTLIGHT_DURATION = 10.0f * 1000.0f;;
    public static float TEST_DURATION = TOTAL_SUBTEST_DIRLIGHT_DURATION
            + TOTAL_SUBTEST_SPOTLIGHT_DURATION + TOTAL_SUBTEST_POINTLIGHT_DURATION;
    private TestCompleteListener mTestCompleteListener;
    private GVRContext mGVRContext;
    private GVRSceneObject mRootSceneObject;
    private final String FBX_PATH = "Common/TestShadows.FBX";
    private long mStartTime = -1;
    private boolean textWrappingDone = false;
    private long mTimeTakenByFrame, mPrevFrameTime = -1;

    private String mOtherErrorNote, mCurrSubTestName;

    private ArrayList<GVRDirectLight> mListDirLights;
    private ArrayList<GVRPointLight> mListPointLights;
    private ArrayList<GVRSpotLight> mListSpotLights;
    ArrayList<GVRSceneObject> listObjTexWrapping = new ArrayList<GVRSceneObject>();
    private FPSCounter mFpsCounter;
    private TestDescOnCameraRig mTestDescDisp;

    public TestShadowsScene_20fps(GVRContext gvrContext) {
        super(gvrContext);
        mGVRContext = gvrContext;
        mListDirLights = new ArrayList<GVRDirectLight>();
        mListPointLights = new ArrayList<GVRPointLight>();
        mListSpotLights = new ArrayList<GVRSpotLight>();
        mFpsCounter = new FPSCounter();

    }

    public void onInit() {
        // set background color
        GVRCameraRig mainCameraRig = this.getMainCameraRig();
        mainCameraRig.getLeftCamera()
                .setBackgroundColor(Color.BLACK);
        mainCameraRig.getRightCamera()
                .setBackgroundColor(Color.BLACK);
        mainCameraRig.getTransform().setPosition(0, 1.5f, 3.5f);
        mTestDescDisp = new TestDescOnCameraRig(mGVRContext,mainCameraRig);
        mTestDescDisp.setTestName("Shadows Test");

        try {
            mRootSceneObject = mGVRContext.getAssetLoader().loadModel(FBX_PATH);
            mRootSceneObject.getTransform().setPosition(0.0f, 0.0f, 0.0f);
            mRootSceneObject.getTransform().setRotationByAxis(180.0f, 0, 1, 0);
            mRootSceneObject.getTransform().setScale(1.0f, 1.0f, 1.0f);

            GVRSceneObject wall = mRootSceneObject.getSceneObjectByName("wall");
            listObjTexWrapping.add(wall);

            GVRSceneObject windowWall2 = mRootSceneObject.getSceneObjectByName("windowwall (2)");
            listObjTexWrapping.add(windowWall2);

            setMaterials();
            addSceneObject(mRootSceneObject);

            initLights();
            testDirLight();
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



    private void initLights() {
        GVRDirectLight dirLight1 = (GVRDirectLight) mRootSceneObject.getSceneObjectByName(
                "DirectionalLight1")
                .getLight();
        setDirLightProperties(dirLight1);
        mListDirLights.add(dirLight1);

        GVRDirectLight dirLight2 = (GVRDirectLight) mRootSceneObject.getSceneObjectByName(
                "DirectionalLight2")
                .getLight();
        setDirLightProperties(dirLight2);
        mListDirLights.add(dirLight2);

        GVRPointLight ptLight1 = (GVRPointLight) mRootSceneObject.getSceneObjectByName(
                "Point Light1")
                .getLight();
        setPointLightProperties(ptLight1);
        mListPointLights.add(ptLight1);

        GVRPointLight ptLight2 = (GVRPointLight) mRootSceneObject.getSceneObjectByName(
                "Point Light2")
                .getLight();
        setPointLightProperties(ptLight2);
        mListPointLights.add(ptLight2);

        GVRPointLight ptLight3 = (GVRPointLight) mRootSceneObject.getSceneObjectByName(
                "Point Light3")
                .getLight();
        setPointLightProperties(ptLight3);
        mListPointLights.add(ptLight3);

        GVRPointLight ptLight4 = (GVRPointLight) mRootSceneObject.getSceneObjectByName(
                "Point Light4")
                .getLight();
        setPointLightProperties(ptLight4);
        mListPointLights.add(ptLight4);

        GVRPointLight ptLight5 = (GVRPointLight) mRootSceneObject.getSceneObjectByName(
                "Point Light5")
                .getLight();
        setPointLightProperties(ptLight5);
        mListPointLights.add(ptLight5);

        GVRPointLight ptLight6 = (GVRPointLight) mRootSceneObject.getSceneObjectByName(
                "Point Light6")
                .getLight();
        setPointLightProperties(ptLight6);
        mListPointLights.add(ptLight6);

        GVRSpotLight spotLight1 = (GVRSpotLight) mRootSceneObject
                .getSceneObjectByName("SpotLight1")
                .getLight();
        setSpotLightProperties(spotLight1, 1, 1, 0);
        mListSpotLights.add(spotLight1);

       GVRSpotLight spotLight2 = (GVRSpotLight) mRootSceneObject
                .getSceneObjectByName("SpotLight2").getLight();
        setSpotLightProperties(spotLight2, 0, 1, 0);
        mListSpotLights.add(spotLight2);

        GVRSpotLight spotLight3 = (GVRSpotLight) mRootSceneObject
                .getSceneObjectByName("SpotLight3").getLight();
        setSpotLightProperties(spotLight3, 1, 0, 0);
        mListSpotLights.add(spotLight3);

        SUBTEST_DIR_LIGHT = false;
        SUBTEST_SPOT_LIGHT = false;
        SUBTEST_POINT_LIGHT = false;

    }


    private void setMaterials() {

        GVRSceneObject glass = mRootSceneObject.getSceneObjectByName("glass");
        glass.getRenderData().getMaterial().setOpacity(0);
        glass.getRenderData().setRenderMask(0);

        GVRSceneObject glass2 = mRootSceneObject.getSceneObjectByName("glass (2)");
        glass2.getRenderData().getMaterial().setOpacity(0);
        glass2.getRenderData().setRenderMask(0);
        GVRSceneObject glass3 = mRootSceneObject.getSceneObjectByName("glass (3)");
        glass3.getRenderData().getMaterial().setOpacity(0);
        glass3.getRenderData().setRenderMask(0);
        GVRSceneObject glass4 = mRootSceneObject.getSceneObjectByName("glass (4)");
        glass4.getRenderData().getMaterial().setOpacity(0);
        glass4.getRenderData().setRenderMask(0);

        GVRSceneObject grass = mRootSceneObject.getSceneObjectByName("Grass Clumps 01");
        grass.getRenderData().getMaterial().setDiffuseColor(0.09804626f, 0.32594937f, 0, 1.0f);

        GVRSceneObject lowerPanel = mRootSceneObject.getSceneObjectByName("lowerpanel");
        lowerPanel.getRenderData().getMaterial()
                .setDiffuseColor(0.21926828f, 0.24264705f, 0.17484862f, 1.0f);

        GVRSceneObject lowerPanel2 = mRootSceneObject.getSceneObjectByName("lowerpanel (2)");
        lowerPanel2.getRenderData().getMaterial()
                .setDiffuseColor(0.21926828f, 0.24264705f, 0.17484862f, 1.0f);

    }

    private synchronized void setTextureWrapping() {

        int doneCount = 0;
        for (GVRSceneObject obj : listObjTexWrapping) {
            List<GVRSceneObject> children = obj.getChildren();
            for (GVRSceneObject child : children) {
                GVRRenderData renderData = child.getRenderData();
                if (renderData != null) {
                    GVRMaterial material = renderData.getMaterial();

                    if (material != null) {
                        Set<String> keys = material.getTextureNames();
                        for (String key : keys) {
                            GVRTexture bmT = (GVRTexture) material.getTexture(key);
                            if (bmT != null) {

                                if (bmT instanceof GVRBitmapTexture) {
                                    GVRTextureParameters texParams = new GVRTextureParameters(
                                            mGVRContext);
                                    texParams.setWrapSType(TextureWrapType.GL_REPEAT);
                                    texParams.setWrapTType(TextureWrapType.GL_REPEAT);
                                    bmT.updateTextureParameters(texParams);
                                    renderData.setMaterial(material);
                                    doneCount++;

                                } else {
                                    Log.d(TAG,
                                            "texture is not GVRBitmapTexture " + ":"
                                                    + child.getName());
                                }
                            } else {
                                Log.d(TAG, "texture is null " + ":" + child.getName());
                            }
                        }
                    } else {
                        Log.d(TAG, "material is null " + ":" + obj.getName());
                    }
                } else {
                    Log.d(TAG, "renderdata is null " + ":" + obj.getName());
                }
            }

        }

        if (doneCount == 2) {
            textWrappingDone = true;
        }

    }

    @Override
    public void registerTestCompleteListener(TestCompleteListener testCompleteListener) {
        mTestCompleteListener = testCompleteListener;

    }

    public void onStep() {
        if (mStartTime != -1) {
            float fps = mFpsCounter.tick();
            if(fps >= 0) {
                mTestDescDisp.setSubTestName(mCurrSubTestName);
                mTestDescDisp.setErrorsToDisplay(mOtherErrorNote);
                mTestDescDisp.setFps(fps);
                mTestDescDisp.displayTestData();
            }
        }
        if (!textWrappingDone) {
            mGVRContext.runOnGlThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            setTextureWrapping();

                        }
                    });
        }

        IsSwitchSubTest();
        animateDirLights();
        animateSpotLights();
        animatePointLights();
    }

    void IsSwitchSubTest() {

        long currTime = System.currentTimeMillis();

        long timeSpentInTest = (currTime - mStartTime);

        if (mPrevFrameTime == -1) {
            mPrevFrameTime = currTime;
        }

        mTimeTakenByFrame = (currTime - mPrevFrameTime);
        mPrevFrameTime = currTime;

        if ((SUBTEST_DIR_LIGHT == true) && (timeSpentInTest >= TOTAL_SUBTEST_DIRLIGHT_DURATION)) {

            testSpotLight();
        } else if ((SUBTEST_SPOT_LIGHT == true)
                && (timeSpentInTest >= (TOTAL_SUBTEST_DIRLIGHT_DURATION + TOTAL_SUBTEST_SPOTLIGHT_DURATION))) {
            testPointLight();
        } else if ((SUBTEST_POINT_LIGHT == true)
                && (timeSpentInTest >= (TEST_DURATION))) {

            float averageFps =
                    mFpsCounter.getAverageFps();
            LinkedHashMap<String,String> testResultData = new LinkedHashMap<String, String>();
            testResultData.put("Average fps: ",  String.format("%4.2f", averageFps));
            ResultData result = new
                    ResultData(SCENE_ID, "Shadows Test: ", testResultData);
            removeSceneObject(mRootSceneObject);
            mTestDescDisp.removeTestDescDisplay();
            mTestCompleteListener.onResult(result);
        }

    }

    @Override
    public int getSceneID() {
        return SCENE_ID;

    }

    // Directional Lights
    private static float DEG_ROTATION = 1.0f * MULTIPLIER ;//just to increase speed

    private class DirLightExtras {
        private float degToAdd;
        private float xAngle = 0;
    }

    private HashMap<String, DirLightExtras> dirLightData = new HashMap<String, DirLightExtras>();

    private void setDirLightProperties(GVRDirectLight dirLight) {
        dirLight.setCastShadow(false);
        dirLight.setEnable(false);
        DirLightExtras extras = new DirLightExtras();
        extras.degToAdd = DEG_ROTATION;
        extras.xAngle = 0;
        dirLightData.put(dirLight.getLightID(), extras);

    }

    void testDirLight() {
        SUBTEST_DIR_LIGHT = true;
        mStartTime = System.currentTimeMillis();
        for (GVRDirectLight dirLight : mListDirLights) {
            dirLight.setEnable(true);
            dirLight.setCastShadow(true);
        }
        mCurrSubTestName = "Dir Light";
        mOtherErrorNote = "Shadow not proper";
     }

    private void animateDirLights() {
        if (SUBTEST_DIR_LIGHT)
        {
            for (GVRDirectLight dirLight : mListDirLights) {
                DirLightExtras extras = dirLightData.get(dirLight.getLightID());
                dirLight.getTransform().rotateByAxis(extras.degToAdd, -1, 0, 0);

                extras.xAngle += extras.degToAdd;

                // if 180 then go back again ...no need of going up
                // 0-180
                if (Math.floor(extras.xAngle) <= 0)
                {
                    extras.degToAdd = DEG_ROTATION;

                } else if (Math.floor(extras.xAngle) >= 180)
                {
                    extras.degToAdd = -DEG_ROTATION;

                }
            }

        }

    }

    // SpotLight
    // since we rotated by 180 degrees-- so left and right got changed
    private static final Vector3f VECTOR_LEFT = new Vector3f(+1, 0, 0);
    private static final Vector3f VECTOR_RIGHT = new Vector3f(-1, 0, 0);
    private static final Vector3f VECTOR_FORWARD = new Vector3f(0, 0, -1);
    private static final Vector3f VECTOR_BACKWARD = new Vector3f(0, 0, +1);

    private class spotLightExtras {
        private Vector3f objOrigPos;
        private float limitLeft;
        private float limitRight;
        private float limitForward;
        private float limitBackward;
        private Vector3f currVector;
        private boolean goOrigPos;
     }

    private HashMap<String, spotLightExtras> spotLightData = new HashMap<String, spotLightExtras>();

    private static float DISP = 2.0f;

    private void setSpotLightProperties(GVRSpotLight spotLight, float r, float g, float b) {
        spotLight.setEnable(false);
        spotLight.setCastShadow(false);
        spotLight.setInnerConeAngle(20);
        spotLight.setOuterConeAngle(25);
        spotLight.setAttenuation( 1.0f, 0.14f, 0.07f); //32
        spotLight.setAmbientIntensity(r, g, b, 1);
        spotLight.setDiffuseIntensity(r, g, b, 1);


        spotLightExtras extras = new spotLightExtras();
        float[] pos = spotLight.getPosition();
        extras.objOrigPos = new Vector3f(pos[0], pos[1], pos[2]);
        extras.limitLeft = extras.objOrigPos.x - DISP;
        extras.limitRight = extras.objOrigPos.x + DISP;
        extras.limitForward = extras.objOrigPos.z - DISP;
        extras.limitBackward = extras.objOrigPos.z + DISP;
        extras.currVector = VECTOR_LEFT;
        extras.goOrigPos = false;
        spotLightData.put(spotLight.getLightID(), extras);

    }


    void testSpotLight() {
        SUBTEST_DIR_LIGHT = false;
        for (GVRDirectLight dirLight : mListDirLights) {
            dirLight.setEnable(false);
            dirLight.setCastShadow(false);
        }

        SUBTEST_SPOT_LIGHT = true;
        for (GVRSpotLight spotLight : mListSpotLights) {
            spotLight.setEnable(true);
            spotLight.setCastShadow(true);
        }
        mCurrSubTestName = "Spot Light";
        mOtherErrorNote = "Shadow not shown";
    }

    public Vector3f getWorldPos(GVRSceneObject obj)
    {
        Vector3f mCurrWorldPos = new Vector3f();
        Matrix4f worldmtx = obj.getTransform().getModelMatrix4f();
        worldmtx.getTranslation(mCurrWorldPos);
        return mCurrWorldPos;
    }


    private void animateSpotLights() {
        if (SUBTEST_SPOT_LIGHT) {
            for (GVRSpotLight spotLight : mListSpotLights) {

                Vector3f prevLight = new Vector3f(spotLight.getPosition()[0],spotLight.getPosition()[1],spotLight.getPosition()[2]);

                spotLightExtras extras = spotLightData.get(spotLight.getLightID());
                Vector3f targetObj = new Vector3f(extras.objOrigPos.x, 0, extras.objOrigPos.z);

                Vector3f translateVec = new Vector3f();
                extras.currVector.mul((mTimeTakenByFrame / 100), translateVec);
                spotLight.getTransform().translate(translateVec.x, translateVec.y, translateVec.z);


                Vector3f currLight = getWorldPos(spotLight.getOwnerObject());

                lookAt(prevLight,currLight, targetObj, spotLight);

               float xPos = currLight.x;
               float zPos = currLight.z;

                    if (extras.currVector == VECTOR_LEFT) {
                        if (xPos <= extras.limitLeft) {
                            extras.currVector = VECTOR_RIGHT;
                        } else if ((xPos <= extras.objOrigPos.x) && (extras.goOrigPos == true)) {

                            spotLight.setPosition(extras.objOrigPos.x,
                                    extras.objOrigPos.y, extras.objOrigPos.z);

                            prevLight = currLight;
                            currLight = getWorldPos(spotLight.getOwnerObject());
                            lookAt(prevLight, currLight, targetObj, spotLight);

                            extras.goOrigPos = false;
                            extras.currVector = VECTOR_FORWARD;
                        }
                        spotLightData.put(spotLight.getLightID(), extras);

                    } else if (extras.currVector == VECTOR_RIGHT) {
                        if (xPos >= extras.limitRight) {
                            extras.currVector = VECTOR_LEFT;
                            extras.goOrigPos = true;
                        }
                        spotLightData.put(spotLight.getLightID(), extras);

                    } else if (extras.currVector == VECTOR_FORWARD) {
                        if (zPos <= extras.limitForward) {
                            extras.currVector = VECTOR_BACKWARD;
                        } else if ((zPos <= extras.objOrigPos.z) && (extras.goOrigPos == true)) {
                            spotLight.setPosition(extras.objOrigPos.x,
                                    extras.objOrigPos.y, extras.objOrigPos.z);

                            prevLight = currLight;
                            currLight = getWorldPos(spotLight.getOwnerObject());
                            lookAt(prevLight, currLight, targetObj, spotLight);

                            extras.goOrigPos = false;
                            extras.currVector = VECTOR_LEFT;
                        }
                        spotLightData.put(spotLight.getLightID(), extras);

                    } else if (extras.currVector == VECTOR_BACKWARD) {
                        if (zPos >= extras.limitBackward) {
                            extras.currVector = VECTOR_FORWARD;
                            extras.goOrigPos = true;
                        }
                        spotLightData.put(spotLight.getLightID(), extras);

                    }
                }

            }
        }



    void lookAt(Vector3f prevLight,Vector3f currLight, Vector3f targetObj, GVRSpotLight spotLight) {
        try {

            spotLightExtras extras = spotLightData.get(spotLight.getLightID());

            Vector3f currLightTarget = new Vector3f();
            currLight.sub(targetObj, currLightTarget);
            currLightTarget.normalize();

            Vector3f prevLightTarget = new Vector3f();
            prevLight.sub(targetObj, prevLightTarget);
            prevLightTarget.normalize();


            if(prevLightTarget.x  == (currLightTarget.x) && (prevLightTarget.y  == currLightTarget.y) && (prevLightTarget.z  == currLightTarget.z)) {

               return;

            }

            float angleInDegrees = (float) Math.toDegrees(prevLightTarget.angle(currLightTarget));
            Vector3f axis = new Vector3f();
            prevLightTarget.cross(currLightTarget, axis);

            Vector3f axisNormalized = new Vector3f();
            axis.normalize(axisNormalized);

            spotLight.getTransform().rotateByAxis(angleInDegrees, -axisNormalized.x, axisNormalized.y,
                    axisNormalized.z);

           } catch(Exception e) {
            Log.e(TAG, "exception :", e);
        }

    }



    // PointLights

    private class PointLightExtras {
        private float timeLeftForTest;
    }

    private HashMap<String, PointLightExtras> pointLightData = new HashMap<String, PointLightExtras>();

    private void setPointLightProperties(GVRPointLight ptLight) {
        ptLight.setEnable(false);
        ptLight.setCastShadow(false);
        ptLight.setAttenuation( 1.0f, 0.14f, 0.07f);//32
        PointLightExtras extras = new PointLightExtras();
        extras.timeLeftForTest = TOTAL_SUBTEST_POINTLIGHT_DURATION;
        pointLightData.put(ptLight.getLightID(), extras);
    }

    void testPointLight()
    {
        SUBTEST_SPOT_LIGHT = false;
        for (GVRSpotLight spotLight : mListSpotLights) {
            spotLight.setEnable(false);
            spotLight.setCastShadow(false);
        }

        SUBTEST_POINT_LIGHT = true;
        for (GVRPointLight ptLight : mListPointLights) {
            ptLight.setEnable(true);
          //  ptLight.setCastShadow(true); //not supported now
        }

        mCurrSubTestName = "Point Light";
        mOtherErrorNote = "shadow not supported";

    }

    private void animatePointLights() {
        if (SUBTEST_POINT_LIGHT) {
            for (GVRPointLight ptLight : mListPointLights) {
                PointLightExtras extra = pointLightData.get(ptLight.getLightID());
                if (extra.timeLeftForTest > (TOTAL_SUBTEST_POINTLIGHT_DURATION * (3f / 4f))) {
                    Vector3f translateVec = new Vector3f();
                    VECTOR_LEFT.mul(mTimeTakenByFrame/100,translateVec);
                    ptLight.getTransform()
                            .translate(translateVec.x, translateVec.y, translateVec.z);
                } else if (extra.timeLeftForTest > (TOTAL_SUBTEST_POINTLIGHT_DURATION / 2f)) {
                    Vector3f translateVec = new Vector3f();
                    VECTOR_RIGHT.mul(mTimeTakenByFrame/100,translateVec);
                    ptLight.getTransform()
                            .translate(translateVec.x, translateVec.y, translateVec.z);
                } else if (extra.timeLeftForTest > (TOTAL_SUBTEST_POINTLIGHT_DURATION / 4f)) {
                    Vector3f translateVec = new Vector3f();
                    VECTOR_FORWARD.mul(mTimeTakenByFrame/100,translateVec);
                    ptLight.getTransform()
                            .translate(translateVec.x, translateVec.y, translateVec.z);
                } else {
                    Vector3f translateVec = new Vector3f();
                    VECTOR_BACKWARD.mul(mTimeTakenByFrame,translateVec);
                    ptLight.getTransform()
                            .translate(translateVec.x, translateVec.y, translateVec.z);
                }
            }

        }

    }

}
