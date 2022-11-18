package com.bunny.fixmyroad;

public class ReadWriteReportDetails {

    public String LocationLatLon, description, reportImage, LocationAddress, ReportNumber, ReportDate, Status;
    public ReadWriteReportDetails(){};

    public ReadWriteReportDetails(String textLocation, String textDescription, String textReportImage, String TextLocationAddress, String TextReportNumber,
                                  String TextReportDate, String TextStatus) {
       // this.fullName = textFullName;
        this.LocationLatLon = textLocation;
        this.description = textDescription;
        this.reportImage = textReportImage;
        this.LocationAddress = TextLocationAddress;
        this.ReportNumber = TextReportNumber;
        this.ReportDate = TextReportDate;
        this.Status = TextStatus;
    }

}
