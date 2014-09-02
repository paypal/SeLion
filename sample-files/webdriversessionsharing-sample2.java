@Test(groups = {"sessionSharingWebTests"},dependsOnMethods="testMainFlow")
@WebTest(sessionName="login-flow",openNewSession=false)
public void testSendMoneyFlow() {
    //Will use the same session to open the browser instead of opening a new one.
}