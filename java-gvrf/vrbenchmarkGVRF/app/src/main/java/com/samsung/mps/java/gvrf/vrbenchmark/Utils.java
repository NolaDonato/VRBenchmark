package com.samsung.mps.java.gvrf.vrbenchmark;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.gearvrf.GVRBitmapTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTextureParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by p.nath on 10/7/16.
 */
public class Utils {
    private static final String TAG = "Test_Utils";


    private static String[] sArrS6DevModels = {};
    private static String[] sArrS6EdgeDevModels = {};
    private static String[] sArrS7DevModels = {"SM-G930"};
    private static String[] sArrS7EdgeDevModels = {"SM-G935"};

    private static HashMap<String, List<String>> mMapDeviceNames = new HashMap<String, List<String>>();

    private static String sDeviceName = null;


    public static  String findDeviceName()
    {
        if(sDeviceName != null) {
            return sDeviceName;
        }

        mMapDeviceNames.put("S6", Arrays.asList(sArrS6DevModels));
        mMapDeviceNames.put("S6Edge", Arrays.asList(sArrS6EdgeDevModels));
        mMapDeviceNames.put("S7", Arrays.asList(sArrS7DevModels));
        mMapDeviceNames.put("S7Edge", Arrays.asList(sArrS7EdgeDevModels));

        String shortDevName = null; ;
        String devModelFromSettings = Build.MODEL;
        Set<String> keys = mMapDeviceNames.keySet();
        for (String key : keys)
        {
            String devName = key;
            List<String> modelNames = mMapDeviceNames.get(key);

            for(String modelName : modelNames)
            {
                if(devModelFromSettings.contains(modelName))
                {
                    shortDevName = devName;
                    break;
                }

            }
            if(shortDevName != null)
            {
                break;
            }
        }
        return shortDevName;
    }


    public static boolean isHighQualityVideoAvailable(String fileName) {
        Uri videoFileUri = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String strUri = "file://" + Environment.getExternalStorageDirectory()
                    + File.separator + fileName;
            Uri uri = Uri.parse(strUri);
            File file = new File(uri.getPath());
            if (file.exists()) {
               return true;
            }

        }
        return false;
    }

    public static void setTextureWrappingType(GVRContext gvrContext, GVRTexture tex, GVRTextureParameters.TextureWrapType wrapType) {
        GVRTextureParameters texParams = new GVRTextureParameters(gvrContext);
        texParams.setWrapSType(wrapType);
        texParams.setWrapTType(wrapType);
        tex.updateTextureParameters(texParams);

    }

    public static void setTextureWrapping(GVRContext gvrContext, ArrayList<GVRSceneObject> listObjTexWrapping,GVRTextureParameters.TextureWrapType wrapType ) {
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
                                    setTextureWrappingType(gvrContext, bmT, wrapType);
                                    renderData.setMaterial(material);

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

    }
    public static void setRenderMask(GVRSceneObject obj, int renderMask) {
        if (obj == null) {
            return;
        }
        if ((obj.getRenderData() != null)) {
            GVRRenderData renderData = obj.getRenderData();
            renderData.setRenderMask(renderMask);
        }
        for (GVRSceneObject child : obj.getChildren()) {
            setRenderMask(child, renderMask);
        }
    }

    public static void setRenderMaskAndOpacity(GVRSceneObject obj, int renderMask, float opacity) {
        if (obj == null) {
            return;
        }
        if ((obj.getRenderData() != null)) {
            GVRRenderData renderData = obj.getRenderData();
            renderData.setRenderMask(renderMask);
            renderData.getMaterial().setOpacity(opacity);
        }
        for (GVRSceneObject child : obj.getChildren()) {
            setRenderMask(child, renderMask);
        }
    }

}
