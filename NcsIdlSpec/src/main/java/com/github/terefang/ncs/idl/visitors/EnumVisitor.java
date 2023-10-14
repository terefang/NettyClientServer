package com.github.terefang.ncs.idl.visitors;

import com.github.terefang.ncs.idl.objects.EnumSpec;
import com.github.terefang.ncs.idl.spec.syntaxtree.*;

public class EnumVisitor extends AbstractVisitor implements Extractor<EnumDefinition, EnumSpec> {

    public static EnumSpec from(EnumDefinition object)
    {
        return new EnumVisitor().extract(object);
    }

    EnumSpec spec = new EnumSpec();

    @Override
    public EnumSpec extract(EnumDefinition object)
    {
        object.accept(this);
        return this.spec;
    }

    @Override
    public void visit(EnumDefinition enumDefinition) {
        this.spec.setEnumName(((NodeToken)enumDefinition.f1.node).tokenImage);
        enumDefinition.f3.accept(this);
    }

    @Override
    public void visit(EnumMemberList enumMemberList) {
        enumMemberList.f0.accept(this);

        if(enumMemberList.f1.present())
        {
            for(Node node : enumMemberList.f1.nodes)
            {
                node.accept(this);
            }
        }
    }

    @Override
    public void visit(EnumMember enumMember) {
        this.spec.getEnumList().add(enumMember.f1.tokenImage);
    }

}
