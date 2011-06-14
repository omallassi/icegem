package com.googlecode.icegem.serialization.codegen.exception;

/**
 * Overflow exception for method frame stack.
 *
 * @author Andrey Stepanov aka standy
 */
public class MethodFrameStackOverflowException extends IceGemRuntimeException {
    private static final long serialVersionUID = -8732786605729114987L;

    public MethodFrameStackOverflowException() {
    }

    public MethodFrameStackOverflowException(Throwable cause) {
        super(cause);
    }

    public MethodFrameStackOverflowException(String message) {
        super(message);
    }

    public MethodFrameStackOverflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
