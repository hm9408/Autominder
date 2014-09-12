package com.autominder;

import java.io.Serializable;
import java.util.Date;

public class Record implements Serializable{

	private static final long serialVersionUID = -8228566643725427154L;
	
	private double cost;
	private Date date;
	private Maintenance maintenance;
	
}
