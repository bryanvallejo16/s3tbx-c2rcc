package org.esa.s3tbx.c2rcc.ancillary;

public interface InterpolationBorderComputer {

    void setInterpolationTimeMJD(double timeMJD);

    double getStartBorderTimeMDJ();

    double getEndBorderTimeMJD();

    String getStartAncFilePrefix();

    String getEndAncFilePrefix();

}
