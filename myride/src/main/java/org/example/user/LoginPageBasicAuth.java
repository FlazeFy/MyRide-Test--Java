package org.example.user;
import org.example.core.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPageBasicAuth extends BasePage {
    // Selector
    @FindBy(xpath = "//h2")
    private WebElement sectionTitle;

    @FindBy(xpath = "//*[@id='form-login']")
    private WebElement loginForm;

    @FindBy(id = "username")
    private WebElement inputUsername;

    @FindBy(id = "password")
    private WebElement inputPassword;

    @FindBy(css = "#form-login a.btn-success")
    private WebElement submitButton;

    public LoginPageBasicAuth(WebDriver driver) {
        super(driver);
    }

    // Verify
    public boolean verifySectionTitle(String title) {
        waitForElementToBeVisible(sectionTitle);
        return sectionTitle.getText().equals(title);
    }

    public boolean verifyLabelDisplayed(String label) {
        return loginForm.getText().contains(label);
    }

    public boolean verifySubmitButton(String text) {
        waitForElementToBeVisible(submitButton);
        return submitButton.getText().equals(text);
    }

    public boolean verifyRedirectToDashboard() {
        waitForUrlContains("/dashboard");

        return driver.getCurrentUrl().contains("/dashboard");
    }

    // User Action
    public void inputEmail(String email) {
        waitForElementToBeVisible(inputUsername);
        inputUsername.clear();
        inputUsername.sendKeys(email);
    }

    public void inputPassword(String password) {
        waitForElementToBeVisible(inputPassword);
        inputPassword.clear();
        inputPassword.sendKeys(password);
    }

    public void clickSubmitButton() {
        submitButton.click();
    }

    public void login(String email, String password) {
        inputEmail(email);
        inputPassword(password);
        clickSubmitButton();
    }
}
