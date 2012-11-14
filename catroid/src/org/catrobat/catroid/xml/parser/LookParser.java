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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LookParser {
	ObjectCreator objectGetter = new ObjectCreator();
	List<LookData> lookList;
	References reference = new References();

	public void parseLookList(NodeList lookNodes, Sprite sprite, Map<String, Object> referencedObjects)
			throws SecurityException, NoSuchFieldException, IllegalAccessException {
		lookList = new ArrayList<LookData>();
		int lookIndex = 1;
		for (int m = 0; m < lookNodes.getLength(); m++) {
			LookData foundLookData = null;
			if (lookNodes.item(m).getNodeType() != Node.TEXT_NODE) {

				Element lookElement = (Element) lookNodes.item(m);
				String lookFileName = null;
				Node lookFileNameNode = lookElement.getElementsByTagName(CatroidXMLConstants.FILE_NAME).item(0);
				if (lookFileNameNode != null) {
					lookFileName = lookFileNameNode.getChildNodes().item(0).getNodeValue();
				}
				String lookName = lookElement.getElementsByTagName(CatroidXMLConstants.NAME).item(0).getChildNodes()
						.item(0).getNodeValue();
				foundLookData = new LookData();
				foundLookData.setLookFilename(lookFileName);
				foundLookData.setLookName(lookName);
				lookList.add(foundLookData);
				String lookindexString = "";
				if (lookIndex > 1) {
					lookindexString = "[" + lookIndex + "]";
				}
				referencedObjects.put(CatroidXMLConstants.LOOK_DATA_ELEMENT_NAME + lookindexString, foundLookData);
				lookIndex++;
			}
		}
		Field lookListField = sprite.getClass().getDeclaredField(CatroidXMLConstants.LOOK_LIST_FIELD_NAME);
		objectGetter.setFieldOfObject(lookListField, sprite, lookList);

	}

	public Boolean setLookdataOfBrick(Brick brickObject, Field valueField, String referenceAttribute,
			Map<String, Object> referencedObjects, List<ForwardReferences> forwardRefs) throws IllegalAccessException {
		int lastIndex = referenceAttribute.lastIndexOf('[');
		String query = CatroidXMLConstants.LOOK_DATA_ELEMENT_NAME;
		String suffix = "";
		if (lastIndex != -1) {
			char referenceNo = referenceAttribute.charAt(referenceAttribute.lastIndexOf('[') + 1);
			suffix = "[" + referenceNo + "]";
		}
		LookData referencedLook = (LookData) referencedObjects.get(query + suffix);
		if (referencedLook == null) {
			return false;
		} else {
			valueField.set(brickObject, referencedLook);
		}
		return true;
	}

}
