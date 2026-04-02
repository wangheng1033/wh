package com.example.rbac.service;

import com.example.rbac.dto.UserDTO;
import com.example.rbac.entity.User;
import com.example.rbac.entity.UserRole;
import com.example.rbac.repository.RoleRepository;
import com.example.rbac.repository.UserRepository;
import com.example.rbac.repository.UserRoleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    public Page<User> getUserList(String username, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return userRepository.searchUsers(username, status, pageable);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public User createUser(UserDTO userDTO) {
        if (userRepository.existsByUsernameAndNotDeleted(userDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));
        } else {
            user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        }
        
        user = userRepository.save(user);
        
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), userDTO.getRoleIds());
        }
        
        return user;
    }
    
    @Transactional
    public User updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!user.getUsername().equals(userDTO.getUsername()) 
                && userRepository.existsByUsernameAndNotDeleted(userDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setStatus(userDTO.getStatus());
        
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));
        }
        
        user = userRepository.save(user);
        
        userRoleRepository.deleteByUserId(user.getId());
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), userDTO.getRoleIds());
        }
        
        return user;
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setDeleted(1);
        userRepository.save(user);
        userRoleRepository.deleteByUserId(id);
    }
    
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleRepository.deleteByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleRepository.save(userRole);
            }
        }
    }
}
