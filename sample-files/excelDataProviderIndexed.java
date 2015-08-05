@DataProvider(name = "excelDataProvider")
public Object[][] getExcelDataProvider() throws Exception {
  DataResource resource = 
    new FileSystemResource("src/test/resources/testdata/MyDataFile.xls",
                           SimpleData.class);
  SeLionDataProvider dataProvider =
    DataProviderFactory.getDataProvider(resource);
  return dataProvider.getDataByIndex("1-2");
}