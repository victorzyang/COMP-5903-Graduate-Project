@tag
Feature: Test Equivalence Partitioning example

  @generateTable
  @EQ x<0
  @EQ 0 <= x <= 3
  @EQ 3 <= x <= 5
  @calculateChristmasBonuses
  Scenario Outline: Calculate Christmas bonuses
    Given Duration of employment in years is <x>
    And Salary is <salary>
    Then Christmas bonus is <bonus>
    Examples:
      |x  |salary|bonus|
      |-2 |25000 |"rejected"|
      |2  |50000 |"0"  |
      |4  |50000 |"25000"|
      |7  |100000|"80000"|
      |12 |100000|"100000"|
      |72 |100000|"rejected"|