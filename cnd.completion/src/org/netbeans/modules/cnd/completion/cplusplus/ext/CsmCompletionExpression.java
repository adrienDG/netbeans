/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.cnd.completion.cplusplus.ext;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.netbeans.cnd.api.lexer.CndLexerUtilities;
import org.netbeans.cnd.api.lexer.CppTokenId;
import org.netbeans.modules.cnd.api.model.CsmType;

/**
 * Expression generated by parsing text by java completion
 *
 * @version 1.00
 */
public class CsmCompletionExpression {

    /** Invalid expression - this ID is used only internally */
    private static final int INVALID = -1;
    /** Constant - int/long/String/char etc. */
    public static final int CONSTANT = 0;
    /** Variable 'a' or 'a.b.c' */
    public static final int VARIABLE = 1;
    /** Operator '+' or '--' */
    public static final int OPERATOR = 2;
    /** Special value for unary operators */
    public static final int UNARY_OPERATOR = 3;
    /** Dot between method calls 'a().b()' or 'a().b.c.d(e, f)' */
    /** Dot and dereference between members 'member.*member' */
    public static final int DOT = 4;
    /** Dot between method calls and dot at the end 'a().b().' or 'a().b.c.d(e, f).' */
    /** Dot and dereference at the end 'member.*' */
    public static final int DOT_OPEN = 5;
    /** Opened array 'a[0' or 'a.b.c[d.e' */
    public static final int ARRAY_OPEN = 6;
    /** Array 'a[0]' or 'a.b.c[d.e]' */
    public static final int ARRAY = 7;
    /** Left opened parentheses */
    public static final int PARENTHESIS_OPEN = 8;
    /** Closed parenthesis holding the subexpression or conversion */
    public static final int PARENTHESIS = 9;
    /** Opened method 'a(' or 'a.b.c(d, e' */
    public static final int METHOD_OPEN = 10;
    /** Method closed by right parentheses 'a()' or 'a.b.c(d, e, f)' */
    public static final int METHOD = 11;
    /** Constructor closed by right parentheses 'new String()' or 'new String("hello")' */ // NOI18N
    public static final int CONSTRUCTOR = 12;
    /** Conversion '(int)a.b()' */
    public static final int CONVERSION = 13;
    /** Data type */
    public static final int TYPE = 14;
    /** 'new' keyword */
    public static final int NEW = 15;
    /** 'instanceof' operator */
    public static final int INSTANCEOF = 16;
    /**
     * Generic type in jdk 1.5.
     * <br>
     * It gets returned as expression with two or more parameters.
     * <br>
     * The first expression parameter is the type itself - VARIABLE
     * or DOT (or DOT_OPEN) expression.
     * <br>
     * The next expression parameters are type arguments (comma separated).
     * They are either VARIABLE, DOT, DOT_OPEN or GENERIC_TYPE.
     * <br>
     * The tokens added to the expression are
     * "&lt;" and zero or more "," and "&gt;".
     */
    // e.g.     List<String>
    // or       List<List<String>>
    // or       HashMap<Integer, String>
    public static final int GENERIC_TYPE = 17;
    /**
     * Unclosed generic type.
     * It's not closed by &gt;.
     */
    // e.g.     List<String
    // or       List<List<String>>
    // or       HashMap<Integer, String>
    public static final int GENERIC_TYPE_OPEN = 18;
    /**
     * '?' in generic type declarations
     * e.g. List<? extends Number>
     */
    public static final int GENERIC_WILD_CHAR = 19;
    /**
     * Annotation in jdk1.5.
     */
    public static final int ANNOTATION = 20;
    public static final int ANNOTATION_OPEN = 21;
    /** '#include' keyword */
    public static final int CPPINCLUDE = 22;
    /** '#include_next' keyword (a Sun Studio supported GNU extension) */
    public static final int CPPINCLUDE_NEXT = 23;
    /** 'case' keyword */
    public static final int CASE = 24;
    /** Arrow between method calls 'a()->b()' or 'a().b.c->d(e, f)' */
    /** arrow and dereference between members 'member->*member' */
    public static final int ARROW = 25;
    /** Arrow at the end 'a().b()->' or 'a().b.c.d(e, f)->' */
    /** arrow and dereference at the end 'member->*' */
    public static final int ARROW_OPEN = 26;
    /** Arrow between method calls 'NS::b()' or 'NS::CLASS::member' */
    public static final int SCOPE = 27;
    /** Arrow between method calls and dot at the end 'NS::' or 'NS::CLASS::' */
    public static final int SCOPE_OPEN = 28;
    /** "const" as type prefix in the 'const A*'*/
    public static final int TYPE_PREFIX = 29;
    /** "const" as type postfix in the 'char* const'*/
    public static final int TYPE_POSTFIX = 30;
    /** "*" or "&" at type postfix in the 'char*' or 'int &'*/
    public static final int TYPE_REFERENCE = 31;
    /** dereference "*" or address-of "&" operators in the '*value' or '&value'*/
    public static final int MEMBER_POINTER = 32;
    /** dereference "*" or address-of "&" operators in the '((A)*' or '((A)&'*/
    public static final int MEMBER_POINTER_OPEN = 33;
    /** 'goto' keyword */
    public static final int GOTO = 34;
    /** 'goto' keyword */
    public static final int LABEL = 35;
    /** 'class', 'struct, 'union' keywords */
    public static final int CLASSIFIER = 36;
    /** Cast expression ids. */
    public static final int CONVERSION_OPEN = 37;
    /** Preproc directive */
    public static final int PREPROC_DIRECTIVE = 38;
    /** Preproc directive */
    public static final int PREPROC_DIRECTIVE_OPEN = 39;
    public static final int TERNARY_OPERATOR = 40;
    /** 'for' keyword */
    public static final int FOR = 41;
    /** 'if' keyword */
    public static final int IF = 42;
    /** 'switch' keyword */
    public static final int SWITCH = 43;
    /** 'while' keyword */
    public static final int WHILE = 44;
    /** Parenthesis like in 'if (condition)' */
    public static final int SPECIAL_PARENTHESIS = 45;
    /** Parenthesis like in 'if (condition)' */
    public static final int SPECIAL_PARENTHESIS_OPEN = 46;
    /** Left opened decltype */
    public static final int DECLTYPE_OPEN = 47;
    /** Closed decltype holding the subexpression */
    public static final int DECLTYPE = 48;    
    /** 'auto' keyword */
    public static final int AUTO = 49;
    /** '->' after cpp11 new syntax function declaration (auto [declarator] -> [return type] ) */
    public static final int ARROW_RETURN_TYPE = 50;
    /** Declaration of lambda function */
    public static final int LAMBDA_FUNCTION = 51;
    /** Unfinished call of lambda function */
    public static final int LAMBDA_CALL_OPEN = 52;
    /** Finished call of lambda function */
    public static final int LAMBDA_CALL = 53;
    /** Unfinished uniform initialization */
    public static final int UNIFORM_INITIALIZATION_OPEN = 54;
    /** Finished uniform initialization */
    public static final int UNIFORM_INITIALIZATION = 55;
    /** Unfinished implicit uniform initialization ({...} without type name before) */
    public static final int IMPLICIT_UNIFORM_INITIALIZATION_OPEN = 56;
    /** Finished implicit uniform initialization ({...} without type name before) */
    public static final int IMPLICIT_UNIFORM_INITIALIZATION = 57;
    /** User defined literal (123_km) */
    public static final int USER_DEFINED_LITERAL = 58;
    /** Last used id of the expression ids. */
    private static final int LAST_ID = USER_DEFINED_LITERAL;
    private static final int cppTokenIDsLength = EnumSet.allOf(CppTokenId.class).size();
    /** Array that holds the precedence of the operator
     * and whether it's right associative or not.
     */
    private static final int[] OP = new int[cppTokenIDsLength + LAST_ID + 1];
    /** Is the operator right associative? */
    private static final int RIGHT_ASSOCIATIVE = 32;


