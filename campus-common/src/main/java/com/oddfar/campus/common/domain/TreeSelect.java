package com.oddfar.campus.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oddfar.campus.common.domain.entity.SysMenuEntity;
import com.oddfar.campus.common.domain.entity.SysResourceEntity;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 *
 * @author ruoyi
 */
public class TreeSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private Long id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect() {
    }

    public TreeSelect(Long id, String label) {
        this.id = id;
        this.label = label;
    }


    public TreeSelect(Long id, String label, List<SysResourceEntity> resources) {
        this.id = id;
        this.label = label;
        this.children = resources.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(SysMenuEntity menu) {
        this.id = menu.getMenuId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(SysResourceEntity resource) {
        this.id = resource.getResourceId();
        this.label = resource.getResourceName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<TreeSelect> getChildren() {
        return children;
    }

    public void setChildren(List<TreeSelect> children) {
        this.children = children;
    }
}
