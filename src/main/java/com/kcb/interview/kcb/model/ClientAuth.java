package com.kcb.interview.kcb.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "tb_client_auth")
public class ClientAuth {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic
    @Column(name = "client")
    private String client;
    @Basic
    @Column(name = "client_key")
    private String clientKey;
    @Basic
    @Column(name = "client_secret")
    private String clientSecret;
    @Basic
    @Column(name = "created_date")
    private Timestamp createdDate;
    @Basic
    @Column(name = "update_date")
    private Timestamp updateDate;
}
