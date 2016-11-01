/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

package com.paypal.selion.internal.utils;

import com.google.common.base.Preconditions;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Methods for performing regex based matches.
 */
public final class RegexUtils {
    private static SimpleLogger logger = SeLionLogger.getLogger();

    private RegexUtils() {
        // Utility class so defeat instantiation.
    }

    /**
     * Performs a wild-card matching for the text and pattern provided.
     * 
     * @param text
     *            the text to be tested for matches.
     * @param pattern
     *            the pattern to be matched for. This can contain the wildcard character '*' (asterisk).
     * @return <tt>true</tt> if a match is found, <tt>false</tt> otherwise.
     */
    public static boolean wildCardMatch(String text, String pattern) {
        logger.entering(new Object[] { text, pattern });
        Preconditions.checkArgument(text != null, "The text on which the search is to be run cannot be null.");
        Preconditions.checkArgument(pattern != null, "The search pattern cannot be null.");
        // Create the cards by splitting using a RegEx. If more speed
        // is desired, a simpler character based splitting can be done.
        String[] cards = pattern.split("\\*");

        // Iterate over the cards.
        for (String card : cards) {
            int idx = text.indexOf(card);

            // Card not detected in the text.
            if (idx == -1) {
                logger.exiting(false);
                return false;
            }

            // Move ahead, towards the right of the text.
            text = text.substring(idx + card.length());
        }
        logger.exiting(true);
        return true;
    }
}
