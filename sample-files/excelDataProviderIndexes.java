@DataProvider(name = "excelDataProvider")
public Object[][] getExcelDataProvider() throws Exception {
  DataResource resource =
    new FileSystemResource("src/test/resources/testdata/MyDataFile.xls",
                           SimpleData.class);
  SeLionDataProvider dataProvider =
    DataProviderFactory.getDataProvider(resource);
  ExcelDataProvider excelDataProvider =
    ExcelDataProvider.class.cast(dataProvider);
  return excelDataProvider.getDataByIndex(new int[] {1, 2});
}