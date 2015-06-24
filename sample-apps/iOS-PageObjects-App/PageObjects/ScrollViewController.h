/*!
 @header ScrollViewController.h
 
 @brief View to handle scroll events.
 
 @copyright  Copyright (C) 2015 PayPal
 
 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 with the License.
 
 You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 the specific language governing permissions and limitations under the License.
 */

#import <UIKit/UIKit.h>

@interface ScrollViewController : UIViewController

/*! @brief Scroll view reference */
@property (strong, nonatomic) IBOutlet UIScrollView *scroller;

/*! @brief Text field to denote the actions on slider */
@property (strong, nonatomic) IBOutlet UITextField *sliderText;

/*! @brief Text field to denote the action on switch */
@property (strong, nonatomic) IBOutlet UITextField *switchText;

/*! @brief Slider reference */
@property (strong, nonatomic) IBOutlet UISlider *slider;

/*!
 @brief Called when slider value is changed.
 
 @param sender Source slider.
 
 @return IBAction.
 */
- (IBAction)sliderValueChanged:(id)sender;

/*!
 @brief Called when switch value is changed.
 
 @param sender Source switch.
 
 @return IBAction.
 */
- (IBAction)switchValueChanged:(id)sender;

@end
