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

  Scenario: Get all people after adding a new one
    Given these are the people already created
      | name  | age |
      | sofia | 22  |
      | yoyo  | 3   |
    When I add a person with the name 'coco'
    And I get all people
    Then these are the people I get
      | name  | age |
      | sofia | 22  |
      | yoyo  | 3   |
      | coco  | 8   |

  Scenario: Delete one person
    Given these are the people already created
      | name  | age |
      | sofia | 22  |
      | yoyo  | 3   |
    When I delete person with the name 'sofia'
    And I get all people
    Then these are the people I get
      | name  | age |
      | yoyo  | 3   |

  Scenario: Update one person
    Given these are the people already created
      | name  | age |
      | sofia | 22  |
      | yoyo  | 3   |
    When I update the person with the name 'sofia' by 'ana'
    And I get all people
    Then these are the people I get
      | name  | age |
      | ana   | 22  |
      | yoyo  | 3   |