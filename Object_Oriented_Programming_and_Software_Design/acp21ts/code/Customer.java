import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This class defines the Customer to store the information read from the .csv file, and compute the age, date when
 * the membership will be expired, the money has been paid by customers according to the months they chose to join
 * the membership. Since the first time when the origin .csv file was read, each customer doesn't have the membership
 * ID, so the ID will also be created randomly and won't be repeated.
 */

public class Customer {
    private String firstName;
    private String lastName;
    private String name;
    private String membershipNum;
    private String teleNum;
    private String dob;
    private String address;
    private String gender;
    private int age;
    private boolean memberState =false;
    private String memberStartDate = "-";
    private String memberEndDate = "-";
    private int moneyPaid = 0;
    private int months;
    private String memberType = "-";
    private String familyMemberNum = "-";

    //initialization
    public Customer(String[] substring) {
        setMembershipNum(substring[0]);
        setFirstName(substring[1]);
        setLastName(substring[2]);
        setDob(substring[3]);
        setGender(substring[4]);
        setAddress(substring[5]);
        setTeleNum(substring[6]);
        setName(getFirstName()+" "+getLastName());
        if (!substring[3].equals(""))
            setAge(computeAge());
        if (substring.length > 8) {
            if (!substring[8].equals("-") && substring[7].equals("0")) {
                setMemberStartDate(substring[8]);
                this.months = 1;
                setMemberState();
                setMemberEndDate(computeMemberEndDate());
            } else if (substring[8].equals("-") && !substring[7].equals("0")) {
                setMonths(Integer.parseInt(substring[7]));
                setMemberStartDate(getToday());
                setMemberState();
                setMemberEndDate(computeMemberEndDate());
            } else if (substring[7].equals("0") && substring[8].equals("-")){
                setMonths(Integer.parseInt(substring[7]));
                setMemberStartDate(substring[8]);
            } else {
                setMonths(Integer.parseInt(substring[7]));
                setMemberStartDate(substring[8]);
                setMemberState();
                setMemberEndDate(computeMemberEndDate());
            }
            if (!substring[9].equals("-"))
                setMemberType(substring[9]);

            if (isMemberState()) {
                if (substring[9].equals(""))
                    setMemberType("I");
            }
            setMoneyPaid();
        }
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMembershipNum() {
        return membershipNum;
    }

    public void setMembershipNum(String membershipNum) {
        if (!checkMembershipNumIsValid(membershipNum, DataProcess.getMembershipNumList())) {
            this.membershipNum = getRandomNum(6, DataProcess.getMembershipNumList());
        } else {
            this.membershipNum = membershipNum;
        }
        DataProcess.getMembershipNumList().add(this.membershipNum);
    }

    //There is a Arraylist in DateProcess which stores the membership ID so that it can be checked whether it is repeated.
    public boolean checkMembershipNumIsValid(String membershipNum, ArrayList<String> list) {
        if (list.isEmpty() && !membershipNum.equals("")) {
            return true;
        } else if (membershipNum.equals("") || list.contains(membershipNum)) {
            return false;
        } else {
            return true;
        }
    }

    public String getRandomNum(double digit, ArrayList<String> list) {
        String random = String.valueOf((int)((Math.random()*9+1)*Math.pow(10,digit)));;
        while (list.contains(random)) {
            random = String.valueOf((int)((Math.random()*9+1)*Math.pow(10,digit)));
        }
        return random;
    }

    public String getTeleNum() {
        return teleNum;
    }

    public void setTeleNum(String teleNum) {
        this.teleNum = teleNum;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {

        if (dob.length() < 9 && dob != "") {
            StringBuilder builder = new StringBuilder(dob);
            this.dob = builder.insert(6, "19").toString();
        } else {
            this.dob = dob;
        }
    }

    public String getAddress() {
        return address;
    }

    //Simply unite the form of the address
    public void setAddress(String address) {
        address = address.replace(",", " ");
        address = address.replace("\"", "");
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    //Compute the Age depend on the DOB
    public int computeAge() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Date dob = null;
        try {
            dob = fmt.parse(this.dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] todaySeperate = fmt.format(new Date()).toString().split("/");
        String[] dobSeperate = fmt.format(dob).toString().split("/");
        int diff = Integer.valueOf(todaySeperate[2]) - Integer.valueOf(dobSeperate[2]);
        if (Integer.valueOf(dobSeperate[1]) < 9) {
            if (Integer.valueOf(todaySeperate[1]) >= 9)
                return diff + 1;
            else
                return diff;
        } else if (Integer.valueOf(todaySeperate[1]) < 9)
            return diff - 1;
        else
            return diff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMemberState() {
        return memberState;
    }

    //Set the MemberState which will be displayed as "Activated/-" in the table
    public void setMemberState() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

        try {
            if (fmt.parse(getToday()).before(fmt.parse(computeMemberEndDate())))
                this.memberState = true;
            else
                this.memberState =false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getMemberStartDate() {
        return memberStartDate;
    }

    public void setMemberStartDate(String memberStartDate) {
        this.memberStartDate = memberStartDate;
    }

    public String getMemberEndDate() {
        return memberEndDate;
    }

    public void setMemberEndDate(String memberEndDate) {
        this.memberEndDate = memberEndDate;
    }

    public int getMoneyPaid() {
        return moneyPaid;
    }

    //Compute the money depend on member type and the length of the member
    public void setMoneyPaid() {
        if (getMemberType().equals("F") && moneyPaid == getMonths() * 60
                || getMemberType().equals("I") && moneyPaid == getMonths() * 36) {
            this.moneyPaid = moneyPaid;
        } else
            this.moneyPaid = computeMoney();
    }

    //Money can also be set by yourself since the visors may attend several times
    public void setMoneyPaid(int moneyPaid) {
        if (moneyPaid == 0) {
            this.moneyPaid = computeMoney();
        } else if (getMemberType().equals("F") && moneyPaid == getMonths() * 60
                || getMemberType().equals("I") && moneyPaid == getMonths() * 36) {
            this.moneyPaid = moneyPaid;
        }
        this.moneyPaid = computeMoney();
    }

    public int computeMoney() {
        if (getMemberType().equals("F")) {
            return getMonths() * 60;
        } else if (getMemberType().equals("I")) {
            return getMonths() * 36;
        }

        return 0;
    }

    public String computeMemberEndDate() {
        Date start = null;
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        try {
            start = fmt.parse(getMemberStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.MONTH, getMonths());
        return fmt.format(cal.getTime()).toString();
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public String getToday() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return fmt.format(new Date()).toString();
    }

    public String getFamilyMemberNum() {
        return familyMemberNum;
    }

    public void setFamilyMemberNum(String familyMemberNum) {
        if (!checkMembershipNumIsValid(familyMemberNum, DataProcess.getFamilyMemberList())) {
            this.familyMemberNum = getRandomNum(4, DataProcess.getFamilyMemberList());
        } else {
            this.familyMemberNum = familyMemberNum;
        }
        DataProcess.getFamilyMemberList().add(this.familyMemberNum);
        this.familyMemberNum = familyMemberNum;
    }

    //Used to write to a new .csv file to get the information updated
    public String print() {
        return getMembershipNum() + "," +
                getFirstName() + "," +
                getLastName() + "," +
                getDob() + "," +
                getGender() + "," +
                getAddress() + "," + //5
                getTeleNum() + "," +
                getMonths() + "," + //7
                getMemberStartDate() + "," +
                getMemberType(); //9
    }
}
