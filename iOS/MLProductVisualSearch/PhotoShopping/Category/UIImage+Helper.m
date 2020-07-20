//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import "UIImage+Helper.h"

@implementation UIImage (Helper)

+ (CGRect)getImageFrameFitScreen:(UIImage *)image{
    BOOL value = [self isNotchScreen];
    CGFloat nav = value?64:88;
    CGFloat width = image.size.width/2.0;
    CGFloat height = image.size.height/2.0;
    BOOL widthBeyond = MIN(width, kMLDocumentDeviceWidth)==kMLDocumentDeviceWidth;
    BOOL heightBeyond = MIN(height, kMLDocumentDeviceHeight- nav)==kMLDocumentDeviceHeight- nav;
    if (widthBeyond||heightBeyond) {
        height = height/width*kMLDocumentDeviceWidth;
        width  = kMLDocumentDeviceWidth;
    }
    CGRect frame = CGRectMake((kMLDocumentDeviceWidth - width)/2, nav, width, height);

    return frame;
}


+ (BOOL)isNotchScreen {
    
    if ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPad) {
        return NO;
    }
    
    CGSize size = [UIScreen mainScreen].bounds.size;
    NSInteger notchValue = size.width / size.height * 100;
    
    if (216 == notchValue || 46 == notchValue) {
        return NO;
    }
    
    return YES;
}

@end
