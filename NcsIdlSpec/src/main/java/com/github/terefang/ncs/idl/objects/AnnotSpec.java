package com.github.terefang.ncs.idl.objects;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Data
public class AnnotSpec extends AbstractSpec
{
    String annotName = "ANNOT";
    List<String> annotParameter = new Vector();
    Map<String, String> annotParamMap = new HashMap();

    public AnnotSpec() {    }

}
