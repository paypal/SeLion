/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

package ${package}.sample.selion;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;

import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.paypal.selion.platform.dataprovider.DataProviderFactory;
import com.paypal.selion.platform.dataprovider.DataResource;
import com.paypal.selion.platform.dataprovider.ExcelDataProvider;
import com.paypal.selion.platform.dataprovider.impl.DefaultCustomType;
import com.paypal.selion.platform.dataprovider.impl.FileSystemResource;
import ${package}.sample.dataobjects.Country;
import ${package}.sample.dataobjects.CustomData;

/**
 * This test class demonstrates how to use SeLion's data driven support abilities and read test data from 
 * Excel Spread sheets.
 */
public class DataDrivenWithCustomTypesTest {

    @DataProvider(name = "simpleReader")
    public Object[][] setupExcelDataProvider () throws IOException, NoSuchMethodException {
        //Lets first initialize the data provider and specify the file from which data is to be read from.
        DataResource resource = new FileSystemResource("src/test/resources/testdata/MyDataFile.xls", CustomData.class);
        ExcelDataProvider dataProvider = (ExcelDataProvider) DataProviderFactory.getDataProvider(resource);
        //Since we now would like to use a custom data type that is known only to our test project and
        //since SeLion has no idea about it, lets tell the excel data provider as to how should it
        //work with our custom type (enum in this case), but passing a custom type object wherein
        //we basically specify the static method in our enum which is responsible for creating enum
        //objects
        Method method = Country.class.getMethod("getCountry", String.class);
        DefaultCustomType type = new DefaultCustomType(Country.class,method );

        //We are now injecting this custom type into excel data provider so that it knows how to work with our custom
        //type viz., the enum 'Country'
        dataProvider.addCustomTypes(type);

        //Now we specify the sheet from which we need the excel data provider to read values from
        //by passing it a dummy object whose class name matches with the worksheet name .
        return dataProvider.getAllData();
    }

    @Test(dataProvider = "simpleReader")
    public void testExcelDataValues (CustomData data) {
        Reporter.log("Running test for " + data, true);
        assertTrue(data.getEmployeeName() != null);
        assertTrue(data.getCountry() == Country.UNITED_STATES);
    }


}
