package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import java.util.Comparator;

import javax.swing.table.TableRowSorter;

/**
 * Sorter for tables (otherwise exceptions in DataTableModel - default compare
 * does not handle enums)
 *
 * @author Petr Miko - miko.petr (at) gmail.com
 *
 */
public class DataRowSorter extends TableRowSorter<DataTableModel> {

	/**
	 * Constructor method.
	 *
	 * @param dataModel table Model
	 */
	public DataRowSorter(DataTableModel dataModel) {
		super(dataModel);
	}

	@Override
	public Comparator<?> getComparator(int column) {
		return new Comparator<Object>() {

			public int compare(Object o1, Object o2) {

				if (o1 instanceof Number && o2 instanceof Number)
					return (Integer) o1 - (Integer) o2;
				else {
					// copied from java tutorials - modified with toString to prevent conversion issues
					String[] strings1 = o1.toString().split("\\s");
					String[] strings2 = o2.toString().split("\\s");
					return strings1[strings1.length - 1].compareTo(strings2[strings2.length - 1]);
				}
			}
		};
	}
}
