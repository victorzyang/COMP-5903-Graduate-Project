import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;

public class YahtzeeGame {

    int d1, d2, d3, d4, d5;
    String box;

    public YahtzeeGame(int d1, int d2, int d3, int d4, int d5, String box) {
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.d4 = d4;
        this.d5 = d5;

        this.box = box;
    }

    public int scoreSumOfFiveDice(){
        ArrayList<Integer> dice = new ArrayList<>();
        dice.add(d1);
        dice.add(d2);
        dice.add(d3);
        dice.add(d4);
        dice.add(d5);

        int[] occurrencePerDie = {0, 0, 0, 0, 0, 0};

        for (int i = 0; i < dice.size(); i++) {
            switch (dice.get(i)) {
                case 1:
                    System.out.println("Value of die is 1");
                    occurrencePerDie[0] = occurrencePerDie[0] + 1;
                    break;
                case 2:
                    System.out.println("Value of die is 2");
                    occurrencePerDie[1] = occurrencePerDie[1] + 1;
                    break;
                case 3:
                    System.out.println("Value of die is 3");
                    occurrencePerDie[2] = occurrencePerDie[2] + 1;
                    break;
                case 4:
                    System.out.println("Value of die is 4");
                    occurrencePerDie[3] = occurrencePerDie[3] + 1;
                    break;
                case 5:
                    System.out.println("Value of die is 5");
                    occurrencePerDie[4] = occurrencePerDie[4] + 1;
                    break;
                case 6:
                    System.out.println("Value of die is 6");
                    occurrencePerDie[5] = occurrencePerDie[5] + 1;
                    break;
            }
        }

        System.out.println("Printing out occurrencePerDie array");
        for (int o = 0; o < occurrencePerDie.length; o++) {
            System.out.println("occurrencePerDie at index " + o + " is: " + occurrencePerDie[o]);
        }

        if (box.equals("3OfAKind")) {
            boolean thereAre3OfAKind = false;

            for (int o = 0; o < occurrencePerDie.length; o++) {
                if (occurrencePerDie[o] >= 3) {
                    thereAre3OfAKind = true;
                    break;
                }
            }

            if (thereAre3OfAKind) {
                return d1 + d2 + d3 + d4 + d5;
            } else {
                return 0;
            }
        } else if (box.equals("4OfAKind")) {
            boolean thereAre4OfAKind = false;

            for (int o = 0; o < occurrencePerDie.length; o++) {
                if (occurrencePerDie[o] >= 4) {
                    thereAre4OfAKind = true;
                    break;
                }
            }

            if (thereAre4OfAKind) {
                return d1 + d2 + d3 + d4 + d5;
            } else {
                return 0;
            }
        } else {
            return d1 + d2 + d3 + d4 + d5;
        }
    }

}
