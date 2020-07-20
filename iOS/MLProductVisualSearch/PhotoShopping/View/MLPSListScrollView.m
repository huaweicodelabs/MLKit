//
//  Copyright (c) Huawei Technologies Co., Ltd. 2020-2028. All rights reserved.
//

#import "MLPSListScrollView.h"
#import "MLPSCollectionViewCell.h"
static NSString *identifier = @"MLPSCollectionViewCell";
@interface MLPSListScrollView ()<UICollectionViewDelegate,UICollectionViewDataSource,UICollectionViewDelegateFlowLayout,UIScrollViewDelegate>
{
    NSArray *listArray;
}
@property (nonatomic, strong) UICollectionView *listView;
@end

@implementation MLPSListScrollView


- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        
        self.contentSize = CGSizeMake(0, frame.size.height);
        self.delegate = self;
        self.scrollEnabled = NO;
        self.backgroundColor = [UIColor colorWithRed:242.0/255.0 green:242.0/255.0 blue:242.0/255.0 alpha:1];
        [self addSubview:self.listView];
    }
    return self;
}

- (UICollectionView *)listView{
    if (!_listView) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        flowLayout.headerReferenceSize = CGSizeMake(kMLDocumentDeviceWidth, 60);
        flowLayout.sectionHeadersPinToVisibleBounds = YES;
        
        _listView = [[UICollectionView alloc] initWithFrame:CGRectMake(0, 0, kMLDocumentDeviceWidth, self.frame.size.height) collectionViewLayout:flowLayout];
        _listView.backgroundColor = [UIColor whiteColor];
        _listView.scrollEnabled = YES;
        _listView.alpha = 1;
        _listView.delegate = self;
        _listView.dataSource = self;
        [_listView registerClass:[MLPSCollectionViewCell class] forCellWithReuseIdentifier:identifier];
        [_listView registerClass:[UICollectionViewCell class] forCellWithReuseIdentifier:@"UICollectionViewCell"];
        [_listView registerClass:[UICollectionReusableView class] forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:@"UICollectionReusableView" ];
    }
    return _listView;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return [self.dataArr getImageList].count;
}

// Define and return each cell
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath{
    MLPSCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
    MLVisionSearchProductImage *image = [[self.dataArr getImageList]  objectAtIndex:indexPath.row];
    cell.bottomLabel.text = [NSString stringWithFormat:@"%@(%.5f)",image.productId,image.score];

    //[cell.topImageView sd_setImageWithURL:[NSURL URLWithString:image.innerUrl]];
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
    NSData* data = [NSData dataWithContentsOfURL:[NSURL URLWithString:image.innerUrl]];
        UIImage *img=[UIImage imageWithData:data];
        dispatch_async(dispatch_get_main_queue(), ^{
            cell.topImageView.image = img;
        });
    });
    return cell;
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath{
    UICollectionReusableView *headerView = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:@"UICollectionReusableView" forIndexPath:indexPath];
    [headerView addSubview:[self headerView]];
    return headerView;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath{
    return CGSizeMake(kMLDocumentDeviceWidth/2 - 10, 150);
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout referenceSizeForHeaderInSection:(NSInteger)section{
    return CGSizeMake(kMLDocumentDeviceWidth, 80);
}

- (UIView *)headerView{
    UIView *headView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kMLDocumentDeviceWidth, 80)];
    headView.backgroundColor = kTTSGrayColor;
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 45, kMLDocumentDeviceWidth, 20)];
    NSArray *array = @[@"others",
    @"clothing",
    @"shoes",
    @"bags",
    @"digital & home appliances",
    @"homegoods",
    @"toys",
    @"cosmetics",
    @"accessories",
    @"food"];
    
    if (array.count -1 >=[_dataArr.type intValue]) {
        label.text = [NSString stringWithFormat:@"%@(ClassType:%@)",NSLocalizedString(@"相似商品", nil),[array objectAtIndex:[_dataArr.type intValue]]];
    }
    
    label.textColor = [UIColor colorWithRed:57.0/255.0 green:159.0/255.0 blue:250.0/255.0 alpha:1];
    label.font = [UIFont systemFontOfSize:18];
    
    label.textAlignment = NSTextAlignmentCenter;
    [headView addSubview:label];
    return headView;
}

@end
