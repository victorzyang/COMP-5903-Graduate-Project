@tag
Feature: Yahtzee scoring

  @Table | D1 | D2 | D3 | D4 | D5 | Box | Score |
  @PARAMETER 'Box' {'ONES', 'TWOS', 'THREES', 'FOURS', 'FIVES', 'SIXES', '3OfAKind', '4OfAKind', 'FULLHOUSE', 'SMALLSEQ', 'LARGESEQ', 'YAHTZEE', 'CHANCE'}
  @SET 5DICE {D1, D2, D3, D4, D5}
  @ConstrainedValue (x >= 1) & (x <= 6) for {D1, D2, D3, D4, D5}
  @CATEGORY 'Score1Five' 1COMBINATION(1,5) IN 5DICE 'Box' 'FIVES' 'Score' 5
  @CATEGORY 'Score2Fours' 1COMBINATION(2,4) IN 5DICE 'Box' 'FOURS' 'Score' 8
  @CATEGORY 'Score3Ones' 1COMBINATION(3,1) IN 5DICE 'Box' 'ONES' 'Score' 3
  @CATEGORY 'Score4Twos' 1COMBINATION(4,2) IN 5DICE 'Box' 'TWOS' 'Score' 8
  @CATEGORY 'Score5Sixes' 1COMBINATION(5,6) IN 5DICE 'Box' 'SIXES' 'Score' 30
  @CATEGORY 'Score2Threes' 1COMBINATION(2,3) IN 5DICE 'Box' 'THREES' 'Score' 6
  @CATEGORY 'Score3ofAKind' 1COMBINATION(3,6) IN 5DICE 'Box' '3OfAKind' 'Score' 'sumOf5Dice'
  @CATEGORY 'Score4ofAKind' 1COMBINATION(4,4) IN 5DICE 'Box' '4OfAKind' 'Score' 'sumOf5Dice'
  @CATEGORY 'FullHouse' 2COMBINATION(3,_,2,_) IN 5DICE 'Box' 'FULLHOUSE' 'Score' 25
  @CATEGORY 'SMALLSEQUENCE' 1COMBINATION(a,a+1,a+2,a+3,_) IN 5DICE 'Box' 'SMALLSEQ' 'Score' 30
  @CATEGORY 'LONGSEQUENCE' 1COMBINATION(a,a+1,a+2,a+3,a+4) IN 5DICE 'Box' 'LARGESEQ' 'Score' 40
  @CATEGORY 'Yahtzee' 1COMBINATION(5,3) IN 5DICE 'Box' 'YAHTZEE' 'Score' 50
  @CATEGORY 'Chance' RANDOM_COMBINATION() IN 5DICE 'Box' 'CHANCE' 'Score' 'sumOf5Dice'
  Scenario Outline: Determine Yahtzee scores
    Given Dice are <D1>, <D2>, <D3>, <D4> and <D5>
    When Category is <Box>
    Then Score is <Score>
    Examples:
