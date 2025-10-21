/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.utbm.ciad.labmanager.utils.io.od;

import com.google.common.base.Strings;
import org.arakhne.afc.vmutil.Resources;
import org.odftoolkit.odfdom.doc.OdfDocument.OdfMediaType;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.type.Color;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Utility class for creating ODF spreadsheet.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
public class OdfSpreadsheetHelper implements AutoCloseable {

    private static final String HORIZONTAL_CENTER = "center"; //$NON-NLS-1$

    private static final String VERTICAL_CENTER = "middle"; //$NON-NLS-1$

    private static final String HEADER_BACKGROUND = "#003195"; //$NON-NLS-1$

    private static final String CURRENCY = "€"; //$NON-NLS-1$

    private static final String CURRENCY_FORMAT = "#0.0 €"; //$NON-NLS-1$

    private static final String LONG_FORMAT = "#0"; //$NON-NLS-1$

    private static final String DOUBLE_FORMAT = "#0.0"; //$NON-NLS-1$

    private final OdfSpreadsheetDocument ods;

    /**
     * Constructor without initial template.
     *
     * @throws Exception if it is impossible to create the ODF document.
     */
    public OdfSpreadsheetHelper() throws Exception {
        this(null);
    }

    /**
     * Constructor with initial template.
     *
     * @param templatePath the path to the initial ODS template.
     * @throws Exception if it is impossible to create the ODF document.
     */
    public OdfSpreadsheetHelper(String templatePath) throws Exception {
        if (Strings.isNullOrEmpty(templatePath)) {
            this.ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
        } else {
            try (final InputStream is = Resources.getResourceAsStream(templatePath)) {
                this.ods = OdfSpreadsheetDocument.loadDocument(is);
            }
        }
    }

    @Override
    public void close() throws Exception {
        this.ods.close();
    }

    /**
     * Create a new table in the document, with the given title.
     *
     * @param title the title of the table.
     * @return the table.
     * @throws Exception if it is impossible to create the ODF table.
     */
    public TableHelper newTable(String title) throws Exception {
        final var source = this.ods.getSpreadsheetTables().get(0);
        final var target = OdfTable.getInstance((TableTableElement) source.getOdfElement().cloneElement());
        this.ods.getContentRoot().appendChild(target.getOdfElement());
        target.setTableName(title);
        return new TableHelper(target);
    }

    /**
     * Get an existing table from the document, with the given index.
     *
     * @param index the position of the table.
     * @param title the new title of the table.
     * @return the table.
     * @throws Exception if it is impossible to get the ODF table.
     */
    public TableHelper getTable(int index, String title) {
        final var source = this.ods.getSpreadsheetTables().get(index);
        source.setTableName(title);
        return new TableHelper(source);
    }

    /**
     * Get an existing table from the document, with the given name.
     *
     * @param currentName the current name of the table in the document.
     * @param newName     the new name to give to the table.
     * @return the table.
     * @throws Exception if it is impossible to get the ODF table.
     */
    public TableHelper getTable(String currentName, String newName) {
        final var source = this.ods.getTableByName(currentName);
        assert source != null : "Table not found with name: " + currentName; //$NON-NLS-1$
        if (!Strings.isNullOrEmpty(newName)) {
            source.setTableName(newName);
        }
        return new TableHelper(source);
    }

    /**
     * Get an existing table from the document, with the given name.
     *
     * @param currentName the current name of the table in the document.
     * @return the table.
     * @throws Exception if it is impossible to get the ODF table.
     */
    public TableHelper getTable(String currentName) {
        return getTable(currentName, null);
    }

    /**
     * Replies the MIME type of the spreadsheet.
     *
     * @return the MIME type.
     */
    public MediaType getMediaType() {
        return MediaType.valueOf(this.ods.getMediaTypeString());
    }

    /**
     * Replies the file extension for the spreadsheet.
     *
     * @return the filename extension.
     */
    @SuppressWarnings("static-method")
    public String getFileExtension() {
        return OdfMediaType.SPREADSHEET.getSuffix();
    }

    /**
     * Save the spreadsheet in a byte array.
     *
     * @param removeFirstTable indicates if the first table is removed from the spreadsheet.
     * @return the byte array of the spreadsheet.
     * @throws Exception if the byte array cannot be created.
     */
    public byte[] toByteArray(boolean removeFirstTable) throws Exception {
        // Remove the first table that is used as a template for the other tables.
        if (removeFirstTable) {
            this.ods.getSpreadsheetTables().get(0).remove();
        }
        // Serialize the spreadsheet
        try (final var output = new ByteArrayOutputStream()) {
            this.ods.save(output);
            output.flush();
            return output.toByteArray();
        }
    }

    /**
     * Utility class for an ODF spreadsheet table.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 3.6
     */
    public static class TableHelper {

        private final OdfTable table;

        /**
         * Constructor.
         *
         * @param table the wrapped table.
         */
        protected TableHelper(OdfTable table) {
            this.table = table;
        }

        /**
         * Replies the row that is the header of the table.
         *
         * @return the header row.
         */
        public TableHeaderHelper getHeader() {
            final var row = this.table.getRowByIndex(0);
            row.setUseOptimalHeight(true);
            return new TableHeaderHelper(row);
        }

        /**
         * Replies the content of the table, assuming there is one header row.
         *
         * @return the content helper.
         */
        public TableContentHelper getContent() {
            return getContent(1);
        }

        /**
         * Replies the content of the table, assuming there is one header row.
         *
         * @param numberOfHeaderRows the number of rows in the header.
         * @return the content helper.
         */
        public TableContentHelper getContent(int numberOfHeaderRows) {
            return new TableContentHelper(this.table, numberOfHeaderRows);
        }

        /**
         * Replies the cell at the given position.
         *
         * @param column the column number from 0.
         * @param row    the row number from 0.
         * @return the cell.
         */
        public TableCellHelper getCell(int column, int row) {
            final var position = new StringBuilder();
            position.append((char) ('A' + (column % 26)));
            var c = column / 26;
            while (c > 0) {
                position.insert(0, (char) ('A' + c));
                c = c / 26;
            }
            position.append(row + 1);
            return getCell(position.toString());
        }

        /**
         * Replies the cell at the given position.
         *
         * @param cellName the name of the cell, e.g. {@code B2}.
         * @return the cell.
         */
        public TableCellHelper getCell(String cellName) {
            return new TableCellHelper(this.table.getCellByPosition(cellName));
        }

        /**
         * Insert a row after the row with the given index.
         *
         * @param rowIndex the index of the row aftr which a row must be inserted.
         * @return the new row.
         */
        public TableContentRowHelper insertRowAfter(int rowIndex) {
            final var rows = this.table.insertRowsBefore(rowIndex, 1);
            return new TableContentRowHelper(rows.get(0));
        }

        /**
         * Merge the vertical cells at the given indexes.
         *
         * @param column the index of the column.
         * @param row0   the first index of the rows, inclusive.
         * @param row1   the last index of the rows, inclusive.
         */
        public void mergeVerticalCells(int column, int row0, int row1) {
            final var range = this.table.getCellRangeByPosition(column, row0, column, row1);
            range.merge();
        }

    }

    /**
     * Utility class for header row of an ODF spreadsheet table.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 3.6
     */
    public static class TableHeaderHelper {

        private final OdfTableRow row;

        private int index;

        /**
         * Constructor.
         *
         * @param row the wrapped row.
         */
        protected TableHeaderHelper(OdfTableRow row) {
            this.row = row;
        }

        /**
         * Append a column in the header.
         *
         * @param title the title of the column.
         */
        public void appendColumn(String title) {
            final var cell = this.row.getCellByIndex(this.index);
            ++this.index;
            cell.setStringValue(Strings.nullToEmpty(title));
            cell.setHorizontalAlignment(HORIZONTAL_CENTER);
            cell.setVerticalAlignment(VERTICAL_CENTER);
            cell.setCellBackgroundColor(HEADER_BACKGROUND);
            cell.setTextWrapped(true);
        }

    }

    /**
     * Utility class for content rows of an ODF spreadsheet table.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 3.6
     */
    public static class TableContentHelper {

        private final OdfTable table;

        private int rowIndex;

        /**
         * Constructor.
         *
         * @param table              the wrapped table.
         * @param numberOfHeaderRows the number of header rows.
         */
        protected TableContentHelper(OdfTable table, int numberOfHeaderRows) {
            this.table = table;
            this.rowIndex = numberOfHeaderRows;
        }

        /**
         * Append a row in the content.
         *
         * @return the row.
         */
        public TableContentRowHelper appendRow() {
            final var row = this.table.getRowByIndex(this.rowIndex);
            ++this.rowIndex;
            return new TableContentRowHelper(row);
        }

    }

    /**
     * Utility class for content row of an ODF spreadsheet table.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 3.6
     */
    public static class TableContentRowHelper {

        private final OdfTableRow row;

        private int index;

        /**
         * Constructor.
         *
         * @param row the wrapped row.
         */
        protected TableContentRowHelper(OdfTableRow row) {
            this.row = row;
        }

        private static void defaultContentCellStyle(OdfTableCell cell) {
            cell.setVerticalAlignment(VERTICAL_CENTER);
            cell.setCellBackgroundColor((Color) null);
            cell.setTextWrapped(true);
        }

