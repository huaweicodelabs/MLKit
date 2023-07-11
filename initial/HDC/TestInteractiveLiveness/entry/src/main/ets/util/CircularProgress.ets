@Component
export struct CircularProgress {
  // https://ost.51cto.com/posts/11552
  @Prop progress: number;
  private viewWH: number;
  private barWidth: number;
  private circleDia: number;

  build() {
    Stack({ alignContent: Alignment.Center }) {
      // 背景环
      Shape() {
        Circle().width(this.circleDia).height(this.circleDia)
      }
      // 形状的视口
      .viewPort({
        x: -this.barWidth / 2,
        y: -this.barWidth / 2,
        width: this.viewWH,
        height: this.viewWH
      })
      // 填充颜色
      .fill('none')
      // 边框颜色
      .stroke('#cccccc')
      // 边框透明度
      .strokeOpacity(0.5)
      // 边框的宽度
      .strokeWidth(this.barWidth)
      // 路径端点绘制样式
      .strokeLineCap(LineCapStyle.Round)

      // 进度圈
      Shape() {
        Circle().width(this.circleDia).height(this.circleDia)
      }
      // 形状的视口
      .viewPort({
        x: -this.barWidth / 2,
        y: -this.barWidth / 2,
        width: this.viewWH,
        height: this.viewWH
      })
      // 填充颜色
      .fill('none')
      // 边框颜色
      .stroke('#E50A59F7')
      // 边框的宽度
      .strokeWidth(this.barWidth)
      // 路径端点绘制样式
      .strokeLineCap(LineCapStyle.Round)
      // 边框的间隙
      .strokeDashArray([
        (this.progress / 100) * Math.PI * this.circleDia,
        ((100 - this.progress) / 100) * Math.PI * this.circleDia,
      ])
      // 逆时针旋转90度，改变圆环的起始位置
      .rotate({
        x: 0,
        y: 0,
        z: 1,
        angle: -90
      })

      // 文字
      Row() {
        Text(this.progress.toString() + '%')
          .fontSize(15)
          .fontWeight(FontWeight.Bold)
          .fontColor('#000000')
      }.height(20)
    }
    .width('100%')
  }
}