//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import "MLProductSearchView.h"

@interface MLProductSearchView()
@property (nonatomic,strong) NSArray *box;
@end

@implementation MLProductSearchView
-(instancetype)initWithFrame:(CGRect)frame box:(NSArray *)boxArray{
    self = [super initWithFrame:frame];
   if (self){
       self.box = boxArray;
       UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick:)];
        [self addGestureRecognizer:tapGesture];
   }

   return self;
}


- (void)drawRect:(CGRect)rect {
    
    double widthScale = self.frame.size.width/self.imageSize.width;
    double heightScale = self.frame.size.height/self.imageSize.height;
    
    long smallX = [self.box[0] floatValue]*widthScale;
    long smallY = [self.box[1] floatValue]*heightScale;
    long bigX = [self.box[2] floatValue]*widthScale;
    long bigY = [self.box[3] floatValue]*heightScale;
    CGContextRef contet = UIGraphicsGetCurrentContext();
    CGContextSetLineCap(contet, kCGLineCapSquare);
    CGContextBeginPath(contet);
    
    CGContextMoveToPoint(contet, smallX , smallY);
    CGContextAddLineToPoint(contet, smallX, smallY);
    CGContextAddLineToPoint(contet,bigX,smallY);
    CGContextAddLineToPoint(contet, bigX, bigY);
    CGContextAddLineToPoint(contet, smallX,bigY);
    
    CGContextSetRGBStrokeColor(contet, 1, 0, 0, 1);
    CGContextClosePath(contet);
    CGContextStrokePath(contet);
    
    UIBezierPath *tempPath = [UIBezierPath bezierPathWithRoundedRect:CGRectMake(smallX, smallY, bigX-smallX, bigY-smallY) byRoundingCorners:(UIRectCornerTopLeft |UIRectCornerTopRight |UIRectCornerBottomRight|UIRectCornerBottomLeft) cornerRadii:CGSizeMake(0, 0)];
    UIView *View = [[UIView alloc] initWithFrame:self.bounds];
    View.backgroundColor = [UIColor blackColor];
    View.alpha = 0.8;
    View.layer.mask = [self addTransparencyViewWith:tempPath];
    [self addSubview:View];
}

- (CAShapeLayer *)addTransparencyViewWith:(UIBezierPath *)tempPath{
    UIBezierPath *path = [UIBezierPath bezierPathWithRect:[UIScreen mainScreen].bounds];
    [path appendPath:tempPath];
    path.usesEvenOddFillRule = YES;
 
    CAShapeLayer *shapeLayer = [CAShapeLayer layer];
    shapeLayer.path = path.CGPath;
    shapeLayer.fillColor= [UIColor blackColor].CGColor;
    shapeLayer.fillRule=kCAFillRuleEvenOdd;
    return shapeLayer;
}

-(void)tapClick:(UITapGestureRecognizer *)tap{
    [self removeFromSuperview];
}

@end
