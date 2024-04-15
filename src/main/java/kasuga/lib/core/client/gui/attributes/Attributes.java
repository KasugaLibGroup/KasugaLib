package kasuga.lib.core.client.gui.attributes;

import kasuga.lib.core.client.gui.enums.ComponentType;
import kasuga.lib.core.client.gui.enums.DisplayType;
import kasuga.lib.core.client.gui.enums.PositionType;
import kasuga.lib.core.client.gui.layout.ElementLocator;

import static kasuga.lib.core.client.gui.attributes.LocatorAttributeOperator.asLocator;

public class Attributes {
    public static AttributeType<Integer> LEFT =
            AttributesRegistry.register("left",IntegerAttribute.of(asLocator(ElementLocator::setLeft)));
    public static AttributeType<Integer> RIGHT =
            AttributesRegistry.register("right",IntegerAttribute.of(asLocator(ElementLocator::setRight)));
    public static AttributeType<Integer> TOP =
            AttributesRegistry.register("top",IntegerAttribute.of(asLocator(ElementLocator::setTop)));
    public static AttributeType<Integer> BOTTOM =
            AttributesRegistry.register("bottom",IntegerAttribute.of(asLocator(ElementLocator::setBottom)));
    public static AttributeType<Integer> WIDTH =
            AttributesRegistry.register("width",IntegerAttribute.of(asLocator(ElementLocator::setWidth)));
    public static AttributeType<Integer> HEIGHT =
            AttributesRegistry.register("height",IntegerAttribute.of(asLocator(ElementLocator::setHeight)));
    public static AttributeType<DisplayType> DISPLAY_TYPE = AttributesRegistry.register("displayType",
            SimpleAttribute.of(DisplayType::fromString, DisplayType::toString,(w, v)->{

            }));

    public static AttributeType<PositionType> POSITION_TYPE = AttributesRegistry.register("positionType",
            SimpleAttribute.of(PositionType::fromString, PositionType::toString,(w, v)->{
                w.triggerLocate();
            }));

    public static AttributeType<ComponentType> COMPONENT_TYPE = AttributesRegistry.register("componentType",
            SimpleAttribute.of(ComponentType::fromString, ComponentType::toString,(w, v)->{

            }));




}
