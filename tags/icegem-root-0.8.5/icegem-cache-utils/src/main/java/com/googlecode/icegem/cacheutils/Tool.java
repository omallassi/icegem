package com.googlecode.icegem.cacheutils;

import org.apache.commons.cli.Options;

/**
 * 
 */
public abstract class Tool implements Executable {
    /**
     * @param commandLineArguments - Command line arguments.
     */
    protected abstract void parseCommandLineArguments(String[] commandLineArguments);

    /**
     * @param options - Options.
     */
    protected abstract void printHelp(final Options options);

    /**
     * @return - GNU options.
     */
    protected abstract Options constructGnuOptions();
}
