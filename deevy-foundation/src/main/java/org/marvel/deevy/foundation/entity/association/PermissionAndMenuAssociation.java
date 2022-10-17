package org.marvel.deevy.foundation.entity.association;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.marvel.deevy.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "MA_ASSOCIATION_PERMISSION_AND_MENU", indexes = {
        @Index(name = "MA_IN_PF_PERMISSION_ID", columnList = "PERMISSION_ID"),
        @Index(name = "MA_IN_PF_MENU_ID", columnList = "MENU_ID"),
        @Index(name = "MA_IN_PF_VALID", columnList = "_VALID")
})
public class PermissionAndMenuAssociation extends BaseEntity {

    @Column(name = "PERMISSION_ID", length = 36)
    private String permissionId;

    @Column(name = "MENU_ID", length = 36)
    private String menuId;
}