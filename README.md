# ExperimentalSetup
Experimental Setup for the evaluation of optimization strategies for flexible Energy Resources: Simulation Model, Optimization Model, Data, and Communication Logic

Before running the Simulink model 'ThermalModel.slx', the Matlab Scrip 'StartUp_VariableDeclaration.m' must be run. The Matlab Script reads the values of the variables from the excel Sheet 'Variables.xlsx'. 

The java code used to communicate with the PLC of the distillation unit is located in the folder 'Distillation Controller'. The Class 'DestilleController' is used for communication with the PLC. 
The code is highly specific to the OPC UA Server running on the PLC. The IP-adress and Node-IDs must be adjusted to the local Server. UA Expert is a helpful tool in identifying Node-IDs. 
The Optimization model is specified in the package 'designpatterns' in the class 'OptimizationModel'. 
