package kasuga.lib.core.client.gui.style;

import kasuga.lib.core.util.data_type.Pair;

public enum PixelUnit {
    INVALID("invalid"),
    NATIVE(""),
    PERCENTAGE("%");

    public static final PixelUnit[] SEARCH_ORDER = {PERCENTAGE, NATIVE};

    public static Pair<Float,PixelUnit> parse(String pixelValue){
        for (PixelUnit pixelUnit : SEARCH_ORDER) {
            if(pixelUnit.isUnit(pixelValue)){
                String val = pixelValue.substring(0,pixelValue.length() - pixelUnit.getSuffix().length()).trim();
                Float numericValue = null;
                try{
                    numericValue = Float.parseFloat(val);
                }catch (NumberFormatException e){
                    continue;
                }
                return Pair.of(numericValue,pixelUnit);
            }
        }
        return Pair.of(0f,INVALID);
    }

    private final String suffix;

    PixelUnit(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isUnit(String string){
        return string.endsWith(suffix);
    }

    public String inUnit(float value){
        return String.valueOf(value) + suffix;
    }

    public String toString(float value){
        String stringValue = String.valueOf(value);
        if(stringValue.endsWith(".0"))
            stringValue = stringValue.substring(0,stringValue.length() - 2);
        return stringValue + this.suffix;
    }
}
