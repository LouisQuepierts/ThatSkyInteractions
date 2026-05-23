package net.quepierts.thatskyinteractions.infra.animation.backend;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class Patterns {

    public static final Pattern PATTERN_IDENTIFIER = Pattern.compile("^[a-zA-Z_#$][a-zA-Z0-9_#$]*$");

    public static final Pattern PATTERN_SEMANTIC = Pattern.compile("^[a-zA-Z_#$][a-zA-Z0-9_#$.]*$");

}
