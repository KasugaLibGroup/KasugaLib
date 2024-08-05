package kasuga.lib.registrations.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraftforge.eventbus.api.IEventBus;

public class CreateRegistry extends SimpleRegistry {
    private final CreateRegistrate createRegistry;
    /**
     * This constructor is used for create a new KasugaLib registration.
     *
     * @param namespace your mod namespace name
     * @param bus       your mod namespace eventbus. For more info see
     *                  {@link FMLJavaModLoadingContext#get()}
     *                  and
     *                  {@link FMLJavaModLoadingContext#getModEventBus()}
     */
    public CreateRegistry(String namespace, IEventBus bus) {
        super(namespace, bus);
        createRegistry = CreateRegistrate.create(namespace);
    }

    public CreateRegistrate createRegistry() {
        return createRegistry;
    }

    @Override
    public void submit() {
        super.submit();
        createRegistry.registerEventListeners(eventBus);
    }

    protected void onSetup(final FMLCommonSetupEvent event) {
        for (InteractionMovementReg movementReg : movements)
            movementReg.onSetup();
    }
}
