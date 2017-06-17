package de.rwth.i9.palm.wrapper;

import java.util.List;

import de.rwth.i9.palm.model.UserWidget;

public class UserWidgetWrapper
{
	private List<UserWidget> userWidgets;

	public List<UserWidget> getUserWidgets()
	{
		return userWidgets;
	}

	public void setUserWidgets( List<UserWidget> userWidgets )
	{
		this.userWidgets = userWidgets;
	}

}