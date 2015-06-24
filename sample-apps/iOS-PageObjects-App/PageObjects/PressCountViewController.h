/*!
 @header PressCountViewController.h

 @brief View controller for the single and double tap button actions.

 @copyright  Copyright (C) 2015 PayPal

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 with the License.
 
 You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 the specific language governing permissions and limitations under the License.
 */

#import <UIKit/UIKit.h>

@interface PressCountViewController : UIViewController

/*! @brief Text field to denote the performed multi tap action */
@property (strong, nonatomic) IBOutlet UITextField *buttonText;

/*! @brief Button reference for the view controller */
@property (strong, nonatomic) IBOutlet UIButton *button;

/*! @brief Text field to denote the performed single tap action */
@property (strong, nonatomic) IBOutlet UITextField *singleButtonText;

/*!
 @brief Captures single press event.
 
 @param sender Source of button.
 
 @return IBAction
 */
- (IBAction)singlePress:(id)sender;

@end
