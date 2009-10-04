package org.allmon.server.loader;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

public class RawMetricLoadToAllmetric extends StoredProcedure {
    
    private static final Log logger = LogFactory.getLog(RawMetricLoadToAllmetric.class);

    private static final String STORED_PROC_NAME = "am_allmetric_mngr.raw_load_to_allmetric";

    public RawMetricLoadToAllmetric(DataSource ds) {
        super(ds, STORED_PROC_NAME);

//        declareParameter(new SqlParameter("p_product_code", Types.VARCHAR));
//        declareParameter(new SqlOutParameter("p_policy_number", Types.VARCHAR));
//        declareParameter(new SqlParameter("p_link_type", Types.VARCHAR));
//        declareParameter(new SqlParameter("p_agent_code", Types.VARCHAR));
//        declareParameter(new SqlParameter("p_auth_number", Types.DECIMAL));
        compile();
    }

//    public String execute(String productCode, String policyNumber,
//            String linkType, String partnerCode, BigDecimal authorization) {
//        Map inParams = new HashMap(5);
//        inParams.put("p_product_code", productCode);
//        inParams.put("p_policy_number", policyNumber);
//        inParams.put("p_link_type", linkType);
//        inParams.put("p_agent_code", partnerCode);
//        inParams.put("p_auth_number", authorization);
//
//        Map outParams = execute(inParams);
//        if (outParams.size() > 0) {
//            return outParams.get("p_policy_number").toString();
//        } else {
//            return null;
//        }
//    }
    public void executeProc() {
        Map inParams = new HashMap();
        execute(inParams);
    }
    
}