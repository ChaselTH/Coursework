import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * The class of the GUI and also including the value changing. This will put the CustomerList into a JTable and all
 * the functions applied including:
 *      1) Detecting the repeated names which is the error contains in the origin .csv file. (Highlighted by Yellow)
 *      2) Delete the customer in a line. (Highlighted by Red)
 *      3) Change values, especially the information about membership, since it will not be set when adding a new
 *      customer. And when either "Moths" or "StartDate" be set, another one will be set automatically(moths will be
 *      1, StartDate will be today), if only one value was set. MemberState can be set only with "F" or "I", if is
 *      null, it will be set "I" as default value. (Highlighted by Green)
 *          Editable values except:
 *              a) Membership ID, since it is created automatically, randomly and non-repeating.
 *              b) Age, computed automatically by the DOB, DOB is necessary when adding a new customer.
 *              c) MemberState, set automatically if customer's moth > 0
 *              d) Money Paid, computed automatically by the length of the membership
 *      4) Search the record with any text which may be included in one or more customers. (Highlighted by Blue)
 *
 * When adding a new customer, the .csv file will be updated immediately, but when you make other changes like delete
 * or change any values, the .csv file will not be updated unless the "update" button be tapped(invisible if no
 * changes made)
 */

public class ShowMenu {
    private JFrame frame;
    private JPanel panel;
    private JScrollPane scrollPane;
    private JTable table = createTable();
    private JButton newCustomer;
    private JButton deleCustomer;
    private JButton update;
    private JButton search;
    private String oldValue = null;
    private String newValue = null;
    private ArrayList<Integer> repeatNameIndex;

    //The columns' names
    private static String[] columnNames = {"MembershipID",
            "First Name",
            "Last Name",
            "DOB",
            "Age",
            "Gender", //5
            "Address",
            "Telephone",
            "ClubMember",
            "MemberType",//9
            "Months", //10
            "StartDate", //11
            "EndDate",
            "Paid"};

