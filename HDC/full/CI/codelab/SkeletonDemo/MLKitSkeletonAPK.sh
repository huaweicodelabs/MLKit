#!/bin/bash


SCRIPT_DIR=$(cd "$(dirname "$0")";pwd)

#************* 修改部分 ******************
#相对于mlkit-source的目录 比如骨骼aidl模块路径：sdk/Human/skeleton/skeletonaidl
MODULE_DIR=SkeletonDemo/app

#模块名
MODULE=app

#上传云龙apk包名
SUBPKGNAME=skeletondemo
#************* 修改部分 ******************

cd ${SCRIPT_DIR}
cd ../../${MODULE_DIR}



functionBuild(){

if [[  ${WORKSPACE} == "" ]]; then
    cd ${SCRIPT_DIR}/../../${MODULE_DIR}
    echo "this is in local!"
else
    cd ${SCRIPT_DIR}/../../${MODULE_DIR}
    #华为签名
    export needHwSign=true
    echo "this is in remote!"

    if [[ ${releaseVersion} == "" ]]; then
        echo "this is snapshot version!"
    else
        echo "this is release version!"
        cd ${SCRIPT_DIR}
        cd ..
        source getVersionCode.sh
        source changeOpenSource.sh $ml_versionName
        cd ${SCRIPT_DIR}/../../${MODULE_DIR}
    fi
fi


gradle assembleRelease
gradle assembleDebug



}

functionCopyApk(){

cd build/outputs/apk/release

if [[  $releaseVersion == "" ]]; then
    timestap=$(date "+%Y%m%d%H%M%S")
    PACKAGE_PRODUCT=ML-product-debug-${SUBPKGNAME}-$timestap
    PACKAGE_MIRROR=ML-mirror-debug-${SUBPKGNAME}-$timestap
	  PACKAGE_NOGARD=ML-nogard-debug-${SUBPKGNAME}-$timestap
	  PACKAGE_STAGING=ML-staging-debug-${SUBPKGNAME}-$timestap
else
    PACKAGE_PRODUCT=ML-product-release-${SUBPKGNAME}-${releaseVersion}
    PACKAGE_MIRROR=ML-mirror-release-${SUBPKGNAME}-${releaseVersion}
	  PACKAGE_NOGARD=ML-nogard-release-${SUBPKGNAME}-${releaseVersion}
	  PACKAGE_STAGING=ML-staging-release-${SUBPKGNAME}-${releaseVersion}
fi

#判断目录 有清除目录下文件 没有则创建目录
if [ -d ${SCRIPT_DIR}/../../build ]; then
    echo '--MLKit-- has build dir'
else
    mkdir  ${SCRIPT_DIR}/../../build
fi
if [ -d ${SCRIPT_DIR}/../../build/releaseApk ]; then
    rm -rf ${SCRIPT_DIR}/../../build/releaseApk/*
else
    mkdir  ${SCRIPT_DIR}/../../build/releaseApk
fi

#复制到工程build/releaseApk目录下
PKNAME=`ls | grep ${MODULE}`
cp ${PKNAME} ${SCRIPT_DIR}/../../build/releaseApk/${PACKAGE_PRODUCT}.apk
cp ${PKNAME} ${SCRIPT_DIR}/../../build/releaseApk/${PACKAGE_NOGARD}.apk
cp ${PKNAME} ${SCRIPT_DIR}/../../build/releaseApk/${PACKAGE_STAGING}.apk

cd ../debug
PKNAMEDEBUG=`ls | grep ${MODULE}`
cp ${PKNAMEDEBUG} ${SCRIPT_DIR}/../../build/releaseApk/${PACKAGE_MIRROR}.apk

}

functionBuild
functionCopyApk

#apk最终生成包位于工程build/releaseApk ML-*.apk等四个包 其中mirror是debug版 四个文件最终上传服务器
