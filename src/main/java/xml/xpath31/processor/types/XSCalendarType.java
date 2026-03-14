/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

/**
 * Base class for all calendar based classes.
 */
public abstract class XSCalendarType extends XSCtrType {

    private static final long serialVersionUID = -6546129697566314664L;
    
    /**
     * Determine whether, two timezone values (represented as XSDuration objects) 
     * are equal. 
     */
    protected boolean isTimezoneEqual(XSDuration tz1, XSDuration tz2, 
                                               boolean isPopulatedFromFnDateFunc1, 
                                               boolean isPopulatedFromFnDateFunc2) {
         
        boolean isTimezoneEqual = false;         
        
        if (tz1 == null && tz2 == null) {
           isTimezoneEqual = true;
        }
        else if (tz1 != null && tz2 != null) {
           isTimezoneEqual = ((tz1.hours() == tz2.hours()) && 
                                                   (tz1.minutes() == tz2.minutes()) && 
                                                           (tz1.negative() == tz2.negative()));
        }
        else if (isPopulatedFromFnDateFunc1 || isPopulatedFromFnDateFunc2) {
            isTimezoneEqual = true; 
        }
        
        return isTimezoneEqual;
    }
	
}
