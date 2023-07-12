// @ts-nocheck
// 导入文本识别所需要的API
import textRecognition from '@hms.ai.textRecognition';
import image from '@ohos.multimedia.image';
import hilog from '@ohos.hilog';

@Entry
@Component
struct Index {
	@State message: string = '通用文本识别'

	build() {
		Row() {
			Column() {
				Text(this.message)
					.fontWeight(FontWeight.Bold)
					.height('70%')
					.alignSelf(ItemAlign.Center)
					.minFontSize(10)
					.maxFontSize(30)
				Button('Test')
					.fontSize(30)
					.type(ButtonType.Capsule)
					.height(50)
					.width('80%')
					.margin(10)
					.align(Alignment.Center)
					.onClick(() => {
						this.textRecognitionTest();
					})
			}.width('100%')
		}
	}
	/**
	 * 开始通用文字识别
	 */
	async textRecognitionTest() {
		// 将图片转换为PixelMap
		let resource = await getContext(this).resourceManager.getMediaContent($r("app.media.image"));
		let imageResource = image.createImageSource(resource.buffer);
		let pixelMapInstance = await imageResource.createPixelMap();

		// 调用通用文本识别接口
		let visionInfo = {
			pixelMap: pixelMapInstance,
		};
		let recognitionResult = await textRecognition.recognizeText(visionInfo);

		// 将结果更新到Text中显示
		let recognitionString = JSON.stringify(recognitionResult);
		hilog.info(0x0000, 'testTag', "textRecognition data is " + recognitionString);
		this.message = JSON.stringify(recognitionString);
	}
}