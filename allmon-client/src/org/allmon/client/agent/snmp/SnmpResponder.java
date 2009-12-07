package org.allmon.client.agent.snmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;


public class SnmpResponder {
	private SnmpSettings settings;
	private SnmpExtractor extractor;
	private Snmp snmp;
	private CommunityTarget target;

	public SnmpResponder(SnmpSettings settings) {
		this.settings = settings;
		this.extractor = new SnmpExtractor();
		init();		
	}

	private void init() {
		// Create an instance of the SNMP class and CommunityTarget class 
		try 
		{
			snmp = new Snmp(new DefaultUdpTransportMapping());
		} 
		catch (IOException e) 
		{		
			e.printStackTrace();
		}	

		target = new CommunityTarget();
		Address targetAddress = GenericAddress.parse("udp:" + settings.getIPAddress() + "/" + settings.getSnmpPortRes());

		// Set the address of the target device
		target.setAddress(targetAddress);

		// Set the version of the target device
		target.setVersion(settings.getSnmpVersion());

		// Set the timeout of the target device
		target.setTimeout(settings.getTimeout());

		// Set the community string of the target device
		target.setCommunity(new OctetString(settings.getReadCommunity()));
	}

	/**
	 *  SNMP GET
	 */	
	public SnmpResponse get(String oid) 
	{
		return get(oid, PDU.GET);	
	}

	/**
	 *  SNMP GETNEXT
	 */	
	public SnmpResponse getNext(String oid) 
	{
		return get(oid, PDU.GETNEXT);
	}		

	/*
	 *  SNMP getTable operation
	 */
	public List<SnmpResponseRow> getTable(String[] oidColumnsStr) 
	{

		// Invoke the listen() method on the Snmp object
		try 
		{
			snmp.listen();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}

		// Create a TableUtils 
		TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory());

		// Set the lower/upper bounds for the table operation
		OID lowerIndex = null;
		OID upperIndex = null;

		// Create an array of the OID's that need to be checked
		OID[] oidColumns = new OID[oidColumnsStr.length];
		for (int i=0; i<oidColumnsStr.length; i++)
			oidColumns[i] = new OID(oidColumnsStr[i]);

		// Transfer output to a data structure
		List<TableEvent> listOfTableEvents = utils.getTable(target, oidColumns, lowerIndex, upperIndex);

		return extractor.getResponseTable(listOfTableEvents);
	}	

	public SnmpResponseRow getColumn(String oidColumnsStr) 
	{

		// Invoke the listen() method on the Snmp object
		try 
		{
			snmp.listen();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}

		// Create a TableUtils 
		TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory());

		// Set the lower/upper bounds for the table operation
		OID lowerIndex = null;
		OID upperIndex = null;

		// Create an array of the OID's that need to be checked
		OID[] oidColumns = new OID[1];
	    oidColumns[0] = new OID(oidColumnsStr);

		// Transfer output to a data structure
		List<TableEvent> listOfTableEvents = utils.getTable(target, oidColumns, lowerIndex, upperIndex);

		return extractor.getResponseColumn(listOfTableEvents);
	}	
	
	/**
	 *  get/getNext operation
	 */ 
	private SnmpResponse get(String oid, int pduType)  
	{
		// Create a PDU
		PDU requestPDU = new PDU();
		requestPDU.add(new VariableBinding(new OID(oid))); 
		requestPDU.setType(pduType);

		ResponseEvent response = null;
		// get OID value
		try
		{
			// set snmp port into listen mode
			snmp.listen();

			// Send the PDU constructed, to the target
			response = snmp.send(requestPDU, target);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				// close the port
				snmp.close();
			}
			catch(IOException e) {
				e.printStackTrace();				
			}
		}

		// get response
		return extractor.getResponse(response);
	}



}