    public ShowMenu() {
        frame = new JFrame("ClubMembership");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        frame.getContentPane();

        //A Tips will be shown on the down right of the window
        JPanel info = new JPanel();
        info.setBounds(1200, 600, 200, 150);

        JPanel yellowBox = new JPanel();
        yellowBox.setBackground(Color.YELLOW);
        JLabel yellowLable = new JLabel("Repeat Name");

        JPanel redBox = new JPanel();
        redBox.setBackground(Color.red);
        JLabel redLable = new JLabel("Delete Lines");

        JPanel greenBox = new JPanel();
        greenBox.setBackground(Color.GREEN);
        JLabel greenLable = new JLabel("Value Changed");


        JPanel blueBox = new JPanel();
        blueBox.setBackground(Color.BLUE);
        JLabel blueLable = new JLabel("Search Result");

        info.add(yellowBox);
        info.add(yellowLable);

        info.add(redBox);
        info.add(redLable);

        info.add(greenBox);
        info.add(greenLable);

        info.add(blueBox);
        info.add(blueLable);

        info.setLayout(new GridLayout(4, 2, 1, 10));

        scrollPane = new JScrollPane(table);
        scrollPane.setSize(1500,500);

        newCustomer = new JButton("Add");
        newCustomer.setBounds(20,600, 100,50);

        deleCustomer = new JButton("Delete");
        deleCustomer.setBounds(150, 600, 100, 50);

        update = new JButton("Update");
        update.setVisible(false);
        update.setBounds(280, 600, 100,50);

        JTextField searchField = new JTextField();
        searchField.setBounds(20, 700, 300, 50);
        search = new JButton("Search");
        search.setBounds(400, 700, 100, 50);

        //These Arraylist is used in the Renderer to locate which cell or row be colored
        ArrayList<Integer> searchResultIndex = new ArrayList<>();
        ArrayList<Integer> delIndex = new ArrayList<>();
        ArrayList<ChangedValue> changedValues = new ArrayList<>();
        ArrayList<String> newValueList = new ArrayList<>();

        //Edit Listener
        table.addPropertyChangeListener(evt -> {
            if ("tableCellEditor".equals(evt.getPropertyName())) {
                Integer[] rowcol = {table.getSelectedRow(), table.getSelectedColumn()};
                if (table.isEditing()) {
                    oldValue = String.valueOf(table.getValueAt(rowcol[0], rowcol[1]));
                } else {
                    newValue = String.valueOf(table.getValueAt(rowcol[0], rowcol[1]));
                    if (!newValue.equals(oldValue)) {
                        newValueList.add(newValue);
                        setQuadraRowBackgroundColor(table,
                                delIndex, Color.RED,
                                repeatNameIndex, Color.YELLOW,
                                newValueList, Color.GREEN,
                                searchResultIndex, Color.BLUE);
                        scrollPane.setViewportView(table);
                        changedValues.add(new ChangedValue(rowcol[0], table.getColumnName(rowcol[1]), newValue));
                        update.setVisible(true);
                    }
                }
            }
        });

        //Add Listener
        newCustomer.addActionListener(e -> {
            showAddMember();
        });

        //Delete Listener
        deleCustomer.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame,
                        "Please select one Row to delete",
                        "Error!",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                delIndex.add(row);
                setQuadraRowBackgroundColor(table,
                        delIndex, Color.RED,
                        repeatNameIndex, Color.YELLOW,
                        newValueList, Color.GREEN,
                        searchResultIndex, Color.BLUE);
                scrollPane.setViewportView(table);
                update.setVisible(true);
            }
        });

        //Update Listener
        update.addActionListener(e -> {
            boolean isError = false;
            if (changedValues.size() > 0) {
                for (ChangedValue newValue : changedValues) {
                    Customer customer = DataProcess.customerArrayList.get(newValue.getIndexOrRow());
                    String value = newValue.getValue();
                    switch (newValue.getColumn()) {
                        case "First Name":
                            customer.setFirstName(value);
                            break;
                        case "Last Name":
                            customer.setLastName(value);
                            break;
                        case "DOB":
                            customer.setDob(value);
                            break;
                        case "Gender":
                            customer.setGender(value);
                            break;
                        case "Address":
                            customer.setAddress(value);
                            break;
                        case "Telephone":
                            customer.setTeleNum(value);
                            break;
                        case "StartDate":
                            if (customer.getAge() >= 12) {
                                if (customer.getMonths() == 0) {
                                    customer.setMonths(1);
                                    customer.setMemberStartDate(value);
                                    customer.setMemberEndDate(customer.computeMemberEndDate());
                                    customer.setMemberState();
                                    if (customer.getMemberType().equals("-"))
                                        customer.setMemberType("I");
                                } else {
                                    customer.setMemberStartDate(value);
                                    customer.setMemberEndDate(customer.computeMemberEndDate());
                                    customer.setMemberState();
                                    if (customer.getMemberType().equals("-"))
                                        customer.setMemberType("I");
                                }
                            } else {
                                isError = true;
                            }
                            break;
                        case "Paid":
                            customer.setMoneyPaid(Integer.parseInt(value));
                            break;
                        case "Months":
                            if (customer.getAge() >= 12) {
                                if (customer.getMonths() == 0 && customer.getMemberStartDate().equals("-")) {
                                    customer.setMonths(Integer.parseInt(value));
                                    customer.setMemberStartDate(customer.getToday());
                                    customer.setMemberEndDate(customer.computeMemberEndDate());
                                    customer.setMemberState();
                                    if (customer.getMemberType().equals("-"))
                                        customer.setMemberType("I");
                                } else if (!customer.getMemberStartDate().equals("-")) {
                                    customer.setMonths(Integer.parseInt(value));
                                    customer.setMemberEndDate(customer.computeMemberEndDate());
                                    customer.setMemberState();
                                    if (customer.getMemberType().equals("-"))
                                        customer.setMemberType("I");
                                }
                            } else {
                                isError = true;
                            }
                            break;
                        case "MemberType":
                            if (value.equals("F") || value.equals("I"))
                                customer.setMemberType(value);
                            else
                                isError = true;
                            break;

                    }
                }
            }

            //If error ocurs which means the input is not legal and all the changes will not be updated
            if (isError) {
                JOptionPane.showMessageDialog(frame, "Information contains errors! Please check the input! " +
                                "\n Individual member should be at least 12 years old and Family member should be at least 18 years old!" +
                                "\n The MemberType should be set \"I\" or \"F\"!",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                DataProcess.update();
            } else {
                if (delIndex.size() > 0) {
                    Collections.sort(delIndex, Collections.reverseOrder());
                    for (int row : delIndex) {
                        DataProcess.customerArrayList.remove(row);
                    }
                }
                DataProcess.outUpdate();
            }

            table = createTable();
            scrollPane.setViewportView(table);
            changedValues.clear();
            delIndex.clear();
            searchResultIndex.clear();
            newValueList.clear();
            update.setVisible(false);
            //Because the "table" was updated, the old listener doesn't work
            table.addPropertyChangeListener(evt -> {
                if ("tableCellEditor".equals(evt.getPropertyName())) {
                    Integer[] rowcol = {table.getSelectedRow(), table.getSelectedColumn()};
                    if (table.isEditing()) {
                        oldValue = String.valueOf(table.getValueAt(rowcol[0], rowcol[1]));
                    } else {
                        newValue = String.valueOf(table.getValueAt(rowcol[0], rowcol[1]));
                        if (!newValue.equals(oldValue)) {
                            newValueList.add(newValue);
                            //setWordColor(table, newValueList, Color.GREEN);
                            setQuadraRowBackgroundColor(table,
                                    delIndex, Color.RED,
                                    repeatNameIndex, Color.YELLOW,
                                    newValueList, Color.GREEN,
                                    searchResultIndex, Color.BLUE);
                            scrollPane.setViewportView(table);
                            changedValues.add(new ChangedValue(rowcol[0], table.getColumnName(rowcol[1]), newValue));
                            update.setVisible(true);
                        }
                    }
                }
            });
        });

        //Search Listener
        search.addActionListener(e -> {
            searchResultIndex.clear();
            for (Customer customer : DataProcess.customerArrayList) {
                if (customer.print().contains(searchField.getText())) {
                    searchResultIndex.add(DataProcess.customerArrayList.indexOf(customer));
                }
            }

            setQuadraRowBackgroundColor(table,
                    delIndex, Color.RED,
                    repeatNameIndex, Color.YELLOW,
                    newValueList, Color.GREEN,
                    searchResultIndex, Color.BLUE);
            scrollPane.setViewportView(table);
        });

        panel.setLayout(null);
        panel.add(newCustomer);
        panel.add(deleCustomer);
        panel.add(update);
        panel.add(scrollPane);
        panel.add(searchField);
        panel.add(search);
        panel.add(info);

        frame.add(panel);
        frame.setSize(1500, 800);
        frame.setVisible(true);
    }

    //A method to initialize the table and this will be called when update needed
    public JTable createTable() {
        DataProcess.getMembershipNumList().clear();
        DataProcess.update();
        Object[][] data = new Object[DataProcess.customerArrayList.size()][columnNames.length];
        int index = 0;

        //Get the repeat names
        ArrayList<String> repeatName = DataProcess.getRepeatName();
        repeatNameIndex = new ArrayList<>();
        for (Customer customer: DataProcess.customerArrayList) {

            if (index <= DataProcess.customerArrayList.size()){
                data[index][0] = customer.getMembershipNum();
                data[index][1] = customer.getFirstName();
                data[index][2] = customer.getLastName();
                data[index][3] = customer.getDob();
                if (customer.getAge() == 0)
                    data[index][4] = "-";
                else
                    data[index][4] = customer.getAge();
                data[index][5] = customer.getGender();
                data[index][6] = customer.getAddress();
                data[index][7] = customer.getTeleNum();
                if (customer.isMemberState())
                    data[index][8] = "Activated";
                else
                    data[index][8] = "-";
                data[index][9] = customer.getMemberType();
                data[index][10] = customer.getMonths();
                data[index][11] = customer.getMemberStartDate();
                data[index][12] = customer.getMemberEndDate();
                data[index][13] = customer.getMoneyPaid();
                if (repeatName.contains(customer.getName())) {
                    repeatNameIndex.add(index);
                }
            }
            index++;
        }

        //Set some columns unable to be edited since this values are computed automatically
        JTable table = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0 || column == 4 || column == 12 || column == 8)
                    return false;
                return super.isCellEditable(row, column);
            }
        });

        //A warning window will appear every time refresh the table
        if (repeatNameIndex.size() > 0) {
            setRowBackgroundColor(table, repeatNameIndex, Color.YELLOW);
            JOptionPane.showMessageDialog(frame,
                    repeatNameIndex.size()/2
                            + " Repeated Names Detected! \n Please Correct the Information Soon!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        table.setFillsViewportHeight(true);
        fitTableColumns(table);
        return table;
    }

    //Override the renderer, but just useful for one time when start the programme
    public void setRowBackgroundColor(JTable table, ArrayList<Integer> rowIndex, Color color) {
        DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                if (rowIndex.contains(row)) {
                    setBackground(color);
                } else {
                    setBackground(Color.WHITE);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        table.setDefaultRenderer(Object.class, tableCellRenderer);
    }

    //Override again using a new method with 4 groups parameters which will be used to color each different states
    public void setQuadraRowBackgroundColor(JTable table, ArrayList<Integer> index1, Color color1, ArrayList<Integer> index2, Color color2, ArrayList<String> word, Color color3, ArrayList<Integer> index3, Color color4) {
        DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                if (index3.contains(row))
                    setBackground(color4);
                else if (index1.contains(row))
                    setBackground(color1);
                else if (word.contains(table.getValueAt(row, column)))
                    setBackground(color3);
                else if (index2.contains(row))
                    setBackground(color2);
                else
                    setBackground(Color.WHITE);

                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        table.setDefaultRenderer(Object.class, tableCellRenderer);
    }

    //Override to let the length of the column fit to the longest value
    public void fitTableColumns(JTable table) {
        JTableHeader header = table.getTableHeader();
        int rowCount = table.getRowCount();

        Enumeration columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) table.getTableHeader()
                    .getDefaultRenderer()
                    .getTableCellRendererComponent(table, column.getIdentifier(), false, false, -1, col)
                    .getPreferredSize()
                    .getWidth();
            for (int row = 0; row < rowCount; row++) {
                int preferedWidth = (int) table.getCellRenderer(row, col)
                        .getTableCellRendererComponent(table, table.getValueAt(row, col), false, false, row, col)
                        .getPreferredSize()
                        .getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column);
            column.setWidth(width + table.getIntercellSpacing().width + 10);
        }
    }

    //New frame to add a new customer
    public void showAddMember() {
        JFrame frame = new JFrame("Add Member");
        JPanel panel = new JPanel();

        JLabel firstNameLable = new JLabel("First Name");
        JTextField firstNameField = new JTextField();

        JLabel lastNameLabel = new JLabel("Last Name");
        JTextField lastNameField = new JTextField();

        JLabel dobLabel = new JLabel("DOB");
        JTextField dobField = new JTextField();

        JLabel genderLabel = new JLabel("Gender");
        JTextField genderField = new JTextField();

        JLabel addressLabel = new JLabel("Address");
        JTextField addressField = new JTextField();

        JLabel telLabel = new JLabel("Telephone");
        JTextField telField = new JTextField();

        JButton conform = new JButton("Confirm");
        JButton cancel = new JButton("Cancel");

        conform.addActionListener(e -> {
            if (firstNameField.getText().equals("")
                    || lastNameField.getText().equals("")
                    || dobField.getText().equals("")) {
                JOptionPane.showMessageDialog(frame,
                        "Please Enter All the Information Required!",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                String[] newMember = {"",
                        firstNameField.getText(),
                        lastNameField.getText(),
                        dobField.getText(),
                        "",
                        genderField.getText(),
                        addressField.getText(),
                        telField.getText()};
                DataProcess.customerArrayList.add(new Customer(newMember));
                DataProcess.outUpdate();
                table = createTable();
                scrollPane.setViewportView(table);
                frame.dispose();

            }

        });

        panel.add(firstNameLable);
        panel.add(firstNameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(dobLabel);
        panel.add(dobField);
        panel.add(genderLabel);
        panel.add(genderField);
        panel.add(addressLabel);
        panel.add(addressField);
        panel.add(telLabel);
        panel.add(telField);
        panel.add(conform);
        panel.add(cancel);
        panel.setLayout(new GridLayout(7,2,1,1));

        cancel.addActionListener(e -> frame.dispose());
        frame.setSize(600, 500);
        frame.add(panel);
        frame.setVisible(true);
    }

}
