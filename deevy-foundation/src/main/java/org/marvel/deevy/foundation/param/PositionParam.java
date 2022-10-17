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
public class PositionParam {

    /**
     * ID
     */
    private String id;

    /**
     * 岗位编号
     */
    private String code;

    /**
     * 岗位名称
     */
    private String name;

    /**
     * 父级岗位ID
     */
    private String parentId;

    /**
     * 岗位状态
     */
    private Integer status;

    /**
     * 岗位排序码
     */
    private Long sortNumber;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 子岗位列表
     */
    private List<PositionParam> children;


    public void addChildNode(PositionParam childNode) {
        if (null == children) {
            children = new ArrayList<>();
        }
        children.add(childNode);
    }

    public int validate() {
        int maxLength = 127;
        if (StrUtil.isBlank(code) || StrUtil.length(code) > maxLength) {
            return 1120;
        }
        if (!Validator.isGeneral(code)) {
            return 1121;
        }
        if (StrUtil.isBlank(name) || StrUtil.length(name) > maxLength) {
            return 1122;
        }
        if (!Validator.isGeneralWithChinese(name)) {
            return 1123;
        }
        if (ObjectUtil.isNull(status)) {
            status = 1;
        }
        if (ObjectUtil.isNull(sortNumber)) {
            sortNumber = 0L;
        }
        return 0;
    }
}
