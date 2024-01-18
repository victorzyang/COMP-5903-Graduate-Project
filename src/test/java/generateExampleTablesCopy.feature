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
      | 1 | 46101 | 0 |
      | 3 | 46101 | 23050 |
      | 4 | 46101 | 23050 |
      | 5 | 46101 | 36880 |
      | 6 | 46101 | 36880 |
      | 7 | 46101 | 36880 |
      | 715354370 | 46101 | 46101 |