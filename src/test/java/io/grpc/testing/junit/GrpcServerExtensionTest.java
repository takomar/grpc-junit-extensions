package io.grpc.testing.junit;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

class GrpcServerExtensionTest {
    @Test
    void test_exception_when_added_statically() throws NoSuchMethodException {
        Constructor<GrpcServerExtension> constructor = GrpcServerExtension.class.getDeclaredConstructor();
        Truth.assertThat(Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        Exception exception = Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
        Truth.assertThat(exception.getCause()).isInstanceOf(IllegalStateException.class);
        Truth.assertThat(exception.getCause().getMessage().contains("JerseyExtension must be registered programmatically"));
    }
}
