/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

import image from '@ohos.multimedia.image'
import router from '@ohos.router'
import util from '@ohos.util'
import common from '@ohos.app.ability.common'
import window from '@ohos.window'

@Entry
@Component
@Preview
struct SuccessPage {
  private context: common.UIAbilityContext = getContext(this) as common.UIAbilityContext
  private arrayStr: string = router.getParams()['data']
  @State mPixelMap: image.PixelMap = null

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

    // let base64 = new util.Base64Helper
    // let arrayResult = base64.decodeSync(this.arrayStr);
    // this.getPixelMap(arrayResult)
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
        // 检测成功后的截图
        Image($r('app.media.ic_scan_sucess'))
          .width(150)
          .height(150)
          .align(Alignment.Center)
          .margin({ bottom: 500 })

        /*Circle()
          .width(340)
          .height(340)
          .fillOpacity(0)
          .strokeWidth(105)
          .stroke(Color.White)
          .margin({ bottom: 400, left: 0, top: 25 })*/

        // 检测成功
        Text('验证通过')
          .width('100%')
          .height(56)
          .fontSize(20)
          .fontColor('#64BB5C')
          .margin({ top: 30 })
          .textAlign(TextAlign.Center)
          .margin({ bottom: 400 })

        Flex({ direction: FlexDirection.Row }) {
          Button("重新检测")
            .flexGrow(1)
            .width('70%')
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
            .width('70%')
            .height(40)
            .fontSize(16)
            .backgroundColor('#E50A59F7')
            .margin({ left: 8 })
            .onClick(() => {
              router.back({
                url: 'pages/LivenessIndex',
              })
            })
        }.width('90%')
        .margin({ bottom: 100 })

        // 成功 tag
        // Image($r('app.media.ic_scan_sucess')).width(36).height(36).margin({ bottom: 700 })

      }.height("100%")
    }
  }

  /**
   * 获取 PixelMap
   *
   * @param arrayBuffer 字节数组buffer
   * @returns PixelMap
   */
  public getPixelMap(arrayBuffer: ArrayBuffer): Promise<PixelMap> {
    return new Promise<PixelMap>((resolve, reject) => {
      if (null == this.mPixelMap) {
        this.createPixelMapByArrayBuffer(arrayBuffer).then(pixelMap => {
          this.mPixelMap = pixelMap
          resolve(pixelMap)
        }).catch(e => {
          reject(e)
        })
      } else {
        resolve(this.mPixelMap)
      }
    })
  }

  /**
   * 利用ArrayBuffer，创建PixelMap
   *
   * @param arr 字节数组
   * @returns PixelMap
   */
  private createPixelMapByArrayBuffer(arr: ArrayBuffer): Promise<PixelMap> {
    let imageSource = image.createImageSource(arr)
    return imageSource.createPixelMap()
  }
}