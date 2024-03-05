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
  @IF ( ( (a==b) || (a==c) || (b==c)) && (!(a == b == c)) && (! INVALID_TRIANGLE) ) THEN msg = ‘isoceles triangle’
  @IF ( ( (a!=b) && (a!=c) && (b!=c)) && (! INVALID_TRIANGLE) ) THEN msg = ‘scalene triangle’
  Scenario Outline: Determine type of triangle
    Given Sides of triangle are <a>, <b> and <c>
    Then Type of triangle is <msg>
    Examples:
      | a | b | c | msg |
      | -2 | 9 | 7 | size < 1 |
      | 0 | 4 | 2 | size < 1 |
      | 10 | -2 | 5 | size < 1 |
      | 4 | 0 | 3 | size < 1 |
      | 5 | 10 | -5 | size < 1 |
      | 9 | 3 | 0 | size < 1 |
      | 10 | 2 | 8 | not a triangle |
      | 18 | 4 | 9 | not a triangle |
      | 3 | 12 | 9 | not a triangle |
      | 10 | 26 | 6 | not a triangle |
      | 5 | 6 | 11 | not a triangle |
      | 4 | 7 | 13 | not a triangle |
      | 7 | 7 | 7 | equilateral triangle |
      | 4 | 4 | 3 | isoceles triangle |
      | 7 | 6 | 7 | isoceles triangle |
      | 8 | 10 | 10 | isoceles triangle |
      | 9 | 8 | 7 | scalene triangle |