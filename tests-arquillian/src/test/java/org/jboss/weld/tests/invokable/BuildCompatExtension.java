package org.jboss.weld.tests.invokable;

import java.util.Collection;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.build.compatible.spi.BeanInfo;
import jakarta.enterprise.inject.build.compatible.spi.BuildCompatibleExtension;
import jakarta.enterprise.inject.build.compatible.spi.InvokerFactory;
import jakarta.enterprise.inject.build.compatible.spi.InvokerInfo;
import jakarta.enterprise.inject.build.compatible.spi.Parameters;
import jakarta.enterprise.inject.build.compatible.spi.Registration;
import jakarta.enterprise.inject.build.compatible.spi.Synthesis;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanCreator;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticComponents;
import jakarta.enterprise.invoke.Invoker;
import jakarta.enterprise.lang.model.declarations.MethodInfo;

import org.jboss.weld.invoke.WeldInvokerBuilder;
import org.jboss.weld.tests.invokable.common.ArgTransformer;
import org.jboss.weld.tests.invokable.common.ExceptionTransformer;
import org.jboss.weld.tests.invokable.common.FooArg;
import org.jboss.weld.tests.invokable.common.InstanceTransformer;
import org.jboss.weld.tests.invokable.common.InvocationWrapper;
import org.jboss.weld.tests.invokable.common.ReturnValueTransformer;
import org.jboss.weld.tests.invokable.common.SimpleBean;
import org.jboss.weld.tests.invokable.common.TransformableBean;
import org.jboss.weld.tests.invokable.common.TrulyExceptionalBean;
import org.junit.Assert;

public class BuildCompatExtension implements BuildCompatibleExtension {

    // basic invokers, some with lookup
    private InvokerInfo noTransformationInvoker;
    private InvokerInfo instanceLookupInvoker;
    private InvokerInfo argLookupInvoker;
    private InvokerInfo lookupAllInvoker;
    private InvokerInfo staticNoTransformationInvoker;
    private InvokerInfo staticInstanceLookupInvoker;
    private InvokerInfo staticArgLookupInvoker;
    private InvokerInfo staticLookupAllInvoker;

    // method arg transformers
    private InvokerInfo argTransformingInvoker;
    private InvokerInfo staticArgTransformingInvoker;
    private InvokerInfo argTransformerWithConsumerInvoker;
    private InvokerInfo staticArgTransformerWithConsumerInvoker;

    // instance transformers
    private InvokerInfo instanceTransformerInvoker;
    private InvokerInfo instanceTransformerWithConsumerInvoker;
    private InvokerInfo instanceTransformerNoParamInvoker;

    // return value transformers
    private InvokerInfo returnTransformerInvoker;
    private InvokerInfo returnTransformerNoParamInvoker;
    private InvokerInfo staticReturnTransformerInvoker;
    private InvokerInfo staticReturnTransformerNoParamInvoker;

    // exception transformers
    private InvokerInfo exceptionTransformerInvoker;
    private InvokerInfo staticExceptionTransformerInvoker;

    // invocation wrapper
    private InvokerInfo invocationWrapperInvoker;
    private InvokerInfo staticInvocationWrapperInvoker;

    // Param keys for invokers
    // basic invokers, some with lookup
    public static String noTransformationInvokerString = "noTransformationInvoker";
    public static String instanceLookupInvokerString = "instanceLookupInvoker";
    public static String argLookupInvokerString = "argLookupInvoker";
    public static String lookupAllInvokerString = "lookupAllInvoker";
    public static String staticNoTransformationInvokerString = "staticNoTransformationInvoker";
    public static String staticInstanceLookupInvokerString = "staticInstanceLookupInvoker";
    public static String staticArgLookupInvokerString = "staticArgLookupInvoker";
    public static String staticLookupAllInvokerString = "staticLookupAllInvoker";

    // method arg transformers
    public static String argTransformingInvokerString = "argTransformingInvoker";
    public static String staticArgTransformingInvokerString = "staticArgTransformingInvoker";
    public static String argTransformerWithConsumerInvokerString = "argTransformerWithConsumerInvoker";
    public static String staticArgTransformerWithConsumerInvokerString = "staticArgTransformerWithConsumerInvoker";

