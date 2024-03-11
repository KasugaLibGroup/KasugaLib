package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

public class RecipeReg<T extends Recipe<?>, F extends RecipeSerializer<?>> extends Reg {

    public T recipe;
    public F recipeSerializer;
    public RegistryObject<RecipeType<T>> recipeRegistryObject = null;
    public RegistryObject<F> recipeSerializerRegistryObject = null;

    public RecipeReg(String registrationKey) {
        super(registrationKey);
    }

    public RecipeReg(String registrationKey, Recipe<?> recipe, RecipeSerializer<?> serializer) {
        this(registrationKey);
        withRecipeAndSerializer(recipe, serializer);
    }


    public RecipeReg<T, F> withRecipeAndSerializer(Recipe<?> recipe, RecipeSerializer<?> serializer) {
        this.recipe = (T) recipe;
        this.recipeSerializer = (F) serializer;
        return this;
    }

    public F getSerializer() {
        return recipeSerializerRegistryObject == null? null : recipeSerializerRegistryObject.get();
    }

    public RecipeType<T> getRecipeType() {
        return recipeRegistryObject == null ? null : recipeRegistryObject.get();
    }



    @Override
    public Reg submit(SimpleRegistry registry) {
        this.recipeRegistryObject = registry.recipe().register(registrationKey, () -> new RecipeType<T>() {
            @Override
            public String toString() {return registrationKey;}
        });
        this.recipeSerializerRegistryObject = registry.recipe_serializer().register(registrationKey, () -> recipeSerializer);
        return this;
    }


    @Override
    public String getIdentifier() {
        return "recipe";
    }
}
