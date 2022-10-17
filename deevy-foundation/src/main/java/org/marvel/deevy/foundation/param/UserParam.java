package org.marvel.deevy.foundation.param;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Data
public class UserParam {

    /**
     * ID
     */
    private String id;

    /**
     * 数据更改时间
     */
    private Long modifyTime;

    /**
     * 用户编码
     */
    private String code;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 登录名称
     */
    private String loginName;

    /**
     * 电话号码
     */
    private String telephone;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 性别： 1 = 男，2 = 女，3 = 保密
     */
    private Integer sex;

    /**
     * 用户状态
     */
    private Integer status;

    /**
     * 部门ID
     */
    private String departmentId;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 排序码
     */
    private Long sortNumber;

    /**
     * 密码，可选字段，修改用户时不更新密码
     */
    private String password;

    /**
     * 是否只读，用于标识不可删除的数据
     */
    private Boolean readOnly;

    /**
     * 岗位ID，支持多选
     */
    private List<String> positionIds;

    /**
     * 角色ID，支持多选
     */
    private List<String> roleIds;

    public int validate() {
        int maxLength = 127;
        // 编码
        if (StrUtil.isBlank(code) || StrUtil.length(code) > maxLength) {
            return 1130;
        }
        if (!Validator.isGeneral(code)) {
            return 1131;
        }
        // 名称
        if (StrUtil.isBlank(name) || StrUtil.length(name) > maxLength) {
            return 1132;
        }
        if (!Validator.isGeneralWithChinese(name)) {
            return 1133;
        }
        // 登录名称
        if (StrUtil.isBlank(loginName) || StrUtil.length(loginName) > maxLength) {
            return 1134;
        }
        if (!Validator.isGeneral(loginName)) {
            return 1135;
        }
        // 用户昵称
        if (StrUtil.length(nickname) > maxLength) {
            return 1136;
        }
        // 用户性别必选
        if (ObjectUtil.isNull(sex)) {
            return 1137;
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
