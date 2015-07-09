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

import org.apache.axis.client.Call;

import com.anpi.app.domain.CallLogs;
import com.anpi.app.domain.EnhancedCallLogsEntry;
import com.anpi.app.domain.TrafficLoadBean;
import com.anpi.app.domain.TrafficSummary;

public class CDRDBOperations {

	public TrafficLoadBean getTrafficLoad(String stEnterPriseId, long fromDateMilli, long toDateMilli, long increment) throws Exception {
		List<TrafficSummary> trafficSummaries = new ArrayList<TrafficSummary>();
		long startTime = new Date().getTime();
		System.out.println("sEnterpriseId :" + stEnterPriseId + ", fromDateMilli" + fromDateMilli + ",toDateMilli" + toDateMilli + ",increment:" + increment);
		TrafficLoadBean loadBean = null;
//		CDRConnector cdrConnector = new CDRConnector();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<String> inBound = new ArrayList<String>();
		ArrayList<String> outBound = new ArrayList<String>();
		ArrayList<String> interCom = new ArrayList<String>();
		try {
			if (fromDateMilli < toDateMilli) {
				// Connect to database
				conn = CDRConnector.getDBConnection();
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
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close db connection
			pstmt.close();
			rs.close();
			conn.close();
		}
		System.out.println("TotalTime:" + (new Date().getTime() - startTime));
		return loadBean;
	}

	public ArrayList<EnhancedCallLogsEntry> getCallLogsTimeZoneNew(String stEnterpriseId, String accountIdList, long fromDateMilli, long toDateMilli, ArrayList<String> phoneNumbers, HashMap<String, String> timeZoneMap,CallLogs callLogs) throws Exception {
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        EnhancedCallLogsEntry callLogsEntry = null;
        ArrayList<EnhancedCallLogsEntry> list = new ArrayList<EnhancedCallLogsEntry>();
        try {
            conn = CDRConnector.getDBConnection();
           
			String stInsert = "select * from ( SELECT fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,subscriberid,enterpriseid,answerindicator," + "    SUM( " + "        CASE"
					+ "           WHEN direction = 'IN' " + "           THEN 1 " + "           ELSE 2 " + "        END) as directiontype, " + "     SUM( " + "        CASE "
					+ "        WHEN subscriberid IN(" + accountIdList + ") " + "        THEN 1 " + "        ELSE 0 " + "END) as ACCOUNTFILTER "
					+ "from tbCallLog   WHERE  enterpriseid=? and starttime>? and endtime<? " + "group by fromnumber,tonumber,starttime,endtime,duration ) as a "
					+ "where ACCOUNTFILTER!=0 order by starttime";
            System.out.println(stInsert);
            int pageSize= callLogs.getPageSize();
            int pageNumber = callLogs.getPageNumber();
           
    		if(pageSize!=0){
    			System.out.println("PageSize:"+pageSize);
    			stInsert+=" limit "+pageSize;
    			System.out.println("StInsert:"+stInsert);
    			
    		}
    		if(pageNumber!=0){
	    			if(pageSize!=0){
	    			pageNumber = (pageNumber - 1) * pageSize;
	    			stInsert+=" offset "+pageNumber;
	    			}
	    	}
    		pstmt = conn.prepareStatement(stInsert);
//            pstmt.setString(1,accountIdList);
            pstmt.setString(1, stEnterpriseId);
            pstmt.setLong(2, fromDateMilli);
            pstmt.setLong(3, toDateMilli);
            
            System.out.println("This is the new query : "+pstmt.toString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                callLogsEntry = new EnhancedCallLogsEntry();
                callLogsEntry.setFromnumber(CommonUtils.checkPhoneNumber(rs.getString("fromnumber")));
                callLogsEntry.setTonumber(CommonUtils.checkPhoneNumber(rs.getString("tonumber")));
                callLogsEntry.setDatecreated(rs.getLong("starttime"));
                callLogsEntry.setAnswerIndicator(rs.getString("answerindicator"));
                callLogsEntry.setCallDate(CommonUtils.getDate(rs.getLong("starttime")));
                callLogsEntry.setStarttime(rs.getLong("starttime"));
                callLogsEntry.setEndtime(rs.getLong("endtime"));
                callLogsEntry.setDuration(rs.getLong("duration"));
                callLogsEntry.setDirection(rs.getInt("directiontype"));
                if (timeZoneMap.get(callLogsEntry.getFromnumber())!=null) { //OutBound and Intercom
                    callLogsEntry.setTimeZone(CommonUtils.getTimeZoneDate(rs.getLong("starttime"), timeZoneMap.get(callLogsEntry.getFromnumber())));
                } else {
                    callLogsEntry.setTimeZone(CommonUtils.getTimeZoneDate(rs.getLong("starttime"), timeZoneMap.get(callLogsEntry.getTonumber())));
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
        	pstmt.close();
			rs.close();
			conn.close();
        }
        return list;
	}
	
	
	 public ArrayList<EnhancedCallLogsEntry> getCallLogsTimeZoneNew(String stEnterPriseId,String stAccountIds, ArrayList<String> phoneNumbers, HashMap<String, String> timeZoneMap,CallLogs callLogs) throws Exception {
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
	            conn = CDRConnector.getDBConnection();
	            
			String stInsert = "select * from ( SELECT fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,subscriberid,enterpriseid,answerindicator," + "    SUM( " + "        CASE"
					+ "           WHEN direction = 'IN' " + "           THEN 1 " + "           ELSE 2 " + "        END) as directiontype, " + "     SUM( " + "        CASE "
					+ "        WHEN subscriberid IN(" + stAccountIds + ") " + "        THEN 1 " + "        ELSE 0 " + "END) as ACCOUNTFILTER " + "from tbCallLog   WHERE  enterpriseid=? "
					+ "group by fromnumber,tonumber,starttime,endtime,duration ) as a " + "where ACCOUNTFILTER!=0 order by starttime";
			int pageSize= callLogs.getPageSize();
            int pageNumber = callLogs.getPageNumber();
           
    		if(pageSize!=0){
    			System.out.println("PageSize:"+pageSize);
    			stInsert+=" limit "+pageSize;
    			System.out.println("StInsert:"+stInsert);
    			
    		}
    		if(pageNumber!=0){
	    			if(pageSize!=0){
	    			pageNumber = (pageNumber - 1) * pageSize;
	    			stInsert+=" offset "+pageNumber;
	    			}
	    	}   
			pstmt = conn.prepareStatement(stInsert);
	            pstmt.setString(1, stEnterPriseId);
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                callLogsEntry = new EnhancedCallLogsEntry();
	                callLogsEntry.setFromnumber(CommonUtils.checkPhoneNumber(rs.getString("fromnumber")));
	                callLogsEntry.setTonumber(CommonUtils.checkPhoneNumber(rs.getString("tonumber")));
	                callLogsEntry.setDatecreated(rs.getLong("starttime"));
	                callLogsEntry.setCallDate(CommonUtils.getDate(rs.getLong("starttime")));
	                callLogsEntry.setStarttime(rs.getLong("starttime"));
	                callLogsEntry.setEndtime(rs.getLong("endtime"));
	                callLogsEntry.setAnswerIndicator(rs.getString("answerindicator"));
	                callLogsEntry.setDuration(rs.getLong("duration"));
	                callLogsEntry.setDirection(rs.getInt("directiontype"));
	                if (timeZoneMap.get(callLogsEntry.getFromnumber())!=null) {
	                    callLogsEntry.setTimeZone(CommonUtils.getTimeZoneDate(rs.getLong("starttime"), timeZoneMap.get(callLogsEntry.getFromnumber())));
	                } else {
	                    callLogsEntry.setTimeZone(CommonUtils.getTimeZoneDate(rs.getLong("starttime"), timeZoneMap.get(callLogsEntry.getTonumber())));
	                }
	                list.add(callLogsEntry);
	            }
	        } catch (SQLException ex) {
	        	System.out.println(ex);
	        } catch (Exception ex) {
	        	System.out.println(ex);
	        } finally {
	        	pstmt.close();
				rs.close();
				conn.close();
	        }
	        return list;
	    }
	
	 
	 public ArrayList<EnhancedCallLogsEntry> getCallLogsAdmin(String stEnterPriseId, long fromDateMilli, long toDateMilli, String stAdminTimeZone,CallLogs callLogs) throws Exception {
	      System.out.println("getCallLogsTimeZoneNew Without Account Ids  "+stEnterPriseId);
	        Connection conn = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        EnhancedCallLogsEntry callLogsEntry = null;
	        ArrayList<EnhancedCallLogsEntry> list = new ArrayList<EnhancedCallLogsEntry>();
	        try {
	            conn = CDRConnector.getDBConnection();
	            System.out.println("conn"+conn);
	            String stInsert = "select fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,answerindicator,SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directiontype,subscriberid from tbCallLog WHERE starttime>? and endtime<? and enterpriseid=? group by fromnumber,tonumber,starttime,endtime,duration order by starttime desc ";
	            System.out.println(stInsert);
	            int pageSize= callLogs.getPageSize();
	            int pageNumber = callLogs.getPageNumber();
	           
	    		if(pageSize!=0){
	    			System.out.println("PageSize:"+pageSize);
	    			stInsert+=" limit "+pageSize;
	    			System.out.println("StInsert:"+stInsert);
	    			
	    		}
	    		if(pageNumber!=0){
		    			if(pageSize!=0){
		    			pageNumber = (pageNumber - 1) * pageSize;
		    			stInsert+=" offset "+pageNumber;
		    			}
		    	}
	            pstmt = conn.prepareStatement(stInsert);
	            pstmt.setLong(1, fromDateMilli);
	            pstmt.setLong(2, toDateMilli);
	            pstmt.setString(3, stEnterPriseId);
	            System.out.println("This is the new query : "+pstmt.toString());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                callLogsEntry = new EnhancedCallLogsEntry();
	                callLogsEntry.setFromnumber(CommonUtils.checkPhoneNumber(rs.getString("fromnumber")));
	                callLogsEntry.setTonumber(CommonUtils.checkPhoneNumber(rs.getString("tonumber")));
	                callLogsEntry.setDatecreated(rs.getLong("starttime"));
	                callLogsEntry.setAnswerIndicator(rs.getString("answerindicator"));
	                callLogsEntry.setCallDate(CommonUtils.getDate(rs.getLong("starttime")));
	                callLogsEntry.setStarttime(rs.getLong("starttime"));
	                callLogsEntry.setEndtime(rs.getLong("endtime"));
	                callLogsEntry.setDuration(rs.getLong("duration"));
	                callLogsEntry.setDirection(rs.getInt("directiontype"));
	                callLogsEntry.setTimeZone(CommonUtils.getTimeZoneDate(rs.getLong("starttime"), stAdminTimeZone));
	                list.add(callLogsEntry);
	            }
	        } catch (SQLException ex) {
	            System.out.println(ex);
	            ex.printStackTrace();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            System.out.println(ex);
	        } finally {
	        	pstmt.close();
				rs.close();
				conn.close();
	        }
	        return list;
	    }
	    
