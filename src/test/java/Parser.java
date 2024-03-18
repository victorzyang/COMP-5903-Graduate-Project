import io.cucumber.java.bs.A;
import io.cucumber.java.sl.In;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

//Below imports are for working with making a procedure call
//import java.lang.reflect.Method;
import java.lang.reflect.*;
import java.awt.*;

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

            ArrayList<ArrayList<String>> listOfTestData = null;
            //ArrayList<ArrayList<Integer>> listOfTestData = null;

            //TODO: have a data structure for all test data that are string
            ArrayList<ArrayList<String>> listOfStringTestData = null;
            boolean[] is_table_column_value_string = null;

            boolean[] is_table_column_value_constrained = null;
            ArrayList<int[]> listOfConstraints = null;

            boolean[] is_table_column_value_a_parameter = null;
            ArrayList<ArrayList<String>> listOfParameters = null;

            ArrayList<String> set_names = new ArrayList<>();
            ArrayList<ArrayList<String>> set_elements = new ArrayList<ArrayList<String>>();

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
                    listOfTestData = new ArrayList<ArrayList<String>>(num_of_table_columns);
                    //listOfTestData = new ArrayList<ArrayList<Integer>>(num_of_table_columns);
                    listOfStringTestData = new ArrayList<ArrayList<String>>(num_of_table_columns);
                    is_table_column_value_string = new boolean[num_of_table_columns];
                    is_table_column_value_constrained = new boolean[num_of_table_columns];
                    listOfConstraints = new ArrayList<int[]>(num_of_table_columns);
                    is_table_column_value_a_parameter = new boolean[num_of_table_columns];
                    listOfParameters = new ArrayList<ArrayList<String>>(num_of_table_columns);

                    System.out.println("Checking all elements in table_column_values arraylist");
                    for (int i = 1; i < arrOfStr.length; i++) {
                        if (!arrOfStr[i].equals("|")) {
                            System.out.println("arrOfStr at index " + i + " is: " + arrOfStr[i]);
                            table_column_values.add(arrOfStr[i]);
                            listOfTestData.add(new ArrayList<String>());
                            //listOfTestData.add(new ArrayList<Integer>());
                            listOfStringTestData.add(new ArrayList<String>());
                            listOfConstraints.add(new int[2]);
                            listOfParameters.add(new ArrayList<String>());
                        }
                    }

                    //System.out.println("Length of listOfTestData is: " + listOfTestData.size());

                    for (int i = 0; i < is_table_column_value_fixed.length; i++) {
                        is_table_column_value_fixed[i] = false;
                        listOfFixedValues[i] = 0;
                        is_table_column_value_string[i] = false;
                        is_table_column_value_constrained[i] = false;
                        is_table_column_value_a_parameter[i] = false;
                    }
                } else if (arrOfStr[0].equals("@PARAMETER")) {
                    String parameter_name = arrOfStr[1].substring(1, arrOfStr[1].length()-1);

                    int index_of_parameter_name = 0;

                    // Data structure of booleans that keeps track of which table column is "Parameter"
                    for (int i = 0; i < table_column_values.size(); i++) {
                        if (table_column_values.get(i).equals(parameter_name)) {
                            is_table_column_value_a_parameter[i] = true;

                            index_of_parameter_name = i;
                        }
                    }

                    // Data structure that keeps track of the different parameters
                    for (int i = 2; i < arrOfStr.length; i++) {
                        String currentParameterString = arrOfStr[i];

                        currentParameterString = currentParameterString.replace("{", "");
                        currentParameterString = currentParameterString.replace("}", "");
                        currentParameterString = currentParameterString.replace("'", "");
                        currentParameterString = currentParameterString.replace("'", "");
                        currentParameterString = currentParameterString.replace(",", "");

                        listOfParameters.get(index_of_parameter_name).add(currentParameterString);
                    }

                    System.out.println("Printing out all booleans in is_table_column_value_a_parameter");
                    for (int i = 0; i < is_table_column_value_a_parameter.length; i++) {
                        System.out.println("is_table_column_value_a_parameter at index " + i + " is: " + is_table_column_value_a_parameter[i]);
                    }

                    System.out.println("Printing out all parameters in listOfParameters");
                    for (int i = 0; i < listOfParameters.size(); i++) {
                        if (!listOfParameters.get(i).isEmpty()) {
                            System.out.println("Printing out all parameters in listOfParameters.get(" + i + ")");
                            for (int j = 0; j < listOfParameters.get(i).size(); j++) {
                                System.out.println("listOfParameters[" + i + "][" + j + "] is " + listOfParameters.get(i).get(j));
                            }
                        }
                    }

                } else if (arrOfStr[0].equals("@SET")) {
                    String set_name = arrOfStr[1];

                    set_names.add(set_name);

                    set_elements.add(new ArrayList<String>());

                    for (int i = 2; i < arrOfStr.length; i++) {
                        String currentSetElementString = arrOfStr[i];

                        currentSetElementString = currentSetElementString.replace("{", "");
                        currentSetElementString = currentSetElementString.replace("}", "");
                        currentSetElementString = currentSetElementString.replace(",", "");

                        set_elements.get(set_elements.size() - 1).add(currentSetElementString);
                    }

                    System.out.println("Printing out all set string names in set_names");
                    for (int i = 0; i < set_names.size(); i++) {
                        System.out.println("set_names at index " + i + " is: " + set_names.get(i));
                    }

                    System.out.println("Printing out all elements in set_elements");
                    for (int i = 0; i < set_elements.size(); i++) {
                        for (int j = 0; j < set_elements.get(i).size(); j++) {
                            System.out.println("set_elements[" + i + "][" + j + "] is " + set_elements.get(i).get(j));
                        }
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

                } else if (arrOfStr[0].equals("@ConstrainedValue")) {

                    //Need to parse the data structure after 'for'
                    ArrayList<String> list_of_constrained_columns = new ArrayList<String>();

                    for (int i = 9; i < arrOfStr.length; i++) {
                        String constrainedColumn = arrOfStr[i];
                        constrainedColumn = constrainedColumn.replace("{", "");
                        constrainedColumn = constrainedColumn.replace("}", "");
                        constrainedColumn = constrainedColumn.replace(",", "");

                        list_of_constrained_columns.add(constrainedColumn);
                    }

                    String thirdStringAfterAnnotation = arrOfStr[3];
                    int int1 = Integer.parseInt(thirdStringAfterAnnotation.replace(")", ""));

                    String seventhStringAfterAnnotation = arrOfStr[7];
                    int int2 = Integer.parseInt(seventhStringAfterAnnotation.replace(")", ""));

                    String symbol1 = arrOfStr[2];

                    String symbol2 = arrOfStr[6];

                    for (int i = 0; i < table_column_values.size(); i++) {
                        //TODO
                        if (list_of_constrained_columns.contains(table_column_values.get(i))) {
                            //TODO: 'is_table_column_value_constrained' and 'listOfConstraints'
                            is_table_column_value_constrained[i] = true;

                            if (symbol1.equals(">=")) {
                                listOfConstraints.get(i)[0] = int1;
                            }

                            if (symbol2.equals("<=")) {
                                listOfConstraints.get(i)[1] = int2;
                            }
                        }
                    }

                    for (int i = 0; i < table_column_values.size(); i++) {
                        System.out.println("Table column at index " + i + " is: " + table_column_values.get(i));
                        System.out.println("is_table_column_value_constrained at index " + i + " is: " + is_table_column_value_constrained[i]);
                    }

                    for (int i = 0; i < listOfConstraints.size(); i++) {
                        System.out.println("listOfConstraints[" + i + "][0]: " + listOfConstraints.get(i)[0]);
                        System.out.println("listOfConstraints[" + i + "][1]: " + listOfConstraints.get(i)[1]);
                    }
                } else if (arrOfStr[0].equals("@BVA")) {
                    generateAtBoundaryValues = true;
                } else if (arrOfStr[0].equals("@CATEGORY")) {
                    //TODO

                    //Check second string which contains "(" or ")"
                    String second_string = arrOfStr[2];

                    if (second_string.contains("COMBINATION")) {
                        //TODO: @ConstrainedValue (x >=1) & (x <=6) for {D1, D2, D3, D4, D5}

                        String[] arrOfSecondStringWithoutCOMBINATION = second_string.split("COMBINATION");

                        System.out.println("Printing out all elements in arrOfSecondStringWithoutCOMBINATION");
                        for (int i = 0; i < arrOfSecondStringWithoutCOMBINATION.length; i++) {
                            System.out.println("arrOfSecondStringWithoutCOMBINATION at index " + i + " is: " + arrOfSecondStringWithoutCOMBINATION[i]);
                        }

                        //TODO
                        ArrayList<String> listOfTableColumnNamesAfterCombination = new ArrayList<>();
                        ArrayList<String> listOfTableColumnValuesAfterCombaintion = new ArrayList<>();

                        String procedureToBeCalled = "";
                        String classOfProcedureToBeCalled = "";
                        //TODO: maybe have a boolean helper variable as well?

                        String[] arrOfParametersAndTypesStrings = null;
                        String[] arrOfParameters = null;
                        String[] arrOfParameterTypes = null;

                        for (int i = 5; i < arrOfStr.length; i+=2) {
                            if (arrOfStr[i].equals("in")) {
                                classOfProcedureToBeCalled = arrOfStr[i+1];

                                classOfProcedureToBeCalled = classOfProcedureToBeCalled.replace("'", "");
                                classOfProcedureToBeCalled = classOfProcedureToBeCalled.replace("'", "");

                                System.out.println("classOfProcedureToBeCalled is: " + classOfProcedureToBeCalled);
                            } else if (arrOfStr[i].equals("with") && arrOfStr[i+1].equals("parameters")) {
                                String stringOfParametersAndTypes = "";

                                for (int j = i+2; j < arrOfStr.length; j++) {
                                    stringOfParametersAndTypes += arrOfStr[j];
                                }

                                arrOfParametersAndTypesStrings = stringOfParametersAndTypes.split(",");

                                arrOfParameters = new String[arrOfParametersAndTypesStrings.length];
                                arrOfParameterTypes = new String[arrOfParametersAndTypesStrings.length];

                                for (int s = 0; s < arrOfParametersAndTypesStrings.length; s++) {
                                    String[] arrOfSpecificStringOfPAndTStrings = arrOfParametersAndTypesStrings[s].split(":");

                                    arrOfParameters[s] = arrOfSpecificStringOfPAndTStrings[0].replace("[", "");
                                    arrOfParameterTypes[s] = arrOfSpecificStringOfPAndTStrings[1].replace("]", "");
                                }

                                System.out.println("Printing out arrOfParameters and arrOfParameterTypes");
                                for (int p = 0; p < arrOfParameters.length; p++) {
                                    System.out.println("arrOfParameters at index " + p + " is: " + arrOfParameters[p]);
                                    System.out.println("arrOfParameterTypes at index " + p + " is: " + arrOfParameterTypes[p]);
                                }

                                break;
                            } else {
                                String currentTableColumnName = arrOfStr[i];
                                currentTableColumnName = currentTableColumnName.replace("'", "");
                                currentTableColumnName = currentTableColumnName.replace("'", "");

                                listOfTableColumnNamesAfterCombination.add(currentTableColumnName);

                                String currentTableColumnValue = arrOfStr[i+1];
                                currentTableColumnValue = currentTableColumnValue.replace("'", "");
                                currentTableColumnValue = currentTableColumnValue.replace("'", "");

                                listOfTableColumnValuesAfterCombaintion.add(currentTableColumnValue);

                                if (i+1 != arrOfStr.length - 1) {
                                    if (arrOfStr[i+2].equals("in")) {
                                        procedureToBeCalled = currentTableColumnValue;
                                        System.out.println("procedureToBeCalled is: " + procedureToBeCalled);
                                    }
                                }
                            }
                        }

                        if (isAnInteger(arrOfSecondStringWithoutCOMBINATION[0])) {
                            int numOfCombinations = Integer.parseInt(arrOfSecondStringWithoutCOMBINATION[0]);

                            int[] occurences = new int[numOfCombinations];
                            //int[] specificCombinationValues = new int[numOfCombinations]; //TODO: maybe this should be an arraylist?
                            ArrayList<Integer> specificCombinationValues = new ArrayList<>();

                            System.out.println("numOfCombinations is: " + numOfCombinations); //This is good

                            String secondStringAfterCOMBINATION = arrOfSecondStringWithoutCOMBINATION[1];
                            secondStringAfterCOMBINATION = secondStringAfterCOMBINATION.replace("(", "");
                            secondStringAfterCOMBINATION = secondStringAfterCOMBINATION.replace(")", "");

                            String[] arrOfSecondStringAfterCOMBINATION = secondStringAfterCOMBINATION.split(",");

                            //TODO: for sequences, check the start of the sequence and the length of the sequence (?)

                            //TODO: check if there are any strings in arrOfSecondStringAfterCOMBINATION which are not integers and not "_" strings either

                            //TODO: work with 'numOfCombinations' integer!!!

                            boolean thereAreCharsIndicatingSequence = false; //TODO

                            for (int s = 0; s < arrOfSecondStringAfterCOMBINATION.length; s++) {
                                // check for "+" or "-" characters in the strings
                                if (arrOfSecondStringAfterCOMBINATION[s].contains("+") || arrOfSecondStringAfterCOMBINATION[s].contains("-")) {
                                    thereAreCharsIndicatingSequence = true; // if there are "+" or "-" characters in the strings in arrOfSecondStringAfterCOMBINATION, then set 'thereAreCharsIndicatingSequence' to true
                                }
                            }

                            // TODO: have a data structure that keeps track of different sequence lengths that are in sequence strings (?)

                            // keep track of the different characters used before the "+" and "-" symbols in a data structure
                            ArrayList<String> variablesUsedForSequences = new ArrayList<>(); //This should be good

                            // Helper data structures to keep track of all the values to be randomly assigned
                            int[] set_values = null;
                            boolean[] has_set_value_been_assigned = null;

                            //Have another set_elements data structure
                            ArrayList<String> set_elements_to_work_with = null;

                            if (thereAreCharsIndicatingSequence) {
                                System.out.println("Are there chars indicating sequence: " + thereAreCharsIndicatingSequence); //this is good

                                for (int s = 0; s < arrOfSecondStringAfterCOMBINATION.length; s++) {
                                    if (arrOfSecondStringAfterCOMBINATION[s].contains("+") || arrOfSecondStringAfterCOMBINATION[s].contains("-")) {
                                        if (arrOfSecondStringAfterCOMBINATION[s].contains("+")) {
                                            String[] arrOfSequenceString = arrOfSecondStringAfterCOMBINATION[s].split("\\+");

                                            if (!variablesUsedForSequences.contains(arrOfSequenceString[0])) {
                                                variablesUsedForSequences.add(arrOfSequenceString[0]);
                                            }
                                        } else {
                                            String[] arrOfSequenceString = arrOfSecondStringAfterCOMBINATION[s].split("-");

                                            if (!variablesUsedForSequences.contains(arrOfSequenceString[0])) {
                                                variablesUsedForSequences.add(arrOfSequenceString[0]);
                                            }
                                        }
                                    }
                                }

                                /*System.out.println("Printing out variablesUsedForSequences");
                                for (int v = 0; v < variablesUsedForSequences.size(); v++) {
                                    System.out.println("variablesUsedForSequences at index " + v + " is: " + variablesUsedForSequences.get(v));
                                }*/

                                //TODO

                                String thirdString = arrOfStr[3];
                                String fourthString = arrOfStr[4];

                                if (thirdString.equals("IN") && set_names.contains(fourthString)) {
                                    //TODO: set_names and set_elements
                                    //ArrayList<String> set_elements_to_work_with = new ArrayList<>();

                                    for (int sn = 0; sn < set_names.size(); sn++) {
                                        if (set_names.get(sn).equals(fourthString)) {
                                            set_elements_to_work_with = set_elements.get(sn);

                                            set_values = new int[set_elements.get(sn).size()];
                                            has_set_value_been_assigned = new boolean[set_elements.get(sn).size()];

                                            for (int se = 0; se < set_elements.get(sn).size(); se++) {
                                                set_values[se] = 0;
                                                has_set_value_been_assigned[se] = false;
                                            }
                                        }
                                    }

                                    for (int c = 0; c < numOfCombinations; c++) {
                                        boolean generatedSequenceIsFeasible = false;

                                        //TODO: implement a while loop to keep track of whether generated sequence satisfies all constraints (WORK WITH THE CHARACTER THAT APPEARS BEFORE THE + OR - SYMBOL)
                                        while (!generatedSequenceIsFeasible) {
                                            ArrayList<Integer> sequence_of_ints = new ArrayList<>();
                                            ArrayList<Integer> set_values_indices = new ArrayList<>(); // an arraylist for indices

                                            generatedSequenceIsFeasible = true;

                                            boolean currentlyCheckingSequence = false; // boolean variable that keeps track of whether currently checking a sequence

                                            int randomValueForVariable = 0;

                                            //TODO: iterate through 'arrOfSecondStringAfterCOMBINATION'
                                            for (int s = 0; s < arrOfSecondStringAfterCOMBINATION.length; s++) { //This for loop is the right idea
                                                System.out.println("arrOfSecondStringAfterCOMBINATION at index " + s + " is: " + arrOfSecondStringAfterCOMBINATION[s]);

                                                if (arrOfSecondStringAfterCOMBINATION[s].contains(variablesUsedForSequences.get(c))) {
                                                    System.out.println("arrOfSecondStringAfterCOMBINATION[s] contains variablesUsedForSequences.get(c)");

                                                    String current_set_element_to_work_with = set_elements_to_work_with.get(s);
                                                    System.out.println("current_set_element_to_work_with: " + current_set_element_to_work_with);

                                                    int table_column_values_index_of_set_element = 0;

                                                    for (int tc = 0; tc < table_column_values.size(); tc++) {
                                                        if (table_column_values.get(tc).equals(current_set_element_to_work_with)) {
                                                            table_column_values_index_of_set_element = tc;
                                                        }
                                                    }

                                                    System.out.println("table_column_values_index_of_set_element: " + table_column_values_index_of_set_element);

                                                    System.out.println("Currently checking sequence? " + currentlyCheckingSequence);

                                                    if (currentlyCheckingSequence == false) {
                                                        currentlyCheckingSequence = true;

                                                        if (is_table_column_value_constrained[table_column_values_index_of_set_element]) {
                                                            System.out.println("table column value is constrained");

                                                            int constraint1 = listOfConstraints.get(table_column_values_index_of_set_element)[0];
                                                            int constraint2 = listOfConstraints.get(table_column_values_index_of_set_element)[1];

                                                            randomValueForVariable = (int) ((Math.random() * (constraint2 - constraint1)) + constraint1);

                                                            System.out.println("Value to be added to sequence_of_ints is " + randomValueForVariable);

                                                            sequence_of_ints.add(randomValueForVariable);

                                                            set_values_indices.add(s);
                                                        }
                                                    } else {
                                                        if (is_table_column_value_constrained[table_column_values_index_of_set_element]) {
                                                            System.out.println("table column value is constrained");

                                                            int constraint1 = listOfConstraints.get(table_column_values_index_of_set_element)[0];
                                                            int constraint2 = listOfConstraints.get(table_column_values_index_of_set_element)[1];

                                                            if (arrOfSecondStringAfterCOMBINATION[s].contains("+")) {
                                                                String[] arrOfSequenceString = arrOfSecondStringAfterCOMBINATION[s].split("\\+");

                                                                int valueToBeAdded = Integer.parseInt(arrOfSequenceString[1]); //TODO

                                                                // check for constraints
                                                                if (randomValueForVariable + valueToBeAdded < constraint1 || randomValueForVariable + valueToBeAdded > constraint2) {
                                                                    generatedSequenceIsFeasible = false;
                                                                }

                                                                System.out.println("Value to be added to sequence_of_ints is " + (randomValueForVariable + valueToBeAdded));

                                                                sequence_of_ints.add(randomValueForVariable + valueToBeAdded);

                                                                set_values_indices.add(s);

                                                                //TODO: need to add randomValueForVariable + valueToBeAdded to test data
                                                            } else if (arrOfSecondStringAfterCOMBINATION[s].contains("-")) {
                                                                String[] arrOfSequenceString = arrOfSecondStringAfterCOMBINATION[s].split("-");

                                                                int valueToBeSubtracted = Integer.parseInt(arrOfSequenceString[1]); //TODO

                                                                // check for constraints
                                                                if (randomValueForVariable - valueToBeSubtracted < constraint1 || randomValueForVariable + valueToBeSubtracted > constraint2) {
                                                                    generatedSequenceIsFeasible = false;
                                                                }

                                                                sequence_of_ints.add(randomValueForVariable - valueToBeSubtracted);

                                                                set_values_indices.add(s);
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    if (currentlyCheckingSequence) {
                                                        break;
                                                    }
                                                }
                                            }

                                            if (generatedSequenceIsFeasible) {
                                                System.out.println("Printing out sequence_of_ints"); //debugging
                                                for (int sv = 0; sv < sequence_of_ints.size(); sv++) {
                                                    System.out.println("sequence_of_ints at index " + sv + " is: " + sequence_of_ints.get(sv));
                                                }

                                                System.out.println("Printing out set_values_indices"); //debugging
                                                for (int svi = 0; svi < set_values_indices.size(); svi++) {
                                                    System.out.println("set_values_indices at index " + svi + " is: " + set_values_indices.get(svi));
                                                }

                                                for (int sv = 0; sv < set_values_indices.size(); sv++) {
                                                    set_values[set_values_indices.get(sv)] = sequence_of_ints.get(sv);
                                                    has_set_value_been_assigned[set_values_indices.get(sv)] = true;
                                                }
                                            }
                                        }
                                    }

                                    for (int sv = 0; sv < set_values.length; sv++) {
                                        if (!has_set_value_been_assigned[sv]) {
                                            int table_index_of_set_element = 0;

                                            //for loop
                                            for (int tc = 0; tc < table_column_values.size(); tc++) {
                                                if (table_column_values.get(tc).equals(set_elements_to_work_with.get(sv))) {
                                                    table_index_of_set_element = tc;
                                                }
                                            }

                                            int constraint1;
                                            int constraint2;

                                            if (arrOfSecondStringAfterCOMBINATION[sv].equals("_")) {
                                                if (is_table_column_value_constrained[table_index_of_set_element]) {
                                                    constraint1 = listOfConstraints.get(table_index_of_set_element)[0];
                                                    constraint2 = listOfConstraints.get(table_index_of_set_element)[1];

                                                    int random_set_value = (int) ((Math.random() * (constraint2 - constraint1)) + constraint1);

                                                    set_values[sv] = random_set_value;
                                                    has_set_value_been_assigned[sv] = true;
                                                }
                                            }

                                        }
                                    }
                                }

                                //TODO: Once all sequential elements are assigned a value, loop through all strings in arrOfSecondStringAfterCOMBINATION to see if there are still any unassigned elements (they may be "_" or an int value)

                                //TODO: set_values
                            } else {
                                for (int i = 0; i < numOfCombinations; i++) {
                                    // i times 2 & i times 2 + 1

                                    occurences[i] = Integer.parseInt(arrOfSecondStringAfterCOMBINATION[i*2]);
                                    //specificCombinationValues[i] = Integer.parseInt(arrOfSecondStringAfterCOMBINATION[i*2+1]);
                                    //specificCombinationValues.add(Integer.parseInt(arrOfSecondStringAfterCOMBINATION[i*2+1]));

                                    String thirdString = arrOfStr[3];
                                    String fourthString = arrOfStr[4];

                                    if (i > 0) {

                                        if (arrOfSecondStringAfterCOMBINATION[i*2+1].equals("_")) {

                                            if (thirdString.equals("IN") && set_names.contains(fourthString)) {
                                                for (int sn = 0; sn < set_names.size(); sn++) {
                                                    if (set_names.get(sn).equals(fourthString)) {

                                                        boolean randomValueGenerated = false;

                                                        //TODO: have a boolean variable that checks if random generated value satisfies all constraints

                                                        // While a random value has not been generated
                                                        while (!randomValueGenerated) {
                                                            // generate a random index less than set_names.get(sn).size()
                                                            int randomIndex = (int) (Math.random() * (set_elements.get(sn).size()-1)); //it might not be 'set_names'...

                                                            // get the set element from set_elements.get(random_index)
                                                            String set_element_at_random_index = set_elements.get(sn).get(randomIndex);

                                                            // get the index of where this set element is in 'table_column_values'
                                                            int table_column_values_index_of_set_element = 0;

                                                            for (int tc = 0; tc < table_column_values.size(); tc++) {
                                                                if (table_column_values.get(tc).equals(set_element_at_random_index)) {
                                                                    table_column_values_index_of_set_element = tc;
                                                                }
                                                            }

                                                            int random_value;

                                                            // check if set element is constrained
                                                            if (is_table_column_value_constrained[table_column_values_index_of_set_element]) {
                                                                // if element is constrained, get the constraints
                                                                int constraint1 = listOfConstraints.get(table_column_values_index_of_set_element)[0];
                                                                int constraint2 = listOfConstraints.get(table_column_values_index_of_set_element)[1];

                                                                // generate random int between the constraints
                                                                random_value = (int) ((Math.random() * (constraint2 - constraint1)) + constraint1);

                                                                // check if that generated random int satisfies at least occurences[i] times
                                                                int numOfConstraintsSatisfied = 0;

                                                                for (int c = 0; c < listOfConstraints.size(); c++) {
                                                                    if (set_elements.get(sn).contains(table_column_values.get(c))) {
                                                                        if (random_value >= listOfConstraints.get(c)[0] && random_value <= listOfConstraints.get(c)[1]) {
                                                                            numOfConstraintsSatisfied++;
                                                                        }
                                                                    }
                                                                }

                                                                //below condition checks if randomly generated value is already in specificCombinationValues

                                                                // set 'randomValueGenerated' to be true when a random value is generated
                                                                if (numOfConstraintsSatisfied >= occurences[i] && !specificCombinationValues.contains(random_value)) {
                                                                    specificCombinationValues.add(random_value);

                                                                    randomValueGenerated = true;
                                                                }

                                                            } else {

                                                            }
                                                        }

                                                    }
                                                }
                                            }

                                        } else {
                                            specificCombinationValues.add(Integer.parseInt(arrOfSecondStringAfterCOMBINATION[i*2+1]));
                                        }

                                    } else {

                                        if (arrOfSecondStringAfterCOMBINATION[i*2+1].equals("_")) {
                                            if (thirdString.equals("IN") && set_names.contains(fourthString)) {
                                                for (int sn = 0; sn < set_names.size(); sn++) {
                                                    if (set_names.get(sn).equals(fourthString)) {

                                                        boolean randomValueGenerated = false;

                                                        //TODO: have a boolean variable that checks if random generated value satisfies all constraints

                                                        // While a random value has not been generated
                                                        while (!randomValueGenerated) {
                                                            // generate a random index less than set_names.get(sn).size()
                                                            int randomIndex = (int) (Math.random() * (set_elements.get(sn).size()-1));

                                                            // get the set element from set_elements.get(random_index)
                                                            String set_element_at_random_index = set_elements.get(sn).get(randomIndex);

                                                            // get the index of where this set element is in 'table_column_values'
                                                            int table_column_values_index_of_set_element = 0;

                                                            for (int tc = 0; tc < table_column_values.size(); tc++) {
                                                                if (table_column_values.get(tc).equals(set_element_at_random_index)) {
                                                                    table_column_values_index_of_set_element = tc;
                                                                }
                                                            }

                                                            int random_value;

                                                            // check if set element is constrained
                                                            if (is_table_column_value_constrained[table_column_values_index_of_set_element]) {
                                                                // if element is constrained, get the constraints
                                                                int constraint1 = listOfConstraints.get(table_column_values_index_of_set_element)[0];
                                                                int constraint2 = listOfConstraints.get(table_column_values_index_of_set_element)[1];

                                                                // generate random int between the constraints
                                                                random_value = (int) ((Math.random() * (constraint2 - constraint1)) + constraint1);

                                                                // check if that generated random int satisfies at least occurences[i] times
                                                                int numOfConstraintsSatisfied = 0;

                                                                for (int c = 0; c < listOfConstraints.size(); c++) {
                                                                    if (set_elements.get(sn).contains(table_column_values.get(c))) {
                                                                        if (random_value >= listOfConstraints.get(c)[0] && random_value <= listOfConstraints.get(c)[1]) {
                                                                            numOfConstraintsSatisfied++;
                                                                        }
                                                                    }
                                                                }

                                                                //below condition generates random value and adds to specificCombinationValues

                                                                // set 'randomValueGenerated' to be true when a random value is generated
                                                                if (numOfConstraintsSatisfied >= occurences[i]) {
                                                                    specificCombinationValues.add(random_value);

                                                                    randomValueGenerated = true;
                                                                }

                                                            } else {

                                                            }
                                                        }

                                                    }
                                                }

                                            }
                                        } else {
                                            specificCombinationValues.add(Integer.parseInt(arrOfSecondStringAfterCOMBINATION[i*2+1]));
                                        }

                                    }

                                    //TODO: check table and parameter (?)

                                }

                                System.out.println("Printing all elements in occurences");
                                for (int i = 0; i < occurences.length; i++) {
                                    System.out.println("occurences[" + i + "]: " + occurences[i]);
                                }

                                System.out.println("Printing all elements in specificCombinationValues");
                                for (int i = 0; i < specificCombinationValues.size()/*length*/; i++) {
                                    System.out.println("specificCombinationValues[" + i + "]: " + specificCombinationValues.get(i));
                                }

                                String thirdString = arrOfStr[3];
                                String fourthString = arrOfStr[4];

                                // Helper data structures to keep track of all the values to be randomly assigned
                                /*int[] set_values = null;
                                boolean[] has_set_value_been_assigned = null;*/

                                //Have another set_elements data structure
                                //ArrayList<String> set_elements_to_work_with = null;

                                //int set_elements_index = 0; //?

                                //TODO: randomly select 'occurences[i]' indices

                                if (thirdString.equals("IN") && set_names.contains(fourthString)) {
                                    //TODO: check set
                                    for (int sn = 0; sn < set_names.size(); sn++) {
                                        if (set_names.get(sn).equals(fourthString)) {
                                            set_elements_to_work_with = set_elements.get(sn);

                                            set_values = new int[set_elements.get(sn).size()];
                                            has_set_value_been_assigned = new boolean[set_elements.get(sn).size()];

                                            for (int se = 0; se < set_elements.get(sn).size(); se++) {
                                                set_values[se] = 0;
                                                has_set_value_been_assigned[se] = false;
                                            }
                                        }
                                    }
                                }

                                if (set_names.contains(fourthString)) {
                                    //TODO: all code up to listOfTestData should only be done if test data does not contain sequence

                                    //TODO
                                    for (int o = 0; o < occurences.length; o++) {
                                        int numOfCombinationValuesAssigned = 0; //TODO?
                                        while (numOfCombinationValuesAssigned != occurences[o]) {
                                            //TODO: generate a random int from 0 to set_values.length-1
                                            int randomIndex = (int) (Math.random() * set_values.length);

                                            //TODO
                                            if (!has_set_value_been_assigned[randomIndex]) {
                                                //set_values[randomIndex] = specificCombinationValues[o];
                                                set_values[randomIndex] = specificCombinationValues.get(o);
                                                has_set_value_been_assigned[randomIndex] = true;

                                                numOfCombinationValuesAssigned++;
                                            }
                                        }
                                    }

                                    for (int sv = 0; sv < set_values.length; sv++) {
                                        if (!has_set_value_been_assigned[sv]) {
                                            int table_index_of_set_element = 0;

                                            //for loop
                                            for (int tc = 0; tc < table_column_values.size(); tc++) {
                                                if (table_column_values.get(tc).equals(set_elements_to_work_with.get(sv))) {
                                                    table_index_of_set_element = tc;
                                                }
                                            }

                                            //TODO: assign random ints to any unassigned set element

                                            int constraint1 /*= 0*/;
                                            int constraint2 /*= 0*/;

                                            //TODO: 'is_table_column_value_constrained' and 'list_of_constraints'
                                            if (is_table_column_value_constrained[table_index_of_set_element]) {
                                                constraint1 = listOfConstraints.get(table_index_of_set_element)[0];
                                                constraint2 = listOfConstraints.get(table_index_of_set_element)[1];

                                                int random_set_value = (int) ((Math.random() * (constraint2 - constraint1)) + constraint1);

                                                if (!specificCombinationValues.contains(random_set_value)) {
                                                    set_values[sv] = random_set_value;
                                                    has_set_value_been_assigned[sv] = true;
                                                } else {
                                                    while (specificCombinationValues.contains(random_set_value)) {
                                                        random_set_value = (int) ((Math.random() * (constraint2 - constraint1)) + constraint1);
                                                    }

                                                    set_values[sv] = random_set_value;
                                                    has_set_value_been_assigned[sv] = true;
                                                }
                                            }

                                        }
                                    }
                                }
                            }

                            System.out.println("Printing all elements in set_values");
                            for (int sv = 0; sv < set_values.length; sv++) {
                                System.out.println("set_values at index " + sv + ": " + set_values[sv]);
                            }

                            System.out.println("Printing all elements in has_set_value_been_assigned");
                            for (int h = 0; h < has_set_value_been_assigned.length; h++) {
                                System.out.println("has_set_value_been_assigned at index " + h + ": " + has_set_value_been_assigned[h]);
                            }

                            //TODO: add to 'listOfTestData' using 'set_names' and 'set_values'
                            for (int n = 0; n < table_column_values.size(); n++) {
                                if (set_elements_to_work_with.contains(table_column_values.get(n))) {
                                    for (int se = 0; se < set_elements_to_work_with.size(); se++) {
                                        if (set_elements_to_work_with.get(se).equals(table_column_values.get(n))) {
                                            listOfTestData.get(n).add("" + set_values[se]);

                                            break;
                                        }
                                    }
                                } else {

                                    if (is_table_column_value_a_parameter[n]) { //Check that table column value is a parameter
                                        for (int tc = 0; tc < listOfTableColumnNamesAfterCombination.size(); tc++) {
                                            if (listOfTableColumnNamesAfterCombination.get(tc).equals(table_column_values.get(n))) {
                                                listOfTestData.get(n).add("" + listOfTableColumnValuesAfterCombaintion.get(tc));

                                                break;
                                            }
                                        }
                                    } else {
                                        for (int tc = 0; tc < listOfTableColumnNamesAfterCombination.size(); tc++) {
                                            if (listOfTableColumnNamesAfterCombination.get(tc).equals(table_column_values.get(n))) {
                                                if (isAnInteger(listOfTableColumnValuesAfterCombaintion.get(tc))) {
                                                    listOfTestData.get(n).add("" + listOfTableColumnValuesAfterCombaintion.get(tc));
                                                } else {
                                                    //TODO: Check that table column value is a function to be called

                                                    if (listOfTableColumnValuesAfterCombaintion.get(tc).equals(procedureToBeCalled)) {
                                                        //TODO: classOfProcedureToBeCalled, arrOfParameters, arrOfParameterTypes

                                                        Class classToBeCalled = Class.forName(classOfProcedureToBeCalled);
                                                        Class[] argsClass = new Class[arrOfParameterTypes.length];
                                                        //Class[] argsClass = new Class[] {int.class, int.class};

                                                        for (int p = 0; p < arrOfParameterTypes.length; p++) {
                                                            if (arrOfParameterTypes[p].equals("Integer")) {
                                                                argsClass[p] = int.class;
                                                            } else if (arrOfParameterTypes[p].equals("String")) {
                                                                argsClass[p] = String.class;
                                                            }
                                                        }

                                                        Object[] argsObject = new Object[arrOfParameters.length];

                                                        for (int p = 0; p < arrOfParameters.length; p++) {
                                                            //TODO: set_values and get the corresponding names from set_elements
                                                            boolean parameterIsInSet = false;
                                                            ArrayList<String> set_elements_selected_arraylist = null;

                                                            for (int se = 0; se < set_elements.size(); se++) {
                                                                if (set_elements.get(se).contains(arrOfParameters[p])) {
                                                                    parameterIsInSet = true;
                                                                    set_elements_selected_arraylist = set_elements.get(se);

                                                                    break;
                                                                }
                                                            }

                                                            if (parameterIsInSet) {
                                                                //TODO: set_elements_selected_arraylist
                                                                for (int s = 0; s < set_elements_selected_arraylist.size(); s++) {
                                                                    if (set_elements_selected_arraylist.get(s).equals(arrOfParameters[p])) {

                                                                        if (set_elements_selected_arraylist.equals(set_elements_to_work_with)) {
                                                                            argsObject[p] = set_values[s];
                                                                        }

                                                                    }
                                                                }
                                                            } else {
                                                                if (listOfTableColumnNamesAfterCombination.contains(arrOfParameters[p])) {
                                                                    for (int v = 0; v < listOfTableColumnNamesAfterCombination.size(); v++) {
                                                                        if (listOfTableColumnNamesAfterCombination.get(v).equals(arrOfParameters[p])) {
                                                                            argsObject[p] = listOfTableColumnValuesAfterCombaintion.get(v);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        Constructor constructor = classToBeCalled.getConstructor(argsClass);

                                                        //Object obj = classToBeCalled.getDeclaredConstructor().newInstance();
                                                        //Object obj = (Object) createObject();
                                                        Object object = constructor.newInstance(argsObject);

                                                        Method method = classToBeCalled.getDeclaredMethod(procedureToBeCalled, null);
                                                        String testDataToBeAdded = "" + method.invoke(object, null);

                                                        listOfTestData.get(n).add(testDataToBeAdded);

                                                        /*try{
                                                            class = Class.forName(classOfProcedureToBeCalled);
                                                        } catch (ClassNotFoundException e) {

                                                        }*/
                                                    }
                                                }

                                                break;
                                            }
                                        }
                                    }

                                }
                            }

                        } else {
                            if (arrOfSecondStringWithoutCOMBINATION[0].equals("RANDOM_")) {
                                //TODO

                                String thirdString = arrOfStr[3];
                                String fourthString = arrOfStr[4];

                                int[] set_values = null;
                                //boolean[] has_set_value_been_assigned = null;

                                ArrayList<String> set_elements_to_work_with = null;

                                if (thirdString.equals("IN") && set_names.contains(fourthString)) {
                                    for (int sn = 0; sn < set_names.size(); sn++) {
                                        if (set_names.get(sn).equals(fourthString)) {
                                            set_elements_to_work_with = set_elements.get(sn);

                                            set_values = new int[set_elements.get(sn).size()];
                                            //has_set_value_been_assigned = new boolean[set_elements.get(sn).size()];

                                            for (int se = 0; se < set_elements.get(sn).size(); se++) {
                                                set_values[se] = 0;
                                                //has_set_value_been_assigned[se] = false;
                                            }
                                        }
                                    }

                                    for (int sv = 0; sv < set_values.length; sv++) {
                                        int table_index_of_set_element = 0;

                                        for (int tc = 0; tc < table_column_values.size(); tc++) {
                                            if (table_column_values.get(tc).equals(set_elements_to_work_with.get(sv))) {
                                                table_index_of_set_element = tc;
                                            }
                                        }

                                        int constraint1;
                                        int constraint2;

                                        if (is_table_column_value_constrained[table_index_of_set_element]) {
                                            constraint1 = listOfConstraints.get(table_index_of_set_element)[0];
                                            constraint2 = listOfConstraints.get(table_index_of_set_element)[1];

                                            int random_set_value = (int) ((Math.random() * (constraint2 - constraint1)) + constraint1);

                                            set_values[sv] = random_set_value;
                                        }
                                    }

                                    //TODO: add to listOfTestData (?)
                                }

                                //TODO: add to listOfTestData
                                for (int n = 0; n < table_column_values.size(); n++) {
                                    if (set_elements_to_work_with.contains(table_column_values.get(n))) {
                                        for (int se = 0; se < set_elements_to_work_with.size(); se++) {
                                            if (set_elements_to_work_with.get(se).equals(table_column_values.get(n))) {
                                                listOfTestData.get(n).add("" + set_values[se]);

                                                break;
                                            }
                                        }
                                    } else {

                                        if (is_table_column_value_a_parameter[n]) { //Check that table column value is a parameter
                                            for (int tc = 0; tc < listOfTableColumnNamesAfterCombination.size(); tc++) {
                                                if (listOfTableColumnNamesAfterCombination.get(tc).equals(table_column_values.get(n))) {
                                                    listOfTestData.get(n).add("" + listOfTableColumnValuesAfterCombaintion.get(tc));

                                                    break;
                                                }
                                            }
                                        } else {
                                            for (int tc = 0; tc < listOfTableColumnNamesAfterCombination.size(); tc++) {
                                                if (listOfTableColumnNamesAfterCombination.get(tc).equals(table_column_values.get(n))) {
                                                    if (isAnInteger(listOfTableColumnValuesAfterCombaintion.get(tc))) {
                                                        listOfTestData.get(n).add("" + listOfTableColumnValuesAfterCombaintion.get(tc));
                                                    } else {
                                                        //TODO: Check that table column value is a function to be called

                                                        if (listOfTableColumnValuesAfterCombaintion.get(tc).equals(procedureToBeCalled)) {
                                                            //TODO: classOfProcedureToBeCalled, arrOfParameters, arrOfParameterTypes

                                                            Class classToBeCalled = Class.forName(classOfProcedureToBeCalled);
                                                            Class[] argsClass = new Class[arrOfParameterTypes.length];
                                                            //Class[] argsClass = new Class[] {int.class, int.class};

                                                            for (int p = 0; p < arrOfParameterTypes.length; p++) {
                                                                if (arrOfParameterTypes[p].equals("Integer")) {
                                                                    argsClass[p] = int.class;
                                                                } else if (arrOfParameterTypes[p].equals("String")) {
                                                                    argsClass[p] = String.class;
                                                                }
                                                            }

                                                            Object[] argsObject = new Object[arrOfParameters.length];

                                                            for (int p = 0; p < arrOfParameters.length; p++) {
                                                                //TODO: set_values and get the corresponding names from set_elements
                                                                boolean parameterIsInSet = false;
                                                                ArrayList<String> set_elements_selected_arraylist = null;

                                                                for (int se = 0; se < set_elements.size(); se++) {
                                                                    if (set_elements.get(se).contains(arrOfParameters[p])) {
                                                                        parameterIsInSet = true;
                                                                        set_elements_selected_arraylist = set_elements.get(se);

                                                                        break;
                                                                    }
                                                                }

                                                                if (parameterIsInSet) {
                                                                    //TODO: set_elements_selected_arraylist
                                                                    for (int s = 0; s < set_elements_selected_arraylist.size(); s++) {
                                                                        if (set_elements_selected_arraylist.get(s).equals(arrOfParameters[p])) {

                                                                            if (set_elements_selected_arraylist.equals(set_elements_to_work_with)) {
                                                                                argsObject[p] = set_values[s];
                                                                            }

                                                                        }
                                                                    }
                                                                } else {
                                                                    if (listOfTableColumnNamesAfterCombination.contains(arrOfParameters[p])) {
                                                                        for (int v = 0; v < listOfTableColumnNamesAfterCombination.size(); v++) {
                                                                            if (listOfTableColumnNamesAfterCombination.get(v).equals(arrOfParameters[p])) {
                                                                                argsObject[p] = listOfTableColumnValuesAfterCombaintion.get(v);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            Constructor constructor = classToBeCalled.getConstructor(argsClass);

                                                            Object object = constructor.newInstance(argsObject);

                                                            Method method = classToBeCalled.getDeclaredMethod(procedureToBeCalled, null);
                                                            String testDataToBeAdded = "" + method.invoke(object, null);

                                                            listOfTestData.get(n).add(testDataToBeAdded);

                                                        }
                                                    }

                                                    break;
                                                }
                                            }
                                        }

                                    }
                                }

                            }
                        }

                    }

                    numOfTestCases++;

                    //Have a data structure that keeps track of the number of occurrences of a particular value in combination

                    //Have another data structure that keeps track of particular values to be included in combination
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
                                        listOfTestData.get(i).add("" + ((int) (Math.random() * (Integer.parseInt(boundaryString) - 1))));

                                        numOfTestCases++;
                                    } else if (arrOfStr[2].equals("<=")) {
                                        listOfTestData.get(i).add("" + (Integer.parseInt(boundaryString) - (int) ((Math.random() * 10) + 1)));
                                        listOfTestData.get(i).add("" + Integer.parseInt(boundaryString));

                                        //TODO: check for the other table columns without fixed data?
                                        for (int j = 0; j < table_column_values.size(); j++) {
                                            if (!table_column_values.get(j).equals(tableColumnName) && !table_column_values.get(j).equals(resultTableColumnName)) {
                                                if (!is_table_column_value_fixed[j]) {
                                                    listOfTestData.get(j).add("" + ((int) (Math.random() * 10) + 1));
                                                    listOfTestData.get(j).add("" + ((int) (Math.random() * 10) + 1));
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
                                            listOfTestData.get(i).add("" + number);
                                        } else {
                                            int number = Math.abs(random.nextInt());
                                            listOfTestData.get(i).add("" + (number + Integer.parseInt(boundaryString)));
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
                                            listOfTestData.get(i).add("" + Integer.parseInt(arrOfStr[7]));
                                            listOfTestData.get(i).add("" + Integer.parseInt(arrOfStr[7]));
                                        } else {
                                            listOfTestData.get(i).add("" + Integer.parseInt(arrOfStr[7]));
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
                                                        listOfTestData.get(i).add("" + listOfFixedValues[j]);
                                                        listOfTestData.get(i).add("" + listOfFixedValues[j]);
                                                    } else {
                                                        listOfTestData.get(i).add("" + listOfFixedValues[j]);
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
                                        listOfTestData.get(i).add("" + testDataInt);
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
                                        listOfTestData.get(i).add("" + int1);
                                    } else if (table_column_values.get(i).equals(tableColumnName2)) {
                                        listOfTestData.get(i).add("" + int2);
                                    } else if (table_column_values.get(i).equals(tableColumnName3)) {
                                        listOfTestData.get(i).add("" + int3);
                                    }
                                }

                                int int4, int5, int6;

                                int5 = (int) (Math.random() * 10) + 1;
                                int6 = (int) (Math.random() * 10) + 1;
                                int4 = (int) (Math.random() * 10) + 1 + int5 + int6;

                                //TODO: do a case for '>'

                                for (int i = 0; i < table_column_values.size(); i++) {
                                    if (table_column_values.get(i).equals(tableColumnName)) {
                                        listOfTestData.get(i).add("" + int4);
                                    } else if (table_column_values.get(i).equals(tableColumnName2)) {
                                        listOfTestData.get(i).add("" + int5);
                                    } else if (table_column_values.get(i).equals(tableColumnName3)) {
                                        listOfTestData.get(i).add("" + int6);
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

                        } else if (arrOfStrLengthFromIfToThen == 6){
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
                                                listOfTestData.get(i).add("" + j);
                                                numOfTestCases++;
                                            }
                                        }
                                    } else if (table_column_values.get(i).equals(resultTableColumnName)) {
                                        if (arrOfStr[2].equals(">=") && secondBoundaryString.charAt(1) != '=') {
                                            for (int j = Integer.parseInt(firstBoundaryString); j < Integer.parseInt(secondBoundaryString.substring(1)); j++) {
                                                listOfTestData.get(i).add("" + ((int) (listOfFixedValues[listOfFixedValuesIndex] * Double.parseDouble(arrOfStr[12]))));
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // check if there are any labeled constraints (This part is good)
                            // and check what are the conditions for said labeled constraint in label_conditions

                            boolean are_there_labeled_constraints = false;
                            ArrayList<ArrayList<String>> label_conditions_to_work_with = new ArrayList<ArrayList<String>>();

                            for (int i = 1; i < arrOfStrLengthFromIfToThen; i++) {
                                String currentString = arrOfStr[i];

                                if (!(arrOfStr[i].equals("(")) && !(arrOfStr[i].equals(")"))) {
                                    currentString = currentString.replace("(", "");
                                    currentString = currentString.replace(")", "");

                                    for (int j = 0; j < label_names.size(); j++) {
                                        if (label_names.get(j).equals(currentString)) {
                                            are_there_labeled_constraints = true;

                                            label_conditions_to_work_with.add(label_conditions.get(j));
                                        }
                                    }
                                }
                            }

                            System.out.println("Are there labeled constraints: " + are_there_labeled_constraints);

                            System.out.println("Printing out label_conditions_to_work_with");
                            for (int i = 0; i < label_conditions_to_work_with.size(); i++) {
                                for (int j = 0; j < label_conditions_to_work_with.get(i).size(); j++) {
                                    System.out.println("label_conditions_to_work_with[" + i + "][" + j + "] is: " + label_conditions_to_work_with.get(i).get(j));
                                }
                            }

                            // Split the @IF annotation string into substrings by dividing the annotation with '&&' (The 'conditions' data structure is good)

                            ArrayList<String> conditions = new ArrayList<>();
                            String stringToAdd = "";

                            numOfTestCases++;

                            int numOfTestCasesInThisAnnotation = 1;

                            for (int i = 1; i < arrOfStrLengthFromIfToThen; i++) {
                                if (arrOfStr[i].equals("&&")) {
                                    conditions.add(stringToAdd);
                                    stringToAdd = "";
                                } else {
                                    if (arrOfStr[i].equals("||")) {
                                        numOfTestCases++;
                                        numOfTestCasesInThisAnnotation++;
                                    }
                                    stringToAdd += " ";
                                    stringToAdd += arrOfStr[i];

                                    if (i == arrOfStrLengthFromIfToThen - 1) {
                                        conditions.add(stringToAdd);
                                    }
                                }
                            }

                            for (int i = 0; i < conditions.size(); i++) {
                                conditions.set(i, conditions.get(i).substring(1));
                            }

                            System.out.println("All of the conditions are:");
                            for (int i = 0; i < conditions.size(); i++) {
                                System.out.println("conditions[" + i + "]: " + conditions.get(i));
                            }

                            //System.out.println("The number of test cases to be generated is: " + numOfTestCases);
                            System.out.println("The number of test cases to be generated from this annotation: " + numOfTestCasesInThisAnnotation); //This is good

                            if (numOfTestCasesInThisAnnotation == 1) {
                                ArrayList<String> testCaseStringTracker = new ArrayList<>(); //data structure that keeps track of the current a, b, and c values

                                // gather all the labeled conditions
                                ArrayList<String> label_conditions_to_work_with_arraylist = new ArrayList<>();
                                boolean there_are_labeled_conditions = false;
                                boolean there_are_labeled_conditions_to_negate = false;

                                for (int c = 0; c < conditions.size(); c++) {
                                    String[] arrOfConditionsStrings_Inner = conditions.get(c).split(" ");

                                    for (int s = 0; s < arrOfConditionsStrings_Inner.length; s++) {
                                        String currentString = arrOfConditionsStrings_Inner[s];
                                        if (!(currentString.equals("(")) && !(currentString.equals(")")) && !(currentString.equals("||"))) {
                                            currentString = currentString.replace("(", "");
                                            currentString = currentString.replace(")", "");

                                            for (int ln = 0; ln < label_names.size(); ln++) {
                                                if (currentString.equals(label_names.get(ln))) {
                                                    if (s == 0) {
                                                        there_are_labeled_conditions = true;
                                                    } else {
                                                        if (arrOfConditionsStrings_Inner[0].contains("!")) {
                                                            there_are_labeled_conditions_to_negate = true; //check if there is a negation in front of labeled condition
                                                        }
                                                    }

                                                    for (int lc = 0; lc < label_conditions_to_work_with.get(ln).size(); lc++) {
                                                        label_conditions_to_work_with_arraylist.add(label_conditions_to_work_with.get(ln).get(lc));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                for (int i = 0; i < conditions.size(); i++) {
                                    String[] arrOfConditionsStrings = conditions.get(i).split(" ");

                                    for (int j = 0; j < arrOfConditionsStrings.length; j++) {
                                        String currentString = arrOfConditionsStrings[j];

                                        if (!(arrOfConditionsStrings[j].equals("(")) && !(arrOfConditionsStrings[j].equals(")"))) {
                                            currentString = currentString.replace("(", "");
                                            currentString = currentString.replace(")", "");

                                            String testCaseString = "";

                                            boolean relation_string_has_been_traversed = false;

                                            //check if 'currentString' is a labeled condition or not
                                            boolean currentStringIsLabeledCondition = false;

                                            for (int ln = 0; ln < label_names.size(); ln++) {
                                                if (label_names.get(ln).equals(currentString)) {
                                                    currentStringIsLabeledCondition = true;
                                                }
                                            }

                                            if (!currentStringIsLabeledCondition) {
                                                for (int char_i = 0; char_i < currentString.length(); char_i++) {
                                                    if (currentString.charAt(char_i) == '=' || currentString.charAt(char_i) == '!') {
                                                        if (testCaseString.length() > 0 && !testCaseStringTracker.contains(testCaseString)) {
                                                            testCaseStringTracker.add(testCaseString);
                                                        }

                                                        testCaseString = "";
                                                    } else {
                                                        testCaseString += currentString.charAt(char_i);
                                                    }

                                                    if (char_i == currentString.length() - 1) {
                                                        if (testCaseString.length() > 0 && !testCaseStringTracker.contains(testCaseString)) {
                                                            testCaseStringTracker.add(testCaseString);
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }

                                System.out.println("Printing all values in testCaseStringTracker:");
                                for (int i = 0; i < testCaseStringTracker.size(); i++) {
                                    System.out.println("testCaseStringTracker at index " + i + " is: " + testCaseStringTracker.get(i));
                                }

                                int[] testCaseValueTracker = new int[testCaseStringTracker.size()];
                                boolean[] testCaseValueAssignedTracker = new boolean[testCaseStringTracker.size()];

                                for (int i = 0; i < testCaseValueTracker.length; i++) {
                                    testCaseValueTracker[i] = 0;
                                    testCaseValueAssignedTracker[i] = false;
                                }

                                ArrayList<String> testCaseConditionStrings = new ArrayList<>();

                                for (int i = 0; i < conditions.size(); i++) {
                                    String[] arrOfConditionsStrings = conditions.get(i).split(" ");

                                    for (int j = 0; j < arrOfConditionsStrings.length; j++) {
                                        String currentString = arrOfConditionsStrings[j];

                                        if (!(currentString.equals("(")) && !(currentString.equals(")")) && !(currentString.equals("(!"))) {
                                            currentString = currentString.replace("(", "");
                                            currentString = currentString.replace(")", "");

                                            boolean currentStringIsLabeledCondition = false;

                                            for (int ln = 0; ln < label_names.size(); ln++) {
                                                if (currentString.equals(label_names.get(ln))) {
                                                    currentStringIsLabeledCondition = true;
                                                }
                                            }

                                            if (!currentStringIsLabeledCondition) {
                                                testCaseConditionStrings.add(currentString);
                                            }

                                        }
                                    }
                                }

                                for (int i = 0; i < conditions.size(); i++) {
                                    String[] arrOfConditionsStrings = conditions.get(i).split(" ");

                                    for (int j = 0; j < arrOfConditionsStrings.length; j++) {
                                        String currentConditionString = arrOfConditionsStrings[j];
                                        boolean checkThisConditionString = false;

                                        if (!arrOfConditionsStrings[j].equals("(") && !(arrOfConditionsStrings[j].equals(")"))) {
                                            currentConditionString = currentConditionString.replace("(", "");
                                            currentConditionString = currentConditionString.replace(")", "");

                                            checkThisConditionString = true;
                                        }

                                        // check that 'currentConditionString' is not a labeled condition
                                        for (int ln = 0; ln < label_names.size(); ln++) {
                                            if (label_names.get(ln).equals(currentConditionString)) {
                                                checkThisConditionString = false;
                                            }
                                        }

                                        if (checkThisConditionString) {
                                            //boolean isNotEmpty = false; (ignore this)

                                            // have a helper variable that keeps track of the number of test case values that were assigned

                                            int numOfTestCaseValuesAssigned = 0;

                                            for (int b = 0; b < testCaseValueAssignedTracker.length; b++) {
                                                if (testCaseValueAssignedTracker[b] == true) {
                                                    numOfTestCaseValuesAssigned++;
                                                }
                                            }

                                            //Need to get 'left_value', 'right_value', and 'relation'
                                            String relation = ""; //ie. '==', '!='
                                            String left_value_string = "";
                                            String right_value_string = "";

                                            boolean relation_string_has_been_traversed = false;

                                            // check 'currentConditionString'
                                            for (int char_i = 0; char_i < currentConditionString.length(); char_i++) { //This part appears good
                                                if (currentConditionString.charAt(char_i) == '=' || currentConditionString.charAt(char_i) == '!') {
                                                    relation += currentConditionString.charAt(char_i);
                                                    relation_string_has_been_traversed = true;
                                                } else {
                                                    if (relation_string_has_been_traversed) {
                                                        right_value_string += currentConditionString.charAt(char_i);
                                                    } else {
                                                        left_value_string += currentConditionString.charAt(char_i);
                                                    }
                                                }
                                            }

                                            if (numOfTestCaseValuesAssigned == 0) { // If 'numOfTestCaseValuesAssigned' == 0, then just generate random ints for the two specific values

                                                int left_value = (int) (Math.random() * 10);
                                                left_value += 2;

                                                int right_value = 0;

                                                if (relation.equals("!=")) {
                                                    boolean two_values_are_not_equal = false;

                                                    while(!two_values_are_not_equal) {
                                                        right_value = (int) (Math.random() * 10);
                                                        right_value += 2;

                                                        if (left_value != right_value) {
                                                            two_values_are_not_equal = true;
                                                        }
                                                    }
                                                }

                                                for (int tc = 0; tc < testCaseStringTracker.size(); tc++) {
                                                    if (testCaseStringTracker.get(tc).equals(left_value_string)) {
                                                        testCaseValueTracker[tc] = left_value;
                                                        testCaseValueAssignedTracker[tc] = true;
                                                    } else if (testCaseStringTracker.get(tc).equals(right_value_string)) {
                                                        testCaseValueTracker[tc] = right_value;
                                                        testCaseValueAssignedTracker[tc] = true;
                                                    }
                                                }

                                                // add the left and right values to listOfTestData
                                                for (int n = 0; n < table_column_values.size(); n++) {
                                                    if (table_column_values.get(n).equals(left_value_string)) {
                                                        listOfTestData.get(n).add("" + left_value);
                                                    } else if (table_column_values.get(n).equals(right_value_string)) {
                                                        listOfTestData.get(n).add("" + right_value);
                                                    }
                                                }

                                            } else {

                                                String testCaseStringToWorkWith = "";
                                                int testDataInt = 0;

                                                boolean all_conditions_satisfied = false;

                                                // First use a while loop to check that generated value for specific test case satisfies all conditions
                                                while(!all_conditions_satisfied) {
                                                    all_conditions_satisfied = true;

                                                    testCaseStringToWorkWith = "";

                                                    // Generate some random int
                                                    testDataInt  = (int) (Math.random() * 10);

                                                    for (int tc = 0; tc < testCaseStringTracker.size(); tc++) {
                                                        if (testCaseStringTracker.get(tc).equals(left_value_string)) {
                                                            if (testCaseValueAssignedTracker[tc] == false) {
                                                                testCaseStringToWorkWith = left_value_string;
                                                            }
                                                        } else if (testCaseStringTracker.get(tc).equals(right_value_string)) {
                                                            if (testCaseValueAssignedTracker[tc] == false) {
                                                                testCaseStringToWorkWith = right_value_string;
                                                            }
                                                        }
                                                    }

                                                    // Conditions will be iterated using a for loop, to see if randomly-generated int satisfies all conditions
                                                    for (int tc = 0; tc < testCaseConditionStrings.size(); tc++) { //'testCaseConditionStrings' contains the different possible test cases
                                                        String relation_tccs = ""; //ie. '==', '!='
                                                        String left_value_string_tccs = "";
                                                        String right_value_string_tccs = "";

                                                        boolean relation_tccs_string_has_been_traversed = false;

                                                        // check 'currentConditionString'
                                                        for (int char_i = 0; char_i < testCaseConditionStrings.get(tc).length(); char_i++) { //This part appears good
                                                            if (testCaseConditionStrings.get(tc).charAt(char_i) == '=' || testCaseConditionStrings.get(tc).charAt(char_i) == '!') {
                                                                relation_tccs += testCaseConditionStrings.get(tc).charAt(char_i);
                                                                relation_tccs_string_has_been_traversed = true;
                                                            } else {
                                                                if (relation_tccs_string_has_been_traversed) {
                                                                    right_value_string_tccs += testCaseConditionStrings.get(tc).charAt(char_i);
                                                                } else {
                                                                    left_value_string_tccs += testCaseConditionStrings.get(tc).charAt(char_i);
                                                                }
                                                            }
                                                        }

                                                        // For each condition, keep track of what is 'value1', 'value2', and 'relation'

                                                        if (relation_tccs.equals("!=")) { // Check if 'relation' is '!='
                                                            if (testCaseStringToWorkWith.equals(left_value_string_tccs)) {
                                                                int right_value = 0;

                                                                for (int tcst = 0; tcst < testCaseStringTracker.size(); tcst++) {
                                                                    if (testCaseStringTracker.get(tcst).equals(right_value_string_tccs)) {
                                                                        right_value = testCaseValueTracker[tcst];
                                                                    }
                                                                }

                                                                if (testDataInt == right_value) {
                                                                    all_conditions_satisfied = false;
                                                                }

                                                            } else if (testCaseStringToWorkWith.equals(right_value_string_tccs)) {
                                                                int left_value = 0;

                                                                for (int tcst = 0; tcst < testCaseStringTracker.size(); tcst++) {
                                                                    if (testCaseStringTracker.get(tcst).equals(left_value_string_tccs)) {
                                                                        left_value = testCaseValueTracker[tcst];
                                                                    }
                                                                }

                                                                if (testDataInt == left_value) {
                                                                    all_conditions_satisfied = false;
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // Check if there are labeled conditions to negate using 'there_are_labeled_conditions' and 'there_are_labeled_conditions_to_negate'
                                                    if (there_are_labeled_conditions_to_negate) {
                                                        for (int lca = 0; lca < label_conditions_to_work_with.size(); lca++) {
                                                            for (int lc = 0; lc < label_conditions_to_work_with.get(lca).size(); lc++) {
                                                                String[] arrOfLabelConditionString = label_conditions_to_work_with.get(lca).get(lc).split(" ");

                                                                int leftSideOfInequality = 0;
                                                                int rightSideOfInequality = 0;

                                                                if (arrOfLabelConditionString[0].equals(testCaseStringToWorkWith)) {
                                                                    leftSideOfInequality += testDataInt;
                                                                } else {
                                                                    for (int tcvt = 0; tcvt < testCaseStringTracker.size(); tcvt++) {
                                                                        if (arrOfLabelConditionString[0].equals(testCaseStringTracker.get(tcvt))) {
                                                                            leftSideOfInequality += testCaseValueTracker[tcvt];
                                                                        }
                                                                    }
                                                                }

                                                                if (arrOfLabelConditionString[2].equals(testCaseStringToWorkWith)) {
                                                                    rightSideOfInequality += testDataInt;
                                                                } else {
                                                                    for (int tcvt = 0; tcvt < testCaseStringTracker.size(); tcvt++) {
                                                                        if (arrOfLabelConditionString[2].equals(testCaseStringTracker.get(tcvt))) {
                                                                            rightSideOfInequality += testCaseValueTracker[tcvt];
                                                                        }
                                                                    }
                                                                }

                                                                if (arrOfLabelConditionString.length > 3) {
                                                                    for (int lcs = 3; lcs < arrOfLabelConditionString.length; lcs++) {
                                                                        if (arrOfLabelConditionString[lcs].equals("+")) {
                                                                            if (arrOfLabelConditionString[lcs + 1].equals(testCaseStringToWorkWith)) {
                                                                                rightSideOfInequality += testDataInt;
                                                                            } else {
                                                                                for (int tcvt = 0; tcvt < testCaseStringTracker.size(); tcvt++) {
                                                                                    if (arrOfLabelConditionString[lcs + 1].equals(testCaseStringTracker.get(tcvt))) {
                                                                                        rightSideOfInequality += testCaseValueTracker[tcvt];
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                if (arrOfLabelConditionString[1].equals(">=")) {
                                                                    if (leftSideOfInequality >= rightSideOfInequality) {
                                                                        all_conditions_satisfied = false;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                }

                                                // Add to 'testCaseValueTracker' and 'testCaseValueAssignedTracker'
                                                for (int tcs = 0; tcs < testCaseStringTracker.size(); tcs++) {
                                                    if (testCaseStringTracker.get(tcs).equals(testCaseStringToWorkWith)) {
                                                        testCaseValueTracker[tcs] = testDataInt;
                                                        testCaseValueAssignedTracker[tcs] = true;
                                                    }
                                                }

                                                // Add to 'listOfTestData' once generated value for specific test case satisfies all conditions
                                                for (int n = 0; n < table_column_values.size(); n++) {
                                                    if (table_column_values.get(n).equals(testCaseStringToWorkWith)) {
                                                        listOfTestData.get(n).add("" + testDataInt);
                                                    }
                                                }
                                            }

                                            /*for (int tcv = 0; tcv < testCaseValueTracker.length; tcv++) { //This block of code may not work
                                                if (testCaseValueTracker[tcv] != null) {
                                                    isNotEmpty = true;
                                                    break;
                                                }
                                            }*/

                                        }
                                    }
                                }

                            } else {
                                for (int i = 0; i < conditions.size(); i++) {
                                    //TODO: check if any of the strings in conditions contains "||"
                                    if (conditions.get(i).contains("||")) {

                                        //Need to have list of strings
                                        String[] arrOfConditionsStrings = conditions.get(i).split(" ");

                                        //TODO: have arraylist of the different possible test cases that are in arrOfConditionsStrings
                                        ArrayList<String> testCaseConditionStrings = new ArrayList<>(); //This data structure is correct

                                        for (int j = 0; j < arrOfConditionsStrings.length; j++) {
                                            String currentString = arrOfConditionsStrings[j];
                                            if (!(arrOfConditionsStrings[j].equals("(")) && !(arrOfConditionsStrings[j].equals(")")) && !(arrOfConditionsStrings[j].equals("||"))) {
                                                currentString = currentString.replace("(", "");
                                                currentString = currentString.replace(")", "");

                                                testCaseConditionStrings.add(currentString);
                                            }
                                        }

                                        System.out.println("Printing out testCaseConditionStrings");
                                        for (int j = 0; j < testCaseConditionStrings.size(); j++) {
                                            System.out.println("testCaseConditionStrings[" + j + "]: " + testCaseConditionStrings.get(j));
                                        }

                                        //TODO: add to listOfTestData in below for loop
                                        for (int j = 0; j < numOfTestCasesInThisAnnotation; j++) {
                                            //look at the possible test cases in 'testCaseConditionStrings'
                                            String relation = ""; //ie. '==', '!='
                                            String value1 = "";
                                            String value2 = "";

                                            boolean relation_string_has_been_traversed = false;

                                            for (int char_i = 0; char_i < testCaseConditionStrings.get(j).length(); char_i++) { //This part appears good
                                                if (testCaseConditionStrings.get(j).charAt(char_i) == '=' || testCaseConditionStrings.get(j).charAt(char_i) == '!') {
                                                    relation += testCaseConditionStrings.get(j).charAt(char_i);
                                                    relation_string_has_been_traversed = true;
                                                } else {
                                                    if (relation_string_has_been_traversed) {
                                                        value2 += testCaseConditionStrings.get(j).charAt(char_i);
                                                    } else {
                                                        value1 += testCaseConditionStrings.get(j).charAt(char_i);
                                                    }
                                                }
                                            }

                                            System.out.println("The value to the left of the relation is: " + value1);
                                            System.out.println("The relation is: " + relation);
                                            System.out.println("The value to the right of the relation is: " + value2);

                                            if (relation.equals("==")) {
                                                // generate same random int for both value1 and value2 (This part seems good)
                                                int testDataInt = (int) (Math.random() * 10);
                                                testDataInt += 2;
                                                int value1Int = testDataInt;
                                                int value2Int = testDataInt;

                                                //TODO: label_names and label_conditions (this is good for the time being)
                                                boolean there_are_labeled_conditions = false;
                                                boolean there_are_labeled_conditions_to_negate = false;
                                                //TODO: have an arraylist of the different possible conditions (this is good)
                                                ArrayList<String> label_conditions_to_work_with_arraylist = new ArrayList<>();

                                                for (int c = 0; c < conditions.size(); c++) {
                                                    String[] arrOfConditionsStrings_Inner = conditions.get(c).split(" ");

                                                    for (int s = 0; s < arrOfConditionsStrings_Inner.length; s++) {
                                                        String currentString = arrOfConditionsStrings_Inner[s];
                                                        if (!(currentString.equals("(")) && !(currentString.equals(")")) && !(currentString.equals("||"))) {
                                                            currentString = currentString.replace("(", "");
                                                            currentString = currentString.replace(")", "");

                                                            //TODO: test this
                                                            for (int ln = 0; ln < label_names.size(); ln++) {
                                                                if (currentString.equals(label_names.get(ln))) {
                                                                    if (s == 0) {
                                                                        there_are_labeled_conditions = true;
                                                                    } else {
                                                                        if (arrOfConditionsStrings_Inner[0].contains("!")) {
                                                                            there_are_labeled_conditions_to_negate = true; //check if there is a negation in front of labeled condition
                                                                        }
                                                                    }

                                                                    for (int lc = 0; lc < label_conditions_to_work_with.get(ln).size(); lc++) {
                                                                        label_conditions_to_work_with_arraylist.add(label_conditions_to_work_with.get(ln).get(lc));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                System.out.println("Are there labeled conditions: " + there_are_labeled_conditions);
                                                System.out.println("Are there labeled conditions to negate: " + there_are_labeled_conditions_to_negate);

                                                System.out.println("Printing out all items in label_conditions_to_work_with_arraylist:");
                                                for (int c = 0; c < label_conditions_to_work_with_arraylist.size(); c++) {
                                                    System.out.println("label_conditions_to_work_with_arraylist at index " + c + " is: " + label_conditions_to_work_with_arraylist.get(c));
                                                }

                                                //TODO: iterate through all conditions in 'conditions'
                                                for (int c = 0; c < conditions.size(); c++) {
                                                    String[] arrOfConditionsStrings_Inner = conditions.get(c).split(" ");

                                                    System.out.println("Printing out arrOfConditionsStrings_Inner:");
                                                    for (int aocs_i = 0; aocs_i < arrOfConditionsStrings_Inner.length; aocs_i++) {
                                                        System.out.println("arrOfConditionsStrings_Inner at index " + aocs_i + " is: " + arrOfConditionsStrings_Inner[aocs_i]);
                                                    }

                                                    boolean checkThisConditionString = true;

                                                    for (int s = 0; s < arrOfConditionsStrings_Inner.length; s++) {
                                                        String currentString = arrOfConditionsStrings_Inner[s];

                                                        if (currentString.equals("||")) {
                                                            checkThisConditionString = false;
                                                        }

                                                        currentString = currentString.replace("(", "");
                                                        currentString = currentString.replace(")", "");

                                                        for (int ln = 0; ln < label_names.size(); ln++) {
                                                            if (label_names.get(ln).equals(currentString)) {
                                                                checkThisConditionString = false;
                                                            }
                                                        }
                                                    }

                                                    if (checkThisConditionString) {
                                                        // check if there is negation by checking arrOfConditionsStrings_Inner[0]
                                                        boolean there_is_negation = false;

                                                        for (int char_i = 0; char_i < arrOfConditionsStrings_Inner[0].length(); char_i++) {
                                                            if (arrOfConditionsStrings_Inner[0].charAt(char_i) == '!') {
                                                                if (arrOfConditionsStrings_Inner[0].charAt(char_i + 1) == '(') {
                                                                    there_is_negation = true;
                                                                }
                                                            }
                                                        }

                                                        //TODO: check the current condition as well as making sure any labeled conditions are still satisfied

                                                        ////

                                                        String previous_value_checked = "";

                                                        // iterate through all values of current condition
                                                        for (int s = 0; s < arrOfConditionsStrings_Inner.length; s += 2) {
                                                            String current_value_being_checked = arrOfConditionsStrings_Inner[s];

                                                            if (s == 0) {
                                                                current_value_being_checked = current_value_being_checked.replace("(", "");
                                                                current_value_being_checked = current_value_being_checked.replace("!", "");

                                                                // check if first value is either value1 or value2
                                                                if (!(current_value_being_checked.equals(value1)) && !(current_value_being_checked.equals(value2))) {
                                                                    // if arrOfConditionsStrings_Inner[2] is either value1 or value2
                                                                    if (arrOfConditionsStrings_Inner[2].equals(value1) || arrOfConditionsStrings_Inner[2].equals(value2)) {
                                                                        if (arrOfConditionsStrings_Inner[1].equals("==")) {
                                                                            if (relation.equals("==") && there_is_negation) {

                                                                                if (there_are_labeled_conditions) {
                                                                                    boolean all_labeled_conditions_are_satisfied = false;

                                                                                    //TODO: label_conditions_to_work_with_arraylist
                                                                                } else if (there_are_labeled_conditions_to_negate) { //checks if there is a negation in front of labeled condition
                                                                                    boolean all_labeled_conditions_are_negated = false;

                                                                                    // generate a random int that is different than value1Int and satisfies the labeled conditions (using label_names and label_conditions)
                                                                                    while (!all_labeled_conditions_are_negated) {
                                                                                        int testDataInt_Inner  = (int) (Math.random() * 10);

                                                                                        boolean there_is_labeled_condition_not_negated = false;

                                                                                        if (testDataInt_Inner != value1Int) {
                                                                                            //TODO: label_conditions_to_work_with
                                                                                            for (int lca = 0; lca < label_conditions_to_work_with.size(); lca++) {
                                                                                                for (int lc = 0; lc < label_conditions_to_work_with.get(lca).size(); lc++) {
                                                                                                    String[] arrOfLabelConditionString = label_conditions_to_work_with_arraylist.get(lc).split(" ");

                                                                                                    int leftSideOfInequality = 0;
                                                                                                    int rightSideOfInequality = 0;

                                                                                                    // if left side is value1 or value2
                                                                                                    if (arrOfLabelConditionString[0].equals(value1) || arrOfLabelConditionString[0].equals(value2)) {
                                                                                                        leftSideOfInequality = value1Int;
                                                                                                    } else if (arrOfLabelConditionString[0].equals(current_value_being_checked)) {
                                                                                                        leftSideOfInequality = testDataInt_Inner;
                                                                                                    }

                                                                                                    if (arrOfLabelConditionString[2].equals(value1) || arrOfLabelConditionString[2].equals(value2)) {
                                                                                                        rightSideOfInequality = value1Int;
                                                                                                    } else if (arrOfLabelConditionString[2].equals(current_value_being_checked)) {
                                                                                                        rightSideOfInequality = testDataInt_Inner;
                                                                                                    }

                                                                                                    if (arrOfLabelConditionString.length > 3) {
                                                                                                        for (int lcs = 3; lcs < arrOfLabelConditionString.length; lcs++) {
                                                                                                            if (arrOfLabelConditionString[lcs].equals("+")) {
                                                                                                                if (arrOfLabelConditionString[lcs + 1].equals(value1) || arrOfLabelConditionString[lcs + 1].equals(value2)) {
                                                                                                                    rightSideOfInequality += value1Int;
                                                                                                                } else if (arrOfLabelConditionString[lcs + 1].equals(current_value_being_checked)) {
                                                                                                                    rightSideOfInequality += testDataInt_Inner;
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }

                                                                                                    if (arrOfLabelConditionString[1].equals(">=")) {
                                                                                                        if (leftSideOfInequality >= rightSideOfInequality) {
                                                                                                            there_is_labeled_condition_not_negated = true;
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                            /*for (int lc = 0; lc < label_conditions_to_work_with_arraylist.size(); lc++) {
                                                                                                String[] arrOfLabelConditionString = label_conditions_to_work_with_arraylist.get(lc).split(" ");

                                                                                                int leftSideOfInequality;
                                                                                                int rightSideOfInequality;

                                                                                                // if left side is value1 or value2
                                                                                                if (arrOfLabelConditionString[0].equals(value1) || arrOfLabelConditionString[0].equals(value2)) {
                                                                                                    leftSideOfInequality = value1Int;
                                                                                                } else if (arrOfLabelConditionString[0].equals()) {
                                                                                                    
                                                                                                }

                                                                                                if (arrOfLabelConditionString[1].equals(">=")) {

                                                                                                }
                                                                                            }*/
                                                                                        }

                                                                                        if (!there_is_labeled_condition_not_negated) {
                                                                                            all_labeled_conditions_are_negated = true;

                                                                                            // add to listOfTestData
                                                                                            for (int n = 0; n < table_column_values.size(); n++) {
                                                                                                if (table_column_values.get(n).equals(value1) || table_column_values.get(n).equals(value2)) {
                                                                                                    listOfTestData.get(n).add("" + value1Int);
                                                                                                } else if (table_column_values.get(n).equals(current_value_being_checked)) {
                                                                                                    listOfTestData.get(n).add("" + testDataInt_Inner);
                                                                                                }
                                                                                            }
                                                                                            
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                previous_value_checked = current_value_being_checked;

                                                            } else if (s == arrOfConditionsStrings_Inner.length - 1) { //if this is the last string
                                                                current_value_being_checked = current_value_being_checked.replace(")", "");

                                                                if (!(current_value_being_checked.equals(value1)) && !(current_value_being_checked.equals(value2))) {
                                                                    if (arrOfConditionsStrings_Inner[s-2].equals(value1) || arrOfConditionsStrings_Inner[s-2].equals(value2)) {
                                                                        if (arrOfConditionsStrings_Inner[s-1].equals("==")) {
                                                                            if (relation.equals("==") && there_is_negation) {
                                                                                if (there_are_labeled_conditions) {
                                                                                    boolean all_labeled_conditions_are_satisfied = false;

                                                                                    //TODO: label_conditions_to_work_with_arraylist
                                                                                } else if (there_are_labeled_conditions_to_negate) { //checks if there is a negation in front of labeled condition
                                                                                    boolean all_labeled_conditions_are_negated = false;

                                                                                    // generate a random int that is different than value1Int and satisfies the labeled conditions (using label_names and label_conditions)
                                                                                    while (!all_labeled_conditions_are_negated) {
                                                                                        int testDataInt_Inner  = (int) (Math.random() * 10);

                                                                                        boolean there_is_labeled_condition_not_negated = false;

                                                                                        if (testDataInt_Inner != value1Int) {
                                                                                            for (int lca = 0; lca < label_conditions_to_work_with.size(); lca++) {
                                                                                                for (int lc = 0; lc < label_conditions_to_work_with.get(lca).size(); lc++) {
                                                                                                    String[] arrOfLabelConditionString = label_conditions_to_work_with_arraylist.get(lc).split(" ");

                                                                                                    int leftSideOfInequality = 0;
                                                                                                    int rightSideOfInequality = 0;

                                                                                                    // if left side is value1 or value2
                                                                                                    if (arrOfLabelConditionString[0].equals(value1) || arrOfLabelConditionString[0].equals(value2)) {
                                                                                                        leftSideOfInequality = value1Int;
                                                                                                    } else if (arrOfLabelConditionString[0].equals(current_value_being_checked)) {
                                                                                                        leftSideOfInequality = testDataInt_Inner;
                                                                                                    }

                                                                                                    if (arrOfLabelConditionString[2].equals(value1) || arrOfLabelConditionString[2].equals(value2)) {
                                                                                                        rightSideOfInequality = value1Int;
                                                                                                    } else if (arrOfLabelConditionString[2].equals(current_value_being_checked)) {
                                                                                                        rightSideOfInequality = testDataInt_Inner;
                                                                                                    }

                                                                                                    if (arrOfLabelConditionString.length > 3) {
                                                                                                        for (int lcs = 3; lcs < arrOfLabelConditionString.length; lcs++) {
                                                                                                            if (arrOfLabelConditionString[lcs].equals("+")) {
                                                                                                                if (arrOfLabelConditionString[lcs + 1].equals(value1) || arrOfLabelConditionString[lcs + 1].equals(value2)) {
                                                                                                                    rightSideOfInequality += value1Int;
                                                                                                                } else if (arrOfLabelConditionString[lcs + 1].equals(current_value_being_checked)) {
                                                                                                                    rightSideOfInequality += testDataInt_Inner;
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }

                                                                                                    if (arrOfLabelConditionString[1].equals(">=")) {
                                                                                                        if (leftSideOfInequality >= rightSideOfInequality) {
                                                                                                            there_is_labeled_condition_not_negated = true;
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                        if (!there_is_labeled_condition_not_negated) {
                                                                                            all_labeled_conditions_are_negated = true;

                                                                                            // add to listOfTestData
                                                                                            for (int n = 0; n < table_column_values.size(); n++) {
                                                                                                if (table_column_values.get(n).equals(value1) || table_column_values.get(n).equals(value2)) {
                                                                                                    listOfTestData.get(n).add("" + value1Int);
                                                                                                } else if (table_column_values.get(n).equals(current_value_being_checked)) {
                                                                                                    listOfTestData.get(n).add("" + testDataInt_Inner);
                                                                                                }
                                                                                            }

                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } else { //if this is a string in between

                                                                if (!(current_value_being_checked.equals(value1)) && !(current_value_being_checked.equals(value2))) {
                                                                    //compare between current_value_being_checked and previous_value_checked
                                                                    if (previous_value_checked.equals(value1) || previous_value_checked.equals(value2)) { //checks if 'previous value' is either value1 or value2
                                                                        if (arrOfConditionsStrings_Inner[s-1].equals("==")) {
                                                                            if (relation.equals("==") && there_is_negation) {
                                                                                if (there_are_labeled_conditions) {
                                                                                    boolean all_labeled_conditions_are_satisfied = false;

                                                                                    //TODO: label_conditions_to_work_with_arraylist
                                                                                } else if (there_are_labeled_conditions_to_negate) { //checks if there is a negation in front of labeled condition
                                                                                    boolean all_labeled_conditions_are_negated = false;

                                                                                    // generate a random int that is different than value1Int and satisfies the labeled conditions (using label_names and label_conditions)
                                                                                    while (!all_labeled_conditions_are_negated) {
                                                                                        int testDataInt_Inner  = (int) (Math.random() * 10);

                                                                                        boolean there_is_labeled_condition_not_negated = false;

                                                                                        if (testDataInt_Inner != value1Int) {
                                                                                            for (int lca = 0; lca < label_conditions_to_work_with.size(); lca++) {
                                                                                                for (int lc = 0; lc < label_conditions_to_work_with.get(lca).size(); lc++) {
                                                                                                    String[] arrOfLabelConditionString = label_conditions_to_work_with_arraylist.get(lc).split(" ");

                                                                                                    int leftSideOfInequality = 0;
                                                                                                    int rightSideOfInequality = 0;

                                                                                                    // if left side is value1 or value2
                                                                                                    if (arrOfLabelConditionString[0].equals(value1) || arrOfLabelConditionString[0].equals(value2)) {
                                                                                                        leftSideOfInequality = value1Int;
                                                                                                    } else if (arrOfLabelConditionString[0].equals(current_value_being_checked)) {
                                                                                                        leftSideOfInequality = testDataInt_Inner;
                                                                                                    }

                                                                                                    if (arrOfLabelConditionString[2].equals(value1) || arrOfLabelConditionString[2].equals(value2)) {
                                                                                                        rightSideOfInequality = value1Int;
                                                                                                    } else if (arrOfLabelConditionString[2].equals(current_value_being_checked)) {
                                                                                                        rightSideOfInequality = testDataInt_Inner;
                                                                                                    }

                                                                                                    if (arrOfLabelConditionString.length > 3) {
                                                                                                        for (int lcs = 3; lcs < arrOfLabelConditionString.length; lcs++) {
                                                                                                            if (arrOfLabelConditionString[lcs].equals("+")) {
                                                                                                                if (arrOfLabelConditionString[lcs + 1].equals(value1) || arrOfLabelConditionString[lcs + 1].equals(value2)) {
                                                                                                                    rightSideOfInequality += value1Int;
                                                                                                                } else if (arrOfLabelConditionString[lcs + 1].equals(current_value_being_checked)) {
                                                                                                                    rightSideOfInequality += testDataInt_Inner;
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }

                                                                                                    if (arrOfLabelConditionString[1].equals(">=")) {
                                                                                                        if (leftSideOfInequality >= rightSideOfInequality) {
                                                                                                            there_is_labeled_condition_not_negated = true;
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                        if (!there_is_labeled_condition_not_negated) { //TODO
                                                                                            all_labeled_conditions_are_negated = true;

                                                                                            // add to listOfTestData
                                                                                            for (int n = 0; n < table_column_values.size(); n++) {
                                                                                                if (table_column_values.get(n).equals(value1) || table_column_values.get(n).equals(value2)) {
                                                                                                    listOfTestData.get(n).add("" + value1Int);
                                                                                                } else if (table_column_values.get(n).equals(current_value_being_checked)) {
                                                                                                    listOfTestData.get(n).add("" + testDataInt_Inner);
                                                                                                }
                                                                                            }

                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                previous_value_checked = current_value_being_checked;
                                                            }
                                                        }

                                                        //TODO: have an arraylist or some other data structure that keeps track of all the test data that still needs to be added

                                                        //TODO: add to 'listOfTestData' (DONE)

                                                        /*for (int s = 0; s < arrOfConditionsStrings_Inner.length; s++) {
                                                            String currentString = arrOfConditionsStrings_Inner[s];
                                                            if (!(currentString.equals("(")) && !(currentString.equals(")")) && !(currentString.equals("||"))) {
                                                                currentString = currentString.replace("(", "");
                                                                currentString = currentString.replace(")", "");

                                                                //TODO
                                                            }
                                                        }*/
                                                    }

                                                }

                                                //TODO: have while loop so that test cases are generated until the appropriate ones are generated

                                            }

                                        }

                                    }
                                }
                            }

                            // if there are any '||' text, that means multiple test cases will have to be generated from the same @IF annotation
                            // numOfTestCases will equal to the number of '||' until either '&&' or 'THEN' is reached
                            // Need to keep track of the particular variables for each different test case (perhaps in a double arraylist of strings?)

                            // ***Use a while loop to determine when a generated test case satisfies all the constraints***

                            //TODO: check how many conditions there are (?)

                            // If expected result is a string, add to 'listOfStringTestData' numOfTestCases times

                            //TODO
                            System.out.println("Adding result test data value(s)");

                            boolean lastStringIsAnInt = true;

                            //System.out.println("arrOfStr[arrOfStrLengthFromIfToThen + 3] is: " + arrOfStr[arrOfStrLengthFromIfToThen + 3]);

                            if (arrOfStr[arrOfStrLengthFromIfToThen + 4].charAt(0) == '‘') {
                                lastStringIsAnInt = false;
                            }

                            System.out.println("Is last string an int: " + lastStringIsAnInt);

                            if (lastStringIsAnInt) {

                            } else {
                                System.out.println("Last string is NOT an int");

                                String testDataStringToAdd = "";

                                resultTableColumnName = arrOfStr[arrOfStrLengthFromIfToThen + 2];

                                for (int i = arrOfStrLengthFromIfToThen + /*3*/4; i < arrOfStr.length; i++) {
                                    if (i == arrOfStrLengthFromIfToThen + /*3*/4) {
                                        testDataStringToAdd += arrOfStr[i].replace("‘", "");
                                    } else if (i == arrOfStr.length - 1) {
                                        testDataStringToAdd += " ";
                                        testDataStringToAdd += arrOfStr[i].replace("’", "");
                                    } else {
                                        testDataStringToAdd += " ";
                                        testDataStringToAdd += arrOfStr[i];
                                    }
                                }

                                for (int i = 0; i < table_column_values.size(); i++) {
                                    if (table_column_values.get(i).equals(resultTableColumnName)) {
                                        for (int j = 0; j < numOfTestCasesInThisAnnotation; j++) {
                                            listOfStringTestData.get(i).add(testDataStringToAdd);
                                        }

                                        is_table_column_value_string[i] = true;

                                        break;
                                    }
                                }
                            }
                        }

                    }

                } else if (arrOfStr[0].equals("Examples:")) {
                    stringToBeWritten += table_string;

                    System.out.println("The number of test cases is: " + numOfTestCases); //This is correct

                    System.out.println("Printing out all elements in listOfStringTestData");
                    for (int i = 0; i < listOfStringTestData.size(); i++) {
                        for (int j = 0; j < listOfStringTestData.get(i).size(); j++) {
                            System.out.println("listOfStringTestData at index " + i + " then " + j + " is: " + listOfStringTestData.get(i).get(j));
                        }
                    }

                    for (int i = 0; i < numOfTestCases; i++) { //add each row of test cases
                        stringToBeWritten += "\n      |";
                        for (int j = 0; j < is_table_column_value_fixed.length; j++) {
                            if (is_table_column_value_fixed[j]) {
                                stringToBeWritten += " ";
                                stringToBeWritten += listOfFixedValues[j];
                                stringToBeWritten += " |";
                            } else {
                                stringToBeWritten += " ";
                                if (is_table_column_value_string[j]) {
                                    stringToBeWritten += listOfStringTestData.get(j).get(i); //TODO: test this
                                } else {
                                    stringToBeWritten += listOfTestData.get(j).get(i);
                                }
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
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAnInteger(String str) {
        try {
            int i = Integer.parseInt(str);
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }
}
