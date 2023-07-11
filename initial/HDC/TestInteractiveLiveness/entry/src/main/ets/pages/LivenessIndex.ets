/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

import router from '@ohos.router'
import abilityAccessCtrl, { Permissions } from '@ohos.abilityAccessCtrl'
import common from '@ohos.app.ability.common'
import window from '@ohos.window'

@Entry
@Component
struct LivenessIndex {
  private context: common.UIAbilityContext = getContext(this) as common.UIAbilityContext
  private array: Array<Permissions> = ["ohos.permission.CAMERA"]

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
    Stack({
      alignContent: Alignment.Bottom
    }) {
      Text('交互式活体检测').fontSize(20)
        .margin({ bottom: 600 })

      Image($r('app.media.liveness_detection'))
        .objectFit(ImageFit.Auto)
        .margin({ bottom: 100 })

      Button("开始检测")
        .width('90%')
        .height(40)
        .backgroundColor('#E50A59F7')
        .fontSize(16)
        .margin({
          bottom: 50
        })
        .onClick(() => {
          this.checkPermissionAndInitCamera()
        })
    }
  }

  private checkPermissionAndInitCamera() {
    let res = abilityAccessCtrl.createAtManager().verifyAccessTokenSync(100, "ohos.permission.CAMERA")
    if (res === abilityAccessCtrl.GrantStatus.PERMISSION_GRANTED) {
      router.pushUrl({
        url: 'pages/InteractivelivenessTest',
      })
    } else {
      abilityAccessCtrl.createAtManager().requestPermissionsFromUser(this.context, this.array, (err, res) => {
        for (var i = 0; i < res.permissions.length; i++) {
          if (res.permissions[i] === "ohos.permission.CAMERA" && res.authResults[i] === 0) {
            router.pushUrl({
              url: 'pages/InteractivelivenessTest',
            })
            break
          }
        }
      })
    }
  }
}