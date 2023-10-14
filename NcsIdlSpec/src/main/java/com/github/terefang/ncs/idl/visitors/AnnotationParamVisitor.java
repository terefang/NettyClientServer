package com.github.terefang.ncs.idl.visitors;

import com.github.terefang.ncs.idl.spec.syntaxtree.*;

import java.util.List;
import java.util.Vector;

public class AnnotationParamVisitor extends AbstractVisitor implements Extractor<AnnotationValue, List<String>> {

    List<String> list = new Vector<>();

    public static List<String> from(AnnotationValue object)
    {
        return new AnnotationParamVisitor().extract(object);
    }

    @Override
    public List<String> extract(AnnotationValue object)
    {
        object.accept(this);
        return this.list;
    }

    @Override
    public void visit(AnnotationParameters annotationParameters) {
        annotationParameters.f0.accept(this);
        if(annotationParameters.f1.present())
        {
            annotationParameters.f1.accept(this);
        }
        annotationParameters.f2.accept(this);
    }

    @Override
    public void visit(AnnotationValue annotationValue) {
        annotationValue.f0.accept(this);
    }

    @Override
    public void visit(AnnotationAnnotationValue annotationAnnotationValue) {
        annotationAnnotationValue.f0.accept(this);
    }

    @Override
    public void visit(ArrayAnnotationValue arrayAnnotationValue)
    {
        //arrayAnnotationValue.f0.accept(this);
        if(arrayAnnotationValue.f1.present())
        {
            arrayAnnotationValue.f1.accept(this);
        }
        //arrayAnnotationValue.f2.accept(this);
    }

    @Override
    public void visit(StringValue stringValue) {
        this.list.add(stringValue.f0.tokenImage);
    }

    @Override
    public void visit(IntegerValue integerValue) {
        this.list.add(integerValue.f1.tokenImage);
    }

    @Override
    public void visit(BooleanValue booleanValue) {
        this.list.add(Boolean.valueOf(booleanValue.f0.tokenImage).toString());
    }

    @Override
    public void visit(NullValue nullValue) {
        this.list.add(null);
    }

    @Override
    public void visit(FullyQualifiedName fullyQualifiedName) {
        fullyQualifiedName.f0.accept(this);
        if(fullyQualifiedName.f1.present())
        {
            for(Node node : fullyQualifiedName.f1.nodes)
            {
                node.accept(this);
            }
        }
    }
}
