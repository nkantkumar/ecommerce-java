package com.capitalmarkets;

import quickfix.*;
import quickfix.Message;
import quickfix.field.*;
import quickfix.fix44.*;
import quickfix.fix44.MessageCracker;

public class MyFixApplication extends MessageCracker implements Application {

    @Override
    public void onCreate(SessionID sessionId) {
        System.out.println("Session created: " + sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        System.out.println("Logon successful: " + sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        System.out.println("Logout: " + sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {

    }


    @Override
    public void fromAdmin(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        // Incoming admin messages
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        // Outgoing application messages
        System.out.println("Sending: " + message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        // Incoming application messages
        crack(message, sessionId);
    }

    // Handle specific message types
    public void onMessage(NewOrderSingle order, SessionID sessionId)
            throws FieldNotFound {

        Symbol symbol = order.getSymbol();
        Side side = order.getSide();
        OrdType ordType = order.getOrdType();
        Price price = order.getPrice();

        System.out.println("Received order for " + symbol.getValue() +
                " Side: " + side.getValue());

        // Send execution report
        sendExecutionReport(order, sessionId);
    }

    private void sendExecutionReport(NewOrderSingle order, SessionID sessionId)
            throws FieldNotFound {

        ExecutionReport execReport = new ExecutionReport(
        );

        execReport.set(order.getClOrdID());
        execReport.set(order.getSymbol());
        execReport.set(new AvgPx(order.getPrice().getValue()));

        try {
            Session.sendToTarget(execReport, sessionId);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }
}