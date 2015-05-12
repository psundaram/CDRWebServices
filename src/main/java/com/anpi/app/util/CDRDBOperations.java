package com.anpi.app.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import scala.annotation.serializable;

import com.anpi.app.domain.EnhancedCallLogsEntry;
import com.anpi.app.domain.TrafficLoadBean;
import com.anpi.app.domain.TrafficSummary;

public class CDRDBOperations {

	public TrafficLoadBean getTrafficLoad(String stEnterPriseId, long fromDateMilli, long toDateMilli, long increment) throws Exception {
		List<TrafficSummary> trafficSummaries = new ArrayList<TrafficSummary>();
		long startTime = new Date().getTime();
		System.out.println("sEnterpriseId :" + stEnterPriseId + ", fromDateMilli" + fromDateMilli + ",toDateMilli" + toDateMilli + ",increment:" + increment);
		TrafficLoadBean loadBean = null;
		CDRConnector cdrConnector = new CDRConnector();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<String> inBound = new ArrayList<String>();
		ArrayList<String> outBound = new ArrayList<String>();
		ArrayList<String> interCom = new ArrayList<String>();
		try {
			if (fromDateMilli < toDateMilli) {
				// Connect to database
				conn = cdrConnector.getDBConnection();
				String stInsert = "select A.starttime,A.endtime,SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directiontype from (SELECT direction,starttime,endtime,fromnumber,tonumber,duration FROM tbCallLog where enterpriseid =?  and (starttime>? and endtime<?) group by  direction,starttime,endtime,fromnumber,tonumber,duration ) as A  group by  starttime,endtime,fromnumber,tonumber,duration"; 
//						"select starttime,endtime,SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directiontype from (select * from  (SELECT direction,starttime,endtime,fromnumber,tonumber,duration FROM tbCallLog where enterpriseid =?  and (starttime>? and endtime<?)) as directions  group by  direction,starttime,endtime,fromnumber,tonumber,duration ) as cdr  group by  starttime,endtime,fromnumber,tonumber,duration";
				// String stInsert =
				// "select starttime,endtime,SUM(CASE WHEN a.direction = 'In' THEN 1 ELSE 2 END) as directiontype from (SELECT direction,starttime,endtime,fromnumber,tonumber,duration FROM tbCallLog where enterpriseid =? and (starttime>? and endtime<?)) as a group by a.starttime,a.endtime,a.fromnumber,a.tonumber,a.duration;";
				pstmt = conn.prepareStatement(stInsert);
				pstmt.setString(1, stEnterPriseId);
				pstmt.setLong(2, fromDateMilli);
				pstmt.setLong(3, toDateMilli);
				System.out.println(" Query --> :" + pstmt);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					TrafficSummary trafficSummary = new TrafficSummary();
					trafficSummary.setStartTime(rs.getLong("starttime"));
					trafficSummary.setEndtTime(rs.getLong("endtime"));
					trafficSummary.setDirection(rs.getInt("directiontype"));
					trafficSummaries.add(trafficSummary);
					System.out.println("startTime:" + trafficSummary.getStartTime() + " , directionType:" + trafficSummary.getDirection() + " ,endtime:" + trafficSummary.getEndtTime());
				}
				System.out.println("TrafficSummary Size :" + trafficSummaries.size());
				// Calculate inbound,outbound, intercom values based on interval
				while (fromDateMilli < toDateMilli) {
					long tempTimeMilli = fromDateMilli + increment;
					int direction = 0;
					int inLogCount = 0;
					int outLogCount = 0;
					int interCount = 0;
					// Checks for the existence of time interval with the db
					// values. If values exist, then check for the direction and
					// increment the respective counter by 1
					for (int i = 0; i < trafficSummaries.size(); i++) {
						TrafficSummary trafficSummary = (TrafficSummary) trafficSummaries.get(i);
						direction = trafficSummary.getDirection();
						if ((trafficSummary.getStartTime() <= tempTimeMilli) && (tempTimeMilli <= trafficSummary.getEndtTime())) {
							if (direction == 1) {
								inLogCount = inLogCount + 1;
							} else if (direction == 2) {
								outLogCount = outLogCount + 1;
							} else if (direction == 3) {
								interCount = interCount + 1;
							}
						}
						long diff = tempTimeMilli - trafficSummary.getEndtTime();
						if (diff > 0 && (diff < increment)) {
							if (direction == 1) {
								inLogCount = inLogCount + 1;
							} else if (direction == 2) {
								outLogCount = outLogCount + 1;
							} else if (direction == 3) {
								interCount = interCount + 1;
							}
						}
					}
					// Set the inbound,outbound and intercom based on the count
					// value.
					inBound.add("[" + (new Date(fromDateMilli)).getTime() + "," + inLogCount + "]");
					outBound.add("[" + (new Date(fromDateMilli)).getTime() + "," + outLogCount + "]");
					interCom.add("[" + (new Date(fromDateMilli)).getTime() + "," + interCount + "]");
					fromDateMilli = tempTimeMilli;

				}
				loadBean = new TrafficLoadBean();
				loadBean.setInBoundLoad(inBound.toString());
				loadBean.setOutBoundLoad(outBound.toString());
				loadBean.setInterComLoad(interCom.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close db connection
			cdrConnector.closeConnection(conn);
		}
		System.out.println("TotalTime:" + (new Date().getTime() - startTime));
		return loadBean;
	}

	public ArrayList<EnhancedCallLogsEntry> getCallLogsTimeZoneNew(String stEnterpriseId, String accountIdList, long fromDateMilli, long toDateMilli, ArrayList<String> phoneNumbers, HashMap<String, String> timeZoneMap) {
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        EnhancedCallLogsEntry callLogsEntry = null;
        ArrayList<EnhancedCallLogsEntry> list = new ArrayList<EnhancedCallLogsEntry>();
        String stTempNumber = null;
        String fromNumber = "";
        String toNumber = "";
        try {
            conn = new CDRConnector().getDBConnection();
           
			String stInsert = "select * from ( SELECT fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,subscriberid,enterpriseid,answerindicator," + "    SUM( " + "        CASE"
					+ "           WHEN direction = 'IN' " + "           THEN 1 " + "           ELSE 2 " + "        END) as directiontype, " + "     SUM( " + "        CASE "
					+ "        WHEN subscriberid IN(" + accountIdList + ") " + "        THEN 1 " + "        ELSE 0 " + "END) as ACCOUNTFILTER "
					+ "from tbCallLog   WHERE  enterpriseid=? and starttime>? and endtime<? " + "group by fromnumber,tonumber,starttime,endtime,duration ) as a "
					+ "where ACCOUNTFILTER!=0 order by starttime";
            System.out.println(stInsert);
            pstmt = conn.prepareStatement(stInsert);
//            pstmt.setString(1,accountIdList);
            pstmt.setString(1, stEnterpriseId);
            pstmt.setLong(2, fromDateMilli);
            pstmt.setLong(3, toDateMilli);
            
            System.out.println("This is the new query : "+pstmt.toString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                callLogsEntry = new EnhancedCallLogsEntry();
                callLogsEntry.setFromnumber(checkPhoneNumber(rs.getString("fromnumber")));
                callLogsEntry.setTonumber(checkPhoneNumber(rs.getString("tonumber")));
                callLogsEntry.setDatecreated(rs.getLong("starttime"));
                callLogsEntry.setAnswerIndicator(rs.getString("answerindicator"));
                callLogsEntry.setCallDate(getDate(rs.getLong("starttime")));
                callLogsEntry.setStarttime(rs.getLong("starttime"));
                callLogsEntry.setEndtime(rs.getLong("endtime"));
                callLogsEntry.setDuration(rs.getLong("duration"));
                callLogsEntry.setDirection(rs.getInt("directiontype"));
                if (timeZoneMap.get(callLogsEntry.getFromnumber())!=null) { //OutBound and Intercom
                    callLogsEntry.setTimeZone(getTimeZoneDate(rs.getLong("starttime"), timeZoneMap.get(callLogsEntry.getFromnumber())));
                } else {
                    callLogsEntry.setTimeZone(getTimeZoneDate(rs.getLong("starttime"), timeZoneMap.get(callLogsEntry.getTonumber())));
                }
                list.add(callLogsEntry);
            }
            System.out.println(list.size());
        } catch (SQLException ex) {
        	System.out.println(ex);
        	ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
        } finally {
            new CDRConnector().closeConnection(conn);
        }
        return list;
	}
	
	
	 public ArrayList<EnhancedCallLogsEntry> getCallLogsTimeZoneNew(String stEnterPriseId,String stAccountIds, ArrayList<String> phoneNumbers, HashMap<String, String> timeZoneMap) {
	        Logger.getLogger(CDRDBOperations.class.getName()).info("getCallLogsNew(String stEnterPriseId,ArrayList<String> phoneNumbers) : Comes in : ");
	        Connection conn = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        EnhancedCallLogsEntry callLogsEntry = null;
	        ArrayList<EnhancedCallLogsEntry> list = new ArrayList<EnhancedCallLogsEntry>();
	        String stTempNumber = null;
//	        String fromNumber = "";
//	        String toNumber = "";
	        try {
	            conn = new CDRConnector().getDBConnection();
	            
			String stInsert = "select * from ( SELECT fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,subscriberid,enterpriseid,answerindicator," + "    SUM( " + "        CASE"
					+ "           WHEN direction = 'IN' " + "           THEN 1 " + "           ELSE 2 " + "        END) as directiontype, " + "     SUM( " + "        CASE "
					+ "        WHEN subscriberid IN(" + stAccountIds + ") " + "        THEN 1 " + "        ELSE 0 " + "END) as ACCOUNTFILTER " + "from tbCallLog   WHERE  enterpriseid=? "
					+ "group by fromnumber,tonumber,starttime,endtime,duration ) as a " + "where ACCOUNTFILTER!=0 order by starttime";
	            pstmt = conn.prepareStatement(stInsert);
	            pstmt.setString(1, stEnterPriseId);
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                callLogsEntry = new EnhancedCallLogsEntry();
	                callLogsEntry.setFromnumber(checkPhoneNumber(rs.getString("fromnumber")));
	                callLogsEntry.setTonumber(checkPhoneNumber(rs.getString("tonumber")));
	                callLogsEntry.setDatecreated(rs.getLong("starttime"));
	                callLogsEntry.setCallDate(getDate(rs.getLong("starttime")));
	                callLogsEntry.setStarttime(rs.getLong("starttime"));
	                callLogsEntry.setEndtime(rs.getLong("endtime"));
	                callLogsEntry.setAnswerIndicator(rs.getString("answerindicator"));
	                callLogsEntry.setDuration(rs.getLong("duration"));
	                callLogsEntry.setDirection(rs.getInt("directiontype"));
	                if (timeZoneMap.get(callLogsEntry.getFromnumber())!=null) {
	                    callLogsEntry.setTimeZone(getTimeZoneDate(rs.getLong("starttime"), timeZoneMap.get(callLogsEntry.getFromnumber())));
	                } else {
	                    callLogsEntry.setTimeZone(getTimeZoneDate(rs.getLong("starttime"), timeZoneMap.get(callLogsEntry.getTonumber())));
	                }
	                list.add(callLogsEntry);
	            }
	        } catch (SQLException ex) {
	        	System.out.println(ex);
	        } catch (Exception ex) {
	        	System.out.println(ex);
	        } finally {
	           new CDRConnector().closeConnection(conn);
	        }
	        return list;
	    }
	
	 
	 public ArrayList<EnhancedCallLogsEntry> getCallLogsAdmin(String stEnterPriseId, long fromDateMilli, long toDateMilli, String stAdminTimeZone) {
	      System.out.println("getCallLogsTimeZoneNew Without Account Ids  "+stEnterPriseId);
	        Connection conn = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        EnhancedCallLogsEntry callLogsEntry = null;
	        ArrayList<EnhancedCallLogsEntry> list = new ArrayList<EnhancedCallLogsEntry>();
	        try {
	            conn = new CDRConnector().getDBConnection();
	            String stInsert = "select fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,answerindicator,SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directiontype,subscriberid from tbCallLog WHERE starttime>? and endtime<? and enterpriseid=? group by fromnumber,tonumber,starttime,endtime,duration order by starttime desc ";
	            System.out.println(stInsert);
	            pstmt = conn.prepareStatement(stInsert);
	            pstmt.setLong(1, fromDateMilli);
	            pstmt.setLong(2, toDateMilli);
	            pstmt.setString(3, stEnterPriseId);
	            System.out.println("This is the new query : "+pstmt.toString());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                callLogsEntry = new EnhancedCallLogsEntry();
	                callLogsEntry.setFromnumber(checkPhoneNumber(rs.getString("fromnumber")));
	                callLogsEntry.setTonumber(checkPhoneNumber(rs.getString("tonumber")));
	                callLogsEntry.setDatecreated(rs.getLong("starttime"));
	                callLogsEntry.setAnswerIndicator(rs.getString("answerindicator"));
	                callLogsEntry.setCallDate(getDate(rs.getLong("starttime")));
	                callLogsEntry.setStarttime(rs.getLong("starttime"));
	                callLogsEntry.setEndtime(rs.getLong("endtime"));
	                callLogsEntry.setDuration(rs.getLong("duration"));
	                callLogsEntry.setDirection(rs.getInt("directiontype"));
	                callLogsEntry.setTimeZone(getTimeZoneDate(rs.getLong("starttime"), stAdminTimeZone));
	                list.add(callLogsEntry);
	            }
	        } catch (SQLException ex) {
	            System.out.println(ex);
	            ex.printStackTrace();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            System.out.println(ex);
	        } finally {
	            new CDRConnector().closeConnection(conn);
	        }
	        return list;
	    }
	    
	    public ArrayList<EnhancedCallLogsEntry> getCallLogsAdmin(String stEnterPriseId, String stAdminTimeZone) {
	        Logger.getLogger(CDRDBOperations.class.getName()).info("getCallLogsTimeZoneNew Without Account Ids : Comes in : " +stEnterPriseId);
	        Connection conn = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        EnhancedCallLogsEntry callLogsEntry = null;
	        ArrayList<EnhancedCallLogsEntry> list = new ArrayList<EnhancedCallLogsEntry>();
	        try {
	            conn = new CDRConnector().getDBConnection();
	            String stInsert = "select fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,answerindicator,SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directiontype,subscriberid from tbCallLog WHERE enterpriseid=? group by fromnumber,tonumber,starttime,endtime,duration order by starttime desc ";
	            System.out.println(stInsert);
	            pstmt = conn.prepareStatement(stInsert);
	            pstmt.setString(1, stEnterPriseId);
	            System.out.println("This is the new query : "+pstmt.toString());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                callLogsEntry = new EnhancedCallLogsEntry();
	                callLogsEntry.setFromnumber(checkPhoneNumber(rs.getString("fromnumber")));
	                callLogsEntry.setTonumber(checkPhoneNumber(rs.getString("tonumber")));
	                callLogsEntry.setDatecreated(rs.getLong("starttime"));
	                callLogsEntry.setAnswerIndicator(rs.getString("answerindicator"));
	                callLogsEntry.setCallDate(getDate(rs.getLong("starttime")));
	                callLogsEntry.setStarttime(rs.getLong("starttime"));
	                callLogsEntry.setEndtime(rs.getLong("endtime"));
	                callLogsEntry.setDuration(rs.getLong("duration"));
	                callLogsEntry.setDirection(rs.getInt("directiontype"));
	                callLogsEntry.setTimeZone(getTimeZoneDate(rs.getLong("starttime"), stAdminTimeZone));
	                list.add(callLogsEntry);
	            }
	        } catch (SQLException ex) {
	            System.out.println(ex);
	            ex.printStackTrace();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        } finally {
	            new CDRConnector().closeConnection(conn);
	        }
	        return list;
	    }
	    
	    private String checkPhoneNumber(String stTempNumber) {
	        if (stTempNumber.startsWith("+1")) {
	            stTempNumber = stTempNumber.substring(2, stTempNumber.length());
	        }
	        if (stTempNumber.length() == 10) {
	            //stTempNumber = stTempNumber.substring(0, 3) + "-" + stTempNumber.substring(3, 6) + "-" + stTempNumber.substring(6, 10);
	        }
	        return stTempNumber;
	    }

	    private String getDate(long millSeconds) {
	        Date date = new Date(millSeconds);
	        DateFormat dateFormat = null;
	        if (isToday(date)) {
	            dateFormat = new SimpleDateFormat(" ; hh:mm a");
	            return "Today " + dateFormat.format(date);
	        } else {
	            dateFormat = new SimpleDateFormat("MM/dd/yy ; hh:mm a");
	            return dateFormat.format(date);
	        }
	    }

	    private boolean isToday(Date date) {
//	        Calendar first = Calendar.getInstance();
//	        first.setTime(date);
//	        Calendar second = Calendar.getInstance();
//	        if(this.stTodayDate!=null)
//	        {
//	            DateFormat dtFormat = new SimpleDateFormat("M/d/y");
//	            try {
//	                Date todayDate = dtFormat.parse(this.stTodayDate);
//	                second.setTime(todayDate);
//	            } catch (ParseException ex) {
//	                java.util.logging.Logger.getLogger(CDRDBOperations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//	            }
//	        }
//	        if (first.get(Calendar.YEAR) == second.get(Calendar.YEAR) && first.get(Calendar.MONTH) == second.get(Calendar.MONTH) && first.get(Calendar.DATE) == second.get(Calendar.DATE)) {
//	            return true;
//	        }
	        return false;
	    }

	    private String getTimeZoneDate(long timeMillSeconds, String stTimeKey) {
	        String[] split = null;
	        if (stTimeKey != null && !stTimeKey.trim().equals("")) {
	            split = stTimeKey.split("---");
	        } else {
	            split = new String[2];
	            split[0] = "GMT";
	            split[1] = "GMT";
	        }
	        Date date = new Date(timeMillSeconds);
	        DateFormat dateFormat = null;
	        if (isToday(date)) {
	            dateFormat = new SimpleDateFormat(" ; hh:mm a");
	            dateFormat.setTimeZone(TimeZone.getTimeZone(split[0]));
	            return "Today " + dateFormat.format(date)+";  "+split[1];
	        } else {
	            dateFormat = new SimpleDateFormat("MM/dd/yy ; hh:mm a");
	            dateFormat.setTimeZone(TimeZone.getTimeZone(split[0]));
	            return dateFormat.format(date)+";  "+split[1];
	        }
	    }
	    

}
