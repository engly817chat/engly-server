package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepo extends JpaRepository<Rooms, String> {
    Page<Rooms> findAllByCategory_Name(CategoryType name, Pageable pageable);

    @Query(value = """
            SELECT r FROM Rooms r WHERE LOWER(r.category) LIKE LOWER('%' || :keyString || '%') OR
            LOWER(r.name) LIKE LOWER('%' || :keyString || '%') OR
            LOWER(r.description) LIKE LOWER('%' || :keyString || '%')
            """
    )
    Page<Rooms> findAllRoomsContainingKeyString(String keyString, Pageable pageable);
}
