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
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, 
                           ChatMemberRepository chatMemberRepository,
                           ChatReadStatusRepository chatReadStatusRepository,
                           ChatMessageRepository chatMessageRepository,
                           UsersRepository usersRepository,
                           UsersService usersService) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.chatReadStatusRepository = chatReadStatusRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    /**
     * グループチャットを作成する
     */
    public ChatRoom createGroupChat(String groupName, Integer creatorId, List<Integer> memberIds) {
        ChatRoom room = new ChatRoom();
        room.setName(groupName);
        room.setIsDm(0); // グループ
        room.setType(2); // GroupControllerに合わせてType=2を設定
        room.setUsersId(creatorId); // 作成者ID
        room.setCreatedAt(LocalDateTime.now());
        ChatRoom savedRoom = chatRoomRepository.save(room);

        // 作成者をメンバーに追加
        chatMemberRepository.save(new ChatMember(new ChatMemberId(savedRoom.getRoomId(), creatorId)));

        // 招待メンバーを追加
        for (Integer mId : memberIds) {
            if (!mId.equals(creatorId)) {
                chatMemberRepository.save(new ChatMember(new ChatMemberId(savedRoom.getRoomId(), mId)));
            }
        }
        return savedRoom;
    }

    /**
     * DMルームを取得する。存在しなければ新規作成する。
     */
    public ChatRoom getOrCreateDmRoom(Integer myUserId, Integer partnerId) {
        List<ChatRoom> myDms = chatRoomRepository.findDmRoomsByUserId(myUserId);
        
        for (ChatRoom room : myDms) {
            boolean isPartnerIn = chatMemberRepository.existsById_UserIdAndId_RoomId(partnerId, room.getRoomId());
            if (isPartnerIn) {
                return room; 
            }
        }
        
        ChatRoom newRoom = new ChatRoom();
        newRoom.setName("DM");
        newRoom.setIsDm(1); 
        newRoom.setType(1); // DMはType=1
        newRoom.setUsersId(myUserId); // 作成者
        newRoom.setCreatedAt(LocalDateTime.now());
        ChatRoom savedRoom = chatRoomRepository.save(newRoom);
        
        chatMemberRepository.save(new ChatMember(new ChatMemberId(savedRoom.getRoomId(), myUserId)));
        chatMemberRepository.save(new ChatMember(new ChatMemberId(savedRoom.getRoomId(), partnerId)));
        
        return savedRoom;
    }

    /**
     * ユーザーが参加しているグループチャット一覧を取得（未読バッジ付き）
     */
    public List<ChatRoomWithNotificationDto> getGroupListForUser(Integer userId) {
        // GroupControllerで使用されている findJoinedRoomsByUserId を使用
        List<ChatRoom> rooms = chatRoomRepository.findJoinedRoomsByUserId(userId, 2);
        
        List<ChatRoomWithNotificationDto> resultList = new ArrayList<>();

        for (ChatRoom room : rooms) {
            boolean hasUnread = hasUnreadMessages(room.getRoomId(), userId);
            resultList.add(new ChatRoomWithNotificationDto(room.getRoomId(), room.getName(), hasUnread));
        }
        return resultList;
    }

    /**
     * 未読があるグループのみ取得（ホーム画面通知用）
     */
    public List<ChatRoom> findUnreadGroupRooms(Integer userId) {
        List<ChatRoom> myGroups = chatRoomRepository.findJoinedRoomsByUserId(userId, 2);
        return myGroups.stream()
            .filter(room -> hasUnreadMessages(room.getRoomId(), userId))
            .collect(Collectors.toList());
    }

    /**
     * 未読があるDMのみ取得（ホーム画面通知用）
     */
    public List<DmDisplayDto> findUnreadDmRooms(Integer userId) {
        List<ChatRoom> myDms = chatRoomRepository.findDmRoomsByUserId(userId);
        List<DmDisplayDto> resultList = new ArrayList<>();

        for (ChatRoom room : myDms) {
            if (hasUnreadMessages(room.getRoomId(), userId)) {
                List<ChatMember> members = chatMemberRepository.findById_RoomId(room.getRoomId());
                Integer partnerId = null;
                String partnerName = "不明なユーザー";
                String iconUrl = null;

                for (ChatMember member : members) {
                    if (!member.getId().getUserId().equals(userId)) {
                        partnerId = member.getId().getUserId();
                        Optional<Users> partnerOpt = usersRepository.findById(partnerId);
                        if (partnerOpt.isPresent()) {
                            partnerName = partnerOpt.get().getName();
                            iconUrl = usersService.getUserIconPath(partnerId);
                        }
                        break;
                    }
                }
                // アイコンURLを含むコンストラクタを使用
                resultList.add(new DmDisplayDto(room.getRoomId(), partnerId, partnerName, iconUrl));
            }
        }
        return resultList;
    }

    /**
     * DM一覧取得
     */
    public List<DmDisplayDto> getDmListForUser(Integer myUserId, Integer sortOrder) {
        List<ChatRoom> myDmRooms = chatRoomRepository.findDmRoomsByUserId(myUserId);
        List<DmDisplayDto> resultList = new ArrayList<>();

        for (ChatRoom room : myDmRooms) {
            List<ChatMember> members = chatMemberRepository.findById_RoomId(room.getRoomId());
            
            Integer partnerId = null;
            String partnerName = "不明なユーザー";
            String iconUrl = null;

            for (ChatMember member : members) {
                Integer memberId = member.getId().getUserId();
                if (!memberId.equals(myUserId)) {
                    partnerId = memberId;
                    Optional<Users> partnerOpt = usersRepository.findById(partnerId);
                    if (partnerOpt.isPresent()) {
                        partnerName = partnerOpt.get().getName();
                        iconUrl = usersService.getUserIconPath(partnerId);
                    }
                    break; 
                }
            }

            if (partnerId != null) {
                resultList.add(new DmDisplayDto(room.getRoomId(), partnerId, partnerName, iconUrl));
            }
        }

        if (sortOrder != null && sortOrder == 2) {
            resultList.sort(Comparator.comparing(DmDisplayDto::getPartnerName, String.CASE_INSENSITIVE_ORDER));
        }

        return resultList;
    }

    /**
     * ルーム内の未読メッセージがあるか判定
     */
    private boolean hasUnreadMessages(Integer roomId, Integer userId) {
        LocalDateTime lastReadAt = chatReadStatusRepository.findByUserIdAndRoomId(userId, roomId)
                .map(ChatReadStatus::getLastReadAt)
                .orElse(LocalDateTime.MIN); 

        Optional<ChatMessage> latestMsg = chatMessageRepository.findTopByRoomIdOrderByCreatedAtDesc(roomId);
        
        if (latestMsg.isPresent()) {
            // ChatMessageエンティティの getUserId() を使用
            if (latestMsg.get().getUserId().equals(userId)) {
                return false;
            }
            return latestMsg.get().getCreatedAt().isAfter(lastReadAt);
        }
        return false;
    }

    // コントローラーから呼ばれるグループ作成メソッド
    public ChatRoom createGroup(String groupName, Integer creatorId, List<Integer> memberIds) {
        return createGroupChat(groupName, creatorId, memberIds);
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

    public String getDmPartnerName(Integer roomId, Integer myUserId) {
        List<ChatMember> members = chatMemberRepository.findById_RoomId(roomId);
        for (ChatMember member : members) {
            if (!member.getId().getUserId().equals(myUserId)) {
                return usersRepository.findById(member.getId().getUserId())
                        .map(Users::getName)
                        .orElse("不明なユーザー");
            }
        }
        return "チャット相手";
    }
    
    public List<UserSearchDto> searchUsersForDm(String keyword, Integer myUserId) {
        List<Users> users = usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        
        return users.stream()
            .filter(u -> !u.getUsersId().equals(myUserId))
            .filter(u -> !"deleted".equals(u.getStatus())) 
            .map(u -> {
                String iconUrl = usersService.getUserIconPath(u.getUsersId());
                return new UserSearchDto(
                    u.getUsersId(),
                    u.getName(),
                    u.getEmail(),
                    iconUrl,
                    u.getRole()
                );
            })
            .collect(Collectors.toList());
    }
}