/*!
 @header TableViewController.h
 
 @brief View to handle table events.
 
 @copyright  Copyright (C) 2015 PayPal
 
 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 with the License.
 
 You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 the specific language governing permissions and limitations under the License.
 */

#import <UIKit/UIKit.h>

@interface TableViewController : UIViewController<UITableViewDataSource, UITableViewDelegate>

/*! @brief Table view reference for view controller */
@property (strong, nonatomic) IBOutlet UITableView *tableView;

/*! @brief Array to hold the table cells */
@property (strong, nonatomic) NSMutableArray *cells;

/*! @brief Holds the name of the cell acted upon */
@property (strong, nonatomic) NSString *cellName;

@end
