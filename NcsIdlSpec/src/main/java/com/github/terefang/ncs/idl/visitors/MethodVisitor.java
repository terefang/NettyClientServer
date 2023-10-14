package com.github.terefang.ncs.idl.visitors;

import com.github.terefang.ncs.idl.IdlObjUtil;
import com.github.terefang.ncs.idl.objects.MethodSpec;
import com.github.terefang.ncs.idl.objects.ParamSpec;
import com.github.terefang.ncs.idl.spec.syntaxtree.*;

import java.util.List;

public class MethodVisitor extends AbstractVisitor implements Extractor<MethodDefinition, MethodSpec> {

    public static MethodSpec from(MethodDefinition object)
    {
        return new MethodVisitor().extract(object);
    }

    MethodSpec spec = new MethodSpec();

    @Override
    public MethodSpec extract(MethodDefinition object)
    {
        object.accept(this);
        return this.spec;
    }

    @Override
    public void visit(MethodDefinition methodDefinition)
    {
        this.spec.setMethodName(methodDefinition.f3.tokenImage);

        Node node = methodDefinition.f1.f1.f0.choice;

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
            this.spec.setMethodType(((NodeToken)node).tokenImage);
        }
        else
        {
            this.spec.setMethodType("Object");
        }

        if(methodDefinition.f1.f1.f1.present())
        {
            if(methodDefinition.f1.f1.f1.node instanceof ArrayTypeSpecification)
            {
                this.spec.setMethodArrayType(true);
            }
        }

        List<ParamSpec> plist = IdlObjUtil.from(methodDefinition.f4);

        this.spec.getParamList().addAll(plist);
    }

}
