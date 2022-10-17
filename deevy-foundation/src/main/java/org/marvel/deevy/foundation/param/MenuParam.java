package org.marvel.deevy.foundation.param;

import cn.hutool.core.lang.Validator;
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
public class MenuParam {

    /**
     * ID
     */
    private String id;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 菜单编码
     */
    private String code;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单图标
     */
    private String iconName;

    /**
     * 菜单路径
     */
    private String path;

    /**
     * 菜单路径类型
     */
    private Integer routeType;

    /**
     * 类型: 1-分组  2-页面 3-第三方页面
     */
    private Integer type;

    /**
     * 显示状态：显示、隐藏
     */
    private Integer visible;

    /**
     * 父级菜单
     */
    private String parentId;

    /**
     * 排序
     */
    private Long sortNumber;

    /**
     * 状态：0不可用1可用
     */
    private Integer status;

    /**
     * 子部门列表
     */
    private List<MenuParam> children;

    public int validate() {
        int maxLength = 127;
        if (StrUtil.isBlank(code) || StrUtil.length(code) > maxLength) {
            return 1150;
        }
        if (!Validator.isGeneral(code)) {
            return 1151;
        }
        if (StrUtil.isBlank(name) || StrUtil.length(name) > maxLength) {
            return 1152;
        }
        if (!Validator.isGeneralWithChinese(name)) {
            return 1153;
        }
        if (null == status) {
            status = 1;
        }
        if (null == sortNumber) {
            sortNumber = 0L;
        }
        return 0;
    }
}
