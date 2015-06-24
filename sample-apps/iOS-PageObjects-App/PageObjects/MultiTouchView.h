/*!
 @header MultiTouchView.h
 
 @brief View to handle multi touch actions like two finger touch.
 
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

@interface MultiTouchView : UIView

/*! @brief Delegate reference */
@property(strong, nonatomic) id<Interface> myDelegate;

/*!
 @brief Method that delegates to the interface.
 
 @param count Number of fingers used in touch.
 */
- (void)buttonWasClicked:(unsigned long)count;

@end
