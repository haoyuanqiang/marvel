package org.marvel.deevy.foundation.param;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Data
public class DepartmentParam {

    /**
     * ID
     */
    private String id;

    /**
     * 部门编号
     */
    private String code;

    /**
     * 部门姓名
     */
    private String name;

    /**
     * 父级部门ID
     */
    private String parentId;

    /**
     * 部门状态
     */
    private Integer status;

    /**
     * 部门排序码
     */
    private Long sortNumber;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 子部门列表
     */
    private List<DepartmentParam> children;

    public int validate() {
        int maxLength = 127;
        if (StrUtil.isBlank(code) || StrUtil.length(code) > maxLength) {
            return 1100;
        }
        if (!Validator.isGeneral(code)) {
            return 1101;
        }
        if (StrUtil.isBlank(name) || StrUtil.length(name) > maxLength) {
            return 1102;
        }
        if (!Validator.isGeneralWithChinese(name)) {
            return 1103;
        }
        if (ObjectUtil.isNull(status)) {
            status = 1;
        }
        if (ObjectUtil.isNull(sortNumber)) {
            sortNumber = 0L;
        }
        return 0;
    }

    public void addChildNode(DepartmentParam childNode) {
        if (null == children) {
            children = new ArrayList<>();
        }
        children.add(childNode);
    }
}
