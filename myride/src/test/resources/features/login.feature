Feature: Login Page Basic Auth

  Scenario: User can log in successfully
    Given I open the login page
    Then I should see the section title "You Are At MyRide Apps"
    And I should see the label "Email / Username"
    And I should see the label "Password"
    And I should see the submit button "Enter the Garage"
    When I login using excel data "SUCCESS_LOGIN"
    Then I should be redirected to the dashboard page