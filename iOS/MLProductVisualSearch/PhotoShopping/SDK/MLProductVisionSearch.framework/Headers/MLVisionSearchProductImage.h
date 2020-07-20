//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface MLVisionSearchProductImage : NSObject
@property (nonatomic, assign) CGFloat score;// Confidence
@property (nonatomic, copy) NSString *imageId;// Image id
@property (nonatomic, copy) NSString *innerUrl;// innerUrl
@property (nonatomic, copy) NSString *productId;// productId
/// Get the picture Id that returns the picture information of the photo purchase test and returns the Product model
- (NSString *)getImageId;
/// Get the confidence of a picture that returns to the photo shopping check and returns the picture information of the Product model
- (CGFloat )getPossibility;
/// Get the product Id corresponding to the picture of the product model returned by the camera purchase inspection
- (NSString *)getProductId;
/// Get the product url of the Product model returned by the camera purchase test
- (NSString *)getInnerUrl;
@end

NS_ASSUME_NONNULL_END
