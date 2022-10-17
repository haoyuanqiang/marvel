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
@Table(name = "MA_ASSOCIATION_ROLE_AND_PERMISSION", indexes = {
        @Index(name = "MA_IN_RP_ROLE_ID", columnList = "ROLE_ID"),
        @Index(name = "MA_IN_RP_PERMISSION_ID", columnList = "PERMISSION_ID"),
        @Index(name = "MA_IN_GU_VALID", columnList = "_VALID")
})
public class RoleAndPermissionAssociation extends BaseEntity {

    @Column(name = "ROLE_ID", length = 36)
    private String roleId;

    @Column(name = "PERMISSION_ID", length = 36)
    private String permissionId;

}
