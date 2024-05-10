package org.bleachhack.command.exception;

import java.io.Serial;

public class CmdSyntaxException extends Exception {

    @Serial
    private static final long serialVersionUID = 7940377774005961331L;

    public CmdSyntaxException() {
        this("Invalid Syntax!");
    }

    public CmdSyntaxException(String message) {
        super(message);
    }

}