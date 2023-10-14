package com.github.terefang.ncs.idl.objects;

import lombok.Data;

import java.util.List;
import java.util.Vector;

@Data
public class ParamSpec  extends AbstractSpec
{
    String paramType = "PTYPE";
    String paramName = "PARAM";
    List<AnnotSpec> annotList = new Vector();
    boolean arrayType = false;

    public ParamSpec() {    }

}
