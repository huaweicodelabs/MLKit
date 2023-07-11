/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

import { LivenessView } from '@hms-core/ml-interactiveliveness'
import LivenessUtils from '../util/LivenessUtils'
import { CircularProgress } from '../util/CircularProgress'
import LivenessResultCode from '../util/LivenessResultCode'
import common from '@ohos.app.ability.common'
import window from '@ohos.window'
import router from '@ohos.router'

@Entry
@Component
struct InteractivelivenessTest {
  @State message: string = '请将脸部正对框内，确保光线充足'
  @State context: common.UIAbilityContext = getContext(this) as common.UIAbilityContext
  @State progressValue: number = 10 // 设置进度条初始值为10

  async setSystemBar() { // api9
    let windowClass = await window.getLastWindow(this.context)
    windowClass.getWindowProperties()
    await windowClass.setWindowSystemBarProperties({
      navigationBarColor: "#FFFFFF",
      statusBarColor: "#FFFFFF",
      navigationBarContentColor: "#000000",
      statusBarContentColor: "#000000"
    })
  }

  aboutToAppear() {
    this.setSystemBar()

    LivenessUtils.guideDetectionIsEnd = false
    LivenessUtils.actionMap.clear()

    this.context.eventHub.on("livenessResult", (mInteractiveLivenessResult) => {
      // 结果码
      let resultCode: number = mInteractiveLivenessResult[0]
      // 动作编码
      let action: number = mInteractiveLivenessResult[1]
      // 图片
      let buffer: ArrayBuffer = mInteractiveLivenessResult[2]
      // 根据结果码(或动作编码)返回不同提示信息
      let msg: string = LivenessUtils.getResultMessage(action, resultCode)
      // 根据结果码控制控件圆弧进度
      this.progressValue = LivenessUtils.circleProgressValue(action, resultCode, this.progressValue)
      // 9999，需跳转success页
      if (resultCode == LivenessResultCode.ALL_ACTION_CORRECT) {
        console.info("InteractivelivenessTest", "action : " + action + " resultCode : " + resultCode + " msg : 人脸识别成功")
        // 取消订阅指定事件（不再接收SDK发送来的消息）
        this.context.eventHub.off('livenessResult')
        // 跳转success页
        setTimeout(()=>{
          LivenessUtils.routerSuccessPage(buffer)
        }, 1000)
      } else {
        console.info("InteractivelivenessTest", "action : " + action + " resultCode : " + resultCode + " msg : " + msg)
        // 除了9999，其他结果码，需跳转error页的
        if (LivenessUtils.isNeedRouterErrorPage(resultCode)) {
          // 取消订阅指定事件（不再接收SDK发送来的消息）
          this.context.eventHub.off('livenessResult')
          // 跳转error页
          setTimeout(()=>{
            LivenessUtils.routerFailPage(resultCode, msg)
          }, 1000)
        } else {
          // 显示提示信息，不跳转错误页
          this.message = msg
        }
      }
    })
  }

  build() {
    // 堆叠布局
    Stack({
      alignContent: Alignment.Bottom
    }) {
      // sdk 扫描框
      LivenessView({
        // left right top bottom
        faceRect: [120, 360, 80, 400],
        timeOut: 10000,
        actions: [2, 5, 4]
      }).margin({ top: 80 })

      Circle()
        .width(400)
        .height(400)
        .fillOpacity(0)
        .strokeWidth(180)
        .stroke(Color.White)
        .margin({ bottom: 245, left: -10 })

      // 提示消息
      Text(this.message).fontSize(20).margin({ bottom: 600 })

      CircularProgress({
        progress: this.progressValue,
        viewWH: 80,
        barWidth: 10,
        circleDia: 70
      }).margin({ bottom: 120 })

      // 标题栏
      Row() {
        // 返回按钮
        Image($r('app.media.ic_back'))
          .onClick(() => {
            router.back()
          }).width(30).height(30).margin({ left: 10 })
        // 标题
        Text('活体检测')
          .width('100%')
          .height(56)
          .fontSize(20)
          .margin({ left: 10 })
      }.backgroundColor('#ffffff').margin({ bottom: 700 })
    }.height("100%")
  }

  aboutToDisappear() {
    // 设置亮度为进入页面之前的亮度
    // brightness.setValue(this.brightnessVal)
    // let windowClass = await window.getLastWindow(this.context)
    // windowClass.setWindowBrightness(0)
    console.info("InteractivelivenessTest", "aboutToDisappear...")
    this.context.eventHub.off("livenessResult")
  }

  /*public async saveImage(context: common.UIAbilityContext, name: String, buf: ArrayBuffer) {
    let path = context.filesDir
    console.info( `save path:${path}`)
    let filePath: string = `${path}/${name}.jpg`
    console.info( `file path:${filePath}`)
    let os = await fs.createStream(filePath, "w+")
    await os.write(buf)
    os.closeSync()
  }*/
}