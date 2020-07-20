//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import "ViewController.h"
#import "MLDocumentViewController.h"
#import "MLMainHeadView.h"
@interface ViewController ()<UIImagePickerControllerDelegate,UINavigationControllerDelegate,rightButtonDelegete>
@property (nonatomic,strong) UIImagePickerController *imagePickerController;

@property (nonatomic,strong)  MLMainHeadView *headViww;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor colorWithRed:242.0/255.0 green:242.0/255.0 blue:242.0/255.0 alpha:1];
    [self.view addSubview:self.headViww];
    [self setButtons];
    [self creatVersionLabel];
}

-(MLMainHeadView *)headViww{
    if (!_headViww) {
        _headViww = [[MLMainHeadView alloc] initWithFrame:CGRectMake(0, 0, kMLDocumentDeviceWidth, kMLDocumentDeviceHeight/3)];
    }
    return _headViww;
}

- (void)setButtons{
   
    CGFloat spactHeight = kMLDocumentDeviceHeight - 100 - kMLDocumentDeviceHeight/3 - (kTTSSafeAreaHeight);
    CGFloat orignY = (spactHeight - 120 - 30)/2+kMLDocumentDeviceHeight/3;
    NSArray *titleArray = @[NSLocalizedString(@"相机", nil),NSLocalizedString(@"相册", nil)];
    CGFloat space = 30;
    CGFloat height = 60;
    
    for (int i =0; i<titleArray.count; i++) {
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        [button setTitle:titleArray[i] forState:UIControlStateNormal];
        button.titleLabel.font = [UIFont systemFontOfSize:14];
        [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        button.tag = i;
        button.titleLabel.font = [UIFont systemFontOfSize:12];
        [button addTarget:self action:@selector(clickItem:) forControlEvents:UIControlEventTouchUpInside];
        button.frame = CGRectMake(20, orignY + (space + height)*i, kMLDocumentDeviceWidth - 40, height);
        button.backgroundColor = [UIColor colorWithRed:184.0/255.0 green:196.0/255.0 blue:86.0/255.0 alpha:1];
        button.layer.cornerRadius = 5;
        button.clipsToBounds = YES;
        [self.view addSubview:button];
    }
}
- (void)clickItem:(UIButton *)button{
    switch (button.tag) {
        case 0:
        {
        self.imagePickerController.sourceType=UIImagePickerControllerSourceTypeCamera;
            self.imagePickerController.cameraDevice=UIImagePickerControllerCameraDeviceRear;
            [self presentViewController:self.imagePickerController animated:YES completion:nil];
        }
            break;
        case 1:
        {
        self.imagePickerController.sourceType=UIImagePickerControllerSourceTypeSavedPhotosAlbum;
            [self presentViewController:self.imagePickerController animated:YES completion:nil];
        }
            break;
            
        default:
            break;
    }
}

- (void)creatVersionLabel{
    UILabel *version = [[UILabel alloc] initWithFrame:CGRectMake(80, kMLDocumentDeviceHeight -(kTTSSafeAreaHeight)-100 , kMLDocumentDeviceWidth - 160, 100)];
    version.font = [UIFont systemFontOfSize:15];
    version.textColor = [UIColor blackColor];
    version.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:version];
    // Get version number
    NSString *ver =  [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    // Get Build version number
    NSString *build =  [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"];
    version.text = [NSString stringWithFormat:@"%@:%@/Build:%@",NSLocalizedString(@"版本", nil),ver,build];
    version.numberOfLines = 0;
    version.backgroundColor = [UIColor clearColor];
}

#pragma mark After selecting the picture, request and draw
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<UIImagePickerControllerInfoKey,id> *)info{
    UIImage *image = [info objectForKey:UIImagePickerControllerOriginalImage];// Original image
    [self dismissViewControllerAnimated:YES completion:nil];

    MLDocumentViewController *dcViewcontroller = [[MLDocumentViewController alloc] init];
    dcViewcontroller.modalPresentationStyle = UIModalPresentationFullScreen;
    dcViewcontroller.selectedImage = image;
    dcViewcontroller.rightDelegete = self;
    [self presentViewController:dcViewcontroller animated:YES completion:nil];;
}

- (NSArray *)getPNGPath
{
    NSMutableArray *tempArray = [[NSMutableArray alloc] initWithCapacity:0];
    // Get the path of the file
    NSString *path = [[NSBundle mainBundle] pathForResource:@"Images" ofType:@"bundle"];
    // Create a file manager
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSDirectoryEnumerator *enumerator;
    enumerator = [fileManager enumeratorAtPath:path];
    while((path = [enumerator nextObject]) != nil) {
        // Add the obtained picture to the array
        [tempArray addObject:path];
        
    }
    return [[NSArray alloc] initWithArray:(NSArray *)tempArray];
}

- (void)rightButtonClick{
   // [self selectItem:selectModel];
}

- (UIImagePickerController *)imagePickerController{
    if (_imagePickerController==nil) {
        _imagePickerController=[[UIImagePickerController alloc]init];
        _imagePickerController.delegate=self;
        _imagePickerController.allowsEditing = NO;
    }
    return _imagePickerController;
}

- (BOOL)isNotchScreen {
    
    if ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPad) {
        return NO;
    }
    
    CGSize size = [UIScreen mainScreen].bounds.size;
    NSInteger notchValue = size.width / size.height * 100;
    
    if (216 == notchValue || 46 == notchValue) {
        return YES;
    }
    
    return NO;
}

@end