    static {
        OP[CppTokenId.EQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.LT.ordinal()] = 10;
        OP[CppTokenId.GT.ordinal()] = 10;
        OP[CppTokenId.LTLT.ordinal()] = 11;
        OP[CppTokenId.GTGT.ordinal()] = 11;
//XXX        OP[CppTokenId.RUSHIFT.ordinal()] = 11;
        OP[CppTokenId.PLUS.ordinal()] = 12;
        OP[CppTokenId.MINUS.ordinal()] = 12;
        OP[CppTokenId.STAR.ordinal()] = 13;
        OP[CppTokenId.SLASH.ordinal()] = 13;
        OP[CppTokenId.AMP.ordinal()] = 8;
        OP[CppTokenId.BAR.ordinal()] = 6;
        OP[CppTokenId.CARET.ordinal()] = 7;
        OP[CppTokenId.PERCENT.ordinal()] = 13;
        OP[CppTokenId.NOT.ordinal()] = 15;
        OP[CppTokenId.TILDE.ordinal()] = 15;

        OP[CppTokenId.EQEQ.ordinal()] = 9;
        OP[CppTokenId.LTEQ.ordinal()] = 10;
        OP[CppTokenId.GTEQ.ordinal()] = 10;
        OP[CppTokenId.LTLTEQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.GTGTEQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
//XXX        OP[CppTokenId.RUSHIFT_EQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.PLUSEQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.MINUSEQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.STAREQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.SLASHEQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.AMPEQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.BAREQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.CARETEQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.PERCENTEQ.ordinal()] = 2 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.NOTEQ.ordinal()] = 9;

        OP[CppTokenId.DOT.ordinal()] = 16;
        OP[CppTokenId.DOTMBR.ordinal()] = 14;
        OP[CppTokenId.ARROW.ordinal()] = 16;
        OP[CppTokenId.ARROWMBR.ordinal()] = 14;
        OP[CppTokenId.SCOPE.ordinal()] = 18;
        OP[CppTokenId.COLON.ordinal()] = 3 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.QUESTION.ordinal()] = 0 | RIGHT_ASSOCIATIVE;
        OP[CppTokenId.LBRACKET.ordinal()] = 16;
        OP[CppTokenId.RBRACKET.ordinal()] = 0; // stop
        OP[CppTokenId.PLUSPLUS.ordinal()] = 16;
        OP[CppTokenId.MINUSMINUS.ordinal()] = 16;
        OP[CppTokenId.AMPAMP.ordinal()] = 5;
        OP[CppTokenId.BARBAR.ordinal()] = 4;

        OP[CppTokenId.COMMA.ordinal()] = 0; // stop
        OP[CppTokenId.SEMICOLON.ordinal()] = 0; // not-recognized
        OP[CppTokenId.LPAREN.ordinal()] = 17;
        OP[CppTokenId.RPAREN.ordinal()] = 0; // not-recognized
        OP[CppTokenId.LBRACE.ordinal()] = 0; // not-recognized
        OP[CppTokenId.RBRACE.ordinal()] = 0; // not-recognized

        OP[cppTokenIDsLength + INVALID] = 0;
        OP[cppTokenIDsLength + CONSTANT] = 1;
        OP[cppTokenIDsLength + VARIABLE] = 1;
        OP[cppTokenIDsLength + UNARY_OPERATOR] = 15;
        OP[cppTokenIDsLength + TERNARY_OPERATOR] = 3 | RIGHT_ASSOCIATIVE;
        OP[cppTokenIDsLength + DOT] = 1;
        OP[cppTokenIDsLength + DOT_OPEN] = 0; // stop
        OP[cppTokenIDsLength + ARROW] = 1;
        OP[cppTokenIDsLength + ARROW_OPEN] = 0; // stop
        OP[cppTokenIDsLength + SCOPE] = 17;
        OP[cppTokenIDsLength + SCOPE_OPEN] = 0; // stop
        OP[cppTokenIDsLength + ARRAY_OPEN] = 0; // stop
        OP[cppTokenIDsLength + ARRAY] = 1;
        OP[cppTokenIDsLength + PARENTHESIS_OPEN] = 0; // stop
        OP[cppTokenIDsLength + PARENTHESIS] = 1;
        OP[cppTokenIDsLength + METHOD_OPEN] = 0; // stop
        OP[cppTokenIDsLength + METHOD] = 1;
        OP[cppTokenIDsLength + CONSTRUCTOR] = 1;
        OP[cppTokenIDsLength + CONVERSION] = 15 | RIGHT_ASSOCIATIVE;
        OP[cppTokenIDsLength + TYPE] = 0; // stop
        OP[cppTokenIDsLength + NEW] = 0; // stop
        OP[cppTokenIDsLength + INSTANCEOF] = 10;
        OP[cppTokenIDsLength + CPPINCLUDE] = 0; // stop
        OP[cppTokenIDsLength + DECLTYPE_OPEN] = 0; // stop
        OP[cppTokenIDsLength + DECLTYPE] = 1; // stop
        OP[cppTokenIDsLength + ARROW_RETURN_TYPE] = 1;

        OP[cppTokenIDsLength + MEMBER_POINTER_OPEN] = 0; // stop
        OP[cppTokenIDsLength + MEMBER_POINTER] = 15 | RIGHT_ASSOCIATIVE; // as unary operators ?        
        OP[cppTokenIDsLength + TYPE_REFERENCE] = 1; // need to set correct value
        OP[cppTokenIDsLength + TYPE_POSTFIX] = 1; // need to set correct value
        OP[cppTokenIDsLength + TYPE_PREFIX] = 1; // need to set correct value
    }
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private CsmCompletionExpression parent;
    /** ID of the expression */
    private int expID;
    /** Result type name */
    private String type;
    /** Current token count * 3 */
    private int tokenCountM3;
    /** token info blocks containing CppTokenId
     * token's text and the position of the token in the document
     */
    private Object[] tokenBlocks = EMPTY_OBJECT_ARRAY;
    /** List of parameters */
    private List<CsmCompletionExpression> prmList;
    private CsmType cachedType;

