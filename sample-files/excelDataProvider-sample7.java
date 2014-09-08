@DataProvider(name = "IReadExcelSheets")
public Object[][] myExcelsheetReader() throws Exception {
  SimpleExcelDataProvider dataProvider =
    new SimpleExcelDataProvider("src/test/resources/testdata/MyDataFile.xls");
  MyDataSheet dataRow = new MyDataSheet();

  ArrayList<String> keyList = new ArrayList<String>();
  keyList.add("a");
  keyList.add("c");
  keyList.add("d");

  String[] keyArray = (String[]) keyList.toArray();

  return dataProvider.getExcelRows(dataRow, keyArray);
}