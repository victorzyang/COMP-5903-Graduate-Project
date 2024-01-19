@tag
Feature: Test Equivalence Partitioning example

  @Table | years | salary | bonus |
  @RANDOM salary BETWEEN 10000 AND 100000
  @BVA
  @IF (years < 3) THEN bonus = 0
  @IF ((years >= 3) & (years <5)) THEN bonus = salary * 0.5
  @IF ((years >= 5) & (years <8)) THEN bonus = salary * 0.8
  @IF (years >= 8) THEN bonus = salary
  Scenario Outline: Calculate Christmas bonuses
    Given Duration of employment in years is <years>
    And Salary is <salary>
    Then Christmas bonus is <bonus>
    Examples:
      | years | salary | bonus |
      | 0 | 22135 | 0 |
      | 3 | 22135 | 11067 |
      | 4 | 22135 | 11067 |
      | 5 | 22135 | 17708 |
      | 6 | 22135 | 17708 |
      | 7 | 22135 | 17708 |
      | 108170190 | 22135 | 22135 |