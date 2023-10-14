package com.github.terefang.ncs.idl.visitors;

import com.github.terefang.ncs.idl.spec.syntaxtree.*;
import com.github.terefang.ncs.idl.spec.visitor.*;

public abstract class AbstractVisitor implements Visitor
{
    @Override
    public void visit(NodeList nodeList) {
        for(Node node : nodeList.nodes) {
            node.accept(this);
        }
    }

    @Override
    public void visit(NodeListOptional nodeListOptional)
    {
        if(nodeListOptional.present())
        {
            for(Node node : nodeListOptional.nodes) {
                node.accept(this);
            }
        }
    }

    @Override
    public void visit(NodeOptional nodeOptional) {
        if(nodeOptional.present())
        {
            nodeOptional.node.accept(this);
        }
    }

    @Override
    public void visit(NodeSequence nodeSequence)
    {
        for(Node node : nodeSequence.nodes)
        {
            node.accept(this);
        }
    }


    @Override
    public void visit(NodeToken n) {

    }

    @Override
    public void visit(ConstantDefinition n) {

    }

    @Override
    public void visit(TypeDefinition n) {

    }

    @Override
    public void visit(TypeDefSpecification n) {

    }

    @Override
    public void visit(QualifiedTypeSpecification n) {

    }

    @Override
    public void visit(TypeQualifier n) {

    }

    @Override
    public void visit(TypeSpecification n) {

    }

    @Override
    public void visit(ArrayTypeSpecification n) {

    }

    @Override
    public void visit(TypeDefName n) {

    }

    @Override
    public void visit(TypeSpecifiers n) {

    }

    @Override
    public void visit(StructOrUnionSpecification n) {

    }

    @Override
    public void visit(StructOrUnionDefinition n) {

    }

    @Override
    public void visit(StructOrUnionReference n) {

    }

    @Override
    public void visit(StructOrUnion n) {

    }

    @Override
    public void visit(StructMemberList n) {

    }

    @Override
    public void visit(StructMember n) {

    }

    @Override
    public void visit(EnumSpecification n) {

    }

    @Override
    public void visit(EnumDefinition n) {

    }

    @Override
    public void visit(EnumReference n) {

    }

    @Override
    public void visit(EnumMemberList n) {

    }

    @Override
    public void visit(EnumMember n) {

    }

    @Override
    public void visit(EnumValue n) {

    }

    @Override
    public void visit(Declarators n) {

    }

    @Override
    public void visit(Declarator n) {

    }

    @Override
    public void visit(PointerSpecification n) {

    }

    @Override
    public void visit(QualifierPointerSpecification n) {

    }

    @Override
    public void visit(DirectDeclarator n) {

    }

    @Override
    public void visit(ArraySpecification n) {

    }

    @Override
    public void visit(AbstractDeclarator n) {

    }

    @Override
    public void visit(AbstractDirectDeclarator n) {

    }

    @Override
    public void visit(InterfaceDefinition n) {

    }

    @Override
    public void visit(InterfaceInheritanceSpecification n) {

    }

    @Override
    public void visit(InterfaceBody n) {

    }

    @Override
    public void visit(MethodDefinition n) {

    }

    @Override
    public void visit(Parameters n) {

    }

    @Override
    public void visit(ParameterList n) {

    }

    @Override
    public void visit(Parameter n) {

    }

    @Override
    public void visit(ParameterQualifier n) {

    }

    @Override
    public void visit(ConstantExpression n) {

    }

    @Override
    public void visit(LogicalOrExpression n) {

    }

    @Override
    public void visit(LogicalAndExpression n) {

    }

    @Override
    public void visit(OrExpression n) {

    }

    @Override
    public void visit(XorExpression n) {

    }

    @Override
    public void visit(AndExpression n) {

    }

    @Override
    public void visit(ShiftExpression n) {

    }

    @Override
    public void visit(AdditiveExpression n) {

    }

    @Override
    public void visit(MulExpression n) {

    }

    @Override
    public void visit(CastExpression n) {

    }

    @Override
    public void visit(UnaryExpression n) {

    }

    @Override
    public void visit(PrimaryExpression n) {

    }

    @Override
    public void visit(Literal n) {

    }

    @Override
    public void visit(ShiftOperation n) {

    }

    @Override
    public void visit(AdditiveOperation n) {

    }

    @Override
    public void visit(MulOperation n) {

    }

    @Override
    public void visit(UnaryOperation n) {

    }

    @Override
    public void visit(Annotations n) {

    }

    @Override
    public void visit(Annotation n) {

    }

    @Override
    public void visit(AnnotationParameters n) {

    }

    @Override
    public void visit(AnnotationValuePairs n) {

    }

    @Override
    public void visit(AnnotationValuePair n) {

    }

    @Override
    public void visit(AnnotationValue n) {

    }

    @Override
    public void visit(AnnotationAnnotationValue n) {

    }

    @Override
    public void visit(ArrayAnnotationValue n) {

    }

    @Override
    public void visit(StringValue n) {

    }

    @Override
    public void visit(IntegerValue n) {

    }

    @Override
    public void visit(BooleanValue n) {

    }

    @Override
    public void visit(NullValue n) {

    }

    @Override
    public void visit(FullyQualifiedName n) {

    }
}
