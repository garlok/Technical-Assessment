package com.ms.assessment.model.enums;

public enum ActionType {
    BUY("BUY"),
    SELL("SELL");

    private final String actionType;

    ActionType(final String actionType){
        this.actionType = actionType;
    }

    private String getActionType(){
        return this.actionType;
    }
}
