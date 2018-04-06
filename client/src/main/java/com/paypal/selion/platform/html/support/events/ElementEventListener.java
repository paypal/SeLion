/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
|                                                                                                                     |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     |
|  with the License.                                                                                                  |
|                                                                                                                     |
|  You may obtain a copy of the License at                                                                            |
|                                                                                                                     |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                    |
|                                                                                                                     |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  |
|  the specific language governing permissions and limitations under the License.                                     |
\*-------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.platform.html.support.events;

import com.paypal.selion.platform.html.AbstractElement;
import com.paypal.selion.platform.html.Button;
import com.paypal.selion.platform.html.CheckBox;
import com.paypal.selion.platform.html.DatePicker;
import com.paypal.selion.platform.html.Form;
import com.paypal.selion.platform.html.Image;
import com.paypal.selion.platform.html.Label;
import com.paypal.selion.platform.html.Link;
import com.paypal.selion.platform.html.RadioButton;
import com.paypal.selion.platform.html.SelectList;
import com.paypal.selion.platform.html.Table;
import com.paypal.selion.platform.html.TextField;

/**
 * Events which can occur in SeLion's {@link AbstractElement} implementations. This interface allows you to hook into
 * those events to do customization.
 */
public interface ElementEventListener {

    /**
     * This event gets triggered before we perform a click on an element. The following objects trigger this event,
     * {@link Button}, {@link CheckBox}, {@link DatePicker}, {@link Form}, {@link Image}, {@link Label}, {@link Link},
     * {@link RadioButton}, {@link SelectList}, {@link Table}, {@link TextField}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Clickable}
     * @param expected
     *            The expected objects that were passed to the click method
     */
    void beforeClick(Clickable target, Object... expected);

    /**
     * This event gets triggered after we perform a click on an element. The following objects trigger this event,
     * {@link Button}, {@link CheckBox}, {@link DatePicker}, {@link Form}, {@link Image}, {@link Label}, {@link Link},
     * {@link RadioButton}, {@link SelectList}, {@link Table}, {@link TextField}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Clickable}
     * @param expected
     *            The expected objects that were passed to the click method
     */
    void afterClick(Clickable target, Object... expected);

    /**
     * This event gets triggered before we take a screenshot when clicking a element. The following objects trigger this
     * event, {@link Button}, {@link CheckBox}, {@link DatePicker}, {@link Form}, {@link Image}, {@link Label},
     * {@link Link}, {@link RadioButton}, {@link SelectList}, {@link Table}, {@link TextField}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Clickable}
     */
    void beforeScreenshot(Clickable target);

    /**
     * This event gets triggered before we take a screenshot when clicking a element. The following objects trigger this
     * event, {@link Button}, {@link CheckBox}, {@link DatePicker}, {@link Form}, {@link Image}, {@link Label},
     * {@link Link}, {@link RadioButton}, {@link SelectList}, {@link Table}, {@link TextField}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Clickable}
     */
    void afterScreenshot(Clickable target);

    /**
     * This event gets triggered before we start typing in an element. The following objects trigger this event,
     * {@link TextField}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Typeable}
     * @param value
     *            The value that was typed in the field
     */
    void beforeType(Typeable target, String value);

    /**
     * This event gets triggered after we start typing in an element. The following objects trigger this event,
     * {@link TextField}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Typeable}
     * @param value
     *            The value that was typed in the field
     */
    void afterType(Typeable target, String value);

    /**
     * This event gets triggered before we check an element. The following objects trigger this event, {@link CheckBox},
     * {@link RadioButton}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Checkable}
     * @param expected
     *            The expected locator that was passed to the check method
     */
    void beforeCheck(Checkable target, String expected);

    /**
     * This event gets triggered before we check an element. The following objects trigger this event, {@link CheckBox},
     * {@link RadioButton}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Checkable}
     * @param expected
     *            The expected locator that was passed to the check method
     */
    void afterCheck(Checkable target, String expected);

    /**
     * This event gets triggered before we check an element. The following objects trigger this event, {@link CheckBox},
     * {@link RadioButton}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Checkable}
     */
    void beforeCheck(Checkable target);

    /**
     * This event gets triggered after we check an element. The following objects trigger this event, {@link CheckBox},
     * {@link RadioButton}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Checkable}
     */
    void afterCheck(Checkable target);

