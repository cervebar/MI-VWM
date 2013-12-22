/*
  Copyright (c) 2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
 */

package de.crysandt.audio.mpeg7audio;

import de.crysandt.xml.*;

public class MP7CreationInformation
	extends XMLNode
{
	public static final String PERSON_GROUP = "PersonGroupType";
	public static final String PERSON = "PersonType";
	public static final String GROUP = "GroupType";

	private static final int PRIO_CREATION_TITLE = -1;
	private static final int PRIO_CREATION_CREATOR = 0;

	public MP7CreationInformation() {
		super("CreationInformation", MP7Writer.PRIO_CREATION_INFO);
	}

	public void setTitle(String title) {
		if (title == null)
			return;

		getNode(new XMLNode("Creation"))
			.getNode((new XMLNode("Title", false, PRIO_CREATION_TITLE))
					 .setAttribute("type", "songTitle"))
			.setChild(new XMLCharacters(XMLCharacters.String2XML(title)));
	}

	public void setArtist(String artist, String person_group) {
		if (artist == null)
			return;

		XMLNode creator = getNode(new XMLNode("Creation"))
			.getNode(new XMLNode("Creator", PRIO_CREATION_CREATOR));

		creator.getNode(new XMLNode("Role").setAttribute("href", "RoleCS"))
			.getNode(new XMLNode("Name", false))
			.setChild(new XMLCharacters(XMLCharacters.String2XML("Artist")));

		creator.getNode(new XMLNode("Agent")
						.setAttribute("xsi:type", person_group))
			.getNode(new XMLNode("Name", false))
			.setChild(new XMLCharacters(XMLCharacters.String2XML(artist)));
	}

	public void setArtist(String artist) {
		setArtist(artist, PERSON_GROUP);
	}
}
