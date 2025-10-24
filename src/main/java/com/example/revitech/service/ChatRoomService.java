package com.example.revitech.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ★ あなたのDTO (ChatRoomWithNotificationDto) をインポート
import com.example.revitech.dto.ChatRoomWithNotificationDto;
import com.example.revitech.dto.RoomMemberDto;
import com.example.revitech.entity.ChatMember;
import com.example.revitech.entity.ChatReadStatus;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatMemberRepository;
import com.example.revitech.repository.ChatMessageRepository;
import com.example.revitech.repository.ChatReadStatusRepository;
import com.example.revitech.repository.ChatRoomRepository;

@Service
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final UsersService usersService;
    private final ChatMessageRepository chatMessageRepository; 

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           ChatMemberRepository chatMemberRepository,
                           ChatReadStatusRepository chatReadStatusRepository,
                           UsersService usersService,
                           ChatMessageRepository chatMessageRepository) { 
        this.chatRoomRepository = chatRoomRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.chatReadStatusRepository = chatReadStatusRepository;
        this.usersService = usersService;
        this.chatMessageRepository = chatMessageRepository; 
    }

    /** ルームIDで検索 (Long) */
    public Optional<ChatRoom> getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId);
    }

    /** ユーザーID (Long) で参加ルーム検索 (DTO版) */
    public List<ChatRoomWithNotificationDto> getRoomsForUser(Long userId) {
        List<ChatMember> memberships = chatMemberRepository.findByIdUserId(userId);

        return memberships.stream().map(member -> {
            ChatRoom room = member.getRoom();
            
            LocalDateTime lastMessageTime = chatMessageRepository.findLatestMessageTimestampByRoomId(room.getId())
                    .orElse(room.getCreatedAt()); 

            LocalDateTime lastReadTime = chatReadStatusRepository.findByUserIdAndRoomId(userId, room.getId())
                    .map(ChatReadStatus::getLastReadAt)
                    .orElse(LocalDateTime.MIN); 

            int unreadCount = chatMessageRepository.countUnreadMessages(room.getId(), lastReadTime);

            // (あなたのDTOのコンストラクタを呼び出す)
            return new ChatRoomWithNotificationDto(
                room,
                unreadCount,
                lastMessageTime
            );
        }).collect(Collectors.toList());
    }

    /** メンバーシップチェック (Long) */
    public boolean isUserMemberOfRoom(Long userId, Long roomId) {
        return chatMemberRepository.existsByIdUserIdAndIdRoomId(userId, roomId);
    }

    /** ルームメンバー取得 (Long) */
    public List<RoomMemberDto> getRoomMembers(Long roomId) {
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return List.of();
        }
        return roomOpt.get().getMembers().stream()
                .map(ChatMember::getUser)
                .map(RoomMemberDto::new)
                .collect(Collectors.toList());
    }

    /** DMルーム取得/作成 (Long) */
    public ChatRoom getOrCreateDmRoom(Long userId1, Long userId2) {
        List<ChatRoom> user1Rooms = chatMemberRepository.findByIdUserId(userId1).stream()
                                        .map(ChatMember::getRoom)
                                        .toList();
        for (ChatRoom room : user1Rooms) {
            if (room.getType() == 1 && isUserMemberOfRoom(userId2, room.getId())) {
                return room;
            }
        }
        Users user1 = usersService.findById(userId1).orElseThrow(() -> new RuntimeException("User not found: " + userId1));
        
        // ★★★ 構文エラーを修正 ★★★
        Users user2 = usersService.findById(userId2).orElseThrow(() -> new RuntimeException("User not found: " + userId2));
        
        ChatRoom newRoom = new ChatRoom(1, userId1);
        newRoom.addMember(new ChatMember(newRoom, user1));
        newRoom.addMember(new ChatMember(newRoom, user2));
        return chatRoomRepository.save(newRoom);
    }

    /** グループ作成 (Long) */
    public ChatRoom createGroupRoom(Long creatorId, String name, List<Long> memberIds) {
        Users creator = usersService.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found: " + creatorId));
        
        ChatRoom newRoom = new ChatRoom(name, 2, creatorId); 
        final ChatRoom savedRoom = chatRoomRepository.save(newRoom); 

        savedRoom.addMember(new ChatMember(savedRoom, creator));

        for (Long memberId : memberIds) {
            if (!memberId.equals(creatorId)) {
                usersService.findById(memberId).ifPresent(user -> {
                    savedRoom.addMember(new ChatMember(savedRoom, user));
                });
            }
        }
        
        return chatRoomRepository.save(savedRoom); 
    }

    /** 既読処理 (Long) */
    public void markRoomAsRead(Long userId, Long roomId) {
        ChatReadStatus readStatus = chatReadStatusRepository.findByUserIdAndRoomId(userId, roomId)
                .orElse(new ChatReadStatus(userId, roomId, LocalDateTime.now()));

        readStatus.setLastReadAt(LocalDateTime.now());
        chatReadStatusRepository.save(readStatus);
    }
}