/**
 * The BusinessException adds custom error code to standard Java exception 
 * 
 * @author amiljkovic
 */
 
package com.frontierwholesales.core.magento.services.exceptions;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.cq.commerce.api.CommerceException;
import com.frontierwholesales.core.magento.services.CorsService;

public class FrontierWholesalesBusinessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(FrontierWholesalesBusinessException.class);

    private final FrontierWholesalesErrorCode code;

    public FrontierWholesalesBusinessException(FrontierWholesalesErrorCode code) {
        super();
        this.code = code;
    }

    public FrontierWholesalesBusinessException(String message, Throwable cause, FrontierWholesalesErrorCode code) {
        super(message, cause);
        this.code = code;
    }

    public FrontierWholesalesBusinessException(String message, FrontierWholesalesErrorCode code) {
        super(message);
        this.code = code;
    }

    public FrontierWholesalesBusinessException(Throwable cause, FrontierWholesalesErrorCode code) {

        super(cause);

        this.code = code;

    }

    public FrontierWholesalesErrorCode getCode() {
        return this.code;
    }   

}