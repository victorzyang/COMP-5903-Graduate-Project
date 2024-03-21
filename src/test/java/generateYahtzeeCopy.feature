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
  @CATEGORY 'Score3ofAKind' 1COMBINATION(3,6) IN 5DICE 'Box' '3OfAKind' 'Score' 'scoreSumOfFiveDice' in 'YahtzeeGame' with parameters [D1: Integer, D2: Integer, D3: Integer, D4: Integer, D5: Integer, Box: String]
  @CATEGORY 'Score4ofAKind' 1COMBINATION(4,4) IN 5DICE 'Box' '4OfAKind' 'Score' 'scoreSumOfFiveDice' in 'YahtzeeGame' with parameters [D1: Integer, D2: Integer, D3: Integer, D4: Integer, D5: Integer, Box: String]
  @CATEGORY 'FullHouse' 2COMBINATION(3,_,2,_) IN 5DICE 'Box' 'FULLHOUSE' 'Score' 25
  @CATEGORY 'SMALLSEQUENCE' 1COMBINATION(a,a+1,a+2,a+3,_) IN 5DICE 'Box' 'SMALLSEQ' 'Score' 30
  @CATEGORY 'LONGSEQUENCE' 1COMBINATION(a,a+1,a+2,a+3,a+4) IN 5DICE 'Box' 'LARGESEQ' 'Score' 40
  @CATEGORY 'Yahtzee' 1COMBINATION(5,3) IN 5DICE 'Box' 'YAHTZEE' 'Score' 50
  @CATEGORY 'Chance' RANDOM_COMBINATION() IN 5DICE 'Box' 'CHANCE' 'Score' 'scoreSumOfFiveDice' in 'YahtzeeGame' with parameters [D1: Integer, D2: Integer, D3: Integer, D4: Integer, D5: Integer, Box: String]
  @CATEGORY 'Score0Five' 1COMBINATION(0,5) IN 5DICE 'Box' 'FIVES' 'Score' 0
  @CATEGORY 'Score3ofAKind' 2COMBINATION(2,_,2,_) IN 5DICE 'Box' '3OfAKind' 'Score' 'scoreSumOfFiveDice' in 'YahtzeeGame' with parameters [D1: Integer, D2: Integer, D3: Integer, D4: Integer, D5: Integer, Box: String]
  @CATEGORY 'Score4ofAKind' 1COMBINATION(2,6) IN 5DICE 'Box' '4OfAKind' 'Score' 'scoreSumOfFiveDice' in 'YahtzeeGame' with parameters [D1: Integer, D2: Integer, D3: Integer, D4: Integer, D5: Integer, Box: String]
  @CATEGORY 'FullHouse' 2COMBINATION(4,_,1,_) IN 5DICE 'Box' 'FULLHOUSE' 'Score' 0
  @CATEGORY 'SMALLSEQUENCE' 1COMBINATION(a,a,a+1,a+2,a+2) IN 5DICE 'Box' 'SMALLSEQ' 'Score' 0
  @CATEGORY 'SMALLSEQUENCE' 1COMBINATION(a,a+1,a+1,a+2,a+3) IN 5DICE 'Box' 'SMALLSEQ' 'Score' 0
  @CATEGORY 'LONGSEQUENCE' 1COMBINATION(a,a+1,a+2,a+3,a+3) IN 5DICE 'Box' 'LARGESEQ' 'Score' 0
  @CATEGORY 'Yahtzee' 2COMBINATION(4,_,1,_) IN 5DICE 'Box' 'YAHTZEE' 'Score' 0
  Scenario Outline: Determine Yahtzee scores
    Given Dice are <D1>, <D2>, <D3>, <D4> and <D5>
    When Category is <Box>
    Then Score is <Score>
    Examples:
      | D1 | D2 | D3 | D4 | D5 | Box | Score |
      | 2 | 4 | 4 | 5 | 2 | FIVES | 5 |
      | 4 | 3 | 1 | 4 | 2 | FOURS | 8 |
      | 3 | 1 | 1 | 1 | 2 | ONES | 3 |
      | 4 | 2 | 2 | 2 | 2 | TWOS | 8 |
      | 6 | 6 | 6 | 6 | 6 | SIXES | 30 |
      | 1 | 4 | 1 | 3 | 3 | THREES | 6 |
      | 3 | 6 | 1 | 6 | 6 | 3OfAKind | 22 |
      | 5 | 4 | 4 | 4 | 4 | 4OfAKind | 21 |
      | 1 | 1 | 1 | 3 | 3 | FULLHOUSE | 25 |
      | 3 | 4 | 5 | 6 | 3 | SMALLSEQ | 30 |
      | 2 | 3 | 4 | 5 | 6 | LARGESEQ | 40 |
      | 3 | 3 | 3 | 3 | 3 | YAHTZEE | 50 |
      | 1 | 4 | 3 | 4 | 4 | CHANCE | 16 |
      | 4 | 4 | 2 | 1 | 3 | FIVES | 0 |
      | 4 | 2 | 5 | 2 | 4 | 3OfAKind | 0 |
      | 4 | 6 | 5 | 6 | 3 | 4OfAKind | 0 |
      | 3 | 1 | 3 | 3 | 3 | FULLHOUSE | 0 |
      | 1 | 1 | 2 | 3 | 3 | SMALLSEQ | 0 |
      | 2 | 3 | 3 | 4 | 5 | SMALLSEQ | 0 |
      | 2 | 3 | 4 | 5 | 5 | LARGESEQ | 0 |
      | 2 | 2 | 2 | 2 | 1 | YAHTZEE | 0 |