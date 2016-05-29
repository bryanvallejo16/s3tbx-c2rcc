package org.esa.s3tbx.c2rcc.seawifs;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.FlagCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import static org.junit.Assert.assertTrue;


/**
 * @author Marco Peters
 */
public class SeawifsProductSignatureTest {
    private static final String[] EXPECTED_REFLEC_BANDS = {
            "reflec_" + 412, "reflec_" + 443, "reflec_" + 490, "reflec_" + 510, "reflec_" + 555,
            "reflec_" + 670, "reflec_" + 765, "reflec_" + 865};
    private static final String EXPECTED_RTOSA_RATION_MIN = "rtosa_ratio_min";
    private static final String EXPECTED_RTOSA_RATION_MAX = "rtosa_ratio_max";
    private static final String EXPECTED_IOP_APIG = "iop_apig";
    private static final String EXPECTED_IOP_ADET = "iop_adet";
    private static final String EXPECTED_IOP_AGELB = "iop_agelb";
    private static final String EXPECTED_IOP_BPART = "iop_bpart";
    private static final String EXPECTED_IOP_BWIT = "iop_bwit";
    private static final String EXPECTED_IOP_ADG = "iop_adg";
    private static final String EXPECTED_IOP_ATOT = "iop_atot";
    private static final String EXPECTED_CONC_CHL = "conc_chl";
    private static final String EXPECTED_CONC_TSM = "conc_tsm";
    private static final String[] EXPECTED_RTOSA_IN_BANDS = {
            "rtosa_in_" + 412, "rtosa_in_" + 443, "rtosa_in_" + 490, "rtosa_in_" + 510, "rtosa_in_" + 555,
            "rtosa_in_" + 670, "rtosa_in_" + 765, "rtosa_in_" + 865};
    private static final String[] EXPECTED_RTOSA_OUT_BANDS = {
            "rtosa_out_" + 412, "rtosa_out_" + 443, "rtosa_out_" + 490, "rtosa_out_" + 510, "rtosa_out_" + 555,
            "rtosa_out_" + 670, "rtosa_out_" + 765, "rtosa_out_" + 865};

    private static final String EXPECTED_L2_QFLAGS = "l2_qflags";

    @Test
    public void testProductSignature_Default() throws FactoryException, TransformException {

        C2rccSeaWiFSOperator operator = createDefaultOperator();

        Product targetProduct = operator.getTargetProduct();

        assertDefaultBands(targetProduct);
    }

    @Test
    public void testProductSignature_WithRtosa() throws FactoryException, TransformException {
        C2rccSeaWiFSOperator operator = createDefaultOperator();
        operator.setOutputRtosa(true);

        Product targetProduct = operator.getTargetProduct();

        assertDefaultBands(targetProduct);
        assertBands(targetProduct, EXPECTED_RTOSA_IN_BANDS);
        assertBands(targetProduct, EXPECTED_RTOSA_OUT_BANDS);
    }

    private void assertDefaultBands(Product targetProduct) {
        assertBands(targetProduct, EXPECTED_REFLEC_BANDS);
        assertBands(targetProduct, EXPECTED_RTOSA_RATION_MIN);
        assertBands(targetProduct, EXPECTED_RTOSA_RATION_MAX);
        assertBands(targetProduct, EXPECTED_IOP_APIG);
        assertBands(targetProduct, EXPECTED_IOP_ADET);
        assertBands(targetProduct, EXPECTED_IOP_AGELB);
        assertBands(targetProduct, EXPECTED_IOP_BPART);
        assertBands(targetProduct, EXPECTED_IOP_BWIT);
        assertBands(targetProduct, EXPECTED_IOP_ADG);
        assertBands(targetProduct, EXPECTED_IOP_ATOT);
        assertBands(targetProduct, EXPECTED_CONC_CHL);
        assertBands(targetProduct, EXPECTED_CONC_TSM);
        assertBands(targetProduct, EXPECTED_L2_QFLAGS);
    }

    private void assertBands(Product targetProduct, String... expectedBands) {
        for (String expectedBand : expectedBands) {
            assertTrue("Expected band " + expectedBand + " in product", targetProduct.containsBand(expectedBand));
        }
    }

    private C2rccSeaWiFSOperator createDefaultOperator() throws FactoryException, TransformException {
        C2rccSeaWiFSOperator operator = new C2rccSeaWiFSOperator();
        operator.setParameterDefaultValues();
        operator.setSourceProduct(createSeawifsTestProduct());
        return operator;
    }

    private Product createSeawifsTestProduct() throws FactoryException, TransformException {
        Product product = new Product("test-seawifs", "t", 1, 1);
        int[] reflecWavelengths = C2rccSeaWiFSAlgorithm.seawifsWavelengths;
        for (int reflec_wavelength : reflecWavelengths) {
            String expression = String.valueOf(reflec_wavelength);
            product.addBand(C2rccSeaWiFSOperator.SOURCE_RADIANCE_NAME_PREFIX + reflec_wavelength, expression);
        }

        for (String angleName : C2rccSeaWiFSOperator.GEOMETRY_ANGLE_NAMES) {
            product.addBand(angleName, "42");
        }

        Band flagBand = product.addBand(C2rccSeaWiFSOperator.FLAG_BAND_NAME, ProductData.TYPE_INT8);
        FlagCoding l2FlagsCoding = new FlagCoding(C2rccSeaWiFSOperator.FLAG_BAND_NAME);
        l2FlagsCoding.addFlag("LAND", 0x01, "");
        product.getFlagCodingGroup().add(l2FlagsCoding);
        flagBand.setSampleCoding(l2FlagsCoding);

        product.setSceneGeoCoding(new CrsGeoCoding(DefaultGeographicCRS.WGS84, 1, 1, 10, 50, 1, 1));

        return product;
    }
}