    /**
     * Construct CsmCompletionExpression instance.
     *
     * @param expID valid expression id.
     */
    CsmCompletionExpression(int expID) {
        if (expID < 0 || expID > LAST_ID) {
            throw new IllegalArgumentException("expID=" + expID); // NOI18N
        }

        this.expID = expID;
    }

    /** Create empty variable. */
    static CsmCompletionExpression createEmptyVariable(int pos) {
        CsmCompletionExpression empty = new CsmCompletionExpression(VARIABLE);
        empty.addToken(CppTokenId.IDENTIFIER, pos, "");
        return empty;
    }
    
    /** Create expression with the given id from old expression. */
    static CsmCompletionExpression createTokenExp(int id, CsmCompletionExpression oldExpression) {
        return createTokenExp(id, oldExpression, false);
    }

    /** Create expression with the given id from old expression. */
    static CsmCompletionExpression createTokenExp(int id, CsmCompletionExpression oldExpression, boolean withParams) {
        CsmCompletionExpression exp = new CsmCompletionExpression(id);
        for (int i = 0; i < oldExpression.getTokenCount(); i++) {
            exp.addToken(oldExpression.getTokenID(i), oldExpression.getTokenOffset(i), oldExpression.getTokenText(i));
        }
        if (withParams) {
            for (int i = 0; i < oldExpression.getParameterCount(); i++) {
                exp.addParameter(oldExpression.getParameter(i));
            }
        }
        return exp;
    }

