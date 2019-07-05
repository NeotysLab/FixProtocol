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
        final ArrayList<ActionParameter> parameters = new ArrayList<>();

        for (final FIxConnectOption option : FIxConnectOption.values()) {
            if (Option.AppearsByDefault.True.equals(option.getAppearsByDefault())) {
                parameters.add(new ActionParameter(option.getName(), option.getDefaultValue(),
                        option.getType()));
            }
        }

        return parameters;
    }

        @Override
        public Class<? extends ActionEngine> getEngineClass() {
        return FixConnectActionEngine.class;
    }

        @Override
        public boolean getDefaultIsHit() {
        return false;
    }

        @Override
        public Icon getIcon() {
        return FixUtils.getFixIcon();
    }

        @Override
        public String getDescription() {
        return "Create the Fix connect  .\n\n" + Arguments.getArgumentDescriptions(FIxConnectOption.values());

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