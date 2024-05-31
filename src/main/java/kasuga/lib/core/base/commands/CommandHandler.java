package kasuga.lib.core.base.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.NoSuchElementException;

import static kasuga.lib.KasugaLib.MAIN_LOGGER;

/**
 * Extend this class to create your command handler(Use CommandReg.INSTANCE.new)
 */
public abstract class CommandHandler {
    protected CommandContext<CommandSourceStack> ctx;
    protected HashMap<String, ArgType> keys;

    public CommandHandler() {
        this.keys = new HashMap<>();
    }

    /**
     * Internal, do not use directly
     */
    public CommandHandler setCtx(CommandContext<CommandSourceStack> ctx) {
        this.ctx = ctx;
        return this;
    }

    /**
     * Internal, do not use directly
     */
    public void setKeys(HashMap<String, ArgType> keys) {
        this.keys = keys;
    }

    public Integer getInteger(String name) {
        if (!keys.containsKey(name) || keys.get(name) != ArgType.INT) {
            MAIN_LOGGER.error("no such element :" + name, new NoSuchElementException());
            throw new NoSuchElementException();
        }
        return IntegerArgumentType.getInteger(ctx, name);
    }

    public Double getDouble(String name) {
        if (!keys.containsKey(name) || keys.get(name) != ArgType.DOUBLE) {
            MAIN_LOGGER.error("no such element :" + name, new NoSuchElementException());
            throw new NoSuchElementException();
        }
        return DoubleArgumentType.getDouble(ctx, name);
    }

    public String getString(String name) {
        if (!keys.containsKey(name) || keys.get(name) != ArgType.STRING) {
            MAIN_LOGGER.error("no such element :" + name, new NoSuchElementException());
            throw new NoSuchElementException();
        }
        return StringArgumentType.getString(ctx, name);
    }

    public ResourceLocation getResourceLocation(String name) {
        if (!keys.containsKey(name) || keys.get(name) != ArgType.RESOURCE_LOCATION) {
            MAIN_LOGGER.error("no such element :" + name, new NoSuchElementException());
            throw new NoSuchElementException();
        }
        return ResourceLocationArgument.getId(ctx, name);
    }

    public Vec3 getVec3(String name) {
        if (!keys.containsKey(name) || keys.get(name) != ArgType.VECTOR3) {
            MAIN_LOGGER.error("no such element :" + name, new NoSuchElementException());
            throw new NoSuchElementException();
        }
        return Vec3Argument.getVec3(ctx, name);
    }

    public Integer getOptionalInteger(String name, int fallback) {
        try {
            return IntegerArgumentType.getInteger(ctx, name);
        } catch (Exception e) {
            return fallback;
        }
    }

    public Double getOptionalDouble(String name, double fallback) {
        try {
            return DoubleArgumentType.getDouble(ctx, name);
        } catch (Exception e) {
            return fallback;
        }
    }

    public String getOptionalString(String name, String fallback) {
        try {
            return StringArgumentType.getString(ctx, name);
        } catch (Exception e) {
            return fallback;
        }
    }

    public ResourceLocation getOptionalResourceLocation(String name, ResourceLocation fallback) {
        try {
            return ResourceLocationArgument.getId(ctx, name);
        } catch (Exception e) {
            return fallback;
        }
    }

    public Vec3 getOptionalVec3(String name, Vec3 fallback) {
        try {
            return Vec3Argument.getVec3(ctx, name);
        } catch (Exception e) {
            return fallback;
        }
    }
    public abstract void run();
}
