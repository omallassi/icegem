package com.googlecode.icegem.cacheutils;

import org.apache.commons.cli.Options;

public abstract class Tool implements Executable {

    protected abstract void parseCommandLineArguments(String[] commandLineArguments);
    
    protected abstract void printHelp(final Options options);
    
    protected abstract Options constructGnuOptions();

}
