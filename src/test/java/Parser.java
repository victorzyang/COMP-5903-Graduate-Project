import io.cucumber.java.sl.In;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Parser {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter input file path: ");
        String inputFilePath = scanner.next();

        System.out.println("Enter output file path: ");
        String outputFilePath = scanner.next();

        String stringToBeWritten = "";
        int numOfTestCases = 0;

        try {
            File myObj = new File(inputFilePath);
            //File myObj = new File("src/test/java/generateExampleTables.feature"/*"constraints.txt"*/);
            Scanner myReader = new Scanner(myObj); //Maybe use FileReader instead of Scanner (?)
            //FileWriter myWriter = new FileWriter("src/test/java/generateExampleTables.feature");
            //TODO: need to write to NEW file?

            //TODO: have a boolean variable that checks if we're currently on a scenario
            boolean checking_scenario_outline = false;

            ArrayList<String> table_column_values = new ArrayList<>();
            boolean[] is_table_column_value_fixed = null;
            int[] listOfFixedValues = null;

            String table_string = "      ";

            boolean generateAtBoundaryValues = false;

            ArrayList<ArrayList<Integer>> listOfTestData = null;

            boolean[] is_table_column_value_constrained = null;
            ArrayList<int[]> listOfConstraints = null;

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] arrOfStr = data.trim().split(" ");

                stringToBeWritten += data;
                stringToBeWritten += "\n";

                if (arrOfStr[0].equals("@Table")) {
                    checking_scenario_outline = true;

                    int num_of_table_columns = 0;

                    for (int i = 1; i < arrOfStr.length; i++) {
                        table_string += arrOfStr[i];
                        if (i != arrOfStr.length - 1) {
                            table_string += " ";
                        }

                        if (!arrOfStr[i].equals("|")) {
                            num_of_table_columns++;
                        }
                    }

                    System.out.println("The number of table columns is: " + num_of_table_columns);
                    System.out.println("table_string is: " + table_string);

                    is_table_column_value_fixed = new boolean[num_of_table_columns];
                    listOfFixedValues = new int[num_of_table_columns];
                    listOfTestData = new ArrayList<ArrayList<Integer>>(num_of_table_columns);
                    is_table_column_value_constrained = new boolean[num_of_table_columns];
                    listOfConstraints = new ArrayList<int[]>(num_of_table_columns);

                    System.out.println("Checking all elements in table_column_values arraylist");
                    for (int i = 1; i < arrOfStr.length; i++) {
                        if (!arrOfStr[i].equals("|")) {
                            System.out.println("arrOfStr at index " + i + " is: " + arrOfStr[i]);
                            table_column_values.add(arrOfStr[i]);
                            listOfTestData.add(new ArrayList<Integer>());
                            listOfConstraints.add(new int[2]);
                        }
                    }

                    //System.out.println("Length of listOfTestData is: " + listOfTestData.size());

                    for (int i = 0; i < is_table_column_value_fixed.length; i++) {
                        is_table_column_value_fixed[i] = false;
                        listOfFixedValues[i] = 0;
                        is_table_column_value_constrained[i] = false;
                    }
                } else if (arrOfStr[0].equals("@RANDOM")) {
                    if (arrOfStr[2].equals("BETWEEN")) {
                        int random_int;

                        int int1 = Integer.parseInt(arrOfStr[3]);
                        int int2 = Integer.parseInt(arrOfStr[5]);

                        random_int = (int) ((Math.random() * (int2 - int1)) + int1);

                        //System.out.println("random_int is: " + random_int);

                        for (int i = 0; i < table_column_values.size(); i++) {
                            if (table_column_values.get(i).equals(arrOfStr[1])) {
                                is_table_column_value_fixed[i] = true; //list of booleans that checks if a value is fixed or not
                                listOfFixedValues[i] = random_int;
                            }
                        }

                    }
                } else if (arrOfStr[0].equals("@Constraint")) {

                    String firstStringAfterAnnotation = arrOfStr[1];
                    String tableColumnName = firstStringAfterAnnotation.replace("(", "");

                    String thirdStringAfterAnnotation = arrOfStr[3];
                    int int1 = Integer.parseInt(thirdStringAfterAnnotation.replace(")", ""));

                    String lastString = arrOfStr[7];
                    int int2 = Integer.parseInt(lastString.replace(")", ""));

                    String symbol1 = arrOfStr[2];

                    String symbol2 = arrOfStr[6];

                    for (int i = 0; i < table_column_values.size(); i++) {
                        if (table_column_values.get(i).equals(tableColumnName)) {
                            is_table_column_value_constrained[i] = true;

                            if (symbol1.equals(">=")) {
                                listOfConstraints.get(i)[0] = int1;
                            }

                            if (symbol2.equals("<=")) {
                                listOfConstraints.get(i)[1] = int2;
                            }

                        }
                    }

                    /*for (int i = 0; i < listOfConstraints.size(); i++) {
                        System.out.println("listOfConstraints[i][0]: " + listOfConstraints.get(i)[0]);
                        System.out.println("listOfConstraints[i][1]: " + listOfConstraints.get(i)[1]);
                    }*/

                } else if (arrOfStr[0].equals("@BVA")) {
                    generateAtBoundaryValues = true;
                } else if (arrOfStr[0].equals("@IF")) {
                    //System.out.println("generateAtBoundaryValues is " + generateAtBoundaryValues);

                    int arrOfStrLengthFromIfToThen = 0;

                    for (int i = 1; i < arrOfStr.length; i++) {
                        if (arrOfStr[i].equals("THEN")) {
                            break;
                        } else {
                            arrOfStrLengthFromIfToThen++;
                        }
                    }

                    //System.out.println("arrOfStrLengthFromIfToThen is " + arrOfStrLengthFromIfToThen);

                    String firstStringAfterAnnotation = arrOfStr[1];
                    String tableColumnName = firstStringAfterAnnotation.replace("(", "");

                    String tableColumnName2;

                    //System.out.println("tableColumnName is " + tableColumnName);

                    if (arrOfStr.length == 8) { //just generate a single value
                        numOfTestCases++;

                        tableColumnName2 = arrOfStr[5];

                        for (int i = 0; i < table_column_values.size(); i++) {
                            if (table_column_values.get(i).equals(tableColumnName)) {
                                String boundaryString = arrOfStr[3].replace(")", "");

                                if (arrOfStr[2].equals("<")) {
                                    listOfTestData.get(i).add((int) (Math.random() * (Integer.parseInt(boundaryString) - 1)));
                                } else if (arrOfStr[2].equals(">=")) {
                                    Random random = new Random();
                                    if (is_table_column_value_constrained[i]) {
                                        int boundaryInt = Integer.parseInt(boundaryString);
                                        int number = random.nextInt(listOfConstraints.get(i)[1] - boundaryInt) + boundaryInt;
                                        listOfTestData.get(i).add(number);
                                    } else {
                                        int number = Math.abs(random.nextInt());
                                        listOfTestData.get(i).add(number + Integer.parseInt(boundaryString));
                                    }
                                }

                                break;
                            }
                        }

                        for (int i = 0; i < table_column_values.size(); i++) {
                            if (table_column_values.get(i).equals(tableColumnName2)) {
                                boolean lastStringIsAnInt = true;

                                for (int j = 0; j < table_column_values.size(); j++) {
                                    if (table_column_values.get(j).equals(arrOfStr[7])) { //check if arrOfStr[7] is an int or not
                                        lastStringIsAnInt = false;
                                        break;
                                    }
                                }

                                if (lastStringIsAnInt) {
                                    listOfTestData.get(i).add(Integer.parseInt(arrOfStr[7]));
                                } else {
                                    for (int j = 0; j < table_column_values.size(); j++) {
                                        if (table_column_values.get(j).equals(arrOfStr[7])) {
                                            listOfTestData.get(i).add(listOfFixedValues[j]);
                                        }
                                    }
                                }

                                break;
                            }
                        }
                    } else {
                        tableColumnName2 = arrOfStr[8];

                        int listOfFixedValuesIndex = 0;

                        for (int i = 0; i < table_column_values.size(); i++) {
                            if (table_column_values.get(i).equals(arrOfStr[10])) {
                                listOfFixedValuesIndex = i;
                            }
                        }

                        if (generateAtBoundaryValues) {
                            String firstBoundaryString = arrOfStr[3].replace(")", "");
                            String secondBoundaryString = arrOfStr[6].replace(")", "");

                            for (int i = 0; i < table_column_values.size(); i++) {
                                if (table_column_values.get(i).equals(tableColumnName)) {
                                    if (arrOfStr[2].equals(">=") && secondBoundaryString.charAt(1) != '=') {
                                        for (int j = Integer.parseInt(firstBoundaryString); j < Integer.parseInt(secondBoundaryString.substring(1)); j++) {
                                            listOfTestData.get(i).add(j);
                                            numOfTestCases++;
                                        }
                                    }
                                } else if (table_column_values.get(i).equals(tableColumnName2)) {
                                    if (arrOfStr[2].equals(">=") && secondBoundaryString.charAt(1) != '=') {
                                        for (int j = Integer.parseInt(firstBoundaryString); j < Integer.parseInt(secondBoundaryString.substring(1)); j++) {
                                            listOfTestData.get(i).add((int) (listOfFixedValues[listOfFixedValuesIndex] * Double.parseDouble(arrOfStr[12])));
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else if (arrOfStr[0].equals("Examples:")) {
                    stringToBeWritten += table_string;

                    for (int i = 0; i < numOfTestCases; i++) { //add each row of test cases
                        stringToBeWritten += "\n      |";
                        for (int j = 0; j < is_table_column_value_fixed.length; j++) {
                            if (is_table_column_value_fixed[j]) {
                                stringToBeWritten += " ";
                                stringToBeWritten += listOfFixedValues[j];
                                stringToBeWritten += " |";
                            } else {
                                stringToBeWritten += " ";
                                stringToBeWritten += listOfTestData.get(j).get(i);
                                stringToBeWritten += " |";
                            }
                        }
                    }
                    //write to file (Maybe use FileWriter.append())
                }

                System.out.println("listOfTestData is " + listOfTestData);

                if (arrOfStr[0].equals("@INEQ")) {
                    if (arrOfStr.length == 3) {
                        //TODO: if arrOfStr.length is 3, then add ONE row to example table

                        if (arrOfStr[2].equals("<")) {
                            //Generate a random int and subtract it from arrOfStr[3]
                        } else { //arrOfStr[2] is ">"
                            //Generate a random int and add it to arrOfStr[3]
                        }

                        //Check the number of "<" and the number of ">"

                        //add to some array
                    } else {
                        //TODO: add all possible rows to example table

                        //add to some array
                    }
                }

                //TODO

                System.out.println("Printing arrOfStr");
                for (int i = 0; i < arrOfStr.length; i++) {
                    System.out.println(arrOfStr[i]);
                }

            }

            System.out.println("stringToBeWritten is:\n" + stringToBeWritten);

            //myWriter.append("\n" + table_string);

            myReader.close();

            //FileWriter myWriter = new FileWriter("src/test/java/generateExampleTablesCopy.feature");
            FileWriter myWriter = new FileWriter(outputFilePath);
            myWriter.write(stringToBeWritten);
            myWriter.close();

            //myWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
