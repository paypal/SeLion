/*!
 @class PressCountViewController
 
 @brief View controller for the single and double tap button actions.
 
 @copyright  Copyright (C) 2015 PayPal
 
 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 with the License.
 
 You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 the specific language governing permissions and limitations under the License.
 */

#import "PressCountViewController.h"

@interface PressCountViewController ()

@end

@implementation PressCountViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Tap";
    NSLog(@"clearing text");
    self.buttonText.text = @"";
    [self.button addTarget:self action:@selector(multipleTap:withEvent:) forControlEvents:UIControlEventTouchDownRepeat];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (IBAction)multipleTap:(id)sender withEvent:(UIEvent*)event {
    UITouch* uiTouch = [[event allTouches] anyObject];
    self.buttonText.text = [NSString stringWithFormat:@"Tap Count: %lu", (unsigned long)uiTouch.tapCount];
}

- (IBAction)singlePress:(id)sender {
    self.singleButtonText.text = @"Tap Count: 1";
}

@end
