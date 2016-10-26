#!/bin/bash

sed -i 's/backend_pure/backend_oculus/g' ~/.gradle/gradle.properties
cd ./lib_gvrfhybrid/GearVRf/
ln -s ../../benchmark/external/ovr_sdk_mobile/ .
cp ovr_sdk_mobile/VrApi/Libs/Android/VrApi.jar GVRf/Framework/framework/src/main/libs/
cp ovr_sdk_mobile/VrAppFramework/Libs/Android/VrAppFramework.jar GVRf/Framework/framework/src/main/libs/
cp ovr_sdk_mobile/VrAppSupport/SystemUtils/Libs/Android/SystemUtils.jar GVRf/Framework/framework/src/main/libs/
cd ./GVRf/Framework/
./gradlew clean
./gradlew assembleDebug
cd ../../../../benchmark/java-gvrf/vrbenchmarkGVRF/
./gradlew clean
./gradlew assemblehybridDebug
