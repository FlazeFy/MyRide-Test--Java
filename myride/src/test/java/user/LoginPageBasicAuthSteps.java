package user;

import core.DriverManager;
import core.TestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.user.LoginPageBasicAuth;
import org.testng.Assert;

public class LoginPageBasicAuthSteps {
    private final LoginPageBasicAuth loginPage = new LoginPageBasicAuth(DriverManager.getDriver());
    private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/data/login-data-test.xlsx";

    @Given("I open the login page")
    public void iOpenTheLoginPage() {
        String baseUrl = System.getProperty("baseUrl");
        DriverManager.getDriver().get(baseUrl + "/login");
    }

    @Then("I should see the section title {string}")
    public void iShouldSeeTheSectionTitle(String title) {
        Assert.assertTrue(loginPage.verifySectionTitle(title), "Section title should be displayed");
    }

    @Then("I should see the label {string}")
    public void iShouldSeeTheLabel(String label) {
        Assert.assertTrue(loginPage.verifyLabelDisplayed(label), "Label should be displayed");
    }

    @Then("I should see the submit button {string}")
    public void iShouldSeeTheSubmitButton(String text) {
        Assert.assertTrue(loginPage.verifySubmitButton(text), "Submit button should be displayed");
    }

    @When("I login using excel data {string}")
    public void iLoginUsingExcelData(String testCaseId) {
        Object[][] data = TestUtils.getTestData(FILE_PATH, "Sheet1");

        for (Object[] row : data) {
            String currentTestCaseId = row[0].toString();

            if (currentTestCaseId.equals(testCaseId)) {
                String email = row[1].toString();
                String password = row[2].toString();

                loginPage.login(email, password);
                break;
            }
        }
    }

    @Then("I should be redirected to the dashboard page")
    public void iShouldBeRedirectedToTheDashboardPage() {
        Assert.assertTrue(loginPage.verifyRedirectToDashboard(), "User should be redirected to dashboard page");
    }
}
