package kasuga.lib.registrations.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import kasuga.lib.registrations.create.InteractionMovementReg;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.LinkedList;

public class CreateRegistry extends SimpleRegistry {
    private final CreateRegistrate createRegistry;
    private final LinkedList<InteractionMovementReg> movements;
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
        movements = new LinkedList<>();
        createRegistry = CreateRegistrate.create(namespace);
        bus.addListener(this::onSetup);
    }

    public void cacheMovementIn(InteractionMovementReg movementReg) {
        this.movements.add(movementReg);
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
