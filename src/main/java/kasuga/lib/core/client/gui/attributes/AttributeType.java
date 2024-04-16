package kasuga.lib.core.client.gui.attributes;

import com.tterrag.registrate.util.nullness.NonNullFunction;

import java.util.HashMap;

public class AttributeType<T> {
    private final NonNullFunction<T, Attribute<T>> creator;
    private final NonNullFunction<String, Attribute<T>> parser;

    public AttributeType(NonNullFunction<T, Attribute<T>> create, NonNullFunction<String, Attribute<T>> parse) {
        this.creator = create;
        this.parser = parse;
    }

    public Attribute<T> create(T data){
        return creator.apply(data);
    }

    public Attribute<T> parse(String string){
        return parser.apply(string).setType(this);
    }

    public static <T> AttributeType<T> of(NonNullFunction<T,Attribute<T>> create,NonNullFunction<String,Attribute<T>> parse){
        return new AttributeType<>(create,parse);
    }

    @SuppressWarnings("unchecked")
    public Attribute<T> castGet(HashMap<AttributeType<?>,Attribute<?>> map){
        Attribute<?> attribute = map.get(this);
        if(attribute.getType() != this){
            throw new IllegalStateException("Invalid attribute type: "+attribute.getType() + ",expected: "+this);
        }
        return (Attribute<T>) attribute;
    }
}
