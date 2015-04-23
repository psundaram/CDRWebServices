package com.anpi.app.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.anpi.app.domain.TrafficLoadBean;
import com.anpi.app.domain.TrafficSummary;

public class CDRDBOperations {


	public  TrafficLoadBean getTrafficLoad(String stEnterPriseId, long fromDateMilli, long toDateMilli, long increment) throws Exception {
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
				String stInsert = "select starttime,endtime,SUM(CASE WHEN a.direction = 'In' THEN 1 ELSE 2 END) as directiontype from (SELECT direction,starttime,endtime,fromnumber,tonumber,duration,SourceRecordId FROM tbCallLog where enterpriseid =? and (starttime>? and endtime<?)) as a group by a.starttime,a.endtime,a.fromnumber,a.tonumber,a.duration,a.SourceRecordId;";
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
				System.out.println("TrafficSummary Size :"+trafficSummaries.size());
				// Calculate inbound,outbound, intercom values based on interval
				while (fromDateMilli < toDateMilli) {
					long tempTimeMilli = fromDateMilli + increment;
					int direction = 0;
					int inLogCount = 0;
					int outLogCount = 0;
					int interCount = 0;
					// Checks for the existence of time interval with the db values. If values exist, then check for the direction and increment the respective counter by 1
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
					// Set the inbound,outbound and intercom based on the count value.
					inBound.add("[" + (new Date(fromDateMilli)).getTime() + "," + inLogCount + "]");
					outBound.add("[" + (new Date(fromDateMilli)).getTime() + "," + 0 + "]");
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
		}finally{
			// Close db connection
			cdrConnector.closeConnection(conn);
		}
		System.out.println("TotalTime:" + (new Date().getTime() - startTime));
		return loadBean;
	}

	

}
