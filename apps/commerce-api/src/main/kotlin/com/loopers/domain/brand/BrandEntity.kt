package com.loopers.domain.brand

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update brand set deleted_at = CURRENT_TIMESTAMP where id = ?")
@Entity
@Table(name = "brand")
class BrandEntity(
    name: String,
): BaseEntity() {

    @Column(name = "name")
    val name: String = name
}
