/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.xml.serializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.xml.ObjectCreator;

public class BrickSerializer extends Serializer {
	private final String brickTagPrefix = "";
	private final String brickListTag = "brickList";
	private final String brickTabs = tab + tab + tab + tab + tab;

	public BrickSerializer(Sprite serializedSprite, Script serializedScript, Project serializedProject) {
		objectCreator = new ObjectCreator();
		fieldMap = new HashMap<String, Field>();
		super.serializedSprite = serializedSprite;
		super.serializedScript = serializedScript;
		super.brickList = serializedScript.getBrickList();
		referenceStrings = new ArrayList<String>();
		costumeList = serializedSprite.getCostumeDataList();
		super.spriteList = serializedProject.getSpriteList();
		super.soundList = serializedSprite.getSoundList();
	}

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException {

		List<String> brickStringList = new ArrayList<String>();
		String xmlElementString = "";

		xmlElementString = brickTabs + tab + getStartTag(brickTagPrefix + object.getClass().getSimpleName());
		brickStringList.add(xmlElementString);

		setBrickfieldsAsElements(object, brickStringList, object.getClass().getSuperclass());
		setBrickfieldsAsElements(object, brickStringList, object.getClass());

		xmlElementString = brickTabs + tab + getEndTag(brickTagPrefix + object.getClass().getSimpleName());
		brickStringList.add(xmlElementString);
		return brickStringList;
	}

	@SuppressWarnings("rawtypes")
	private void setBrickfieldsAsElements(Object object, List<String> brickStringList, Class cls)
			throws IllegalAccessException {
		String xmlElementString;
		fieldMap = objectCreator.getFieldMapOfThisClass(cls);
		Collection<Field> fields = fieldMap.values();
		for (Field brickClassField : fields) {
			String fieldName = objectCreator.extractTagName(brickClassField);
			brickClassField.setAccessible(true);
			if (!brickClassField.getType().isPrimitive()) {
				if (fieldName.equals("sprite")) {
					xmlElementString = brickTabs + tab + tab + spriteElementPrefix + "\"../../../../..\"/>" + "\n";
					brickStringList.add(xmlElementString);
				} else if (brickClassField.getType().equals(String.class)) {
					xmlElementString = brickTabs + tab + tab
							+ getElementString(fieldName, (String) brickClassField.get(object));
					brickStringList.add(xmlElementString);
				} else {
					String referenceString = getReference(brickClassField, object);
					xmlElementString = brickTabs + tab + tab + "<" + fieldName + " reference=\"" + referenceString
							+ "\"/>" + "\n";
					brickStringList.add(xmlElementString);
				}
			} else {
				xmlElementString = brickTabs + tab + tab
						+ getElementString(fieldName, brickClassField.get(object).toString());
				brickStringList.add(xmlElementString);
			}
		}
	}

	public List<String> serializeBrickList(List<Brick> brickList) throws IllegalArgumentException,
			IllegalAccessException {
		List<String> brickStrings = new ArrayList<String>();
		brickStrings.add(brickTabs + getStartTag(brickListTag));
		for (Object brickObject : brickList) {
			brickStrings.addAll(serialize(brickObject));
		}
		brickStrings.add(brickTabs + getEndTag(brickListTag));
		return brickStrings;

	}
}
