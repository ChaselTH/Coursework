import sheffield.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class contains several methods to read the .csv file and store the information from each line in the .csv
 * file into the Arraylist, and when the new information comes, there is a method that can write them into a new .csv
 * file and with the name of the current time. Then when we need to read the .csv file, it can find the latest file
 * which has been updated. Also contains a method to get the repeated name in an Arraylist.
 *
 * This will created a new .csv file call "next_update.csv" which stored the file name of the latest file.
 */

public class DataProcess {
    public static ArrayList<String> membershipNumList = new ArrayList<>();
    public static ArrayList<Customer> customerArrayList = new ArrayList<>();
    public static ArrayList<String> familyMemberList = new ArrayList<>();
    private static File thisFile = new File(".");
    private static String originFile = "customerlist.csv";

    //Read the latest file and update the CustomerList
    public static void update() {
        customerArrayList.clear();
        EasyReader customerList = new EasyReader(findLatestFile());
        while(!customerList.eof()) {
            String line = customerList.readString();
            String[] subString = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
            customerArrayList.add(new Customer(subString));
        }
    }

    //Find the file in the local content
    public static boolean findFile(String fileName) {
        File[] files = thisFile.listFiles();
        for (File f : files) {
            if (f.getName().equals(fileName))
                return true;
        }
        return false;
    }

    //Find the name of  latest file and stored it in the "next_update.csv"
    public static String findLatestFile() {
        if (findFile("next_update.csv")) {
            EasyReader nexFileName = new EasyReader("next_update.csv");
            return nexFileName.readString();
        } else {
            return originFile;
        }
    }

    //Out put the new file with the current time on its file name
    public static void outUpdate() {
        SimpleDateFormat form = new SimpleDateFormat();
        form.applyPattern("dd-MM-yyyy HH-mm-ss");
        Date date = new Date();
        String fileName = "updated_customerlist "+form.format(date)+".csv";
        EasyWriter writer = new EasyWriter(fileName);

        EasyWriter nextFileWriter = new EasyWriter("next_update.csv");
        nextFileWriter.append(fileName);
        nextFileWriter.close();

        int index = 0;
        for (Customer customer : customerArrayList) {
            writer.append(customer.print());
            index++;
            if (index < customerArrayList.size())
                writer.append("\n");
        }
        writer.close();
    }

    //Return the repeat name list in customList
    public static ArrayList<String> getRepeatName() {
        ArrayList<String> uniqueNameList = new ArrayList<>();
        ArrayList<String> repeatNameList = new ArrayList<>();
        for (Customer customer : customerArrayList) {
            String name = customer.getName();
            if (!uniqueNameList.contains(name))
                uniqueNameList.add(name);
            else
                if (!repeatNameList.contains(name))
                    repeatNameList.add(name);
        }
        return repeatNameList;
    }

    public static ArrayList<String> getMembershipNumList() {
        return membershipNumList;
    }

    public static ArrayList<String> getFamilyMemberList() {
        return familyMemberList;
    }

}
