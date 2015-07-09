package com.anpi.app.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.anpi.app.config.GroupByHavingProjection;
import com.anpi.app.dao.CallLogDAO;
import com.anpi.app.domain.DirectionType;
import com.anpi.app.model.CallLogDetail;
import com.anpi.app.util.CDRConnector;
import com.google.common.base.Strings;

@Repository("callLogDAO")
public class CallLogDAOImpl implements CallLogDAO {
	@Autowired
	SessionFactory sessionFactory;

	protected final Session getCurrentSession() {
		return this.sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public List<CallLogDetail> search() {
		System.out.println("Search:");
		Criteria criteria =  getCurrentSession().createCriteria(CallLogDetail.class,"cd");
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.alias(Projections.groupProperty("fromNumber"), "fromNumber"));
		projList.add(Projections.alias(Projections.groupProperty("toNumber"), "toNumber"));
		projList.add(Projections.alias(Projections.groupProperty("startTime"), "startTime"));
		projList.add(Projections.alias(Projections.groupProperty("endTime"), "endTime"));
//		projList.add(Projections.alias(Projections.groupProperty("duration"), "duration"));
		projList.add(Projections.alias(Projections.property("datecreated"), "datecreated"));
		projList.add(Projections.alias(Projections.property("answerIndicator"), "answerIndicator"));
		projList.add(Projections.alias(Projections.property("subscriberId"), "subscriberId"));
		projList.add(Projections.alias(Projections.property("direction"), "direction"));
		projList.add(Projections.alias(Projections.property("enterpriseId"), "enterpriseId"));
		projList.add(Projections.alias(Projections.sqlProjection("SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directionType", new String[] { "directionType" }, new Type[] { Hibernate.INTEGER }),"directionType"));
		String accountIdList ="10022413";
//		projList.add(Projections.alias(Projections.sqlProjection("SUM(CASE WHEN subscriberid IN("+accountIdList+") THEN 1 ELSE 0 END) as accountFilter", new String[] { "accountFilter" }, new Type[] { Hibernate.STRING}), "accountFilter"));
		
		projList.add(new GroupByHavingProjection("duration", Projections.sqlProjection("SUM(CASE WHEN subscriberid IN("+accountIdList+") THEN 1 ELSE 0 END) as accountFilter)",new String[]{},new Type[]{}), "!=", 0));
		Criterion criterion = Restrictions.conjunction().add(Restrictions.eq("cd.enterpriseId", "1003075")).add(Restrictions.ge("cd.startTime", new Long(String.valueOf("1430092800000"))))
				.add(Restrictions.le("cd.endTime", new Long(String.valueOf("1430179199000"))));
		criteria.setProjection(projList).add(criterion).setResultTransformer(Transformers.aliasToBean(CallLogDetail.class));
		criteria.addOrder(Order.desc("startTime"));
		System.out.println("criteria : "+ criteria);
		List<CallLogDetail> listOfCallLogs = criteria.list();
		System.out.println("listOfCallLogs:"+listOfCallLogs.size());
		return  listOfCallLogs;

	}

	public List<CallLogDetail> getCallLogsAdmin(String enterpriseId, Long time, Long timeInMillis,String callType,String searchKey,int pageNumber,int pageSize) {
		System.out.println("getCallLogsTimeZoneNew Without Account Ids  " + enterpriseId);
		Criteria criteria = getCurrentSession().createCriteria(CallLogDetail.class);
		if(pageNumber!=0){
			if(pageSize!=0)
			pageNumber = (pageNumber - 1) * pageSize;
			criteria.setFirstResult(pageNumber);
		}
		if(pageSize!=0){
			criteria.setMaxResults(pageSize);
		}
		System.out.println("PageNumber:"+pageNumber + " page Size: "+ pageSize);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.alias(Projections.groupProperty("fromNumber"), "fromNumber"));
		projList.add(Projections.alias(Projections.groupProperty("toNumber"), "toNumber"));
		projList.add(Projections.alias(Projections.groupProperty("startTime"), "startTime"));
		projList.add(Projections.alias(Projections.groupProperty("endTime"), "endTime"));
		projList.add(Projections.alias(Projections.groupProperty("duration"), "duration"));
		projList.add(Projections.alias(Projections.property("datecreated"), "datecreated"));
		projList.add(Projections.alias(Projections.property("answerIndicator"), "answerIndicator"));
		projList.add(Projections.alias(Projections.property("subscriberId"), "subscriberId"));
		projList.add(Projections.alias(Projections.property("direction"), "direction"));
		projList.add(Projections.alias(Projections.property("enterpriseId"), "enterpriseId"));
		projList.add(Projections.alias(Projections.sqlProjection("SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directionType", new String[] { "directionType" }, new Type[] { Hibernate.INTEGER }),"directionType"));
		
