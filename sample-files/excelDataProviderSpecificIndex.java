@DataProvider(name = "excelDataProvider")
public Object[][] getExcelDataProvider() throws Exception {
  DataResource resource =
    new FileSystemResource("src/test/resources/testdata/MyDataFile.xls",
                            SimpleData.class);
  SeLionDataProvider dataProvider =
    DataProviderFactory.getDataProvider(resource);
  ExcelDataProvider excelDataProvider =
    ExcelDataProvider.class.cast(dataProvider);
  SimpleData simpleData =
    SimpleData.class.cast(excelDataProvider.getSingleExcelRow(2));
  Object[][] data = new Object[1][1];
  data[0][0] = simpleData;
  return data;
}