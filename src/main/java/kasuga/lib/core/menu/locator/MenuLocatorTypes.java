package kasuga.lib.core.menu.locator;

public class MenuLocatorTypes {
    public static MenuLocatorType<BlockMenuLocator> BLOCK = new MenuLocatorType<>(BlockMenuLocator::new);
    public static MenuLocatorType<EntityMenuLocator> ENTITY = new MenuLocatorType<>(EntityMenuLocator::new);
}
