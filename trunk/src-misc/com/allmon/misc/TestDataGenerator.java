package com.allmon.misc;


public class TestDataGenerator {

    public static void main(String[] args) {
    	TestDataGenerator generator = new TestDataGenerator();
    	generator.genDataForActionClasses();
    }

    private void genDataForActionClasses() {
        int startDay = 1;
        int days = 7; // whole week
        int classCount = 1000; 
        int maxCallsPerHour= 30;
        
        //
        System.out.println("-- begin generate dimensions");
        for (int dateTime = startDay; dateTime <= days; dateTime++) {
            for (int hour = 0; hour < 24; hour++) {
                String in = "INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), '2008-01-"+dateTime+"-"+hour+"');";
                System.out.println(in);
            }
        }
        System.out.println("COMMIT;");
        for (int actionClass = 1; actionClass <= classCount; actionClass++) {
            String in = "INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_"+actionClass+"');";
            System.out.println(in);
        }
        System.out.println("COMMIT;");
        
        //
        System.out.println("-- begin generate values");
        int row = 1;
        for (int dateTime = startDay; dateTime <= days; dateTime++) {
            for (int hour = 0; hour < 24; hour++) {
                String date = "2008-01-" + dateTime + "-" + hour; //2008-01-2-8
                for (int actionClass = 1; actionClass <= classCount; actionClass++) {
                    String actionClassName = "CLASS_" + actionClass; //CLASS_43
                    int callsInHour = (int)(Math.random() * maxCallsPerHour);
                    for (int i = 0; i < callsInHour; i++) {
                        // add this point to the space only in 80% of cases
                        if (Math.random() < 0.8) {
                            String execTime = Double.toString(Math.random() * 10000);
                            
                            //System.out.println("-- row: " +row + " / " + i + " " + date + " " + actionClassName + " " + execTime);
                            
                            String in1 = "INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = '"+date+"'), "+row+");";
                            String in2 = "INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = '"+actionClassName+"'), "+row+");";
                            String in3 = "INSERT INTO fc_valuesmsr(fc_vlm_id, fc_msr_id, rownumber, val) VALUES (fc_vlm_seq.NEXTVAL, (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECTIME'), "+row+", "+execTime+");";
                            
                            System.out.println(in1);
                            System.out.println(in2);
                            System.out.println(in3);
                            
                            row++;
                        }
                    }
                    System.out.println("COMMIT;");
                } 
            }
        } 
        System.out.println("-- Total rows:" + row);
        
        System.out.println("EXIT;");

    }
    
}
