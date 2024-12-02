package kasuga.lib.core.menu.locator;

public class MenuLocatorTypes {
    public static MenuLocatorType<BlockMenuLocator> CHUNK_MENU = new MenuLocatorType<>(BlockMenuLocator::new);
    public static MenuLocatorType<EntityMenuLocator> ENTITY = new MenuLocatorType<>(EntityMenuLocator::new);

    public static MenuLocatorType<ContraptionBlockMenuLocator> CONTRAPTION = new MenuLocatorType<>(ContraptionBlockMenuLocator::new);
}
