/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.rwapi.test;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.rwapi.XPathFactory;
import org.apache.xpath.rwapi.eval.Evaluator;
import org.apache.xpath.rwapi.expression.ConditionalExpr;
import org.apache.xpath.rwapi.expression.Expr;
import org.apache.xpath.rwapi.expression.ExprContext;
import org.apache.xpath.rwapi.expression.ExpressionFactory;
import org.apache.xpath.rwapi.expression.ForAndQuantifiedExpr;
import org.apache.xpath.rwapi.expression.Literal;
import org.apache.xpath.rwapi.expression.NodeTest;
import org.apache.xpath.rwapi.expression.OperatorExpr;
import org.apache.xpath.rwapi.expression.PathExpr;
import org.apache.xpath.rwapi.expression.StepExpr;
import org.apache.xpath.rwapi.expression.Variable;
import org.apache.xpath.rwapi.expression.Visitor;
import org.apache.xpath.rwapi.impl.parser.SimpleNode;
import org.apache.xpath.rwapi.impl.parser.XPath;
import org.apache.xpath.rwapi.impl.parser.XPathTreeConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
public class Test
{
    public Test(String[] args)
    {
        try
        {
            final boolean dumpTree = ((args.length == 1)
                && args[0].equals("-dump")) ? true : false;

            /*if(args[0].endsWith(".xquery"))
            {
              System.out.println("Running test for: "+args[0]);
              File file = new File(args[0]);
              FileInputStream fis = new FileInputStream(file);
              XPath parser = new XPath(fis);
              SimpleNode tree = parser.XPath2();
              if(dumpTree)
                tree.dump("|") ;
            }
            else
            {*/
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(ClassLoader.getSystemResourceAsStream(
                        "org/apache/xpath/rwapi/test/xpathsamples.xml"));
            doc.normalize();

            Element tests = doc.getDocumentElement();
            NodeList testElems = tests.getChildNodes();
            int nChildren = testElems.getLength();
            int testid = 0;
            boolean testSuccess = true;

            for (int i = 0; i < nChildren; i++)
            {
                org.w3c.dom.Node node = testElems.item(i);

                if (org.w3c.dom.Node.ELEMENT_NODE == node.getNodeType())
                {
                    testid++;

                    String xpathString = ((Element) node).getAttribute("value");
                    System.out.println("Test[" + testid + "]: " + xpathString);

                    XPath parser = new XPath(new StringReader(xpathString));
                    SimpleNode tree = parser.XPath2();

                    if (SimpleNode.PRODUCE_RAW_TREE)
                    {
                        if (dumpTree)
                        {
                            tree.dump("|");
                        }
                    }
                    else
                    {
                        Expr expr = (Expr) tree.jjtGetChild(0);

                        // Gets the reference AST to compare with
                        NodeList astNodes = ((Element) node)
                            .getElementsByTagName("ast");

                        if ((astNodes != null) && (astNodes.getLength() >= 1))
                        {
                            Node astNode = astNodes.item(0);

                            if (!checkAST((SimpleNode) expr,
                                        (Element) ((Element) astNode).getElementsByTagName(
                                            "node").item(0)))
                            {
                                System.err.println(
                                    "Generated AST doesn't match the reference one");

                                tree.dump("|");
                            }
                        }
                        else
                        {
                            System.err.println("No reference AST provided");
                        }

                        String ab = expr.getString(true);

                        //System.out.println(
                        //	"Parsed expr (abbreviate form):" + ab);
                        //System.out.println(
                        //	"Parsed expr (non-abbreviate form):"
                        //		+ expr.getString(false));
                        if (!ab.equals(xpathString))
                        {
                            System.err.print(
                                "Bad external or internal representation: ");
                            System.err.println(ab + "  !=  " + xpathString);
                            testSuccess = false;
                        }
                    }
                }
            }

            // }
            if (testSuccess)
            {
                System.out.println("Parsing test successful!!!");
            }
            else
            {
                System.out.println("Parsing Test fails!!!!!!!!!");
            }

            // Test creation
            ExpressionFactory exprFct = XPathFactory.newInstance()
                                                    .newExpressionFactory();

            Expr expr = exprFct.createExpr("child::tutu[10]");
            System.out.println("tutu[10] =? " + expr.getString(true));

            PathExpr pathExpr = exprFct.createPathExpr(true);
            System.out.println("/ =? " + pathExpr.getString(true));

            NodeTest nt = exprFct.createNameTest(null, "toto");
            pathExpr.addOperand(exprFct.createStepExpr(StepExpr.AXIS_CHILD, nt));
            System.out.println("/toto =? " + pathExpr.getString(true));
            System.out.println("/child::toto =? " + pathExpr.getString(false));

            nt = exprFct.createNameTest(null, "titi");
            pathExpr.addOperand(exprFct.createStepExpr(
                    StepExpr.AXIS_DESCENDANT, nt));

            System.out.println("/toto/descendant::titi =? "
                + pathExpr.getString(true));
            System.out.println("/child::toto/descendant::titi =? "
                + pathExpr.getString(false));

            StepExpr se = (StepExpr) pathExpr.getOperand(0); // first step
            se.appendPredicate(exprFct.createIntegerLiteralExpr(50));
            System.out.println("/toto[50]/descendant::titi =? "
                + pathExpr.getString(true));
            System.out.println("/child::toto[50]/descendant::titi =? "
                + pathExpr.getString(false));

            // Test visitor
            pathExpr.visit(new Visitor()
                {
                    /**
                    *
                    */
                    public boolean visitPath(PathExpr path)
                    {
                        System.out.println("visit path "
                            + path.getString(false));

                        return true;
                    }

                    /**
                     *
                     */
                    public boolean visitStep(StepExpr step)
                    {
                        System.out.println("visit step "
                            + step.getString(false));

                        return true;
                    }

                    /**
                     *
                     */
                    public boolean visitLiteral(Literal primary)
                    {
                        System.out.println(primary.getString(false));

                        return true;
                    }

                    /**
                     *
                     */
                    public boolean visitOperator(OperatorExpr arithmetic)
                    {
                        System.out.println(arithmetic.getString(false));

                        return true;
                    }

                    /**
                     *
                     */
                    public boolean visitConditional(ConditionalExpr condition)
                    {
                        System.out.println(condition.getString(false));

                        return true;
                    }

                    /**
                     *
                     */
                    public boolean visitForOrQuantifiedExpr(
                        ForAndQuantifiedExpr expr)
                    {
                        System.out.println(expr.getString(false));

                        return true;
                    }

                    /**
                     *
                     */
                    public boolean visitVariable(Variable var)
                    {
                        return false;
                    }
                });

            // Evaluation
            Evaluator eval = XPathFactory.newInstance().newEvaluatorFactory()
                                         .createEvaluator();

            ExprContext ctx = eval.createExprContext();
            ctx.getDynamicContext().setContextItem(doc.getDocumentElement());

            // exprs
            Expr e = exprFct.createExpr("expr[2]/@value");
            Object res = eval.evaluate(ctx, e);

            // Xalan dependent code
            System.out.println("10[$var] ?= " + ((XObject) res).str());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check that the given internal representation of expr match the reference
     * AST
     * @param expr
     * @param refAST
     * @return boolean
     */
    public boolean checkAST(SimpleNode expr, Element refAST)
    {
        String nodeName = refAST.getAttributes().getNamedItem("name")
                                .getNodeValue();

        if (XPathTreeConstants.jjtNodeName[expr.getId()].equals(nodeName))
        {
            int i = 0;

            for (Node rac = refAST.getFirstChild(); rac != null;
                    rac = rac.getNextSibling())
            {
                if ("node".equals(rac.getNodeName()))
                {
                    if (!checkAST((SimpleNode) expr.jjtGetChild(i),
                                (Element) rac))
                    {
                        return false;
                    }

                    i++;
                }
            }

            if (i < expr.jjtGetNumChildren())
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public static void main(String[] args)
    {
        new Test(args);
    }
}
