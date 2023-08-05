/**
 * This class stored the information including the index of row and column and the changed value of the old value in
 * the table.
 */

public class ChangedValue {
    private int indexOrRow;
    private String column;
    private String value;

    public ChangedValue(int indexOrRow, String column, String value) {
        this.indexOrRow = indexOrRow;
        this.column = column;
        this.value = value;
    }

    public int getIndexOrRow() {
        return indexOrRow;
    }

    public String getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }
}
