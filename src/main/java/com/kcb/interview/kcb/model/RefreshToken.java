package com.kcb.interview.kcb.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "tb_refresh_token")
public class RefreshToken {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "client_key", referencedColumnName = "id")
    private ClientAuth clientAuth;
    @Basic
    @Column(name = "token")
    private String token;
    @Basic
    @Column(name = "expiry_date")
    private Timestamp expiryDate;
    @Basic
    @Column(name = "created_date")
    private Timestamp createdDate;
}
