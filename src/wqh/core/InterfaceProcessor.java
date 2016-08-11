package wqh.core;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * http://www.race604.com/annotation-processing/
 * http://blog.csdn.net/xfxyy_sxfancy/article/details/44275549#t3
 *
 * Element代表程序的元素，例如包、类或者方法。每个Element代表一个静态的、语言级别的构件。
 * 可以理解为一个签名(public class Main  或者 private void fun(int a) .....)
 *
 * Created on 2016/8/10.
 *
 * @author 王启航
 * @version 1.0
 */
public class InterfaceProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;


    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        elementUtils = env.getElementUtils();
        filer = env.getFiler();
        typeUtils = env.getTypeUtils();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Map<String, AnnotatedClass> classMap = new HashMap<>();

        // 得到所有注解@Interface的Element集合
        Set<? extends Element> elementSet = env.getElementsAnnotatedWith(Interface.class);
        /*
         * 构成如下的Map: classMap
         * key -> 类名
         * value -> AnnotatedClass
         *
         * AnnotatedClass结构如下
         * key -> 类名
         * value -> 要生成的方法
         */
        for (Element e : elementSet) {
            if (e.getKind() != ElementKind.METHOD) {
                error(e, "错误的注解类型，只有函数能够被该 @%s 注解处理", Interface.class.getSimpleName());
                return true;
            }

            ExecutableElement element = (ExecutableElement) e;
            AnnotatedMethod annotatedMethod = new AnnotatedMethod(element);

            String classname = annotatedMethod.getSimpleClassName();
            AnnotatedClass annotatedClass = classMap.get(classname);
            if (annotatedClass == null) {
                PackageElement pkg = elementUtils.getPackageOf(element);
                annotatedClass = new AnnotatedClass(pkg.getQualifiedName().toString(), element.getAnnotation(Interface.class).value());
                annotatedClass.addMethod(annotatedMethod);
                classMap.put(classname, annotatedClass);
            } else
                annotatedClass.addMethod(annotatedMethod);

        }
        // 代码生成
        for (AnnotatedClass annotatedClass : classMap.values()) {
            annotatedClass.generateCode(elementUtils, filer);
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new TreeSet<>();
        types.add(Interface.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /*
     * 注解处理器程序(APT)必须完成运行而不崩溃.
     */
    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

}
