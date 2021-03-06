package org.cern.cms.dbloader.util;

import jdk.nashorn.internal.objects.annotations.Setter;

/**
 * Operator authentication data.
 * @author valdo
 */
public class OperatorAuth {

    private final String username;
    private final String fullname;
    private final boolean construct_permission;
    private final boolean condition_permission;
    private final boolean tracking_permission;

    private String operatorName;

    public OperatorAuth(String username, String fullname, boolean construct_permission, boolean condition_permission, boolean tracking_permission) {
        this.username = username;
        this.fullname = fullname;
        this.construct_permission = construct_permission;
        this.condition_permission = condition_permission;
        this.tracking_permission = tracking_permission;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public boolean isConstructPermission() {
        return construct_permission;
    }

    public boolean isConditionPermission() {
        return condition_permission;
    }

    public boolean isTrackingPermission() {
        return tracking_permission;
    }

    public String getOperatorValue() {
        if (this.operatorName != null) {
            return this.operatorName;
        } else {
            return String.format("%s (%s)", getFullname(), getUsername());
        }
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}