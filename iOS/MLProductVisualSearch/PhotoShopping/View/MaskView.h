//
//  MaskView.h
//  MLPhotoShoppingDemo
//
//  Created by user on 2020/5/19.
//  Copyright © 2020 Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface MaskView : UIView
-(instancetype)initWithFrame:(CGRect)frame;
+(instancetype)makeViewWithMask:(CGRect)frame andView:(UIView*)view;
-(void)block:(void(^)())block;
@end

NS_ASSUME_NONNULL_END
