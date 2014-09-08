@DataProvider(name = "IReadExcelSheets")
public Object[][] myExcelsheetReader() throws Exception {
  SimpleExcelDataProvider dataProvider =
    new SimpleExcelDataProvider("src/test/resources/testdata/MyDataFile.xls");
  MyDataSheet dataRow = new MyDataSheet();
  return dataProvider.getExcelRows(dataRow, "1-3");
}