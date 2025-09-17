package stepDefinitions;

;

import pages.LoginPage;
import reporting.ExtentReportManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class LoginSteps {
    private static final Logger logger = LoggerFactory.getLogger(LoginSteps.class);
    private LoginPage loginPage;

    public LoginSteps() {
        this.loginPage = new LoginPage();
    }

    @Given("the user is on the login screen")
    public void theUserIsOnTheLoginScreen() {
        try {

            Assert.assertTrue(loginPage.isUsernameFieldDisplayed(), "Username field should be displayed");
            Assert.assertTrue(loginPage.isPasswordFieldDisplayed(), "Password field should be displayed");
            ExtentReportManager.logPass("User is on the login screen");
            logger.info("User is on the login screen");
        } catch (AssertionError e) {
            ExtentReportManager.logFail("User is not on the login screen: " + e.getMessage());
            logger.error("User is not on the login screen", e);
            throw e;
        }
    }

    @When("the user enters username {string}")
    public void theUserEntersUsername(String username) {
        try {
            loginPage.clickLoginButton();
            loginPage.enterUsername(username);
            ExtentReportManager.logPass("Entered username: " + username);
            logger.info("Entered username: {}", username);
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to enter username: " + e.getMessage());
            logger.error("Failed to enter username", e);
            throw e;
        }
    }

    @And("the user enters password {string}")
    public void theUserEntersPassword(String password) {
        try {
            loginPage.enterPassword(password);
            ExtentReportManager.logPass("Entered password");
            logger.info("Entered password");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to enter password: " + e.getMessage());
            logger.error("Failed to enter password", e);
            throw e;
        }
    }

    @And("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        try {
            //Assert.assertTrue(loginPage.isLoginButtonEnabled(), "Login button should be enabled");
            loginPage.clickContinueToLogin();
            ExtentReportManager.logPass("Clicked login button");
            logger.info("Clicked login button");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to click login button: " + e.getMessage());
            logger.error("Failed to click login button", e);
            throw e;
        }
    }

    @Then("the user should be logged in successfully")
    public void theUserShouldBeLoggedInSuccessfully() {
        try {
            Assert.assertTrue(loginPage.isLoginSuccessful(), "Login should be successful");
            ExtentReportManager.logPass("User logged in successfully");
            logger.info("User logged in successfully");
        } catch (AssertionError e) {
            ExtentReportManager.logFail("Login was not successful: " + e.getMessage());
            logger.error("Login was not successful", e);
            throw e;
        }
    }

    @Then("the user should see an error message {string}")
    public void theUserShouldSeeAnErrorMessage(String expectedMessage) {
        try {
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
            String actualMessage = loginPage.getErrorMessage();
            Assert.assertEquals(actualMessage, expectedMessage, "Error message should match expected message");
            ExtentReportManager.logPass("Error message displayed correctly: " + actualMessage);
            logger.info("Error message displayed correctly: {}", actualMessage);
        } catch (AssertionError e) {
            ExtentReportManager.logFail("Error message verification failed: " + e.getMessage());
            logger.error("Error message verification failed", e);
            throw e;
        }
    }

    @When("the user performs login with username {string} and password {string}")
    public void theUserPerformsLoginWithUsernameAndPassword(String username, String password) {
        try {
            loginPage.performLogin(username, password);
            ExtentReportManager.logPass("Performed login with username: " + username);
            logger.info("Performed login with username: {}", username);
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to perform login: " + e.getMessage());
            logger.error("Failed to perform login", e);
            throw e;
        }
    }

    @And("the user clicks forgot password link")
    public void theUserClicksForgotPasswordLink() {
        try {
            loginPage.clickForgotPassword();
            ExtentReportManager.logPass("Clicked forgot password link");
            logger.info("Clicked forgot password link");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to click forgot password link: " + e.getMessage());
            logger.error("Failed to click forgot password link", e);
            throw e;
        }
    }

    @And("the user clears all input fields")
    public void theUserClearsAllInputFields() {
        try {
            loginPage.clearFields();
            ExtentReportManager.logPass("Cleared all input fields");
            logger.info("Cleared all input fields");
        } catch (Exception e) {
            ExtentReportManager.logFail("Failed to clear input fields: " + e.getMessage());
            logger.error("Failed to clear input fields", e);
            throw e;
        }
    }

    @Then("the login button should be {string}")
    public void theLoginButtonShouldBe(String state) {
        try {
            boolean isEnabled = loginPage.isLoginButtonEnabled();
            boolean expectedState = "enabled".equalsIgnoreCase(state);

            Assert.assertEquals(isEnabled, expectedState,
                    "Login button should be " + state + " but was " + (isEnabled ? "enabled" : "disabled"));

            ExtentReportManager.logPass("Login button is " + state + " as expected");
            logger.info("Login button is {} as expected", state);
        } catch (AssertionError e) {
            ExtentReportManager.logFail("Login button state verification failed: " + e.getMessage());
            logger.error("Login button state verification failed", e);
            throw e;
        }
    }

    @Given("user selects the language")
    public void userSelectsTheLanguage() {
        loginPage.selectLanguage();
    }
}
