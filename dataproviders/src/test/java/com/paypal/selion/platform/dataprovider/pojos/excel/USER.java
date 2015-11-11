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

package com.paypal.selion.platform.dataprovider.pojos.excel;

import java.util.Arrays;

public class USER {

    /*
     * Data structure. Please note that the order is very important here. If this order is mismatched with the
     * excel-sheet column, then the data may not be read correctly, or even fail to read.
     * 
     * This is the starting point of our data.
     */
    private String name;
    private String password;
    private Long accountNumber;
    private Double amount;
    private AREA_CODE[] areaCode;
    private BANK bank;
    private String phoneNumber;
    private int preintTest;
    private boolean isbooleanGood;
    private double doubleTest;
    private long longTest;
    private float floatTest;
    private byte byteTest;

    /*
     * Get and Set properties
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public AREA_CODE[] getAreaCode() {
        return Arrays.copyOf(areaCode, areaCode.length);
    }

    public void setAreaCode(AREA_CODE[] areaCode) {
        this.areaCode = areaCode;
    }

    public BANK getBank() {
        return bank;
    }

    public void setBank(BANK bank) {
        this.bank = bank;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPreintTest() {
        return preintTest;
    }

    public boolean getIsBooleanGood() {
        return isbooleanGood;
    }

    public double getDoubleTest() {
        return doubleTest;
    }

    public long getLongTest() {
        return longTest;
    }

    public float getFloatTest() {
        return floatTest;
    }

    public byte getByteTest() {
        return byteTest;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("USER [");
        if (name != null) {
            builder.append("name=");
            builder.append(name);
            builder.append(", ");
        }
        if (password != null) {
            builder.append("password=");
            builder.append(password);
            builder.append(", ");
        }
        if (accountNumber != null) {
            builder.append("accountNumber=");
            builder.append(accountNumber);
            builder.append(", ");
        }
        if (amount != null) {
            builder.append("amount=");
            builder.append(amount);
            builder.append(", ");
        }
        if (areaCode != null) {
            builder.append("areaCode=");
            builder.append(Arrays.toString(areaCode));
            builder.append(", ");
        }
        if (bank != null) {
            builder.append("bank=");
            builder.append(bank);
            builder.append(", ");
        }
        if (phoneNumber != null) {
            builder.append("phoneNumber=");
            builder.append(phoneNumber);
            builder.append(", ");
        }
        builder.append("preintTest=");
        builder.append(preintTest);
        builder.append(", isbooleanGood=");
        builder.append(isbooleanGood);
        builder.append(", doubleTest=");
        builder.append(doubleTest);
        builder.append(", longTest=");
        builder.append(longTest);
        builder.append(", floatTest=");
        builder.append(floatTest);
        builder.append(", byteTest=");
        builder.append(byteTest);
        builder.append("]");
        return builder.toString();
    }

    public void setByteTest(byte b) {
        this.byteTest = b;
    }

    public void setPreintTest(int i) {
        this.preintTest = i;
    }

    public void setIsbooleanGood(boolean b) {
        this.isbooleanGood = b;

    }

    public void setDoubleTest(double d) {
        this.doubleTest = d;

    }

    public void setLongTest(long l) {
        this.longTest = l;

    }

    public void setFloatTest(float f) {
        this.floatTest = f;

    }

    public boolean getIsbooleanGood() {
        return isbooleanGood;
    }
}
