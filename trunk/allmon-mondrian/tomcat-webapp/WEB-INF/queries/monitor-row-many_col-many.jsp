<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jp:mondrianQuery id="query01" jdbcDriver="oracle.jdbc.OracleDriver" jdbcUrl="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=LONTD03.CORP.TTC)(PORT=1521))(CONNECT_DATA=(SERVER=dedicated)(SERVICE_NAME=realtime)))" catalogUri="/WEB-INF/queries/schema-monitor.xml"
   jdbcUser="monitor" jdbcPassword="monitor" connectionPooling="false">

select ([ActionClasses].[All]) ON COLUMNS,
  ([Date].[All]) ON ROWS
from [MONITOR_ACTION_CLASSES]

</jp:mondrianQuery>

<c:set var="title01" scope="session">Test Query uses Mondrian OLAP for Monitor data mart</c:set>
 