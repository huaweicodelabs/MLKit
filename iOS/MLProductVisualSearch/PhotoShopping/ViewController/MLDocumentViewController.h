//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol rightButtonDelegete<NSObject>

/**
* Right button
*/
- (void)rightButtonClick;

@end
NS_ASSUME_NONNULL_BEGIN

@interface MLDocumentViewController : UIViewController

@property (nonatomic, strong) UIImage *selectedImage;

@property (nonatomic, weak) id<rightButtonDelegete>rightDelegete;


@end

NS_ASSUME_NONNULL_END
