package com.github.terefang.ncs.idl.visitors;

import com.github.terefang.ncs.idl.IdlObjUtil;
import com.github.terefang.ncs.idl.objects.ParamSpec;
import com.github.terefang.ncs.idl.objects.StructSpec;
import com.github.terefang.ncs.idl.spec.syntaxtree.*;

public class StructVisitor extends AbstractVisitor implements Extractor<StructOrUnionDefinition, StructSpec> {

    public static StructSpec from(StructOrUnionDefinition object)
    {
        return new StructVisitor().extract(object);
    }

    StructSpec spec = new StructSpec();

    @Override
    public StructSpec extract(StructOrUnionDefinition object) {
        object.accept(this);
        return this.spec;
    }

    @Override
    public void visit(StructOrUnionDefinition structOrUnionDefinition) {
        if(structOrUnionDefinition.f1.present())
        {
            this.spec.setStructName(((NodeToken)structOrUnionDefinition.f1.node).tokenImage);
        }
        structOrUnionDefinition.f3.accept(this);
    }

    @Override
    public void visit(StructMemberList structMemberList) {
        if(structMemberList.f0.present())
        {
            for(Node node : structMemberList.f0.nodes)
            {
                node.accept(this);
            }
        }
    }

    @Override
    public void visit(StructMember structMember) {
        ParamSpec s = IdlObjUtil.from(structMember);
        this.spec.getParamList().add(s);
    }

}
