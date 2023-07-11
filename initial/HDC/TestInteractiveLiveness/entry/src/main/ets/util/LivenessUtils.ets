/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2018-2022. All rights reserved.
 */
import LivenessResultCode from './LivenessResultCode'
import router from '@ohos.router'
import util from '@ohos.util'
import HashMap from '@ohos.util.HashMap'

/**
 * 活体检测工具类
 *
 * @since 2023-06-06
 */
export default class LivenessUtils {
  private static TAG: string = "InteractiveLivenessResultCodeUtils"
  static guideDetectionIsEnd: boolean = false
  static actionMap = new HashMap<Number, Number>()

  /**
   * 提示框
   * @param msg 内容
   */
  public static dialog(msg: string) {
    AlertDialog.show({
      title: "Tips",
      message: msg,
      primaryButton: {
        value: "OK",
        action: undefined
      }
    })
  }

  /**
   * 根据不同的动作编码，返回不同的动作要求
   * @param action 动作编码
   * @returns
   */
  public static getActionSt(action: number): string {
    switch (action) {
      case 1:
        return "请缓慢点头"

      case 2:
        return "请缓慢张嘴"

      case 3:
        return "请缓慢眨眼"

      case 4:
        return "请缓慢向左摇头"

      case 5:
        return "请缓慢向右摇头"

      case 6:
        return "请注视并保持稳定"

      default:
        return "请将脸部正对框内，确保光线充足"
    }
  }

  /**
   * 根据结果码判断是否跳转页面
   *
   * @param buffer 视频流截图
   */
  public static routerSuccessPage(buffer: ArrayBuffer) {
    console.info("InteractivelivenessTest", "routerSuccessPage...")
    // 9999 : 代表检测通过
    let base64 = new util.Base64Helper
    let arrayStr = base64.encode(new Uint8Array(buffer))

    router.replaceUrl({
      url: 'pages/SuccessPage',
      params: {
        data: arrayStr
      }
    })
  }

  /**
   * 根据结果码判断是否跳转页面
   *
   * @param resultCode 结果码
   */
  public static routerFailPage(resultCode: number, msg: string) {
    console.info("InteractivelivenessTest", "routerFailPage...")
    // 除了 9999 （检测通过）以外，需要跳转错误页的 code
    router.replaceUrl({
      url: 'pages/FailPage',
      params: {
        text: msg,
        code: resultCode
      }
    })
  }

  /**
   * 需要跳转错误页的code
   *
   * @param resultCode 结果码
   */
  public static isNeedRouterErrorPage(resultCode: number): boolean {
    if (
    // ----------------- onCompleted -----------------
      resultCode == LivenessResultCode.RESULT_TIME_OUT // 5030 : 代表超时
      || resultCode == LivenessResultCode.ERROR_RESULT_BEFORE // 5020 : 多次出现错误，上次的还没结束
      || resultCode == LivenessResultCode.SPOOFING // 2002 : 代表非活体
      || resultCode == LivenessResultCode.ACTION_MUTUALLY_EXCLUSIVE_ERROR // 1009 : 动作互斥错误
      || resultCode == LivenessResultCode.ACTION_DETECTION_TIMEOUT // 1010 : 算法结果返回超时
      || resultCode == LivenessResultCode.CONTINUITY_DETECTION_ERROR // 1014 : 代表连续性检测失败
      // ----------------- onError -----------------
      || resultCode == LivenessResultCode.CAMERA_NO_PERMISSION // 11401 : 相机权限未获取
      || resultCode == LivenessResultCode.CAMERA_START_FAILED // 11402 : 相机启动失败
      || resultCode == LivenessResultCode.USER_CANCEL // 11403 : 用户取消
      || resultCode == LivenessResultCode.DETECT_FACE_TIME_OUT // 11404 : 质量检测超时
      || resultCode == LivenessResultCode.USER_DEFINED_ACTIONS_INVALID // 11405 : 动作不合规
      // ----------------- 计费相关结果码 -----------------
      || resultCode == LivenessResultCode.OFFLINE_USE_COUNT_OVER_THRESHOLD // 6001 : 离线使用次数超过阈值
      || resultCode == LivenessResultCode.OFFLINE_USE_TIME_OVER_THRESHOLD // 6002 : 离线使用时间超过阈值
      || resultCode == LivenessResultCode.NO_ORDER_PAY // 5001 : 没有开通付费档错误码，且免费额度用完
      || resultCode == LivenessResultCode.OUT_OF_CREDIT // 5002 : 账户欠费
      || resultCode == LivenessResultCode.BLACK_LIST // 5003 : 黑名单
      || resultCode == LivenessResultCode.LOCAL_AND_CLOUD_BILL_INFO_IS_NULL // 7001 : 既没有本地计量状态信息，也没有云测状态信息
    ) {
      return true
    }
    return false
  }

