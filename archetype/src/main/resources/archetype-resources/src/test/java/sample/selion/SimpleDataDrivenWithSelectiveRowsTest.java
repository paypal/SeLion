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

import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.paypal.selion.platform.dataprovider.DataProviderFactory;
import com.paypal.selion.platform.dataprovider.DataResource;
import com.paypal.selion.platform.dataprovider.SeLionDataProvider;
import com.paypal.selion.platform.dataprovider.impl.FileSystemResource;
import ${package}.sample.dataobjects.SimpleData;

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
        DataResource resource = new FileSystemResource("src/test/resources/testdata/MyDataFile.xls", SimpleData.class);
        SeLionDataProvider dataProvider = DataProviderFactory.getDataProvider(resource);

        return dataProvider.getDataByIndex("1-2");
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
