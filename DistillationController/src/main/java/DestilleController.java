import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.util.EndpointUtil;

import com.github.sarxos.webcam.Webcam;

import designpatterns.DesignPatterns;
import designpatterns.OptimizationModel;
import ilog.concert.*;
import ilog.cplex.*;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


public class DestilleController {
	
	
    public static void main(String[] args) {
    	
    	try {
    		List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints("opc.tcp://139.11.207.51:4840").get();
    		EndpointDescription configPoint = EndpointUtil.updateUrl(endpoints.get(0), "139.11.207.51", 4840);

    		OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();
    		cfg.setEndpoint(configPoint);
    		cfg.setIdentityProvider(new UsernameProvider("admin", "wago"));

    		OpcUaClient client = OpcUaClient.create(cfg.build());
    		client.connect().get();     
//    		String endpointUrl = "opc.tcp://127.0.0.1:12686/milo"; 
//    		OpcUaClient client = OpcUaClient.create(
//    			    endpointUrl,
//    			    endpoints ->
//    			        endpoints.stream()
//    			            .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
//    			            .findFirst(),
//    			    configBuilder ->
//    			        configBuilder.build()
//    			);
//    			client.connect().get();
    		
        	Runnable loggerRunnable = new LoggerRunnable(client);
        	Runnable setterRunnable = new SetterRunnable(client);
        	
        	Thread loggerThread = new Thread(loggerRunnable);
        	Thread setterThread = new Thread(setterRunnable);
        	
        	loggerThread.start();
        	setterThread.start();
                       
    	} catch (Exception e) {
    		// TODO: handle exception
    	}
   

    }
}

class LoggerRunnable implements Runnable {
    private OpcUaClient client;
    
