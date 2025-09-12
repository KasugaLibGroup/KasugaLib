package kasuga.lib.core.compat;


import kasuga.lib.core.compat.iris.IrisOculusCompat;
import kasuga.lib.core.compat.iris.IrisOculusCompatImpl;
import kasuga.lib.core.util.Envs;
import kasuga.lib.core.util.MSupplier;
import net.minecraftforge.fml.ModList;

import java.util.Optional;
import java.util.function.Supplier;

public class AllCompats {
    public Optional<IrisOculusCompat> IRIS_OCULUS =
            ((Optional<MSupplier<IrisOculusCompat>>) (isIrisOculusCompatEnabled() ? Optional.of((MSupplier<IrisOculusCompat>)()->IrisOculusCompatImpl::new): Optional.empty()))
            .map(Supplier::get).map(Supplier::get);


    protected boolean isIrisOculusCompatEnabled(){
        return !Envs.isDedicateServer() && (ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus"));
    }
}
