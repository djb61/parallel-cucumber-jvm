Feature: Sample Scenarios
    A single scenario which is invalid

  Scenario: Backwards Navigation
    Given I am on the second page
    Wheen I press the previous page button
    Then I should be on the first page
