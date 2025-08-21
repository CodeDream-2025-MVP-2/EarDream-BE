package com.eardream.domain.books.mapper;

import com.eardream.domain.books.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BookMapper {

	int insertBook(Book book);

	Optional<Book> findById(@Param("id") Long id);

	List<Book> findByFamilyId(@Param("familyId") Long familyId);

	int renameBook(@Param("id") Long id, @Param("name") String name);

	int updateBookFiles(@Param("id") Long id, @Param("pdfUrl") String pdfUrl, @Param("imageUrl") String imageUrl);
}


