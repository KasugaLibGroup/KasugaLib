package kasuga.lib.registrations.registry;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.core.annos.Beta;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Util;
import kasuga.lib.core.client.ModelMappings;
import kasuga.lib.core.client.render.model.CustomRenderedItemModel;
import kasuga.lib.core.base.SimpleCreativeTab;
import kasuga.lib.registrations.common.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

/**
 * SimpleRegistry is the core registry of KasugaLib provide Registration.
 * call via {@link kasuga.lib.registrations.registry.SimpleRegistry#SimpleRegistry(String, IEventBus)} with your namespace
 * name and your mod event bus. We have provided registries for some types of game elements.
 * To use these registrations, see {@link kasuga.lib.registrations.Reg} and its subClasses.
 */
public class SimpleRegistry {

    public final String namespace;
    public final IEventBus eventBus;
    private final Logger logger;
    private final DeferredRegister<SoundEvent> SOUNDS;
    private final DeferredRegister<Block> BLOCK;
    private final DeferredRegister<Item> ITEMS;
    private final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES;
    private final DeferredRegister<MenuType<?>> MENUS;
    private final DeferredRegister<EntityType<?>> ENTITIES;
    private final DeferredRegister<Attribute> ATTRIBUTES;
    private final DeferredRegister<RecipeType<?>> RECIPES;
    private final DeferredRegister<MobEffect> EFFECT;
    private final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS;
    private final DeferredRegister<FluidType> FLUID_TYPE;
    private final DeferredRegister<Fluid> FLUID;
    private final ModelRegistry MODELS;
    private final HashMap<String, BlockEntityReg<?>> CACHE_OF_BLOCK_ENTITIES;
    private final HashMap<String, MenuReg<?, ?, ?>> CACHE_OF_MENUS;
    private final HashMap<Supplier<Block>, BlockReg.BlockRendererBuilder<Block>> CACHE_OF_BLOCK_RENDERER;
    private final HashSet<EntityReg<?>> CACHE_OF_ENTITIES;
    private final HashSet<String> CUSTOM_RENDERED_ITEMS;
    private final HashSet<EntityReg<? extends LivingEntity>> CACHE_OF_LIVING_ENTITIES;
    private final ModelMappings modelMappings;
    private final HashMap<String, SimpleCreativeTab> TABS;

