package designpatterns;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * ResourceParameters for Design Patterns.
 */
public class ResourceParameters {

	/** The name. */
	String name; 

	/** The energy carrier input. */
	String energyCarrierInput; 

	/** The energy carrier output. */
	String energyCarrierOutput; 

	/** The relevant energy carrier. */
	String relevantEnergyCarrier; 
	/** The min/max power input. */
	double minPowerInput; 

	/** The max power input. */
	double maxPowerInput = Double.MAX_VALUE; 

	/** The min/max power output. */
	double minPowerOutput;

	/** The max power output. */
	double maxPowerOutput = Double.MAX_VALUE;

	/** The min/max ramp. */
	double minRamp; 

	/** The max ramp. */
	double maxRamp;

	/** The latency of output. */
	int latencyOfOutput;

	/** The system states. */
	List<SystemState> systemStates = new ArrayList<SystemState>();

	/** The number of system states. */
	int numberOfSystemStates;

	/** The initial system state. */
	int initialSystemState = 0; 
	
	/**  Efficiencies/IO Relation. */
	double efficiency; 

	/** The intercept. */
	double slope, intercept; 

	/** The pla. */
	List<PiecewiseLinearApproximation> pla = new ArrayList<PiecewiseLinearApproximation>();

	/** The number of linear segments. */
	int numberOfLinearSegments; 

	/**  Storage Specific parameters. */
	boolean isStorage = false; 

	/** The maximum storage capacity. */
	double maximumStorageCapacity;

	/** The minimum storage capacity. */
	double minimumStorageCapacity; 
	
	/** The unit conversion factor storage. */
	double unitConversionFactorStorage = 1; 

	/** The inital capacity. */
	double initalCapacity; 
	
	/** The capacity target. */
	double capacityTarget = -1; 

	/** The capacity target comparator. */
	String capacityTargetComparator = "Eq";
	
	/** The static power loss. */
	double staticEnergyLoss;
	
	/** The dynamic energy loss. */
	double dynamicEnergyLoss;
	
	/** The reference value for dynamic energy loss. */
	double referenceDynamicEnergyLoss; 

	/** The efficiency input storage. */
	double efficiencyInputStorage = 1; 

	/** The efficiency output storage. */
	double efficiencyOutputStorage = 1; 

	/** The efficiency output reciprocal. */ 
	double efficiencyOutputReciprocal = 1/efficiencyOutputStorage;

	/**
	 * Creates the pla list from arguments.
	 *
	 * @param interceptpla the interceptpla
	 * @param slopepla the slopepla
	 * @param lowerboundpla the lowerboundpla
	 * @param upperboundpla the upperboundpla
	 */
	public void createPlaList (double interceptpla, double slopepla, double lowerboundpla, double upperboundpla) {
		PiecewiseLinearApproximation plaItem = new PiecewiseLinearApproximation();
		plaItem.setIntercept(interceptpla);
		plaItem.setSlope(slopepla);
		plaItem.setUpperBound(upperboundpla);
		plaItem.setLowerBound(lowerboundpla);
		this.pla.add(plaItem);
	}


	/**
	 * Adds the system state.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 */
	public void addSystemState (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		this.systemStates.add(state);
	}

