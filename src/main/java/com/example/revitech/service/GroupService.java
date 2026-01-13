package com.example.revitech.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.entity.ChatGroup;
import com.example.revitech.entity.GroupMember;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatGroupRepository;
import com.example.revitech.repository.GroupMemberRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class GroupService {

    private final GroupMemberRepository groupMemberRepository;
    private final UsersRepository usersRepository;
    private final ChatGroupRepository chatGroupRepository;

    public GroupService(GroupMemberRepository groupMemberRepository, 
                        UsersRepository usersRepository,
                        ChatGroupRepository chatGroupRepository) {
        this.groupMemberRepository = groupMemberRepository;
        this.usersRepository = usersRepository;
        this.chatGroupRepository = chatGroupRepository;
    }

    public List<ChatGroup> getAllGroups() {
        return chatGroupRepository.findAll();
    }
    
    // ★ 修正: メンバーリストを受け取ってグループ作成
    public void createGroupWithMembers(String name, String description, List<Integer> memberIds) {
        ChatGroup group = new ChatGroup();
        group.setGroupName(name);
        group.setDescription(description);
        ChatGroup savedGroup = chatGroupRepository.save(group);
        
        // メンバー登録
        if (memberIds != null) {
            for (Integer userId : memberIds) {
                addMember(savedGroup.getGroupId(), userId);
            }
        }
    }
    
    // グループ削除
    public void deleteGroup(Integer groupId) {
        chatGroupRepository.deleteById(groupId);
    }

    // 既存メソッド
    public List<Users> getGroupMembers(Integer groupId) {
        List<Integer> memberIds = groupMemberRepository.findByGroupId(groupId).stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toList());
        if (memberIds.isEmpty()) return List.of();
        return usersRepository.findAllById(memberIds);
    }

    public List<Users> getCandidateUsers(Integer groupId) {
        List<Users> allUsers = usersRepository.findAll().stream()
                .filter(u -> !"deleted".equals(u.getStatus()))
                .collect(Collectors.toList());

        List<Integer> memberIds = groupMemberRepository.findByGroupId(groupId).stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toList());

        return allUsers.stream()
                .filter(u -> !memberIds.contains(u.getUsersId()))
                .collect(Collectors.toList());
    }

    public void addMember(Integer groupId, Integer userId) {
        if (groupMemberRepository.findByGroupIdAndUserId(groupId, userId).isPresent()) {
            return;
        }
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        groupMemberRepository.save(member);
    }

    public void removeMember(Integer groupId, Integer userId) {
        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }
}