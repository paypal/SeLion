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

package com.paypal.selion.utils;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.paypal.selion.utils.FileBackedStringBuffer;

/**
 * This method used to test FileBackedStringBuffer append and toString method.
 * 
 */
public class FileBackedStringBufferTest {

    @Test
    public void FileBackedStrBufferTest() {
        {
            FileBackedStringBuffer fsb = new FileBackedStringBuffer(5);
            String s = "0123456789";
            String s3 = s + s + s;

            fsb.append(s3);
            assertEquals(fsb.toString(), s3);
        }
    }

    @Test
    public void appendTest() {
        FileBackedStringBuffer fsb = new FileBackedStringBuffer(5);
        String s = "0123456789";
        String s3 = s + s + s;

        fsb.append(s);
        fsb.append(s);
        fsb.append(s);
        assertEquals(fsb.toString().replaceAll("\\n", ""), s3);
    }
}
