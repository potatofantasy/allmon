<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jp:mondrianQuery id="query01" jdbcDriver="oracle.jdbc.OracleDriver" jdbcUrl="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=LONTD03.CORP.TTC)(PORT=1521))(CONNECT_DATA=(SERVER=dedicated)(SERVICE_NAME=realtime)))" catalogUri="/WEB-INF/queries/schema-monitor.xml"
   jdbcUser="monitor" jdbcPassword="monitor" connectionPooling="false">

select Crossjoin({[ActionClasses].[ALL].[commonservices], [ActionClasses].[ALL].[contracts], [ActionClasses].[ALL].[operations], [ActionClasses].[ALL].[products], [ActionClasses].[ALL].[reservations]}, {[Measures].[EXECCOUNT]}) ON COLUMNS,
  {[Date].[ALL].[2008].[1], [Date].[ALL].[2008].[2], [Date].[ALL].[2008].[3], [Date].[ALL].[2008].[4], [Date].[ALL].[2008].[5], [Date].[ALL].[2008].[6], [Date].[ALL].[2008].[7], [Date].[ALL].[2008].[8], [Date].[ALL].[2008].[9], [Date].[ALL].[2008].[10], [Date].[ALL].[2008].[11], [Date].[ALL].[2008].[12], [Date].[ALL].[2009].[1], [Date].[ALL].[2009].[2], [Date].[ALL].[2009].[3], [Date].[ALL].[2009].[4], [Date].[ALL].[2009].[5], [Date].[ALL].[2009].[6], [Date].[ALL].[2009].[7], [Date].[ALL].[2009].[8], [Date].[ALL].[2009].[9], [Date].[ALL].[2009].[10], [Date].[ALL].[2009].[11], [Date].[ALL].[2009].[12]} ON ROWS
from [MONITOR_ACTION_CLASSES]

</jp:mondrianQuery>

<c:set var="title01" scope="session">Test Query uses Mondrian OLAP for Monitor data mart</c:set>
 