package uwi.third.thirdparty.util;

import java.util.Date;

public interface NewDateFormatter {
	public String printDate();
	public String getSimpleOracleDate();
	public String printDateSlash();
	public String printDateSQLServer();
	public String getSimpleDate();
	public String getMMMDate(Date date);
	public String getFormattedDate(Date d);
	public String getPreviousDay();
}
