package com.h10.sideproject.member.entity;

import com.h10.sideproject.poll.entity.Poll;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String profileImage;

    @OneToMany(mappedBy="member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Poll> pollList;

    @Builder
    public Member( String nickname, String password, String email){
        this.email = email;
        this.nickname = nickname;
        this.password = password;

    }
}
