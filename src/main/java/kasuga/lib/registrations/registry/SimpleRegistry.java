package kasuga.lib.registrations.registry;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.ModelMappings;
import kasuga.lib.core.client.render.model.CustomRenderedItemModel;
import kasuga.lib.core.util.SimpleCreativeTab;
import kasuga.lib.registrations.common.BlockEntityReg;
import kasuga.lib.registrations.common.EntityReg;
import kasuga.lib.registrations.common.ItemReg;
import kasuga.lib.registrations.common.MenuReg;
import kasuga.lib.registrations.registry.ModelRegistry;
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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
    private final HashSet<EntityReg<?>> CACHE_OF_ENTITIES;
    private final HashSet<String> CUSTOM_RENDERED_ITEMS;
    private final HashSet<EntityReg<? extends LivingEntity>> CACHE_OF_LIVING_ENTITIES;
    private final ModelMappings modelMappings;
    private final HashMap<String, SimpleCreativeTab> TABS;

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
        TABS = new HashMap<>();
    }

    public Logger logger() {
        return logger;
    }

    public DeferredRegister<SoundEvent> sound() {return SOUNDS;}

    public DeferredRegister<Block> block() {
        return BLOCK;
    }

    public DeferredRegister<Item> item() {
        return ITEMS;
    }

    public DeferredRegister<BlockEntityType<?>> blockEntity() {
        return BLOCK_ENTITIES;
    }

    public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityReg.BlockEntityProvider<T> type,
                                                                    BlockEntityReg.BlockEntityRendererBuilder<T> rendererBuilder) {
        BlockEntityRenderers.register(type.provide(), rendererBuilder::build);
    }

    public DeferredRegister<MenuType<?>> menus() {
        return MENUS;
    }

    public DeferredRegister<EntityType<?>> entity() {
        return ENTITIES;
    }
    public DeferredRegister<Attribute> attribute() {return ATTRIBUTES;}
    public DeferredRegister<RecipeType<?>> recipe() {
        return RECIPES;
    }
    public DeferredRegister<RecipeSerializer<?>> recipe_serializer() {
        return RECIPE_SERIALIZERS;
    }
    public DeferredRegister<MobEffect> mob_effect() {return EFFECT;}
    public DeferredRegister<FluidType> fluid_type() {return FLUID_TYPE;}
    public DeferredRegister<Fluid> fluid() {return FLUID;}
    public ModelRegistry model() {return MODELS;}
    public HashMap<String, SimpleCreativeTab> tab() {return TABS;}

    public void stackCustomRenderedItemIn(String registrationKey) {
        CUSTOM_RENDERED_ITEMS.add(registrationKey);
    }

    public ModelMappings modelMappings(){return modelMappings;}

    public void cacheBeIn(BlockEntityReg<?> entityReg) {
        CACHE_OF_BLOCK_ENTITIES.put(entityReg.registrationKey, entityReg);
    }

    public boolean hasBeCache(String registrationKey) {
        return CACHE_OF_BLOCK_ENTITIES.containsKey(registrationKey);
    }

    public BlockEntityReg<?> getBeCached(String registrationKey) {
        return CACHE_OF_BLOCK_ENTITIES.getOrDefault(registrationKey, null);
    }

    public void cacheMenuIn(MenuReg<?, ?, ?> menuReg) {
        CACHE_OF_MENUS.put(menuReg.registrationKey, menuReg);
    }

    public boolean hasMenuCache(String registrationKey) {
        return CACHE_OF_MENUS.containsKey(registrationKey);
    }

    public MenuReg<?, ?, ?> getMenuCached(String registrationKey) {
        return CACHE_OF_MENUS.getOrDefault(registrationKey, null);
    }

    public void cacheLivingEntityIn(EntityReg<? extends LivingEntity> reg) {
        CACHE_OF_LIVING_ENTITIES.add(reg);
    }

    public HashSet<EntityReg<? extends LivingEntity>> getCachedLivingEntities() {
        return CACHE_OF_LIVING_ENTITIES;
    }

    public void cacheEntityIn(EntityReg<?> entityReg) {
        CACHE_OF_ENTITIES.add(entityReg);
    }

    public void onEntityRendererReg() {
        for(EntityReg<?> entityReg : CACHE_OF_ENTITIES) {
            entityReg.registerRenderer();
        }
        for(String key: CACHE_OF_BLOCK_ENTITIES.keySet()) {
            CACHE_OF_BLOCK_ENTITIES.get(key).registerRenderer(this);
        }
        CACHE_OF_BLOCK_ENTITIES.clear();
        CACHE_OF_ENTITIES.clear();
    }

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

    public ResourceLocation asResource(String path) {
        return new ResourceLocation(namespace, path);
    }



    public void submit() {
        SOUNDS.register(eventBus);
        BLOCK.register(eventBus);
        ITEMS.register(eventBus);
        FLUID_TYPE.register(eventBus);
        FLUID.register(eventBus);
        for(String key : CACHE_OF_BLOCK_ENTITIES.keySet()) {CACHE_OF_BLOCK_ENTITIES.get(key).submit(this);}
        for(String key : CACHE_OF_MENUS.keySet()) {CACHE_OF_MENUS.get(key).submit(this);}
        CACHE_OF_MENUS.clear();
        BLOCK_ENTITIES.register(eventBus);
        MENUS.register(eventBus);
        ENTITIES.register(eventBus);
        RECIPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
        KasugaLib.STACKS.stackIn(this);
    }
}
