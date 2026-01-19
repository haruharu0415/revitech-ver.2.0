package com.example.revitech.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.entity.ChatMember;
import com.example.revitech.entity.ChatMemberId;
import com.example.revitech.entity.GroupMember;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatMemberRepository;
import com.example.revitech.repository.GroupMemberRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class GroupService {

    private final GroupMemberRepository groupMemberRepository;
    private final ChatMemberRepository chatMemberRepository; // ★ここが重要
    private final UsersRepository usersRepository;

    public GroupService(GroupMemberRepository groupMemberRepository,
                        ChatMemberRepository chatMemberRepository, // ★コンストラクタに追加
                        UsersRepository usersRepository) {
        this.groupMemberRepository = groupMemberRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.usersRepository = usersRepository;
    }

    /**
     * グループのメンバー一覧を取得
     */
    public List<Users> getGroupMembers(Integer groupId) {
        // chat_members から取得するのが確実
        List<ChatMember> chatMembers = chatMemberRepository.findById_RoomId(groupId);
        return chatMembers.stream()
            .map(cm -> usersRepository.findById(cm.getId().getUserId()).orElse(null))
            .filter(u -> u != null)
            .collect(Collectors.toList());
    }

    /**
     * メンバーを追加する
     */
    public void addMember(Integer groupId, Integer userId) {
        // 1. グループ管理用のテーブルに追加
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            GroupMember gm = new GroupMember();
            gm.setGroupId(groupId);
            gm.setUserId(userId);
            groupMemberRepository.save(gm);
        }

        // 2. ★★★ チャット機能用のテーブル(chat_members)にも追加！ ★★★
        // これがないとチャットが見れません
        if (!chatMemberRepository.existsById_UserIdAndId_RoomId(userId, groupId)) {
            ChatMember cm = new ChatMember();
            ChatMemberId cmId = new ChatMemberId(groupId, userId); // 引数の順番に注意(roomId, userId)
            cm.setId(cmId);
            chatMemberRepository.save(cm);
        }
    }

    /**
     * メンバーを削除する
     */
    public void removeMember(Integer groupId, Integer userId) {
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
        }
        
        if (chatMemberRepository.existsById_UserIdAndId_RoomId(userId, groupId)) {
            ChatMemberId cmId = new ChatMemberId(groupId, userId);
            chatMemberRepository.deleteById(cmId);
        }
    }
}