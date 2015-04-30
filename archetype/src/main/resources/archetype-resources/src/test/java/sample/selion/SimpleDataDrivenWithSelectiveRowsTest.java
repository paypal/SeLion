/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

import com.paypal.selion.platform.dataprovider.SimpleExcelDataProvider;
import ${package}.sample.dataobjects.SimpleData;

import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 * In this sample we will see how can SeLion be used for running data driven tests wherein the data for the data driven
 * tests are stored in excel spread sheets. For the sake of simplicity this TestNG based test will resort to just
 * running assertions on the data fetched from the excel spreadsheet.
 */
public class SimpleDataDrivenWithSelectiveRowsTest {

    private int numberOfRowsRead = 0;

    @DataProvider(name = "simpleReader")
    public Object[][] setupExcelDataProvider () throws IOException{
        //Lets first initialize the data provider and specify the file from which data is to be read from.
        SimpleExcelDataProvider
            dataProvider =
            new SimpleExcelDataProvider("src/test/resources/testdata/MyDataFile.xls");

        //Now we specify the sheet from which we need the excel data provider to read values from
        //by passing it a dummy object whose class name matches with the worksheet name .
        return dataProvider.getExcelRows(new SimpleData(), "1-2");
    }

    @Test(dataProvider = "simpleReader")
    public void testExcelDataValues (SimpleData data) {
        Reporter.log("Running test for " + data, true);
        assertTrue(data.getEmployeeId() != 0);
        assertTrue(data.getEmployeeName() != null);
        numberOfRowsRead += 1;
    }

    @Test(dependsOnMethods = {"testExcelDataValues"})
    public void testHowManyRowsWereRead () {
        assertTrue(numberOfRowsRead == 2);
    }

}
