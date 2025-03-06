package org.apache.xpath.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnySimpleType;
import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of XML Schema data type xs:gMonthDay.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGMonthDay extends XSAnySimpleType {

	private static final long serialVersionUID = -3463438692413910274L;

	private XSInteger month;
	
	private XSInteger day;
	
	private static final String XS_GMONTH_DAY = "xs:gMonthDay";
	
	/**
	 * Default constructor.
	 */
	public XSGMonthDay() {
		// NO OP
	}
	
	/**
	 * Class constructor.
	 */
	public XSGMonthDay(String gMonthDayStrValue) throws TransformerException {
		if (gMonthDayStrValue.length() != 7) {
		    throw new TransformerException("FOCA0003 : An xs:gMonthDay value cannot be "
		    		                                                   + "constructed from string value " + gMonthDayStrValue);
		}
		else {
			String monthStrValue = gMonthDayStrValue.substring(2, 4);
			String dayStrValue = gMonthDayStrValue.substring(5);
			
			if (!isMonthAndDayValueConsistent(monthStrValue, dayStrValue)) {
				throw new TransformerException("FOCA0003 : day value component " + dayStrValue + " is not "
								                                          + "possible to exist with month value component " + 
								                                          monthStrValue + ", within the specified xs:monthDay value " + 
								                                          gMonthDayStrValue + ".");
			}
			
			this.month = new XSInteger(monthStrValue);	
			this.day = new XSInteger(dayStrValue);
		}
	}

	@Override
	public String stringType() {
		return XS_GMONTH_DAY;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSGMonthDay xsGMonthDay = new XSGMonthDay(strVal);
			result.add(xsGMonthDay);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}

	@Override
	public String stringValue() {
		String resultStr = null;
		
		String monthStrValue = month.stringValue();
		monthStrValue = (monthStrValue.length() == 1) ? "0"+monthStrValue : monthStrValue; 
		String dayStrValue = day.stringValue();
		dayStrValue = (dayStrValue.length() == 1) ? "0"+dayStrValue : dayStrValue;
		
		resultStr = "--" + monthStrValue + "-" + dayStrValue;
		
		return resultStr;
	}
	
	/**
	 * Get the month component from this xs:gMonthDay object instance. 
	 */
	public XSInteger getMonth() {
		return month;
	}
	
	/**
	 * Get the day component from this xs:gMonthDay object instance. 
	 */
	public XSInteger getDay() {
		return day;
	}
	
	public int getType() {
		return CLASS_GMONTHDAY;
	}
	
	/**
	 * Implementation of comparison operation "equals", for the 
	 * type xs:gMonthDay.
	 */
	public boolean eq(XSGMonthDay value) {
		boolean result = false;
		
		XSInteger thisMonth = this.getMonth();
		XSInteger argMonth = value.getMonth();
		
		XSInteger thisDay = this.getDay();
		XSInteger argDay = value.getDay();
		
		result = ((thisMonth.intValue()).equals(argMonth.intValue()) && (thisDay.intValue()).equals(argDay.intValue()));
		
		return result;
	}
	
	/**
	 * Implementation of comparison operation "not equals", for the 
	 * type xs:gMonthDay.
	 */
	public boolean ne(XSGMonthDay value) {
        boolean result = false;
		
		XSInteger thisMonth = this.getMonth();
		XSInteger argMonth = value.getMonth();
		
		XSInteger thisDay = this.getDay();
		XSInteger argDay = value.getDay();
		
		result = (!(thisMonth.intValue()).equals(argMonth.intValue()) || !(thisDay.intValue()).equals(argDay.intValue()));
		
		return result;
	}
	
	/**
	 * Method definition to check whether, the provided specific day value obeys 
	 * the known day value for specified month value.
	 */
	private boolean isMonthAndDayValueConsistent(String monthStrValue, String dayStrValue) {
		
		boolean result = false;
		
		Integer monthIntValue = Integer.valueOf(monthStrValue);
		Integer dayIntValue = Integer.valueOf(dayStrValue);
		
		List<MonthAndNoOfDaysValuesPair> listOfValidObjects = getValidMonthAndDaysPairs();
		
		result = listOfValidObjects.stream().filter(obj -> obj.getMonthValue().equals(monthIntValue) && 
				                                           obj.getNoOfDaysInMonth().equals(dayIntValue)).findFirst().isPresent();
		
		return result;
	}
	
	/**
	 * Method definition to construct, a list of 'valid month' and 'no of 
	 * days in that month' pairs.
	 */
	private List<MonthAndNoOfDaysValuesPair> getValidMonthAndDaysPairs() {
		List<MonthAndNoOfDaysValuesPair> listOfValidObjects = new ArrayList<MonthAndNoOfDaysValuesPair>();
		
		for (int idx = 1; idx <= 29; idx++) {
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(1, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(2, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(3, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(4, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(5, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(6, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(7, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(8, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(9, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(10, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(11, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(12, idx));
		}


		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(1, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(3, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(4, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(5, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(6, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(7, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(8, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(9, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(10, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(11, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(12, 30));
		
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(1, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(3, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(5, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(7, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(8, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(10, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(12, 31));
		
		return listOfValidObjects;
	}
	
	/**
	 * An object of this class, contains a pair of month and days 
	 * values for checking existence of a (month + days) object within 
	 * a list;
	 */
	class MonthAndNoOfDaysValuesPair {
		
		private Integer monthValue;
		
		private Integer noOfDaysInMonth;
		
		public MonthAndNoOfDaysValuesPair(Integer monthValue, Integer noOfDaysInMonth) {
			this.monthValue = monthValue;
			this.noOfDaysInMonth = noOfDaysInMonth;
		}

		public Integer getMonthValue() {
			return monthValue;
		}

		public void setMonthValue(Integer monthValue) {
			this.monthValue = monthValue;
		}

		public Integer getNoOfDaysInMonth() {
			return noOfDaysInMonth;
		}

		public void setNoOfDaysInMonth(Integer noOfDaysInMonth) {
			this.noOfDaysInMonth = noOfDaysInMonth;
		}
	}

}
