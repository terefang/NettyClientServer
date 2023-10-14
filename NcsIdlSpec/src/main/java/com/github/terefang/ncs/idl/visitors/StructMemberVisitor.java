package com.github.terefang.ncs.idl.visitors;

import com.github.terefang.ncs.idl.IdlObjUtil;
import com.github.terefang.ncs.idl.objects.ParamSpec;
import com.github.terefang.ncs.idl.spec.syntaxtree.*;

public class StructMemberVisitor extends AbstractVisitor implements Extractor<StructMember, ParamSpec> {

    public static ParamSpec from(StructMember object)
    {
        return new StructMemberVisitor().extract(object);
    }

    ParamSpec spec = new ParamSpec();

    @Override
    public ParamSpec extract(StructMember object) {
        object.accept(this);
        return this.spec;
    }

    @Override
    public void visit(StructMember structMember) {
        // TYPE = structMember.f1
        // NAME = structMember.f2

        this.spec.setParamType(IdlObjUtil.from(structMember.f1));

        this.spec.setParamName(((NodeToken)structMember.f2.f0.f1.f0.choice).tokenImage);
        if(structMember.f2.f0.f1.f1.present())
        {
            if(structMember.f2.f0.f1.f1.nodes.get(0) instanceof ArraySpecification) {
                this.spec.setArrayType(true);
            }
        }
    }

}
