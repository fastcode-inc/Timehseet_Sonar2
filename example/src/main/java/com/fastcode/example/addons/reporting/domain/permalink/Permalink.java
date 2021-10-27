package com.fastcode.example.addons.reporting.domain.permalink;

import com.fastcode.example.domain.core.abstractentity.AbstractEntity;
import com.fastcode.example.domain.core.authorization.users.Users;
import java.util.UUID;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "permalink")
public class Permalink extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false, length = 256)
    private UUID id;

    @Basic
    @Column(name = "authentication", nullable = true, length = 30)
    private String authentication;

    @Basic
    @Column(name = "description", nullable = true)
    private Boolean description;

    @Basic
    @Column(name = "refresh_rate", nullable = true)
    private Long refreshRate;

    @Basic
    @Column(name = "rendering", nullable = false, length = 30)
    private String rendering;

    @Basic
    @Column(name = "resource", nullable = false, length = 30)
    private String resource;

    @Basic
    @Column(name = "password", nullable = true, length = 256)
    private String password;

    @Basic
    @Column(name = "resource_id", nullable = true)
    private Long resourceId;

    @Basic
    @Column(name = "toolbar", nullable = true)
    private Boolean toolbar;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users users;
}
