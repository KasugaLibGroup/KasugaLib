package kasuga.lib.core.client.gui;

public class ElementBoundingBox {
    public static ElementBoundingBox EMPTY = new ElementBoundingBox(0,0,0,0);
    public final int left;
    public int top;
    public int right;
    public int bottom;

    protected ElementBoundingBox(int left,int top,int right,int bottom){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public static ElementBoundingBox ofHeightWidth(int x,int y,int h,int w){
        return new ElementBoundingBox(x,y,x+h,y+w);
    }

    public static ElementBoundingBox ofPositions(int left,int top,int right,int bottom){
        return new ElementBoundingBox(left,top,right,bottom);
    }

    public int getWidth(){
        return right - left;
    }

    public int getHeight(){
        return bottom - top;
    }

    public ElementBoundingBox merge(ElementBoundingBox external){
        return new ElementBoundingBox(
                Math.min(external.left,left),
                Math.max(external.right,right),
                Math.min(external.top,top),
                Math.max(external.bottom,bottom)
        );
    }

    public ElementBoundingBox withSize(int height,int width){
        return ElementBoundingBox.ofHeightWidth(left,top,height,width);
    }

    public ElementBoundingBox padding(ElementBoundingBox external){
        return new ElementBoundingBox(
                left - external.left,
                top - external.top,
                right + external.right,
                bottom - external.bottom
        );
    }

    public ElementBoundingBox add(ElementBoundingBox external){
        return new ElementBoundingBox(
                left + external.left,
                top + external.top,
                right + external.right,
                bottom + external.bottom
        );
    }
}
