@tag
Feature: Test Equivalence Partitioning example

  @Table | years | salary | bonus |
  @RANDOM salary BETWEEN 10000 AND 100000
  @Constraint (years >= 0) & (years <= 100)
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