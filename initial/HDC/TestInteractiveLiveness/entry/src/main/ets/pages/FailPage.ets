/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

import router from '@ohos.router'
import common from '@ohos.app.ability.common'
import window from '@ohos.window'

@Entry
@Component
struct SuccessPage {
  private context: common.UIAbilityContext = getContext(this) as common.UIAbilityContext
  private message: string = router.getParams()['text']
  private resultCode: number = router.getParams()['code']

  async setSystemBar() { // api9
    let windowClass: window.Window = await window.getLastWindow(this.context)
    await windowClass.setWindowSystemBarProperties({
      navigationBarColor: "#FFFFFF",
      statusBarColor: "#FFFFFF",
      navigationBarContentColor: "#000000",
      statusBarContentColor: "#000000"
    })
  }

  aboutToAppear() {
    this.setSystemBar()
  }

  build() {
    Column() {
      // 标题栏
      Row() {
        // 返回按钮
        Image($r('app.media.ic_back')).onClick(() => {
          router.back()
        }).width(30).height(30).margin({ left: 10 })
        // 标题
        Text('活体检测')
          .width('100%')
          .height(56)
          .fontSize(20)
          .margin({ left: 10 })
      }

      Stack({
        alignContent: Alignment.Bottom
      }) {
        // 检测失败默认的头像
       /* Image($r("app.media.fail_img"))
          .width(236)
          .height(236)
          .margin({ bottom: 430 })*/

        Image($r('app.media.ic_scan_failure'))
          .width(150)
          .height(150)
          .align(Alignment.Center)
          .margin({ bottom: 500 })

        // 检测失败
        Text('验证失败')
          .width('100%')
          .height(56)
          .fontSize(20)
          .fontColor('#E84026')
          .margin({ top: 30 })
          .textAlign(TextAlign.Center)
          .margin({ bottom: 400 })

        // 检测失败原因
        Text(this.message)
          .width('100%')
          .height(56)
          .fontSize(14)
          .fontColor('#99000000')
          .textAlign(TextAlign.Center)
          .margin({ bottom: 350 })

        Flex({ direction: FlexDirection.Row }) {
          Button("重新检测")
            .flexGrow(1)
            .width('100%')
            .height(40)
            .fontSize(16)
            .backgroundColor('#E50A59F7')
            .margin({ right: 8 })
            .onClick(() => {
              router.replaceUrl({
                url: 'pages/InteractivelivenessTest',
              })
            })

          Button("退出")
            .flexGrow(1)
            .width('100%')
            .height(40)
            .fontSize(16)
            .backgroundColor('#E50A59F7')
            .margin({ left: 8})
            .onClick(() => {
              router.back({
                url: 'pages/LivenessIndex',
              })
            })
        }.width('90%')
        .margin({ bottom: 100 })

        // 失败 tag
        //Image($r('app.media.ic_scan_failure')).width(36).height(36).margin({ bottom: 700 })

      }.height("100%")
    }
  }
}