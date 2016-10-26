#!/bin/bash

# Add full path to aapt if there any error
AAPT="aapt"
HYBRID_APK="app/build/outputs/apk/app-hybrid-debug.apk"
HYBRID_PACKAGE="com.samsung.mps.java.gvrf.hybrid.vrbenchmark"
HYBRID_LIB="libgvrf-oculus.so"
PURE_APK="app/build/outputs/apk/app-pure-debug.apk"
PURE_PACKAGE="com.samsung.mps.java.gvrf.pure.vrbenchmark"
PURE_LIB="libgvrf-pure.so"
if [ -f $HYBRID_APK ]; then
	#echo "Hybrid apk file" 
	if $AAPT dump permissions $HYBRID_APK | grep $HYBRID_PACKAGE; then
		if $AAPT list $HYBRID_APK | grep $HYBRID_LIB; then
			echo "CORRECT BUILD"
		else
			echo "WRONG BACKEND"
		fi
	else 
		echo "WRONG PACKAGE";
	fi
elif [ -f  $PURE_APK ]; then
	#	echo "Pure apk file"
	if $AAPT dump permissions $PURE_APK | grep $PURE_PACKAGE; then
		if $AAPT list $PURE_APK | grep $PURE_LIB; then
			echo "CORRECT BUILD"
		else
			echo "WRONG BACKEND"
		fi
	else 
		echo "WRONG PACKAGE";
	fi
else
	echo "Unknown config"
fi