    /**
     * This constructor is used for create a new KasugaLib registration.
     * @param namespace your mod namespace name
     * @param bus your mod namespace eventbus. For more info see
     *            {@link FMLJavaModLoadingContext#get()}
     *            and
     *            {@link FMLJavaModLoadingContext#getModEventBus()}
     */
    public SimpleRegistry(String namespace, IEventBus bus) {
        this.namespace = namespace;
        this.eventBus = bus;
        logger = LoggerFactory.getLogger(namespace + "/reg");
        SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, namespace);
        BLOCK = DeferredRegister.create(ForgeRegistries.BLOCKS, namespace);
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, namespace);
        BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, namespace);
        MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, namespace);
        ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, namespace);
        RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, namespace);
        RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, namespace);
        EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, namespace);
        ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, namespace);
        FLUID_TYPE = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, namespace);
        FLUID = DeferredRegister.create(ForgeRegistries.Keys.FLUIDS, namespace);
        MODELS = new ModelRegistry(namespace, this);
        CACHE_OF_BLOCK_ENTITIES = new HashMap<>();
        CUSTOM_RENDERED_ITEMS = new HashSet<>();
        CACHE_OF_MENUS = new HashMap<>();
        CACHE_OF_LIVING_ENTITIES = new HashSet<>();
        modelMappings = new ModelMappings(namespace);
        CACHE_OF_ENTITIES = new HashSet<>();
        CACHE_OF_BLOCK_RENDERER = new HashMap<>();
        TABS = new HashMap<>();
    }

    /**
     * return the registry logger.
     * @return registry logger.
     */
    public Logger logger() {
        return logger;
    }

    /**
     * return the registry of SoundEvents. See {@link kasuga.lib.registrations.common.SoundReg}
     * @return Registry of SoundEvents
     */
    public DeferredRegister<SoundEvent> sound() {return SOUNDS;}

    /**
     * return the registry of Blocks. See {@link kasuga.lib.registrations.common.BlockReg}
     * @return Registry of Blocks
     */
    public DeferredRegister<Block> block() {
        return BLOCK;
    }

    /**
     * return the registry of items. See {@link kasuga.lib.registrations.common.ItemReg}
     * @return Registry of items.
     */
    public DeferredRegister<Item> item() {
        return ITEMS;
    }

    /**
     * return the registry of BlockEntities. See {@link kasuga.lib.registrations.common.BlockEntityReg}
     * @return Registry of BlockEntities.
     */
    public DeferredRegister<BlockEntityType<?>> blockEntity() {
        return BLOCK_ENTITIES;
    }

    /**
     * return the registry of ContainerMenus and GUI Screens. See {@link kasuga.lib.registrations.common.MenuReg}
     * @return Registry of ContainerMenus.
     */
    public DeferredRegister<MenuType<?>> menus() {
        return MENUS;
    }

    /**
     * return the registry of Entities. See {@link kasuga.lib.registrations.common.EntityReg}
     * @return Registry of Entities.
     */
    public DeferredRegister<EntityType<?>> entity() {
        return ENTITIES;
    }

    /**
     * return the registry of entity AI attributes.
     * @return Registry of attributes.
     */
    @Beta
    public DeferredRegister<Attribute> attribute() {return ATTRIBUTES;}

    /**
     * return the registry of recipes. See {@link kasuga.lib.registrations.common.RecipeReg}
     * @return Registry of recipes.
     */
    public DeferredRegister<RecipeType<?>> recipe() {
        return RECIPES;
    }

    /**
     * return the registry of recipe serializers. See {@link kasuga.lib.registrations.common.RecipeReg}
     * @return Registry of recipe.
     */
    public DeferredRegister<RecipeSerializer<?>> recipe_serializer() {
        return RECIPE_SERIALIZERS;
    }

    /**
     * return the registry of poison effects. See {@link kasuga.lib.registrations.common.EffectReg}
     * @return Registry of effects.
     */
    public DeferredRegister<MobEffect> mob_effect() {return EFFECT;}

    /**
     * return the registry of fluid types. See {@link kasuga.lib.registrations.common.FluidReg}
     * @return the registry of fluid types.
     */
    public DeferredRegister<FluidType> fluid_type() {return FLUID_TYPE;}

    /**
     * return the registry of fluid. See {@link kasuga.lib.registrations.common.FluidReg}
     * @return the registry of fluid.
     */
    public DeferredRegister<Fluid> fluid() {return FLUID;}

    /**
     * retrun the registry of kasuga lib style models. See {@link kasuga.lib.registrations.client.ModelReg}
     * @return the registry of kasuga lib style models.
     */
    public ModelRegistry model() {return MODELS;}

    /**
     * return the registry of Creative Mode Tabs. See {@link kasuga.lib.registrations.common.CreativeTabReg}
     * @return the regsitry of kasuga lib style models.
     */
    public HashMap<String, SimpleCreativeTab> tab() {return TABS;}

    /**
     * method for get location for resource under given namespace
     * @param path the path of resource called.
     * @return new resource location.
     */
    @Util
    public ResourceLocation asResource(String path) {
        return new ResourceLocation(namespace, path);
    }

    /**
     * You must call this after the registry has all been loaded.
     */
    @Mandatory
    public void submit() {
        SOUNDS.register(eventBus);
        BLOCK.register(eventBus);
        ITEMS.register(eventBus);
        FLUID_TYPE.register(eventBus);
        FLUID.register(eventBus);
        for(String key : CACHE_OF_BLOCK_ENTITIES.keySet()) {
            BlockEntityReg<?> reg = CACHE_OF_BLOCK_ENTITIES.get(key);
            try {
                reg.getType();
                reg.submit(this);
            } catch (Exception ignored) {}
        }
        for(String key : CACHE_OF_MENUS.keySet()) {CACHE_OF_MENUS.get(key).submit(this);}
        BLOCK_ENTITIES.register(eventBus);
        MENUS.register(eventBus);
        ENTITIES.register(eventBus);
        RECIPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
        KasugaLib.STACKS.stackIn(this);
    }

    /**
     * Don't use this in your registration.
     * @param registrationKey key
     */
    @Inner
    public void stackCustomRenderedItemIn(String registrationKey) {
        CUSTOM_RENDERED_ITEMS.add(registrationKey);
    }

    /**
     * Don't use this in your registration.
     * @return Model Mappings.
     */
    @Inner
    public ModelMappings modelMappings(){return modelMappings;}

    /**
     * Don't use this in your registration.
     * @param entityReg be to be cached
     */
    @Inner
    public void cacheBeIn(BlockEntityReg<?> entityReg) {
        CACHE_OF_BLOCK_ENTITIES.put(entityReg.registrationKey, entityReg);
    }

    /**
     * Don't use this in your registration.
     * @param registrationKey key of cache
     * @return contains?
     */
    @Inner
    public boolean hasBeCache(String registrationKey) {
        return CACHE_OF_BLOCK_ENTITIES.containsKey(registrationKey);
    }

    /**
     * Don't use.
     * @param registrationKey key
     * @return the blockEntity cached.
     */
    @Inner
    public BlockEntityReg<?> getBeCached(String registrationKey) {
        return CACHE_OF_BLOCK_ENTITIES.getOrDefault(registrationKey, null);
    }

    /**
     * Don't use
     * @param menuReg the reg would be cached in.
     */
    @Inner
    public void cacheMenuIn(MenuReg<?, ?, ?> menuReg) {
        CACHE_OF_MENUS.put(menuReg.registrationKey, menuReg);
    }

    /**
     * Don't use.
     * @param registrationKey key
     * @return contains?
     */
    @Inner
    public boolean hasMenuCache(String registrationKey) {
        return CACHE_OF_MENUS.containsKey(registrationKey);
    }

    /**
     * Don't use.
     * @param registrationKey key
     * @return the reg cached.
     */
    @Inner
    public MenuReg<?, ?, ?> getMenuCached(String registrationKey) {
        return CACHE_OF_MENUS.getOrDefault(registrationKey, null);
    }

    public HashMap<String, MenuReg<?, ?, ?>> getCahcedMenus() {
        return CACHE_OF_MENUS;
    }

    /**
     * Don't use.
     * @param reg the reg to cache in.
     */
    @Inner
    public void cacheLivingEntityIn(EntityReg<? extends LivingEntity> reg) {
        CACHE_OF_LIVING_ENTITIES.add(reg);
    }

    /**
     * Don't use.
     * @return the reg cached.
     */
    @Inner
    public HashSet<EntityReg<? extends LivingEntity>> getCachedLivingEntities() {
        return CACHE_OF_LIVING_ENTITIES;
    }

    /**
     * Don't use.
     * @param entityReg the reg to cache in.
     */
    @Inner
    public void cacheEntityIn(EntityReg<?> entityReg) {
        CACHE_OF_ENTITIES.add(entityReg);
    }




    @Inner
    public void cacheBlockRendererIn(BlockReg reg, BlockReg.BlockRendererBuilder rendererBuilder) {
        this.CACHE_OF_BLOCK_RENDERER.put(reg::getBlock, rendererBuilder);
    }

    @Inner
    public void cacheBlockRendererIn(FluidBlockReg reg, BlockReg.BlockRendererBuilder rendererBuilder) {
        this.CACHE_OF_BLOCK_RENDERER.put(reg::getBlock, rendererBuilder);
    }

    public void onBlockRendererReg() {
        KasugaLibStacks stacks = KasugaLib.STACKS;
        CACHE_OF_BLOCK_RENDERER.forEach((a, b) -> stacks.cacheBlockRendererIn(a.get(), b.build(a).get()));
    }
    /**
     * Don't use. This would be call via {@link kasuga.lib.core.events.client.ModelRegistryEvent}
     */
    @Inner
    public void onEntityRendererReg() {
        CACHE_OF_ENTITIES.forEach(EntityReg::registerRenderer);
        CACHE_OF_BLOCK_ENTITIES.forEach((a, b) -> b.registerRenderer(this));
        CACHE_OF_BLOCK_ENTITIES.clear();
        CACHE_OF_ENTITIES.clear();
    }

    /**
     * Don't use. This would be calls via {@link kasuga.lib.core.events.client.ModelRegistryEvent}
     * @param registry the registry would be fired.
     */
    @Inner
    public void onCustomItemRendererReg(Map<ResourceLocation, BakedModel> registry) {
        for(String key : this.CUSTOM_RENDERED_ITEMS) {
            ModelResourceLocation location = new ModelResourceLocation(this.namespace, key, "inventory");
            if(!registry.containsKey(location)) continue;
            CustomRenderedItemModel model = new CustomRenderedItemModel(registry.get(location));
            registry.remove(location);
            registry.put(location, model);
        }
        CUSTOM_RENDERED_ITEMS.clear();
    }

    /**
     * Don't use this in your registration. This method is for BlockEntityRenderer binding.
     * @param type BlockEntity for binding
     * @param rendererBuilder BlockEntityRenderer for binding
     * @param <T> the type of BlockEntity
     */
    @Inner
    public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityReg.BlockEntityProvider<T> type,
                                                                    BlockEntityReg.BlockEntityRendererBuilder<T> rendererBuilder) {
        BlockEntityRenderers.register(type.provide(), rendererBuilder::build);
    }
}
