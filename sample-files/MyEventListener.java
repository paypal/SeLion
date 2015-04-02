package qa.tests;

import com.paypal.selion.platform.grid.EventListener;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.DriverCommand;
import org.testng.Reporter;

/**
 *
 */
public class MyEventListener implements EventListener {
  /**
   * This method will be called by {@link EventFiringCommandExecutor} BEFORE executing each selenium command.
   *
   * @param command - A {@link Command} that represents the command being executed by
   *                Selenium.
   *                In order to filter out specific commands you may extract the actual command via
   *                {@link Command#getName()} and then compare it with the predefined set of
   *                commands available as strings in {@link DriverCommand}
   */
  @Override
  public void beforeEvent(Command command) {
    if (command.getName().equals(DriverCommand.CLICK)) {
      //We are interested only in the click events here.
      Reporter.log("A Click operation is about to be performed");
    }
  }

  /**
   * This method will be called by {@link EventFiringCommandExecutor} AFTER executing each selenium command.
   *
   * @param command - A {@link Command} that represents the command being executed by
   *                Selenium.
   *                In order to filter out specific commands you may extract the actual command via
   *                {@link Command#getName()} and then compare it with the predefined set of
   *                commands available as strings in {@link DriverCommand}
   */
  @Override
  public void afterEvent(Command command) {
    if (command.getName().equals(DriverCommand.SEND_KEYS_TO_ELEMENT)) {
      //Here we are interested only in the send keys event (viz., type action)
      Reporter.log("Completed typing of text in the current element.");
    }

  }
}
