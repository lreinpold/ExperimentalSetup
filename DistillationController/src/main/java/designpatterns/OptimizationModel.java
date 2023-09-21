package designpatterns;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * The Class OptimizationModel.
 * @author Lukas Wagner
 * 
 * Kommentare und Fragen von Lasse
 * -Dynamic System Loss sollte sich aus dem Mittelwert von T(i) und T(i-1) berechnen
 * -Warum kann der System Input 1 kW sein? 
 * -Wir sollten Efficiency und Unit Conversion nicht verwechseln: Stand jetzt wird beim Storage Input eine Efficiency von
 * 		1/mC eingerechnet. Ich würde vorschlagen so zu rechnen, dass die Efficiency 1 ist, und der gesamte Term der Energieflüsse
 * 		in und aus dem Speicher mit 1/mc multipliziert, so wie wir auch beim SOC alles mit 1/SorageCapacity multiplizieren. 
 * - Nebenbedinungen funktionieren teilweise nicht: 
 * 			"resource1.setMaxPowerInput(0.365);" wird nicht richtig angewandt (aus Excel Tabelle ersichtlich) Dadurch erwärmt sich das System zu schnell		
 * 			"double maxPowerSystem = 1; " legt nahe, dass das System mit 1kW erwärmt werden kann --> nicht zutreffend
 * - Die Umrechnung von Energiestrom auf Massestrom (mit dem Faktor 2675.58) würde ich nur einmal ganz am Ende vollziehen.
 */
public class OptimizationModel {
	/** The nolimit. */
	static final double NOLIMIT = 9999;

	/** The Constant INPUT. */
	static final String INPUT = "Input";

	/** The Constant OUTPUT. */
	static final String OUTPUT = "Output";

	/** The Constant SOC. */
	static final String SOC = "SOC";

	/** The Constant POWER. */
	static final String POWER = "Power";

	/** The Constant BINARY. */
	static final String BINARY = "Binary";

	/** The Constant SEGMENT. */
	static final String SEGMENT = "Segment";

	/** The Constant STATE. */
	static final String STATE = "State";

	public static void main(String[] args) throws IloException {
		setOptimizationParameters();
		optimizationModel();
	}

