package com.github.dynamicextensionsalfresco.blueprint;

import com.github.dynamicextensionsalfresco.BeanNames;
import com.github.dynamicextensionsalfresco.schedule.quartz2.Quartz2TaskScheduler;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.util.VersionNumber;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DynamicExtensionsApplicationContextTest {
    public static class InfrastructureBeansTest {
        @Test
        public void testRegisterTaskSchedulingBeans_alfresco6x() {
            DynamicExtensionsApplicationContext context = new DynamicExtensionsApplicationContext(new String[0], null);

            DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
            Descriptor descriptor = mock(Descriptor.class);

            when(descriptor.getVersionNumber()).thenReturn(new VersionNumber("6.0.4"));
            context.registerTaskSchedulingBeans(beanFactory, descriptor);

            // Verify invocations on the mock
            ArgumentCaptor<BeanDefinition> beanDefCaptor = ArgumentCaptor.forClass(BeanDefinition.class);
            verify(beanFactory, times(1))
                    .registerBeanDefinition(eq(BeanNames.QUARTZ_TASK_SCHEDULER.id()), beanDefCaptor.capture());

            // Expecting class Quartz2TaskScheduler on Alfresco 6.x
            assertThat(beanDefCaptor.getValue().getBeanClassName(), is(Quartz2TaskScheduler.class.getName()));
        }
    }
}