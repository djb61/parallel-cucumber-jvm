@IncludedTag
Feature: Sample Scenario
  A single scenario which is valid

  @ExcludedTag
  Scenario: Basic Navigation
    Given I am on the first page
    When I press the next page button
    Then I should be on the second page
