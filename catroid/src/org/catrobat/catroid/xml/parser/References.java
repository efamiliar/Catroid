/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.xml.parser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.util.Log;

public class References {

	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();
	ObjectCreator objectGetter = new ObjectCreator();

	public static String getReferenceAttribute(Node brickValue) {
		Element brickElement = (Element) brickValue;
		String attributeString = null;
		if (brickValue.getNodeName().equals(CatroidXMLConstants.SPRITE)) {
			return null;
		}
		if (brickElement != null) {
			NamedNodeMap attributes = brickElement.getAttributes();
			if (attributes != null) {
				Node referenceNode = attributes.getNamedItem(CatroidXMLConstants.REFERENCE_ATTRIBUTE);
				if (referenceNode != null) {
					attributeString = referenceNode.getTextContent();

				}
			}
		}
		return attributeString;
	}

	public Object resolveReference(Object referencedObject, Node elementWithReference, String referenceString,
			Map<String, Object> referencedObjects, List<ForwardReferences> forwardRefs)
			throws XPathExpressionException, IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		XPathExpression exp = xpath.compile(referenceString);
		Log.i("resolveRef", "xpath evaluated for :" + referenceString);
		Element refferredElement = (Element) exp.evaluate(elementWithReference, XPathConstants.NODE);
		if (refferredElement == null) {
			throw new ParseException("Element by reference not found");
		}
		String xpathFromRoot = ParserUtil.getElementXpath(refferredElement);
		Object object = referencedObjects.get(xpathFromRoot);
		if (object == null) {
			referencedObject = objectGetter.getobjectOfClass(referencedObject.getClass(), "");
			ForwardReferences forwardRef = new ForwardReferences(referencedObject, xpathFromRoot, null);
			forwardRefs.add(forwardRef);

		} else {
			referencedObject = object;
		}
		return referencedObject;

	}

	public void resolveForwardReferences(Map<String, Object> referencedObjects, List<ForwardReferences> forwardRefs)
			throws IllegalArgumentException, IllegalAccessException {
		for (ForwardReferences reference : forwardRefs) {
			Field refField = reference.getFieldWithReference();
			String referenceString = reference.getReferenceString();
			if (!referencedObjects.containsKey(referenceString)) {
				Log.i("Forward referencing", "reference for " + referenceString + " not found");
			}
			if (refField != null) {
				Object parentObj = reference.getObjectWithReferencedField();
				Object valueObj = referencedObjects.get(reference.getReferenceString());
				if (!(valueObj.equals(refField.get(parentObj)))) {
					refField.set(parentObj, valueObj);
				}
			}
		}

	}
}