    static boolean isSeparatorOrOperator(CppTokenId tokenID) {
        return CndLexerUtilities.isSeparatorOrOperator(tokenID);
    }

    /** Return id of the operator or 'new' or 'instance' keywords
     * or -1 for the rest.
     */
    static int getOperatorID(CppTokenId tokenID) {
        int id = -1;

        if (isSeparatorOrOperator(tokenID)) {
            id = tokenID.ordinal();
        } else {
            switch (tokenID) {
                case NEW:
                    id = cppTokenIDsLength + NEW;
                    break;

//                case CppTokenId.INSTANCEOF_ID:
//                    id = javaTokenIDsLength + INSTANCEOF;
//                    break;

            }
        }

        return id;
    }

    static int getOperatorID(CsmCompletionExpression exp) {
        int expID = (exp != null) ? exp.getExpID() : INVALID;
        switch (expID) {
            case OPERATOR:
                return exp.getTokenID(0).ordinal();
        }
        return cppTokenIDsLength + expID;
    }

    static int getOperatorPrecedence(int opID) {
        return OP[opID] & 31;
    }

    static boolean isOperatorRightAssociative(int opID) {
        return (OP[opID] & RIGHT_ASSOCIATIVE) != 0;
    }

    /** Is the expression a valid type. It can be either datatype
     * or array.
     */
    static boolean isValidType(CsmCompletionExpression exp) {
        switch (exp.getExpID()) {
            case ARRAY:
                if (exp.getParameterCount() == 1) {
                    return isValidType(exp.getParameter(0));
                }
                return false;

            case TYPE_POSTFIX:
            case TYPE_PREFIX:
            case TYPE_REFERENCE:
                if (exp.getParameterCount() >= 1) {
                    return isValidType(exp.getParameter(0));
                }
                return false;

//        case DOT:
//        case ARROW:
            case SCOPE:
                int prmCnt = exp.getParameterCount();
                for (int i = 0; i < prmCnt; i++) {
                    if (exp.getParameter(i).getExpID() != VARIABLE) {
                        return false;
                    }
                }
                return true;

            case GENERIC_TYPE: // make no further analysis here
            case TYPE:
            case VARIABLE:
                return true;
        }

        return false;
    }

