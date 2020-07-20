//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#ifndef MLPRODUCTVISUALSEARCHCONFIGURE_H
#define MLPRODUCTVISUALSEARCHCONFIGURE_H

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface MLRemoteProductVisionSearchAnalyzerSetting : NSObject
@property (nonatomic, assign) NSInteger maxResult;// The value range is [1-100], otherwise an exception will be thrown
@property (nonatomic, copy) NSString *productSetId;// productSetId
/// Get the maximum number of set products
- (NSInteger)getLargestNumOfReturns;
/// Get product collection ID
- (NSString *)getProductSetId;
@end
NS_ASSUME_NONNULL_END

#endif //MLPRODUCTVISUALSEARCHCONFIGURE_H
