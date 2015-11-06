/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

package com.paypal.selion.platform.mobile.ios;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * <code>GestureOptions</code> enum houses the gesture specifications for mobile web drivers. Contains most of the
 * gestures as stated in
 * https://developer.apple.com/library/prerelease/ios/documentation/DeveloperTools/Reference/UIAutomationRef/index.html
 */
public enum GestureOptions {

    /**
     * Tap count. The number of taps that compose the specified gesture. The default value is 1 (single tap).
     */
    TAP_COUNT("tapCount"),

    /**
     * Touch count. The number of touches to use in the specified gesture. (Effectively, the number of fingers a user
     * would use to make the specified gesture.) The default touch count value is 1.
     */
    TOUCH_COUNT("touchCount"),

    /**
     * Start offset. The offset to use for the specified tap gesture. The default offset value is {x:0.0, y:0.0}. See
     * the discussion for details.
     */
    START_OFFSET("startOffset"),

    /**
     * End offset. The last offset to use for a multiple-point gesture. The default value is {x:0.0, y:0.0}. See the
     * discussion for details.
     */
    END_OFFSET("endOffset"),

    /**
     * Center offset. The offset to use for the center of the rotate gesture. The default offset value is {x:0.0,
     * y:0.0}.
     */
    CENTER_OFFSET("centerOffset"),

    /**
     * Tap offset. The offset to use for the specified tap gesture. The default offset value is {x:0.0, y:0.0}. See the
     * discussion for details.
     */
    TAP_OFFSET("tapOffset"),

    /**
     * Radius. The distance in points from the center to the edge of the circular path.
     */
    RADIUS("radius"),

    /**
     * Rotation. The length of rotation in radians. The default is pi (π).
     */
    ROTATION("rotation"),

    /**
     * Duration. The length of hold time for the specified gesture. The default duration value for a tap is 0. The
     * default value for touch-and-hold gestures (such as drag, pinch open, and pinch close) is 1.
     */
    DURATION("duration"),

    /**
     * X coordinate, or offset in the range 0 to 1 inclusive
     */
    X("x"),

    /**
     * Y coordinate, or offset in the range 0 to 1 inclusive
     */
    Y("y");

    private String optionName;

    private GestureOptions(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionName() {
        return optionName;
    }

    /**
     * The <code>TapOption</code> class serves as a utility class to convert value based {@link GestureOptions} to JSON
     * objects. UIAAutomation requires these tapOptions to be Javascript objects, but JSON formats are also taken by
     * UIAAutomation framework.
     */
    public static class TapOptions {

        private static final SimpleLogger logger = SeLionLogger.getLogger();

        @SuppressWarnings("unused")
        private Integer tapCount;

        @SuppressWarnings("unused")
        private Integer touchCount;

        @SuppressWarnings("unused")
        private Integer duration;

        @SuppressWarnings("unused")
        private TapOffset tapOffset;

        private final Gson gson;

        public TapOptions() {
            gson = new GsonBuilder().disableHtmlEscaping().create();
        }

        /**
         * Sets a valid option for this {@link TapOptions} object. Invalid options are silently ignored.
         * 
         * @param option
         *            {@link GestureOptions#TAP_COUNT} or {@link GestureOptions#TOUCH_COUNT} or
         *            {@link GestureOptions#DURATION}
         * @param value
         *            int value.
         */
        public void setOption(GestureOptions option, int value) {
            switch (option) {
            case TOUCH_COUNT:
                touchCount = value;
                break;
            case TAP_COUNT:
                tapCount = value;
                break;
            case DURATION:
                duration = value;
                break;
            default:

                // Ignore invalid options for TapOptions
                break;
            }
        }

        /**
         * Sets the tapOffset for this {@link TapOptions} object.
         * 
         * @param tapOffset
         *            {@link TapOffset} instance.
         */
        public void setOffset(TapOffset tapOffset) {
            this.tapOffset = tapOffset;
        }

        /**
         * Returns a JSON representation of this {@link TapOptions}
         * 
         * @return JSON data as {@link String}
         */
        public String toString() {
            return gson.toJson(this);
        }

        /**
         * Returns a {@link Map} representation of this {@link TapOptions}
         * 
         * @return Instance of {@link Map}
         */
        public Map<String, String> asMap() {
            logger.entering();
            Map<?, ?> m = gson.fromJson(this.toString(), Map.class);
            Map<String, String> map = new HashMap<String, String>();
            for (Entry<?, ?> entry : m.entrySet()) {
                map.put(entry.getKey().toString(), entry.getValue().toString());
            }
            logger.exiting(map);
            return map;
        }

    }

    /**
     * The <code>TapOffset</code> acts as a utility class for converting cartesion coordinates (x, y) to JSON
     * representation.
     */
    public static class TapOffset {

        @SuppressWarnings("unused")
        private final double x;

        @SuppressWarnings("unused")
        private final double y;

        private Gson gson;

        public TapOffset(double x, double y) {
            this.x = x;
            this.y = y;
            gson = new GsonBuilder().disableHtmlEscaping().create();
        }

        /**
         * Returns a JSON representation of this {@link TapOptions}
         * 
         * @return JSON data as {@link String}
         */
        public String toString() {
            return gson.toJson(this);
        }

    }
}
