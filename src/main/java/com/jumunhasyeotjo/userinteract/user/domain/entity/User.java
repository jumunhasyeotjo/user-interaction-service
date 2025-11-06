package com.jumunhasyeotjo.userinteract.user.domain.entity;

import com.jumunhasyeotjo.userinteract.common.entity.BaseEntity;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String slackId;

    private UUID belong;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private CompanyManager companyManager;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Driver driver;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private HubManager hubManager;

    public static User join(String name, String password, String slackId, String role, UUID belong) {
        User user = new User();
        user.name = name;
        user.password = password;
        user.slackId = slackId;
        user.status = UserStatus.of("PENDING");
        user.role = UserRole.of(role);
        if (user.role.canBelongTo()) {
            user.belong = belong;
        }
        return user;
    }

    public void approve(ApproveTarget target) {
        if (!this.status.canBeApproved()) {
            throw new BusinessException(ErrorCode.ALREADY_APPROVED);
        }

        this.status = UserStatus.APPROVED;
        target.approve(this); // 공통 행위

        switch (this.role) {
            case HUB_DRIVER, COMPANY_DRIVER -> this.driver = (Driver) target;
            case COMPANY_MANAGER -> this.companyManager = (CompanyManager) target;
            case HUB_MANAGER -> this.hubManager = (HubManager) target;
            default -> throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
    }

    public void memberCancellation(Long requesterId) {
        this.markDeleted(requesterId);
        if (this.driver != null) {
            this.driver.leave();
            this.driver = null;
        } else if (this.hubManager != null) {
            this.hubManager.leave();
            this.hubManager = null;
        } else if (this.companyManager != null) {
            this.companyManager.leave();
            this.companyManager = null;
        }
    }
}