        /**
         * Append a cell with a text value.
         *
         * @param value the content of the cell.
         */
        public void append(String value) {
            final var cell = this.row.getCellByIndex(this.index);
            ++this.index;
            cell.setStringValue(Strings.nullToEmpty(value));
            defaultContentCellStyle(cell);
        }

        /**
         * Append a column with a date value in the cell.
         *
         * @param value the new date value.
         */
        public void append(LocalDate value) {
            final Calendar calendar;
            if (value != null) {
                calendar = new GregorianCalendar(value.getYear(), value.getMonthValue() - 1, value.getDayOfMonth());
            } else {
                calendar = null;
            }
            final var cell = this.row.getCellByIndex(this.index);
            ++this.index;
            if (calendar == null) {
                cell.setStringValue(null);
            } else {
                cell.setDateValue(calendar);
            }
            defaultContentCellStyle(cell);
        }

        /**
         * Append a cell with a currency value.
         *
         * @param value the content of the cell.
         */
        public void appendCurrency(Double value) {
            final var cell = this.row.getCellByIndex(this.index);
            ++this.index;
            cell.setCurrencyValue(value == null ? Double.valueOf(Double.NaN) : value, CURRENCY);
            cell.setCurrencyFormat(CURRENCY, CURRENCY_FORMAT);
            defaultContentCellStyle(cell);
        }

        /**
         * Append a cell with an integer value.
         *
         * @param value the content of the cell.
         */
        public void append(Long value) {
            final var cell = this.row.getCellByIndex(this.index);
            ++this.index;
            cell.setDoubleValue(Double.valueOf(value == null ? 0L : value.doubleValue()));
            cell.setFormatString(LONG_FORMAT);
            defaultContentCellStyle(cell);
        }

        /**
         * Append a cell with a floating-point value.
         *
         * @param value the content of the cell.
         */
        public void append(Double value) {
            final var cell = this.row.getCellByIndex(this.index);
            ++this.index;
            cell.setDoubleValue(value == null ? Double.valueOf(0.) : value);
            cell.setFormatString(DOUBLE_FORMAT);
            defaultContentCellStyle(cell);
        }

        /**
         * Append a cell with a boolean value.
         *
         * @param value the content of the cell.
         */
        public void append(Boolean value) {
            final var cell = this.row.getCellByIndex(this.index);
            ++this.index;
            cell.setBooleanValue(value == null ? Boolean.FALSE : value);
            defaultContentCellStyle(cell);
        }

    }

    /**
     * Utility class for content row of an ODF spreadsheet table.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 3.6
     */
    public static class TableCellHelper {

        private final OdfTableCell cell;

        /**
         * Constructor.
         *
         * @param cell the wrapped cell.
         */
        protected TableCellHelper(OdfTableCell cell) {
            this.cell = cell;
        }

        /**
         * Replace the given inputText by the replacement text in the content of the cell.
         *
         * @param inputText   the text to be replaced. If it is null or empty, no replacement is done.
         * @param replacement the replacement text.
         */
        public void replace(String inputText, String replacement) {
            if (!Strings.isNullOrEmpty(inputText)) {
                final var ovalue = Strings.nullToEmpty(this.cell.getStringValue());
                final var nvalue = ovalue.replace(inputText, replacement);
                this.cell.setStringValue(nvalue);
            }
        }

        /**
         * Change the text value in the cell.
         *
         * @param value the new text value.
         */
        public void set(String value) {
            this.cell.setStringValue(Strings.nullToEmpty(value));
        }

        /**
         * Change the date value in the cell.
         *
         * @param value the new date value.
         */
        public void set(LocalDate value) {
            final Calendar calendar;
            if (value != null) {
                calendar = new GregorianCalendar(value.getYear(), value.getMonthValue() - 1, value.getDayOfMonth());
            } else {
                calendar = null;
            }
            if (calendar == null) {
                this.cell.setStringValue(null);
            } else {
                this.cell.setDateValue(calendar);
            }
        }

        /**
         * Change the integer value in the cell.
         *
         * @param value the new integer value.
         */
        public void set(Long value) {
            this.cell.setDoubleValue(Double.valueOf(value == null ? 0L : value.doubleValue()));
            this.cell.setFormatString(LONG_FORMAT);
        }

        /**
         * Change the floating-point value in the cell.
         *
         * @param value the new floating-point value.
         */
        public void set(Double value) {
            this.cell.setDoubleValue(Double.valueOf(value == null ? 0. : value.doubleValue()));
            this.cell.setFormatString(DOUBLE_FORMAT);
        }

        /**
         * Change the currency value in the cell.
         *
         * @param value the new currency value.
         */
        public void setCurrency(Double value) {
            this.cell.setCurrencyValue(Double.valueOf(value == null ? 0. : value.doubleValue()), CURRENCY);
            this.cell.setCurrencyFormat(CURRENCY, CURRENCY_FORMAT);
        }

    }

}
