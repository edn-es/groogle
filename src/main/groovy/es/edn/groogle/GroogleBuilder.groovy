package es.edn.groogle

import es.edn.groogle.core.GroovyGoogle
import groovy.transform.CompileStatic

import java.util.function.Consumer
import java.util.function.Function

@CompileStatic
class GroogleBuilder {

    private static Closure noOp = {}

    private static Groogle newGroogle(Groogle groogle){
        return groogle;
    }

    private static final Function<Groogle, ? extends Groogle> factory = { new GroogleBuilder() } as Function;

    static Groogle build(@DelegatesTo(Groogle.class) final Closure closure){
        build(factory, closure);
    }

    static Groogle build(Consumer<Groogle> consumer){
        GroovyGoogle impl = new GroovyGoogle()
        consumer.accept(impl)
        impl
    }

    static Groogle build(final Function<Groogle, ? extends Groogle> factory) {
        build(factory, noOp);
    }

    static Groogle build(final Function<Groogle, ? extends Groogle> factory, @DelegatesTo(Groogle.class) final Closure closure) {
        GroovyGoogle impl = new GroovyGoogle()
        closure.delegate=impl
        closure.resolveStrategy=Closure.DELEGATE_FIRST
        closure();
        return run(factory,impl);
    }

    static Groogle evaluate( File txt ){
        evaluate( txt.text)
    }

    static Groogle evaluate( String txt ){
        String evaluate = """{ script-> script.with{ $txt } }"""
        Closure closure = new GroovyShell().evaluate(evaluate) as Closure
        return build(closure)
    }

    static Groogle run(final Function<Groogle, ? extends Groogle> factory, final GroovyGoogle groogle){
        factory.apply(groogle);
        return groogle;
    }
}
