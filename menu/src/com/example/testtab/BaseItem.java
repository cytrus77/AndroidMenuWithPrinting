package com.example.testtab;

/**
 * This class is used specifically to pass these 3 strings.
 * passing the XMLresource started giving wierd errors with the garbage collector..
 * 
 * @author WINDAdmin
 *
 */
public class BaseItem {
	public String image;
	public String title;
	public String description;
	public int 	num;
	public String comment;
	BaseItem(String s, String t, String d)
	{
		image = s;
		title = t;
		description = d;
		num = 1;
		comment = "";
	}
}
