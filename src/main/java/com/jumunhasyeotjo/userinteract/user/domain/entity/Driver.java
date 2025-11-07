package com.jumunhasyeotjo.userinteract.user.domain.entity;

import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_driver")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Driver implements ApproveTarget{

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private UUID hubId;

    private Integer order;

    public Driver(User user) {
        this.user = user;
    }

    public void assignOrder(Integer order) {
        this.order = order;
    }

    @Override
    public void approve(User user) {
        if (user.getRole() == UserRole.COMPANY_DRIVER) {
            this.hubId = user.getBelong();
        }
        this.user = user;
    }

    @Override
    public void leave() {
        this.user = null;
    }
}
