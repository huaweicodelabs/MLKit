//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#ifndef MLPRODUCTMODEL_H
#define MLPRODUCTMODEL_H
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "MLVisionSearchProduct.h"
NS_ASSUME_NONNULL_BEGIN

typedef struct {
    CGFloat left_top_x;
    CGFloat left_top_y;
    CGFloat right_bottom_x;
    CGFloat right_bottom_y;
}MLBoxRect;

@interface MLProductVisionSearch : NSObject
@property (nonatomic, copy) NSString *type;// Class type
@property (nonatomic, assign)  MLBoxRect border;// Box rect.The starting point is the upper left corner
@property (nonatomic, strong) NSArray<MLVisionSearchProduct *> *productList;//Product list
/// Get the category of the result returned by the photo purchase test
- (NSString *)getType;
/// Get the x and y values of the two coordinate points of the border returned by the photo purchase detection.The coordinate value here is based on the width and height of the incoming picture as the coordinate system. Please note that if the minimum value of the width and height of the original picture is less than 640, the width and height of the coordinate system of the picture based on the same, if the minimum value is greater than 640, the small side of the picture based on the coordinates becomes 640, The large sides will be scaled in proportion. (For example: if the width of a picture is 1280 and the height is 1400, then the width of the picture based on the returned coordinate value is compressed to 640, and the height is proportional to 700)
- (MLBoxRect )getBorder;
/// List of all products returned by the photo purchase test
- (NSArray<MLVisionSearchProduct *> *)getProductList;
/// A list of all picture information returned by the camera purchase test
- (NSArray<MLVisionSearchProductImage *> *)getImageList;
@end
NS_ASSUME_NONNULL_END

#endif //MLPRODUCTMODEL_H