	    public ArrayList<EnhancedCallLogsEntry> getCallLogsAdmin(String stEnterPriseId, String stAdminTimeZone,CallLogs callLogs) throws Exception {
	        Logger.getLogger(CDRDBOperations.class.getName()).info("getCallLogsTimeZoneNew Without Account Ids : Comes in : " +stEnterPriseId);
	        Connection conn = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        EnhancedCallLogsEntry callLogsEntry = null;
	        ArrayList<EnhancedCallLogsEntry> list = new ArrayList<EnhancedCallLogsEntry>();
	        try {
	            conn = CDRConnector.getDBConnection();
	            String stInsert = "select fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,answerindicator,SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directiontype,subscriberid from tbCallLog WHERE enterpriseid=? group by fromnumber,tonumber,starttime,endtime,duration order by starttime desc ";
	            int pageSize= callLogs.getPageSize();
	            int pageNumber = callLogs.getPageNumber();
	           
	    		if(pageSize!=0){
	    			System.out.println("PageSize:"+pageSize);
	    			stInsert+=" limit "+pageSize;
	    			System.out.println("StInsert:"+stInsert);
	    			
	    		}
	    		if(pageNumber!=0){
		    			if(pageSize!=0){
		    			pageNumber = (pageNumber - 1) * pageSize;
		    			stInsert+=" offset "+pageNumber;
		    			}
		    	}
	            System.out.println(stInsert);
	            pstmt = conn.prepareStatement(stInsert);
	            pstmt.setString(1, stEnterPriseId);
	            System.out.println("This is the new query : "+pstmt.toString());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                callLogsEntry = new EnhancedCallLogsEntry();
	                callLogsEntry.setFromnumber(CommonUtils.checkPhoneNumber(rs.getString("fromnumber")));
	                callLogsEntry.setTonumber(CommonUtils.checkPhoneNumber(rs.getString("tonumber")));
	                callLogsEntry.setDatecreated(rs.getLong("starttime"));
	                callLogsEntry.setAnswerIndicator(rs.getString("answerindicator"));
	                callLogsEntry.setCallDate(CommonUtils.getDate(rs.getLong("starttime")));
	                callLogsEntry.setStarttime(rs.getLong("starttime"));
	                callLogsEntry.setEndtime(rs.getLong("endtime"));
	                callLogsEntry.setDuration(rs.getLong("duration"));
	                callLogsEntry.setDirection(rs.getInt("directiontype"));
	                callLogsEntry.setTimeZone(CommonUtils.getTimeZoneDate(rs.getLong("starttime"), stAdminTimeZone));
	                list.add(callLogsEntry);
	            }
	        } catch (SQLException ex) {
	            System.out.println(ex);
	            ex.printStackTrace();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        } finally {
	        	pstmt.close();
				rs.close();
				conn.close();
	        }
	        return list;
	    }
	    
	   
		public TrafficLoadBean getTrafficLoad1(String stEnterPriseId, long fromDateMilli, long toDateMilli, long increment) throws Exception {
		List<TrafficSummary> trafficSummaries = new ArrayList<TrafficSummary>();
		long startTime = new Date().getTime();
		System.out.println("sEnterpriseId :" + stEnterPriseId + ", fromDateMilli" + fromDateMilli + ",toDateMilli" + toDateMilli + ",increment:" + increment);
		TrafficLoadBean loadBean = null;
//		CDRConnector cdrConnector = CDRConnector;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<String> inBound = new ArrayList<String>();
		ArrayList<String> outBound = new ArrayList<String>();
		ArrayList<String> interCom = new ArrayList<String>();
		try {
			if (fromDateMilli < toDateMilli) {
				// Connect to database
				conn = CDRConnector.getDBConnection();
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
				//	System.out.println("startTime:" + trafficSummary.getStartTime() + " , directionType:" + trafficSummary.getDirection() + " ,endtime:" + trafficSummary.getEndtTime());
				}
				System.out.println("TrafficSummary Size :" + trafficSummaries.size());
				// Calculate inbound,outbound, intercom values based on interval
				
				long maxEndTime = 0l;
				int inCount = 0;
				int outCount = 0;
				int intercomCount = 0;
				while (fromDateMilli < toDateMilli) {
					long minStartTime = new Date().getTime();
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
							if(trafficSummary.getStartTime()<minStartTime){
								minStartTime = trafficSummary.getStartTime();
							}
							if(trafficSummary.getEndtTime()>maxEndTime)
								maxEndTime = trafficSummary.getEndtTime();
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
							if(trafficSummary.getEndtTime()>maxEndTime)
								maxEndTime = trafficSummary.getEndtTime();
							if(trafficSummary.getStartTime()<minStartTime)
								minStartTime = trafficSummary.getStartTime();
						}
					}
					
					// Set the inbound,outbound and intercom based on the count value
					
					if(inLogCount>0){
						inBound.add("{ \"x\":" + (new Date(fromDateMilli)).getTime() + ",\"y\": " + inLogCount + ",\"startTime\": "+(new Date(minStartTime)).getTime() + ",\"endTime\": " + (new Date(maxEndTime)).getTime() + "}");
					}
					else{
						inBound.add("{ \"x\":" + (new Date(fromDateMilli)).getTime() + ",\"y\": " + inLogCount + ",\"startTime\": 0 , \"endTime\": 0}");
					}
					
					if(outLogCount>0){
						outBound.add("{ \"x\":" + (new Date(fromDateMilli)).getTime() + ",\"y\": " + outLogCount + ",\"startTime\": "+(new Date(minStartTime)).getTime() + ",\"endTime\": " + (new Date(maxEndTime)).getTime() + "}");
					}
					else{
						outBound.add("{ \"x\":" + (new Date(fromDateMilli)).getTime() + ",\"y\": " + outLogCount + ",\"startTime\": 0 , \"endTime\": 0}");
						
					}
					
					if(interCount >0){
						interCom.add("{ \"x\":" + (new Date(fromDateMilli)).getTime() + ",\"y\": " + interCount + ",\"startTime\": "+(new Date(minStartTime)).getTime() + ",\"endTime\": " + (new Date(maxEndTime)).getTime() + "}");
						
					}
					else{
						interCom.add("{ \"x\":" + (new Date(fromDateMilli)).getTime() + ",\"y\": " + interCount + ",\"startTime\": 0 , \"endTime\": 0}");
					}
					
					//outBound.add("[" + (new Date(fromDateMilli)).getTime() + "," + outLogCount + "]");
					//interCom.add("[" + (new Date(fromDateMilli)).getTime() + "," + interCount + "]");
					inCount +=inLogCount;
					outCount +=outLogCount;
					intercomCount +=interCount;
					fromDateMilli = tempTimeMilli;

				}
				loadBean = new TrafficLoadBean();
				loadBean.setInBoundLoad(inBound.toString());
				loadBean.setOutBoundLoad(outBound.toString());
				loadBean.setInterComLoad(interCom.toString());
				loadBean.setInterComLoad(interCom.toString());
				loadBean.setInCount(inCount);
				loadBean.setOutCount(outCount);
				loadBean.setInterCount(intercomCount);
				System.out.println("inCount:"+inCount+"outcount:"+outCount+"interCount:"+intercomCount);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close db connection
			pstmt.close();
			rs.close();
			conn.close();
		}
		System.out.println("TotalTime:" + (new Date().getTime() - startTime));
		return loadBean;
	}
	    

}
