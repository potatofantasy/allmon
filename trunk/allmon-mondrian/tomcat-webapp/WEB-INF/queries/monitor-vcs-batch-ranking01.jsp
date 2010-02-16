<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jp:mondrianQuery id="query01" jdbcDriver="oracle.jdbc.OracleDriver" jdbcUrl="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=LONTD03.CORP.TTC)(PORT=1521))(CONNECT_DATA=(SERVER=dedicated)(SERVICE_NAME=realtime)))" catalogUri="/WEB-INF/queries/schema-monitor.xml"
   jdbcUser="monitor" jdbcPassword="monitor" connectionPooling="false">

select 
  NON EMPTY Crossjoin(Crossjoin({[STARTDATETIME_YEAR].[ALL].Children}, {[STARTDATETIME_MONTH].[ALL].Children}), {[Measures].[EXECCOUNT], [Measures].[TOTALRUNTIME_MINS]}) ON COLUMNS,
  NON EMPTY Crossjoin({[MODULE].[ALL].Children}, {[PROCESSNAME].[ALL].Children}) ON ROWS
from [MONITOR_BATCH_JOBS_VMD]

</jp:mondrianQuery>

<c:set var="title01" scope="session">
Monitor data mart (Batch jobs) - Calls Distribution 01: Modules/Batches
</c:set>