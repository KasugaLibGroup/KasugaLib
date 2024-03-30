package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

/**
 * Use this to create a recipe and it's serializer. Recipe is used for crafting, smelting and so on.
 * For example, see {@link net.minecraft.world.item.crafting.CraftingRecipe} or
 * {@link net.minecraft.world.item.crafting.SingleItemRecipe}
 * @param <T> the class of your recipe.
 * @param <F> the class of your recipe serializer.
 */
public class RecipeReg<T extends Recipe<?>, F extends RecipeSerializer<?>> extends Reg {
    public T recipe;
    public F recipeSerializer;
    public RegistryObject<RecipeType<T>> recipeRegistryObject = null;
    public RegistryObject<F> recipeSerializerRegistryObject = null;

    public RecipeReg(String registrationKey) {
        super(registrationKey);
    }

    /**
     * Create a recipe reg.
     * @param registrationKey The registration key of your recipe.
     * @param recipe The recipe instance.
     * @param serializer The recipe serializer instance.
     */
    public RecipeReg(String registrationKey, Recipe<?> recipe, RecipeSerializer<?> serializer) {
        this(registrationKey);
        withRecipeAndSerializer(recipe, serializer);
    }

    /**
     * Submit your config to minecraft and forge registry.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Override
    @Mandatory
    public RecipeReg<T, F> submit(SimpleRegistry registry) {
        this.recipeRegistryObject = registry.recipe().register(registrationKey, () -> new RecipeType<T>() {
            @Override
            public String toString() {return registrationKey;}
        });
        this.recipeSerializerRegistryObject = registry.recipe_serializer().register(registrationKey, () -> recipeSerializer);
        return this;
    }

    public F getSerializer() {
        return recipeSerializerRegistryObject == null? null : recipeSerializerRegistryObject.get();
    }

    public RecipeType<T> getRecipeType() {
        return recipeRegistryObject == null ? null : recipeRegistryObject.get();
    }

    @Inner
    private RecipeReg<T, F> withRecipeAndSerializer(Recipe<?> recipe, RecipeSerializer<?> serializer) {
        this.recipe = (T) recipe;
        this.recipeSerializer = (F) serializer;
        return this;
    }

    @Override
    public String getIdentifier() {
        return "recipe";
    }
}
