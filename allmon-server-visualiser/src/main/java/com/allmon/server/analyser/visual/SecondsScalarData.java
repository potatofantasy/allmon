package com.allmon.server.analyser.visual;

import java.math.BigDecimal;

public class SecondsScalarData {
	String SEC; //Date SEC;
	BigDecimal COUNT; 
	BigDecimal SUM;
	BigDecimal AVG;
	BigDecimal MIN;
	BigDecimal MAX;
	
	public SecondsScalarData subtract(SecondsScalarData scalar) {
		SecondsScalarData newScalar = new SecondsScalarData();
		newScalar.SEC = SEC;
		newScalar.COUNT = COUNT;
		newScalar.SUM = SUM.subtract(scalar.SUM);
		newScalar.AVG = AVG.subtract(scalar.AVG);
		newScalar.MIN = MIN.subtract(scalar.MIN);
		newScalar.MAX = MAX.subtract(scalar.MAX);
		return newScalar;
	}
	
	@Override
	public String toString() {
		return 
			" SEC:" + SEC + 
			" COUNT:" + COUNT + 
			" SUM:" + SUM + 
			" AVG:" + AVG + 
			" MIN:" + MIN + 
			" MAX:" + MAX;
	}
}
