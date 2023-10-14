package com.github.terefang.ncs.idl.visitors;

import com.github.terefang.ncs.idl.objects.AnnotSpec;
import com.github.terefang.ncs.idl.spec.syntaxtree.*;

import java.util.List;
import java.util.Vector;

public class AnnotVisitor extends AbstractVisitor implements Extractor<Annotation, AnnotSpec> {

    private AnnotSpec spec;

    public static AnnotSpec from(Annotation object)
    {
        return new AnnotVisitor().extract(object);
    }
    public static List<AnnotSpec> from(Annotations object)
    {
        return new AnnotVisitor().extract(object);
    }

    List<AnnotSpec> slist = new Vector();

    @Override
    public AnnotSpec extract(Annotation object) {
        object.accept(this);
        return this.slist.get(0);
    }

    public List<AnnotSpec> extract(Annotations object) {
        object.accept(this);
        return this.slist;
    }

    @Override
    public void visit(Annotations annotations)
    {
        if(annotations.f0.present())
        {
            for(Node node : annotations.f0.nodes)
            {
                node.accept(this);
            }
        }
    }

    @Override
    public void visit(Annotation annotation) {
        this.spec = new AnnotSpec();
        this.slist.add(spec);
        spec = null;
    }

}
