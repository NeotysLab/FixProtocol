package com.neotys.FIX.customactions;

import com.google.common.base.Optional;
import com.neotys.FIX.FixUtils;
import com.neotys.action.argument.Arguments;
import com.neotys.action.argument.Option;
import com.neotys.extensions.action.Action;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class FIxConnectAction implements Action

    {
        private static final String BUNDLE_NAME = "com.neotys.FIX.customactions.FixConnect.bundle";
        private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayName");
        private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");

        @Override
        public String getType() {
        return "FixConnect";
    }

        @Override
        public List<ActionParameter> getDefaultActionParameters() {
    	final  List<ActionParameter> parameters = new ArrayList<ActionParameter>();
        parameters.add(new ActionParameter("PathConfigfile",""));
    	parameters.add(new ActionParameter("PathMessageFile",""));
    	parameters.add(new ActionParameter("PathOutputFile",""));
		return parameters;

    }

        @Override
        public Class<? extends ActionEngine> getEngineClass() {
        return FixConnectActionEngine.class;
    }

        @Override
        public boolean getDefaultIsHit() {
        return true;
    }

        @Override
        public Icon getIcon() {
        return FixUtils.getFixIcon();
    }

        @Override
        public String getDescription() {
        return " client connection with the Fiximulator server  .\n\n" ;

    }
        @Override
        public String getDisplayName() {
        return DISPLAY_NAME;
    }

        @Override
        public String getDisplayPath() {
        return DISPLAY_PATH;
    }

        @Override
        public Optional<String> getMinimumNeoLoadVersion() {
        return Optional.of("6.7");
    }

        @Override
        public Optional<String> getMaximumNeoLoadVersion() {
        return Optional.absent();
    } }