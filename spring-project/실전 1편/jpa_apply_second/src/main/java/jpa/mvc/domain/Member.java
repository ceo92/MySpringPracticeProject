package jpa.mvc.domain;

import jakarta.persistence.*;
import jpa.mvc.Address;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue //auto : h2이면 그냥 SEQUENCE 전략 ! sequence 객체로부터 하나씩 만듬 ㅇㅇ
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member" , fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();


    public Member(){}

    public Member(String username, Address address) {
        this.username = username;
        this.address = address;
    }
//연관관계 설정 및 연관관계 편의 메서드 정의
    // 즉 dto 따로 만들어야지 ㅇㅇ



}
