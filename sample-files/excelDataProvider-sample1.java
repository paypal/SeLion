@DataProvider(name = "IReadExcelSheets")
public Object[][] myExcelsheetReader() throws Exception {
  ExcelDataProvider dataProvider =
    new ExcelDataProvider("src/test/resources/testdata/MyDataFile.xls");
  MyDataSheet dataRow = new MyDataSheet();

  return dataProvider.getAllExcelRows(dataRow);
}