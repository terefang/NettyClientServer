package com.github.terefang.ncs.idl.visitors;

public interface Extractor<A,B>
{
    B extract(A object);
}
