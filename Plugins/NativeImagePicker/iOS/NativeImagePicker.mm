#include <MobileCoreServices/MobileCoreServices.h>

#define kGameObjectName [@"NIP_599349_GO" UTF8String]
#define kMethodName [@"CallbackSelectedImage" UTF8String]

extern "C" void UnitySendMessage(const char *, const char *, const char *);

@interface NativeImagePicker : NSObject<UINavigationControllerDelegate, UIImagePickerControllerDelegate>

@end

@implementation NativeImagePicker

- (instancetype)init
{
    self = [super init];
    if (self) {
    }
    return self;
}

- (void)fromLibrary:(BOOL)allowEditing
{
    UIImagePickerController *media = [[UIImagePickerController alloc] init];
    media.delegate = self;
    media.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    media.allowsEditing = allowEditing;
    media.mediaTypes = [[NSArray alloc] initWithObjects:(NSString *)kUTTypeImage, nil];
    [[[[UIApplication sharedApplication] keyWindow] rootViewController] presentViewController:media animated:YES completion:nil];
}

- (void)fromCamera:(BOOL)allowEditing
{
    UIImagePickerController *media = [[UIImagePickerController alloc] init];
    media.delegate = self;
    media.sourceType = UIImagePickerControllerSourceTypeCamera;
    media.allowsEditing = allowEditing;
    media.mediaTypes = [[NSArray alloc] initWithObjects:(NSString *)kUTTypeImage, nil];
    [[[[UIApplication sharedApplication] keyWindow] rootViewController] presentViewController:media animated:YES completion:nil];
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info
{
    UIImage *img = [info valueForKey:UIImagePickerControllerOriginalImage];
    UIImage *edited = [info valueForKey:UIImagePickerControllerEditedImage];
    
    if(edited != nil)
        img = edited;
    
    if(img != nil)
    {
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *path = [[paths objectAtIndex:0] stringByAppendingPathComponent:@"temp.png"];

        NSString *fileUri = [NSString stringWithFormat:@"file:%@",path];
        
        NSData *data = UIImagePNGRepresentation(img);
        [data writeToFile:path atomically:YES];
        
        UnitySendMessage(kGameObjectName, kMethodName, [fileUri UTF8String]);
    }
    else
    {
        UnitySendMessage(kGameObjectName, kMethodName, [@"" UTF8String]);
    }
    
    [picker dismissViewControllerAnimated:YES completion:nil];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    UnitySendMessage(kGameObjectName, kMethodName, [@"" UTF8String]);
    
    [picker dismissViewControllerAnimated:YES completion:nil];
}

@end

extern "C"
{
    static NativeImagePicker *sImagePicker = nil;
    
    void _CNativeImagePickerFromLibrary(bool allowEditing)
	{
        if(sImagePicker == nil)
            sImagePicker = [[NativeImagePicker alloc] init];
        
        [sImagePicker fromLibrary:allowEditing];
	}
    
    void _CNativeImagePickerFromCamera(bool allowEditing)
    {
        if(sImagePicker == nil)
            sImagePicker = [[NativeImagePicker alloc] init];
        
        [sImagePicker fromCamera:allowEditing];
    }
}
