package org.marvel.deevy.foundation.param;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Data
public class RoleParam {
    /**
     * ID
     */
    private String id;

    /**
     * 更新时间
     */
    private Long modifyTime;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 状态：正常，停用
     */
    private Integer status;

    /**
     * 排序码
     */
    private Long sortNumber;


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
        if (ObjectUtil.isNull(status)) {
            status = 1;
        }
        if (ObjectUtil.isNull(sortNumber)) {
            sortNumber = 0L;
        }
        return 0;
    }
}
