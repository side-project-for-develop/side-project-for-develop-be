package com.h10.sideproject.profile.service;

import com.h10.sideproject.comment.repository.CommentRepository;
import com.h10.sideproject.common.CookieUtil;
import com.h10.sideproject.common.exception.CustomException;

import com.h10.sideproject.common.response.ErrorCode;
import com.h10.sideproject.member.entity.Member;
import com.h10.sideproject.member.repository.MemberRepository;
import com.h10.sideproject.poll.repository.PollRepository;
import com.h10.sideproject.profile.dto.ProfileRequestDto;
import com.h10.sideproject.security.MemberDetailsImpl;
import com.h10.sideproject.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PollRepository pollRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void editProfile(MemberDetailsImpl memberDetails,
                            ProfileRequestDto profileRequestDto) {
        //bearerToken 에서 email 추출
        Member member =memberRepository.findByEmail(memberDetails.getMember().getEmail()).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
        member.update(profileRequestDto.getProfileImage(), profileRequestDto.getNickname());
        if(!member.getEmail().equals(memberDetails.getMember().getEmail())) {
            throw new CustomException(ErrorCode.NOT_FOUND_EMAIL);
        }
        memberRepository.save(member);
    }

    @Transactional
    public void invalidateToken(MemberDetailsImpl memberDetails,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        String token = jwtUtil.resolveToken(request.getHeader(JwtUtil.AUTHORIZATION_HEADER));
        if(StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            jwtUtil.invalidateToken(token);
            CookieUtil.deleteCookie(response, JwtUtil.AUTHORIZATION_KEY);
        }
    }

    @Transactional
    public void withdrawal(Long memberId) {
        try{
            Member member = memberRepository.findById(memberId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_MEMBER));
            commentRepository.deleteAllByCommentId2(memberId);
            pollRepository.deleteAllByMemberId(memberId);
            memberRepository.delete(member);
        }catch (Exception e) {
            throw new CustomException(ErrorCode.MEMBER_FOUND_NULL);
        }

    }
}
