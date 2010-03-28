
-- 
SELECT v.instancename, v.hostname, v.hostip, v.resourcename, v.sourcename, v.year, v.month, v.DAY, v.hour, 
       AVG(v.metricvalue), COUNT(*)
FROM   vam_metricsdata_cal v
WHERE  v.artifactcode = 'JVM'
AND    v.metriccode = 'JVMJMX'
GROUP BY v.instancename, v.hostname, v.hostip, v.resourcename, v.sourcename, v.year, v.month, v.DAY, v.hour
ORDER BY 1,2,3,4,5,6,7,8,9,10;


SELECT v.instancename, v.hostname, v.hostip, v.resourcename, v.sourcename, v.ts, --v.year, v.month, v.DAY, v.hour, v.minute,
       AVG(v.metricvalue), COUNT(*)
FROM   vam_metricsdata_cal v
WHERE  v.artifactcode = 'JVM'
AND    v.metriccode = 'JVMJMX'
AND    v.MONTH = 12 AND v.DAY = 12
GROUP BY v.instancename, v.hostname, v.hostip, v.resourcename, v.sourcename, v.ts --v.year, v.month, v.DAY, v.hour, v.minute,
ORDER BY 1,2,3,4,5,6,7,8;
