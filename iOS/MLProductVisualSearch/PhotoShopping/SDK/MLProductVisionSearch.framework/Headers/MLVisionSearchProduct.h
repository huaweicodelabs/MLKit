//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MLVisionSearchProductImage.h"
NS_ASSUME_NONNULL_BEGIN

@interface MLVisionSearchProduct : NSObject
@property (nonatomic, copy) NSString *productId;// Product Id
@property (nonatomic, copy) NSString *customContent;// Custom content
@property (nonatomic, copy) NSString *productUrl;// Product url
@property (nonatomic, strong) NSArray<MLVisionSearchProductImage *> *images;// List of all pictures under the product model
/// Get a list of pictures that return to the photo shopping test and return to the Product model
- (NSArray<MLVisionSearchProductImage*> *)getImageList;
/// Get the Product Id of the Product model returned by the camera purchase inspection
- (NSString *)getProductId;
/// Get the custom content of the Product model returned by the photo purchase inspection
- (NSString *)getCustomContent;
/// Get the product url of the Product model returned by the camera purchase test
- (NSString *)getProductUrl;
@end

NS_ASSUME_NONNULL_END
