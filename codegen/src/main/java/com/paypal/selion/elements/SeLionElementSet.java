/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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

package com.paypal.selion.elements;


import com.paypal.selion.plugins.CodeGeneratorLogger;
import org.apache.commons.lang.StringUtils;

import com.paypal.selion.plugins.CodeGeneratorLoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

/**
 * Abstract implementation for representing SeLion elements which are used for code generation.
 */
public abstract class SeLionElementSet extends LinkedHashSet<SeLionElement> {
    // used to ensure that all new elements are added to the front of the set, once initialized.
    private final boolean initialized;

    public SeLionElementSet() {
        super();
        initialized = true;
    }

    public SeLionElementSet(SeLionElement[] elements) {
        super(Arrays.asList(elements));
        initialized = true;
    }

    public SeLionElementSet(Collection<SeLionElement> elements) {
        super(elements);
        initialized = true;
    }

    /**
     * @param element The element that needs to be searched.
     * @return Either the {@link SeLionElement} found from the list or <code>null</code> if it was not
     * found.
     */
    public SeLionElement findMatch(String element) {
        if (!StringUtils.isEmpty(element)) {
            Optional<SeLionElement> first =
                stream().filter(eachElement -> eachElement.looksLike(element)).findFirst();
            return first.orElse(null);
        }
        return null;
    }


    /**
     * @param element The element that needs to be searched.
     * @return <code>true</code> if the element was found on the list or <code>false</code> if it was not found.
     */
    public boolean isValid(String element) {
        return findMatch(element) != null;
    }

    /**
     * @param element The element that needs to be searched.
     * @return- <code>true</code> if the element was found in the list of elements and if its
     * {@link SeLionElement#isUIElement()} resulted in <code>true</code>.
     */
    public boolean isValidUIElement(String element) {
        SeLionElement se = findMatch(element);
        if (se != null) {
            return se.isUIElement();
        }
        return false;
    }

    /**
     * @param element The element that needs to be searched.
     * @return <code>true</code> if the element was found in the list.
     */
    public boolean isExactMatch(String element) {
        if (!StringUtils.isEmpty(element)) {
            Optional<SeLionElement> first =
                stream().filter(eachElement -> eachElement.getElementClass().equals(element)).findFirst();
            return first.isPresent();
        }
        return false;
    }

    /**
     * By providing the qualified name of a custom element we can register it to the element set. Custom elements are
     * inserted before SeLion elements. If you use the same name it will overwrite the existing element.
     *
     * @param element string of the qualified class
     */
    public abstract boolean add(String element);

    /**
     * Add an element to the list
     * @param element the element
     */
    public boolean add(SeLionElement element) {
        // if already initialized, add the new elements to the front of the list.
        if (initialized) {
            CodeGeneratorLogger logger = CodeGeneratorLoggerFactory.getLogger();

            // info messages are only displayed after initialization happens
            if (isExactMatch(element.getElementClass())) {
                logger.info("Registering the element " + element + " which is now overwriting a pre-existing element.");
            } else {
                logger.info("Registering the element " + element + " as a valid element.");
            }

            SeLionElementSet elementSet = (SeLionElementSet) clone();
            clear();
            super.add(element);
            elementSet.stream().forEachOrdered(a -> super.add(a));
            return true;
        }
        return super.add(element);
    }
}
