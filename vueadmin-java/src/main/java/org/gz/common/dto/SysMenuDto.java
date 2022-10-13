package org.gz.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysMenuDto implements Serializable{
    private Long id;
    private String title;
    private String icon;
    private String path;
    private String name;
    private String component;
    List<SysMenuDto> children = new ArrayList<>();
}
