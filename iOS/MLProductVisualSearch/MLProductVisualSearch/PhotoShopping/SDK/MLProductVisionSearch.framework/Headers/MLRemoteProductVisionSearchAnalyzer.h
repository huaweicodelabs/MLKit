//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#ifndef MLPRODUCTVISUALSEARCH_H
#define MLPRODUCTVISUALSEARCH_H

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "MLProductVisionSearch.h"
#import "MLRemoteProductVisionSearchAnalyzerSetting.h"

NS_ASSUME_NONNULL_BEGIN

@interface MLRemoteProductVisionSearchAnalyzer : NSObject
/// Photo purchase request interface
/// @param image incoming picture parameters
/// @param successHandler The callback for successful request, the productModel is returned, and the calling interface knows that it needs to be displayed according to the data.
/// @param failureHandler Failure callback interface
+ (void)asyncAnalyseImage:(UIImage *)image
              addOnSuccessListener:(void(^)(MLProductVisionSearch *productModel))successHandler
              addOnFailureListener:(void(^)(NSInteger errCode,NSString *errMsg))failureHandler;

/// Set interface
/// @param setting settingMLRemoteProductVisionSearchAnalyzerSetting example
+ (void)setRemoteProductVisionSearchAnalyzerSetting:(MLRemoteProductVisionSearchAnalyzerSetting *)setting;
@end

NS_ASSUME_NONNULL_END

#endif // MLPRODUCTVISUALSEARCH_H
