
CURR_FILE_PATH=$(readlink -f "$0")
CURR_PRJ_PATH=$(dirname "$CURR_FILE_PATH")
cd $CURR_PRJ_PATH/Assets/Scripts/Editor
"/opt/Unity/Editor/Unity" -quit -batchmode -logFile -projectPath $CURR_PRJ_PATH -executeMethod UnityBuildScript.PerformBuild


