package designpatterns;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ilog.concert.*;
import ilog.cplex.*;

/**
 * The Class DesignPatterns.
 *
 * @author Lukas Wagner, lukas.wagner@hsu-hh.de
 * Code for Design Patterns for Energy Optimization.
 * Conference paper "Design Patterns for Optimization Models of Flexible Energy Resources", ONCON 2023
 */
public class DesignPatterns {

	/** The global cplex. */
	static	IloCplex globalCplex = null; 

	/** The solving times. */
	static double[] solvingTimes = new double [4]; 

	/** The resource parameters. */
	static List<ResourceParameters> resourceParameters = new ArrayList<>();

	/** The array length. */
	static int arrayLength; 

	/** The time interval = length of time step. */
	static double timeInterval; 

	/** The optimality gap. */
	static double optimalityGap = 0.0001; // default 10e-4

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

	/** The hashmap for decision variables vector. */
	static HashMap<String, IloNumVar[]> decisionVariablesVector = new HashMap<>(); 

	/** The hashmap decision variables matrix. */
	static HashMap<String, IloNumVar[][]> decisionVariablesMatrix = new HashMap<>(); 
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IloException the ilo exception
	 */
	public static void main(String[] args) throws IloException {
		setOptimizationParameters();
		//		optimizationModelWithMethods();
	}

	/**
	 * Sets the optimization parameters, primarily in ArrayList<ResourceParameters> resourceParameters.
	 */
	public static void setOptimizationParameters () {
		setOptimalityGap(0.01); // default 10e-4
		setTimeInterval(0.25); // 0.05 = 3Minutes, 0.125 = 7.5 Minutes
		setArrayLength(2); // set arrayLength in # of time steps

		double maxPowerEl = 52.25; // MW

		// parameters
		// new object for resource to set parameters
		ResourceParameters resource1 = new  ResourceParameters();
		resource1.setName("Electrolyzer1");
		resource1.setEnergyCarrier(POWER);
		resource1.setMinPowerInput(0);
		resource1.setMinPowerOutput(0);
		resource1.setMaxPowerInput(maxPowerEl);
		resource1.setMaxPowerOutput(100000);

		resource1.createPlaList(0,0,0,7.84); // kg/h
		resource1.createPlaList(21.997,-26.36,7.84,10.6091); // kg/h
		resource1.createPlaList(20.754,-13.173,10.6091,13.31882);
		resource1.createPlaList(19.782,-0.23309,13.31882,16.59741);
		resource1.createPlaList(18.787,16.289,16.59741,20.78419);
		resource1.createPlaList(17.864,35.465,20.78419,25.39345);
		resource1.createPlaList(17.132,54.051,25.39345,29.70148);
		resource1.createPlaList(16.609,69.603,29.70148,33.85188);
		resource1.createPlaList(16.058,88.24,33.85188,39.48562);
		resource1.createPlaList(15.622,105.45,39.48562,42.91256);
		resource1.createPlaList(15.308,118.93,42.91256,46.914);
		resource1.createPlaList(14.946,135.92,46.914,maxPowerEl);
		resource1.setNumberOfLinearSegments(resource1.getPla().size());

		resource1.addSystemStateWithMaxPowerOutput(0, "off", 4, NOLIMIT, new int[] {1}, 0, 0, 0);
		resource1.addSystemStateWithMaxPowerOutput(1, "start-up", 2, 2, new int[] {2}, 0, 7.84, 0);
		resource1.addSystemState(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7, maxPowerEl);
		resource1.addSystemStateWithMaxPowerOutput(3, "stand-by", 0, 10, new int[] {2,4}, 0.52,0.52, 0);
		resource1.addSystemStateWithMaxPowerOutput(4, "shut down", 2, 2, new int[] {0}, 0, 7, 0);
		resource1.setNumberOfSystemStates(resource1.getSystemStates().size());
		// add object to Array List
		resourceParameters.add(resource1);		

		ResourceParameters resource2 = new ResourceParameters();
		resource2.setName("Electrolyzer2");
		resource2.setEnergyCarrier(POWER);
		resource2.setMinPowerInput(0);
		resource2.setMinPowerOutput(0);
		resource2.setMaxPowerInput(maxPowerEl);
		resource2.setMaxPowerOutput(1000000);

		resource2.createPlaList(0,0,0,7.84); // kg/h
		resource2.createPlaList(21.997,-26.36,7.84,10.6091); // kg/h
		resource2.createPlaList(20.754,-13.173,10.6091,13.31882);
		resource2.createPlaList(19.782,-0.23309,13.31882,16.59741);
		resource2.createPlaList(18.787,16.289,16.59741,20.78419);
		resource2.createPlaList(17.864,35.465,20.78419,25.39345);
		resource2.createPlaList(17.132,54.051,25.39345,29.70148);
		resource2.createPlaList(16.609,69.603,29.70148,33.85188);
		resource2.createPlaList(16.058,88.24,33.85188,39.48562);
		resource2.createPlaList(15.622,105.45,39.48562,42.91256);
		resource2.createPlaList(15.308,118.93,42.91256,46.914);
		resource2.createPlaList(14.946,135.92,46.914,50);

		resource2.setNumberOfLinearSegments(resource2.getPla().size());

		resource2.addSystemStateWithMaxPowerOutput(0, "off", 4, NOLIMIT, new int[] {1}, 0, 0, 0);
		resource2.addSystemStateWithMaxPowerOutput(1, "start-up", 2, 2, new int[] {2}, 0, 7.84, 0);
		resource2.addSystemState(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7, maxPowerEl);
		resource2.addSystemStateWithMaxPowerOutput(3, "stand-by", 0, 10, new int[] {2,4}, 0.52, 0.52, 0);
		resource2.addSystemStateWithMaxPowerOutput(4, "shut down", 2, 2, new int[] {0}, 0, 7, 0);
		resource2.setNumberOfSystemStates(resource2.getSystemStates().size());
		resourceParameters.add(resource2);

		ResourceParameters resource3 = new  ResourceParameters();
		resource3.setName("Storage");
		resource3.setEnergyCarrier(POWER);
		resource3.setMinPowerInput(0);
		resource3.setMaxPowerInput(20000);
		resource3.setMaxPowerOutput(100000);
		resource3.setResourceAsStorage(true);
		resource3.setEfficiencyInputStorage(1);
		resource3.setEfficiencyOutputStorage(1);
		resource3.setInitalCapacity(0);
		resource3.setMaximumStorageCapacity(5000);
		//		resource3.setStaticEnergyLoss(0);
		//		resource3.setDynamicEnergyLoss(0);
		//		resource3.setReferenceDynamicEnergyLoss(resource3.maximumStorageCapacity);
		resourceParameters.add(resource3);
	}