		criteria.add(Restrictions.eq("this.enterpriseId", enterpriseId));
		System.out.println("time:"+time);
		if(time!=null && !time.equals("") && timeInMillis!=null && !timeInMillis.equals("")){
			Criterion criterion = Restrictions.conjunction().add(Restrictions.ge("this.startTime", time)).
			add(Restrictions.le("this.endTime", timeInMillis));
			criteria.add(criterion);
		}
		if(!Strings.isNullOrEmpty(searchKey)){
			Criterion criterion = Restrictions.disjunction().add(Restrictions.like("this.fromNumber", "%"+searchKey+"%")).
					add(Restrictions.like("this.toNumber", "%"+searchKey+"%"));
			criteria.add(criterion);
		}
		
		if(!Strings.isNullOrEmpty(callType)){
			projList.add(new GroupByHavingProjection("duration", Projections.sqlProjection("SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directionType",new String[]{},new Type[]{}), "=", Integer.parseInt(callType)));
		}
		
		criteria.setProjection(projList).setResultTransformer(Transformers.aliasToBean(CallLogDetail.class));
		criteria.addOrder(Order.desc("startTime"));
		System.out.println("Criteria :" + criteria);
		List<CallLogDetail> listOfCallLogs = criteria.list();
		System.out.println("listOfCallLogs"+listOfCallLogs.size());
		return listOfCallLogs;
		
	}


	public List<CallLogDetail> getCallLogsTimeZoneNew(String enterpriseId, String accountIdList, Long time, Long toTime,String callType,String searchKey, int pageNumber,int pageSize) {
		System.out.println("getCallLogsTimeZoneNew Without Account Ids  " + enterpriseId + ","+ accountIdList);
		Criteria criteria =  getCurrentSession().createCriteria(CallLogDetail.class,"cd");
		if(pageNumber!=0){
			if(pageSize!=0)
			pageNumber = (pageNumber - 1) * pageSize;
			criteria.setFirstResult(pageNumber);
		}
		
		if(pageSize!=0){
			criteria.setMaxResults(pageSize);
		}
		System.out.println("PageNumber:"+pageNumber + " page Size: "+ pageSize);
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.alias(Projections.groupProperty("fromNumber"), "fromNumber"));
		projList.add(Projections.alias(Projections.groupProperty("toNumber"), "toNumber"));
		projList.add(Projections.alias(Projections.groupProperty("startTime"), "startTime"));
		projList.add(Projections.alias(Projections.groupProperty("endTime"), "endTime"));
//		projList.add(Projections.alias(Projections.groupProperty("duration"), "duration"));
		projList.add(Projections.alias(Projections.property("datecreated"), "datecreated"));
		projList.add(Projections.alias(Projections.property("answerIndicator"), "answerIndicator"));
		projList.add(Projections.alias(Projections.property("subscriberId"), "subscriberId"));
		projList.add(Projections.alias(Projections.property("direction"), "direction"));
		projList.add(Projections.alias(Projections.property("enterpriseId"), "enterpriseId"));
		projList.add(Projections.alias(Projections.sqlProjection("SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directionType", new String[] { "directionType" }, new Type[] { Hibernate.INTEGER }),"directionType"));
		if(!Strings.isNullOrEmpty(callType)){
			projList.add(new GroupByHavingProjection("duration", Projections.sqlProjection("directiontype="+Integer.parseInt(callType)+" and SUM(CASE WHEN subscriberid IN("+accountIdList+") THEN 1 ELSE 0 END) as accountFilter )",new String[]{},new Type[]{}), "!=", 0));
		}else{
			projList.add(new GroupByHavingProjection("duration", Projections.sqlProjection("SUM(CASE WHEN subscriberid IN("+accountIdList+") THEN 1 ELSE 0 END) as accountFilter )",new String[]{},new Type[]{}), "!=", 0));
		}
		criteria.add(Restrictions.eq("cd.enterpriseId", enterpriseId));
		if(time!=null && !time.equals("") && toTime!=null && !toTime.equals("")){
			Criterion criterion = Restrictions.conjunction().add(Restrictions.ge("cd.startTime", time)).
			add(Restrictions.le("cd.endTime", toTime));
			criteria.add(criterion);
		}
		if(!Strings.isNullOrEmpty(searchKey)){
			Criterion criterion = Restrictions.disjunction().add(Restrictions.like("cd.fromNumber", "%"+searchKey+"%")).
					add(Restrictions.like("cd.toNumber", "%"+searchKey+"%"));
			criteria.add(criterion);
		}
		criteria.setProjection(projList).setResultTransformer(Transformers.aliasToBean(CallLogDetail.class));
		criteria.addOrder(Order.desc("startTime"));
		System.out.println("criteria : "+ criteria);