    /** Get expression ID */
    public int getExpID() {
        return expID;
    }

    /** Set expression ID */
    void setExpID(int expID) {
        this.expID = expID;
    }

    public CsmCompletionExpression getParent() {
        return parent;
    }

    void setParent(CsmCompletionExpression parent) {
        this.parent = parent;
    }

    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    public int getTokenCount() {
        return tokenCountM3 / 3;
    }

    public String getTokenText(int tokenInd) {
        tokenInd *= 3;
        return (String) tokenBlocks[tokenInd + 2];
    }

    public int getTokenOffset(int tokenInd) {
        tokenInd *= 3;
        return ((Integer) tokenBlocks[tokenInd + 1]).intValue();
    }

    public int getTokenLength(int tokenInd) {
        tokenInd *= 3;
        return ((String) tokenBlocks[tokenInd + 2]).length();
    }

    public CppTokenId getTokenID(int tokenInd) {
        tokenInd *= 3;
        return (CppTokenId) tokenBlocks[tokenInd];
    }

    void addToken(CppTokenId CppTokenId, int tokenOffset, String tokenText) {
        if (tokenCountM3 == tokenBlocks.length) {
            Object[] tmp = new Object[Math.max(3, tokenBlocks.length * 2)];
            if (tokenBlocks.length > 0) {
                System.arraycopy(tokenBlocks, 0, tmp, 0, tokenBlocks.length);
            }
            tokenBlocks = tmp;
        }

        tokenBlocks[tokenCountM3++] = CppTokenId;
        tokenBlocks[tokenCountM3++] = Integer.valueOf(tokenOffset);
        tokenBlocks[tokenCountM3++] = tokenText;
    }

    public int getParameterCount() {
        return (prmList != null) ? prmList.size() : 0;
    }

    public CsmCompletionExpression getParameter(int index) {
        return prmList.get(index);
    }

    void addParameter(CsmCompletionExpression prm) {
        if (prmList == null) {
            prmList = new ArrayList<CsmCompletionExpression>();
        }
        prm.setParent(this);
        prmList.add(prm);
    }

    void swapOperatorParms() {
        if ((expID == OPERATOR || expID == INSTANCEOF) && getParameterCount() == 2) {
            CsmCompletionExpression exp1 = prmList.remove(0);
            prmList.add(exp1);
            exp1.swapOperatorParms();
            (prmList.get(0)).swapOperatorParms();
        }
    }

