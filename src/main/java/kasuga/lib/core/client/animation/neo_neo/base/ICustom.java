package kasuga.lib.core.client.animation.neo_neo.base;

import interpreter.compute.data.Namespace;
import interpreter.compute.infrastructure.Formula;
import net.minecraft.nbt.CompoundTag;

public interface ICustom {

    void setX(String x);
    void setY(String y);
    void setZ(String z);

    Formula getX();
    Formula getY();
    Formula getZ();

    Namespace getNamespace();
    default void writeCustomFormulas(CompoundTag nbt, String name) {
        String x = getX().getString();
        String y = getY().getString();
        String z = getZ().getString();
        CompoundTag tag = new CompoundTag();
        tag.putString("type", "custom_formula");
        tag.putString("x", x);
        tag.putString("y", y);
        tag.putString("z", z);
        nbt.put(name, tag);
    }

    default void readCustomFormulas(CompoundTag nbt, String name) {
        if (!nbt.contains(name)) return;
        CompoundTag tag = nbt.getCompound(name);
        if (!tag.getString("type").equals("custom_formula")) return;
        setX(tag.getString("x"));
        setY(tag.getString("y"));
        setZ(tag.getString("z"));
    }
}
