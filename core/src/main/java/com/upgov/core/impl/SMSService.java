package com.upgov.core.impl;

/**
 * Created with IntelliJ IDEA.
 * User: shivani
 * Date: 17/2/15
 * Time: 5:32 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SMSService {
    void sendSMSToNumber(String bodyText, String toMobileNumber );
}
