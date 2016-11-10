package org.de.jmg.jmgphotouploader;

public class ViewHolder extends java.lang.Object
{
	public ImgListItem item;
	public int groupPosition;
	public int childPosition;

	public ViewHolder(ImgListItem item, int groupPosition, int childPosition)

	{
		this.item = item;
		this.groupPosition = groupPosition;
		this.childPosition = childPosition;
	}
}