/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.formulaeditor;

import java.util.LinkedList;
import java.util.List;

public class InternFormulaParser {

	private class InternFormulaParserException extends Exception {

		public InternFormulaParserException(String errorMessage) {
			super(errorMessage);
		}
	}

	public static final int PARSER_OK = -1;
	public static final int PARSER_STACK_OVERFLOW = -2;
	public static final int PARSER_INPUT_SYNTAX_ERROR = -3;
	public static final int PARSER_NO_INPUT = -4;

	private List<InternToken> internTokensToParse;
	private int currentTokenParseIndex;
	private int errorTokenIndex;
	private InternToken currentToken;

	public InternFormulaParser(List<InternToken> internTokensToParse) {

		this.internTokensToParse = new LinkedList<InternToken>();

		for (InternToken internToken : internTokensToParse) {
			this.internTokensToParse.add(internToken);
		}

	}

	private void getNextToken() throws InternFormulaParserException {
		currentTokenParseIndex++;
		if (currentTokenParseIndex >= internTokensToParse.size()) {
			currentToken = null;
			throw new InternFormulaParserException("Parse Error");
		}
		currentToken = internTokensToParse.get(currentTokenParseIndex);

	}

	public int getErrorTokenIndex() {
		return errorTokenIndex;

	}

	private FormulaElement findLowerPriorityOperatorElement(Operators currentOp, FormulaElement curElem) {
		FormulaElement returnElem = curElem.getParent();
		FormulaElement notNullElem = curElem;
		boolean goon = true;

		while (goon) {
			if (returnElem == null) {
				goon = false;
				returnElem = notNullElem;
			} else {
				Operators parentOp = Operators.getOperatorByValue(returnElem.getValue());
				int compareOp = parentOp.compareOperatorTo(currentOp);
				if (compareOp < 0) {
					goon = false;
					returnElem = notNullElem;
				} else {
					notNullElem = returnElem;
					returnElem = returnElem.getParent();
				}
			}
		}
		return returnElem;
	}

	public void handleOperator(String operator, FormulaElement curElem, FormulaElement newElem) {
		//        System.out.println("handleOperator: operator="+operator + " curElem="+curElem.getValue() + " newElem="+newElem.getValue());

		if (curElem.getParent() == null) {
			new FormulaElement(FormulaElement.ElementType.OPERATOR, operator, null, curElem, newElem);
			//            System.out.println("handleOperator-after: " + curElem.getRoot().getTreeString());
			return;
		}

		Operators parentOp = Operators.getOperatorByValue(curElem.getParent().getValue());
		Operators currentOp = Operators.getOperatorByValue(operator);

		int compareOp = parentOp.compareOperatorTo(currentOp);

		if (compareOp >= 0) {
			FormulaElement newLeftChild = findLowerPriorityOperatorElement(currentOp, curElem);
			//            System.out.println("findLowerPriorityOperatorElement: " + newLeftChild.getValue());
			FormulaElement newParent = newLeftChild.getParent();

			if (newParent != null) {
				newLeftChild.replaceWithSubElement(operator, newElem);
			} else {
				new FormulaElement(FormulaElement.ElementType.OPERATOR, operator, null, newLeftChild, newElem);
			}
		} else {
			curElem.replaceWithSubElement(operator, newElem);
		}

		//        System.out.println("handleOperator-after: " + curElem.getRoot().getTreeString());
	}

	private void addEndOfFileToken() {
		int lastIndex = internTokensToParse.size() - 1;

		if (internTokensToParse.get(lastIndex).isEndOfFileToken()) {
			return;
		}

		InternToken endOfFileParserToken = new InternToken(InternTokenType.PARSER_END_OF_FILE);
		internTokensToParse.add(endOfFileParserToken);
	}

	public FormulaElement parseFormula() {
		errorTokenIndex = PARSER_OK;
		currentTokenParseIndex = 0;

		if (internTokensToParse == null) {
			errorTokenIndex = PARSER_NO_INPUT;
			return null;
		}
		if (internTokensToParse.size() == 0) {
			errorTokenIndex = PARSER_NO_INPUT;
			return null;
		}

		currentToken = internTokensToParse.get(0);

		addEndOfFileToken();

		FormulaElement formulaParseTree = null;

		try {
			formulaParseTree = formula();
		} catch (InternFormulaParserException parseExeption) {
			errorTokenIndex = currentTokenParseIndex;
		}

		return formulaParseTree;

	}

