Feature: Sample Scenarios
    Two scenarios which are valid

  Scenario: Basic Navigation
    Given I am on the first page
    When I press the next page button
    Then I should be on the second page

  Scenario: Backwards Navigation
    Given I am on the second page
    When I press the previous page button
    Then I should be on the first page