    // instance transformers
    public static String instanceTransformerInvokerString = "instanceTransformerInvoker";
    public static String instanceTransformerWithConsumerInvokerString = "instanceTransformerWithConsumerInvoker";
    public static String instanceTransformerNoParamInvokerString = "instanceTransformerNoParamInvoker";

    // return value transformers
    public static String returnTransformerInvokerString = "returnTransformerInvoker";
    public static String returnTransformerNoParamInvokerString = "returnTransformerNoParamInvoker";
    public static String staticReturnTransformerInvokerString = "staticReturnTransformerInvoker";
    public static String staticReturnTransformerNoParamInvokerString = "staticReturnTransformerNoParamInvoker";

    // exception transformers
    public static String exceptionTransformerInvokerString = "exceptionTransformerInvoker";
    public static String staticExceptionTransformerInvokerString = "staticExceptionTransformerInvoker";

    // invocation wrapper
    public static String invocationWrapperInvokerString = "invocationWrapperInvoker";
    public static String staticInvocationWrapperInvokerString = "staticInvocationWrapperInvoker";

    @Synthesis
    public void synth(SyntheticComponents syntheticComponents) {
        // create synth beans that has all the invokers as params
        syntheticComponents.addBean(SynthBean.class)
                .createWith(SynthBeanCreator.class)
                .type(SynthBean.class)
                .scope(ApplicationScoped.class)
                .withParam(noTransformationInvokerString, noTransformationInvoker)
                .withParam(instanceLookupInvokerString, instanceLookupInvoker)
                .withParam(argLookupInvokerString, argLookupInvoker)
                .withParam(lookupAllInvokerString, lookupAllInvoker)
                .withParam(staticNoTransformationInvokerString, staticNoTransformationInvoker)
                .withParam(staticInstanceLookupInvokerString, staticInstanceLookupInvoker)
                .withParam(staticArgLookupInvokerString, staticArgLookupInvoker)
                .withParam(staticLookupAllInvokerString, staticLookupAllInvoker)
                .withParam(argTransformingInvokerString, argTransformingInvoker)
                .withParam(staticArgTransformingInvokerString, staticArgTransformingInvoker)
                .withParam(argTransformerWithConsumerInvokerString, argTransformerWithConsumerInvoker)
                .withParam(staticArgTransformerWithConsumerInvokerString, staticArgTransformerWithConsumerInvoker)
                .withParam(instanceTransformerInvokerString, instanceTransformerInvoker)
                .withParam(instanceTransformerWithConsumerInvokerString, instanceTransformerWithConsumerInvoker)
                .withParam(instanceTransformerNoParamInvokerString, instanceTransformerNoParamInvoker)
                .withParam(returnTransformerInvokerString, returnTransformerInvoker)
                .withParam(returnTransformerNoParamInvokerString, returnTransformerNoParamInvoker)
                .withParam(staticReturnTransformerInvokerString, staticReturnTransformerInvoker)
                .withParam(staticReturnTransformerNoParamInvokerString, staticReturnTransformerNoParamInvoker)
                .withParam(exceptionTransformerInvokerString, exceptionTransformerInvoker)
                .withParam(staticExceptionTransformerInvokerString, staticExceptionTransformerInvoker)
                .withParam(invocationWrapperInvokerString, invocationWrapperInvoker)
                .withParam(staticInvocationWrapperInvokerString, staticInvocationWrapperInvoker);
    }

    @Registration(types = SimpleBean.class)
    public void createNoTransformationInvokers(BeanInfo b, InvokerFactory invokers) {
        Collection<MethodInfo> invokableMethods = b.declaringClass().methods();
        Assert.assertEquals(4, invokableMethods.size());
        for (MethodInfo invokableMethod : invokableMethods) {
            if (invokableMethod.name().contains("staticPing")) {
                staticNoTransformationInvoker = invokers.createInvoker(b, invokableMethod).build();
                staticInstanceLookupInvoker = invokers.createInvoker(b, invokableMethod).withInstanceLookup().build();
                staticArgLookupInvoker = invokers.createInvoker(b, invokableMethod).withArgumentLookup(0).withArgumentLookup(1)
                        .build();
                staticLookupAllInvoker = invokers.createInvoker(b, invokableMethod).withArgumentLookup(0).withArgumentLookup(1)
                        .withInstanceLookup().build();
            } else if (invokableMethod.name().contains("ping")) {
                noTransformationInvoker = invokers.createInvoker(b, invokableMethod).build();
                instanceLookupInvoker = invokers.createInvoker(b, invokableMethod).withInstanceLookup().build();
                argLookupInvoker = invokers.createInvoker(b, invokableMethod).withArgumentLookup(0).withArgumentLookup(1)
                        .build();
                lookupAllInvoker = invokers.createInvoker(b, invokableMethod).withArgumentLookup(0).withArgumentLookup(1)
                        .withInstanceLookup().build();
            }
        }
    }

