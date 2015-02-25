Feature: Sample Scenarios
    Three scenarios which are valid

  Scenario: Basic Navigation
    Given I am on the first page
    When I press the next page button
    Then I should be on the second page

  Scenario: Backwards Navigation
    Given I am on the second page
    When I press the previous page button
    Then I should be on the first page
  
  Scenario: Goto Navigation
    Given I am on the first page
    When I select goto page 3
    Then I should be on the third page
