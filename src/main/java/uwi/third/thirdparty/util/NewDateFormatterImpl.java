package uwi.third.thirdparty.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;


@Service
  public class NewDateFormatterImpl implements NewDateFormatter {
	  private Date date;
	public String printDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		date = new Date();
		
		
		String s = formatter.format(date);
		return s;
		
	}
	public String getSimpleOracleDate(){
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		date = new Date();
		
		
		
		String s = formatter.format(date);
		return s;
		
	}
	
	
	public String printDateSlash(){
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

		date = new Date();
		
		
		String s = formatter.format(date);
		return s;
		
	}
	public String printDateSQLServer(){
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

		date = new Date();
		
		
		String s = formatter.format(date);
		return s;
	}
	public String getSimpleDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		date = new Date();
		String s = formatter.format(date);
		return s;
	}
	public String getMMMDate(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
		//date = new Date();
		String s = formatter.format(date);
		return s;
	}
	public String getFormattedDate(Date d){
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");

		String s = formatter.format(d);
		return s;
	}
	public String getPreviousDay(){
		
		
		// Get today as a Calendar
		Calendar today = Calendar.getInstance();
		// Subtract 1 day
		today.add(Calendar.DATE, -1);
		// Make an SQL Date out of that
		java.util.Date yesterday = new java.util.Date(today.getTimeInMillis());

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		String s = formatter.format(yesterday);
		
		return s;
		
	}

}
