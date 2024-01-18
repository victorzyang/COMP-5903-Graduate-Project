import io.cucumber.java.en.Given; //If I use my own Cucumber, then I use my own imports
import io.cucumber.java.en.Then; //Alternatively, can modify what 'Given' and 'Then' does
import io.cucumber.java.en.When;
import junit.framework.TestCase;

public class StepDefEquivalencePartitioning extends TestCase{

    int numOfYears, salary;

    @Given("Duration of employment in years is {int}")
    public void years_of_employment_is(int /*Integer*/ x){
        numOfYears = x;
    }

    @Given("Salary is {int}")
    public void salary_is(int /*Integer*/ salary){
        this.salary = salary;
    }

    @Then("Christmas bonus is {string}")
    public void compute_results(String bonus){
        String expected_result = "";
        int expected_bonus;

        if (numOfYears < 0 || numOfYears > 70) {
            expected_result = "rejected";
        } else if (numOfYears >= 0 && numOfYears <= 3) {
            expected_result = Integer.toString(0);
        } else if (numOfYears > 3 && numOfYears <= 5) {
            expected_bonus = salary / 2;
            expected_result = Integer.toString(expected_bonus);
        } else if (numOfYears > 5 && numOfYears <= 8) {
            expected_bonus = (salary * 4) / 5;
            expected_result = Integer.toString(expected_bonus);
        } else {
            expected_bonus = salary;
            expected_result = Integer.toString(expected_bonus);
        }

        assertEquals(bonus, expected_result);
    }

}