    public LoggerRunnable(OpcUaClient client) {
        this.client = client;
    }
    public void run() {
    	AddressSpace addressSpace = client.getAddressSpace();
    	int nameSpaceIndex = 4;
    	LocalTime localTime = LocalTime.now();
    	long localTimeInSeconds = localTime.toSecondOfDay();
    	String fileName = "data" + Long.toString(localTimeInSeconds) + ".xlsx";
    	String sheetName = "Data Sheet";
    	double  measurementValue;
    	boolean measurementBool; 
    	    	
       	HashMap<Integer, String> nodeIdMap = new HashMap<Integer, String>();
    	//Energiemessungen
    	nodeIdMap.put(1, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.Energiemanagement.Messknoten_1_MeasurementValue_Wh");
    	nodeIdMap.put(2, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.Energiemanagement.Messknoten_2_MeasurementValue_Wh");
    	nodeIdMap.put(3, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.Energiemanagement.Messknoten_3_MeasurementValue_Wh");
    	//Analoge Inputs
    	nodeIdMap.put(4, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Temp_Kopf");
    	nodeIdMap.put(5, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Temp_Sumpf");
    	nodeIdMap.put(6, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Temp_Sumpf_Innen");
    	//Digitale Inputs
    	nodeIdMap.put(7, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Level_Sumpf_unten");
    	nodeIdMap.put(8, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Level_Sumpf_oben");
    	nodeIdMap.put(9, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Level_Destillat");
    	nodeIdMap.put(10, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Kuehlstrom_An");
    	//Analoge Outputs
    	nodeIdMap.put(11, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Ausschwenker");
    	nodeIdMap.put(12, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.HeizenAn");
    	nodeIdMap.put(13, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Mixer");
    	nodeIdMap.put(14, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Pumpe_Kuehlung");
    	nodeIdMap.put(15, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Ventil_Ausgang");
    	nodeIdMap.put(16, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Ventil_Eingang");
    	nodeIdMap.put(17, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Ventil_Destillat1");
    	nodeIdMap.put(18, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Ventil_Destillat2");
    	//Zustaende
    	nodeIdMap.put(19, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.Betriebsbereit");
    	
    	//EXCEL Datei erstellen
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        int rowIndex = 0;
        Row headerRow = sheet.createRow(rowIndex++);
        headerRow.createCell(0).setCellValue(new Date().toString());
        
        for (int i = 1; i <= nodeIdMap.size(); i++) {
        	headerRow.createCell(i).setCellValue(nodeIdMap.get(i));
		}
        
        //Webcam auswählen und öffenen
        Webcam webcam = Webcam.getWebcams().get(0);
        webcam.setViewSize(new Dimension(640, 480));
        
        webcam.open();
        
        while (true) {
        	try {
        		Row dataRow = sheet.createRow(rowIndex++);
            	for (int i = 1; i <= nodeIdMap.size(); i++) {
            		dataRow.createCell(0).setCellValue(new Date().toString());
            		UaVariableNode measurementNode = (UaVariableNode) addressSpace.getNode(new NodeId(nameSpaceIndex, nodeIdMap.get(i)));
            		try {
            			measurementValue = ((Number) measurementNode.readValue().getValue().getValue()).doubleValue();
            			dataRow.createCell(i).setCellValue(measurementValue);
            		} catch (Exception e) {
            			measurementBool = (Boolean) measurementNode.readValue().getValue().getValue();
            			dataRow.createCell(i).setCellValue(measurementBool);
            		}


            		FileOutputStream outputStream = new FileOutputStream(fileName);
            		workbook.write(outputStream);
            		outputStream.close();

            	}
            	
                // get default webcam and open it


                // capture image and save to file
                BufferedImage image = webcam.getImage();
                double time = System.currentTimeMillis();
                File outputfile = new File("image"+Double.toString(time)+".jpg");
                ImageIO.write(image, "jpg", outputfile);

            	Thread.sleep(10000);
			} catch (Exception e) {
				System.out.println("Error in Excel log, Webcam Capture, or Thread Sleep");
				e.printStackTrace() ;
			}
        	
        }
    }
}

class SetterRunnable implements Runnable {
    private OpcUaClient client;
 
    public SetterRunnable(OpcUaClient client) {
        this.client = client;
    }
    
    public void run() {
    	AddressSpace addressSpace = client.getAddressSpace();
    	int nameSpaceIndex = 4; 
    	OptimizationModel optModel = new OptimizationModel();
    	optModel.setOptimizationParameters();
      int[] schedule = new int[DesignPatterns.getArrayLength()];
//    	int[] schedule = new int[] { 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 }; 

    	int timestep = -1;

    	try {
        	double[][] optResults = optModel.optimizationModel();
//        	int length = optResults.length;
        	
        	for (int i = 0; i < optResults.length-1; i++) {
        		if(optResults[i][0]>0.1) {
        			schedule[i] = 1;
        		} else {
        			schedule[i] = 0;
        		}
			}
        	
		} catch (Exception e) {
			System.err.println("model solve failed");
			e.printStackTrace();
		}
    	   	
    	HashMap<Integer, String> nodeIdMap = new HashMap<Integer, String>();
    	nodeIdMap.put(1, "|var|WAGO 750-8212 PFC200 G2 2ETH RS.Application.POU_1.HeizenAn");
    	
    	
    	LocalTime localTime = LocalTime.now();
    	long localTimeInSeconds = localTime.toSecondOfDay();
    	double localTimeInQuarterHours = (double) localTimeInSeconds/15/60;
//    	try {
//			System.out.println((int) (Math.ceil(localTimeInQuarterHours)*15*60-localTimeInSeconds)*1000+1000);
//
//    		Thread.sleep((int) (Math.ceil(localTimeInQuarterHours)*15*60-localTimeInSeconds)*1000+1000);
//		} catch (InterruptedException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
    	
        for (timestep = 1; timestep < schedule.length; timestep++) {
    		try {
				UaVariableNode setPowerNode = (UaVariableNode) addressSpace.getNode(new NodeId(nameSpaceIndex, nodeIdMap.get(1)));
//    			UaVariableNode setPowerNode = (UaVariableNode) addressSpace.getNode(new NodeId(2, "HelloWorld/ScalarTypes/Double"));
				if (schedule[timestep]==1) {
					setPowerNode.writeValue(new Variant(true));
				} else {
					setPowerNode.writeValue(new Variant(false));
				}

		    	localTime = LocalTime.now();
		    	localTimeInSeconds = localTime.toSecondOfDay();
		    	localTimeInQuarterHours = (int) localTimeInSeconds/3/60;
		    	System.out.println((int) ((localTimeInQuarterHours+1)*3*60-localTimeInSeconds)*1000+1000);
		    	Thread.sleep((int) ((localTimeInQuarterHours+1)*3*60-localTimeInSeconds)*1000+1000); 
            } catch (InterruptedException e) {
            	e.printStackTrace();
            } catch (UaException e1) {
            	e1.printStackTrace();
			}
    		}
        }
    }

    	
