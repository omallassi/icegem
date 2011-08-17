package com.googlecode.icegem.serialization.codegen;

import static com.googlecode.icegem.serialization.codegen.CodeGenUtils.tab;

public class StaticConstructorGenerator {

	public String process(XClass xClass, String serializerClsName) {
    	byte beanVersion = xClass.getBeanVersion();
    	byte historyLength = xClass.getVersionHistoryLength();
    	
    	StringBuilder builder = new StringBuilder();
        builder.append(tab("VERSION_METADATA = new com.googlecode.icegem.serialization.codegen.VersionMap(\""+ xClass.getName()+"\", "+ beanVersion +","+ historyLength +");\n"));
        for(int i = 0 ; i < historyLength; i++) {
        	int version = beanVersion - i;
			builder.append(tab(2, "VERSION_METADATA.put((byte)" + version + ", (short)" + 
        			xClass.getVersionModelHashCode(version) + ");\n"));
        }
        return builder.toString();
	}
	
}
