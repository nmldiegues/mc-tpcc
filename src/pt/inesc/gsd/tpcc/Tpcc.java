package pt.inesc.gsd.tpcc;

import java.util.Map;

public class Tpcc {

	// Order of inputs:
	// warehouses
	// paymentWeight
	// orderStatusWeight
	// numberThreads
    	// parallelNestedSiblings (0 = disabled, > 0 enabled per thread in numberThreads)
	// simulationTime
	public static void main(String[] args) {
		TpccPopulationStage populationStage = new TpccPopulationStage();
		populationStage.setNumWarehouses(Integer.parseInt(args[0]));
		populationStage.populate();
		
		TpccBenchmarkStage benchmarkStage = new TpccBenchmarkStage();
		benchmarkStage.setPaymentWeight(Integer.parseInt(args[1]));
		benchmarkStage.setOrderStatusWeight(Integer.parseInt(args[2]));
		benchmarkStage.setNumOfThreads(Integer.parseInt(args[3]));
		benchmarkStage.setPerThreadSimulTime(Long.parseLong(args[4]));
		Map<String, String> results = benchmarkStage.executeOnSlave();
		for (Map.Entry<String, String> pair : results.entrySet()) {
			System.out.println(pair.getKey() + " " + pair.getValue());
		}
	}
	
}
