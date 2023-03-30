## MLKit_ML_SiDemo


## Table of Contents

 * [Introduction](#introduction)
 * [Installation](#installation)
 * [Configuration ](#configuration )
 * [Supported Environments](#supported-environments)
 * [Sample Code](# Sample Code)
 * [License](#license)
 
 
## Introduction
    HUAWEI ML Kit提供的“同声传译”服务将实时输入的长语音（时长不超过5小时）实时翻译为不同语种的文本以及语音，并实时输出原语音文本、翻译后的文本以及翻译文本的语音播报。支持中英文互译，支持多种音色语音播报。并将识别结果返回给开发者的App。

## Installation
	在使用前请检查本机环境是否安装JDk、Android-SDK、AndroidStudio等
	建议设备：EMUI 5.0及以上的华为手机或Android 4.4及以上的非华为手机（部分能力仅支持华为手机）
	如果同时使用多个HMS Core的服务，则需要使用各个Kit对应的最大值。

    
## Supported Environments
	JDK 1.8.211及以上
	Gradle 4.6及以上（推荐）
	
## Configuration
    如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)。
    `agconnect-services.json`文件请从[华为开发者社区](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050990353)网站申请获取。
    将agconnect-services.json拷贝至工程根目录。

	以下描述了agconnect-services.json中的参数。

	app_id：应用程序ID，从AGC应用程序信息中获取。

	api_key：应用程序的访问密钥，从AGC应用程序信息中获取。



## Sample Code
    MLKit_ML_SiDemo示例代码，集成了Huawei MLkit同传sdk功能，对实时语音进行识别、翻译、播报等
    下面描述一下具体的API方法。
	从代码仓平台下载siDemo示例代码，

    1) startRecognizing（config）:    //开启语音识别.

    2) onStartListening() :    //开启语音识别.

	3) onVoiceDataReceived：    //原始音频流.

	4) onRecognizingResults    //实时识别结果.

	5) onResults    //识别结果.

	6) onError():    //错误回调.

	7)destroy():    //释放资源.



##  License
    MLKit_ML_SiDemo is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).



