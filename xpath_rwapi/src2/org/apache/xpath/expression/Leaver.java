/*
 * Created on Aug 11, 2003
 */
package org.apache.xpath.expression;

/**
 * Callbacks for receiving "leave" events. A "leave" event is sent
 * for an given expression part when all its sub expression parts
 * was visited. 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public interface Leaver
{
	/**
	 * Leave path expression
	 * @param path
	 */
	void leavePath(PathExpr path);

	/**
	 * Leave step expression
	 * @param     
	 */
	void leaveStep(StepExpr step);

	/**
	 * Leave operator expression
	 * @param
	 * 
	 */
	void leaveOperator(OperatorExpr operator);

	/**
	 * Leave conditional expression
	 * @param
	 */
	void leaveConditional(ConditionalExpr condition);

	/**
	 * Leave for or quantified expression
	 * @param
	 */
	void leaveForOrQuantifiedExpr(ForAndQuantifiedExpr expr);
	
	/**
	 * Leave instance of expression
	 */
	void leaveInstanceOf(InstanceOfExpr expr);
	
	/**
	 * Leave 'castable as' expression
	 */
	void leaveCastableAs(CastableAsExpr expr);

}
