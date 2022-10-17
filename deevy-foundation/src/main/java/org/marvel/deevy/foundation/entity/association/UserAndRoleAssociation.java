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
@Table(name = "MA_ASSOCIATION_USER_AND_ROLE", indexes = {
        @Index(name = "MA_IN_UR_USER_ID", columnList = "USER_ID"),
        @Index(name = "MA_IN_UR_ROLE_ID", columnList = "ROLE_ID"),
        @Index(name = "MA_IN_UR_VALID", columnList = "_VALID")
})
public class UserAndRoleAssociation extends BaseEntity {

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "ROLE_ID")
    private String roleId;
}
