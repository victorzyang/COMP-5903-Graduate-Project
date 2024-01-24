@tag
Feature: Myer’s triangle problem

  @Table | a | b | c | msg |
  @LABEL INVALID_TRIANGLE
  @IF (a >= b + c) THEN msg = ‘not a triangle’
  @IF (b >= a + c) THEN msg = ‘not a triangle’
  @IF (c >= a + b) THEN msg = ‘not a triangle’
  @END_LABEL INVALID_TRIANGLE
  @IF (a <= 0) THEN msg = ‘size < 1’
  @IF (b <= 0) THEN msg = ‘size < 1’
  @IF (c <= 0) THEN msg = ‘size < 1’
  @IF (a >= b + c) THEN msg = ‘not a triangle’
  @IF (b >= a + c) THEN msg = ‘not a triangle’
  @IF (c >= a + b) THEN msg = ‘not a triangle’
  @IF (a == b == c) THEN msg = ‘equilateral triangle’
  @IF (   ( (a==b) || (a==c) || (b==c)) &&  (!(a == b == c))  &&  (! INVALID_TRIANGLE)       ) THEN msg = ‘isoceles triangle’
  @IF (      ( (a!=b) && (a!=c) && (b!=c)) &&  (! INVALID_TRIANGLE)          ) THEN msg = ‘scalene triangle’
  Scenario Outline: Determine type of triangle
    Given Sides of triangle are <a>, <b> and <c>
    Then Type of triangle is <msg>
    Examples:
      | a | b | c | msg |
      | 0 | 25 | 26 | size < 1 |
      | 2 | 0 | 5 | size < 1  |
      | 1 | 244 | 0 | size < 1  |
      | -5 | 25 | 26 | size < 1 |
      | 2 | -5 | 5 | size < 1  |
      | 1 | 244 | -5 | size < 1  |
      | 5 | 2 | 3 | not a triangle  |
      | 2 | 5 | 3 | not a triangle  |
      | 2 | 3 | 5 | not a triangle  |
      | 8 | 2 | 3 | not a triangle  |
      | 2 | 8 | 3 | not a triangle  |
      | 2 | 3 | 8 | not a triangle  |
      | 1 | 1 | 1 | equilateral triangle  |
      | 3 | 3 | 4 | isoceles triangle  |
      | 3 | 4 | 3 | isoceles triangle  |
      | 4 | 3 | 3 | isoceles triangle  |
      | 5 | 3 | 4 | scalene triangle  |