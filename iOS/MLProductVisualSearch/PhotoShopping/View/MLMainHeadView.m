//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import "MLMainHeadView.h"

@implementation MLMainHeadView

-(instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        [self createElement];
    }
    return self;
}

-(void)createElement{
    self.backgroundColor =  [UIColor colorWithRed:0/255.0 green:56.0/255.0 blue:83.0/255.0 alpha:1];
    
    UIButton *settingButton = [[UIButton alloc] initWithFrame:CGRectMake(kMLDocumentDeviceWidth -40, 20+10, 20, 20)];
    [settingButton setImage:[UIImage imageNamed:@"icon_setting"] forState:UIControlStateNormal];
    [self addSubview:settingButton];
    
    
    UILabel *mlLabel = [[UILabel alloc] initWithFrame:CGRectMake(80, self.bounds.size.height/2-50, 100, 100)];
    mlLabel.text = [NSString stringWithFormat:@"%@\n LEARNING",@"MACHINE"];
    mlLabel.numberOfLines = 0;
    mlLabel.textColor = [UIColor whiteColor];
    [self addSubview:mlLabel];
    
    UIImageView *bgImageView = [[UIImageView alloc] initWithFrame:CGRectMake(self.bounds.size.width/2 +30, self.bounds.size.height/2-50, 100, 100)];
    bgImageView.image = [UIImage imageNamed:@"icon_logo"];
    [self addSubview:bgImageView];
}

-(void)drawRect:(CGRect)rect{
        UIColor *color = [UIColor colorWithRed:242.0/255.0 green:242.0/255.0 blue:242.0/255.0 alpha:1];
        [color set]; // Set line color
        
        UIBezierPath *bezierPath = [UIBezierPath bezierPath];
        [bezierPath addArcWithCenter:CGPointMake(kMLDocumentDeviceWidth/2,kMLDocumentDeviceHeight/2) radius: kMLDocumentDeviceHeight/5 startAngle:M_PI_2 endAngle:0 clockwise:YES];
        [bezierPath fill];
        [bezierPath stroke];
   
}
@end
