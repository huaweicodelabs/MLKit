//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import "MLPSCollectionViewCell.h"

@implementation MLPSCollectionViewCell

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        [self addSubview:self.topImageView];
        [self addSubview:self.bottomLabel];
    }
    return self;
}

- (UIImageView *)topImageView{
    if (!_topImageView) {
        _topImageView = [[UIImageView alloc] initWithFrame:CGRectMake(16, 10, self.frame.size.width-32, self.frame.size.height-25)];
        //_topImageView.backgroundColor = [UIColor orangeColor];
        _topImageView.layer.cornerRadius = 3;
        _topImageView.clipsToBounds = YES;
        [_topImageView setContentScaleFactor:[[UIScreen mainScreen] scale]];
        _topImageView.contentMode =  UIViewContentModeScaleAspectFill;

    }
    return _topImageView;
}

- (UILabel *)bottomLabel{
    if (!_bottomLabel) {
        _bottomLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(_topImageView.frame), self.frame.size.width, 15)];
        _bottomLabel.textAlignment = NSTextAlignmentCenter;
        _bottomLabel.font = [UIFont systemFontOfSize:12];
        _bottomLabel.textColor = [UIColor blackColor];
    }
    return _bottomLabel;
}


@end
