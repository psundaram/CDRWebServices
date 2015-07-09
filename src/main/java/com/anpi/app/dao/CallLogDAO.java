package com.anpi.app.dao;

import java.util.List;

import com.anpi.app.domain.DirectionType;
import com.anpi.app.model.CallLogDetail;

public interface CallLogDAO{

	List<CallLogDetail> search();

	List<CallLogDetail> getCallLogsAdmin(String enterpriseId, Long time, Long timeInMillis,String callType, String searchKey, int pageNumber, int pageSize);

	List<CallLogDetail> getCallLogsTimeZoneNew(String enterpriseId, String accountIdList, Long time, Long toTime,String callType,String searchKey,int pageNumber, int pageSize);

	DirectionType getCallLogCount(String enterpriseId, String accountIdList, Long time, Long toTime) throws Exception;

	DirectionType getCallLogCountForAdmin(String enterpriseId, Long time, Long toTime) throws Exception;
	

}
