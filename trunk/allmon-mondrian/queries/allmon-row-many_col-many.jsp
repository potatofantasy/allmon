<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>


<jp:mondrianQuery id="query01" jdbcDriver="oracle.jdbc.OracleDriver" 
   jdbcUrl="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=LONTD03.CORP.TTC)(PORT=1521))(CONNECT_DATA=(SERVER=dedicated)(SERVICE_NAME=realtime)))" 
   catalogUri="/WEB-INF/queries/schema-allmetrics.xml"
   jdbcUser="allmon" jdbcPassword="allmon" connectionPooling="false">

select Hierarchize(Crossjoin({[Measures].[METRICVALUE]}, Union({[METRICNAME].[ALL]}, [METRICNAME].[ALL].Children))) ON COLUMNS,
  Hierarchize(Union({[CALENDAR_YEAR].[ALL]}, [CALENDAR_YEAR].[ALL].Children)) ON ROWS
from [ALLMETRICS_METRICSDATA]

</jp:mondrianQuery>

<c:set var="title01" scope="session">Test Query uses Mondrian OLAP for Allmon </c:set>
