/*
 * Created on Jun 9, 2003
 */
package org.apache.xpath.impl;

import org.apache.xpath.expression.CastableAsExpr;
import org.apache.xpath.expression.ConditionalExpr;
import org.apache.xpath.expression.ForAndQuantifiedExpr;
import org.apache.xpath.expression.InstanceOfExpr;
import org.apache.xpath.expression.Literal;
import org.apache.xpath.expression.OperatorExpr;
import org.apache.xpath.expression.PathExpr;
import org.apache.xpath.expression.StepExpr;
import org.apache.xpath.expression.Variable;
import org.apache.xpath.expression.Visitor;


/**
 * Helper for implementing expression visitor.
 * @author villard
 */
public class VisitorHelper implements Visitor {

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.Visitor#visitCastableAs(org.apache.xpath.expression.CastableAsExpr)
	 */
	public boolean visitCastableAs(CastableAsExpr expr) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.Visitor#visitConditional(org.apache.xpath.expression.ConditionalExpr)
	 */
	public boolean visitConditional(ConditionalExpr condition) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.Visitor#visitForOrQuantifiedExpr(org.apache.xpath.expression.ForAndQuantifiedExpr)
	 */
	public boolean visitForOrQuantifiedExpr(ForAndQuantifiedExpr expr) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.Visitor#visitInstanceOf(org.apache.xpath.expression.InstanceOfExpr)
	 */
	public boolean visitInstanceOf(InstanceOfExpr expr) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.Visitor#visitLiteral(org.apache.xpath.expression.Literal)
	 */
	public boolean visitLiteral(Literal literal) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.Visitor#visitOperator(org.apache.xpath.expression.OperatorExpr)
	 */
	public boolean visitOperator(OperatorExpr operator) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.Visitor#visitPath(org.apache.xpath.expression.PathExpr)
	 */
	public boolean visitPath(PathExpr path) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.Visitor#visitStep(org.apache.xpath.expression.StepExpr)
	 */
	public boolean visitStep(StepExpr step) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.Visitor#visitVariable(org.apache.xpath.expression.Variable)
	 */
	public boolean visitVariable(Variable var) {
		return true;
	}

}