	/**
	 * Creation of decision variables from ResourceParameters.
	 * saves created decision variables to 2 hashmaps, depending on #2 of colums (1 -> vector, 2-> matrix)
	 *
	 * @param maxPowerSystem the max power system
	 * @throws IloException the ilo exception
	 */
	public static void creationOfDecisionVariables (double maxPowerSystem) throws IloException {
		IloNumVar[] powerInput = getCplex().numVarArray(getArrayLength(),  0 ,  maxPowerSystem);
		getDecisionVariablesVector().put("System"+"-"+INPUT+"-"+POWER, powerInput);

		IloNumVar[] powerOutputSystem = getCplex().numVarArray(getArrayLength(), 0, Double.MAX_VALUE);
		getDecisionVariablesVector().put("System"+"-"+OUTPUT+"-"+POWER, powerOutputSystem);

		List<String> converterList = new ArrayList<String>();
		List<String> storageList = new ArrayList<String>();
		// extract names of list 
		for (int i = 0; i < getResourceParameters().size(); i++) {
			ResourceParameters resource = resourceParameters.get(i);
			if (resource.isStorage()==false) {
				// get name of resource
				converterList.add(resource.getName());
			} else {
				// get name of storage
				storageList.add(resource.getName());

			}
		}

		String nameOfResource; 
		for (int resource = 0; resource < converterList.size(); resource++) {
			nameOfResource = converterList.get(resource);			

			int indexOfResource = -1;
			indexOfResource = findIndexByName(nameOfResource);
			if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");

			IloNumVar[] powerInputResource = getCplex().numVarArray(
					getArrayLength(), 
					resourceParameters.get(indexOfResource).getMinPowerInput(), 
					resourceParameters.get(indexOfResource).getMaxPowerInput()
					);
			getDecisionVariablesVector().put(nameOfResource+"-"+INPUT+"-"+POWER, powerInputResource);
			//			System.out.println("Input variable "+ nameOfResource);

			IloNumVar[] powerOutputResource = getCplex().numVarArray(
					getArrayLength(), 
					resourceParameters.get(indexOfResource).getMinPowerOutput(), 
					resourceParameters.get(indexOfResource).getMaxPowerOutput()
					);
			getDecisionVariablesVector().put(nameOfResource+"-"+OUTPUT+"-"+POWER, powerOutputResource);
			//		System.out.println("Output variable "+ nameOfResource);

			if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()!=0) {
				// only create decision variable, if necessary
				IloIntVar[][] binariesPlaResource = new IloIntVar[resourceParameters.get(indexOfResource).getNumberOfLinearSegments()][getArrayLength()]; 
				for (int plaSegment = 0; plaSegment < resourceParameters.get(indexOfResource).getNumberOfLinearSegments(); plaSegment++) {
					binariesPlaResource[plaSegment] = getCplex().intVarArray(getArrayLength(), 0 , 1);
				}
				getDecisionVariablesMatrix().put(nameOfResource+"-"+POWER+"-"+BINARY, binariesPlaResource);
				//				System.out.println("binary power variable "+ nameOfResource);

				IloNumVar[][] powerInputLinearSegmentsResource = new IloNumVar[resourceParameters.get(indexOfResource).getNumberOfLinearSegments()][getArrayLength()]; 
				for (int plaSegment = 0; plaSegment < resourceParameters.get(indexOfResource).getNumberOfLinearSegments(); plaSegment++) {
					powerInputLinearSegmentsResource[plaSegment] = getCplex().numVarArray(
							getArrayLength(),
							resourceParameters.get(indexOfResource).getMinPowerInput(), 
							resourceParameters.get(indexOfResource).getMaxPowerInput()
							);
				}
				getDecisionVariablesMatrix().put(nameOfResource+"-"+POWER+"-"+SEGMENT, powerInputLinearSegmentsResource);
				//				System.out.println("power segment variable "+ nameOfResource);
			}

			if (resourceParameters.get(indexOfResource).getNumberOfSystemStates()!=0) {
				// only create decision variable, if necessary
				IloIntVar[][] statesIntArrayResource = new IloIntVar[getArrayLength()+1][resourceParameters.get(indexOfResource).getNumberOfSystemStates()];
				for (int timeStep = 0; timeStep < getArrayLength()+1; timeStep++) {
					for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
						statesIntArrayResource[timeStep][state] = getCplex().intVar(0, 1);
					}
				}
				getDecisionVariablesMatrix().put(nameOfResource+"-"+POWER+"-"+STATE, statesIntArrayResource);
				//				System.out.println("state variable "+ nameOfResource);
			}
			System.out.println("Created variables for "+ nameOfResource);
		}

		String nameOfStorage = ""; 
		// ---------------------RESOURCE  - Storage---------------------
		for (int storage = 0; storage < storageList.size(); storage++) {
			nameOfStorage = storageList.get(storage);		

			int indexOfStorage = -1;
			indexOfStorage = findIndexByName(nameOfStorage);
			if (indexOfStorage==-1) System.err.println("Storage System not found in list of resourceParameters!");

			// check if resource is actually storage!
			if (resourceParameters.get(indexOfStorage).isStorage()==true) {
				IloNumVar[] powerInputStorage = getCplex().numVarArray(
						getArrayLength(), 
						resourceParameters.get(indexOfStorage).getMinPowerInput(), 
						resourceParameters.get(indexOfStorage).getMaxPowerInput()
						);
				getDecisionVariablesVector().put(nameOfStorage+"-"+INPUT+"-"+POWER, powerInputStorage);
				//				System.out.println("power input variable "+ nameOfStorage);
				IloNumVar[] stateOfCharge = getCplex().numVarArray(
						getArrayLength()+1, 
						resourceParameters.get(indexOfStorage).getMinimumStorageCapacity(), 
						resourceParameters.get(indexOfStorage).getMaximumStorageCapacity()
						);
				getDecisionVariablesVector().put(nameOfStorage+"-"+SOC+"-"+POWER, stateOfCharge);
				//				System.out.println("soc variable "+ nameOfStorage);
				IloNumVar[] powerOutputStorage = getCplex().numVarArray(
						getArrayLength(), 
						resourceParameters.get(indexOfStorage).getMinPowerOutput(), 
						resourceParameters.get(indexOfStorage).getMaxPowerOutput()
						);
				getDecisionVariablesVector().put(nameOfStorage+"-"+OUTPUT+"-"+POWER, powerOutputStorage);
				//				System.out.println("power output variable "+ nameOfStorage);


				if (resourceParameters.get(indexOfStorage).getNumberOfSystemStates()!=0) {
					// only create decision variable, if necessary
					IloIntVar[][] statesIntArrayResource = new IloIntVar[getArrayLength()+1][resourceParameters.get(indexOfStorage).getNumberOfSystemStates()];
					for (int timeStep = 0; timeStep < getArrayLength()+1; timeStep++) {
						for (int state = 0; state < resourceParameters.get(indexOfStorage).getNumberOfSystemStates(); state++) {
							statesIntArrayResource[timeStep][state] = getCplex().intVar(0, 1);
						}
					}
					getDecisionVariablesMatrix().put(nameOfStorage+"-"+POWER+"-"+STATE, statesIntArrayResource);
					//				System.out.println("state variable "+ nameOfResource);
				}
			}
			System.out.println("Created variables for "+ nameOfStorage);
		}
	}

	/**
	 * Optimization model with methods.
	 *
	 * @throws IloException the ilo exception
	 */
	public static void optimizationModelWithMethods() throws IloException {
		try {
			//additional parameters for system
			double maxPowerSystem = 100; 
			double constantHydrogenDemand = 1000;

			//-------------------------------------------------------------------- Decision Variables --------------------------------------------------------------------
			creationOfDecisionVariables(maxPowerSystem);

			// ------------------------------------------------------------------------ CONSTRAINTS--------------------------------------------------------------------

			for (int i = 0; i < getArrayLength(); i++) {
				getCplex().addEq(getDecisionVariableFromVector("System", OUTPUT, POWER)[i], constantHydrogenDemand);
			}

			// ------------------------------------------------------------------------ Use of Design Patterns--------------------------------------------------------------------
			generateCorrelativeDependency(
					new IloNumVar[][] {getDecisionVariableFromVector("System", INPUT, POWER)}, 
					new IloNumVar[][] {
						getDecisionVariableFromVector("Electrolyzer1", INPUT, POWER), 
						getDecisionVariableFromVector("Electrolyzer2", INPUT, POWER)
					}
					);

			generateCorrelativeDependency(
					new IloNumVar[][] {
						getDecisionVariableFromVector("Electrolyzer1", OUTPUT, POWER), 
						getDecisionVariableFromVector("Electrolyzer2", OUTPUT, POWER)
					}, 
					new IloNumVar[][] {
						getDecisionVariableFromVector("Storage", INPUT, POWER)
					}
					);

			/**
			IloIntVar[][][] restrictiveDepElectrolyzersStorage = 	generateRestrictiveDependency(
					new IloNumVar[][] {
						getDecisionVariableFromVector("Electrolyzer1", OUTPUT, POWER), 
						getDecisionVariableFromVector("Electrolyzer2", OUTPUT, POWER),
						//getDecisionVariableFromVector("Electrolyzer3", OUTPUT, POWER)
					}, 
					new IloNumVar[][] {
						getDecisionVariableFromVector("Storage", INPUT, POWER)
					}
					);
			 */

			generateCorrelativeDependency(
					new IloNumVar[][] {getDecisionVariableFromVector("Storage", OUTPUT, POWER)}, 
					new IloNumVar[][] {getDecisionVariableFromVector("System", OUTPUT, POWER)}
					);

			generateInputOutputRelationship("Electrolyzer1");
			generateSystemStateSelectionByPowerLimits("Electrolyzer1");
			generateStateSequencesAndHoldingDuration("Electrolyzer1");
			generateRampLimits("Electrolyzer1", INPUT);

			generateInputOutputRelationship("Electrolyzer2");
			generateSystemStateSelectionByPowerLimits("Electrolyzer2");
			generateStateSequencesAndHoldingDuration("Electrolyzer2");
			generateRampLimits("Electrolyzer2", INPUT);

			generateEnergyBalanceForStorageSystem("Storage");

			//	System.out.println(cplex);
			getCplex().exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = getCplex().linearNumExpr();
			for (int i = 0; i < getArrayLength(); i++) {
				objective.addTerm(timeInterval*getElectricityPrice()[i], getDecisionVariableFromVector("System", INPUT, POWER)[i]);
				objective.addTerm(2000, getDecisionVariableFromMatrix("Electrolyzer1", POWER, STATE)[i][1]);
				objective.addTerm(2000, getDecisionVariableFromMatrix("Electrolyzer2", POWER, STATE)[i][1]);
			}
			getCplex().addMinimize(objective);

			// solver specific parameters
			//cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			getCplex().setParam(IloCplex.Param.MIP.Tolerances.MIPGap, getOptimalityGap());
			//			long start = System.currentTimeMillis();
			System.out.println("cplex solve");
			if (getCplex().solve()) {
				//				long end = System.currentTimeMillis();
				//				long solvingTime = 	(end - start);
				System.out.println("obj = "+getCplex().getObjValue());
				System.out.println(getCplex().getCplexStatus());


				int sizeOfResultsMatrix = getDecisionVariablesMatrix().size()*40+getDecisionVariablesVector().size(); 

				double [][] optimizationResults = new double [getArrayLength()+1][sizeOfResultsMatrix];

				for (int i = 1; i < getArrayLength()+1; i++) {
					int counter = 0; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("System", INPUT, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Electrolyzer1", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Electrolyzer1", OUTPUT, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Electrolyzer2", INPUT, POWER)[i-1]);
					counter++;
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Electrolyzer2", OUTPUT, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Storage", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Storage", OUTPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Storage", SOC, POWER)[i-1]);
					counter++; 

					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT).length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY).length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY)[j][i-1]);
						counter++; 
					}


					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer1",POWER,STATE)[i-1][j]);
						counter++; 
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT).length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY).length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[i-1][j]);
						counter++; 
					}

					//					for (int j = 0; j < restrictiveDepElectrolyzersStorage.length; j++) {
					//						optimizationResults[i][counter] = getCplex().getValue(restrictiveDepElectrolyzersStorage[0][i-1][0]);
					//						counter++; 
					//						optimizationResults[i][counter] = getCplex().getValue(restrictiveDepElectrolyzersStorage[0][i-1][1]);
					//						counter++; 
					//						optimizationResults[i][counter] = getCplex().getValue(restrictiveDepElectrolyzersStorage[1][i-1][0]);
					//						counter++; 
					//					}

				}

				String headerOptimizationResults = null; 
				headerOptimizationResults ="System-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-SOC";

				for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-Binary-"+Integer.toString(j);
				}
				for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-State-"+Integer.toString(j);
				}
				for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-BINARY-"+Integer.toString(j);
				}
				for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-STATE-"+Integer.toString(j);
				}
				//				headerOptimizationResults = headerOptimizationResults + ";binaryOutput1;binaryOutput2;binaryInput";
				writeResultsToFile(optimizationResults, "designpatterns_from-method", headerOptimizationResults);
			} else {
				System.out.println("Model not solved");
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
		}
	}


	/**
	 * Find index by name in list resourceParameters.
	 *
	 * @param nameToFind the name to find
	 * @return the index
	 */
	public static int findIndexByName(String nameToFind) {
		for (int i = 0; i < resourceParameters.size(); i++) {
			ResourceParameters resource = resourceParameters.get(i);
			if (resource.getName().equals(nameToFind)) {
				return i;
			}
		}
		return -1; // Return -1 if name not found
	}

	/**
	 * Gets the decision variable from vector.
	 *
	 * @param name the name
	 * @param port the port
	 * @param medium the medium
	 * @return the decision variable from vector
	 */
	public static IloNumVar[] getDecisionVariableFromVector(String name, String port, String medium) {
		return getDecisionVariablesVector().get(name+"-"+port+"-"+medium);
	}

	/**
	 * Gets the decision variable from matrix.
	 *
	 * @param name the name
	 * @param medium the medium
	 * @param type the type
	 * @return the decision variable from matrix
	 */
	public static IloNumVar[][] getDecisionVariableFromMatrix(String name, String medium, String type) {
		return getDecisionVariablesMatrix().get(name+"-"+medium+"-"+type);
	}
	/**
	 * Generate input output relationship.
	 *
	 * @param nameOfResource the name of resource
	 * @param powerInputResource decision variable the power input resource
	 * @param powerOutputResource decision variable  the power output resource
	 * @param binariesPlaResource decision variable  the binaries pla resource
	 * @param powerInputResourceLinearSegments decision variable  the power input resource linear segments
	 * @throws IloException the ilo exception
	 */
	public static void generateInputOutputRelationship (String nameOfResource) throws IloException {//, IloNumVar[] powerInputResource, IloNumVar[] powerOutputResource, IloIntVar[][] binariesPlaResource, IloNumVar[][] powerInputResourceLinearSegments) throws IloException {
		// Pattern input output
		// find id by name
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");


		// get decision variables from vector/matrix
		IloNumVar[] powerInputResource = getDecisionVariableFromVector(nameOfResource, INPUT, POWER);
		IloNumVar[] powerOutputResource = getDecisionVariableFromVector(nameOfResource, OUTPUT, POWER);
		IloIntVar[][] binariesPlaResource  = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, BINARY);
		IloNumVar[][] powerInputResourceLinearSegments = getDecisionVariableFromMatrix(nameOfResource, POWER, SEGMENT);

		if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()==0
				&& resourceParameters.get(indexOfResource).getEfficiency()!=0
				) {
			// Case 1: efficiency
			System.out.println("Input-Output Relationship by Efficiency for "+ nameOfResource);
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				getCplex().addEq(powerOutputResource[timestep], 
						getCplex().prod(powerInputResource[timestep], resourceParameters.get(indexOfResource).getEfficiency())
						);
			} 
		} else if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()==0
				&& resourceParameters.get(indexOfResource).getEfficiency()==0
				&& resourceParameters.get(indexOfResource).getSlope()!=0) {
			// y = ax+b
			System.out.println("Input-Output Relationship y = ax+b for "+ nameOfResource);
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				getCplex().addEq(powerOutputResource[timestep], 
						getCplex().sum(
								resourceParameters.get(indexOfResource).getIntercept(),
								getCplex().prod(powerInputResource[timestep], resourceParameters.get(indexOfResource).getSlope())
								)
						);
			}
		} else if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()>0) {
			// Case 3: pla
			System.out.println("Input-Output Relationship piecewise linear approximation for "+ nameOfResource);
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				// sum binaries[i] = 1 part 1
				IloNumExpr binarySum = getCplex().numExpr();
				IloNumExpr powerInputSum = getCplex().numExpr();

				for (int plaSegment = 0; plaSegment < resourceParameters.get(indexOfResource).getNumberOfLinearSegments(); plaSegment++) {

					// lowerBound * power <= binary
					getCplex().addLe(
							getCplex().prod(
									resourceParameters.get(indexOfResource).getPla().get(plaSegment).getLowerBound(), 
									binariesPlaResource[plaSegment][timestep]
									), 
							powerInputResourceLinearSegments[plaSegment][timestep]
							);

					//power <= upperBound * binary
					getCplex().addLe(
							powerInputResourceLinearSegments[plaSegment][timestep], 
							getCplex().prod(
									resourceParameters.get(indexOfResource).getPla().get(plaSegment).getUpperBound(), 
									binariesPlaResource[plaSegment][timestep]
									)
							);

					// sum binaries[i] = 1 part 2
					binarySum = getCplex().sum(binarySum,(IloIntVar) getDecisionVariableFromMatrix(nameOfResource, POWER, BINARY)[plaSegment][timestep]);
					//					binarySum = getCplex().sum(binarySum,binariesPlaResource[plaSegment][timestep]);
					powerInputSum = getCplex().sum(powerInputSum, powerInputResourceLinearSegments[plaSegment][timestep]);
				}

				// sum binaries[i] = 1 part 3           
				getCplex().addEq(binarySum, 1);

				// sum powerInputElectrolyzer1LinearSegments[i] = powerOutputElectrolyzer1[i] part 3
				getCplex().addEq(powerInputSum, powerInputResource[timestep]);

				for (int plasegment = 0; plasegment < resourceParameters.get(indexOfResource).getNumberOfLinearSegments(); plasegment++) {
					getCplex().add(
							getCplex().ifThen(
									//									getCplex().and(
									//											getCplex().le(binariesPlaResource[plasegment][timestep], 1),
									//											getCplex().ge(binariesPlaResource[plasegment][timestep], 0.9)
									//											),
									getCplex().eq(binariesPlaResource[plasegment][timestep], 1),
									getCplex().eq(powerOutputResource[timestep],
											getCplex().sum(
													resourceParameters.get(indexOfResource).getPla().get(plasegment).getIntercept(),
													getCplex().prod(
															//powerInputSum,
															powerInputResource[timestep],
															//powerInputResourceLinearSegments[plasegment][timestep],
															resourceParameters.get(indexOfResource).getPla().get(plasegment).getSlope()
															)
													)
											)
									)
							);
				}
			}
		}
		System.out.println("Input-output successfully created for resource " + nameOfResource);
	}

	/**
	 * Generate system state selection by power limits.
	 *
	 * @param nameOfResource the name of resource
	 * @param powerInputResource the power input resource
	 * @param powerOutputResource the power output resource
	 * @param statesIntArrayResource the states int array resource
	 * @throws IloException the ilo exception
	 */
	public static void generateSystemStateSelectionByPowerLimits (String nameOfResource) throws IloException {
		// find id by name
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");

		if (resourceParameters.get(indexOfResource).isStorage()==false) {
			IloNumVar[] powerInputResource = getDecisionVariableFromVector(nameOfResource, INPUT, POWER);
			IloNumVar[] powerOutputResource  = getDecisionVariableFromVector(nameOfResource, OUTPUT, POWER);
			IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, STATE);

			//set initial system state, all other states = 0 
			getCplex().addEq(statesIntArrayResource[0][resourceParameters.get(indexOfResource).getInitialSystemState()], 1);
			//		getCplex().addEq(statesIntArrayResource[0][1], 1);
			getCplex().addEq(getCplex().sum(statesIntArrayResource[0]), 1);

			for (int timestep = 1; timestep < getArrayLength()+1; timestep++) {
				getCplex().addEq(getCplex().sum(statesIntArrayResource[timestep]), 1);

				IloNumExpr powerMinSum = getCplex().numExpr();
				IloNumExpr powerMaxSum = getCplex().numExpr();
				IloNumExpr powerOutputMaxSum = getCplex().numExpr();
				for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
					powerMinSum = getCplex().sum(
							powerMinSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinPower()
									)
							);
					powerMaxSum = getCplex().sum(
							powerMaxSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxPower()
									)
							);
					powerOutputMaxSum = getCplex().sum(
							powerOutputMaxSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxPowerOutput()
									)
							);
				}
				getCplex().addGe(powerInputResource[timestep-1], powerMinSum);
				getCplex().addLe(powerInputResource[timestep-1], powerMaxSum);
				getCplex().addLe(powerOutputResource[timestep-1], powerOutputMaxSum);
			}
			System.out.println("System states created for "+ nameOfResource);
		}
		else {
			// Create system states for storage by SOC

			IloNumVar[] powerInputResource = getDecisionVariableFromVector(nameOfResource, INPUT, POWER);
			IloNumVar[] powerOutputResource  = getDecisionVariableFromVector(nameOfResource, OUTPUT, POWER);
			IloNumVar[] stateOfChargeResource  = getDecisionVariableFromVector(nameOfResource, SOC, POWER);
			IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, STATE);

			//set initial system state, all other states = 0 
			getCplex().addEq(statesIntArrayResource[0][resourceParameters.get(indexOfResource).getInitialSystemState()], 1);
			//		getCplex().addEq(statesIntArrayResource[0][1], 1);
			getCplex().addEq(getCplex().sum(statesIntArrayResource[0]), 1);

			for (int timestep = 1; timestep < getArrayLength()+1; timestep++) {
				getCplex().addEq(getCplex().sum(statesIntArrayResource[timestep]), 1);

				IloNumExpr socMinSum = getCplex().numExpr();
				IloNumExpr socMaxSum = getCplex().numExpr();
				IloNumExpr powerOutputMaxSum = getCplex().numExpr();
				for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
					socMinSum = getCplex().sum(
							socMinSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinPower()
									)
							);
					socMaxSum = getCplex().sum(
							socMaxSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxPower()
									)
							);
					powerOutputMaxSum = getCplex().sum(
							powerOutputMaxSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxPowerOutput()
									)
							);
				}
				getCplex().addGe(stateOfChargeResource[timestep-1], socMinSum);
				getCplex().addLe(stateOfChargeResource[timestep-1], socMaxSum);
				getCplex().addLe(powerOutputResource[timestep-1], powerOutputMaxSum);
			}
			System.out.println("System states (storage) created for "+ nameOfResource);
		}
	}


	/**
	 * Generate ramp limits.
	 *
	 * @param nameOfResource the name of resource
	 * @param port the side (INPUT XOR OUTPUT)
	 * @param relevantPowerFlowResource the power input resource
	 * @param statesIntArrayResource the states int array resource
	 * @throws IloException the ilo exception
	 */
	public static void generateRampLimits (String nameOfResource, String port) throws IloException {
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");


		IloNumVar[] relevantPowerFlowResource = getDecisionVariableFromVector(nameOfResource, port, POWER); 
		IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, STATE);

		if (port == INPUT) {
			// --------------------- ramp limits ----------------------------------
			for (int timestep = 1; timestep < getArrayLength(); timestep++) {
				//				TODO was ist für das erste Intervall? -> pI[0] <= rampmax, pi[0]>=rampmin
				IloNumExpr sumMinRamp = getCplex().numExpr();
				IloNumExpr sumMaxRamp = getCplex().numExpr();
				IloNumExpr powerDifferenceEl1 = getCplex().numExpr();

				powerDifferenceEl1 = getCplex().diff(relevantPowerFlowResource[timestep], relevantPowerFlowResource[timestep-1]);

				for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
					sumMinRamp = getCplex().sum(sumMinRamp, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinRampInput()));
					sumMaxRamp = getCplex().sum(sumMaxRamp, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxRampInput()));
				}

				getCplex().addGe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMinRamp));
				getCplex().addLe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMaxRamp));
			}
		} else if (port == OUTPUT) {
			for (int timestep = 1; timestep < getArrayLength(); timestep++) {
				//				TODO was ist für das erste Intervall? -> pI[0] <= rampmax, pi[0]>=rampmin
				IloNumExpr sumMinRamp = getCplex().numExpr();
				IloNumExpr sumMaxRamp = getCplex().numExpr();
				IloNumExpr powerDifferenceEl1 = getCplex().numExpr();

				powerDifferenceEl1 = getCplex().diff(relevantPowerFlowResource[timestep], relevantPowerFlowResource[timestep-1]);

				for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
					sumMinRamp = getCplex().sum(sumMinRamp, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinRampOutput()));
					sumMaxRamp = getCplex().sum(sumMaxRamp, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxRampOutput()));
				}

				getCplex().addGe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMinRamp));
				getCplex().addLe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMaxRamp));
			}
		}
		System.out.println("Ramp limits created for "+ nameOfResource);
	}

	/**
	 * Generate state sequences and holding duration.
	 *
	 * @param nameOfResource the name of resource
	 * @param statesIntArrayResource the states int array resource
	 * @throws IloException the ilo exception
	 */
	public static void generateStateSequencesAndHoldingDuration (String nameOfResource) throws IloException {
		// find id by name
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");
		IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, STATE);

		for (int timestep = 1; timestep < getArrayLength()+1; timestep++) {

			for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
				// min duration
				if (resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinStateDuration() == NOLIMIT) {

				} else {
					IloNumExpr constraintLeftSide = getCplex().diff(statesIntArrayResource[timestep][state],statesIntArrayResource[timestep-1][state]);
					IloNumExpr constraintRightSide = statesIntArrayResource[timestep][state];

					int counter = 1;
					for (int timeInStateCounter = 1; timeInStateCounter < resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinStateDuration(); timeInStateCounter++) {

						try {
							constraintRightSide = getCplex().sum(constraintRightSide,statesIntArrayResource[timestep+timeInStateCounter][state]);
							counter++; 

						} catch (Exception e) {
							// TODO: handle exception
						} 
					}
					getCplex().addLe(getCplex().prod(
							constraintLeftSide, 
							Math.min(counter, 
									resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinStateDuration()
									)
							), 
							constraintRightSide);
				}
				//max Duration
				if (resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxStateDuration() == NOLIMIT) {

				}else{
					IloNumExpr constraintLeftSide = statesIntArrayResource[timestep][state];

					int counter = 1;
					for (int timeInStateCounter = 1; timeInStateCounter < resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxStateDuration()+1; timeInStateCounter++) {

						try {
							constraintLeftSide = getCplex().sum(constraintLeftSide,statesIntArrayResource[timestep+timeInStateCounter][state]);
							counter++; 

						} catch (Exception e) {
							// TODO: handle exception
						} 
					}
					getCplex().addLe(constraintLeftSide, Math.min(counter,resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxStateDuration()));
				}


				//State sequences
				IloNumExpr constraintLeftSide = getCplex().diff(statesIntArrayResource[timestep-1][state],statesIntArrayResource[timestep][state]);
				IloNumExpr constraintRightSide = statesIntArrayResource[timestep][resourceParameters.get(indexOfResource).getSystemStates().get(state).getFollowerStates()[0]];

				for (int followerStatesCounter = 1; followerStatesCounter < resourceParameters.get(indexOfResource).getSystemStates().get(state).getFollowerStates().length; followerStatesCounter++) {
					constraintRightSide = getCplex().sum(constraintRightSide, statesIntArrayResource[timestep][resourceParameters.get(indexOfResource).getSystemStates().get(state).getFollowerStates()[followerStatesCounter]]);
				}
				getCplex().addLe(constraintLeftSide, constraintRightSide);
			}
		}
		System.out.println("State sequences and holding duration created for " + nameOfResource);
	}



	/**
	 * Generate energy balance for storage system.
	 *
	 * @param nameOfResource the name of resource
	 * @param stateOfCharge the state of charge
	 * @param powerInputStorage the power input storage
	 * @param powerOutputStorage the power output storage
	 * @throws IloException the ilo exception
	 */
	public static void generateEnergyBalanceForStorageSystem (String nameOfResource) throws IloException {
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");
		if (resourceParameters.get(indexOfResource).isStorage()==false) System.err.println("System "+ nameOfResource + " is not a storage system!");

		IloNumVar[] stateOfCharge = getDecisionVariableFromVector(nameOfResource, SOC, POWER);
		IloNumVar[] powerInputStorage = getDecisionVariableFromVector(nameOfResource, INPUT, POWER); 
		IloNumVar[] powerOutputStorage = getDecisionVariableFromVector(nameOfResource, OUTPUT, POWER);

		// --------------------- storage and loss ----------------------------------
		for (int timestep = 0; timestep < getArrayLength()+1; timestep++) {
			if(timestep==0) {
				getCplex().addEq(stateOfCharge[timestep], resourceParameters.get(indexOfResource).getInitalCapacity());
			} else {
				getCplex().addEq(stateOfCharge[timestep], 
						getCplex().sum(
								stateOfCharge[timestep-1],
								getCplex().prod(resourceParameters.get(indexOfResource).getUnitConversionFactorStorage(),
										// Hier Unit Conversion einfügen
										getCplex().diff(
												getCplex().prod(powerInputStorage[timestep-1], resourceParameters.get(indexOfResource).getEfficiencyInputStorage()*getTimeInterval()),
												getCplex().sum(
														getCplex().sum(
																getCplex().prod(powerOutputStorage[timestep-1], resourceParameters.get(indexOfResource).getEfficiencyOutputReciprocal()*getTimeInterval()),
																resourceParameters.get(indexOfResource).getStaticEnergyLoss()*getTimeInterval()
																), 
														getCplex().prod(
																resourceParameters.get(indexOfResource).getDynamicEnergyLoss()*getTimeInterval(), 
																getCplex().diff(
																		resourceParameters.get(indexOfResource).getReferenceDynamicEnergyLoss(),
																		getCplex().prod(0.5, 
																				getCplex().sum(														
																						stateOfCharge[timestep], 
																						stateOfCharge[timestep-1]
																						)
																				)
																		)
																)
														)
												)
										)
								)
						);

				if (getResourceParameters().get(indexOfResource).getNumberOfSystemStates()!=0) {
					for (int state = 0; state < getResourceParameters().get(indexOfResource).getNumberOfSystemStates(); state++) {
						if (getResourceParameters().get(indexOfResource).getSystemStates().get(state).isInputIsEqualToOutput()==true) {
							getCplex().add(
									getCplex().ifThen(									
											getCplex().eq(
													getDecisionVariableFromMatrix(nameOfResource, POWER, STATE)[timestep][state], 
													1
													),
											getCplex().eq(
													getCplex().prod(
															powerInputStorage[timestep-1], 
															resourceParameters.get(indexOfResource).getEfficiencyInputStorage()*getTimeInterval()
															),
													getCplex().sum(
															getCplex().sum(
																	getCplex().prod(
																			powerOutputStorage[timestep-1],
																			resourceParameters.get(indexOfResource).getEfficiencyOutputReciprocal()*getTimeInterval()
																			),
																	resourceParameters.get(indexOfResource).getStaticEnergyLoss()*getTimeInterval()
																	), 
															getCplex().prod(
																	resourceParameters.get(indexOfResource).getDynamicEnergyLoss()*getTimeInterval(), 
																	getCplex().diff(
																			resourceParameters.get(indexOfResource).getReferenceDynamicEnergyLoss(),
																			getCplex().prod(0.5, 
																					getCplex().sum(														
																							stateOfCharge[timestep], 
																							stateOfCharge[timestep-1]
																							)
																					)
																			)
																	)
															)
													)
											)
									);
						}
					}
				}
			}
		}
		if (resourceParameters.get(indexOfResource).getCapacityTarget()!=-1) {
			if(resourceParameters.get(indexOfResource).getCapacityTargetComparator()=="Eq") 	getCplex().addEq(stateOfCharge[getArrayLength()], resourceParameters.get(indexOfResource).getCapacityTarget());
			if(resourceParameters.get(indexOfResource).getCapacityTargetComparator()=="Ge") 	getCplex().addGe(stateOfCharge[getArrayLength()], resourceParameters.get(indexOfResource).getCapacityTarget());
			if(resourceParameters.get(indexOfResource).getCapacityTargetComparator()=="Le") 	getCplex().addLe(stateOfCharge[getArrayLength()], resourceParameters.get(indexOfResource).getCapacityTarget());
		}

		System.out.println("Energy balance created for "+ nameOfResource);
	}


	/**
	 * Generate correlative dependency.
	 * sum powerOutput[i] = sum PowerInput[i]
	 *
	 * @param powerOutputs array of all relevant power output arrays (new IloNumVar [][] {})
	 * @param powerInputs array of all relevant power input arrays (new IloNumVar [][] {})
	 * @throws IloException the ilo exception
	 */
	public static void generateCorrelativeDependency (IloNumVar[][] powerOutputs, IloNumVar[][] powerInputs) throws IloException {
		// sum powerOutput[i] = sum PowerInput[i]
		if (powerOutputs.length >0 && powerInputs.length >0) {

			for (int timeStep = 0; timeStep < getArrayLength(); timeStep++) {
				IloNumExpr powerOutputSum = getCplex().numExpr();
				IloNumExpr powerInputSum = getCplex().numExpr();
				for (int outputI = 0; outputI < powerOutputs.length; outputI++) {
					powerOutputSum = getCplex().sum(powerOutputSum, powerOutputs[outputI][timeStep]);
					//					System.out.println("output" + timeStep + " " + outputI + " added");
				}
				for (int inputI = 0; inputI < powerInputs.length; inputI++) {
					powerInputSum = getCplex().sum(powerInputSum, powerInputs[inputI][timeStep]);			
					//					System.out.println("input" + timeStep + " " + inputI + " added");
				}
				getCplex().addEq(powerOutputSum, powerInputSum);
				//				System.out.println(timeStep + "corr dep added");
			}
		} else {
			System.err.println("Input or Output empty");
		}

	}


	/**
	 * Generate restrictive dependency.
	 * only one active member per side, all others == 0
	 *
	 * @param powerOutputs array of all relevant power output arrays (new IloNumVar [][] {})
	 * @param powerInputs array of all relevant power input arrays (new IloNumVar [][] {})
	 * @return the ilo int var[][][] of all binary decision variables created in method
	 * @return  [2 -> powerOutputs, powerInputs][getArrayLength()][number of Elements in powerOutputs/power Inputs, depends on index in first[]	]
	 * @throws IloException the ilo exception
	 */
	public static IloIntVar[][][] generateRestrictiveDependency (IloNumVar[][] powerOutputs, IloNumVar[][] powerInputs) throws IloException {
		// binary variables for dependency
		if (powerOutputs.length >0 && powerInputs.length >0) {

			IloIntVar[][] binaryVariablesOutputSide = new IloIntVar[getArrayLength()][powerOutputs.length];
			IloIntVar[][] binaryVariablesInputSide = new IloIntVar[getArrayLength()][powerInputs.length];

			// [2 -> powerOutputs, powerInputs][getArrayLength()][number of Elements in powerOutputs/power Inputs, depends on index in first[]	]
			IloIntVar[][][] variablesRestDep = new IloIntVar[][][] {binaryVariablesOutputSide, binaryVariablesInputSide};
			for (int timeStep = 0; timeStep < getArrayLength(); timeStep++) {
				IloNumExpr binarySumOutput = getCplex().numExpr();
				IloNumExpr binarySumInput = getCplex().numExpr();

				IloNumVar powerOutputRestrDep = getCplex().numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
				IloNumVar powerInputRestrDep = getCplex().numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
				// any output connected to any other input
				getCplex().addEq(powerOutputRestrDep, powerInputRestrDep);


				//---- OUTPUT ----
				for (int outputI = 0; outputI < powerOutputs.length; outputI++) {
					binaryVariablesOutputSide[timeStep][outputI] = getCplex().intVar(0, 1);
					binarySumOutput = getCplex().sum(binarySumOutput, binaryVariablesOutputSide[timeStep][outputI]);

					// if binary variable =1, then output active
					getCplex().add(
							getCplex().ifThen(
									getCplex().eq(binaryVariablesOutputSide[timeStep][outputI], 1),
									getCplex().eq(powerOutputRestrDep, powerOutputs[outputI][timeStep])
									)
							);
					System.out.println("Output "+outputI + " added to restrictive dependency");
				}

				//---- INPUT ----
				for (int inputI = 0; inputI < powerInputs.length; inputI++) {
					binaryVariablesInputSide[timeStep][inputI] = getCplex().intVar(0, 1);
					binarySumInput = getCplex().sum(binarySumInput, binaryVariablesInputSide[timeStep][inputI]);

					// if binary variable =1, then input active
					getCplex().add(
							getCplex().ifThen(
									getCplex().eq(binaryVariablesInputSide[timeStep][inputI], 1),
									getCplex().eq(powerInputRestrDep, powerInputs[inputI][timeStep])
									)
							);
					System.out.println("Input "+inputI + " added to restrictive dependency");

				}

				// sum time step = 1
				getCplex().addEq(binarySumInput, 1);
				getCplex().addEq(binarySumOutput, 1);
			}
			// return all binary decision variables created in method
			return variablesRestDep; 
		}
		System.err.println("at least one side of rest. dep empty");
		return null; 
	}


	/**
	 * Gets the electricity price by arrayLength.
	 *
	 * @return the electricity price
	 */
	public static double[] getElectricityPrice() {
//		double[] electricityPrice = new double[] {
//				85.87,37.21,32.62,32.27,51.03,63.76,44.84,39.2,58.28,43.46,42.47,47.48, 58.96,42.99,43.27,50.5,
//				80.21,30.64,21.47,35.41,15.67,24.15,43.98,55.36,23.58,42.56,60.2,87.49,22.46,38.95,	31.68,60.19,
//				17.49,37.23,40.51,57.98,46.62,46.74,61.23,71.33,57.2,55.6,59.39,81.35,48.79,58.3,58.01,57.73,71.85,
//				64.5,57.07,57.84,44.25,38.6,65.02,67.76,42.31,62.06,83.36,100.31,57.25,66.51,84.52,95.44,73.57,84.5,
//				85.41,98.72,75.05,98.16,101.62,96.88,127.03,92.24,102.1,79.11,140.98,93.1,91.67,66.07,125.87,89.96,
//				76.41,48.12,100.5,74.96,74.03,64.77,100.75,75.26,87.21,38.04,85.35,60.38,56.9,35.95
//		};
		
		double[] electricityPrice = new double[] { -19.99,-11.59,-10,7.33,-6,-5.01,-5,30,1,10,149.1,190,132.5,69.99,
				-10,-3,-5.5,-5,-14.1,144.1,80,50,75.45,70 } ;

		
		double[] electricityPriceNew = new double[getArrayLength()];


		if (getArrayLength()>electricityPrice.length) {
			for (int j = 0; j < electricityPrice.length; j++) {
				electricityPriceNew[j] = electricityPrice[j];
			}
			for (int k = electricityPrice.length; k<getArrayLength(); k++) {
				electricityPriceNew[k] = electricityPrice[(int) (Math.random()*electricityPrice.length)];
			}
			return electricityPriceNew;
		} else {
			for (int j = 0; j < getArrayLength(); j++) {
				electricityPriceNew[j] = electricityPrice[j];
			}
			return electricityPriceNew; 
		}
	}
	
	public static double[] convertPriceToArbitrayIntervals (double[] price, int newLengthInMinutes) {

		int arrayLengthOld = price.length;
		int multiplier = 15/newLengthInMinutes;
		int arrayLengthNew = multiplier*arrayLengthOld;
		double[] longPrice = new double[arrayLengthNew];

		int counter = 0; 
		for (int i = 0; i < arrayLengthOld; i++) {
			for (int k = 0; k < multiplier; k++) {
				longPrice[counter] = price[i];
				counter++; 
			}
		}
		return longPrice;
	}
	/**
	 * Write results to file.
	 *
	 * @param contentToWrite the content to write
	 * @param fileName the file name
	 * @param header the header
	 */
	public static void writeResultsToFile (double[][] contentToWrite, String fileName, String header) {
		String date = Double.toString(System.currentTimeMillis());
		try {
			//	double currentTime = System.currentTimeMillis(); 
			try {
				FileWriter myWriter = new FileWriter("C:/Users/Wagner/OneDrive - Helmut-Schmidt-Universität/Papers/Oncon_Labortest/optResults/"+fileName+date+".csv");
						myWriter.write("id;"+header);
				myWriter.write("\n");
				for (int i = 0; i < contentToWrite.length; i++) {
					myWriter.write(Double.toString(i).replace(".", ","));
					for(int j = 0; j < contentToWrite[0].length; j++) {
						myWriter.write(";"); // Use semicolon as separator
						//myWriter.write(Double.toString(contentToWrite[i][j]));
						myWriter.write(Double.toString(contentToWrite[i][j]).replace(".", ",")); // Replace decimal point with comma
					}
					myWriter.write("\n");
				}
				myWriter.close();
				System.out.println("Successfully wrote data to the file "+ fileName+".csv.");
			} catch (Exception e) {
				FileWriter myWriter = new FileWriter("C:/Users/Reinpold/Documents/OneDrive - Helmut-Schmidt-Universität/General/Labortest_ONCON/"+fileName+date+".csv");
						myWriter.write("id;"+header);
				myWriter.write("\n");
				for (int i = 0; i < contentToWrite.length; i++) {
					myWriter.write(Double.toString(i).replace(".", ","));
					for(int j = 0; j < contentToWrite[0].length; j++) {
						myWriter.write(";"); // Use semicolon as separator
						//myWriter.write(Double.toString(contentToWrite[i][j]));
						myWriter.write(Double.toString(contentToWrite[i][j]).replace(".", ",")); // Replace decimal point with comma
					}
					myWriter.write("\n");
				}
				myWriter.close();
				System.out.println("Successfully wrote data to the file "+ fileName+".csv.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Gets the cplex.
	 *
	 * @return the cplex
	 */

	public static IloCplex getCplex() {
		if(globalCplex==null) {
			try {
				globalCplex = new IloCplex();
			} catch (IloException e) {
				e.printStackTrace();
			}
		}
		return globalCplex;
	}



	/**
	 * Gets the array length.
	 *
	 * @return the arrayLength
	 */
	public static int getArrayLength() {
		return arrayLength;
	}



	/**
	 * Sets the array length.
	 *
	 * @param arrayLength the arrayLength to set
	 */
	public static void setArrayLength(int arrayLength) {
		DesignPatterns.arrayLength = arrayLength;
	}



	/**
	 * Gets the time interval.
	 *
	 * @return the timeInterval
	 */
	public static double getTimeInterval() {
		return timeInterval;
	}



	/**
	 * Sets the time interval.
	 *
	 * @param timeInterval the timeInterval to set
	 */
	public static void setTimeInterval(double timeInterval) {
		DesignPatterns.timeInterval = timeInterval;
	}

	/**
	 * Gets the optimality gap.
	 *
	 * @return the optimalityGap
	 */
	public static double getOptimalityGap() {
		return optimalityGap;
	}

	/**
	 * Sets the optimality gap.
	 *
	 * @param optimalityGap the optimalityGap to set
	 */
	public static void setOptimalityGap(double optimalityGap) {
		DesignPatterns.optimalityGap = optimalityGap;
	}

	/**
	 * @return the decisionVariables
	 */
	public static HashMap<String, IloNumVar[]> getDecisionVariablesVector() {
		return decisionVariablesVector;
	}

	/**
	 * @param decisionVariables the decisionVariables to set
	 */
	public static void setDecisionVariablesVector(HashMap<String, IloNumVar[]> decisionVariables) {
		DesignPatterns.decisionVariablesVector = decisionVariables;
	}

	/**
	 * @return the decisionVariablesMatrix
	 */
	public static HashMap<String, IloNumVar[][]> getDecisionVariablesMatrix() {
		return decisionVariablesMatrix;
	}

	/**
	 * @param decisionVariablesMatrix the decisionVariablesMatrix to set
	 */
	public static void setDecisionVariablesMatrix(HashMap<String, IloNumVar[][]> decisionVariablesMatrix) {
		DesignPatterns.decisionVariablesMatrix = decisionVariablesMatrix;
	}

	/**
	 * @return the resourceParameters
	 */
	public static List<ResourceParameters> getResourceParameters() {
		return resourceParameters;
	}

	/**
	 * @param resourceParameters the resourceParameters to set
	 */
	public static void setResourceParameters(List<ResourceParameters> resourceParameters) {
		DesignPatterns.resourceParameters = resourceParameters;
	}
}