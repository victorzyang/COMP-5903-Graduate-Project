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
            //File myObj = new File("src/test/java/generateEquivalencePartitioning.feature"/*"constraints.txt"*/);
            Scanner myReader = new Scanner(myObj); //Maybe use FileReader instead of Scanner (?)
            //FileWriter myWriter = new FileWriter("src/test/java/generateEquivalencePartitioning.feature");

            //TODO: have a boolean variable that checks if we're currently on a scenario
            boolean checking_scenario_outline = false;

            ArrayList<String> table_column_values = new ArrayList<>();
            boolean[] is_table_column_value_fixed = null;
            int[] listOfFixedValues = null;

            String table_string = "      ";

            //TODO: have 2D ArrayList that keeps track of labels
            ArrayList<String> label_names = null;
            ArrayList<ArrayList<String>> label_conditions = null;
            boolean currentlyCheckingLabel = false;

            boolean generateAtBoundaryValues = false;

            ArrayList<ArrayList<Integer>> listOfTestData = null;

            //TODO: have a data structure for all test data that are string
            ArrayList<ArrayList<String>> listOfStringTestData = null;
            boolean[] is_table_column_value_string = null;

            boolean[] is_table_column_value_constrained = null;
            ArrayList<int[]> listOfConstraints = null;

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] arrOfStr = data.trim().split(" ");

                stringToBeWritten += data;
                stringToBeWritten += "\n";

                System.out.println("Size of arrOfStr is: " + arrOfStr.length);

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
                    label_names = new ArrayList<String>();
                    label_conditions = new ArrayList<ArrayList<String>>();
                    listOfTestData = new ArrayList<ArrayList<Integer>>(num_of_table_columns);
                    listOfStringTestData = new ArrayList<ArrayList<String>>(num_of_table_columns);
                    is_table_column_value_string = new boolean[num_of_table_columns];
                    is_table_column_value_constrained = new boolean[num_of_table_columns];
                    listOfConstraints = new ArrayList<int[]>(num_of_table_columns);

                    System.out.println("Checking all elements in table_column_values arraylist");
                    for (int i = 1; i < arrOfStr.length; i++) {
                        if (!arrOfStr[i].equals("|")) {
                            System.out.println("arrOfStr at index " + i + " is: " + arrOfStr[i]);
                            table_column_values.add(arrOfStr[i]);
                            listOfTestData.add(new ArrayList<Integer>());
                            listOfStringTestData.add(new ArrayList<String>());
                            listOfConstraints.add(new int[2]);
                        }
                    }

                    //System.out.println("Length of listOfTestData is: " + listOfTestData.size());

                    for (int i = 0; i < is_table_column_value_fixed.length; i++) {
                        is_table_column_value_fixed[i] = false;
                        listOfFixedValues[i] = 0;
                        is_table_column_value_string[i] = false;
                        is_table_column_value_constrained[i] = false;
                    }
                } else if (arrOfStr[0].equals("@LABEL")) {
                    System.out.println("At @LABEL tag");
                    String current_label_name = arrOfStr[1];
                    System.out.println("current_label_name is: " + current_label_name);
                    label_names.add(current_label_name);
                    label_conditions.add(new ArrayList<String>());
                    //labels.get(labels.size() - 1).add(current_label_name);
                    currentlyCheckingLabel = true;
                } else if (arrOfStr[0].equals("@END_LABEL")) {
                    System.out.println("Checking label_names");
                    for (int i = 0; i < label_names.size(); i++) {
                        System.out.println("label_names at index " + i + " is: " + label_names.get(i));
                    }

                    System.out.println("\nChecking label_conditions");
                    for (int i = 0; i < label_conditions.size(); i++) {
                        for (int j = 0; j < label_conditions.get(i).size(); j++) {
                            System.out.println("label_conditions at index [" + i + "][" + j + "] is: " + label_conditions.get(i).get(j));
                        }
                    }

                    currentlyCheckingLabel = false;
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

                    if (currentlyCheckingLabel) {

                        //int indexOfThen;
                        String conditionString = "";

                        for (int i = 1; i < arrOfStr.length; i++) {
                            if (arrOfStr[i].equals("THEN")) {
                                //indexOfThen = i;
                                break;
                            } else {
                                if (i == 1) {
                                    conditionString += arrOfStr[i].replace("(", "");
                                } else {
                                    conditionString += " ";
                                    conditionString += arrOfStr[i].replace(")", "");
                                }
                            }
                        }

                        /*conditionString.replace("(", "");
                        conditionString.replace(")", "");*/

                        System.out.println("conditionString is: " + conditionString);

                        label_conditions.get(label_conditions.size() - 1).add(conditionString);

                    } else {

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

                        String resultTableColumnName, tableColumnName2, tableColumnName3;

                        //System.out.println("tableColumnName is " + tableColumnName);

                        if (arrOfStrLengthFromIfToThen == 3) { //just generate a single value

                            resultTableColumnName = arrOfStr[5];

                            for (int i = 0; i < table_column_values.size(); i++) {
                                if (table_column_values.get(i).equals(tableColumnName)) {
                                    String boundaryString = arrOfStr[3].replace(")", "");

                                    if (arrOfStr[2].equals("<")) {
                                        listOfTestData.get(i).add((int) (Math.random() * (Integer.parseInt(boundaryString) - 1)));

                                        numOfTestCases++;
                                    } else if (arrOfStr[2].equals("<=")) {
                                        listOfTestData.get(i).add(Integer.parseInt(boundaryString) - (int) ((Math.random() * 10) + 1));
                                        listOfTestData.get(i).add(Integer.parseInt(boundaryString));

                                        //TODO: check for the other table columns without fixed data?
                                        for (int j = 0; j < table_column_values.size(); j++) {
                                            if (!table_column_values.get(j).equals(tableColumnName) && !table_column_values.get(j).equals(resultTableColumnName)) {
                                                if (!is_table_column_value_fixed[j]) {
                                                    listOfTestData.get(j).add((int) (Math.random() * 10));
                                                    listOfTestData.get(j).add((int) (Math.random() * 10));
                                                }
                                            }
                                        }

                                        numOfTestCases += 2;

                                        //TODO: add print statement for debugging (listOfTestData)
                                        System.out.println("Printing out listOfTestData");
                                        for (int j = 0; j < listOfTestData.size(); j++) {
                                            for (int k = 0; k < listOfTestData.get(j).size(); k++) {
                                                System.out.println("listOfTestData[" + j + "][" + k + "]: " + listOfTestData.get(j).get(k));
                                            }
                                        }
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

                                        numOfTestCases++; //TODO: revise this
                                    }

                                    break;
                                }
                            }

                            for (int i = 0; i < table_column_values.size(); i++) {
                                if (table_column_values.get(i).equals(resultTableColumnName)) {
                                    boolean lastStringIsAnInt = true;

                                    for (int j = 0; j < table_column_values.size(); j++) {
                                        if (table_column_values.get(j).equals(arrOfStr[7])) { //check if arrOfStr[7] is an int or not
                                            lastStringIsAnInt = false;
                                            break;
                                        }
                                    }

                                    if (arrOfStr[7].charAt(0) == '‘') { //Something's wrong here
                                        lastStringIsAnInt = false;
                                    }

                                    System.out.println("lastStringIsAnInt is: " + lastStringIsAnInt);

                                    if (lastStringIsAnInt) {
                                        if (arrOfStr[2].equals("<=")) {
                                            listOfTestData.get(i).add(Integer.parseInt(arrOfStr[7]));
                                            listOfTestData.get(i).add(Integer.parseInt(arrOfStr[7]));
                                        } else {
                                            listOfTestData.get(i).add(Integer.parseInt(arrOfStr[7]));
                                        }
                                    } else {
                                        if (arrOfStr[7].charAt(0) == '‘') {
                                            //TODO
                                            String stringToAdd = "";

                                            for (int j = 7; j < arrOfStr.length; j++) {
                                                if (j == 7) {
                                                    stringToAdd += arrOfStr[j].replace("‘", "");
                                                } else if (j == arrOfStr.length - 1) {
                                                    stringToAdd += " ";
                                                    stringToAdd += arrOfStr[j].replace("’", "");
                                                } else {
                                                    stringToAdd += " ";
                                                    stringToAdd += arrOfStr[j];
                                                }
                                            }

                                            if (arrOfStr[2].equals("<=")) {
                                                listOfStringTestData.get(i).add(stringToAdd);
                                                listOfStringTestData.get(i).add(stringToAdd);
                                            } else {
                                                listOfStringTestData.get(i).add(stringToAdd);
                                            }

                                            is_table_column_value_string[i] = true;
                                        } else {
                                            for (int j = 0; j < table_column_values.size(); j++) {
                                                if (table_column_values.get(j).equals(arrOfStr[7])) {
                                                    if (arrOfStr[2].equals("<=")) {
                                                        listOfTestData.get(i).add(listOfFixedValues[j]);
                                                        listOfTestData.get(i).add(listOfFixedValues[j]);
                                                    } else {
                                                        listOfTestData.get(i).add(listOfFixedValues[j]);
                                                    }
                                                }
                                            }
                                        }

                                    }

                                    break;
                                }
                            }

                            //TODO: add print statement for debugging (listOfStringTestData)
                            System.out.println("Printing out listOfStringTestData");
                            for (int i = 0; i < listOfStringTestData.size(); i++) {
                                for (int j = 0; j < listOfStringTestData.get(i).size(); j++) {
                                    System.out.println("listOfStringTestData[" + i + "][" + j + "]: " + listOfStringTestData.get(i).get(j));
                                }
                            }
                        } else if (arrOfStrLengthFromIfToThen == 5) {
                            resultTableColumnName = arrOfStr[7];
                            tableColumnName2 = arrOfStr[3];
                            tableColumnName3 = arrOfStr[5].replace(")", "");

                            //TODO: 'not a triangle' examples
                            // if generate at boundary values?

                            if (arrOfStr[2].equals("==") && arrOfStr[4].equals("==")) {
                                int testDataInt = (int) (Math.random() * 10) + 1;

                                for (int i = 0; i < table_column_values.size(); i++) {
                                    if (table_column_values.get(i).equals(tableColumnName) || table_column_values.get(i).equals(tableColumnName2)
                                        || table_column_values.get(i).equals(tableColumnName3)) {
                                        listOfTestData.get(i).add(testDataInt);
                                        //numOfTestCases++; //TODO: Maybe this should be moved outside the for loop?
                                    }
                                }

                                numOfTestCases++;
                            } else if (arrOfStr[2].equals(">=") && arrOfStr[4].equals("+")) {
                                int int1, int2, int3;

                                int2 = (int) (Math.random() * 10) + 1;
                                int3 = (int) (Math.random() * 10) + 1;
                                int1 = int2 + int3;

                                //case for '='

                                for (int i = 0; i < table_column_values.size(); i++) {
                                    if (table_column_values.get(i).equals(tableColumnName)) {
                                        listOfTestData.get(i).add(int1);
                                    } else if (table_column_values.get(i).equals(tableColumnName2)) {
                                        listOfTestData.get(i).add(int2);
                                    } else if (table_column_values.get(i).equals(tableColumnName3)) {
                                        listOfTestData.get(i).add(int3);
                                    }
                                }

                                int int4, int5, int6;

                                int5 = (int) (Math.random() * 10) + 1;
                                int6 = (int) (Math.random() * 10) + 1;
                                int4 = (int) (Math.random() * 10) + 1 + int5 + int6;

                                //TODO: do a case for '>'

                                for (int i = 0; i < table_column_values.size(); i++) {
                                    if (table_column_values.get(i).equals(tableColumnName)) {
                                        listOfTestData.get(i).add(int4);
                                    } else if (table_column_values.get(i).equals(tableColumnName2)) {
                                        listOfTestData.get(i).add(int5);
                                    } else if (table_column_values.get(i).equals(tableColumnName3)) {
                                        listOfTestData.get(i).add(int6);
                                    }
                                }

                                numOfTestCases += 2;
                            }

                            System.out.println("Printing out listOfTestData");
                            for (int j = 0; j < listOfTestData.size(); j++) {
                                for (int k = 0; k < listOfTestData.get(j).size(); k++) {
                                    System.out.println("listOfTestData[" + j + "][" + k + "]: " + listOfTestData.get(j).get(k));
                                }
                            }

                            boolean lastStringIsAnInt = true;

                            if (arrOfStr[9].charAt(0) == '‘') {
                                lastStringIsAnInt = false;
                            }

                            if (lastStringIsAnInt) {

                            } else {
                                if (arrOfStr[9].charAt(0) == '‘') {
                                    String stringToAdd = "";

                                    //TODO: listOfStringTestData and is_table_column_value_string
                                    for (int i = 9; i < arrOfStr.length; i++) {
                                        if (i == 9) {
                                            stringToAdd += arrOfStr[i].replace("‘", "");
                                        } else if (i == arrOfStr.length - 1) {
                                            stringToAdd += " ";
                                            stringToAdd += arrOfStr[i].replace("’", "");
                                        } else {
                                            stringToAdd += " ";
                                            stringToAdd += arrOfStr[i];
                                        }
                                    }

                                    for (int i = 0; i < table_column_values.size(); i++) {
                                        if (table_column_values.get(i).equals(resultTableColumnName)) {
                                            if (arrOfStr[2].equals("==") && arrOfStr[4].equals("==")) {
                                                listOfStringTestData.get(i).add(stringToAdd);
                                            } else if (arrOfStr[2].equals(">=") && arrOfStr[4].equals("+")) {
                                                listOfStringTestData.get(i).add(stringToAdd);
                                                listOfStringTestData.get(i).add(stringToAdd);
                                            }

                                            is_table_column_value_string[i] = true;

                                            break;
                                        }
                                    }
                                }

                                //TODO: need to check all column names to see if random number needs to be generated

                                System.out.println("Printing out listOfStringTestData");
                                for (int i = 0; i < listOfStringTestData.size(); i++) {
                                    for (int j = 0; j < listOfStringTestData.get(i).size(); j++) {
                                        System.out.println("listOfStringTestData[" + i + "][" + j + "]: " + listOfStringTestData.get(i).get(j));
                                    }
                                }
                            }

                        } else {
                            resultTableColumnName = arrOfStr[8];

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
                                    } else if (table_column_values.get(i).equals(resultTableColumnName)) {
                                        if (arrOfStr[2].equals(">=") && secondBoundaryString.charAt(1) != '=') {
                                            for (int j = Integer.parseInt(firstBoundaryString); j < Integer.parseInt(secondBoundaryString.substring(1)); j++) {
                                                listOfTestData.get(i).add((int) (listOfFixedValues[listOfFixedValuesIndex] * Double.parseDouble(arrOfStr[12])));
                                            }
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

            //FileWriter myWriter = new FileWriter("src/test/java/generateEquivalencePartitioningCopy.feature");
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
