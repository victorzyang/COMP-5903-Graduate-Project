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
        return d1 + d2 + d3 + d4 + d5;
    }

}
