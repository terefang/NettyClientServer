package com.github.terefang.ncs.idl;

import com.github.terefang.ncs.idl.objects.*;
import com.github.terefang.ncs.idl.spec.syntaxtree.*;
import com.github.terefang.ncs.idl.visitors.*;

import java.util.List;
import java.util.Map;

public class IdlObjUtil
{
    public static List<AnnotSpec> from(Annotations anno)
    {
        return AnnotVisitor.from(anno);
    }

    public static AnnotSpec from(Annotation anno)
    {
        int i = 0;
        AnnotSpec as = new AnnotSpec();
        as.setAnnotName(anno.f1.f0.tokenImage);
        if(anno.f2.present())
        {
            if(anno.f2.node instanceof AnnotationParameters)
            {
                Node node = ((NodeChoice) ((AnnotationParameters) anno.f2.node).f1.node).choice;
                if(node instanceof AnnotationValue)
                {
                    as.getAnnotParameter().addAll(AnnotationParamVisitor.from((AnnotationValue)node));
                }
                else
                if(node instanceof AnnotationValuePairs)
                {
                    Map<String, String> hash = AnnotationVpVisitor.from((AnnotationValuePairs) node);
                    as.getAnnotParamMap().putAll(hash);;
                }
            }
            else
            if(anno.f2.node instanceof NodeChoice)
            {
                if(((NodeChoice)anno.f2.node).choice instanceof AnnotationValuePairs)
                {
                    String some = ((AnnotationValuePairs)((NodeChoice)anno.f2.node).choice).f0.toString();
                    i++;
                }
            }
        }
        return as;
    }

    public static StructSpec from(List<AnnotSpec> annotationStack, StructOrUnionDefinition structNode)
    {
        StructSpec pStructSpec = StructVisitor.from(structNode);
        pStructSpec.getAnnotList().addAll(annotationStack);

        return pStructSpec;
    }


    public static EnumSpec from(List<AnnotSpec> annotationStack, EnumDefinition enumNode)
    {
        EnumSpec enumSpec = EnumVisitor.from(enumNode);
        //enumSpec.getAnnotList().addAll(annotationStack);
        return enumSpec;
    }

    public static ParamSpec from(StructMember structMember)
    {
        return StructMemberVisitor.from(structMember);
    }

    public static MethodSpec from(List<AnnotSpec> annotationStack, MethodDefinition md) {
        MethodSpec pMethodSpec = MethodVisitor.from(md);
        pMethodSpec.getAnnotList().addAll(annotationStack);
        return pMethodSpec;
    }

    public static List<ParamSpec> from(Parameters parameters)
    {
        return ParameterVisitor.from(parameters);
    }

    public static String from(QualifiedTypeSpecification qtype)
    {
        Node node = qtype.f1.f0.choice;

        if(node instanceof TypeSpecifiers)
        {
            node = ((TypeSpecifiers)node).f0.nodes.get(0);
            node = ((NodeChoice)node).choice;
        }
        else
        if(node instanceof TypeDefName)
        {
            node = ((TypeDefName)node).f0.f0;
        }
        else
        {
            node = null;
        }

        if(node!=null)
        {
            return ((NodeToken)node).tokenImage;
        }
        else
        {
            return "Object";
        }
    }

    public static TypeSpec from(TypeDefinition typeDefNode)
    {
        //typeDefNode.f0.choice
        return null;
    }
}
