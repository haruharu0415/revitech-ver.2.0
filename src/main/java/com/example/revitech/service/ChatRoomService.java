package com.example.revitech.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.ChatRoomWithNotificationDto;
import com.example.revitech.dto.DmDisplayDto;
import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.ChatMember;
import com.example.revitech.entity.ChatMemberId;
import com.example.revitech.entity.ChatMessage;
import com.example.revitech.entity.ChatReadStatus;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatMemberRepository;
import com.example.revitech.repository.ChatMessageRepository;
import com.example.revitech.repository.ChatReadStatusRepository;
import com.example.revitech.repository.ChatRoomRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UsersService usersService;
    private final UsersRepository usersRepository;

    // SQL Server用の安全な最小日付
    private static final LocalDateTime SAFE_MIN_DATE = LocalDateTime.of(1970, 1, 1, 0, 0);

    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChatMemberRepository chatMemberRepository,
                           ChatReadStatusRepository chatReadStatusRepository, ChatMessageRepository chatMessageRepository,
                           UsersService usersService, UsersRepository usersRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.chatReadStatusRepository = chatReadStatusRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.usersService = usersService;
        this.usersRepository = usersRepository;
    }

    /**
     * 自分のユーザーIDとソート設定をもとに、DM一覧（相手の名前付き）を取得する
     */
    public List<DmDisplayDto> getDmListForUser(Integer myUserId, Integer sortOrder) {
        List<ChatRoom> myDmRooms = chatRoomRepository.findDmRoomsByUserId(myUserId);
        List<DmDisplayDto> resultList = new ArrayList<>();

        for (ChatRoom room : myDmRooms) {
            List<ChatMember> members = chatMemberRepository.findById_RoomId(room.getRoomId());
            
            Integer partnerId = null;
            String partnerName = "不明なユーザー";

            for (ChatMember member : members) {
                Integer memberId = member.getId().getUserId();
                if (!memberId.equals(myUserId)) {
                    partnerId = memberId;
                    Optional<Users> partnerOpt = usersRepository.findById(partnerId);
                    if (partnerOpt.isPresent()) {
                        partnerName = partnerOpt.get().getName();
                    }
                    break; 
                }
            }

            if (partnerId != null) {
                resultList.add(new DmDisplayDto(room.getRoomId(), partnerId, partnerName));
            }
        }

        if (sortOrder != null && sortOrder == 2) {
            resultList.sort(Comparator.comparing(DmDisplayDto::getPartnerName, String.CASE_INSENSITIVE_ORDER));
        }

        return resultList;
    }

    public List<DmDisplayDto> getDmListForUser(Integer myUserId) {
        return getDmListForUser(myUserId, 1);
    }

    // --- 以下、既存のメソッド群 ---

    public void deleteGroupRoom(Integer roomId) {
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (roomOpt.isPresent()) {
            ChatRoom room = roomOpt.get();
            if (room.getType() == 2) {
                chatMessageRepository.deleteByRoomId(roomId);
                chatMemberRepository.deleteById_RoomId(roomId);
                chatReadStatusRepository.deleteByRoomId(roomId);
                chatRoomRepository.deleteById(roomId);
            }
        }
    }

    public List<ChatRoom> findUnreadGroupRooms(Integer userId) {
         return chatRoomRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(r -> r.getType() == 2)
                .filter(r -> isUserMemberOfRoom(userId, r.getRoomId()))
                .filter(r -> hasUnreadMessages(r.getRoomId(), userId))
                .collect(Collectors.toList());
    }

    // ★★★ 修正箇所: ここで相手の名前を解決して DmDisplayDto を返すように変更 ★★★
    public List<DmDisplayDto> findUnreadDmRooms(Integer userId) {
        List<ChatRoom> myDms = chatRoomRepository.findDmRoomsByUserId(userId);
        List<DmDisplayDto> resultList = new ArrayList<>();

        for (ChatRoom room : myDms) {
            // 未読がある場合のみ処理
            if (hasUnreadMessages(room.getRoomId(), userId)) {
                // パートナー特定ロジック
                List<ChatMember> members = chatMemberRepository.findById_RoomId(room.getRoomId());
                Integer partnerId = null;
                String partnerName = "不明なユーザー";

                for (ChatMember member : members) {
                    if (!member.getId().getUserId().equals(userId)) {
                        partnerId = member.getId().getUserId();
                        Optional<Users> partnerOpt = usersRepository.findById(partnerId);
                        if (partnerOpt.isPresent()) {
                            partnerName = partnerOpt.get().getName();
                        }
                        break;
                    }
                }
                // DTOを作成してリストに追加
                resultList.add(new DmDisplayDto(room.getRoomId(), partnerId, partnerName));
            }
        }
        return resultList;
    }

    private boolean hasUnreadMessages(Integer roomId, Integer userId) {
        Optional<ChatReadStatus> statusOpt = chatReadStatusRepository.findByUserIdAndRoomId(userId, roomId);
        LocalDateTime lastRead = statusOpt.map(ChatReadStatus::getLastReadAt).orElse(SAFE_MIN_DATE);
        long count = chatMessageRepository.countByRoomIdAndCreatedAtAfterAndUserIdNot(roomId, lastRead, userId);
        return count > 0;
    }

    public ChatRoom getOrCreateDmRoom(Integer userId1, Integer userId2) {
        List<ChatRoom> existingRooms = chatRoomRepository.findDmRoomsByUserId(userId1);
        for (ChatRoom room : existingRooms) {
            if (isUserMemberOfRoom(userId2, room.getRoomId())) {
                return room;
            }
        }

        Users user2 = usersService.findById(userId2).orElseThrow();
        ChatRoom newDmRoom = new ChatRoom();
        newDmRoom.setType(1);
        newDmRoom.setUsersId(userId1);
        newDmRoom.setName(user2.getName());
        ChatRoom savedRoom = chatRoomRepository.save(newDmRoom);

        ChatMemberId memberId1 = new ChatMemberId(savedRoom.getRoomId(), userId1);
        ChatMemberId memberId2 = new ChatMemberId(savedRoom.getRoomId(), userId2);

        chatMemberRepository.save(new ChatMember(memberId1));
        chatMemberRepository.save(new ChatMember(memberId2));

        return savedRoom;
    }

    public List<UserSearchDto> getRoomMembers(Integer roomId) {
        List<ChatMember> members = chatMemberRepository.findById_RoomId(roomId);
        return members.stream()
            .map(member -> {
                Users user = usersService.findById(member.getId().getUserId()).orElseThrow();
                String iconUrl = usersService.getUserIconPath(user.getUsersId());
                return new UserSearchDto(
                    user.getUsersId(), 
                    user.getName(), 
                    user.getEmail(),
                    iconUrl,
                    user.getRole()
                );
            })
            .collect(Collectors.toList());
    }

    public List<ChatRoomWithNotificationDto> getRoomsForUserWithNotifications(Integer userId) {
        List<ChatMember> memberships = chatMemberRepository.findById_UserId(userId);
        return memberships.stream().map(member -> {
            ChatRoom room = chatRoomRepository.findById(member.getId().getRoomId()).orElseThrow();
            Optional<ChatReadStatus> readStatusOpt = chatReadStatusRepository.findByUserIdAndRoomId(userId, room.getRoomId());
            
            long unreadCount = readStatusOpt
                .map(status -> chatMessageRepository.countByRoomIdAndCreatedAtAfterAndUserIdNot(room.getRoomId(), status.getLastReadAt(), userId))
                .orElseGet(() -> chatMessageRepository.countByRoomIdAndCreatedAtAfterAndUserIdNot(room.getRoomId(), SAFE_MIN_DATE, userId));
                
            LocalDateTime lastMessageTimestamp = chatMessageRepository.findFirstByRoomIdOrderByCreatedAtDesc(room.getRoomId())
                .map(ChatMessage::getCreatedAt).orElse(room.getCreatedAt());
            return new ChatRoomWithNotificationDto(room, unreadCount, lastMessageTimestamp);
        }).collect(Collectors.toList());
    }

    public ChatRoom createGroupRoom(Integer creatorId, String name, List<Integer> memberIds) {
        ChatRoom group = new ChatRoom();
        group.setName(name);
        group.setType(2);
        group.setUsersId(creatorId);
        
        ChatRoom savedGroup = chatRoomRepository.save(group);
        
        chatMemberRepository.save(new ChatMember(new ChatMemberId(savedGroup.getRoomId(), creatorId)));
        memberIds.forEach(memberId -> {
            if (!memberId.equals(creatorId)) {
                 chatMemberRepository.save(new ChatMember(new ChatMemberId(savedGroup.getRoomId(), memberId)));
            }
        });
        return savedGroup;
    }

    public void markRoomAsRead(Integer userId, Integer roomId) {
        ChatReadStatus status = chatReadStatusRepository.findByUserIdAndRoomId(userId, roomId)
            .orElse(new ChatReadStatus(userId, roomId));
        status.setLastReadAt(LocalDateTime.now());
        chatReadStatusRepository.save(status);
    }

    public Optional<ChatRoom> getRoomById(Integer roomId) {
        return chatRoomRepository.findById(roomId);
    }

    public boolean isUserMemberOfRoom(Integer userId, Integer roomId) {
        return chatMemberRepository.existsById_UserIdAndId_RoomId(userId, roomId);
    }
}