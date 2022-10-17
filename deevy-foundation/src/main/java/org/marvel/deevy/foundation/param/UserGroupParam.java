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
public class UserGroupParam {

    /**
     * ID
     */
    private String id;

    /**
     * 更新时间
     */
    private Long modifyTime;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 父级用户组ID
     */
    private String parentId;

    /**
     * 排序码
     */
    private Long sortNumber;

    /**
     * 用户组状态
     */
    private Integer status;

    /**
     * 子岗位列表
     */
    private List<UserGroupParam> children;


    public void addChildNode(UserGroupParam childNode) {
        if (null == children) {
            children = new ArrayList<>();
        }
        children.add(childNode);
    }

    public int validate() {
        int maxLength = 127;
        if (StrUtil.isBlank(code) || StrUtil.length(code) > maxLength) {
            return 1110;
        }
        if (!Validator.isGeneral(code)) {
            return 1111;
        }
        if (StrUtil.isBlank(name) || StrUtil.length(name) > maxLength) {
            return 1112;
        }
        if (!Validator.isGeneralWithChinese(name)) {
            return 1113;
        }
        if (ObjectUtil.isNull(sortNumber)) {
            sortNumber = 0L;
        }
        return 0;
    }
}
