/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aws_sig4.impl;

import com.mendix.systemwideinterfaces.MendixRuntimeException;

/**
 *
 * @author reinout
 */
@SuppressWarnings("serial")
public class AWSRequestSigningInterceptorException extends MendixRuntimeException {

    private static final String DEFAULT_MESSAGE = "Unable to sign request";

    public AWSRequestSigningInterceptorException() {
        this(DEFAULT_MESSAGE);
    }

    public AWSRequestSigningInterceptorException(String message) {
        super(message);
    }

    public AWSRequestSigningInterceptorException(Throwable t) {
        this(DEFAULT_MESSAGE, t);
    }

    public AWSRequestSigningInterceptorException(String message, Throwable t) {
        super(message, t);
    }

}

