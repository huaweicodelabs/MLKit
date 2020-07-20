//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN


@protocol navViewDelegate<NSObject>
@optional

/// Navigate back
- (void)navBackClicked;

///  Right button
- (void)rightButtonClick;

@end

@interface MLDocumentNavView : UIView

@property (nonatomic, strong) UIButton *leftButton;

@property (nonatomic, strong) UIButton *rightButton;

@property (nonatomic, strong) UILabel *titleLabel;

@property (nonatomic, strong) UILabel *timeShowLabel;

@property (nonatomic, weak) id<navViewDelegate>navBackDelegate;

@end

NS_ASSUME_NONNULL_END
