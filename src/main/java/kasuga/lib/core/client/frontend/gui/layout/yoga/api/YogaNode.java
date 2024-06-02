package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.util.yoga.Yoga.*;

public class YogaNode implements AutoCloseable {
    private long pointer;

    private final List<YogaNode> children = new ArrayList<>(4);

    protected YogaNode owner;
    private boolean hasMeasureFunc;

    YogaNode(long pointer){
        this.pointer = pointer;
    }
    public static YogaNode create(){
        long pointer = YGNodeNew();
        return new YogaNode(pointer);
    }

    public static YogaNode fromPointer(long pointer){
        return new YogaNode(pointer);
    }

    public void reset(){
        YGNodeReset(pointer);
    }

    public int getChildCount(){
        return children.size();
    }

    public int indexOf(YogaNode node){
        return children.indexOf(node);
    }

    public YogaNode getChildAt(int i){
        return children.get(i);
    }

    public void addChildAt(int i,YogaNode node){
        if(node.owner != null){
            throw new IllegalStateException("Child already has a parent, it must be removed first.");
        }

        children.add(i,node);
        node.owner = this;
        YGNodeInsertChild(pointer,node.pointer,i);
    }

    public void setIsReferenceBaseline(boolean isReferenceBaseline) {
        YGNodeSetIsReferenceBaseline(pointer,isReferenceBaseline);
    }

    public boolean isReferenceBaseline(){
        return YGNodeIsReferenceBaseline(pointer);
    }

    public void swapChildAt(YogaNode newChild,int position){
        children.remove(position);
        children.add(position,newChild);
        newChild.owner = this;
        YGNodeSwapChild(pointer,newChild.pointer,position);
    }

    public void clearChildren(){
        children.clear();
        YGNodeRemoveAllChildren(pointer);
    }

    public void removeChildAt(int i){
        final YogaNode node = children.remove(i);
        node.owner = null;
        YGNodeRemoveChild(pointer,node.pointer);
    }

    public YogaNode getOwner(){
        return owner;
    }

    @Deprecated
    public YogaNode getParent(){
        return getOwner();
    }

    public void calculateLayout(float width, float height) {
        YGNodeCalculateLayout(pointer, width, height, YGDirectionInherit);
    }

    public void dirty(){
        if(this.owner == null || !hasMeasureFunc)
            return;
        YGNodeMarkDirty(pointer);
    }

    public void dirtyAllDescendants(){
        YGNodeMarkDirtyAndPropogateToDescendants(pointer);
    }

    public boolean isDirty(){
        return YGNodeIsDirty(pointer);
    }

    public void setDirection(YogaDirection direction){
        YGNodeStyleSetDirection(pointer,direction.getValue());
    }

    public void setJustifyContent(YogaJustify justifyContent) {
        YGNodeStyleSetJustifyContent(pointer, justifyContent.getValue());
    }

    public void setAlignItems(YogaAlign alignItems) {
        YGNodeStyleSetAlignItems(pointer, alignItems.getValue());
    }

    public void setAlignSelf(YogaAlign alignSelf) {
        YGNodeStyleSetAlignSelf(pointer, alignSelf.getValue());
    }

    public void setAlignContent(YogaAlign alignContent) {
        YGNodeStyleSetAlignContent(pointer, alignContent.getValue());
    }

    public void setPositionType(YogaPositionType type){
        YGNodeStyleSetPositionType(pointer,type.getValue());
    }

    public void setWrap(YogaWrap flexWrap) {
        YGNodeStyleSetFlexWrap(pointer, flexWrap.getValue());
    }

    public void setOverflow(YogaOverflow overflow) {
        YGNodeStyleSetOverflow(pointer, overflow.getValue());
    }

    public void setDisplay(YogaDisplay display) {
        YGNodeStyleSetDisplay(pointer, display.getValue());
    }

    public void setFlex(float flexGrow){
        YGNodeStyleSetFlex(pointer,flexGrow);
    }

    public void setFlexGrow(float flexGrow){
        YGNodeStyleSetFlexGrow(pointer,flexGrow);
    }

    public void setFlexShrink(float flexShrink){
        YGNodeStyleSetFlexShrink(pointer,flexShrink);
    }

    public void setFlexBasis(float flexBasis){
        YGNodeStyleSetFlexBasis(pointer,flexBasis);
    }

    public void setFlexBasisPercent(float flexPercent){
        YGNodeStyleSetFlexBasisPercent(pointer,flexPercent);
    }

    public void setFlexBasisAuto(){
        YGNodeStyleSetFlexBasisAuto(pointer);
    }

    public void setMargin(YogaEdge edge, float margin){
        YGNodeStyleSetMargin(pointer,edge.getValue(),margin);
    }

