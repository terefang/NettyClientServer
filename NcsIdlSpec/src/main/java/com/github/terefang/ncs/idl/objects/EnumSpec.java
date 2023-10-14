package com.github.terefang.ncs.idl.objects;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class EnumSpec  extends AbstractSpec
{
    String enumName = "ENUM";
    List<String> enumList = new LinkedList();

    public EnumSpec() {    }

}
