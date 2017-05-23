import java.awt.color.ColorSpace;

public class CIELab extends ColorSpace {

    public static CIELab getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        double l = f(colorvalue[1]);
        double L = 116.0 * l - 16.0;
        double a = 500.0 * (f(colorvalue[0]) - l);
        double b = 200.0 * (l - f(colorvalue[2]));
        return new float[] {(float) L, (float) a, (float) b};
    }

    @Override
    public float[] fromRGB(float[] rgbvalue) {
        float[] xyz = CIEXYZ.fromRGB(rgbvalue);
        return fromCIEXYZ(xyz);
    }

    @Override
    public float getMaxValue(int component) {
        return 128f;
    }

    @Override
    public float getMinValue(int component) {
        return (component == 0)? 0f: -128f;
    }    

    @Override
    public String getName(int idx) {
        return String.valueOf("Lab".charAt(idx));
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue) {
        double i = (colorvalue[0] + 16.0) * (1.0 / 116.0);
        double X = fInv(i + colorvalue[1] * (1.0 / 500.0));
        double Y = fInv(i);
        double Z = fInv(i - colorvalue[2] * (1.0 / 200.0));
        return new float[] {(float) X, (float) Y, (float) Z};
    }

    @Override
    public float[] toRGB(float[] colorvalue) {
        float[] xyz = toCIEXYZ(colorvalue);
        return CIEXYZ.toRGB(xyz);
    }
    
    public float[] rgbToXyz(float[] rgb){
    	float var_R = ( rgb[0] / 255 );
    	float var_G = ( rgb[1] / 255 );
    	float var_B = ( rgb[2] / 255 );

    	if ( var_R > 0.04045 ) var_R = (float) Math.pow(( ( var_R + 0.055 ) / 1.055 ), 2.4);
    	else                   var_R = (float) (var_R / 12.92);
    	if ( var_G > 0.04045 ) var_G = (float) Math.pow(( ( var_G + 0.055 ) / 1.055 ), 2.4);
    	else                   var_G = (float) (var_G / 12.92);
    	if ( var_B > 0.04045 ) var_B = (float) Math.pow(( ( var_B + 0.055 ) / 1.055 ), 2.4);
    	else                   var_B = (float) (var_B / 12.92);

    	var_R = var_R * 100;
    	var_G = var_G * 100;
    	var_B = var_B * 100;
    	
    	float[] xyz = new float[3];
    	xyz[0] = (float) (var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805);
    	xyz[1] = (float) (var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722);
    	xyz[2] = (float) (var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505);
    	return xyz;
    }
    
    CIELab() {
        super(ColorSpace.TYPE_Lab, 3);
    }

    private static double f(double x) {
        if (x > 216.0 / 24389.0) {
            return Math.cbrt(x);
        } else {
            return (841.0 / 108.0) * x + N;
        }
    }

    private static double fInv(double x) {
        if (x > 6.0 / 29.0) {
            return x*x*x;
        } else {
            return (108.0 / 841.0) * (x - N);
        }
    }

    private Object readResolve() {
        return getInstance();
    }

    private static class Holder {
        static final CIELab INSTANCE = new CIELab();
    }

    private static final long serialVersionUID = 5027741380892134289L;

    private static final ColorSpace CIEXYZ =
        ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);

    private static final double N = 4.0 / 29.0;

}