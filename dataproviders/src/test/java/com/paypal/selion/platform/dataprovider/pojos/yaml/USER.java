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

package com.paypal.selion.platform.dataprovider.pojos.yaml;

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

    public USER() {
        super();
    }

    public USER(String name, String password, Long accountNumber, Double amount, AREA_CODE[] areaCode, BANK bank,
            String phoneNumber, int preintTest, boolean isbooleanGood, double doubleTest, long longTest,
            float floatTest, byte byteTest) {
        super();
        this.setName(name);
        this.setPassword(password);
        this.setAccountNumber(accountNumber);
        this.setAmount(amount);
        this.setAreaCode(areaCode);
        this.setBank(bank);
        this.setPhoneNumber(phoneNumber);
        this.setPreintTest(preintTest);
        this.setIsbooleanGood(isbooleanGood);
        this.setDoubleTest(doubleTest);
        this.setLongTest(longTest);
        this.setFloatTest(floatTest);
        this.setByteTest(byteTest);

    }

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
        return areaCode;
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

    public boolean isIsbooleanGood() {
        return isbooleanGood;
    }

    public void setIsbooleanGood(boolean isbooleanGood) {
        this.isbooleanGood = isbooleanGood;
    }

    public void setPreintTest(int preintTest) {
        this.preintTest = preintTest;
    }

    public void setDoubleTest(double doubleTest) {
        this.doubleTest = doubleTest;
    }

    public void setLongTest(long longTest) {
        this.longTest = longTest;
    }

    public void setFloatTest(float floatTest) {
        this.floatTest = floatTest;
    }

    public void setByteTest(byte byteTest) {
        this.byteTest = byteTest;
    }

    public int getPreintTest() {
        return preintTest;
    }

    public boolean getIsbooleanGood() {
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
}
