package kasuga.lib.core.client.render.texture;

import java.util.function.Supplier;

public class Matrix<T> {
    private final Object[][] matrix;
    private Supplier<T> defaultElement;

    public Matrix(int row, int colum, Supplier<T> defaultElement) {
        this(row, colum);
        this.defaultElement = defaultElement;
    }

    public Matrix(int row, int column) {
        matrix = new Object[row][column];
        this.defaultElement = () -> null;
    }

    public Matrix(int a) {
        matrix = new Object[a][a];
        this.defaultElement = () -> null;
    }

    public Matrix(int a, Supplier<T> defaultElement) {
        this(a);
        this.defaultElement = defaultElement;
    }

    public Supplier<T> getDefaultElement() {
        return defaultElement;
    }

    public void setDefaultElement(Supplier<T> defaultElement) {
        this.defaultElement = defaultElement;
    }

    public T get(int row, int column) {
        if (matrix[row - 1][column - 1] == null)
            return defaultElement.get();
        return (T) matrix[row - 1][column - 1];
    }

    public void set(int row, int column, T element) {
        matrix[row - 1][column - 1] = element;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("matrix<")
                .append(matrix.length).append('*').append(matrix[0].length).append('>').append('\n');
        for (Object[] vector : matrix) {
            builder.append('|');
            for (Object obj : vector) {
                builder.append(' ').append(obj).append(' ');
            }
            builder.append('|').append('\n');
        }
        return builder.toString();
    }
}
