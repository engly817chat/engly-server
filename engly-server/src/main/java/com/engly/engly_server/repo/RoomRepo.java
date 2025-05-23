package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepo extends JpaRepository<Rooms, String> {
    List<Rooms> findAllByCategory_Name(CategoryType name);

    @Query(value = """
            SELECT r FROM Rooms r WHERE
            LOWER(r.category.name) LIKE LOWER('%' || :keyString || '%') OR
            LOWER(r.name) LIKE LOWER('%' || :keyString || '%') OR
            LOWER(r.description) LIKE LOWER('%' || :keyString || '%')
            """
    )
    List<Rooms> findAllRoomsContainingKeyString(String keyString);

    List<Rooms> findAllByNameContainingIgnoreCaseAndCategoryName(String keyString, CategoryType categoryName);
}