	/**
	 * Adds the system state with ramp.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param minRampState the min ramp state
	 * @param maxRampState the max ramp state
	 */
	public void addSystemStateWithRamp (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, double minRampState, double maxRampState) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setMinRampInput(minRampState);
		state.setMaxRampInput(maxRampState);
		this.systemStates.add(state);
	}

	/**
	 * Adds the system state with max power output.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param maxPowerOutputState the max power output state
	 */
	public void addSystemStateWithMaxPowerOutput (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, double maxPowerOutputState) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setMaxPowerOutput(maxPowerOutputState);
		this.systemStates.add(state);
	}

	/**
	 * Adds the system state with max power output and ramp input.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param maxPowerOutputState the max power output state
	 * @param minRampStateInput the min ramp state input
	 * @param maxRampStateInput the max ramp state input
	 */
	public void addSystemStateWithMaxPowerOutputAndRampInput (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, double maxPowerOutputState, double minRampStateInput, double maxRampStateInput) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setMaxPowerOutput(maxPowerOutputState);
		state.setMinRampInput(minRampStateInput);
		state.setMaxRampInput(maxRampStateInput);
		this.systemStates.add(state);
	}

	/**
	 * Adds the system state with latency.
	 * sets values of minHoldingDuration and maxHoldingDuration to double latencyState
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param latencyState the latency state
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 */
	public void addSystemStateWithLatency (int stateID, String stateName, double latencyState,  int[] idsOfFollowerStates, double minPowerState, double maxPowerState) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(latencyState);
		state.setMaxStateDuration(latencyState);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		this.systemStates.add(state);
	}

	
	/**
	 * Adds the system state with max power output and ramp output.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param maxPowerOutputState the max power output state
	 * @param minRampStateOutput the min ramp state output
	 * @param maxRampStateOutput the max ramp state output
	 */
	public void addSystemStateWithMaxPowerOutputAndRampOutput (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, double maxPowerOutputState, double minRampStateOutput, double maxRampStateOutput) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setMaxPowerOutput(maxPowerOutputState);
		state.setMinRampOutput(minRampStateOutput);
		state.setMaxRampOutput(maxRampStateOutput);
		this.systemStates.add(state);
	}

	/**
	 * Adds the system state with max power output and ramp input output.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param maxPowerOutputState the max power output state
	 * @param minRampStateInput the min ramp state input
	 * @param maxRampStateInput the max ramp state input
	 * @param minRampStateOutput the min ramp state output
	 * @param maxRampStateOutput the max ramp state output
	 */
	public void addSystemStateWithMaxPowerOutputAndRampInputOutput (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, double maxPowerOutputState, double minRampStateInput, double maxRampStateInput, double minRampStateOutput, double maxRampStateOutput) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setMaxPowerOutput(maxPowerOutputState);
		state.setMinRampInput(minRampStateInput);
		state.setMaxRampInput(maxRampStateInput);
		state.setMinRampOutput(minRampStateOutput);
		state.setMaxRampOutput(maxRampStateOutput);
		this.systemStates.add(state);
	}
	/**
	 * Gets the min power.
	 *
	 * @return the minPower
	 */
	public double getMinPowerInput() {
		return minPowerInput;
	}

	/**
	 * Sets the min power.
	 *
	 * @param minPower the minPower to set
	 */
	public void setMinPowerInput(double minPower) {
		this.minPowerInput = minPower;
	}

	/**
	 * Gets the max power.
	 *
	 * @return the maxPower
	 */
	public double getMaxPowerInput() {
		return maxPowerInput;
	}

	/**
	 * Sets the max power.
	 *
	 * @param maxPower the maxPower to set
	 */
	public void setMaxPowerInput(double maxPower) {
		this.maxPowerInput = maxPower;
	}

	/**
	 * Gets the min ramp.
	 *
	 * @return the minRamp
	 */
	public double getMinRamp() {
		return minRamp;
	}

	/**
	 * Sets the min ramp.
	 *
	 * @param minRamp the minRamp to set
	 */
	public void setMinRamp(double minRamp) {
		this.minRamp = minRamp;
	}

	/**
	 * Gets the max ramp.
	 *
	 * @return the maxRamp
	 */
	public double getMaxRamp() {
		return maxRamp;
	}

	/**
	 * Sets the max ramp.
	 *
	 * @param maxRamp the maxRamp to set
	 */
	public void setMaxRamp(double maxRamp) {
		this.maxRamp = maxRamp;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the relevant energy carrier.
	 *
	 * @return the relevantEnergyCarrier
	 */
	public String getRelevantEnergyCarrier() {
		return relevantEnergyCarrier;
	}

	/**
	 * Sets the relevant energy carrier.
	 *
	 * @param relevantEnergyCarrier the relevantEnergyCarrier to set
	 */
	public void setRelevantEnergyCarrier(String relevantEnergyCarrier) {
		this.relevantEnergyCarrier = relevantEnergyCarrier;
	}

	/**
	 * Gets the efficiency.
	 *
	 * @return the efficiency
	 */
	public double getEfficiency() {
		return efficiency;
	}

	/**
	 * Sets the efficiency.
	 *
	 * @param efficiency the efficiency to set
	 */
	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}

	/**
	 * Gets the slope.
	 *
	 * @return the slope
	 */
	public double getSlope() {
		return slope;
	}

	/**
	 * Sets the slope.
	 *
	 * @param slope the slope to set
	 */
	public void setSlope(double slope) {
		this.slope = slope;
	}

	/**
	 * Gets the intercept.
	 *
	 * @return the intercept
	 */
	public double getIntercept() {
		return intercept;
	}

	/**
	 * Sets the intercept.
	 *
	 * @param intercept the intercept to set
	 */
	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}



	/**
	 * Gets the number of linear segments.
	 *
	 * @return the numberOfLinearSegments
	 */
	public int getNumberOfLinearSegments() {
		return numberOfLinearSegments;
	}

	/**
	 * Sets the number of linear segments.
	 *
	 * @param numberOfLinearSegments the numberOfLinearSegments to set
	 */
	public void setNumberOfLinearSegments(int numberOfLinearSegments) {
		this.numberOfLinearSegments = numberOfLinearSegments;
	}

	/**
	 * Gets the pla.
	 *
	 * @return the pla
	 */
	public List<PiecewiseLinearApproximation> getPla() {
		return pla;
	}

	/**
	 * Sets the pla.
	 *
	 * @param pla the pla to set
	 */
	public void setPla(List<PiecewiseLinearApproximation> pla) {
		this.pla = pla;
	}


	/**
	 * Checks if is storage.
	 *
	 * @return the isStorage
	 */
	public boolean isStorage() {
		return isStorage;
	}


	/**
	 * Sets the resource as storage.
	 *
	 * @param isStorage the isStorage to set
	 */
	public void setResourceAsStorage(boolean isStorage) {
		this.isStorage = isStorage;
	}


	/**
	 * Gets the maximum storage capacity.
	 *
	 * @return the maximumStorageCapacity
	 */
	public double getMaximumStorageCapacity() {
		return maximumStorageCapacity;
	}


	/**
	 * Sets the maximum storage capacity.
	 *
	 * @param maximumStorageCapacity the maximumStorageCapacity to set
	 */
	public void setMaximumStorageCapacity(double maximumStorageCapacity) {
		this.maximumStorageCapacity = maximumStorageCapacity;
	}


	/**
	 * Gets the minimum storage capacity.
	 *
	 * @return the minimumStorageCapacity
	 */
	public double getMinimumStorageCapacity() {
		return minimumStorageCapacity;
	}


	/**
	 * Sets the minimum storage capacity.
	 *
	 * @param minimumStorageCapacity the minimumStorageCapacity to set
	 */
	public void setMinimumStorageCapacity(double minimumStorageCapacity) {
		this.minimumStorageCapacity = minimumStorageCapacity;
	}


	/**
	 * Gets the inital capacity.
	 *
	 * @return the initalCapacity
	 */
	public double getInitalCapacity() {
		return initalCapacity;
	}


	/**
	 * Sets the inital capacity.
	 *
	 * @param initalCapacity the initalCapacity to set
	 */
	public void setInitalCapacity(double initalCapacity) {
		this.initalCapacity = initalCapacity;
	}


	/**
	 * Gets the static energy loss (energy loss independent of power flows or storage level).
	 *
	 * @return the staticEnergyLoss
	 */
	public double getStaticEnergyLoss() {
		return staticEnergyLoss;
	}


	/**
	 * Sets the static energy loss (energy loss independent of power flows or storage level).
	 *
	 * @param staticEnergyLoss the staticPowerLoss to set
	 */
	public void setStaticEnergyLoss(double staticEnergyLoss) {
		this.staticEnergyLoss = staticEnergyLoss;
	}

	/**
	 * Gets the min power output.
	 *
	 * @return the minPowerOutput
	 */
	public double getMinPowerOutput() {
		return minPowerOutput;
	}

	/**
	 * Sets the min power output.
	 *
	 * @param minPowerOutput the minPowerOutput to set
	 */
	public void setMinPowerOutput(double minPowerOutput) {
		this.minPowerOutput = minPowerOutput;
	}

	/**
	 * Gets the max power output.
	 *
	 * @return the maxPowerOutput
	 */
	public double getMaxPowerOutput() {
		return maxPowerOutput;
	}

	/**
	 * Sets the max power output.
	 *
	 * @param maxPowerOutput the maxPowerOutput to set
	 */
	public void setMaxPowerOutput(double maxPowerOutput) {
		this.maxPowerOutput = maxPowerOutput;
	}

	/**
	 * Gets the efficiency input storage.
	 *
	 * @return the efficiencyInputStorage
	 */
	public double getEfficiencyInputStorage() {
		return efficiencyInputStorage;
	}

	/**
	 * Sets the efficiency input storage.
	 *
	 * @param efficiencyInputStorage the efficiencyInputStorage to set
	 */
	public void setEfficiencyInputStorage(double efficiencyInputStorage) {
		this.efficiencyInputStorage = efficiencyInputStorage;
	}

	/**
	 * Gets the efficiency output storage.
	 *
	 * @return the efficiencyOutputStorage
	 */
	public double getEfficiencyOutputStorage() {
		return efficiencyOutputStorage;
	}

	/**
	 * Sets the efficiency output storage.
	 *
	 * @param efficiencyOutputStorage the efficiencyOutputStorage to set
	 */
	public void setEfficiencyOutputStorage(double efficiencyOutputStorage) {
		this.efficiencyOutputStorage = efficiencyOutputStorage;
	}

	/**
	 * Gets the efficiency output reciprocal.
	 *
	 * @return the efficiencyOutputReciprocal
	 */
	public double getEfficiencyOutputReciprocal() {
		return efficiencyOutputReciprocal;
	}

	/**
	 * Sets the efficiency output reciprocal.
	 *
	 * @param efficiencyOutputReciprocal the efficiencyOutputReciprocal to set
	 */
	public void setEfficiencyOutputReciprocal(double efficiencyOutputReciprocal) {
		this.efficiencyOutputReciprocal = efficiencyOutputReciprocal;
	}


	/**
	 * Gets the system states.
	 *
	 * @return the systemStates
	 */
	public List<SystemState> getSystemStates() {
		return systemStates;
	}


	/**
	 * Sets the system states.
	 *
	 * @param systemStates the systemStates to set
	 */
	public void setSystemStates(List<SystemState> systemStates) {
		this.systemStates = systemStates;
	}


	/**
	 * Gets the number of system states.
	 *
	 * @return the numberOfSystemStates
	 */
	public int getNumberOfSystemStates() {
		return numberOfSystemStates;
	}


	/**
	 * Sets the number of system states.
	 *
	 * @param numberOfSystemStates the numberOfSystemStates to set
	 */
	public void setNumberOfSystemStates(int numberOfSystemStates) {
		this.numberOfSystemStates = numberOfSystemStates;
	}


	/**
	 * Gets the latency of output.
	 *
	 * @return the latencyOfOutput
	 */
	public int getLatencyOfOutput() {
		return latencyOfOutput;
	}


	/**
	 * Sets the latency of output.
	 *
	 * @param latencyOfOutput the latencyOfOutput to set
	 */
	public void setLatencyOfOutput(int latencyOfOutput) {
		this.latencyOfOutput = latencyOfOutput;
	}


	/**
	 * Gets the energy carrier input.
	 *
	 * @return the energyCarrierInput
	 */
	public String getEnergyCarrierInput() {
		return energyCarrierInput;
	}

	/**
	 * Sets the energy carrier of both sides.
	 *
	 * @param energyCarrier the new energy carrier
	 */
	public void setEnergyCarrier(String energyCarrier) {
		this.energyCarrierInput = energyCarrier;
		this.energyCarrierOutput = energyCarrier;
	} 

	/**
	 * Sets the energy carrier input.
	 *
	 * @param energyCarrierInput the energyCarrierInput to set
	 */
	public void setEnergyCarrierInput(String energyCarrierInput) {
		//		this.energyCarrierInput = energyCarrierInput;
	}


	/**
	 * Gets the energy carrier output.
	 *
	 * @return the energyCarrierOutput
	 */
	public String getEnergyCarrierOutput() {
		return energyCarrierOutput;
	}


	/**
	 * Sets the energy carrier output.
	 *
	 * @param energyCarrierOutput the energyCarrierOutput to set
	 */
	public void setEnergyCarrierOutput(String energyCarrierOutput) {
		this.energyCarrierOutput = energyCarrierOutput;
	}


	/**
	 * Gets the dynamic energy loss (energy loss dependent of power flows or storage level).
	 *
	 * @return the dynamicEnergyLoss
	 */
	public double getDynamicEnergyLoss() {
		return dynamicEnergyLoss;
	}


	/**
	 * Sets the dynamic energy loss (energy loss dependent of power flows or storage level)
	 * loss = dynamicLoss * (maxSOC - SOC[i]).
	 *
	 * @param dynamicEnergyLoss the dynamicEnergyLoss to set
	 */
	public void setDynamicEnergyLoss(double dynamicEnergyLoss) {
		this.dynamicEnergyLoss = dynamicEnergyLoss;
	}


	/**
	 * Gets the reference dynamic energy loss.
	 *
	 * @return the referenceDynamicEnergyLoss
	 */
	public double getReferenceDynamicEnergyLoss() {
		return referenceDynamicEnergyLoss;
	}


	/**
	 * Sets the reference dynamic energy loss.
	 *
	 * @param referenceDynamicEnergyLoss the referenceDynamicEnergyLoss to set
	 */
	public void setReferenceDynamicEnergyLoss(double referenceDynamicEnergyLoss) {
		this.referenceDynamicEnergyLoss = referenceDynamicEnergyLoss;
	}


	/**
	 * Gets the initial system state.
	 *
	 * @return the initialSystemState
	 */
	public int getInitialSystemState() {
		return initialSystemState;
	}


	/**
	 * Sets the initial system state.
	 *
	 * @param initialSystemState the initialSystemState to set
	 */
	public void setInitialSystemState(int initialSystemState) {
		this.initialSystemState = initialSystemState;
	}


	/**
	 * @return the capacityTarget
	 */
	public double getCapacityTarget() {
		return capacityTarget;
	}


	/**
	 * Sets the capacity target at last time step.
	 *
	 * @param capacityTarget the capacityTarget to set
	 */
	public void setCapacityTarget(double capacityTarget) {
		this.capacityTarget = capacityTarget;
	}


	/**
	 * @return the capacityTargetComparator
	 */
	public String getCapacityTargetComparator() {
		return capacityTargetComparator;
	}


	/**
	 * Sets the capacity target comparator (Eq =, Ge =>, Le <=).
	 *
	 * @param capacityTargetComparator the capacityTargetComparator to set
	 */
	public void setCapacityTargetComparator(String capacityTargetComparator) {
		this.capacityTargetComparator = capacityTargetComparator;
	}


	/**
	 * @return the unitConversionFactorStorage
	 */
	public double getUnitConversionFactorStorage() {
		return unitConversionFactorStorage;
	}


	/**
	 * Sets the unit conversion factor storage f -> SOC[i] = SOC[i-1] + f(P_in*eta*t-Pout/eta*t-Ploss).
	 *
	 * @param unitConversionFactorStorage the unitConversionFactorStorage to set
	 */
	public void setUnitConversionFactorStorage(double unitConversionFactorStorage) {
		this.unitConversionFactorStorage = unitConversionFactorStorage;
	} 

}