    /**
     * This event gets triggered before we uncheck an element. The following objects trigger this event,
     * {@link CheckBox}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Uncheckable}
     * @param expected
     *            The expected locator that was passed to the check method
     */
    void beforeUncheck(Uncheckable target, String expected);

    /**
     * This event gets triggered after we uncheck an element. The following objects trigger this event, {@link CheckBox}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Uncheckable}
     * @param expected
     *            The expected locator that was passed to the check method
     */
    void afterUncheck(Uncheckable target, String expected);

    /**
     * This event gets triggered before we uncheck an element. The following objects trigger this event,
     * {@link CheckBox}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Uncheckable}
     */
    void beforeUncheck(Uncheckable target);

    /**
     * This event gets triggered after we uncheck an element. The following objects trigger this event, {@link CheckBox}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Uncheckable}
     */
    void afterUncheck(Uncheckable target);

    /**
     * This event gets triggered before we submit an element. The following objects trigger this event, {@link Form}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Submitable}
     */
    void beforeSubmit(Submitable target);

    /**
     * This event gets triggered after we submit an element. The following objects trigger this event, {@link Form}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Submitable}
     */
    void afterSubmit(Submitable target);

    /**
     * This event gets triggered before we select a option in an element. The following objects trigger this event,
     * {@link SelectList}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Selectable}
     * @param index
     *            The index of the option that we want to select
     */
    void beforeSelect(Selectable target, int index);

    /**
     * This event gets triggered after select a option in an element. The following objects trigger this event,
     * {@link SelectList}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Selectable}
     * @param index
     *            The index of the option that we want to select
     */
    void afterSelect(Selectable target, int index);

    /**
     * This event gets triggered before we select a option in an element. The following objects trigger this event,
     * {@link SelectList}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Selectable}
     * @param value
     *            The value that is going to be selected in the List
     */
    void beforeSelect(Selectable target, String value);

    /**
     * This event gets triggered after select a option in an element. The following objects trigger this event,
     * {@link SelectList}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Selectable}
     * @param value
     *            The value that was selected in the List
     */
    void afterSelect(Selectable target, String value);

    /**
     * This event gets triggered before we deselect a option in an element. The following objects trigger this event,
     * {@link SelectList}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Selectable}
     * @param index
     *            The index of the option that we want to deselect
     */
    void beforeDeselect(Deselectable target, int index);

    /**
     * This event gets triggered after deselect a option in an element. The following objects trigger this event,
     * {@link SelectList}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Selectable}
     * @param index
     *            The index of the option that we want to deselect
     */
    void afterDeselect(Deselectable target, int index);

    /**
     * This event gets triggered before we deselect a option in an element. The following objects trigger this event,
     * {@link SelectList}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Selectable}
     * @param value
     *            The value that is going to be deselected in the List
     */
    void beforeDeselect(Deselectable target, String value);

    /**
     * This event gets triggered after deselect a option in an element. The following objects trigger this event,
     * {@link SelectList}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Selectable}
     * @param value
     *            The value that was deselected in the List
     */
    void afterDeselect(Deselectable target, String value);

    /**
     * This event gets triggered before we deselect a option in an element. The following objects trigger this event,
     * {@link SelectList}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Selectable}
     */
    void beforeDeselect(Deselectable target);

    /**
     * This event gets triggered after deselect a option in an element. The following objects trigger this event,
     * {@link SelectList}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Selectable}
     */
    void afterDeselect(Deselectable target);

    /**
     * This event gets triggered before we hover an element. The following objects trigger this event, {@link Button},
     * {@link CheckBox}, {@link DatePicker}, {@link Form}, {@link Image}, {@link Label}, {@link Link},
     * {@link RadioButton}, {@link SelectList}, {@link Table}, {@link TextField}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Hoverable}
     * @param expected
     *            The expected objects that were passed to the hover method
     */
    void beforeHover(Hoverable target, Object... expected);

    /**
     * This event gets triggered after we hover an element. The following objects trigger this event, {@link Button},
     * {@link CheckBox}, {@link DatePicker}, {@link Form}, {@link Image}, {@link Label}, {@link Link},
     * {@link RadioButton}, {@link SelectList}, {@link Table}, {@link TextField}
     * 
     * @param target
     *            Instance of the element that triggered this event and implements {@link Hoverable}
     * @param expected
     *            The expected objects that were passed to the hover method
     */
    void afterHover(Hoverable target, Object... expected);

}
