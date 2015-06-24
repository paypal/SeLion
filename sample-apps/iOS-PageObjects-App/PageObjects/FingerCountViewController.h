/*!
 @header FingerCountViewController.h
 
 @brief View controller for the two finger touch actions.
 
 @copyright  Copyright (C) 2015 PayPal
 
 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 with the License.
 
 You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 the specific language governing permissions and limitations under the License.
 */

#import <UIKit/UIKit.h>
#import "Interface.h"

@interface FingerCountViewController : UIViewController<Interface>

/*! @brief Text field to denote the two finger tap action */
@property (strong, nonatomic) IBOutlet UITextField *twoFingerButtonText;

/*! @brief Button reference for view controller */
@property (strong, nonatomic) IBOutlet UIButton *twoFingerButton;

@end