	private FormulaElement formula() throws InternFormulaParserException {

		FormulaElement termListTree = termList();

		if (currentToken.isEndOfFileToken()) {
			return termListTree;
		}

		throw new InternFormulaParserException("Parse Error");
	}

	private FormulaElement termList() throws InternFormulaParserException {
		FormulaElement curElem = term();

		FormulaElement loopTermTree;
		String operatorStringValue;
		while (currentToken.isOperator()) {

			operatorStringValue = currentToken.getTokenSringValue();
			getNextToken();

			loopTermTree = term();
			handleOperator(operatorStringValue, curElem, loopTermTree);
			curElem = loopTermTree;
		}

		return curElem.getRoot();
	}

	private FormulaElement term() throws InternFormulaParserException {

		FormulaElement termTree = new FormulaElement(FormulaElement.ElementType.NUMBER, null, null);
		FormulaElement curElem = termTree;

		if (currentToken.isOperator() && currentToken.getTokenSringValue().equals("-")) {

			curElem = new FormulaElement(FormulaElement.ElementType.NUMBER, null, termTree, null, null);
			termTree.replaceElement(FormulaElement.ElementType.OPERATOR, "-", null, curElem);

			getNextToken();
		}

		if (currentToken.isNumber()) {

			curElem.replaceElement(FormulaElement.ElementType.NUMBER, number());

		} else if (currentToken.isBracketOpen()) {

			getNextToken();

			curElem.replaceElement(FormulaElement.ElementType.BRACKET, null, null, termList());

			if (!currentToken.isBracketClose()) {
				throw new InternFormulaParserException("Parse Error");
			}
			getNextToken();

		} else if (currentToken.isFunctionName()) {
			curElem.replaceElement(function());

		} else if (currentToken.isSensor()) {
			curElem.replaceElement(sensor());

		} else if (currentToken.isCostume()) {
			curElem.replaceElement(costume());

		} else if (currentToken.isUserVariable()) {

			curElem.replaceElement(userVariable());

		} else {
			throw new InternFormulaParserException("Parse Error");
		}

		return termTree;

	}

	private FormulaElement userVariable() throws InternFormulaParserException {
		//TODO check if user-variable exists

		FormulaElement costumeTree = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE,
				currentToken.getTokenSringValue(), null);

		getNextToken();

		return costumeTree;
	}

	private FormulaElement costume() throws InternFormulaParserException {
		//TODO check if costume-name exists

		FormulaElement costumeTree = new FormulaElement(FormulaElement.ElementType.SENSOR,
				currentToken.getTokenSringValue(), null);

		getNextToken();

		return costumeTree;
	}

	private FormulaElement sensor() throws InternFormulaParserException {
		//TODO check if sensor-name exists

		FormulaElement sensorTree = new FormulaElement(FormulaElement.ElementType.SENSOR,
				currentToken.getTokenSringValue(), null);

		getNextToken();

		return sensorTree;
	}

	private FormulaElement function() throws InternFormulaParserException {
		FormulaElement functionTree = new FormulaElement(FormulaElement.ElementType.FUNCTION, null, null);

		//TODO check if functionName is valid
		functionTree = new FormulaElement(FormulaElement.ElementType.FUNCTION, currentToken.getTokenSringValue(), null);
		getNextToken();

		if (currentToken.isFunctionParameterBracketOpen()) {
			getNextToken();
			functionTree.setLeftChild(termList());

			if (currentToken.isFunctionParameterDelimiter()) {
				getNextToken();
				functionTree.setRightChild(termList());
			}

			if (!currentToken.isFunctionParameterBracketClose()) {
				throw new InternFormulaParserException("Parse Error");
			}
			getNextToken();

		}

		return functionTree;
	}

	private String number() throws InternFormulaParserException {

		String numberToCheck = currentToken.getTokenSringValue();

		if (!numberToCheck.matches("(\\d)+(\\.(\\d)+)?")) {
			throw new InternFormulaParserException("Parse Error");
		}

		getNextToken();

		return numberToCheck;
	}
}
