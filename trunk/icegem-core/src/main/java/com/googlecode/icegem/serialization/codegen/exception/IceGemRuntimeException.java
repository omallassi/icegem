package com.googlecode.icegem.serialization.codegen.exception;

/**
 * Internal runtime exception for IceGem library.
 *
 * @author Andrey Stepanov aka standy
 */
public class IceGemRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -2823592964786861425L;

    public IceGemRuntimeException() {
    }

    public IceGemRuntimeException(Throwable cause) {
        super(cause);
    }

    public IceGemRuntimeException(String message) {
        super(message);
    }

    public IceGemRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
