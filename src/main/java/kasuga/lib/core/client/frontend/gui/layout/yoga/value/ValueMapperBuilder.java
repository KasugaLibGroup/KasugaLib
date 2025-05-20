package kasuga.lib.core.client.frontend.gui.layout.yoga.value;

import kasuga.lib.core.client.frontend.gui.layout.yoga.api.*;
import kasuga.lib.core.client.frontend.gui.styles.layout.*;

import java.util.HashMap;

public class ValueMapperBuilder<IT extends Class<I>, I, OT extends Class<O>, O> {
    protected HashMap<I, O> mapper = new HashMap<>();

    public ValueMapperBuilder with(I key, O value) {
        mapper.put(key, value);
        return this;
    }

    public O get(I key) {
        return mapper.get(key);
    }

    public static <IT extends Class<I>, I, OT extends Class<O>, O> ValueMapperBuilder<IT, I, OT, O> create(Class<I> clazz, Class<O> clazzO) {
        return new ValueMapperBuilder<>();
    }

    public ValueMapper<I, O> build() {
        return new ValueMapper<>(mapper);
    }

    public static class ValueMapper<I, O> {
        private final HashMap<I, O> mapper;

        public ValueMapper(HashMap<I, O> mapper) {
            this.mapper = mapper;
        }

        public O get(I key) {
            return mapper.get(key);
        }
    }

    public static ValueMapperBuilder.ValueMapper<AlignType, YogaAlign> ALIGN_TYPE =
            create(AlignType.class, YogaAlign.class)
            .with(AlignType.AUTO, YogaAlign.AUTO)
            .with(AlignType.BASELINE, YogaAlign.BASELINE)
            .with(AlignType.CENTER, YogaAlign.CENTER)
            .with(AlignType.FLEX_END, YogaAlign.FLEX_END)
            .with(AlignType.FLEX_START, YogaAlign.FLEX_START)
            .with(AlignType.SPACE_AROUND, YogaAlign.SPACE_AROUND)
            .with(AlignType.SPACE_BETWEEN, YogaAlign.SPACE_BETWEEN)
            .with(AlignType.STRETCH, YogaAlign.STRETCH)
            .with(AlignType.INVALID, null).build();


    public static ValueMapperBuilder.ValueMapper<DisplayType, YogaDisplay> DISPLAY_TYPE =
            create(DisplayType.class, YogaDisplay.class)
            .with(DisplayType.FLEX, YogaDisplay.FLEX)
            .with(DisplayType.UNSET, YogaDisplay.NONE)
            .with(DisplayType.INVALID, null)
            .build();

    public static ValueMapperBuilder.ValueMapper<FlexDirection, YogaFlexDirection> FLEX_DIRECTION =
            create(FlexDirection.class, YogaFlexDirection.class)
            .with(FlexDirection.ROW, YogaFlexDirection.ROW)
            .with(FlexDirection.ROW_REVERSE, YogaFlexDirection.ROW_REVERSE)
            .with(FlexDirection.COLUMN, YogaFlexDirection.COLUMN)
            .with(FlexDirection.COLUMN_REVERSE, YogaFlexDirection.COLUMN_REVERSE)
            .with(FlexDirection.INVALID, null)
            .build();

    public static ValueMapperBuilder.ValueMapper<JustifyType, YogaJustify> JUSTIFY_TYPE =
            create(JustifyType.class, YogaJustify.class)
            .with(JustifyType.FLEX_START, YogaJustify.FLEX_START)
            .with(JustifyType.CENTER, YogaJustify.CENTER)
            .with(JustifyType.FLEX_END, YogaJustify.FLEX_END)
            .with(JustifyType.SPACE_BETWEEN, YogaJustify.SPACE_BETWEEN)
            .with(JustifyType.SPACE_AROUND, YogaJustify.SPACE_AROUND)
            .with(JustifyType.SPACE_EVENLY, YogaJustify.SPACE_EVENLY)
            .with(JustifyType.INVALID, null)
            .build();

    public static ValueMapperBuilder.ValueMapper<PositionType, YogaPositionType> POSITION_TYPE =
            create(PositionType.class, YogaPositionType.class)
            .with(PositionType.ABSOLUTE, YogaPositionType.ABSOLUTE)
            .with(PositionType.RELATIVE, YogaPositionType.RELATIVE)
            .with(PositionType.INVALID, null)
            .build();
}
