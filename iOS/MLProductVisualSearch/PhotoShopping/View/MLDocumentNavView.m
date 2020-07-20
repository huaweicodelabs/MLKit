//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import "MLDocumentNavView.h"

@interface MLDocumentNavView()

@property (nonatomic, strong) UILabel *navtitle;

@property (nonatomic, strong) UILabel *line;

@property (nonatomic, strong )UIImageView *rightImage;

@end

 @implementation MLDocumentNavView

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        [self addSubview:self.titleLabel];
        [self addSubview:self.leftButton];
        [self addSubview:self.rightButton];
        [self addSubview:self.timeShowLabel];
        self.backgroundColor = [UIColor colorWithRed:242.0/255.0 green:242.0/255.0 blue:242.0/255.0 alpha:1];
    }
    return self;
}
- (UIButton *)leftButton{
    if (!_leftButton) {
        _leftButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _leftButton.frame = CGRectMake(16, (44 - 30) /2, 30, 30);
        //[_leftButton setTitle:@"返回" forState:UIControlStateNormal];
        [_leftButton setImage:[UIImage imageNamed:@"black_back"] forState:UIControlStateNormal];
        [_leftButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        [_leftButton addTarget:self  action:@selector(leftButtonClick) forControlEvents:UIControlEventTouchUpInside];
        _leftButton.titleLabel.font = [UIFont systemFontOfSize:14];

    }
    return _leftButton;
}

- (UIButton *)rightButton{
    if (!_rightButton) {
        _rightButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _rightButton.frame = CGRectMake(self.frame.size.width - 36, (44 - 30) /2, 30, 30);
        [_rightButton setImage:[UIImage imageNamed:@"icon_card_add"] forState:UIControlStateNormal];
        [_rightButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        [_rightButton addTarget:self  action:@selector(rightButtonClick) forControlEvents:UIControlEventTouchUpInside];
        _rightButton.titleLabel.font = [UIFont systemFontOfSize:14];

    }
    return _rightButton;
}

- (UILabel *)titleLabel{
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(self.frame.size.width - 36 - 70, 0, 70, 70)];
        _titleLabel.text = @"0S";
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.font = [UIFont systemFontOfSize:12];
        _titleLabel.textColor = [UIColor blackColor];
    }
    return _titleLabel;
}

- (UILabel *)timeShowLabel{
    if (!_timeShowLabel) {
        _timeShowLabel = [[UILabel alloc] initWithFrame:CGRectMake((kMLDocumentDeviceWidth -  150)/2, 0, 150, 44)];
        _timeShowLabel.text = @"拍照购";
        _timeShowLabel.textAlignment = NSTextAlignmentCenter;
        _timeShowLabel.textColor = [UIColor blackColor];
    }
    return _titleLabel;
}

- (void)leftButtonClick{
    if ([self.navBackDelegate respondsToSelector:@selector(navBackClicked)]) {
        [self.navBackDelegate navBackClicked];
    }
}

- (void)rightButtonClick{
    if ([self.navBackDelegate respondsToSelector:@selector(rightButtonClick)]) {
        [self.navBackDelegate rightButtonClick];
    }
}
- (BOOL)isNotchScreen {
    
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
