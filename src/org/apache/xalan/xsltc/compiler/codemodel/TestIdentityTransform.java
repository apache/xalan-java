package org.apache.xalan.xsltc.compiler.codemodel;

import java.util.Vector;
import java.io.OutputStreamWriter;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Gopal Sharma
 * @version 1.0
 */

public class TestIdentityTransform {

  public TestIdentityTransform() {
  }

  public static void main(String[] args) {

    CmVoidType tpVoid = new CmVoidType();

    //Create specific class type like DOM
    CmType tpDom = new CmClassType ("DOM");
    CmType tpNi = new CmClassType ("NodeIterator");
    CmType tpToh = new CmClassType ("TransletOutputHandler");

    CmVariableDecl vrDom = new CmVariableDecl(tpDom, "_dom");

    //Create Parameters
    CmParameterDecl pdDom = new CmParameterDecl (tpDom, "dom");
    CmParameterDecl pdNi = new CmParameterDecl (tpNi, "iterator");
    CmParameterDecl pdToh = new CmParameterDecl (tpToh, "handler");
    CmParameterDecl pdNode = new CmParameterDecl (CmIntegerType.instance, "node");
    CmParameterDecl pdCurrent = new CmParameterDecl (CmIntegerType.instance, "current");
    CmVariableDecl vdCurrent = new CmVariableDecl (CmIntegerType.instance, "current");

    //Create List
    Vector li = new Vector(4);
    li.add(pdDom);
    li.add(pdNi);
    li.add(pdToh);
    li.add(pdNode);

    //Create class variable
    CmVariableRefExpr vreDom = new CmVariableRefExpr("dom");
    CmVariableRefExpr vreDomNull = new CmVariableRefExpr("DOM.NULL");
    CmVariableRefExpr vreNode = new CmVariableRefExpr("node");
    CmVariableRefExpr vreHandler = new CmVariableRefExpr("handler");
    CmVariableRefExpr vreCurrent = new CmVariableRefExpr("current");
    CmVariableRefExpr vreIteratornext = new CmVariableRefExpr("iterator.next()");

    CmVariableDecl dummy = new CmVariableDecl(tpDom,"dummy");

    //Create Statements
    CmStatement stTr = CmEmptyStmt.instance;
    CmStatement stBk = CmEmptyStmt.instance; //new CmExprStmt(new CmAssignmentExpr(new CmNameExpr(dummy,CmOperator.ASGN,vreDom));
    CmStatement stTl = CmEmptyStmt.instance;


    ////Creating template$dot$0 call
    //Create list for copy

    Vector licpy = new Vector(2);

    //Method Decl
    CmMethodDecl mdBk = new CmMethodDecl (CmModifier.PUBLIC, tpVoid.instance, "buildKeys", li, stBk);
    CmMethodDecl mdTl = new CmMethodDecl (CmModifier.PUBLIC, tpVoid.instance, "topLevel",  pdDom, pdNi, pdToh, stTl);



    CmStatement sTrans1 = new CmExprStmt(new CmMethodCallExpr("transferOutputSettings",vreHandler));
    CmStatement sTrans2 = new CmExprStmt(new CmMethodCallExpr("buildKeys",licpy));


    CmStatement sTrans3 = new CmExprStmt(new CmMethodCallExpr("topLevel",licpy));
    CmStatement sTrans4 = new CmExprStmt(new CmMethodCallExpr(vreHandler,"startDocument",null));
    CmStatement sTrans5 = new CmExprStmt(new CmMethodCallExpr("applyTemplates",licpy));
    CmStatement sTrans6 = new CmExprStmt(new CmMethodCallExpr(vreHandler,"endDocument",null));

    Vector liTrans = new Vector();
    liTrans.add(sTrans1);
    liTrans.add(sTrans2);
    liTrans.add(sTrans3);
    liTrans.add(sTrans4);
    liTrans.add(sTrans5);
    liTrans.add(sTrans6);


    CmStatement stTrans = new CmBlockStmt(liTrans);
    CmMethodDecl mdTrans = new CmMethodDecl (CmModifier.PUBLIC, tpVoid.instance, "transform", pdDom, pdNi, pdToh, stTrans);

    CmBreakStmt sBreak = new CmBreakStmt();

    //Creating applyTemplates call
    licpy.add(vreDom);
    licpy.add(new CmVariableRefExpr("iterator"));
    licpy.add(vreHandler);
    licpy.add(vreCurrent);
    licpy.remove(vreCurrent);
//    licpy.add(new CmVariableRefExpr("DOM.ROOTNODE"));

    Vector lichar = new Vector(2);
    lichar.add(vreCurrent);
    lichar.add(vreHandler);

    CmExpression case0 = new CmMethodCallExpr("template$dot$0" , licpy);
    CmExpression case1 = new CmMethodCallExpr(vreDom,"characters",lichar);
    CmExpression case2 = new CmMethodCallExpr("template$dot$0" , licpy);
    CmExpression exWhile = new CmBinaryExpr(new CmAssignmentExpr(vreCurrent, CmOperator.ASGN, vreIteratornext), CmOperator.NE, vreDomNull);

    Vector liget =new Vector(1);
    liget.add(vreCurrent);

    CmExpression exSwitch = new CmMethodCallExpr(vreDom,"getType",liget);
    CmStatement lsC0 = new CmLabeledStmt ("case 0", new CmExprStmt(case0));
    CmStatement lsC1 = new CmLabeledStmt ("case 1", new CmExprStmt(case1));
    CmStatement lsC2 = new CmLabeledStmt ("case 2", sBreak);
    CmStatement lsC3 = new CmLabeledStmt ("case 3", new CmExprStmt(new CmMethodCallExpr("applyTemplates",vreDom,new CmMethodCallExpr(vreDom,"getChildren",liget),vreHandler)));

    // Creating block for Switch
    Vector blkList = new Vector();
    blkList.add(lsC0);
    blkList.add(sBreak);
    blkList.add(lsC1);
    blkList.add(sBreak);
    blkList.add(lsC2);
    blkList.add(sBreak);
    blkList.add(lsC3);
    blkList.add(sBreak);

    CmBlockStmt block = new CmBlockStmt(blkList);
    CmStatement lsSw = new CmSwitchStmt (exSwitch, block);

    CmWhileStmt wsAt = new CmWhileStmt(exWhile, new CmBlockStmt(lsSw));

    CmMethodDecl mdAt = new CmMethodDecl (CmModifier.PUBLIC, tpVoid.instance, "applyTemplates", pdDom, pdNi, pdToh, wsAt);



    CmExpression exTd = new CmMethodCallExpr(vreDom, "copy", lichar) ;
    CmStatement stTd = new CmExprStmt(exTd) ;

    li.remove(pdNode);
    li.add(pdCurrent);

    CmMethodDecl mdTd = new CmMethodDecl (CmModifier.PUBLIC, tpVoid.instance, "template$dot$0", li, stTd);

    CmMethodDecl mdTr = new CmMethodDecl (CmModifier.PUBLIC, tpVoid.instance, "transform", pdDom, pdToh, stTr);

    //Import Statements
    CmImportDecl imstDom = new CmImportDecl ("org.apache.xalan.xsltc.DOM");
    CmImportDecl imstNi = new CmImportDecl ("org.apache.xalan.xsltc.NodeIterator");
    CmImportDecl imstToh = new CmImportDecl("org.apache.xalan.xsltc.TransletOutputHandler");
    CmImportDecl imstAt = new CmImportDecl("org.apache.xalan.xsltc.runtime.AbstractTranslet");

    //Class Decl
    CmClassDecl classDecl = new CmClassDecl (CmModifier.PUBLIC, "Copy01", "AbstractTranslet", null) ;
    classDecl.addCmDeclaration(imstDom).addCmDeclaration(imstNi).addCmDeclaration(imstToh).addCmDeclaration(imstAt)
             .addCmModifiers(CmModifier.PUBLIC)
             .addCmDeclaration(vrDom)
             .addCmMethodDecl(mdBk)
             .addCmMethodDecl(mdTl)
             .addCmMethodDecl(mdTrans)
             .addCmMethodDecl(mdAt)
             .addCmMethodDecl(mdTd)
             .addCmMethodDecl(mdTr)
             ;


    //TestIdentityTransform testIdentityTransform1 = new TestIdentityTransform();
    JavaCmVisitor visitor = new JavaCmVisitor(new OutputStreamWriter(System.out));
    classDecl.accept(visitor, null);
    visitor.flush();
  }
}