	/**
	 * Sets the optimization parameters, primarily in ArrayList<ResourceParameters> resourceParameters.
	 */
	public static void setOptimizationParameters () {

		designpatterns.DesignPatterns.setOptimalityGap(0.0001); // default 10e-4 = 0.001
		designpatterns.DesignPatterns.setTimeInterval(0.05); // 0.05 = 3Minutes, 0.125 = 7.5 Minutes
		designpatterns.DesignPatterns.setArrayLength(4*6*5); // set arrayLength in # of time steps

		// parameters
		// new object for resource to set parameters
		double ambientTemperature = 23; 

		ResourceParameters resource1 = new  ResourceParameters();
		resource1.setName("mC1");
		resource1.setResourceAsStorage(true);
		resource1.setMaxPowerInput(0.365);
		resource1.setMaxPowerOutput(0.3);

		resource1.setMinimumStorageCapacity(ambientTemperature);
		resource1.setMaximumStorageCapacity(99);
		resource1.setInitalCapacity(99);

		resource1.addSystemStateWithMaxPowerOutput(0, "cold", 0, NOLIMIT, new int[] {1}, 0, 99, 0);
//		resource1.addSystemState(1, "hot", 0, NOLIMIT, new int[] {0}, 99, 99);
		resource1.addSystemStateStorageEqualInputOutput(1, "hot", 0, NOLIMIT, new int[] {0}, 99, 99, true);
		resource1.setNumberOfSystemStates(resource1.getSystemStates().size());
		resource1.setInitialSystemState(1);

		double mC1 = 7.982013540355776; // kJ/K = kWs/K
		double mC1conv = 1/(mC1/3600); // K/kWh
		resource1.setUnitConversionFactorStorage(mC1conv);
		//		resource1.setEfficiencyInputStorage(mC1conv); //Obsolet durch neue Unit conversion
		//		resource1.setEfficiencyOutputStorage(1/(mC1conv/2675.58)); //Obsolet durch neue Unit conversion

		double alpha1A1 = 0.001081973903655; // kW/K 
		resource1.setDynamicEnergyLoss(-alpha1A1);
		resource1.setReferenceDynamicEnergyLoss(ambientTemperature);

		designpatterns.DesignPatterns.getResourceParameters().add(resource1);

		ResourceParameters resource2 = new  ResourceParameters();
		resource2.setName("mC2");
		resource2.setResourceAsStorage(true);
		//		resource2.setMaxPowerInput(-);
		resource2.setMaxPowerOutput(0.267585); // 0.018 kg/0.05 h * 2675.85 kJ/kg/3600 = kW
		resource2.setMinimumStorageCapacity(ambientTemperature);
		resource2.setMaximumStorageCapacity(96);
		resource2.setInitalCapacity(96);

		resource2.addSystemStateWithMaxPowerOutput(0, "cold", 0, NOLIMIT, new int[] {1}, 0, 96, 0);
//		resource2.addSystemState(1, "hot", 0, NOLIMIT, new int[] {0}, 99, 99);
		resource2.addSystemStateStorageEqualInputOutput(1, "hot", 0, NOLIMIT, new int[] {0}, 96, 96, true);
		resource2.setNumberOfSystemStates(resource2.getSystemStates().size());
		resource2.setInitialSystemState(1);

		double mC2 = 0.817058868710792; // kJ/K = kWs/K
		double mC2conv = 1/(mC2/3600); // K/kWh
		resource2.setUnitConversionFactorStorage(mC2conv);
		//		resource2.setEfficiencyInputStorage(mC2conv); //Obsolet durch neue Unit conversion
		//		resource2.setEfficiencyOutputStorage(1/(mC2conv/2675.58)); //Obsolet durch neue Unit conversion

		double alpha2A2 = 0.002873551267486; // kW/K 
		resource2.setDynamicEnergyLoss(-alpha2A2);
		resource2.setReferenceDynamicEnergyLoss(ambientTemperature);

		designpatterns.DesignPatterns.getResourceParameters().add(resource2);


		// set parameters for additional energy resources
	}


