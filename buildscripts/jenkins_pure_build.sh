#!/bin/bash

sed -i 's/backend_oculus/backend_pure/g' ~/.gradle/gradle.properties
cd ./lib_gvrfhybrid/GearVRf/GVRf/Framework/
ln -s ../../../../lib_gvrfpure/Gear-VR-Framework/GVRf/Framework/backend_pure/ .
./gradlew clean
./gradlew assembleDebug
cd ../../../../benchmark/java-gvrf/vrbenchmarkGVRF/
./gradlew clean
./gradlew assemblepureDebug
