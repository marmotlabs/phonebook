Feature: Person management

  Scenario Outline: Add a person
    When I add a person with the name '<name>'
    Then the person is created
    And her name is '<name>'
    Examples:
      | name  |
      | coco  |
      | yoyo  |
      | sofia |

  Scenario: Get all people
    Given these are the people already created
      | name  | age |
      | sofia | 22  |
      | yoyo  | 3   |
    When I get all people
    Then these are the people I get
      | name  | age |
      | sofia | 22  |
      | yoyo  | 3   |