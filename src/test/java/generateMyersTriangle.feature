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