    private static void appendSpaces(StringBuilder sb, int spaceCount) {
        while (--spaceCount >= 0) {
            sb.append(' '); //NOI18N
        }
    }

    static String getIDName(int expID) {
        switch (expID) {
            case CONSTANT:
                return "CONSTANT"; // NOI18N
            case VARIABLE:
                return "VARIABLE"; // NOI18N
            case OPERATOR:
                return "OPERATOR"; // NOI18N
            case UNARY_OPERATOR:
                return "UNARY_OPERATOR"; // NOI18N
            case DOT:
                return "DOT"; // NOI18N
            case DOT_OPEN:
                return "DOT_OPEN"; // NOI18N
            case ARROW:
                return "ARROW"; // NOI18N
            case ARROW_OPEN:
                return "ARROW_OPEN"; // NOI18N
            case SCOPE:
                return "SCOPE"; // NOI18N
            case SCOPE_OPEN:
                return "SCOPE_OPEN"; // NOI18N
            case ARRAY:
                return "ARRAY"; // NOI18N
            case ARRAY_OPEN:
                return "ARRAY_OPEN"; // NOI18N
            case PARENTHESIS_OPEN:
                return "PARENTHESIS_OPEN"; // NOI18N
            case PARENTHESIS:
                return "PARENTHESIS"; // NOI18N
            case DECLTYPE_OPEN:
                return "DECLTYPE_OPEN"; // NOI18N
            case DECLTYPE:
                return "DECLTYPE"; // NOI18N
            case METHOD_OPEN:
                return "METHOD_OPEN"; // NOI18N
            case METHOD:
                return "METHOD"; // NOI18N
            case CONSTRUCTOR:
                return "CONSTRUCTOR"; // NOI18N
            case CONVERSION:
                return "CONVERSION"; // NOI18N
            case TYPE:
                return "TYPE"; // NOI18N
            case NEW:
                return "NEW"; // NOI18N
            case INSTANCEOF:
                return "INSTANCEOF"; // NOI18N
            case GENERIC_TYPE:
                return "GENERIC_TYPE"; // NOI18N
            case GENERIC_TYPE_OPEN:
                return "GENERIC_TYPE_OPEN"; // NOI18N
            case GENERIC_WILD_CHAR:
                return "GENERIC_WILD_CHAR"; // NOI18N
            case ANNOTATION:
                return "ANNOTATION"; // NOI18N
            case ANNOTATION_OPEN:
                return "ANNOTATION_OPEN"; // NOI18N
            case CPPINCLUDE:
                return "INCLUDE"; // NOI18N
            case CASE:
                return "CASE"; // NOI18N
            case TYPE_PREFIX:
                return "TYPE_PREFIX"; // NOI18N
            case TYPE_POSTFIX:
                return "TYPE_POSTFIX"; // NOI18N
            case TYPE_REFERENCE:
                return "TYPE_REFERENCE"; // NOI18N
            case MEMBER_POINTER:
                return "MEMBER_POINTER"; // NOI18N
            case MEMBER_POINTER_OPEN:
                return "MEMBER_POINTER_OPEN"; // NOI18N
            case GOTO:
                return "GOTO"; // NOI18N
            case LABEL:
                return "LABEL"; // NOI18N
            case CLASSIFIER:
                return "CLASSIFIER"; // NOI18N
            case CONVERSION_OPEN:
                return "CONVERSION_OPEN"; // NOI18N
            case PREPROC_DIRECTIVE:
                return "PREPROC_DIRECTIVE"; // NOI18N
            case PREPROC_DIRECTIVE_OPEN:
                return "PREPROC_DIRECTIVE_OPEN"; // NOI18N
            case TERNARY_OPERATOR:
                return "TERNARY_OPERATOR"; // NOI18N
            case FOR:
                return "FOR"; // NOI18N
            case IF:
                return "IF"; // NOI18N
            case SWITCH:
                return "SWITCH"; // NOI18N
            case WHILE:
                return "WHILE"; // NOI18N
            case SPECIAL_PARENTHESIS:
                return "SPECIAL_PARENTHESIS"; // NOI18N
            case SPECIAL_PARENTHESIS_OPEN:
                return "SPECIAL_PARENTHESIS_OPEN"; // NOI18N
            case LAMBDA_FUNCTION:
                return "LAMBDA"; // NOI18N
            case LAMBDA_CALL_OPEN:
                return "LAMBDA_CALL_OPEN"; // NOI18N
            case LAMBDA_CALL:
                return "LAMBDA_CALL"; // NOI18N
            case UNIFORM_INITIALIZATION_OPEN:
                return "UNIFORM_INITIALIZATION_OPEN"; // NOI18N
            case UNIFORM_INITIALIZATION:
                return "UNIFORM_INITIALIZATION"; // NOI18N
            case IMPLICIT_UNIFORM_INITIALIZATION_OPEN:
                return "IMPLICIT_UNIFORM_INITIALIZATION_OPEN"; // NOI18N
            case IMPLICIT_UNIFORM_INITIALIZATION:
                return "IMPLICIT_UNIFORM_INITIALIZATION"; // NOI18N
            case USER_DEFINED_LITERAL:
                return "USER_DEFINED_LITERAL"; // NOI18N
            default:
                return "Unknown expID " + expID; // NOI18N
        }
    }

    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        appendSpaces(sb, indent);
        sb.append("expID=" + getIDName(expID)); // NOI18N