	/**
	 * Optimization model
	 *
	 * @throws IloException the ilo exception
	 */
	public static double[][] optimizationModel () throws IloException {
		String nameOfModel = "";
		try {
			//additional parameters for system
			double maxPowerSystem = 0.365; 
			double enthalpy = 2675.58; // kJ/kg Enthalpie von gesättigtem Dampf bei 100 °C
			double outputMassFlow = 0.15; // kg
			double outputTarget = enthalpy*outputMassFlow/3600; // kJ/kg*kg -> kJ -> kJ/3600 = kWh

			//-------------------------------------------------------------------- Create Decision Variables --------------------------------------------------------------------
			designpatterns.DesignPatterns.creationOfDecisionVariables(maxPowerSystem);

			// ------------------------------------------------------------------------ Use of Design Patterns--------------------------------------------------------------------
			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)}, // Decision variables Output Side Dependency
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("mC1", INPUT, POWER)} // Decision variables Input Side dependency
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("mC1", OUTPUT, POWER)},
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("mC2", INPUT, POWER)} 
					);
			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("mC2", OUTPUT, POWER)}, // Decision variables Output Side Dependency
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, POWER)} // Decision variables Input Side dependency
					);

			designpatterns.DesignPatterns.generateEnergyBalanceForStorageSystem("mC1");
			designpatterns.DesignPatterns.generateEnergyBalanceForStorageSystem("mC2");

			designpatterns.DesignPatterns.generateSystemStateSelectionByPowerLimits("mC1");
			designpatterns.DesignPatterns.generateSystemStateSelectionByPowerLimits("mC2");

			IloNumExpr outputSystemSum = designpatterns.DesignPatterns.getCplex().numExpr();
			for (int timeStep = 0; timeStep <designpatterns.DesignPatterns.getArrayLength(); timeStep++) {
				outputSystemSum = designpatterns.DesignPatterns.getCplex().sum(outputSystemSum,	
						designpatterns.DesignPatterns.getCplex().prod(
								designpatterns.DesignPatterns.getTimeInterval(),
								designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, POWER)[timeStep])
						);
			}
			designpatterns.DesignPatterns.getCplex().addGe(outputSystemSum, outputTarget);

			designpatterns.DesignPatterns.getCplex().exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = designpatterns.DesignPatterns.getCplex().linearNumExpr();
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				objective.addTerm(
//						designpatterns.DesignPatterns.getTimeInterval()*designpatterns.DesignPatterns.getElectricityPrice()[i]*0.001, 
						designpatterns.DesignPatterns.getTimeInterval()*DesignPatterns.convertPriceToArbitrayIntervals(DesignPatterns.getElectricityPrice(), 3)[i]*0.001,
						designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)[i]
						);
			System.out.println(DesignPatterns.convertPriceToArbitrayIntervals(DesignPatterns.getElectricityPrice(), 3)[i]);
			}
		
			designpatterns.DesignPatterns.getCplex().addMinimize(objective);

			// solver specific parameters
			//cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			System.out.println("optimalityGap = " + designpatterns.DesignPatterns.getOptimalityGap());
			designpatterns.DesignPatterns.getCplex().setParam(IloCplex.Param.MIP.Tolerances.MIPGap, designpatterns.DesignPatterns.getOptimalityGap());
			long start = System.currentTimeMillis();
			System.out.println("cplex solve");
			if (designpatterns.DesignPatterns.getCplex().solve()) {
				long end = System.currentTimeMillis();
				long solvingTime = 	(end - start);
				System.out.println("obj = "+designpatterns.DesignPatterns.getCplex().getObjValue());
				System.out.println("bb = "+designpatterns.DesignPatterns.getCplex().getBestObjValue());
				System.out.println(designpatterns.DesignPatterns.getCplex().getCplexStatus());

				int sizeOfResultsMatrix = designpatterns.DesignPatterns.getDecisionVariablesMatrix().size()*40+designpatterns.DesignPatterns.getDecisionVariablesVector().size(); 
				double [][] optimizationResults = new double [designpatterns.DesignPatterns.getArrayLength()+1][sizeOfResultsMatrix];
				for (int i = 1; i < designpatterns.DesignPatterns.getArrayLength()+1; i++) {
					int counter = 0; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)[i-1]);
					counter++; 
					System.out.println(designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)[i-1]));
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("mC1", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("mC1", OUTPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("mC1", SOC, POWER)[i-1]);
					counter++; 

					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("mC1",POWER,STATE)[0].length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("mC1",POWER,STATE)[i][j]);
						counter++; 
					}

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("mC2", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("mC2", OUTPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("mC2", SOC, POWER)[i-1]);
					counter++; 

					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("mC2",POWER,STATE)[0].length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("mC2",POWER,STATE)[i][j]);
						counter++; 
					}

				}
				String headerOptimizationResults = "system-input; mc1-input; mc1-output; mc1-soc; mc1-state0; mc1-state1;mc2-input; mc2-output; mc2-soc; mc2-state0; mc2-state1"; 

				designpatterns.DesignPatterns.writeResultsToFile(optimizationResults, "distille"+nameOfModel, headerOptimizationResults);
				return optimizationResults;
			} else {
				System.out.println("Model not solved");
				return null;
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
			return null;
		}
		finally {
			if (designpatterns.DesignPatterns.getCplex()!=null)  {
				designpatterns.DesignPatterns.getCplex().close();
				designpatterns.DesignPatterns.globalCplex=null;
			}
		}
	}
}
