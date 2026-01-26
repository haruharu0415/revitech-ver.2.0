package com.example.revitech.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.dto.DmDisplayDto;
import com.example.revitech.entity.ChatGroup;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatGroupRepository;
import com.example.revitech.repository.ChatRoomRepository;
import com.example.revitech.repository.UsersRepository;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.GroupService;
import com.example.revitech.service.UsersService;

@Controller
public class GroupController {

    private final ChatRoomService chatRoomService;
    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final GroupService groupService;

    public GroupController(ChatRoomService chatRoomService, 
                           UsersService usersService,
                           UsersRepository usersRepository,
                           ChatRoomRepository chatRoomRepository,
                           ChatGroupRepository chatGroupRepository,
                           GroupService groupService) {
        this.chatRoomService = chatRoomService;
        this.usersService = usersService;
        this.usersRepository = usersRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatGroupRepository = chatGroupRepository;
        this.groupService = groupService;
    }

    // --- グループ一覧表示 ---
    @GetMapping("/group/list")
    public String listGroups(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users loginUser = null;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
             loginUser = usersService.findByEmail(auth.getName()).orElse(null);
        }

        List<ChatRoom> groups = new ArrayList<>();
        if (loginUser != null) {
            model.addAttribute("user", loginUser);
            Integer userId = loginUser.getUsersId();
            Integer role = loginUser.getRole();
            Integer sortOrder = loginUser.getChatSortOrder() != null ? loginUser.getChatSortOrder() : 1;
            boolean isNameSort = (sortOrder == 2);

            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchWord = keyword.trim();
                model.addAttribute("keyword", searchWord);
                if (role == 1) {
                    groups = isNameSort 
                        ? chatRoomRepository.searchJoinedGroupsByNameOrderByName(userId, searchWord)
                        : chatRoomRepository.searchJoinedGroupsByName(userId, searchWord);
                } else if (role == 2) {
                    groups = isNameSort
                        ? chatRoomRepository.searchJoinedGroupsByNameOrMemberOrderByName(userId, searchWord)
                        : chatRoomRepository.searchJoinedGroupsByNameOrMember(userId, searchWord);
                } else if (role == 3) {
                    groups = isNameSort
                        ? chatRoomRepository.searchAllGroupsByNameOrMemberOrderByName(searchWord)
                        : chatRoomRepository.searchAllGroupsByNameOrMember(searchWord);
                }
            } else {
                if (role == 3) {
                    groups = isNameSort
                        ? chatRoomRepository.findByTypeOrderByNameAsc(2)
                        : chatRoomRepository.findByTypeOrderByCreatedAtDesc(2);
                } else {
                    groups = isNameSort
                        ? chatRoomRepository.findJoinedRoomsByUserIdOrderByName(userId, 2)
                        : chatRoomRepository.findJoinedRoomsByUserId(userId, 2);
                }
            }
            List<DmDisplayDto> dmList = chatRoomService.getDmListForUser(userId, sortOrder);
            model.addAttribute("dmList", dmList);
        }
        model.addAttribute("groups", groups);
        return "group-list";
    }

    @PostMapping("/group/change-sort")
    public String changeSortOrder(@RequestParam("sortOrder") Integer sortOrder) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
             Users loginUser = usersService.findByEmail(auth.getName()).orElse(null);
             if (loginUser != null) {
                 loginUser.setChatSortOrder(sortOrder);
                 usersRepository.save(loginUser);
             }
        }
        return "redirect:/group/list";
    }

    // --- ★★★ グループ作成機能 ★★★ ---

    @GetMapping("/group/create")
    public String showCreateGroupForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        Users user = usersService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        
        return "group-create";
    }

    @PostMapping("/group/create")
    public String createGroup(@RequestParam("name") String groupName,
                              @RequestParam(name = "memberIds", required = false) String memberIdsStr,
                              RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        Users creator = usersService.findByEmail(auth.getName()).orElseThrow();

        // 1. ChatRoom作成
        ChatRoom room = new ChatRoom();
        room.setName(groupName);
        room.setType(2); // 2 = GROUP
        
        // ★★★ ここを追加: DBエラー回避のため、作成者のIDをセット ★★★
        room.setUsersId(creator.getUsersId()); 
        
        chatRoomRepository.save(room);

        // 2. ChatGroup作成
        ChatGroup group = new ChatGroup();
        group.setGroupId(room.getRoomId()); 
        group.setGroupName(groupName);
        group.setDescription(""); 
        chatGroupRepository.save(group);

        // 3. 作成者をメンバーに追加
        groupService.addMember(room.getRoomId(), creator.getUsersId());

        // 4. 招待メンバー追加
        if (memberIdsStr != null && !memberIdsStr.trim().isEmpty()) {
            String[] ids = memberIdsStr.split(",");
            for (String idStr : ids) {
                try {
                    Integer userId = Integer.parseInt(idStr.trim());
                    if (!userId.equals(creator.getUsersId())) {
                        groupService.addMember(room.getRoomId(), userId);
                    }
                } catch (NumberFormatException e) {
                    // 無効なIDは無視
                }
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "グループを作成しました。");
        return "redirect:/group/list";
    }

    // --- ★★★ 管理画面用 ★★★ ---

    @GetMapping("/group/manage/{groupId}")
    public String manageGroup(@PathVariable("groupId") Integer groupId, Model model) {
        Optional<ChatGroup> groupOpt = chatGroupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return "redirect:/group/list";
        }
        model.addAttribute("groupId", groupId);
        model.addAttribute("currentMembers", groupService.getGroupMembers(groupId));
        model.addAttribute("group", groupOpt.get());
        return "group-manage";
    }

    @PostMapping("/group/{groupId}/add-member")
    public String addMember(@PathVariable("groupId") Integer groupId,
                            @RequestParam("userId") Integer userId,
                            RedirectAttributes redirectAttributes) {
        groupService.addMember(groupId, userId);
        redirectAttributes.addFlashAttribute("successMessage", "メンバーを追加しました。");
        return "redirect:/group/manage/" + groupId;
    }

    @PostMapping("/group/{groupId}/remove-member")
    public String removeMember(@PathVariable("groupId") Integer groupId,
                               @RequestParam("userId") Integer userId,
                               RedirectAttributes redirectAttributes) {
        groupService.removeMember(groupId, userId);
        redirectAttributes.addFlashAttribute("successMessage", "メンバーを削除しました。");
        return "redirect:/group/manage/" + groupId;
    }
}