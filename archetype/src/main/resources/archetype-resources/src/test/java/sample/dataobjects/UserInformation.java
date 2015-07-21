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

package ${package}.sample.dataobjects;

import java.util.Arrays;

/**
 * This is a simple POJO (Plain Old Java Object) that represents the information that is stored
 * in the yaml files located @ src/test/resources/testdata/.
 */
public class UserInformation {

    private String name = null;
    private String password = null;
    private Long accountNumber = null;
    private Double amount = null;
    private AreaCode[] areaCode = null;
    private BankInformation bank = null;
    private String phoneNumber = null;
    private int preintTest = 0;
    private boolean isbooleanGood = false;
    private double doubleTest = 0.0;
    private long longTest = 0;
    private float floatTest = (float) 0.0;
    private byte byteTest;

    public UserInformation () {
    }

    public UserInformation (String name, String password, Long accountNumber, Double amount, AreaCode[] areaCode,
                            BankInformation bank,
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
    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public Long getAccountNumber () {
        return accountNumber;
    }

    public void setAccountNumber (Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getAmount () {
        return amount;
    }

    public void setAmount (Double amount) {
        this.amount = amount;
    }

    public AreaCode[] getAreaCode () {
        if (areaCode == null){
            return areaCode;
        }
        return Arrays.copyOf(areaCode, areaCode.length);
    }

    public void setAreaCode (AreaCode[] areaCode) {
        if (areaCode != null) {
            this.areaCode = Arrays.copyOf(areaCode, areaCode.length);
        }
    }

    public BankInformation getBank () {
        return bank;
    }

    public void setBank (BankInformation bank) {
        this.bank = bank;
    }

    public String getPhoneNumber () {
        return phoneNumber;
    }

    public void setPhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isIsbooleanGood () {
        return isbooleanGood;
    }

    public void setIsbooleanGood (boolean isbooleanGood) {
        this.isbooleanGood = isbooleanGood;
    }

    public void setPreintTest (int preintTest) {
        this.preintTest = preintTest;
    }

    public void setDoubleTest (double doubleTest) {
        this.doubleTest = doubleTest;
    }

    public void setLongTest (long longTest) {
        this.longTest = longTest;
    }

    public void setFloatTest (float floatTest) {
        this.floatTest = floatTest;
    }

    public void setByteTest (byte byteTest) {
        this.byteTest = byteTest;
    }

    public int getPreintTest () {
        return preintTest;
    }

    public boolean getIsbooleanGood () {
        return isbooleanGood;
    }

    public double getDoubleTest () {
        return doubleTest;
    }

    public long getLongTest () {
        return longTest;
    }

    public float getFloatTest () {
        return floatTest;
    }

    public byte getByteTest () {
        return byteTest;
    }

    @Override
    public String toString () {
        final StringBuilder sb = new StringBuilder("UserInformation{");
        sb.append("name='").append(name).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", accountNumber=").append(accountNumber);
        sb.append(", amount=").append(amount);
        sb.append(", areaCode=").append(Arrays.toString(areaCode));
        sb.append(", bank=").append(bank);
        sb.append(", phoneNumber='").append(phoneNumber).append('\'');
        sb.append(", preintTest=").append(preintTest);
        sb.append(", isbooleanGood=").append(isbooleanGood);
        sb.append(", doubleTest=").append(doubleTest);
        sb.append(", longTest=").append(longTest);
        sb.append(", floatTest=").append(floatTest);
        sb.append(", byteTest=").append(byteTest);
        sb.append('}');
        return sb.toString();
    }
}
