package com.neotys.FIX.customactions;

import com.google.common.base.Optional;
import com.neotys.FIX.CLientApplication;
import com.neotys.action.argument.Arguments;
import com.neotys.action.argument.Option;
import com.neotys.action.result.ResultFactory;
import com.neotys.extensions.action.Action;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;

import quickfix.ConfigError;
import quickfix.SessionNotFound;

import javax.swing.*;
import javax.ws.rs.client.Client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;

public class FixConnectActionEngine implements ActionEngine {
	  private static String PathConfigfile ;
	  private static String PathMessageFile ;
	  public static String PathOutputFile   ;

    
	  private static void parseParameters(List<ActionParameter> parameters) {
		 
		  for (ActionParameter temp:parameters){
				switch (temp.getName()) {
				case "PathConfigfile":
					PathConfigfile = temp.getValue();
					break;
				case "PathMessageFile":
					PathMessageFile = temp.getValue();
					break;
				case "PathOutputFile":
					PathOutputFile = temp.getValue();
					break;
				default :
				}
			} 
	  }
    
    @Override
    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();
        parseParameters(parameters);
        LocalDateTime dateStart2 = LocalDateTime.now();
        System.out.println(dateStart2 + " SampleResult : Config File " + PathConfigfile ); 
        CLientApplication client = new CLientApplication();
        sampleResult.sampleStart(); 
        try {
			client.connector(PathConfigfile);
		} catch (FileNotFoundException | ConfigError | InterruptedException | SessionNotFound e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        LocalDateTime dateStart5 = LocalDateTime.now();
     
        client.sendMsg(PathMessageFile);
        System.out.print("here i am close the session by calling the stop function ");
        client.disconnet();    
        System.out.print("connection closed ! ");

         sampleResult.sampleEnd();
  
        sampleResult.setRequestContent(requestBuilder.toString());
        sampleResult.setResponseContent(responseBuilder.toString());
        return sampleResult;
    }

    private void appendLineToStringBuilder(final StringBuilder sb, final String line) {
        sb.append(line).append("\n");
    }

    /**
     * This method allows to easily create an error result and log exception.
     */
    private static SampleResult getErrorResult(final Context context, final SampleResult result, final String errorMessage, final Exception exception) {
        result.setError(true);
        result.setStatusCode("NL-SendContractTransaction_ERROR");
        result.setResponseContent(errorMessage);
        if (exception != null) {
            context.getLogger().error(errorMessage, exception);
        } else {
            context.getLogger().error(errorMessage);
        }
        return result;
    }


    @Override
    public void stopExecute() {
    	
        // TODO add code executed when the test have to stop.
    }
}