//		List<CallLogDetail> listOfCallLogs = criteria.list();
		List<CallLogDetail> listOfCallLogs = new ArrayList<CallLogDetail>();
		System.out.println("listOfCallLogs:"+listOfCallLogs.size());
		return listOfCallLogs;
	}

	public DirectionType getCallLogCount(String enterpriseId, String accountIdList, Long time, Long toTime) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		DirectionType directionType = new DirectionType();
		try {
			conn = CDRConnector.getDBConnection();
			String sql = "";
			if(time!=null && !time.equals("") && toTime!=null && !toTime.equals("")){
				sql = "select directionType,count(B.directionType) as count  from  (select * from ( SELECT fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,subscriberid,enterpriseid,answerindicator," + "    SUM( " + "        CASE"
						+ "           WHEN direction = 'IN' " + "           THEN 1 " + "           ELSE 2 " + "        END) as directiontype, " + "     SUM( " + "        CASE "
						+ "        WHEN subscriberid IN(" + accountIdList + ") " + "        THEN 1 " + "        ELSE 0 " + "END) as ACCOUNTFILTER "
						+ "from tbCallLog   WHERE  enterpriseid='"+enterpriseId+ "' and starttime>"+time+ " and endtime<"+toTime+" " + "group by fromnumber,tonumber,starttime,endtime,duration ) as a "
						+ "where ACCOUNTFILTER!=0 order by starttime) as B group by directionType ;";
			}else{
				sql = "select directionType,count(B.directionType) as count  from  (select * from ( SELECT fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,subscriberid,enterpriseid,answerindicator," + "    SUM( " + "        CASE"
						+ "           WHEN direction = 'IN' " + "           THEN 1 " + "           ELSE 2 " + "        END) as directiontype, " + "     SUM( " + "        CASE "
						+ "        WHEN subscriberid IN(" + accountIdList + ") " + "        THEN 1 " + "        ELSE 0 " + "END) as ACCOUNTFILTER "
						+ "from tbCallLog   WHERE  enterpriseid='"+enterpriseId+ "' group by fromnumber,tonumber,starttime,endtime,duration ) as a "
						+ "where ACCOUNTFILTER!=0 order by starttime) as B group by directionType ;";
			}
			System.out.println("Sql:" + sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			directionType = new DirectionType();
			while (rs.next()) {
				if (rs.getString("directionType").equals("1")) {
					directionType.setIncomingCount(rs.getInt("count"));
				} else if (rs.getString("directionType").equals("2")) {
					directionType.setOutgoingCount(rs.getInt("count"));
				} else if (rs.getString("directionType").equals("3")) {
					directionType.setIntercomCount(rs.getInt("count"));
				}
			}
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		return directionType;
		
	}

	public DirectionType getCallLogCountForAdmin(String enterpriseId, Long time, Long toTime) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		DirectionType directionType = new DirectionType();
		try {
			conn = CDRConnector.getDBConnection();
			String sql = "";
			if (time != null && !time.equals("") && toTime != null && !toTime.equals("")) {
				sql = "select directionType,count(B.directionType) as count  from  (select fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,answerindicator,SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directiontype,subscriberid from tbCallLog WHERE " +
						"enterpriseid='"
						+ enterpriseId
						+ "' and starttime>"
						+ time
						+ " and endtime<"
						+ toTime
						+ " group by fromnumber,tonumber,starttime,endtime,duration order by starttime desc ) as B group by directionType ;";

			} else {
				sql = "select directionType,count(B.directionType) as count  from  (select fromnumber,tonumber,starttime,endtime,duration,datecreated,direction,answerindicator,SUM(CASE WHEN direction = 'In' THEN 1 ELSE 2 END) as directiontype,subscriberid from tbCallLog WHERE " +
						"enterpriseid='"
						+ enterpriseId + "' group by fromnumber,tonumber,starttime,endtime,duration order by starttime desc ) as B group by directionType ;";
			}
			System.out.println("Sql:" + sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			directionType = new DirectionType();
			while (rs.next()) {

				if (rs.getString("directionType").equals("1")) {
					directionType.setIncomingCount(rs.getInt("count"));
				} else if (rs.getString("directionType").equals("2")) {
					directionType.setOutgoingCount(rs.getInt("count"));
				} else if (rs.getString("directionType").equals("3")) {
					directionType.setIntercomCount(rs.getInt("count"));
				}
			}
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
		return directionType;
	}
		
}