    public void setMarginPercent(YogaEdge edge, float percent) {
        YGNodeStyleSetMarginPercent(pointer, edge.getValue(), percent);
    }

    public void setMarginAuto(YogaEdge edge) {
        YGNodeStyleSetMarginAuto(pointer, edge.getValue());
    }


    public void setPadding(YogaEdge edge, float padding) {
        YGNodeStyleSetPadding(pointer, edge.getValue(), padding);
    }

    public void setPaddingPercent(YogaEdge edge, float percent) {
        YGNodeStyleSetPaddingPercent(pointer, edge.getValue(), percent);
    }

    public float getBorder(YogaEdge edge) {
        return YGNodeStyleGetBorder(pointer, edge.getValue());
    }

    public void setBorder(YogaEdge edge, float border) {
        YGNodeStyleSetBorder(pointer, edge.getValue(), border);
    }

    public void setMinWidth(float minWidth) {
        YGNodeStyleSetMinWidth(pointer, minWidth);
    }

    public void setMinWidthPercent(float percent) {
        YGNodeStyleSetMinWidthPercent(pointer, percent);
    }

    public void setMinHeight(float minHeight) {
        YGNodeStyleSetMinHeight(pointer, minHeight);
    }

    public void setMinHeightPercent(float percent) {
        YGNodeStyleSetMinHeightPercent(pointer, percent);
    }

    public void setMaxWidthPercent(float percent) {
        YGNodeStyleSetMaxWidthPercent(pointer, percent);
    }

    public void setMaxHeightPercent(float percent) {
        YGNodeStyleSetMaxHeightPercent(pointer, percent);
    }

    public float getAspectRatio() {
        return YGNodeStyleGetAspectRatio(pointer);
    }

    public void setAspectRatio(float aspectRatio) {
        YGNodeStyleSetAspectRatio(pointer, aspectRatio);
    }
    // -----

    public void setWidth(float width){
        YGNodeStyleSetWidth(pointer,width);
    }

    public void setHeight(float height){
        YGNodeStyleSetHeight(pointer,height);
    }

    public void setWidthPercent(float percent){
        YGNodeStyleSetWidthPercent(pointer,percent);
    }

    public void setHeightPercent(float percent){
        YGNodeStyleSetHeightPercent(pointer,percent);
    }

    public void setWidthAuto(){
        YGNodeStyleSetWidthAuto(pointer);
    }

    public void setHeightAuto(){
        YGNodeStyleSetHeightAuto(pointer);
    }

    public void setMaxWidth(float maxWidth){
        YGNodeStyleSetMaxWidth(pointer,maxWidth);
    }

    public void setMaxHeight(float maxHeight){
        YGNodeStyleSetMaxHeight(pointer,maxHeight);
    }

    public void setMaxWidthPercentage(float maxWidthPercentage){
        YGNodeStyleSetMaxWidthPercent(pointer,maxWidthPercentage);
    }

    public void setMaxHeightPercentage(float maxHeightPercentage){
        YGNodeStyleSetMaxHeightPercent(pointer,maxHeightPercentage);
    }


    public void setMeasureFunction(YogaMeasureFunction measureFunction){
        YGNodeSetMeasureFunc(pointer,YogaMeasureFunctionTransformer.transform(measureFunction));
        this.hasMeasureFunc = true;
    }

    public void setPosition(YogaEdge edge,float value){
        YGNodeStyleSetPosition(pointer,edge.getValue(),value);
    }

    public void setPositionPercent(YogaEdge edge,float value){
        YGNodeStyleSetPositionPercent(pointer,edge.getValue(),value);
    }

    public void setFlexDirection(YogaFlexDirection flexDirection){
        YGNodeStyleSetFlexDirection(pointer,flexDirection.getValue());
    }

    public float getLayoutLeft(){
        return YGNodeLayoutGetLeft(pointer);
    }

    public float getLayoutTop(){
        return YGNodeLayoutGetTop(pointer);
    }

    public float getLayoutWidth(){
        return YGNodeLayoutGetWidth(pointer);
    }

    public float getLayoutHeight(){
        return YGNodeLayoutGetHeight(pointer);
    }

    public void setNodeType(YogaNodeType type) {
        YGNodeSetNodeType(pointer, type.getValue());
    }

    public boolean hasNewLayout(){
        return YGNodeGetHasNewLayout(pointer);
    }

    public void visited(){
        YGNodeSetHasNewLayout(pointer,false);
    }

    public void free() {
        System.out.println("[GC] Yoga Node Free");
        if (pointer != 0) {
            long nativePointer = pointer;
            pointer = 0;
            YGNodeFree(nativePointer);
        }
    }

    @Override
    public void close() {
        free();
    }
}
