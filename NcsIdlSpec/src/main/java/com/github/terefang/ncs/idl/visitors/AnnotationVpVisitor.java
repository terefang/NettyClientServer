package com.github.terefang.ncs.idl.visitors;

import com.github.terefang.ncs.idl.spec.syntaxtree.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnnotationVpVisitor extends AbstractVisitor implements Extractor<AnnotationValuePairs, Map<String,String>> {

    Map<String, String> map = new LinkedHashMap<>();
    private String key;
    private String value;

    public static Map<String, String> from(AnnotationValuePairs object)
    {
        return new AnnotationVpVisitor().extract(object);
    }

    @Override
    public Map<String, String> extract(AnnotationValuePairs object)
    {
        object.accept(this);
        return this.map;
    }

    @Override
    public void visit(AnnotationValuePairs annotationValuePairs) {
        annotationValuePairs.f0.accept(this);
        if(annotationValuePairs.f1.present())
        {
            for(Node node : annotationValuePairs.f1.nodes)
            {
                node.accept(this);
            }
        }
    }

    @Override
    public void visit(AnnotationValuePair annotationValuePair) {
        key = annotationValuePair.f0.tokenImage;
        annotationValuePair.f2.accept(this);
        if(value.startsWith("\"") && value.endsWith("\""))
        {
            value = value.substring(1, value.length()-1);
        }
        this.map.put(key, value);
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
    public void visit(StringValue stringValue) {
        value = stringValue.f0.tokenImage;
    }

    @Override
    public void visit(IntegerValue integerValue) {
        this.value = integerValue.f1.tokenImage;
    }

    @Override
    public void visit(BooleanValue booleanValue) {
        this.value = Boolean.valueOf(booleanValue.f0.tokenImage).toString();
    }

    @Override
    public void visit(NullValue nullValue) {
        this.value = "null";
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
