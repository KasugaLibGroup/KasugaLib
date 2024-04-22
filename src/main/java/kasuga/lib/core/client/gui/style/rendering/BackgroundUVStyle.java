package kasuga.lib.core.client.gui.style.rendering;

import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.style.Style;
import kasuga.lib.core.client.gui.style.StyleType;
import kasuga.lib.core.client.gui.style.Styles;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class BackgroundUVStyle extends Style<String> {
    public static final BackgroundUVStyle EMPTY = new BackgroundUVStyle();
    String uvString = "";
    boolean valid = false;
    int left = 0;
    int top = 0;
    int width = 0;
    int height = 0;

    public BackgroundUVStyle(String uv){
        int[] positions = new int[4];
        int pointer = 0;
        try(Scanner scanner=new Scanner(uv)){
            while(scanner.hasNext() && pointer < 4) {
                positions[pointer] = scanner.nextInt();
                pointer++;
            }
        }catch (InputMismatchException mismatchException){
            return;
        }
        if(pointer != 4)
            return;
        left = positions[0];
        top = positions[1];
        width = positions[2];
        height = positions[3];
        valid = true;
    }

    public BackgroundUVStyle() {}

    @Override
    public boolean isValid(Map<StyleType<?>, Style<?>> origin) {
        return valid;
    }

    @Override
    public StyleType<?> getType() {
        return Styles.BACKGROUND_UV;
    }

    @Override
    public void apply(Node node) {
        if(!isValid(null))
            return;
        node.getBackground().setUV(left,top,width,height);
    }

    @Override
    public String getValueString() {
        return uvString;
    }

    @Override
    public String getValue() {
        return uvString;
    }
}
