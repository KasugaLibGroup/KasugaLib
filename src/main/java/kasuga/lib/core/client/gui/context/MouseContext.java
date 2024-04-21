package kasuga.lib.core.client.gui.context;

public interface MouseContext {
    int x();
    int y();

    public static class EmptyMouseContext implements MouseContext{

        @Override
        public int x() {
            return 0;
        }

        @Override
        public int y() {
            return 0;
        }
    }

    public static EmptyMouseContext EMPTY = new EmptyMouseContext();
}
