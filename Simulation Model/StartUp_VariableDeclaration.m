% Specify the name of the Excel file
excelFileName = 'Variables.xlsx';

% Read data from the Excel file
[num, txt, ~] = xlsread(excelFileName, 'SimulinkInput', 'A2:C27');

% Get the variable names from column A (txt will contain a cell array of strings)
variableNames = txt(:, 1);

% Get the variable values from column C (num will contain a numeric array)
variableValues = num(:, 1);

% Create individual variables in the MATLAB workspace
for i = 1:numel(variableNames)
    assignin('base', genvarname(variableNames{i}), variableValues(i));
end    

% Display the created variables
disp('Variables created and saved in the variables.mat file:');
disp(variableNames');


%this is the simulink input data needed for calibrating the model
%parameters. The data is used in the Simulink model in a
%'From Workspace' Block. 
excelFileName = '0907 Experimental Data.xlsx';

[SimInput, ~, ~] = xlsread(excelFileName, 'ToSimulink', 'C420:H3407');
Time = SimInput(:,1);
HeaterTemperature = SimInput(:,6);
HeadTemperature = SimInput(:,5);
ProductVolume = SimInput(:,2);


%this is the simulink input data needed for simulating the result of
%optimized operating schedules. The data is used in the Simulink model in a
%'From Workspace' Block. 
excelFileName = '0908_Experimental Data.xlsx';

[SimInputOpt, ~, ~] = xlsread(excelFileName, 'SimInputOpt', 'A2:E1677');
TimeOpt = SimInputOpt(:,1);
MC1Temperature = SimInputOpt(:,3);
MC2Temperature = SimInputOpt(:,4);
ProductVolumeOpt = SimInputOpt(:,5);
