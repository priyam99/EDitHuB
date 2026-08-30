package com.edithub.user.repository;

import com.edithub.user.model.User;
import com.edithub.user.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, UUID> {

    List<UserSkill> findByUser(User user);

    void deleteByUser(User user);
}
