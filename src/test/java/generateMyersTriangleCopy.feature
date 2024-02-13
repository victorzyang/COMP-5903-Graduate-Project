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
      | -10 | 10 | 5 | size < 1 |
      | 0 | 5 | 5 | size < 1 |
      | 4 | -1 | 7 | size < 1 |
      | 8 | 0 | 6 | size < 1 |
      | 2 | 4 | -9 | size < 1 |
      | 3 | 5 | 0 | size < 1 |
      | 16 | 8 | 8 | not a triangle |
      | 24 | 8 | 8 | not a triangle |
      | 1 | 4 | 3 | not a triangle |
      | 1 | 8 | 6 | not a triangle |
      | 10 | 6 | 16 | not a triangle |
      | 5 | 8 | 18 | not a triangle |
      | 10 | 10 | 10 | equilateral triangle |
      | 9 | 9 | 6 | isoceles triangle |
      | 5 | 2 | 5 | isoceles triangle |
      | 2 | 3 | 3 | isoceles triangle |
      | 6 | 2 | 7 | scalene triangle |