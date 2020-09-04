//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import "MLDocumentViewController.h"
#import "MLPSListScrollView.h"


#import "MLProductSearchView.h"
@interface MLDocumentViewController ()<navViewDelegate>{
    MLPSListScrollView *list;
    UIImageView *imageView;
    MLDocumentNavView *navView;
}
@property (nonatomic, strong) UIImageView *fullImageview;
@property (nonatomic,strong) MLProductVisionSearch *dataArr;
@end

@implementation MLDocumentViewController

- (void)dealloc{
    NSLog(@"MLDocumentViewController释放了");
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    self.view.backgroundColor = [UIColor colorWithRed:242.0/255.0 green:242.0/255.0 blue:242.0/255.0 alpha:1];
    [self.view addSubview:self.fullImageview];
    navView = [[MLDocumentNavView alloc] initWithFrame:CGRectMake(0, [self isNotchScreen]?20:44, kMLDocumentDeviceWidth, 44)];
    navView.navBackDelegate = self;
    [self.view bringSubviewToFront:navView];
    [self.view addSubview:navView];
    [self requestImage];
}

- (void)navBackClicked{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)rightButtonClick{
    [self dismissViewControllerAnimated:YES completion:nil];
    if ([self.rightDelegete respondsToSelector:@selector(rightButtonClick)]) {
        [self.rightDelegete rightButtonClick];
    }
}

- (void)requestImage{
    if (!list) {
        list  = [[MLPSListScrollView alloc] initWithFrame:CGRectMake(0, 150+(kTTSNavBarAndStatuHeight), kMLDocumentDeviceWidth, kMLDocumentDeviceHeight - (kTTSNavBarAndStatuHeight) -(kTTSSafeAreaHeight) - 150)];
    }
    
    MLRemoteProductVisionSearchAnalyzerSetting *setting = [[MLRemoteProductVisionSearchAnalyzerSetting alloc] init];
    setting.maxResult = 20;
    //setting.productSetId = @"**********";
    [MLRemoteProductVisionSearchAnalyzer setRemoteProductVisionSearchAnalyzerSetting:setting];
    
    NSDate *startTime = [NSDate date];
    [MLRemoteProductVisionSearchAnalyzer asyncAnalyseImage:self.selectedImage addOnSuccessListener:^(MLProductVisionSearch * _Nonnull productModel) {
            NSTimeInterval codeTime = -[startTime timeIntervalSinceNow];
            self->navView.timeShowLabel.text = [NSString stringWithFormat:@"%.4f S",codeTime];
            self->list.selectedImage = self.selectedImage;
            self->list.dataArr = productModel;
            if(productModel.productList&&productModel.productList.count>0){
                MLProductSearchView *searchView = [[MLProductSearchView alloc] initWithFrame:self.fullImageview.frame box:@[@(productModel.border.left_top_x),@(productModel.border.left_top_y),@(productModel.border.right_bottom_x),@(productModel.border.right_bottom_y)]];
                searchView.imageSize = [self calculateTextScale:self.selectedImage];
                [self.view addSubview:searchView];
                searchView.backgroundColor = [UIColor clearColor];
            }
            UIView *maskView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kMLDocumentDeviceWidth, kMLDocumentDeviceHeight)];
            maskView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.3];
            maskView.userInteractionEnabled = YES;
            UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick:)];
            [maskView addGestureRecognizer:tapGesture];
            [self.view addSubview:maskView];
            [self.view addSubview:self->list];
            [self addImageView];
        
    } addOnFailureListener:^(NSInteger errCode, NSString * _Nonnull errMsg) {
        NSTimeInterval codeTime = -[startTime timeIntervalSinceNow];
        self->navView.timeShowLabel.text = [NSString stringWithFormat:@"%.4f S",codeTime];
    }];
    
}

 - (CGSize )calculateTextScale:(UIImage *)selectedImage {

    int width = selectedImage.size.width;
    int height = selectedImage.size.height;
    int minSide = MIN(width, height);

    CGSize targetSize;
    if (minSide <= 640) {
        // Images with short sides less than 640 are not processed
        targetSize = CGSizeMake(width, height);
    } else {
        float compressSizeScale = minSide *1.0f /640;
        double widthImage = width /compressSizeScale;
        double heightImage = height /compressSizeScale;
        targetSize = CGSizeMake(widthImage, heightImage);
    }

    return targetSize;
}

- (void)addImageView{
    
    imageView = [[UIImageView alloc] initWithFrame:CGRectMake(16, CGRectGetMinY(list.frame) - 20, 60, 60)];
    imageView.layer.cornerRadius = 3;
    imageView.clipsToBounds = YES;
    imageView.image = self.selectedImage;
    [self.view addSubview:imageView];
}


-(void)tapClick:(UITapGestureRecognizer *)tap{
    UIView *touchView = tap.view;
    [touchView removeFromSuperview];
    [list removeFromSuperview];
    [imageView removeFromSuperview];
    list = nil;
    imageView = nil;
}

- (UIImageView *)fullImageview{
    if (!_fullImageview) {
        _fullImageview = [[UIImageView alloc] initWithFrame:[UIImage getImageFrameFitScreen:self.selectedImage]];
        _fullImageview.image = self.selectedImage;
    }
    return _fullImageview;
}

-(float)mNavigationbarHeight{
    // Navigation bar height + status bar height
    return [self isNotchScreen]?64:88;
}

- (CGRect)getImageFrame:(CGRect )rect{
    BOOL value = [self isNotchScreen];
    CGFloat nav = value?64:88;
    CGFloat width = rect.size.width/2.0;
    CGFloat height = rect.size.height/2.0;
    BOOL widthBeyond = MIN(width, kMLDocumentDeviceWidth)==kMLDocumentDeviceWidth;
    BOOL heightBeyond = MIN(height, kMLDocumentDeviceHeight- nav)==kMLDocumentDeviceHeight- nav;
    if (widthBeyond||heightBeyond) {
        height = height/width*kMLDocumentDeviceWidth;
        width  = kMLDocumentDeviceWidth;
    }
    CGRect frame = CGRectMake((kMLDocumentDeviceWidth - width)/2, nav, width, height);

    return frame;
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

- (void)viewTouch{
    CGFloat imagevIewAlpha = self.fullImageview.alpha;
    self.fullImageview.alpha = (imagevIewAlpha == 1.0?0.7:1.0);
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
