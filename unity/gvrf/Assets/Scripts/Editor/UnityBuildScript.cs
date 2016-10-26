using UnityEditor;
public static class UnityBuildScript
{
     static void PerformBuild ()
     {
        EditorUserBuildSettings.SwitchActiveBuildTarget(BuildTarget.Android);
        EditorUserBuildSettings.androidBuildSubtarget = MobileTextureSubtarget.ASTC;
        BuildPipeline.BuildPlayer(GetScenePaths(), "Builds/Android/VRBenchmark_GVRf.apk", BuildTarget.Android, BuildOptions.None);
    }


    static string[] GetScenePaths()
    {
        string[] scenes = new string[EditorBuildSettings.scenes.Length];

        for (int i = 0; i < scenes.Length; i++)
        {
            scenes[i] = EditorBuildSettings.scenes[i].path;
        }
        return scenes;
    }
}


/*
**can refer to:
    //http://wiki.unity3d.com/index.php?title=AutoBuilder
**command:
     "C:\Program Files\Unity\Editor\Unity.exe" -quit -batchmode -logFile -executeMethod UnityBuildScript.PerformBuild
**my path of adb:
      C:/Android/android-sdk/platform-tools/adb.exe
**editor log file path:
      C:\Users\pnath\AppData\Local\Unity\Editor
*/
