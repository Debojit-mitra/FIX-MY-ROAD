package com.bunny.fixmyroad;

public class ReadWriteReportSolvedUnsolvedDetails {

    public String LocationLatLon, description, reportImage, LocationAddress, ReportDate, Status, UserID, ReportID;
    public ReadWriteReportSolvedUnsolvedDetails(){};

    public ReadWriteReportSolvedUnsolvedDetails(String textLocation, String textDescription, String textReportImage, String TextLocationAddress,
                                                String TextReportDate, String TextStatus, String TextUserID, String TextReportID) {
        // this.fullName = textFullName;
        this.LocationLatLon = textLocation;
        this.description = textDescription;
        this.reportImage = textReportImage;
        this.LocationAddress = TextLocationAddress;
        this.ReportDate = TextReportDate;
        this.Status = TextStatus;
        this.UserID = TextUserID;
        this.ReportID = TextReportID;
    }

}
