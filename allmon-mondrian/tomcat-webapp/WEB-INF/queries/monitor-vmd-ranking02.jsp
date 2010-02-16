<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jp:mondrianQuery id="query01" jdbcDriver="oracle.jdbc.OracleDriver" jdbcUrl="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=LONTD03.CORP.TTC)(PORT=1521))(CONNECT_DATA=(SERVER=dedicated)(SERVICE_NAME=realtime)))" catalogUri="/WEB-INF/queries/schema-monitor.xml"
   jdbcUser="monitor" jdbcPassword="monitor" connectionPooling="false">

/*
select 
  Crossjoin({[MODULE_NAME].[ALL], [MODULE_NAME].[ALL].Children}, {[Measures].[EXECCOUNT]}) ON COLUMNS,
  --Hierarchize(Crossjoin({[YEAR].[ALL].Children}, Union({[MONTH].[ALL]}, [MONTH].[ALL].Children))) ON ROWS
  Hierarchize(Crossjoin({[YEAR].[ALL].Children}, [MONTH].[ALL].Children)) ON ROWS
from [MONITOR_ACTION_CLASSES_VMD]
*/

select 
  NON EMPTY Crossjoin(Crossjoin({[YEAR].[ALL].Children}, {[MONTH].[ALL].Children}), {[Measures].[EXECCOUNT], [Measures].[SUM_EXECTIME_MIN]}) ON COLUMNS,
  NON EMPTY Crossjoin(Crossjoin({[MODULE_NAME].[ALL].Children}, {[SUBMODULE].[ALL].Children}), {[CLASS_NAME].[ALL].Children}) ON ROWS
from [MONITOR_ACTION_CLASSES_VMD]

</jp:mondrianQuery>

<c:set var="title01" scope="session">
Monitor data mart (Action classes) - Ranking 02: Ranking of Tropics classes
</c:set>