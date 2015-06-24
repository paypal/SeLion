/*!
 @class FingerCountViewController
 
 @brief View controller for the two finger touch actions. It implements the protocol Interface to receive events.
 
 @copyright  Copyright (C) 2015 PayPal
 
 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 with the License.
 
 You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 the specific language governing permissions and limitations under the License.
 */

#import "FingerCountViewController.h"
#import "MultiTouchView.h"

@interface FingerCountViewController ()

@end

@implementation FingerCountViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Touch";
    self.twoFingerButtonText.text = @"";
    MultiTouchView* multiTouchView = (MultiTouchView*) self.view;
    multiTouchView.myDelegate = self;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

- (void) buttonPressed:(unsigned long)count {
    self.twoFingerButtonText.text = [NSString stringWithFormat:@"Touch Count: %lu", count];
}

@end
