package com.jumunhasyeotjo.userinteract.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "p_company_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CompanyManager implements ApproveTarget {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private UUID companyId;

    public CompanyManager(User user) {
        this.user = user;
    }

    @Override
    public void approve(User user) {
        this.user = user;
        this.companyId = user.getBelong();
    }

    @Override
    public void leave() {
        this.user = null;
    }
}
