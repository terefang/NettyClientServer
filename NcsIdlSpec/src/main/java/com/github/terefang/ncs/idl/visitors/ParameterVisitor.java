package com.github.terefang.ncs.idl.visitors;

import com.github.terefang.ncs.idl.IdlObjUtil;
import com.github.terefang.ncs.idl.objects.AnnotSpec;
import com.github.terefang.ncs.idl.objects.ParamSpec;
import com.github.terefang.ncs.idl.spec.syntaxtree.*;

import java.util.List;
import java.util.Vector;

public class ParameterVisitor extends AbstractVisitor implements Extractor<Parameters, List<ParamSpec>> {

    public static List<ParamSpec> from(Parameters parameters)
    {
        return new ParameterVisitor().extract(parameters);
    }

    List<ParamSpec> plist = new Vector();

    @Override
    public List<ParamSpec> extract(Parameters object)
    {
        object.accept(this);
        return this.plist;
    }

    @Override
    public void visit(Parameters parameters) {
        parameters.f0.choice.accept(this);
    }

    @Override
    public void visit(ParameterList parameterList) {
        parameterList.f0.accept(this);
        if(parameterList.f1.present())
        {
            for(Node node : parameterList.f1.nodes)
            {
                node.accept(this);
            }
        }
        if(parameterList.f2.present())
        {
            parameterList.f2.node.accept(this);

        }
    }

    @Override
    public void visit(Parameter parameter)
    {
        ParamSpec spec = new ParamSpec();
        List<AnnotSpec> an = IdlObjUtil.from(parameter.f0);
        spec.getAnnotList().addAll(an);
        spec.setParamType(IdlObjUtil.from(parameter.f2));

        //if(structMember.f2.f0.f1.f1.present())
        //{
        //    if(structMember.f2.f0.f1.f1.nodes.get(0) instanceof ArraySpecification) {
        //        this.spec.setArrayType(true);
        //    }
        //}
    }

}