        if (type != null) {
            sb.append(", result type="); // NOI18N
            sb.append(type);
        }

        // Debug tokens
        int tokenCnt = getTokenCount();
        sb.append(", token count="); // NOI18N
        sb.append(tokenCnt);
        if (tokenCnt > 0) {
            for (int i = 0; i < tokenCountM3;) {
                CppTokenId CppTokenId = (CppTokenId) tokenBlocks[i++];
                int tokenOffset = ((Integer) tokenBlocks[i++]).intValue();
                String tokenText = (String) tokenBlocks[i++];
                sb.append(", token" + (i / 3 - 1) + "='" + debugString(tokenText) + "'"); // NOI18N
            }
        }

        // Debug parameters
        int parmCnt = getParameterCount();
        sb.append(", parm count="); // NOI18N
        sb.append(parmCnt);
        if (parmCnt > 0) {
            for (int i = 0; i < parmCnt; i++) {
                sb.append('\n'); //NOI18N
                appendSpaces(sb, indent + 4);
                sb.append("parm" + i + "=[" + getParameter(i).toString(indent + 4) + "]"); // NOI18N
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }

    /** Replace '\n', '\r' and '\t' in the string so they are identifiable. */
    private static String debugString(String s) {
        return (s != null) ? debugChars(s.toCharArray(), 0, s.length())
                : "NULL STRING"; // NOI18N
    }

    /** Replace '\n', '\r' and '\t' in the char array so they are identifiable. */
    private static String debugChars(char chars[], int offset, int len) {
        if (len < 0) {
            return "debugChars() !ERROR! len=" + len + " < 0"; // NOI18N
        }
        if (offset < 0) {
            return "debugChars() !ERROR! offset=" + offset + " < 0"; // NOI18N
        }
        if (offset + len > chars.length) {
            return "debugChars() !ERROR! offset=" + offset + " + len=" + len // NOI18N
                    + " > chars.length=" + chars.length; // NOI18N
        }
        StringBuilder sb = new StringBuilder(len);
        int endOffset = offset + len;
        for (; offset < endOffset; offset++) {
            switch (chars[offset]) {
                case '\n':
                    sb.append("\\n"); // NOI18N
                    break;
                case '\t':
                    sb.append("\\t"); // NOI18N
                    break;
                case '\r':
                    sb.append("\\r"); // NOI18N
                    break;
                default:
                    sb.append(chars[offset]);
            }
        }
        return sb.toString();
    }

    CsmType getCachedType() {
        return cachedType;
    }

    void cacheType(CsmType expType) {
        this.cachedType = expType;
    }
}
