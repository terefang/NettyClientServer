package com.github.terefang.ncs.idl.objects;

import lombok.Data;

import java.util.List;
import java.util.Vector;

@Data
public class StructSpec  extends AbstractSpec
{

    String structName = "STRUCT";
    List<AnnotSpec> annotList = new Vector<>();
    List<ParamSpec> paramList = new Vector<>();

    public StructSpec() {    }

}
