package org.allmon.client.agent.snmp;

public class HostResourcesMib {
	// root address: .1.3.6.1.2.1.25
	public static final String ROOT_OID_NAME = ".iso.org.dod.internet.mgmt.mib-2.host";
	
	/*			HR DEVICE SECTION */
	/**
	 * The average, over the last minute, of the
	 * percentage of time that this processor was not
	 * idle.
	 */
	public static final String HR_PROCESSOR_LOAD_OID = ".1.3.6.1.2.1.25.3.3.1.2";

	/*			HR SW RUN SECTION */	
	/**
	 *  A unique value for each piece of software running
	 *  on the host.  Wherever possible, this should be the
	 *  system's native, unique identification number.
	 */
	public static final String HR_SW_RUN_INDEX = ".1.3.6.1.2.1.25.4.2.1.1";
	
	/**
	 *  A textual description of this running piece of
	 *  software, including the manufacturer, revision,
	 *  and the name by which it is commonly known.  If
	 *  this software was installed locally, this should be
	 *  the same string as used in the corresponding
	 *  hrSWInstalledName.
	 *  example: IEXPLORE.EXE
	 */
	public static final String HR_SW_RUN_NAME = ".1.3.6.1.2.1.25.4.2.1.2";	
	
	/**
	 *  The type of this software.
	 *  example: application, operatingSystem
	 */
	public static final String HR_SW_RUN_TYPE = ".1.3.6.1.2.1.25.4.2.1.6";
	
	/*			HR SW RUN PERF SECTION */
	/**
	 * The number of centi-seconds of the total system's
	 * CPU resources consumed by this process.  Note that
	 * on a multi-processor system, this value may
	 * increment by more than one centi-second in one
	 * centi-second of real (wall clock) time.
	 * On Windows XP it's refreshed every 2 minutes
	 */
	public static final String HR_SW_RUN_PERF_CPU = ".1.3.6.1.2.1.25.5.1.1.1";
	
	/**
	 *  The total amount of real system memory allocated
	 *  to this process in Kbytes.
	 */
	public static final String HR_SW_RUN_PERF_MEM = ".1.3.6.1.2.1.25.5.1.1.2";
}
