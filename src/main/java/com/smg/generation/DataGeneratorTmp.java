package com.smg.generation;
import com.smg.sqlparser.domain.sql.Column;
import com.smg.sqlparser.enums.DataType;

import java.time.LocalDate;
import java.util.Random;

public class DataGeneratorTmp {
	
	private static final Random random = new Random();
	
	public static String generateValue(Column column, int rowIndex) {
		DataType type = column.getType();
		
		int safeLength = (column.getLength() != null && column.getLength() > 0)
			? Math.toIntExact(column.getLength())
			: 10;
		
		if (type.equals(DataType.INT)) {
			int max = (int) Math.pow(10, Math.min(safeLength, 9)) - 1;
			int value = (rowIndex + 1) % max;
			return String.valueOf(value == 0 ? 1 : value);
			
		} else if (type.equals(DataType.CHAR) || type.equals(DataType.VARCHAR)) {
			String base = column.getName() + "_" + (rowIndex + 1);
			if (base.length() > safeLength) {
				base = base.substring(0, safeLength);
			}
			return "'" + base + "'";
			
		} else if (type.equals(DataType.DATE)) {
			LocalDate base = LocalDate.of(2020, 1, 1);
			LocalDate value = base.plusDays(random.nextInt(2000));
			return "'" + value + "'";
			
		} else if (type.equals(DataType.BOOLEAN)) {
			return random.nextBoolean() ? "TRUE" : "FALSE";
		}
		
		String fallback = "VAL_" + (rowIndex + 1);
		if (fallback.length() > safeLength) {
			fallback = fallback.substring(0, safeLength);
		}
		return "'" + fallback + "'";
	}
}