    @Registration(types = TransformableBean.class)
    public void createArgTransformationInvokers(BeanInfo b, InvokerFactory invokers) {
        Collection<MethodInfo> invokableMethods = b.declaringClass().methods();
        Assert.assertEquals(4, invokableMethods.size());
        for (MethodInfo invokableMethod : invokableMethods) {
            if (invokableMethod.name().contains("staticPing")) {
                staticArgTransformingInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b, invokableMethod))
                        .withArgumentTransformer(0, FooArg.class, "doubleTheString") // non-static Transformer method
                        .withArgumentTransformer(1, ArgTransformer.class, "transform") // static Transformer method
                        .build();
                staticArgTransformerWithConsumerInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b,
                        invokableMethod))
                        .withArgumentTransformer(0, FooArg.class, "doubleTheString") // non-static Transformer method
                        .withArgumentTransformer(1, ArgTransformer.class, "transform2") // static Transformer method with Consumer
                        .build();
            } else if (invokableMethod.name().contains("ping")) {
                argTransformingInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b, invokableMethod))
                        .withArgumentTransformer(0, FooArg.class, "doubleTheString") // non-static Transformer method
                        .withArgumentTransformer(1, ArgTransformer.class, "transform") // static Transformer method
                        .build();
                argTransformerWithConsumerInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b,
                        invokableMethod))
                        .withArgumentTransformer(0, FooArg.class, "doubleTheString") // non-static Transformer method
                        .withArgumentTransformer(1, ArgTransformer.class, "transform2") // static Transformer method with Consumer
                        .build();
            }
        }
    }

    @Registration(types = TransformableBean.class)
    public void createInstanceTransformationInvokers(BeanInfo b, InvokerFactory invokers) {
        Collection<MethodInfo> invokableMethods = b.declaringClass().methods();
        Assert.assertEquals(4, invokableMethods.size());
        for (MethodInfo invokableMethod : invokableMethods) {
            if (invokableMethod.name().contains("ping")) {
                instanceTransformerInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b, invokableMethod))
                        .withInstanceTransformer(InstanceTransformer.class, "transform")
                        .build();
                instanceTransformerWithConsumerInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b,
                        invokableMethod))
                        .withInstanceTransformer(InstanceTransformer.class, "transform2")
                        .build();
                instanceTransformerNoParamInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b,
                        invokableMethod))
                        .withInstanceTransformer(TransformableBean.class, "setTransformed")
                        .build();
            }
        }
    }

    @Registration(types = TransformableBean.class)
    public void createReturnValueTransformationInvokers(BeanInfo b, InvokerFactory invokers) {
        Collection<MethodInfo> invokableMethods = b.declaringClass().methods();
        Assert.assertEquals(4, invokableMethods.size());
        for (MethodInfo invokableMethod : invokableMethods) {
            if (invokableMethod.name().contains("ping")) {
                returnTransformerInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b, invokableMethod))
                        .withReturnValueTransformer(ReturnValueTransformer.class, "transform")
                        .build();
                returnTransformerNoParamInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b, invokableMethod))
                        .withReturnValueTransformer(String.class, "strip")
                        .build();

            } else if (invokableMethod.name().contains("staticPing")) {
                staticReturnTransformerInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b, invokableMethod))
                        .withReturnValueTransformer(ReturnValueTransformer.class, "transform")
                        .build();
                staticReturnTransformerNoParamInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b,
                        invokableMethod))
                        .withReturnValueTransformer(String.class, "strip")
                        .build();
            }
        }
    }

    @Registration(types = TrulyExceptionalBean.class)
    public void createExceptionTransformationInvokers(BeanInfo b, InvokerFactory invokers) {
        Collection<MethodInfo> invokableMethods = b.declaringClass().methods();
        Assert.assertEquals(2, invokableMethods.size());
        for (MethodInfo invokableMethod : invokableMethods) {
            if (invokableMethod.name().contains("ping")) {
                exceptionTransformerInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b, invokableMethod))
                        .withExceptionTransformer(ExceptionTransformer.class, "transform")
                        .build();

            } else if (invokableMethod.name().contains("staticPing")) {
                staticExceptionTransformerInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b,
                        invokableMethod))
                        .withExceptionTransformer(ExceptionTransformer.class, "transform")
                        .build();
            }
        }
    }

    @Registration(types = SimpleBean.class)
    public void createInvocationWrapperInvokers(BeanInfo b, InvokerFactory invokers) {
        Collection<MethodInfo> invokableMethods = b.declaringClass().methods();
        Assert.assertEquals(4, invokableMethods.size());
        for (MethodInfo invokableMethod : invokableMethods) {
            if (invokableMethod.name().contains("ping")) {
                invocationWrapperInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b, invokableMethod))
                        .withInvocationWrapper(InvocationWrapper.class, "transform")
                        .build();

            } else if (invokableMethod.name().contains("staticPing")) {
                staticInvocationWrapperInvoker = ((WeldInvokerBuilder<InvokerInfo>) invokers.createInvoker(b, invokableMethod))
                        .withInvocationWrapper(InvocationWrapper.class, "transform")
                        .build();
            }
        }
    }

    public static class SynthBeanCreator implements SyntheticBeanCreator<SynthBean> {

        @Override
        public SynthBean create(Instance<Object> instance, Parameters parameters) {
            SynthBean result = new SynthBean();
            result.setNoTransformationInvoker(parameters.get(noTransformationInvokerString, Invoker.class));
            result.setInstanceLookupInvoker(parameters.get(instanceLookupInvokerString, Invoker.class));
            result.setArgLookupInvoker(parameters.get(argLookupInvokerString, Invoker.class));
            result.setLookupAllInvoker(parameters.get(lookupAllInvokerString, Invoker.class));
            result.setStaticNoTransformationInvoker(parameters.get(staticNoTransformationInvokerString, Invoker.class));
            result.setStaticInstanceLookupInvoker(parameters.get(staticInstanceLookupInvokerString, Invoker.class));
            result.setStaticArgLookupInvoker(parameters.get(staticArgLookupInvokerString, Invoker.class));
            result.setStaticLookupAllInvoker(parameters.get(staticLookupAllInvokerString, Invoker.class));
            result.setArgTransformingInvoker(parameters.get(argTransformingInvokerString, Invoker.class));
            result.setStaticArgTransformingInvoker(parameters.get(staticArgTransformingInvokerString, Invoker.class));
            result.setArgTransformerWithConsumerInvoker(parameters.get(argTransformerWithConsumerInvokerString, Invoker.class));
            result.setStaticArgTransformerWithConsumerInvoker(
                    parameters.get(staticArgTransformerWithConsumerInvokerString, Invoker.class));
            result.setInstanceTransformerInvoker(parameters.get(instanceTransformerInvokerString, Invoker.class));
            result.setInstanceTransformerWithConsumerInvoker(
                    parameters.get(instanceTransformerWithConsumerInvokerString, Invoker.class));
            result.setInstanceTransformerNoParamInvoker(parameters.get(instanceTransformerNoParamInvokerString, Invoker.class));
            result.setReturnTransformerInvoker(parameters.get(returnTransformerInvokerString, Invoker.class));
            result.setReturnTransformerNoParamInvoker(parameters.get(returnTransformerNoParamInvokerString, Invoker.class));
            result.setStaticReturnTransformerInvoker(parameters.get(staticReturnTransformerInvokerString, Invoker.class));
            result.setStaticReturnTransformerNoParamInvoker(
                    parameters.get(staticReturnTransformerNoParamInvokerString, Invoker.class));
            result.setExceptionTransformerInvoker(parameters.get(exceptionTransformerInvokerString, Invoker.class));
            result.setStaticExceptionTransformerInvoker(parameters.get(staticExceptionTransformerInvokerString, Invoker.class));
            result.setInvocationWrapperInvoker(parameters.get(invocationWrapperInvokerString, Invoker.class));
            result.setStaticInvocationWrapperInvoker(parameters.get(staticInvocationWrapperInvokerString, Invoker.class));
            return result;
        }
    }
}
