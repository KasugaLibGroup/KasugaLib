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
     * @param bus       mod bus
     */
    public CreateRegistry(String namespace, IEventBus bus) {
        super(namespace, bus);
        createRegistry = CreateRegistrate.create(namespace);
        movements = new LinkedList<>();
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
