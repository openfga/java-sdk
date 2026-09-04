package dev.openfga.sdk.api.client;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/** Preserves a generic Java type for response deserialization. */
public abstract class SdkTypeToken<T> {
    private final Type type;

    protected SdkTypeToken() {
        Type superclass = getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            throw new IllegalArgumentException("SdkTypeToken must include a type parameter");
        }
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    private SdkTypeToken(Type type) {
        this.type = type;
    }

    /** Creates a token for a non-generic class. */
    public static <T> SdkTypeToken<T> of(Class<T> type) {
        return new SimpleTypeToken<>(type);
    }

    static <T> SdkTypeToken<T> from(Type type) {
        return new SimpleTypeToken<>(type);
    }

    /** Creates a token for a parameterized type. */
    public static <T> SdkTypeToken<T> parameterized(Class<?> rawType, Type... typeArguments) {
        return new SimpleTypeToken<>(new ParameterizedTypeImpl(rawType, typeArguments));
    }

    /** Returns the captured Java type. */
    public Type getType() {
        return type;
    }

    private static final class SimpleTypeToken<T> extends SdkTypeToken<T> {
        private SimpleTypeToken(Type type) {
            super(type);
        }
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType {
        private final Class<?> rawType;
        private final Type[] typeArguments;

        private ParameterizedTypeImpl(Class<?> rawType, Type[] typeArguments) {
            this.rawType = rawType;
            this.typeArguments = typeArguments.clone();
        }

        @Override
        public Type[] getActualTypeArguments() {
            return typeArguments.clone();
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType that = (ParameterizedType) other;
            return rawType.equals(that.getRawType())
                    && that.getOwnerType() == null
                    && Arrays.equals(typeArguments, that.getActualTypeArguments());
        }

        @Override
        public int hashCode() {
            return rawType.hashCode() ^ Arrays.hashCode(typeArguments);
        }

        @Override
        public String getTypeName() {
            return rawType.getTypeName() + "<"
                    + String.join(
                            ", ",
                            Arrays.stream(typeArguments).map(Type::getTypeName).toArray(String[]::new)) + ">";
        }
    }
}
