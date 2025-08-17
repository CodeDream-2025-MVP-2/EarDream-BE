package com.eardream.global.config;

import com.eardream.domain.user.entity.UserType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 설정 클래스
 * TypeHandler 및 Alias 자동 등록
 */
@Configuration
public class MybatisConfig {

    /**
     * Oracle DB의 CHAR(1) 'Y'/'N'을 Boolean으로 자동 변환하는 TypeHandler
     */
    @Bean
    public TypeHandler<Boolean> oracleBooleanTypeHandler() {
        return new org.apache.ibatis.type.TypeHandler<Boolean>() {
            @Override
            public void setParameter(java.sql.PreparedStatement ps, int i, Boolean parameter, 
                                   org.apache.ibatis.type.JdbcType jdbcType) throws java.sql.SQLException {
                if (parameter == null) {
                    ps.setString(i, "N");
                } else {
                    ps.setString(i, parameter ? "Y" : "N");
                }
            }

            @Override
            public Boolean getResult(java.sql.ResultSet rs, String columnName) throws java.sql.SQLException {
                String value = rs.getString(columnName);
                return "Y".equals(value);
            }

            @Override
            public Boolean getResult(java.sql.ResultSet rs, int columnIndex) throws java.sql.SQLException {
                String value = rs.getString(columnIndex);
                return "Y".equals(value);
            }

            @Override
            public Boolean getResult(java.sql.CallableStatement cs, int columnIndex) throws java.sql.SQLException {
                String value = cs.getString(columnIndex);
                return "Y".equals(value);
            }
        };
    }

    /**
     * UserType Enum을 String으로 자동 변환하는 TypeHandler
     */
    @Bean
    public TypeHandler<UserType> userTypeTypeHandler() {
        return new org.apache.ibatis.type.TypeHandler<UserType>() {
            @Override
            public void setParameter(java.sql.PreparedStatement ps, int i, UserType parameter, 
                                   org.apache.ibatis.type.JdbcType jdbcType) throws java.sql.SQLException {
                if (parameter == null) {
                    ps.setString(i, null);
                } else {
                    ps.setString(i, parameter.name());
                }
            }

            @Override
            public UserType getResult(java.sql.ResultSet rs, String columnName) throws java.sql.SQLException {
                String value = rs.getString(columnName);
                return value == null ? null : UserType.valueOf(value);
            }

            @Override
            public UserType getResult(java.sql.ResultSet rs, int columnIndex) throws java.sql.SQLException {
                String value = rs.getString(columnIndex);
                return value == null ? null : UserType.valueOf(value);
            }

            @Override
            public UserType getResult(java.sql.CallableStatement cs, int columnIndex) throws java.sql.SQLException {
                String value = cs.getString(columnIndex);
                return value == null ? null : UserType.valueOf(value);
            }
        };
    }
}