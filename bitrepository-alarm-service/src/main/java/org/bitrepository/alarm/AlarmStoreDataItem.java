package org.bitrepository.alarm;
import java.util.StringTokenizer;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.bitrepository.bitrepositorymessages.Alarm;
import org.bitrepository.bitrepositoryelements.AlarmcodeType;
import org.bitrepository.bitrepositoryelements.ComponentTYPE;
import org.bitrepository.bitrepositoryelements.ComponentTYPE.ComponentType;


/** 
 * Class to contain the data of a received alarm. Contains methods to serialize/deserialize the object 
 * so it can be persisted in a flat file.  
 */
public class AlarmStoreDataItem {

	private XMLGregorianCalendar date;
	private ComponentTYPE raiser;
	private AlarmcodeType alarmCode;
	private String alarmText;
	
	/**
	 * Private constructor to be used when creating a AlarmStoreDataItem by deserializing from a string. 
	 */
	private AlarmStoreDataItem(XMLGregorianCalendar date, ComponentTYPE raiser, AlarmcodeType alarmCode,
			String alarmText) {
		this.date = date;
		this.raiser = raiser;
		this.alarmCode = alarmCode;
		this.alarmText = alarmText;
	}
	
	/**
	 * Publicly accessible  constructor.
	 * @param Alarm Alarm obbject to build the AlarmStoreDataItem from.
	 * @return The fresh AlarmStoreDataItem object. 
	 */
	public AlarmStoreDataItem(Alarm message) {
		date = message.getAlarmDescription().getOrigDateTime();
		raiser = message.getAlarmRaiser();
		alarmCode = message.getAlarmDescription().getAlarmCode();
		alarmText = message.getAlarmDescription().getAlarmText();
	}
	
	/**
	 * toString method to deliver a HTML representation of the alarm. 
	 * @return A string containing a HTML table row presenting the alarm.  
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr><td>");
		sb.append(date.toString());
		sb.append("</td><td>");
		sb.append(raiser.toString());
		sb.append("</td><td>");
		sb.append(alarmCode.toString());
		sb.append("</td><td>");
		sb.append(alarmText);
		sb.append("</td></tr>");
		return sb.toString();
	}
	
	/**
	 * Method for serializing the object to a string representation so it can be persisted to disk.
	 * @return The string representation of the object.   
	 */
	public String serialize() {
		return date.toString() + " #!# " + raiser.toString() + " #!# " + alarmCode.toString() + " #!# " + alarmText;
	}
	
	/**
	 * Method for deserializing the string representation of a AlarmStoreDataItem object to a java object. 
	 * @param String String representation of a AlarmStoreDataItem
	 * @return The AlarmStoreDataItem object corresponding to the data. 
	 */
	public static AlarmStoreDataItem deserialize(String data) throws IllegalArgumentException {
		XMLGregorianCalendar date;
		ComponentTYPE raiser;
		AlarmcodeType alarmCode;
		String alarmText;
		
		StringTokenizer st = new StringTokenizer(data, "#!#");
		if(st.countTokens() != 4) {
			throw new IllegalArgumentException("The input string did not contain excatly 4 tokens");
		}
		try {
			date = DatatypeFactory.newInstance().newXMLGregorianCalendar(st.nextToken());
			// TODO fix this mess..
			raiser = new ComponentTYPE();
			raiser.setComponentID("Lost-in-conversion!");
			raiser.setComponentType(ComponentType.PILLAR);
			alarmCode = AlarmcodeType.valueOf(st.nextToken());
			alarmText = st.nextToken();
			return new AlarmStoreDataItem(date, raiser, alarmCode, alarmText);
		} catch (DatatypeConfigurationException e) {
			throw new IllegalArgumentException("The date token is invalid");
		}
	}
	
}
