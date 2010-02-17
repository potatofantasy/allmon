<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jp:mondrianQuery id="query01" jdbcDriver="oracle.jdbc.OracleDriver" jdbcUrl="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=LONTD03.CORP.TTC)(PORT=1521))(CONNECT_DATA=(SERVER=dedicated)(SERVICE_NAME=realtime)))" catalogUri="/WEB-INF/queries/schema-monitor.xml"
   jdbcUser="monitor" jdbcPassword="monitor" connectionPooling="false">

select NON EMPTY Hierarchize(Union(Union({[DatetimeWeeksOfYear].[ALL], [DatetimeWeeksOfYear].[ALL].[2008], [DatetimeWeeksOfYear].[ALL].[2009]}, [DatetimeWeeksOfYear].[ALL].[2008].Children), [DatetimeWeeksOfYear].[ALL].[2009].Children)) ON COLUMNS,
  NON EMPTY {([ActionClasses].[ALL].[commonservices].[alerts], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[batch], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[brand], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[ChangeSellingCompanyAction], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[ChangeSubSystemAction], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[GetOperatingRegionsAction], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[GetOprRegProStdAction], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[GetSellingCompanyAction], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[GetSupplyPurposeDetailsAction], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[masters], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[OpenHelpAction], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[purgedata], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[role], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[uploaddata], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[commonservices].[user], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[allotment], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[approvalprocess], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[basicsupplier], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[citysheet], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[common], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[cruise], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[ferry], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[groundhandler], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[hotelsupplier], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[insurance], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[ldc], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[localservice], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[miscellaneous], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[motorcoachsupplier], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[proposal], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[reports], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[restaurant], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[setupfunction], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[tourpersonnel], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[tourseries], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[train], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[contracts].[venuesupplier], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[operations].[administration], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[operations].[inventorymanagement], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[operations].[operatingcompanytaxsetup], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[operations].[operationfinancialsDSFE], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[operations].[operationfinancialsSPRR], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[operations].[opscomn], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[operations].[purchasing], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[operations].[resourcescheduling], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[operations].[yieldmanagement], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[products].[contracts], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[products].[costingandpricing], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[products].[nftbuildpart1], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[products].[nftbuildpart2], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[products].[nonfitair], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[products].[quote], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[reservations].[air], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[reservations].[booking], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[reservations].[clientdocumentation], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[reservations].[finance], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[reservations].[policies], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[reservations].[products], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[reservations].[reports], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[reservations].[sctaxsetup], [Measures].[SUM_EXECTIME_MIN]), ([ActionClasses].[ALL].[reservations].[tam], [Measures].[SUM_EXECTIME_MIN])} ON ROWS
from [MONITOR_ACTION_CLASSES]

</jp:mondrianQuery>

<c:set var="title01" scope="session">Test Query uses Mondrian OLAP for Monitor data mart</c:set>