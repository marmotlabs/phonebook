Feature: Person management

  Background:
    Given these are the people already created
      | name  |
      | sofia |
      | yoyo  |

  Scenario: Add a person
    When I add a person with the name 'mara'
    Then the person is created
    And her name is 'mara'

  Scenario: Add a person fails
    When I add a person with the name 'sofia'
    Then the person is not created


  Scenario Outline: Add a person
    When I add a person with the name '<name>'
    Then the person is created
    And her name is '<name>'
    Examples:
      | name |
      | tim  |
      | yol  |
      | zui  |

  Scenario: Get all people
    When I get all people
    Then these are the people I get
      | name  |
      | sofia |
      | yoyo  |

  Scenario: Get all people after adding a new one
    When I add a person with the name 'ola'
    And I get all people
    Then these are the people I get
      | name  |
      | sofia |
      | yoyo  |
      | ola   |

  Scenario: Delete one person
    When I delete person with the name 'sofia'
    And I get all people
    Then these are the people I get
      | name |
      | yoyo |

  Scenario: Update one person
    When I update the person with the name 'sofia' by 'ana'
    And I get all people
    Then these are the people I get
      | name |
      | ana  |
      | yoyo |

    @phoneTpPerson
  Scenario Outline: Add a phone number to person
    When I add phone number '<phoneNumber>' to person '<name>'
    Then the phone number is added
    And '<name>' has now the new phoneNumber '<phoneNumber>'
    Examples:
      | name  | phoneNumber |
      | yoyo  | 444666      |
      | sofia | 555777      |
      | sofia | 34398429038 |
