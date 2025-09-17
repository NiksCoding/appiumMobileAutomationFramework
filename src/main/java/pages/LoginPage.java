package pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);
    @AndroidFindBy(accessibility = "English")
    private WebElement englishLang;

    @AndroidFindBy(accessibility = "Arabic")
    private WebElement arabLang;

    @AndroidFindBy(accessibility = "Continue\nContinue")
    private WebElement continueBtn;

//    @AndroidFindBy(id = "//android.view.View[@content-desc='Enter Email Address']/android.widget.EditText")
//    @iOSXCUITFindBy(id = "username")
   // @AndroidFindBy(accessibility = "Enter Email Address")
    @AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.EditText\").instance(0)")
    private WebElement usernameField;

    @AndroidFindBy(accessibility = "Enter Password")
    @iOSXCUITFindBy(id = "password")
    private WebElement passwordField;

    @AndroidFindBy(accessibility = "Log In\nLog In")
    @iOSXCUITFindBy(id = "loginButton")
    private WebElement loginButton;

    @AndroidFindBy(accessibility = "Continue\nContinue")
    private WebElement continueLoginBtn;

    @AndroidFindBy(id = "com.example.app:id/error_message")
    @iOSXCUITFindBy(id = "errorMessage")
    private WebElement errorMessage;

    @AndroidFindBy(accessibility = "Football Trivia")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name='Welcome']")
    private WebElement homeScreen;

    @AndroidFindBy(id = "com.example.app:id/forgot_password")
    @iOSXCUITFindBy(id = "forgotPassword")
    private WebElement forgotPasswordLink;

    public LoginPage() {
        super();
    }

    /**
     * Enter username
     */
    public LoginPage enterUsername(String username) {
        sendTextToElement(usernameField, username, "Username Field");
        return this;
    }

    /**
     * Enter password
     */
    public LoginPage enterPassword(String password) {
        sendTextToElement(passwordField, password, "Password Field");
        return this;
    }



    /**
     * Check if login is successful (welcome message is displayed)
     */
    public boolean isLoginSuccessful() {
        return isElementDisplayed(homeScreen, "Football Trivia");
    }

    /**
     * Get error message text
     */
    public String getErrorMessage() {
        return getTextFromElement(errorMessage, "Error Message");
    }

    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage, "Error Message");
    }

    /**
     * Click forgot password link
     */
    public void clickForgotPassword() {
        clickElement(forgotPasswordLink, "Forgot Password Link");
    }

    /**
     * Check if username field is displayed
     */
    public boolean isUsernameFieldDisplayed() {
        return isElementDisplayed(usernameField, "Username Field");
    }

    /**
     * Check if password field is displayed
     */
    public boolean isPasswordFieldDisplayed() {
        return isElementDisplayed(passwordField, "Password Field");
    }

    /**
     * Check if login button is enabled
     */
    public void selectLanguage(){
        try{
            waitForElementToBeClickable(englishLang);
            englishLang.click();
            log.info("Selected english language");
            waitForElementToBeClickable(continueBtn);
            continueBtn.click();
        }catch (Exception e){
            log.info("Exception - "+e);
        }
    }
    public boolean isLoginButtonEnabled() {
        try {
            waitForElementToBeVisible(loginButton);
            return loginButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * Click login button
     */
    public void clickLoginButton() {
        clickElement(loginButton, "Login Button");
    }

    /**
     * Perform complete login
     */
    public void clickContinueToLogin(){
        clickElement(continueLoginBtn, "Continue Login Button");
    }
    public void performLogin(String username, String password) {
        clickLoginButton();
        enterUsername(username);
        enterPassword(password);
        continueLoginBtn.click();

    }
    /**
     * Clear all input fields
     */
    public void clearFields() {
        if (isElementDisplayed(usernameField, "Username Field")) {
            usernameField.clear();
        }
        if (isElementDisplayed(passwordField, "Password Field")) {
            passwordField.clear();
        }
    }
}
