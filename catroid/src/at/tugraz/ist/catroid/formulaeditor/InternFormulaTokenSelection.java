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

import at.tugraz.ist.catroid.formulaeditor.InternFormula.TokenSelectionType;

public class InternFormulaTokenSelection {

	private int internTokenSelectionStart;
	private int internTokenSelectionEnd;
	private TokenSelectionType tokenSelectionType;

	public InternFormulaTokenSelection(TokenSelectionType tokenSelectionType, int internTokenSelectionStart,
			int internTokenSelectionEnd) {
		this.tokenSelectionType = tokenSelectionType;
		this.internTokenSelectionStart = internTokenSelectionStart;
		this.internTokenSelectionEnd = internTokenSelectionEnd;
	}

	public int getStartIndex() {
		return internTokenSelectionStart;
	}

	public int getEndIndex() {
		return internTokenSelectionEnd;
	}

	public TokenSelectionType getTokenSelectionType() {
		return tokenSelectionType;
	}

	@Override
	public boolean equals(Object objectToCompare) {

		if (objectToCompare instanceof InternFormulaTokenSelection) {
			InternFormulaTokenSelection selectionToCompare = (InternFormulaTokenSelection) objectToCompare;
			if (internTokenSelectionStart != selectionToCompare.internTokenSelectionStart) {
				return false;
			}
			if (internTokenSelectionEnd != selectionToCompare.internTokenSelectionEnd) {
				return false;
			}
			if (tokenSelectionType != selectionToCompare.tokenSelectionType) {
				return false;
			}
			return true;
		}

		return super.equals(objectToCompare);
	}

	public InternFormulaTokenSelection deepCopy() {

		return new InternFormulaTokenSelection(tokenSelectionType, internTokenSelectionStart, internTokenSelectionEnd);
	}

}
