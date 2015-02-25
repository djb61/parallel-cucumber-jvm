Feature: Sample Scenarios
    A single scenario which is valid

  Scenario: Backwards Navigation
    Given I am on the second page
    When I press the previous page button
    Then I should be on the first page
