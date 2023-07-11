/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

/**
 * 活体检测结果码
 *
 * @since 2023-06-06
 */
export default class LivenessResultCode {
    // 代表检测通过
    public static ALL_ACTION_CORRECT: number = 9999

    // 前一次已经检查出异常结果，如SPOOFING，ACTION_MUTUALLY_EXCLUSIVE_ERROR，RESULT_TIME_OUT
    // 算法监测为多线程并行监测，可能存在监测未结束，前后两次超时的结果返回，前面页面出现一闪情况，但强行release算法会可能导致crash
    public static ERROR_RESULT_BEFORE: number = 5020

    // 代表超时
    public static RESULT_TIME_OUT: number = 5030

    // 检测正在进行
    public static IN_PROGRESS: number = 2000

    // 活体
    public static LIVE: number = 2001

    // 代表非活体  遇到则需要退出检测
    public static SPOOFING: number = 2002

    // 代表活体且动作正确
    public static LIVE_AND_ACTION_CORRECT: number = 2003

    // 代表引导检测成功
    public static GUIDE_DETECTION_SUCCESS: number = 2004

    // 面部未回正
    public static FACING_SCREEN: number = 2005

    // 人脸框未设置
    public static INIT_FACE_RECTANGLE_ERROR: number = 2007

    // 图片质量不佳
    public static IMAGE_QUALITY_POOR: number = 2008

    // 代表人脸朝向不符
    public static FACE_ASPECT: number = 1001

    // 代表无人脸
    public static NO_FACE: number = 1002

    // 代表多人脸
    public static MULTI_FACES: number = 1003

    // 代表人脸偏移中心
    public static PART_FACE: number = 1004

    // 代表人脸过大
    public static BIG_FACE: number = 1005

    // 代表人脸过小
    public static SMALL_FACE: number = 1006

    // 代表墨镜遮挡
    public static SUNGLASSES_FACE: number = 1007

    // 代表口罩遮挡
    public static MASK_FACE: number = 1008

    // 代表动作互斥错误 遇到则需要退出检测
    public static ACTION_MUTUALLY_EXCLUSIVE_ERROR: number = 1009

    // 算法结果返回超时状态 遇到则需要退出检测
    public static ACTION_DETECTION_TIMEOUT: number = 1010

    // 图片获取错误
    public static GET_IMAGE_ERROR: number = 1011

    // 口罩遮挡
    public static MASK_3D_DETECTION_ERROR: number = 1012

    // 皮肤颜色检测失败
    public static SKIN_COLOUR_DETECTION_ERROR: number = 1013

    // 代表连续性检测失败
    public static CONTINUITY_DETECTION_ERROR: number = 1014

    // 异常
    public static UNCONVENTIONAL_OPERATION_ERROR: number = 1015

    // 初始化失败
    public static LIVENESS_INIT_ERROR: number = 1016

    // 授权失败
    public static LICENSE_EXPIRED_ERROR: number = 1017

    // 代表光线暗
    public static DARK: number = 1018

    // 代表图片模糊
    public static BLUR: number = 1019

    // 代表逆光
    public static BACK_LIGHTING: number = 1020

    // 代表光线亮
    public static BRIGHT: number = 1021

    // 代表算法未返回结果
    public static OTHER: number = 0

    // ----------------- onFailure -----------------

    // 相机权限未获取
    public static CAMERA_NO_PERMISSION: number = 11401

    // 相机启动失败
    public static CAMERA_START_FAILED: number = 11402

    // 用户取消
    public static USER_CANCEL: number = 11403

    // 质量检测模块检测超时
    public static DETECT_FACE_TIME_OUT: number = 11404

    // 用户自定义动作不合规
    public static USER_DEFINED_ACTIONS_INVALID: number = 11405

    // ----------------- 计费相关结果码 -----------------
    // 离线状态使用超过阈值
    public static OFFLINE_USE_COUNT_OVER_THRESHOLD: number = 6001

    // 离线状态使用超过时间阈值
    public static OFFLINE_USE_TIME_OVER_THRESHOLD: number = 6002

    // 没有开通付费档错误码，且免费额度用完
    public static NO_ORDER_PAY: number = 5001

    // 账户欠费
    public static OUT_OF_CREDIT: number = 5002

    // 黑名单
    public static BLACK_LIST: number = 5003

    // 既没有本地计量状态信息，也没有云测状态信息
    public static LOCAL_AND_CLOUD_BILL_INFO_IS_NULL: number = 7001;

    public constructor() {
    }
}