  /**
   * 根据结果码(或动作编码)返回不同提示信息
   *
   * @param action 动作编码
   * @param resultCode 结果码
   * @returns 提示信息
   */
  public static getResultMessage(action: number, resultCode: number): string {
    switch (resultCode) {
    // ----------------- onCompleted -----------------
      case LivenessResultCode.IN_PROGRESS:
        if (!this.actionMap.hasKey(action)) {
          // map中，放置 action，2000
          this.actionMap.set(action, resultCode)
        }
        if (this.guideDetectionIsEnd) {
          return this.getActionSt(action)
        } else {
          console.info("InteractivelivenessTest", "getResultMessage msg : 正在进行检测")
          return "请将脸部正对框内，确保光线充足"
        }

      case LivenessResultCode.ALL_ACTION_CORRECT: // 遇到则需要退出检测
        this.actionMap.clear()
        return "人脸识别成功"

      case LivenessResultCode.RESULT_TIME_OUT: // 遇到则需要退出检测
        return "检测超时"

      case LivenessResultCode.ERROR_RESULT_BEFORE: // 遇到则需要退出检测
        return "多次出错"

      case LivenessResultCode.SPOOFING: // 遇到则需要退出检测
        //return "失败可能原因\n\n 1.非活体\n 2.光线太亮或太暗\n 3.逆光场景\n 4.动作幅度过大\n\n 请换个场景试一下"
        return "非活体"

      case LivenessResultCode.ACTION_MUTUALLY_EXCLUSIVE_ERROR: // 遇到则需要退出检测
        return "动作错误"

      case LivenessResultCode.ACTION_DETECTION_TIMEOUT: // 遇到则需要退出检测
        return "算法超时"

      case LivenessResultCode.CONTINUITY_DETECTION_ERROR: // 遇到则需要退出检测
        // return "失败可能原因\n\n 1.非活体\n 2.光线太亮或太暗\n 3.逆光场景\n 4.动作幅度过大\n\n 请换个场景试一下"
        return "连续性检测失败"

      case LivenessResultCode.LIVE_AND_ACTION_CORRECT:
        this.guideDetectionIsEnd = true
        if (this.actionMap.hasKey(action)) {
          // map中，action，2000 改成 action，2003
          this.actionMap.set(action, resultCode)
        }
        return "动作正确"

      case LivenessResultCode.GUIDE_DETECTION_SUCCESS:
        console.info("InteractivelivenessTest", "getResultMessage msg : 引导检测成功")
        this.guideDetectionIsEnd = true
        return "请将脸部正对框内，确保光线充足"

      case LivenessResultCode.FACING_SCREEN:
        return "面部未回正"

      case LivenessResultCode.NO_FACE:
        return "无人脸，请对准人脸框"

      case LivenessResultCode.MULTI_FACES:
        return "多人进入检测环境"

      case LivenessResultCode.FACE_ASPECT:
        return "请调整手机方向"

      case LivenessResultCode.PART_FACE:
        return "人脸偏移，请对准人脸框"

      case LivenessResultCode.IMAGE_QUALITY_POOR:
        return "请正对屏幕，双眼睁开，双唇闭合"

      case LivenessResultCode.BIG_FACE:
        return "请将手机远离一点"

      case LivenessResultCode.SMALL_FACE:
        return "请将手机靠近一点"

      case LivenessResultCode.SUNGLASSES_FACE:
        return "请不要遮挡眼睛"

      case LivenessResultCode.MASK_FACE:
        return "请不要遮挡面部"

      case LivenessResultCode.DARK:
        return "请到光线明亮清晰处检测"

      case LivenessResultCode.BACK_LIGHTING:
        return "请到光线明亮清晰处检测"

      case LivenessResultCode.BRIGHT:
        return "请到光线明亮清晰处检测"

      case LivenessResultCode.BLUR:
        return "请到光线明亮清晰处检测"

    // ----------------- onError -----------------
      case LivenessResultCode.CAMERA_NO_PERMISSION:
        return "相机权限未获取"

      case LivenessResultCode.CAMERA_START_FAILED:
        return "相机启动失败"

      case LivenessResultCode.USER_CANCEL:
        return "用户取消"

      case LivenessResultCode.DETECT_FACE_TIME_OUT:
        return "质量检测超时"

      case LivenessResultCode.USER_DEFINED_ACTIONS_INVALID:
        return "自定义动作不合规"

    // ----------------- 计费相关结果码 -----------------
      case LivenessResultCode.OFFLINE_USE_COUNT_OVER_THRESHOLD:
        return "离线使用次数超过阈值"

      case LivenessResultCode.OFFLINE_USE_TIME_OVER_THRESHOLD:
        return "离线使用时间超过阈值"

      case LivenessResultCode.NO_ORDER_PAY:
        return "没有开通付费档错误码，且免费额度用完"

      case LivenessResultCode.OUT_OF_CREDIT:
        return "账户欠费"

      case LivenessResultCode.BLACK_LIST:
        return "黑名单"

      case LivenessResultCode.LOCAL_AND_CLOUD_BILL_INFO_IS_NULL:
        return "既没有本地计量状态信息，也没有云测状态信息"

      default:
        return ""
    }
  }

  /**
   * 根据结果码控制控件圆弧进度
   *
   * @param resultCode 结果码
   * @param mCircleProgressValue 当前进度条的进度值
   * @return 圆弧进度值
   */
  public static circleProgressValue(action: number, resultCode: number, mCircleProgressValue: number): number {
    let tempValue: number = mCircleProgressValue
    if (resultCode == LivenessResultCode.GUIDE_DETECTION_SUCCESS) {
      tempValue = mCircleProgressValue + 30
    } else if (resultCode == LivenessResultCode.LIVE_AND_ACTION_CORRECT) {
      // 如果actionMap中包含key为action的值，且值为2003，则进度条+20
      if (this.actionMap.hasKey(action) && this.actionMap.get(action) == LivenessResultCode.LIVE_AND_ACTION_CORRECT) {
        tempValue = mCircleProgressValue + 20
        this.actionMap.remove(action)
      }
    } else if (resultCode == LivenessResultCode.ALL_ACTION_CORRECT) {
      tempValue = mCircleProgressValue + 20
    }
    if (tempValue > 100) {
      tempValue = 100
    }
    console.info("InteractivelivenessTest", "circleProgress value : " + tempValue)
    return tempValue
  }
}