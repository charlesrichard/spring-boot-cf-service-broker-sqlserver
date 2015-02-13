package org.cloudfoundry.community.servicebroker.util;
import java.util.ArrayList;

public class FormattedVariableList extends ArrayList<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toString()
	{
		String formattedList = "";
		for(String variable : this)
		{
			formattedList += variable + ", ";
		}
		return formattedList;
	}
}
