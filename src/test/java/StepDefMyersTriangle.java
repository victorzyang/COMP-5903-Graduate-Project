import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import junit.framework.TestCase;

public class StepDefMyersTriangle extends TestCase{

    int a, b, c;

    @Given("Sides of triangle are {int}, {int} and {int}")
    public void set_triangle_sides(int a, int b, int c){
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Then("Type of triangle is {string}")
    public void determine_triangle(String msg){
        String expected_result = "";

        if ((a >= b + c) || (b >= a + c) || (c >= a + b)) {
            expected_result = "not a triangle";
        } else if (a <= 0 || b <= 0 || c <= 0) {
            expected_result = "size < 1";
        } else if (a == b && a == c) {
            expected_result = "equilateral triangle";
        } else if ((a == b) || (a==c) || (b==c)) {
            expected_result = "isoceles triangle";
        } else {
            expected_result = "scalene triangle";
        }

        assertEquals(msg, expected_result);
    }

}
